package ws.alek.torrator.torrent.bencoded;

import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.torrent.TorrentFile;
import ws.alek.torrator.torrent.Tracker;

public class MetainfoReader {
	private static final Logger LOG = LoggerFactory
			.getLogger(MetainfoReader.class);

	/**
	 * Extract 'piece-length' from metainfo data.
	 * 
	 * @param metainfo
	 *            parsed torrent file represented in Map interface
	 * @return 'piece length' value
	 */
	static public int readPieceLength(Map<BinaryString, Object> metainfo) {
		Object pieceLengthObj = metainfo.get(new BinaryString("piece length"
				.getBytes()));
		if (pieceLengthObj == null || !(pieceLengthObj instanceof BigInteger)) {
			throw new IllegalArgumentException(
					"Can't extract 'piece length' field");
		}
		BigInteger pieceLength = (BigInteger) pieceLengthObj;
		return pieceLength.intValue();

	}

	/**
	 * Extract 'info->pieces' field from metainfo
	 * 
	 * @param metainfo
	 * @return 'pieces' array Ñ byte[20] SHA1 hashes of each piece.
	 */
	static public byte[][] readPieces(Map<BinaryString, Object> metainfo) {
		Map<BinaryString, Object> info = readMap(metainfo, "info");

		Object piecesObj = info.get(new BinaryString("pieces".getBytes()));
		if (piecesObj == null || !(piecesObj instanceof BinaryString)) {
			throw new IllegalArgumentException("Can't extract 'pieces' field");
		}

		BinaryString piecesRaw = (BinaryString) piecesObj;
		if (piecesRaw.getBytes().length < 20
				|| piecesRaw.getBytes().length % 20 != 0) {
			throw new IllegalArgumentException("'pieces' field is corrupted");
		}

		byte[] piecesData = piecesRaw.getBytes();
		int piecesNum = piecesData.length % 20;
		byte[][] pieces = new byte[piecesNum][];
		for (int i = 0; i < piecesNum; i++) {
			pieces[i] = Arrays.copyOfRange(piecesData, i * 20, i * 20 + 20);
		}

		return pieces;
	}

	/**
	 * Extract 'info->private' field from metainfo
	 * 
	 * @param metainfo
	 * @return True if torrent is private
	 */
	static public boolean readPrivate(Map<BinaryString, Object> metainfo) {
		Map<BinaryString, Object> info = readMap(metainfo, "info");

		try {
			BigInteger isPrivate = readBigInteger(info, "private");
			return isPrivate.equals(BigInteger.valueOf(1));
		} catch (IllegalArgumentException e) {
		}
		return false;
	}

	/**
	 * Read 'announce-list' and 'announce' fields and return list of trackers.
	 * 
	 * @param metainfo
	 *            Map representation of torrent file.
	 * @return list of trackers for this torrent.
	 */
	static public List<Tracker> readTrackers(Map<BinaryString, Object> metainfo) {
		List<Tracker> trackers = new ArrayList<Tracker>();

		try {
			List<Object> announceList = readList(metainfo, "announce-list");

			for (Object trackerUrlsObj : announceList) {
				if (!(trackerUrlsObj instanceof List<?>)) {
					throw new IllegalArgumentException(
							"Incorrect format of 'announce-list' field.");
				}
				@SuppressWarnings("unchecked")
				List<Object> trackerUrls = (List<Object>) trackerUrlsObj;

				for (Object urlObj : trackerUrls) {
					if (!(urlObj instanceof BinaryString)) {
						throw new IllegalArgumentException(
								"Incorrect format of 'announce-list' field.");
					}
					BinaryString url = (BinaryString) urlObj;
					if (url.toString().startsWith("http://")
							|| url.toString().startsWith("http://"))
						try {
							trackers.add(new Tracker(new URL(url.toString())));
						} catch (MalformedURLException e) {
							LOG.warn("Skip tracker URL: " + e.getMessage());
						}
					else
						LOG.warn("Tracker URL schema is not http or https. Skip this tracker's URL: "
								+ url);
				}
			}
		} catch (IllegalArgumentException e) {
			// If torrent has no 'announce-list' field, the search for tracker's
			// url in 'announce' field
			String url = readBinaryString(metainfo, "announce").toString();
			if (url.startsWith("http://") || url.startsWith("http://"))
				try {
					trackers.add(new Tracker(new URL(url)));
				} catch (MalformedURLException e1) {
					LOG.warn(e1.getMessage());
				}
			else
				LOG.warn("Tracker URL schema is not http or https: " + url);
		}

		return trackers;
	}

	/**
	 * Read list of files included in torrent.
	 * 
	 * @param metainfo
	 *            Map representation of torrent file.
	 * @return list of files.
	 */
	public static List<TorrentFile> readFiles(Map<BinaryString, Object> metainfo) {
		List<TorrentFile> files = new ArrayList<TorrentFile>();
		Map<BinaryString, Object> info = readMap(metainfo, "info");
		String name = readBinaryString(info, "name").toString();
		if (containsKey(info, "length")) {
			// Single file torrent
			int length = readBigInteger(info, "length").intValue();
			TorrentFile torrentFile = new TorrentFile(name, length);
			files.add(torrentFile);
		} else if (containsKey(info, "files")) {
			// Multi file torrent
			List<Object> filesObj = readList(info, "files");
			if (!(filesObj instanceof List<?>)) {
				throw new IllegalArgumentException(
						"'info->files' field is not a list.");
			}
			for (Object fileObj : filesObj) {
				if (!(fileObj instanceof Map<?, ?>)) {
					throw new IllegalArgumentException(
							"'info->files->file' element is not a Map.");
				}
				@SuppressWarnings("unchecked")
				Map<BinaryString, Object> fileMap = (Map<BinaryString, Object>) fileObj;
				String path = readPath(readList(fileMap, "path"));
				int length = readBigInteger(fileMap, "length").intValue();
				files.add(new TorrentFile(name + File.separator + path, length));
			}
		} else {
			throw new IllegalArgumentException(
					"Can't read files from torrent metainfo.");
		}
		return files;
	}

	/**
	 * Read 'info->name' field and return File object instance named by this
	 * field located in <em>downloads<em> directory.
	 * 
	 * @param metainfo
	 *            Map representation of torrent file.
	 * @return Base directory to save files to or just file's name if this is a
	 *         single-file torrent.
	 */
	public static File readName(Map<BinaryString, Object> metainfo) {
		Map<BinaryString, Object> info = readMap(metainfo, "info");
		String name = readBinaryString(info, "name").toString();
		if (name.equals("..")) {
			throw new SecurityException(
					"Path name in torrent files should no contain '..' part.");
		}
		return new File(Configuration.getDownloadsDir(), name);
	}

	/**
	 * Return true if torrent is single-file.
	 * 
	 * @param metainfo
	 *            Map representation of torrent file.
	 * @return true if torrent is single-file.
	 */
	public static boolean readSingleFile(Map<BinaryString, Object> metainfo) {
		Map<BinaryString, Object> info = readMap(metainfo, "info");
		if (containsKey(info, "length")) {
			return true;
		} else if (containsKey(info, "files")) {
			return false;

		}
		throw new IllegalArgumentException(
				"Can't read files from torrent metainfo.");
	}

	/**
	 * Get Map object from metainfo data. Also performs check for type
	 * correctness.
	 * 
	 * @param metainfo
	 *            Metainfo data
	 * @param key
	 *            Key to retrieve from metainfo dictionary
	 * @return Map object from metainfo at key.
	 */
	private static Map<BinaryString, Object> readMap(
			Map<BinaryString, Object> metainfo, String key) {
		Object mapObj = metainfo.get(new BinaryString(key.getBytes()));
		if (mapObj == null || !(mapObj instanceof Map<?, ?>)) {
			throw new IllegalArgumentException(
					"Can't read Map object from metainfo.");
		}
		@SuppressWarnings("unchecked")
		Map<BinaryString, Object> map = (Map<BinaryString, Object>) mapObj;
		return map;
	}

	/**
	 * Get List object from metainfo data. Also performs check for type
	 * correctness.
	 * 
	 * @param metainfo
	 *            Metainfo data
	 * @param key
	 *            Key to retrieve from metainfo dictionary
	 * @return List object from metainfo at key.
	 */
	private static List<Object> readList(Map<BinaryString, Object> metainfo,
			String key) {
		Object listObj = metainfo.get(new BinaryString(key.getBytes()));
		if (listObj == null || !(listObj instanceof List<?>)) {
			throw new IllegalArgumentException(
					"Can't read List object from metainfo.");
		}
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) listObj;
		return list;
	}

	/**
	 * Get BigInteger object from metainfo data. Also performs check for type
	 * correctness.
	 * 
	 * @param metainfo
	 *            Metainfo data
	 * @param key
	 *            Key to retrieve from metainfo dictionary
	 * @return BigInteger object from metainfo at key.
	 */
	private static BigInteger readBigInteger(
			Map<BinaryString, Object> metainfo, String key) {
		Object intObj = metainfo.get(new BinaryString(key.getBytes()));
		if (intObj == null || !(intObj instanceof BigInteger)) {
			throw new IllegalArgumentException(
					"Can't read BigInteger object from metainfo.");
		}
		return (BigInteger) intObj;
	}

	/**
	 * Get BinaryString object from metainfo data. Also performs check for type
	 * correctness.
	 * 
	 * @param metainfo
	 *            Metainfo data
	 * @param key
	 *            Key to retrieve from metainfo dictionary
	 * @return BinaryString object from metainfo at key.
	 */
	private static BinaryString readBinaryString(
			Map<BinaryString, Object> metainfo, String key) {
		Object stringObj = metainfo.get(new BinaryString(key.getBytes()));
		if (stringObj == null || !(stringObj instanceof BinaryString)) {
			throw new IllegalArgumentException(
					"Can't read BigInteger object from metainfo.");
		}
		return (BinaryString) stringObj;

	}

	private static boolean containsKey(Map<BinaryString, Object> metainfo,
			String key) {
		return metainfo.containsKey(new BinaryString(key.getBytes()));
	}

	/**
	 * Build path for file from path elements in 'info->files->path' field
	 * 
	 * @param pathElements
	 *            path elements
	 * @return file path
	 */
	private static String readPath(List<Object> pathElements) {
		File file = null;
		for (Object pathElementObj : pathElements) {
			if (!(pathElementObj instanceof BinaryString)) {
				throw new IllegalArgumentException(
						"Path element should be a BinaryString object.");
			}
			String pathElement = pathElementObj.toString();
			if (".".equals(pathElement) || "..".equals(pathElement)) {
				throw new IllegalArgumentException(
						"Path contains invalid elements '.' or '..'.");
			}

			if (file == null) {
				file = new File(pathElement);
			} else {
				file = new File(file, pathElement);
			}
		}
		return file.getPath();
	}

}

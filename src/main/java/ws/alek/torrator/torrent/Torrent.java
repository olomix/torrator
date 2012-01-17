package ws.alek.torrator.torrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.torrent.bencoded.BEDecoder;
import ws.alek.torrator.torrent.bencoded.BinaryString;

public class Torrent {
	private File torrentFile;
	private byte[] info_hash = null;
	private Peer[] peers;
	private List<Tracker> trackers;
	private static final Logger LOG = LoggerFactory.getLogger(Torrent.class);
	private boolean isPrivate = false;
	private int pieceLength;
	private byte[][] pieces;
	private List<TorrentFile> files;

	public Torrent() {
	}

	public void setTorrentFile(File file) throws IOException {
		torrentFile = file;
		BEDecoder torrentFileDecoder = new BEDecoder(new FileInputStream(
				torrentFile), true);

		@SuppressWarnings("unchecked")
		Map<BinaryString, Object> torrentMap = (Map<BinaryString, Object>) torrentFileDecoder
				.decode();

		info_hash = torrentFileDecoder.getInfoHash().getBytes();
		if (info_hash.length != 20) {
			throw new IllegalStateException("info_hash length not equal to 20.");
		}

		setTrackers(torrentMap);

		@SuppressWarnings("unchecked")
		Map<BinaryString, Object> infoMap = (Map<BinaryString, Object>) torrentMap
				.get(new BinaryString("info".getBytes()));
		if (infoMap != null) {
			setPrivate(infoMap);
			setPieces(infoMap);
		} else {
			throw new IllegalArgumentException("Incorrect torrent file.");
		}

		setFilesData(torrentMap);
	}

	private void setFilesData(Map<BinaryString, Object> torrentMap) {
		// TODO
	}

	/**
	 * Populate 'piece length' and 'pieces' fields from the 'info' part of
	 * torrent file.
	 * 
	 * @param torrentMap
	 *            'info' part of torrent file
	 */
	private void setPieces(Map<BinaryString, Object> torrentMap) {
		BigInteger pieceLength = (BigInteger) torrentMap.get(new BinaryString(
				"piece length".getBytes()));
		if (pieceLength == null) {
			throw new IllegalArgumentException(
					"Incorrect torrent file: 'piece-length' field is absent.");
		}
		this.pieceLength = pieceLength.intValue();

		BinaryString pieces = (BinaryString) torrentMap.get(new BinaryString(
				"pieces".getBytes()));
		if (pieces == null || pieces.getBytes().length < 20
				|| pieces.getBytes().length % 20 != 0) {
			throw new IllegalArgumentException(
					"Incorrect torrent file: 'pieces' field is corrupted.");
		}
		byte[] piecesData = pieces.getBytes();
		int piecesNum = piecesData.length % 20;
		this.pieces = new byte[piecesNum][];
		for (int i = 0; i < piecesNum; i++) {
			this.pieces[i] = Arrays
					.copyOfRange(piecesData, i * 20, i * 20 + 20);
		}
	}

	private void setPrivate(Map<BinaryString, Object> torrentMap) {
		BigInteger isPrivate = (BigInteger) torrentMap.get(new BinaryString(
				"private".getBytes()));
		this.isPrivate = isPrivate != null
				&& isPrivate.equals(BigInteger.valueOf(1));
	}

	/**
	 * Populate trackers list from metainfo data.
	 * 
	 * @param torrentMap
	 *            metainfo map data
	 */
	private void setTrackers(Map<BinaryString, Object> torrentMap) {
		trackers = new ArrayList<Tracker>();
		BinaryString announceKey = new BinaryString("announce".getBytes());
		BinaryString announceListKey = new BinaryString(
				"announce-list".getBytes());

		@SuppressWarnings("unchecked")
		List<List<BinaryString>> announceList = (List<List<BinaryString>>) torrentMap
				.get(announceListKey);
		if (announceList != null) {
			for (List<BinaryString> trackerUrls : announceList) {
				for (BinaryString url : trackerUrls) {
					if (url.toString().startsWith("http://")
							|| url.toString().startsWith("http://"))
						try {
							trackers.add(new Tracker(new URL(url.toString())));
						} catch (MalformedURLException e) {
							LOG.warn(e.getMessage());
						}
					else
						LOG.warn("Tracker URL schema is not http or https: "
								+ url);
				}
			}
		} else if (torrentMap.containsKey(announceKey)) {
			String url = torrentMap.get(announceKey).toString();
			if (url.startsWith("http://") || url.startsWith("http://"))
				try {
					trackers.add(new Tracker(new URL(url)));
				} catch (MalformedURLException e) {
					LOG.warn(e.getMessage());
				}
			else
				LOG.warn("Tracker URL schema is not http or https: " + url);
		}
	}

	/**
	 * Open new torrent from file
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Torrent open(File torrentFile) throws IOException {
		Torrent torrent = new Torrent();
		// Save file to permanent storate
		torrent.saveTorrentFile(torrentFile);

		//

		return torrent;
	}

	/**
	 * Load existing torrent (on application start)
	 * 
	 * @return
	 */
	public static Torrent load(File torrentFile) {

		return null;
	}

	/**
	 * Save the given file to permanent storage.
	 * 
	 * @param newTorrentFile
	 *            torrent file to save
	 * @throws IOException
	 */
	private void saveTorrentFile(File newTorrentFile) throws IOException {
		torrentFile = new File(Configuration.getTorrentsDir(),
				newTorrentFile.getName());
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(newTorrentFile).getChannel();
			outputChannel = new FileOutputStream(torrentFile).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
	}

}

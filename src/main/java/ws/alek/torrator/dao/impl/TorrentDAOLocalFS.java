package ws.alek.torrator.dao.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.dao.TorrentDAO;
import ws.alek.torrator.torrent.Torrent;

public class TorrentDAOLocalFS implements TorrentDAO {
	private static final Logger LOG = LoggerFactory
			.getLogger(TorrentDAOLocalFS.class);

	@Override
	public void save(Torrent torrent) throws IOException {
		File sessionFile = new File(Configuration.getTorrentsDir(), torrent
				.getInfoHash().toString() + ".s");
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(sessionFile));
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Scan torrents dir for saved sessions and return deserialized list of
	 * torrent files.
	 * 
	 * @return array of torrent files.
	 */
	@Override
	public Torrent[] getAll() {
		List<Torrent> torrents = new ArrayList<Torrent>();
		File torrentDir = Configuration.getTorrentsDir();
		for (File f : torrentDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".s");
			}
		})) {
			Torrent t = load(f);
			if (t == null) {
				continue;
			}
			torrents.add(t);
		}
		return torrents.toArray(new Torrent[0]);
	}

	/**
	 * Read serialized Torrent from file into new Object.
	 * 
	 * @param sessionFile
	 *            file with serialized Torrent object.
	 * @return {@link Torrent} object deserialized from <em>sessionFile</em>
	 */
	private Torrent load(File sessionFile) {
		ObjectInputStream in;
		Object obj = null;
		try {
			in = new ObjectInputStream(new FileInputStream(sessionFile));
			obj = in.readObject();
		} catch (ClassNotFoundException e) {
			LOG.error("Can't read saved session: " + e.getMessage());
			return null;
		} catch (IOException e) {
			try {
				LOG.error("Can't read from file "
						+ sessionFile.getCanonicalPath() + ": "
						+ e.getMessage());
			} catch (IOException e1) {
				LOG.error("Can't read from file: " + e.getMessage());
			}
			return null;
		}
		if (obj instanceof Torrent) {
			return (Torrent) obj;
		}
		LOG.error("Object is not of type Torrent.");
		return null;
	}

	/**
	 * Save the given torrent file to permanent storage. The name of the file
	 * would be <em>&lt;info_hash&gt;.torrent</em>
	 * 
	 * @param torrentFile
	 *            torrent file to save
	 * @param torrent
	 *            Torrent instance to get info_hash from
	 * @throws IOException
	 */
	@Override
	public void persistTorrentFile(File torrentFile, Torrent torrent)
			throws IOException {
		String fileName = torrent.getInfoHash().toString() + ".torrent";
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(torrentFile).getChannel();
			outputChannel = new FileOutputStream(new File(
					Configuration.getTorrentsDir(), fileName)).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
	}

	/**
	 * Save torrent stream to permanent storage.
	 * 
	 * @param torrentIn
	 *            stream of torrent to save.
	 * @param torrent
	 *            parsed torrent
	 * @throws IOException
	 */
	@Override
	public void persistTorrentFile(InputStream torrentIn, Torrent torrent)
			throws IOException {
		String fileName = torrent.getInfoHash().toString() + ".torrent";
		OutputStream outputStream = null;
		try {
			outputStream = new BufferedOutputStream(new FileOutputStream(
					fileName));
			byte[] buf = new byte[8192];
			int len;
			while ((len = torrentIn.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

}

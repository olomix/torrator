package ws.alek.torrator.dao.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

	@Override
	public Torrent create(File torrentFile) throws IOException {
		File savedTorrentFile = saveTorrentFile(torrentFile);
		Torrent t = new Torrent(new FileInputStream(savedTorrentFile));
		// File savedTorrentFile = saveTorrentFile(torrentFile);
		// return Torrent.load(savedTorrentFile);
		return t;
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
		return (Torrent[]) torrents.toArray();
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
	 * Save the given file to permanent storage.
	 * 
	 * @param originalTorrentFile
	 *            torrent file to save
	 * @throws IOException
	 */
	private File saveTorrentFile(File originalTorrentFile) throws IOException {
		File savedTorrentFile = new File(Configuration.getTorrentsDir(),
				originalTorrentFile.getName());
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(originalTorrentFile)
					.getChannel();
			outputChannel = new FileOutputStream(savedTorrentFile).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
		return savedTorrentFile;
	}

}

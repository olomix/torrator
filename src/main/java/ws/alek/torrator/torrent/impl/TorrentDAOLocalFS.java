package ws.alek.torrator.torrent.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.torrent.Torrent;
import ws.alek.torrator.torrent.TorrentDAO;

public class TorrentDAOLocalFS implements TorrentDAO {

	@Override
	public void save(Torrent torrent) {
		// TODO serialize torrent to permanent storage
		throw new NotImplementedException();
	}

	@Override
	public Torrent create(File torrentFile) throws IOException {
		File savedTorrentFile = saveTorrentFile(torrentFile);
		Torrent t = new Torrent(new FileInputStream(savedTorrentFile));
		// File savedTorrentFile = saveTorrentFile(torrentFile);
		// return Torrent.load(savedTorrentFile);
		return t;
	}

	@Override
	public Torrent[] getAll() {
		// TODO Return all saved torrents.
		throw new NotImplementedException();
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

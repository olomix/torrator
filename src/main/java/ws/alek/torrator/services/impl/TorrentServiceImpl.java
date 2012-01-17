package ws.alek.torrator.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.services.TorrentService;
import ws.alek.torrator.torrent.Torrent;
import ws.alek.torrator.torrent.TorrentDAO;

public class TorrentServiceImpl implements TorrentService {

	private TorrentDAO torrentDAO;

	public TorrentDAO getTorrentDAO() {
		return torrentDAO;
	}

	public void setTorrentDAO(TorrentDAO torrentDAO) {
		this.torrentDAO = torrentDAO;
	}
	
	@Override
	public Torrent add(File torrentFile) {
		// Save torrent file locally
		try {
			File newTorrentFile = saveTorrentFile(torrentFile);
		} catch (Exception e) {
			return null;
		}
		
		
		return new Torrent();
	}

	private File saveTorrentFile(File torrentFile) throws IOException {
		File file = new File(Configuration.getTorrentsDir(),
				torrentFile.getName());
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(torrentFile).getChannel();
			outputChannel = new FileOutputStream(file).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
		return file;
	}


}

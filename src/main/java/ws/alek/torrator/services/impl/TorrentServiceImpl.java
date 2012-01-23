package ws.alek.torrator.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.dao.TorrentDAO;
import ws.alek.torrator.services.TorrentManager;
import ws.alek.torrator.services.TorrentService;
import ws.alek.torrator.torrent.Torrent;

public class TorrentServiceImpl implements TorrentService {

	private TorrentDAO torrentDAO;
	private TorrentManager torrentManager;

	private static final Logger LOG = LoggerFactory
			.getLogger(TorrentServiceImpl.class);

	public TorrentDAO getTorrentDAO() {
		return torrentDAO;
	}

	public void setTorrentDAO(TorrentDAO torrentDAO) {
		this.torrentDAO = torrentDAO;
	}

	public TorrentManager getTorrentManager() {
		return torrentManager;
	}

	public void setTorrentManager(TorrentManager torrentManager) {
		this.torrentManager = torrentManager;
	}

	@Override
	public Torrent add(File torrentFile) {
		InputStream in = null;
		Torrent torrent;
		try {
			in = new FileInputStream(torrentFile);
			torrent = new Torrent(in);
		} catch (Exception e) {
			LOG.error("Can't create Torrent: " + e.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.error("Can't close input stream: " + e.getMessage());
				}
			}
		}

		if (getTorrentManager().contains(torrent)) {
			throw new IllegalArgumentException("We already have such Torrent.");
		}
		
		// Save torrent file for backup needs
		try {
			getTorrentDAO().persistTorrentFile(torrentFile, torrent);
		} catch (IOException e) {
			LOG.error("Can't save torrent file for backup use: " + e.getMessage(), e);
		}
		
		getTorrentManager().add(torrent);
		
		return torrent;
	}
}

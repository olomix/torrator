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
import ws.alek.torrator.torrent.TorrentExistsException;

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
	public Torrent add(InputStream torrentIn) throws TorrentExistsException {
		Torrent torrent;
		try {
			torrent = new Torrent(torrentIn);
		} catch (Exception e) {
			LOG.error("Can't create Torrent: " + e.getMessage());
			return null;
		}

		if (getTorrentManager().contains(torrent)) {
			throw new TorrentExistsException();
		}
		
		// Save torrent file for backup needs
		try {
			getTorrentDAO().persistTorrentFile(torrentIn, torrent);
		} catch (IOException e) {
			LOG.error("Can't save torrent file for backup use: " + e.getMessage(), e);
		}
		
		getTorrentManager().add(torrent);
		
		return torrent;
	}
}

package ws.alek.torrator.services.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.dao.TorrentDAO;
import ws.alek.torrator.services.TorrentManager;
import ws.alek.torrator.torrent.InfoHash;
import ws.alek.torrator.torrent.Torrent;

public class TorrentManagerImpl implements TorrentManager {
	Map<InfoHash, Torrent> torrents = null;
	TorrentDAO torrentDAO = null;
	private static final Logger LOG = LoggerFactory
			.getLogger(TorrentManagerImpl.class);

	public TorrentManagerImpl() {
	}

	public void setTorrentDAO(TorrentDAO torrentDAO) {
		this.torrentDAO = torrentDAO;
	}

	public TorrentDAO getTorrentDAO() {
		if (torrentDAO == null) {
			throw new IllegalStateException("TorrentDAO can't be null.");
		}
		return torrentDAO;
	}
	
	public void init() {
		// If torrents is not null, then we already initialized somehow.
		if (torrents != null) {
			throw new IllegalStateException(
					"TorrentManager already initialized.");
		}

		torrents = new ConcurrentHashMap<InfoHash, Torrent>();
		for (Torrent t : torrentDAO.getAll()) {
			if (torrents.containsKey(t.getInfoHash())) {
				torrents.put(t.getInfoHash(), t);
			}
		}
	}

	public void terminate() {
		for (Torrent t : torrents.values()) {
			try {
				torrentDAO.save(t);
			} catch (IOException e) {
				LOG.error("Can't save session file for torrent: "
						+ t.getName().getName() + " (hash: "
						+ t.getInfoHash().toString() + ")");
			}
		}
		torrents = null;
	}
	
	public boolean contains(Torrent torrent) {
		return torrents.containsKey(torrent.getInfoHash());
	}

	@Override
	public void add(Torrent torrent) {
		torrents.put(torrent.getInfoHash(), torrent);
		// TODO may be start this torrent somehow.
	}

}

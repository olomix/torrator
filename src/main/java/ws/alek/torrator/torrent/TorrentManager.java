package ws.alek.torrator.torrent;

import java.util.HashSet;
import java.util.Set;

public class TorrentManager {
	Set<Torrent> torrents = null;
	TorrentDAO torrentDAO = null;
	
	public TorrentManager() {}
	
	public void setTorrentDAO(TorrentDAO torrentDAO) {
		this.torrentDAO = torrentDAO;
	}
	
	public TorrentDAO getTorrentDAO() {
		if(torrentDAO == null) {
			throw new IllegalStateException("TorrentDAO can't be null.");
		}
		return torrentDAO;
	}
	
	public void init() {
		// If torrents is not null, then we already initialized somehow.
		if(torrents != null) {
			throw new IllegalStateException("TorrentManager already initialized.");
		}
		
		torrents = new HashSet<Torrent>();
		for(Torrent t : torrentDAO.getAll()) {
			torrents.add(t);
		}
	}
	
	public void terminate() {
		for(Torrent t : torrents) {
			torrentDAO.save(t);
		}
		
		torrents = null;
	}

}

package ws.alek.torrator.dao;

import java.io.File;
import java.io.IOException;

import ws.alek.torrator.torrent.Torrent;

public interface TorrentDAO {
	/**
	 * Serialize Torrent instance into permanent storage
	 * 
	 * @param torrent
	 * @throws IOException
	 */
	public void save(Torrent torrent) throws IOException;
	
	public void persistTorrentFile(File torrentFile, Torrent torrent) throws IOException;

	public Torrent[] getAll();
}

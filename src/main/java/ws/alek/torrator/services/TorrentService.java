package ws.alek.torrator.services;

import java.io.InputStream;

import ws.alek.torrator.torrent.Torrent;
import ws.alek.torrator.torrent.TorrentExistsException;

public interface TorrentService {
	/**
	 * Adds new torrent to download.
	 * 
	 * @param torrentFile
	 * @return
	 * @throws TorrentExistsException
	 *             throws if we already have torrent with such info_hash in our
	 *             database.
	 */
	public Torrent add(InputStream torrentIn) throws TorrentExistsException;
}

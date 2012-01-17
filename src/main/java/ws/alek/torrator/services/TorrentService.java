package ws.alek.torrator.services;

import java.io.File;

import ws.alek.torrator.torrent.Torrent;

public interface TorrentService {
	/**
	 * Adds new torrent to download.
	 * @param torrentFile
	 * @return
	 */
	public Torrent add(File torrentFile);
}

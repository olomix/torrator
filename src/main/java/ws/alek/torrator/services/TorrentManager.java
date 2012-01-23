package ws.alek.torrator.services;

import ws.alek.torrator.torrent.Torrent;

public interface TorrentManager {
	/**
	 * Return true if torrent with such info_hash already contains in
	 * TorrentManager.
	 * 
	 * @param torrent
	 * @return true if torrent already exists.
	 */
	public boolean contains(Torrent torrent);

	/**
	 * Place torrent under TorrentManager control.
	 * 
	 * @param torrent
	 */
	public void add(Torrent torrent);
}

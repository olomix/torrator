package ws.alek.torrator.dao;

import java.io.File;
import java.io.IOException;

import ws.alek.torrator.torrent.Torrent;

public interface TorrentDAO {
	public void save(Torrent torrent) throws IOException;
	public Torrent create(File torrentFile) throws IOException;
	public Torrent[] getAll();
}

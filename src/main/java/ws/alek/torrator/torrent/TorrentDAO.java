package ws.alek.torrator.torrent;

import java.io.File;
import java.io.IOException;

public interface TorrentDAO {
	public void save(Torrent torrent);
	public Torrent create(File torrentFile) throws IOException;
	public Torrent[] getAll();
}

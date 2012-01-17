package ws.alek.torrator.torrent.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.torrent.Torrent;
import ws.alek.torrator.torrent.TorrentDAO;

public class TorrentDAOLocalFS implements TorrentDAO {

	@Override
	public void save(Torrent torrent) {
		// TODO Auto-generated method stub

	}

	@Override
	public Torrent create(File torrentFile) throws IOException {
		// File savedTorrentFile = saveTorrentFile(torrentFile); 
		// return Torrent.load(savedTorrentFile);
		return null;
	}

	@Override
	public Torrent[] getAll() {
		// TODO Auto-generated method stub
		return new Torrent[0];
	}
	

}

package ws.alek.torrator.torrent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import ws.alek.torrator.torrent.bencoded.BEDecoder;
import ws.alek.torrator.torrent.bencoded.BinaryString;
import ws.alek.torrator.torrent.bencoded.MetainfoReader;

public class Torrent {
	private InputStream torrentFileStream;
	private byte[] infoHash = null;
	private Peer[] peers;
	private List<Tracker> trackers;
	private static final Logger LOG = LoggerFactory.getLogger(Torrent.class);
	private boolean privateTorrent = false;
	private int pieceLength;
	private byte[][] pieces;
	private List<TorrentFile> files;

	public Torrent() {
	}
	
	public Torrent(InputStream in) {
		setTorrentFile(in);
	}

	@SuppressWarnings("unchecked")
	public void setTorrentFile(InputStream in) {
		torrentFileStream = in;
		BEDecoder decoder = new BEDecoder(in, true);

		Map<BinaryString, Object> metainfo;
		try {
			metainfo = (Map<BinaryString, Object>) decoder
					.decode();
		} catch (IOException e) {
			throw new IllegalArgumentException("Can't read from torrent file: " + e.getMessage());
		}

		setInfoHash(decoder.getInfoHash().getBytes());
		setPieces(MetainfoReader.readPieces(metainfo));
		setPieceLength(MetainfoReader.readPieceLength(metainfo));
		setPrivateTorrent(MetainfoReader.readPrivate(metainfo));
		setTrackers(MetainfoReader.readTrackers(metainfo));
		setFilesData(metainfo);
	}

	private void setFilesData(Map<BinaryString, Object> torrentMap) {
		throw new NotImplementedException();
		// TODO
	}

	private void setPieces(byte[][] pieces) {
		this.pieces = pieces;
	}

	private void setPieceLength(int length) {
		this.pieceLength = length;
	}

	private void setPrivateTorrent(boolean isPrivate) {
		this.privateTorrent = isPrivate;
	}

	private void setTrackers(List<Tracker> trackers) {
		this.trackers = trackers;
	}

	private void setInfoHash(byte[] infoHash) {
		if (infoHash.length != 20) {
			throw new IllegalArgumentException(
					"info_hash length not equal to 20.");
		}
		this.infoHash = infoHash;
	}

	/**
	 * Open new torrent from file
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Torrent open(File torrentFile) throws IOException {
		Torrent torrent = new Torrent();
		// Save file to permanent storate
		// torrent.saveTorrentFile(torrentFile);

		//

		return torrent;
	}

	/**
	 * Load existing torrent (on application start)
	 * 
	 * @return
	 */
	public static Torrent load(File torrentFile) {

		return null;
	}

}

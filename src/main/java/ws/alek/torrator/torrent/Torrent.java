package ws.alek.torrator.torrent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ws.alek.torrator.torrent.bencoded.BEDecoder;
import ws.alek.torrator.torrent.bencoded.BinaryString;
import ws.alek.torrator.torrent.bencoded.MetainfoReader;

public class Torrent implements Serializable {
	private static final long serialVersionUID = 1L;
	// TODO this stream is unset after restart. Do we need it at all?
	transient private InputStream torrentFileStream;
	private InfoHash infoHash = null;
	transient private Peer[] peers;
	private List<Tracker> trackers;
	private static final Logger LOG = LoggerFactory.getLogger(Torrent.class);
	private boolean privateTorrent = false;
	private int pieceLength;
	private byte[][] pieces;
	private List<TorrentFile> files;
	// If user stops the torrent, then it becomes inactive.
	private boolean active = true;
	// Set to true when downloading is finished
	private boolean finished = false;
	private File name;
	private boolean singleFile = true;

	public Torrent() {
	}
	
	@SuppressWarnings("unchecked")
	public Torrent(InputStream in) {
		setTorrentFile(in);
		BEDecoder decoder = new BEDecoder(in, true);

		Map<BinaryString, Object> metainfo;
		try {
			metainfo = (Map<BinaryString, Object>) decoder
					.decode();
		} catch (IOException e) {
			throw new IllegalArgumentException("Can't read from torrent file: " + e.getMessage());
		}

		setInfoHash(decoder.getInfoHash());
		setPieces(MetainfoReader.readPieces(metainfo));
		setPieceLength(MetainfoReader.readPieceLength(metainfo));
		setPrivateTorrent(MetainfoReader.readPrivate(metainfo));
		setTrackers(MetainfoReader.readTrackers(metainfo));
		setFiles(MetainfoReader.readFiles(metainfo));
		setName(MetainfoReader.readName(metainfo));
		setSingleFile(MetainfoReader.readSingleFile(metainfo));
	}

	public void setTorrentFile(InputStream in) {
		torrentFileStream = in;
	}

	private void setFiles(List<TorrentFile> files) {
		this.files = files;
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

	private void setInfoHash(InfoHash infoHash) {
		this.infoHash = infoHash;
	}
	
	public InfoHash getInfoHash() {
		return infoHash;
	}
	
	private void setName(File name) {
		this.name = name;
	}
	
	public File getName() {
		return name;
	}
	
	private void setSingleFile(boolean singleFile) {
		this.singleFile = singleFile;
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

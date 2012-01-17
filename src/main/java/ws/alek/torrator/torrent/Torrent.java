package ws.alek.torrator.torrent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;

import ws.alek.torrator.cfg.Configuration;
import ws.alek.torrator.torrent.bencoded.BEDecoder;
import ws.alek.torrator.torrent.bencoded.BinaryString;

public class Torrent {
	private File torrentFile;
	private byte[] info_hash = null;
	private boolean isActive = false;
	private Peer[] peers;

	public Torrent() {
	}
	
	public void setTorrentFile(File file) throws IOException {
		torrentFile = file;
		BEDecoder torrentFileDecoder = new BEDecoder(new FileInputStream(torrentFile), true);
		
		@SuppressWarnings("unchecked")
		Map<BinaryString, Object> torrentMap = (Map<BinaryString, Object>) torrentFileDecoder.decode();
		
		info_hash = torrentFileDecoder.getInfoHash().getBytes();
		if(info_hash.length != 20) {
			throw new IllegalStateException("info_hash length not equal to 20.");
		}
		
		// TODO
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
		torrent.saveTorrentFile(torrentFile);

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

	/**
	 * Save the given file to permanent storage.
	 * 
	 * @param newTorrentFile
	 *            torrent file to save
	 * @throws IOException
	 */
	private void saveTorrentFile(File newTorrentFile) throws IOException {
		torrentFile = new File(Configuration.getTorrentsDir(),
				newTorrentFile.getName());
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(newTorrentFile).getChannel();
			outputChannel = new FileOutputStream(torrentFile).getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
	}

}

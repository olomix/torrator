package ws.alek.torrator.torrent;

import java.io.File;

import ws.alek.torrator.cfg.Configuration;

public class TorrentFile {
	File file;
	int length;

	public TorrentFile(String fileName, int length) {
		File file = new File(fileName);
		setFile(file);
		setLength(length);
	}

	private void setLength(int length) {
		this.length = length;
	}

	private void setFile(File file) {
		if (file.isAbsolute()) {
			this.file = file;
		} else {
			this.file = new File(Configuration.getDownloadsDir(),
					file.getPath());
		}
	}
}

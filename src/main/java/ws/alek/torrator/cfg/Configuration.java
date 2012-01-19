package ws.alek.torrator.cfg;

import java.io.File;

public class Configuration {

	/**
	 * Create and return a directory to store working data.
	 * 
	 * @return data directory
	 */
	public static File getDataDir() {
		File dataDir = new File(System.getProperty("user.home"), ".torrator");
		getDir(dataDir);
		return dataDir;
	}

	/**
	 * Create and return a directory to store torrent files.
	 * 
	 * @return directory to save torret files
	 */
	public static File getTorrentsDir() {
		File torrentsDir = new File(getDataDir(), "torrents");
		getDir(torrentsDir);
		return torrentsDir;
	}

	/**
	 * Create and return a directory to download files into.
	 * 
	 * @return directory with downloaded files.
	 */
	public static File getDownloadsDir() {
		File downloadsDir = new File(getDataDir(), "downloads");
		getDir(downloadsDir);
		return downloadsDir;
	}

	/**
	 * For a givven path create a directory if it don't exists and check that it
	 * is actually a directory
	 * 
	 * @param path
	 * @return
	 */
	private static void getDir(File path) {
		// Create directory if it does not exists.
		if (!path.exists())
			synchronized (Configuration.class) {
				if (!path.exists())
					path.mkdir();
			}

		// Raise an exception if file not a directory
		if (!path.isDirectory())
			throw new IllegalStateException("File exists and not a directory: "
					+ path.getAbsolutePath());
	}

}

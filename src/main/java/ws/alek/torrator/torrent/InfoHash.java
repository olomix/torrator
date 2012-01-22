package ws.alek.torrator.torrent;

import java.util.Arrays;
import java.util.Formatter;

public class InfoHash {
	private final byte[] infoHash;
	private Integer hashCode;

	public InfoHash(byte[] infoHash) {
		if (infoHash == null) {
			throw new NullPointerException("InfoHash is null.");
		}

		if (infoHash.length != 20) {
			throw new IllegalArgumentException(
					"InfoHash length should be 20 bytes.");
		}

		this.infoHash = Arrays.copyOf(infoHash, infoHash.length);
		this.hashCode = Arrays.hashCode(this.infoHash);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof InfoHash) {
			InfoHash ih = (InfoHash) obj;
			return Arrays.equals(infoHash, ih.infoHash);
		}
		return false;
	}
	
	@Override
	public String toString() {
		Formatter f = new Formatter();
		for (byte b : infoHash) {
			f.format("%02x", b);
		}
		return f.toString();
	}
}

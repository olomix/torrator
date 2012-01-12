package ws.alek.torrator.torrent.bencoded;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryString {
	private final static Logger LOG = LoggerFactory
			.getLogger(BinaryString.class);
	private byte[] data;

	public BinaryString(byte[] inputData) {
		data = inputData.clone();
	}

	/**
	 * @return UTF-8 decoded String from binary data
	 */
	@Override
	public String toString() {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.error("Can't convert binary data to String with UTF-8: "
					+ toHexString());
			return "";
		}
	}

	/**
	 * @return string as hex dump.
	 */
	public String toHexString() {
		Formatter f = new Formatter();
		for (byte b : data) {
			f.format("%02x", b);
		}
		return f.toString();
	}

	public byte[] getBytes() {
		return data;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryString)
			return Arrays.equals(data, ((BinaryString) obj).getBytes());
		return false;
	}
}

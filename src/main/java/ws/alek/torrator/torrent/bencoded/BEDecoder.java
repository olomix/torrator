package ws.alek.torrator.torrent.bencoded;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BEDecoder {
	private final PushbackInputStream pis;
	// Let the maximum allowable string size would be 10M
	private final static long MAX_STRING_LENGTH = 10 * 1024 * 1024;

	private final static int MAX_LENGTH_FIELD_SIZE = 255;

	public BEDecoder(InputStream in) {
		pis = new PushbackInputStream(in, 1);
	}

	/**
	 * Decodes and return proper object from bencoded InputStream. Returned
	 * objects may be of type: BinaryString, BigInteger, List or Map.
	 * 
	 * @param in
	 *            InputStream to read bencoded data from
	 * @return BinaryString, BigInteger, List or Map
	 * @throws IOException
	 */
	public static Object decode(InputStream in) throws IOException {
		return new BEDecoder(in).decode();
	}

	private Object decode() throws IOException {
		int marker = pis.read();
		if (marker == -1) {
			throw new EOFException();
		} else if (marker >= '0' && marker <= '9') {
			pis.unread(marker);
			return readBinaryString();
		} else if (marker == 'i') {
			return readInteger();
		} else if (marker == 'l') {
			return readList();
		} else if (marker == 'd') {
			return readMap();
		}
		throw new BencodedFormatException("Invalid bencoded format.");
	}

	private BinaryString readBinaryString() throws IOException {
		StringBuilder sb = new StringBuilder();
		int ch;
		int counter = 0;
		while ((ch = pis.read()) >= '0' && ch <= '9') {
			sb.append((char) ch);

			if (++counter > MAX_LENGTH_FIELD_SIZE)
				throw new IllegalStateException("No, really? So big string?");
		}

		if (ch != ':' || counter <= 0)
			throw new BencodedFormatException(
					"Can't find ':' char after string length");

		BigInteger length = new BigInteger(sb.toString(), 10);
		if (length.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0
				|| length.intValue() > MAX_STRING_LENGTH)
			throw new IllegalArgumentException(
					"Maximum allowable size for binary strings is "
							+ MAX_STRING_LENGTH + " bytes");

		return new BinaryString(read(length.intValue()));
	}

	private BigInteger readInteger() throws IOException {
		byte[] bytes = new byte[MAX_LENGTH_FIELD_SIZE];
		int ch;
		int idx = 0;
		while ((ch = pis.read()) != 'e') {
			if (ch == -1)
				throw new EOFException();
			else if (ch < '0' || ch > '9')
				throw new BencodedFormatException("Invalid integer format.");

			bytes[idx] = (byte) ch;
			idx++;
			if (idx >= MAX_LENGTH_FIELD_SIZE) {
				throw new IllegalStateException("Integer length overflow.");
			}
		}

		return new BigInteger(new String(bytes, 0, idx));
	}

	private List<Object> readList() throws IOException {
		List<Object> list = new ArrayList<Object>();
		int ch;
		while ((ch = pis.read()) != 'e') {
			if (ch == -1)
				throw new EOFException();

			pis.unread(ch);
			list.add(decode());
		}
		return list;
	}

	private Map<BinaryString, Object> readMap() throws IOException {
		Map<BinaryString, Object> map = new HashMap<BinaryString, Object>();
		int ch;
		while((ch = pis.read()) != 'e') {
			if (ch == -1)
				throw new EOFException();

			pis.unread(ch);
			BinaryString key = readBinaryString();
			Object value = decode();
			map.put(key, value);
		}
		return map;
	}

	private byte[] read(int length) throws IOException {
		if (length > MAX_STRING_LENGTH) {
			throw new IllegalArgumentException(
					"Maximum allowable size for binary strings is "
							+ MAX_STRING_LENGTH + " bytes");
		}

		int size = 0;
		byte[] buf = new byte[length];
		int readLen;
		while (size < length) {
			readLen = pis.read(buf, size, length - size);
			if (readLen == -1)
				throw new EOFException();
			size += readLen;
		}
		return buf;
	}

}

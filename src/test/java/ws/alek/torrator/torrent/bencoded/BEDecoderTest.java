package ws.alek.torrator.torrent.bencoded;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class BEDecoderTest {
	@Test
	public void testString() throws IOException {
		String string = "5:hello";
		InputStream in = new ByteArrayInputStream(string.getBytes());
		Object obj = BEDecoder.decode(in);
		Assert.assertTrue(obj instanceof BinaryString);
		BinaryString bs = (BinaryString) obj;
		Assert.assertEquals("hello", bs.toString());
	}

	@Test(expected = BencodedFormatException.class)
	public void testStringInvalidFormat() throws IOException {
		String string = "5ello";
		InputStream in = new ByteArrayInputStream(string.getBytes());
		BEDecoder.decode(in);
	}

	@Test(expected = IllegalStateException.class)
	public void testStringTooLongLengthField() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 260; i++) {
			sb.append("1");
		}
		sb.append(":bla-bla");
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		BEDecoder.decode(in);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testStringLonger10M() throws IOException {
		StringBuilder sb = new StringBuilder();
		int length = 10 * 1024 * 1024 + 1;
		sb.append(Integer.toString(length) + ":");
		for (int i = 0; i < length; i++) {
			sb.append('a');
		}
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		BEDecoder.decode(in);
	}

	@Test
	public void testLongStringShorter10M() throws IOException {
		StringBuilder sb = new StringBuilder();
		int length = 10 * 1024 * 1024 - 1;
		for (int i = 0; i < length; i++) {
			sb.append('a');
		}
		String string = sb.toString();
		String buffer = Integer.toString(string.length()) + ":" + string;
		InputStream in = new ByteArrayInputStream(buffer.getBytes());
		BinaryString bs = (BinaryString) BEDecoder.decode(in);
		Assert.assertEquals(string, bs.toString());
	}

	@Test
	public void testInteger() throws IOException {
		String integer = "i100e";
		InputStream in = new ByteArrayInputStream(integer.getBytes());
		Object obj = BEDecoder.decode(in);
		Assert.assertTrue(obj instanceof BigInteger);
		BigInteger bi = (BigInteger) obj;
		Assert.assertTrue(bi.equals(BigInteger.valueOf(100L)));
	}

	@Test(expected = BencodedFormatException.class)
	public void testIntegerInvalidFormat() throws IOException {
		String integer = "i10a0e";
		InputStream in = new ByteArrayInputStream(integer.getBytes());
		BEDecoder.decode(in);
	}

	@Test(expected = IllegalStateException.class)
	public void testIntegerTooLong() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append('i');
		for (int i = 0; i < 255; i++) {
			sb.append('1');
		}
		sb.append('e');
		InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
		BEDecoder.decode(in);
	}

	@Test
	public void testIntegerVeryLong() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 254; i++) {
			sb.append('1');
		}
		BigInteger bi = new BigInteger(sb.toString());
		String biBuffer = "i" + bi.toString() + "e";
		InputStream in = new ByteArrayInputStream(biBuffer.getBytes());
		BigInteger out = (BigInteger) BEDecoder.decode(in);
		Assert.assertTrue(bi.equals(out));
	}

	@Test
	public void testList() throws IOException {
		String list = "li3e2:abe";
		List<Object> jList = new ArrayList<Object>();
		jList.add(new BigInteger("3"));
		jList.add(new BinaryString(new byte[] { 'a', 'b' }));
		InputStream in = new ByteArrayInputStream(list.getBytes());
		Object obj = BEDecoder.decode(in);
		Assert.assertTrue(obj instanceof List<?>);
		@SuppressWarnings("unchecked")
		List<Object> objList = (List<Object>) obj;
		Assert.assertTrue(objList.equals(jList));
	}
	
	@Test
	public void testListEmpty() throws IOException {
		String list = "le";
		List<Object> jList = new ArrayList<Object>();
		InputStream in = new ByteArrayInputStream(list.getBytes());
		Object obj = BEDecoder.decode(in);
		Assert.assertTrue(obj instanceof List<?>);
		@SuppressWarnings("unchecked")
		List<Object> objList = (List<Object>) obj;
		Assert.assertTrue(objList.equals(jList));
	}

	@Test
	public void testMap() throws IOException {
		// { 'abc': 4; 'def': [1, 2, 3, 'aaa'], "ghi": {'a': 1} }
		String bencodedMap = "d3:abci4e3:defli1ei2ei3e3:aaae3:ghid1:ai1eee";
		InputStream in = new ByteArrayInputStream(bencodedMap.getBytes());
		Object obj = BEDecoder.decode(in);
		Assert.assertTrue(obj instanceof Map<?, ?>);
		
		Map<BinaryString, Object> map = new HashMap<BinaryString, Object>();
		map.put(new BinaryString(new byte[] { 'a', 'b', 'c'}), BigInteger.valueOf(4));
		
		List<Object> list = new ArrayList<Object>();
		list.add(BigInteger.valueOf(1));
		list.add(BigInteger.valueOf(2));
		list.add(BigInteger.valueOf(3));
		list.add(new BinaryString(new byte[] { 'a', 'a', 'a'}));
		map.put(new BinaryString(new byte[] { 'd', 'e', 'f'}), list);
		
		Map<BinaryString, Object> map2 = new HashMap<BinaryString, Object>();
		map2.put(new BinaryString(new byte[] { 'a'}), BigInteger.valueOf(1));
		map.put(new BinaryString(new byte[] { 'g', 'h', 'i'}), map2);
		
		Assert.assertTrue(map.equals(obj));
	}
}

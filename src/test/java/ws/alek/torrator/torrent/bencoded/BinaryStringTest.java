package ws.alek.torrator.torrent.bencoded;

import org.junit.Assert;
import org.junit.Test;

public class BinaryStringTest {
	
	@Test
	public void testToStringSuccess() {
		BinaryString bs = new BinaryString(new byte[] {0x31, 0x32, 0x33});
		Assert.assertEquals("123", bs.toString());
	}
	
	@Test
	public void testToHexString() {
		BinaryString bs = new BinaryString(new byte[] {0x31, 0x32, (byte) 0xfe, -1});
		Assert.assertEquals("3132feff", bs.toHexString());
	}
}

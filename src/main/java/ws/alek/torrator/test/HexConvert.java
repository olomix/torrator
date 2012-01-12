package ws.alek.torrator.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ws.alek.torrator.torrent.bencoded.BEDecoder;

public class HexConvert {

	public static void main(String[] args) {
		File file = new File("/Users/alek/Downloads/[pornolab.net].t1402046.torrent");
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}

		Object obj = null;
		try {
			obj = BEDecoder.decode(in);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		System.out.println(obj);
		System.exit(0);
	}
}

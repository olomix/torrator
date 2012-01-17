package ws.alek.torrator.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


import ws.alek.torrator.torrent.bencoded.BEDecoder;

public class HexConvert {

	public static void main(String[] args) throws IOException {

		System.out.println(args[0]);
		File torrent = new File(args[0]);
		BEDecoder d = new BEDecoder(new FileInputStream(torrent), true);
		d.decode();
		System.out.println(d.getInfoHash().toHexString());
		System.exit(0);
		
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

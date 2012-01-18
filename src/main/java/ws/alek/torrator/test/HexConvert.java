package ws.alek.torrator.test;

import java.io.File;
import java.io.IOException;

public class HexConvert {

	public static void main(String[] args) throws IOException {
		File f = new File("/1/2/3.txt");
		File f2 = new File("2/3/../../../../../../../etc/passwd");
		System.out.println(f.getParent());
		System.out.println(f.getPath());
		System.out.println(f2.getParent());
		System.out.println(f2.getPath());
		System.out.println(f2.getAbsolutePath());
		System.out.println(f2.getCanonicalPath());
		
		File f3 = new File(File.separatorChar + "Users" + File.separatorChar + "alek");
		File f4 = new File(f3, "hello.txt");
		System.out.println(f4.getCanonicalPath());
		System.exit(0);
	}
}

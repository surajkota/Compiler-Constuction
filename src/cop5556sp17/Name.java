package cop5556sp17;
import java.io.File;
import cop5556sp17.PLPRuntimeImageIO;
import cop5556sp17.PLPRuntimeImageOps;
import cop5556sp17.PLPRuntimeFrame;

import java.awt.image.BufferedImage;
import java.net.URL;

public class Name implements Runnable {
	//public URL u;
	public URL u;
	public URL f;
	public int a;

	public Name(String[] args) {
		this.u = PLPRuntimeImageIO.getURL(args, 0);
		this.f = PLPRuntimeImageIO.getURL(args, 0);
		this.a = Integer.parseInt(args[2]);
	}

	public static void main(String[] args) {
		(new Name(args)).run();
	}

	public void run() {

//		try {
//			Thread.sleep((long)1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		PLPRuntimeLog.globalLogAddEntry("\nentering run");
		BufferedImage img1 = null;
		BufferedImage img2 = null;
		PLPRuntimeFrame fr = null;
		img1 = PLPRuntimeImageIO.readFromURL(this.u);
		img2 = PLPRuntimeImageIO.readFromURL(this.f);
		img2 = PLPRuntimeImageOps.sub(img2, img1);

		}
}

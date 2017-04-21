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

	public Name(String[] args) {
		this.u = PLPRuntimeImageIO.getURL(args, 0);
	}

	public static void main(String[] args) {
		(new Name(args)).run();
	}

	public void run() {
		BufferedImage j = null;
		PLPRuntimeFrame f = null;
		j = PLPRuntimeImageOps.copyImage(PLPRuntimeImageIO.readFromURL(this.u));
		f = PLPRuntimeFrame.createOrSetFrame((BufferedImage) j, f);
		f.showImage();
		try {
			Thread.sleep((long)2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int x;
		int y;
		x = f.getXVal();
		y = f.getYVal();
		f.moveFrame(x+100, y-100);
		f.showImage();
	}
}
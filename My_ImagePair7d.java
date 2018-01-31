	// My_ImagePair7d.java		Michael Konrad		code: 21 Nov 2016		run: 21 Nov 2016
	// My_ImagePair7.java series; only creates a pixel when difference image exceed tHold
	// _7d; only processes pixels inside mask; this app a workbench to test algorithms
	// 20 Nov: output center of mass of hits
		import ij.process.*;
		import ij.plugin.filter.*;
		import ij.ImagePlus;
		import java.io.*;
		import java.lang.*;

		public class My_ImagePair7d implements PlugInFilter {
			ImagePlus imp;
			@Override

			public int setup(String arg, ImagePlus imp) {
				this.imp = imp;
				return DOES_ALL+DOES_STACKS+SUPPORTS_MASKING;
			}

		// must be declared outside the run(ImageProcessor) loop
			public int iImage = 0;
			public int refImage [][] = new int [640][480];	// old (previous) frame
			public int cumImage [][] = new int [640][480];	// new (current) frame
			public int kPixel;								// temp value of pixel
			public double dimDown = 0.5;					// dimDown -> darker image of animal for background

			public boolean bMask [][] = new boolean [640][480];
			public int p1;									// pixel value previous image
			public int p2;									// 				present image
			public int pDif, pDisplay;						// frame difference, display color
			public int pCum;								// 				cumulative image
			public int tHold = 6;							// threshold for display
			public int colorBlack = 0;						// value of black
			public int colorWhite = 255;					// white
			public double maskL = 260.0;					// x of left edge of mask (region detected)
			public double maskR = 390.0;
			public double maskW = 130.0;					// x width of mask
			public int nHits[] = new int[18000];
			public int xHits[] = new int [18000];
			public int yHits[] = new int [18000];
			public double dx,dy,dn;
			public double xCenter,yCenter;
			public int iPrint = 298;						// print hits(frame no) at this frame

		// ip called repeatedly to process all images in stack
			@Override
			public void run(ImageProcessor ip) {
				int w = ip.getWidth();
				int h = ip.getHeight();

		// iImage = 0	-------------------------------------------	 make logical mask array (even if not used) from 1st frame 
				if (iImage == 0) {									// create bMask
					for (int y = 0; y < h; y++) {					// move down y (horizontal) scan lines
						for (int x = 0; x < w; x++) {				// move across each y line
							kPixel = ip.getPixel(x,y);
							if ( kPixel < 128) {bMask [x][y] = true;}	// true in black region
							else { bMask [x][y] = false; }
						}
					}
				}
		// iImage = 1 ----------------------------------------------   copy to image0; add dim image of animal to cumImage
				if (iImage == 1) {
					for (int y = 0; y < h; y++) {					// move down y (horizontal) scan lines
						for (int x = 0; x < w; x++) {				// move across each y line
							refImage[x][y] = ip.getPixel(x, y);
						}
					}
				}
		// iImage > 1 ----------------------------------   get new image, make 2 hitImages, save new image as old for next loop
				if (iImage > 1) {
					for (int y = 0; y < h; y++) {
						for (int x = 0; x < w; x++) {
							p1 = refImage[x][y];				// get previous image
							p2 = ip.getPixel(x, y);				// get present image
							refImage[x][y] = p2;	 			// save present image
							pDif = p1 - p2;						// previous - present pixel value
							if ((pDif > tHold) & (bMask[x][y])) {
								pDisplay = colorBlack;			// color pixel black
							// compute parameters to follow beat
								nHits[iImage] ++;
								xHits[iImage] += x;
								yHits[iImage] += y;
							}
							else {pDisplay = colorWhite;}
							
							cumImage[x][y] = pDisplay;			// put into cumImage- but it's not really a cummulative image
							ip.putPixel(x, y, cumImage[x][y]);	// display
							
						}
					}
				}
				
				if (iImage == iPrint) {
					try {
						PrintWriter outWriter = new PrintWriter("ImagePairOutput");
						outWriter.printf ("%s\t%6d\n", "My_ImagePair7d.java",iPrint);
						for (int j = 0; j < iPrint; j++) {
							if(nHits[j] != 0) {
								xCenter = (dx = xHits[j]) / (dn = nHits[j]);
								yCenter = (dy = yHits[j]) / (dn = nHits[j]);
							}
							outWriter.printf("%6d\t%8.2f\t%8.2f\t%8.2f\n",j,nHits[j],xCenter,yCenter);
						}
						outWriter.flush();
						outWriter.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			iImage++;
			}
		}
			
		



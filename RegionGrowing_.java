
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Stack;

import ij.*;
import ij.gui.GenericDialog;
import ij.gui.PointRoi;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class RegionGrowing_ implements PlugInFilter {

	ImagePlus imp1;
	
	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		
		imp1 = imp;
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING + ROI_REQUIRED;
	} // setup
	
	public static int[][] performRegionGrowing(int[][] inImgArr, int width, int height, int lowerThresh, int upperThresh, int seedX, int seedY) {
		// constants
		int BG_VAL = 0;
		int FG_VAL = 255;
		int UNPROCESSED_VAL = -1;
		
		int[][] returnArr = new int[width][height];
		
		for( int x= 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnArr[x][y] = UNPROCESSED_VAL;
			}
		}
		
		Stack<Point> processingStack = new Stack<Point>();
		
		//first check if seed point is valid
		int seedVal = inImgArr[seedX][seedY];
		if(seedVal >= lowerThresh && seedVal <= upperThresh) {
			processingStack.push(new Point(seedX, seedY));
			returnArr[seedX][seedY] = FG_VAL;
		}
		
		while(!processingStack.empty()) {
			Point nextPos = processingStack.pop();
			
			//check all children in N4
			for( int xOffset= -1; xOffset <= 1; xOffset++) {
				for (int yOffset = -1; yOffset <= 1; yOffset++) {
					int nbX = nextPos.x + xOffset;
					int nbY = nextPos.y + yOffset;
					
					// check if valid range ==> position within image boundaries
					if(nbX >= 0 && nbY >= 0 && nbX < width && nbY < height) {
						
						int nbVal = inImgArr[nbX][nbY];
						//check if pixel is unprocessed and if vlaue in threshold range
						if(returnArr[nbX][nbY] == UNPROCESSED_VAL) {
							//check if range valid
							if(nbVal >= lowerThresh && nbVal <= upperThresh) {
								returnArr[nbX][nbY] = FG_VAL;
								processingStack.push(new Point(nbX,nbY));
							}
							else {
								returnArr[nbX][nbY] = BG_VAL;
							}
						}
					}
				}//for yOffset
			}// for xOffset
		
		} //while
		
		System.out.println(processingStack.size());
		
		//cleanup - all values still unprocessed - get assigned the background value BG_VAL
		for( int x = 0; x < width; x++) {
			for (int y = 0; y< height; y++) {
				if(returnArr[x][y] == UNPROCESSED_VAL) {
					returnArr[x][y] = BG_VAL;
				}
			}
		}
		
		
		return returnArr;
	}


	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		//request seed point
		PointRoi pr = (PointRoi)imp1.getRoi();
		Rectangle rect = pr.getBounds();
		int xStart = pr.getXCoordinates()[0] + rect.x;
		int yStart = pr.getXCoordinates()[0] + rect.y;
		
		System.out.println("xStart : " + xStart +  ", yStart: " + yStart);
		
		// user input - default
		int lowerThresh = 100;
		int upperThresh = 255;
		// user dialog
		GenericDialog gd = new GenericDialog("thresh params");
		gd.addSlider("lower thresh", 0, 255, lowerThresh);
		gd.addSlider("upper thresh", 0, 255, upperThresh);
		gd.showDialog();

		if (!gd.wasCanceled()) {
			lowerThresh = (int) gd.getNextNumber();
			upperThresh = (int) gd.getNextNumber();
		}
		
		//finally calling functiopn
		int[][] resultImg = performRegionGrowing(inDataArrInt, width, height, lowerThresh, upperThresh, xStart, yStart);
		
		ImageJUtility.showNewImage(resultImg, width, height, "region coin result");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

} // class FilterTemplate_

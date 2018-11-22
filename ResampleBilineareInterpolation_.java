
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import ij.gui.GenericDialog;

public class ResampleBilineareInterpolation_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		int newWidth = width;
		int newHeight = height;

		// first request target scale factor from user
		GenericDialog dialog = new GenericDialog("user input");
		dialog.addNumericField("scale factor", 1.0, 2);
		dialog.showDialog();

		// if user has canceled the dialog
		if (dialog.wasCanceled()) {
			return;
		}

		//get user input of scale factor
		double tgtScaleFactor = dialog.getNextNumber();

		// check range
		if (tgtScaleFactor < 0.01 || tgtScaleFactor > 10) {
			return;
		}

		//calculate new width and height with scale factor
		newWidth = (int) (width * tgtScaleFactor + 0.5);
		newHeight = (int) (height * tgtScaleFactor + 0.5);

		// calculate scale factor per dimension (variant a)
		double scaleFactorX = newWidth / ((double) width);
		double scaleFactorY = newHeight / ((double) height);
		
		//information output
		System.out.println("tgtScale = " + tgtScaleFactor + "sX=" + scaleFactorX + "sY=" + scaleFactorY);
		System.out.println("new width = " + newWidth + "new height = " + newHeight);

		int[][] scaledImg = new int[newWidth][newHeight];

		// fill new result image --> iterate over result image
		for (int x = 0; x < newWidth; x++) {
			for (int y = 0; y < newHeight; y++) {
				// calculate new coordinate
				double newX = x / scaleFactorX;
				double newY = y / scaleFactorY;

				//get bilinear interpolated value
				double resultVal = GetBilinearInterpolatedValue(inDataArrInt, newX, newY, width, height);
				
				//set new rounded value for current location
				scaledImg[x][y] = (int) (resultVal + 0.5);

			}
		}

		//show new image
		ImageJUtility.showNewImage(scaledImg, newWidth, newHeight, "scaled img (bilinear interpolation");

	} // run

	public double GetBilinearInterpolatedValue(int[][] inImg, double x, double y, int width, int height) {
		
		// calculate the delta for x and y
		double deltaX = x - Math.floor(x);
		double deltaY = y - Math.floor(y);

		// set calculation fregment
		int xPlus1 = (int) x + 1;
		int yPlus1 = (int) y + 1;

		//handling of image edge for x
		if (x + 1 >= width) {
			xPlus1 = (int) x;
		} 
		
		//handling of image edge for y
		if (y + 1 >= height) {
			yPlus1 = (int) y;
		}

		// get 4 neighboring pixels
		int neighbor1 = inImg[xPlus1][(int) (y)];
		int neighbor2 = inImg[(int) (x)][yPlus1];
		int neighbor3 = inImg[xPlus1][yPlus1];
		int neighbor4 = inImg[(int) (x)][(int) (y)];

		// calculate weighted mean out of neighbors
		double weightedMean = ((1 - deltaX) * (1 - deltaY) * neighbor4) + (deltaX * (1 - deltaY) * neighbor1)
				+ ((1 - deltaX) * deltaY * neighbor2) + (deltaX * deltaY * neighbor3);

		return weightedMean;
	}

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

} // class ResampleBilinearInterpolation_

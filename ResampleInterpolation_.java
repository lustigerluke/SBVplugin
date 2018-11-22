import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;

public class ResampleInterpolation_ implements PlugInFilter {

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
		
		int newWidth = width;
		int newHeight = height;

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		// first request target scale factor from user
		GenericDialog dialog = new GenericDialog("user input");
		dialog.addNumericField("scale factor: ", 1.0, 2);
		//second request checker board size from user
		dialog.addNumericField("checker board size: ", 4.0, 2);
		dialog.showDialog();

		if (dialog.wasCanceled()) {
			return;
		}

		double tgtScaleFactor = dialog.getNextNumber();
		if (tgtScaleFactor < 0.01 || tgtScaleFactor > 10) {
			return;
		}

		int segments = (int) (dialog.getNextNumber());
		if (segments < 0.01 || tgtScaleFactor > 20) {
			return;
		}

		newWidth = (int) (width * tgtScaleFactor + 0.5);
		newHeight = (int) (height * tgtScaleFactor + 0.5);

		// variant A of transformation of coordinates
		// double scaleFactorX = newWidth / (double)(width);
		// double scaleFactorY = newHeight / (double)(height);

		// variant B of transformation of coordinates
		double scaleFactorX = (double) (newWidth - 1.0) / (double) (width - 1.0);
		double scaleFactorY = (double) (newHeight - 1.0) / (double) (height - 1.0);

		// information output
		System.out.println("tgtScale: " + tgtScaleFactor + ", sX: " + scaleFactorX + ", sY: " + scaleFactorY);
		System.out.println("new width: " + newWidth + ", new height: " + newHeight);

		// arrays for the two different interpolated images
		int[][] scaledImageNN = new int[newWidth][newHeight];
		int[][] scaledImageBI = new int[newWidth][newHeight];

		// variables for checkerboard
		int segmentsWidth = newWidth / segments;
		int segmentsHeight = newHeight / segments;
		int[][] scaledCheckerBoard = new int[newWidth][newHeight];

		// iterate over all pixel of the scaled images
		//variable for x-axis checker board switch
		boolean switchX = false;
		for (int x = 0; x < newWidth; x++) {
			if (x % segmentsWidth == 0) {
				if (switchX == true) {
					switchX = false;
				} else {
					switchX = true;
				}
			}

			//variable for y-axis checker board switch
			boolean switchY = false;
			for (int y = 0; y < newHeight; y++) {
				if (y % segmentsHeight == 0) {
					if (switchY == true) {
						switchY = false;
					} else {
						switchY = true;
					}
				}

				// calculate new scaled x and y coordinates
				double newX = (double) (x) / scaleFactorX;
				double newY = (double) (y) / scaleFactorY;

				// calculate new result values
				int resultValNN = GetNNinterpolatedValue(inDataArrInt, newX, newY, width, height);
				double resultValBI = GetBilinearinterpolatedValue(inDataArrInt, newX, newY, width, height);
				
				// set new values for NN and BI
				scaledImageNN[x][y] = resultValNN;
				scaledImageBI[x][y] = (int) (resultValBI + 0.5);

				//set new values for checker board
				if (switchY == switchX) {
					scaledCheckerBoard[x][y] = resultValNN;
				} else {
					scaledCheckerBoard[x][y] = (int) (resultValBI + 0.5);
				}
			}
		}

		// show new image
		ImageJUtility.showNewImage(scaledImageNN, newWidth, newHeight, "scaled img: nearest neighbor interpolation");
		ImageJUtility.showNewImage(scaledImageBI, newWidth, newHeight, "scaled img: bilinear interpolation");
		ImageJUtility.showNewImage(scaledCheckerBoard, newWidth, newHeight, "scaled img: checkerboard of NN and BI");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	// caclulates nearest neighbor interpolation
	public int GetNNinterpolatedValue(int[][] inImg, double x, double y, int width, int height) {
		// round x and y position
		int xPos = (int) (x + 0.5);
		int yPos = (int) (y + 0.5);

		// safety check
		if (xPos >= 0 && xPos < width && yPos >= 0 && yPos < height) {
			return inImg[xPos][yPos];
		}
		return 0;
	}

	// calculates bilinear interpolation
	public double GetBilinearinterpolatedValue(int[][] inImg, double x, double y, int width, int height) {
		// calculate the delta for x and y
		double deltaX = x - Math.floor(x);
		double deltaY = y - Math.floor(y);

		// set calculation fregment
		int xPlus1 = (int) x + 1;
		int yPlus1 = (int) y + 1;

		// handling of image edge for x
		if (x + 1 >= width) {
			xPlus1 = (int) x;
		}

		// handling of image edge for y
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

} // class ResampleInterpolation_

import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;
import java.awt.Rectangle;
import java.util.Arrays;

import com.sun.net.httpserver.Authenticator.Success;

public class Median_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	public void run(ImageProcessor ip) {

		System.out.println("RUN: Plugin Median");
		int width = ip.getWidth();
		int height = ip.getHeight();


		int radius = getUserInputRadius(4);
		// int radius = 2; // default value for debugging

		if (2 * radius > width || 2 * radius > height) {
			System.out.println("Be aware that double the radius has to fit in the image!");
		}

		double[][] resultImage = runFilter(ip, radius);
		
		System.out.println("Now show the result image!");
		ImageJUtility.showNewImage(resultImage, width, height, "mean with kernel r=" + radius);
		System.out.println("SUCCESS: MEDIAN FILTER DONE.");
		
		System.out.println("Now plot 4x4 to see filtereffect.");

		plot4x4(ip, resultImage);

	} // run

	private void plot4x4(ImageProcessor ip, double[][] filteredImg) {
		int segments = 4;
		
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		double[][] resultImg = ImageJUtility.convertToDoubleArr2D(inArr, width, height);

		
		int xCaroLength = width / segments;
		int yCaroLength = height / segments;
		
		// for every region
		for (int i = 0; i < segments; i++) {
			for (int j = 0; j < segments; j++) {
				if ((i+j) % 2 == 0) {
					int xIndex = i*xCaroLength;
					int yIndex = j*yCaroLength;
					// calculate region
					Rectangle roi = new Rectangle(xIndex, yIndex, xCaroLength, yCaroLength);
					double[][] tmpImg = ImageJUtility.cropImage(filteredImg, roi.width, roi.height, roi);
					
					// copy region to result image
					for (int x = 0; x < xCaroLength; x++) {
						for (int y = 0; y < yCaroLength; y++) {
							resultImg[xIndex + x][yIndex + y] =  tmpImg[x][y];
						}
					}			
				}
			}
		}
		
		ImageJUtility.showNewImage(resultImg, width, height, "4x4 caro for filter effect evaluation");
		
	}

	public static double[][] runFilter(ImageProcessor ip, int radius) {
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		double[][] inDataArrDouble = ImageJUtility.convertToDoubleArr2D(inArr, width, height);
		
		double[][] resultImage = inDataArrDouble.clone();
		int successIndex = 0;
		int failureIndex = 0;
		// step1: move mask to all possible image pixel positions
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double[][] mask = inDataArrDouble;
				Rectangle roi = getROI(width, height, x, y, radius);
				mask = ImageJUtility.cropImage(mask, roi.width, roi.height, roi);
				double median = getMedian(mask,roi.width,roi.height);
				resultImage[x][y] = median;
			}
		}

		return resultImage;
	}

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	/**
	 * get region of interest. defined by a Rectangle with x and y coorinates of the
	 * upper left corner and width and hight as parameters.
	 * 
	 * @param width  of the image
	 * @param height of the image
	 * @param x      the x coordinate of the center of the mask
	 * @param y      the y coodrinate of the center of the mask
	 * @param radius of the mask
	 * @return
	 */
	public static Rectangle getROI(int width, int height, int x, int y, int radius) {
		int xsize = 2 * radius + 1;
		int ysize = 2 * radius + 1;

		
		// special behaviour
		if (x - radius < 0) {
			xsize = xsize - (radius - x);
			x = radius;
		}// set minimum x
		if (y - radius < 0) {
			ysize = ysize - (radius - y);
			y = radius;
		} // set minimum y
		
		
		if (x + radius >= width) {
			int d = (radius - (width - x));
			xsize = xsize - d - 1 ;
		}// set maximum x
		if (y + radius >= height) {
			int d = (radius - (height - y));
			ysize = ysize - d - 1 ;
		} // set maximum y
		
		return new Rectangle(x - radius, y - radius, xsize, ysize);
	}

	public static double getMedian(double[][] inputImg, int width, int height) {
		int size = width * height;

		// fill array
		double[] arr = new double[size];
		int index = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				arr[index] = inputImg[i][j];
				index++;
			}
		}

		// sort array
		Arrays.sort(arr);
		return arr[(int) (size / 2 + 1)];
	}

	/**
	 * Asks the user to input a radius.
	 * 
	 * @return radius from user input. 0 if failed.
	 */
	public static int getUserInputRadius(int defaultValue) {
		// user input
		System.out.println("Read user input: radius");
		GenericDialog gd = new GenericDialog("user input:");
		gd.addNumericField("radius", defaultValue, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return 0;
		}
		return (int) gd.getNextNumber();
	}

} // class FilterTemplate_

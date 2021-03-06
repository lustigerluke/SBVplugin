import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;

public class Gauss_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int tgtRadius = getUserInput(4, "radius");
		int sigma = getUserInput(4, "sigma");

		double[][] resultImage = runFilter(ip, tgtRadius, sigma);

		ImageJUtility.showNewImage(resultImage, width, height, "mean with kernel r=" + tgtRadius);

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	/**
	 * Asks the user to input.
	 * 
	 * @return value from user input. 0 if failed.
	 */
	public static int getUserInput(int defaultValue, String nameOfValue) {
		// user input
		System.out.print("Read user input: " + nameOfValue);
		GenericDialog gd = new GenericDialog("user input:");
		gd.addNumericField("defaultValue", defaultValue, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return 0;
		}
		int radius = (int) gd.getNextNumber();
		System.out.println(radius);
		return radius;
	}

	public static double[][] runFilter(ImageProcessor ip, int radius, int sigma) {
		// convert to pixel array
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		int tgtRadius = radius;
		int size = 2 * radius +1;

		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		double[][] inDataArrDouble = ImageJUtility.convertToDoubleArr2D(inArr, width, height);

		double[][] filterMask = ConvolutionFilter.GetGaussMask(tgtRadius, sigma);
		int[][] filterMaskInt = convert2Int(filterMask);
		ImageJUtility.showNewImage(filterMaskInt, size, size, "GaussMask");
		
		return ConvolutionFilter.ConvolveDoubleNorm(inDataArrDouble, width, height, filterMask, tgtRadius);

	}
	
	
	public static int[][] convert2Int(double[][] inMask) {
		double[][] tmpMask = inMask.clone();
		int size = inMask.length;
		int[][] maskInt = new int[size][size];
		int maxInt = 255;
		
		// get maximum
		double maxDouble = 0;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j ++) {
				if (maxDouble < tmpMask[i][j] ) { maxDouble = tmpMask[i][j];}
			}
		}
		
		// scale mask
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j ++) {
				
				maskInt[i][j] = (int) (tmpMask[i][j] / maxDouble * maxInt);
			}
		}
		
		
		return maskInt;
		
		
	}

} // class FilterTemplate_


import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import ij.gui.GenericDialog;


public class Mean_ implements PlugInFilter {
  
   public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
	} //setup
	

	
	public void run(ImageProcessor ip) {
		int width = ip.getWidth();
		int height = ip.getHeight();
		int tgtRadius = 4; //default value
		
		tgtRadius = getUserInputRadius(tgtRadius);
		
		double[][] resultImage = runFilter(ip, tgtRadius);
		
		ImageJUtility.showNewImage(resultImage, width, height, "mean with kernel r=" + tgtRadius);
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
	public static double[][] runFilter(ImageProcessor ip, int radius) {
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		double[][] inDataArrDouble = ImageJUtility.convertToDoubleArr2D(inArr, width, height);
		double[][] filterMask = ConvolutionFilter.GetMeanMask(radius);
		double[][] resultImage = ConvolutionFilter.ConvolveDoubleNorm(inDataArrDouble, width, height, filterMask, radius);
		return resultImage;
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
	
} //class FilterTemplate_



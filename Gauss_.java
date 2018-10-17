import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import ij.gui.GenericDialog;

public class Gauss_ implements PlugInFilter {
  
   public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
	} //setup
	

	
	public void run(ImageProcessor ip) {
		
		// convert to pixel array
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		int tgtRadius = 4;
		
		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		double[][] inDataArrDouble = ImageJUtility.convertToDoubleArr2D(inArr, width, height);
		
		
		//user input for radius
		GenericDialog gd = new GenericDialog("user input:");
		gd.addNumericField("radius", tgtRadius, 0);
		gd.showDialog();
		if(gd.wasCanceled()) {return;}
		tgtRadius = (int)gd.getNextNumber();
		
		double[][] filterMask = ConvolutionFilter.GetGaussMask(tgtRadius,4);
		ImageJUtility.showNewImage(filterMask, filterMask.length, filterMask.length, "Gauss Mask");
        
		//double[][] resultImage = ConvolutionFilter.ConvolveDouble(inDataArrDouble, width, height, filterMask, tgtRadius);
		double[][] resultImage = ConvolutionFilter.ConvolveDoubleNorm(inDataArrDouble, width, height, filterMask, tgtRadius);
        ImageJUtility.showNewImage(resultImage, width, height, "mean with kernel r=" + tgtRadius);
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
} //class FilterTemplate_



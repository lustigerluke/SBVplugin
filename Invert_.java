
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class Invert_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+SUPPORTS_MASKING;
	} //setup

	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		//original Image
		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		
		System.out.println("w = " + width + " h = "+ height);
		
		int[] invertTF = ImageTransformationFilter.GetInversionTF(255);
		
		int[][] resultImg = ImageTransformationFilter.GetTransformedImage(inArr, width, height, invertTF);
		
		ImageJUtility.showNewImage(resultImg, width, height, "RemInvert");
		
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"image invert\n");
	} //showAbout
	
} //class template_



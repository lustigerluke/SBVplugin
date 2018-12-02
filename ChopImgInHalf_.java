import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.Rectangle;
import java.awt.*;
import ij.gui.GenericDialog;

public class ChopImgInHalf_ implements PlugInFilter {
  
	
   public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
	} //setup
	

	
	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
        int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
        
		double widthHalf = width / 2.0;
		double[][] tmpImage = ImageJUtility.convertToDoubleArr2D(inDataArrInt, width, height);
		Rectangle roi = new Rectangle(0, 0, (int)widthHalf, height);
		double[][] Img1 = ImageJUtility.cropImage(tmpImage, roi.width, roi.height, roi);
		ImageJUtility.showNewImage(Img1, (int)widthHalf, height, "first half image");
		roi = new Rectangle((int)widthHalf, 0, (int)widthHalf, height);
		double[][] Img2 = ImageJUtility.cropImage(tmpImage, roi.width, roi.height, roi);
		ImageJUtility.showNewImage(Img2, (int)widthHalf, height, "first half image");
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
} //class FilterTemplate_


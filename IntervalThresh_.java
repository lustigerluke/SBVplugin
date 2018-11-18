
import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class IntervalThresh_ implements PlugInFilter {
  
   public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
	} //setup
   
   public int[][] performIntervalThresh(int[][] inImg, int width, int height, int BG_VAL, int FG_VAL, int lowerThresh, int upperThresh) {
	   int[][] resultImg = new int[width][height];
	   
	   for(int x = 0; x < width; x ++) {
		   for (int y = 0; y < height; y++) {
			   int currVal = inImg[x][y];
			   
			   //check for FG pixel
			   if (currVal >= lowerThresh && currVal <= upperThresh) {
				   resultImg[x][y] = FG_VAL;
				   
			   }
			   else resultImg[x][y] = BG_VAL;
			   
		   }
	   }
	   
	   
	   return resultImg;
   }
	
	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		
		// user input
		int lowerThresh = 80;
		int upperThresh = 120;
		
		// constants
		int BG_VAL= 0;
		int FG_VAL= 255;
		
		GenericDialog gd = new GenericDialog("thresh params");
		gd.addSlider("lower thresh", 0,255, 100);
		gd.addSlider("upper thresh", 0,255, 200);
		gd.showDialog();
		
		if(gd.wasCanceled()) {
			lowerThresh = (int)gd.getNextNumber();
			upperThresh = (int)gd.getNextNumber();
		}
		
		
		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		int[][] resultImg = performIntervalThresh(inDataArrInt, width, height, BG_VAL, FG_VAL, lowerThresh, upperThresh);
				
		
        inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
                          
        ImageJUtility.showNewImage(inDataArrInt, width, height, "threshold image");
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
} //class FilterTemplate_


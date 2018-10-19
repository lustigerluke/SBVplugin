
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import java.awt.*;

import ij.gui.GenericDialog;


public class Resample_ implements PlugInFilter {
  
   public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about"))
			{showAbout(); return DONE;}
		return DOES_8G+DOES_STACKS+SUPPORTS_MASKING;
	} //setup
	

	
	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[])ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		int tgtRadius = 4;
		
		int newWidth = width;
		int newHeight = height;
		
		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		
		//first request target scale factor from user
		GenericDialog dialog = new GenericDialog("user input");
		dialog.addNumericField("scale factor: ", 1.0, 2);
		dialog.showDialog();
		
		if(dialog.wasCanceled()) {
			return;
		}
		
		double tgtScaleFactor = dialog.getNextNumber();
		if(tgtScaleFactor < 0.01 || tgtScaleFactor > 10) {
			return;
		}
		
		newWidth = (int)(width * tgtScaleFactor + 0.5);
		newHeight = (int)(height * tgtScaleFactor + 0.5);
		
		
		// variante A
		//double scaleFactorX = newWidth / (double)(width);
		//double scaleFactorY = newHeight / (double)(height);
		
		// variante B
		double scaleFactorX = (double)(newWidth - 1.0) / (double)(width - 1.0);
		double scaleFactorY = (double)(newHeight - 1.0) / (double)(height - 1.0);
		
		System.out.println("tgtScale: " + tgtScaleFactor + ", sX: " + scaleFactorX + ", sY: " + scaleFactorY);
		System.out.println("new width: " + newWidth + ", new height: " + newHeight);
		
		int[][] scaledImage = new int[newWidth][newHeight];
		
		for(int x = 0; x < newWidth; x++) {
			for (int y = 0 ; y < newHeight; y++) {
				double newX = (double)(x) / scaleFactorX;
				double newY = (double)(y) / scaleFactorY;
				
				int resultVal = GetNNinterpolatedValue(inDataArrInt, newX, newY, width, height);

				scaledImage[x][y] = resultVal;
				
			}
		}
		
		
        //
        //                  
        ImageJUtility.showNewImage(scaledImage, newWidth, newHeight, "scaled img");
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
	
	public int GetNNinterpolatedValue(int[][] inImg, double x, double y, int width, int height) {
		int xPos = (int) (x + 0.5);
		int yPos = (int) (y + 0.5);
		
		//safety check
		if(xPos >= 0 && xPos < width && yPos >= 0 && yPos < height) {
			return inImg[xPos][yPos];
		}
		return 0;
	}
	
} //class FilterTemplate_





import java.awt.Rectangle;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class ImageJUtility {

	public static int[][] convertFrom1DByteArr(byte[] pixels, int width, int height) {
		
		int[][] inArray2D = new int[width][height];
				
		int pixelIdx1D = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				inArray2D[x][y] = (int) pixels[pixelIdx1D];
				if (inArray2D[x][y] < 0) {
					inArray2D[x][y] += 256;
				} // if
				pixelIdx1D++;
			}
		}
		
		return inArray2D;		
	}	
	
	
	public static double[][] convertToDoubleArr2D(int[][] inArr, int width, int height) {
		double[][] returnArr = new double[width][height];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				returnArr[x][y] = inArr[x][y];
			}
		}
		
		return returnArr;
	}
	
	public static int[][] convertToIntArr2D(double[][] inArr, int width, int height) {
		int[][] returnArr = new int[width][height];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				returnArr[x][y] = (int)(inArr[x][y] + 0.5);
			}
		}
		
		return returnArr;
	}
	
	
	public static byte[] convertFrom2DIntArr(int[][] inArr, int width, int height) {
	  int pixelIdx1D = 0;
	  byte[] outArray2D = new byte[width * height];
	  
	  for (int y = 0; y < height; y++) {
		for (int x = 0; x < width; x++) {
			int resultVal = inArr[x][y];
			if (resultVal > 127) {
		      resultVal -= 256;
			}				
			outArray2D[pixelIdx1D] = (byte) resultVal;
			pixelIdx1D++;
			}
		}
	  
	  return outArray2D;
	}
	
	public static void showNewImage(int[][] inArr, int width, int height, String title) {
		byte[] byteArr = ImageJUtility.convertFrom2DIntArr(inArr, width, height);
		ImageJUtility.showNewImage(byteArr, width, height, title);
	}
	
	public static void showNewImage(double[][] inArr, int width, int height, String title) {
		int[][] intArr = ImageJUtility.convertToIntArr2D(inArr, width, height);
		byte[] byteArr = ImageJUtility.convertFrom2DIntArr(intArr, width, height);
		ImageJUtility.showNewImage(byteArr, width, height, title);
	}
	
	public static void showNewImage(byte[] inByteArr, int width, int height, String title) {
	  ImageProcessor outImgProc = new ByteProcessor(width, height);
	  outImgProc.setPixels(inByteArr);
	  
	  ImagePlus ip = new ImagePlus(title, outImgProc);
	  ip.show();
	}
	
	public static double[][] cropImage(double[][]inImg, int width, int height, Rectangle roi) {
		int roiWidth = roi.width;
		int roiHeight = roi.height;
		
		int roiXseed = roi.x;
		int roiYseed = roi.y;
		
		double[][] returnImg = new double[roiWidth][roiHeight];
		for(int xIdx = 0; xIdx < width; xIdx++) {
			for(int yIdx = 0; yIdx < height; yIdx++) {
				int origXIdx = xIdx + roiXseed;
				int origYIdx = yIdx + roiYseed;
				returnImg[xIdx][yIdx] = inImg[origXIdx][origYIdx];
			}
		}
		
		return returnImg;
	}
	
	public static int CONVERSION_MODE_RGB_GRAYSCALE_MEAN = 1;
	
	public static double[][] getGrayscaleImgFromRGB(ImageProcessor imgProc, int conversionMode) {
		int width = imgProc.getWidth();
		int height = imgProc.getHeight();
		int[] rgbArr = new int[3];
		double[][] returnImg = new double[imgProc.getWidth()][imgProc.getHeight()];
	    for(int xIdx = 0; xIdx < width; xIdx++) {
	    	for(int yIdx = 0; yIdx < height; yIdx++) {
		    	rgbArr = imgProc.getPixel(xIdx, yIdx, rgbArr);
		    	if(conversionMode == CONVERSION_MODE_RGB_GRAYSCALE_MEAN) {
		    		double meanVal = rgbArr[0] + rgbArr[1] + rgbArr[2];
			    	meanVal = meanVal / 3.0;
			    	returnImg[xIdx][yIdx] = meanVal;
		    	}		    	
		    }
	    }
		
		return returnImg;
	}
	
	public static int[][] calculateImgDifference(int[][] inImgA, int[][] inImgB, int width, int height) {
		int[][] returnImg = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				returnImg[x][y] = Math.abs(inImgA[x][y] - inImgB[x][y]);
			}
		}
		
		return returnImg;
	}
			
	
}

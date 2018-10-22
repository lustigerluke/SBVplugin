
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class HistogrammEqualization_BL_ implements PlugInFilter {

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

		final int MAXVAL = 255;

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		int[] tf2 = GetHistogramEqualizationTF2(MAXVAL, inDataArrInt, width, height);

		int[][] outDataArrInt2 = ImageTransformationFilter.GetTransformedImage(inDataArrInt, width, height, tf2);

		ImageJUtility.showNewImage(outDataArrInt2, width, height, "Equalized Image");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	public static int[] GetHistogramEqualizationTF2(int maxValue, int[][] inputImage, int width, int height) {

		int maxValueTF = maxValue - 0 + 1;
		int pixelCount = width * height;
		double probabilitySum = 0;
		
		int[] histogram = getHisto(inputImage,  width,  height,  maxValue);
		int[] transferFunction = new int[maxValue +1];
		
		for (int i = 0; i < histogram.length; i++) {
			probabilitySum+=((double)histogram[i])/pixelCount;
			double tmpSum = probabilitySum * maxValueTF + 0;
			transferFunction[i] = (int)(Math.floor(tmpSum));
			
			if(transferFunction[i]>maxValue) {
				transferFunction[i] = maxValue;
			}
		}
		
		return transferFunction;
		
		
	}

	public static int[] getHisto(int[][] inImg, int width, int height, int maxValue) {
		int[] histogram = new int[maxValue + 1];
		// step1: get histogram
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				histogram[inImg[x][y]]++;
			}
		}
		return histogram;
	}
} // class FilterTemplate_

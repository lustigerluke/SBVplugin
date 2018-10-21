
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class HisogrammEqualization_ implements PlugInFilter {

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
		
		ImageJUtility.showNewImage(outDataArrInt2, width, height, "TF2 on Blackboard");


	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	public static int[] GetHistogramEqualizationTF2(int maxValue, int[][] inputImage, int width, int height) {

		int[] tf = new int[maxValue +1];
		int expectedScale = width * height / 256;
		int tmpExpectedScale = expectedScale;
		int usedSum = 0;
		int tonalVal = 0;

		int[] histogram = getHisto(inputImage,  width,  height,  maxValue);

		//tf[0] = 0;
		for (int i = 0; i < histogram.length; i++) {
			usedSum += histogram[i];
			while (usedSum >= expectedScale) {
				tonalVal++;
				expectedScale = expectedScale + tmpExpectedScale;
			}
			tf[i] = tonalVal;
		}
		
		
		for (int i = 0; i < tf.length; i++) {
			if(tf[i] > 255) {tf[i] = 255;}
		}
		
		return tf;
	}
	
	public static int[] getHisto(int[][] inImg, int width, int height, int maxValue) {
		int[] histogram = new int[maxValue +1 ];
		// step1: get histogram
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				histogram[inImg[x][y]]++;
			}
		}
		return histogram;
	}
} // class FilterTemplate_

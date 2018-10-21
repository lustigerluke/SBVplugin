
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

import java.awt.*;

import ij.gui.GenericDialog;

public class HisogrammDoris_ implements PlugInFilter {

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
		int tgtRadius = 4;

		final int MAXVAL = 255;

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		int[] tf2 = GetHistogramEqualizationTF2(MAXVAL, inDataArrInt, width, height);
		int[][] outDataArrInt2 = ImageTransformationFilter.GetTransformedImage(inDataArrInt, width, height, tf2);
		ImageJUtility.showNewImage(outDataArrInt2, width, height, "TF2 on Blackboard");

				ImageJUtility.showNewImage(inDataArrInt, width, height, "mean with kernel r=" + tgtRadius);

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

	public static int[] GetHistogramEqualizationTF2(int maxVal, int[][] inImg, int width, int height) {

		int[] tf = new int[maxVal +1];
		int expectedValueSum = width * height / 256;
		int constExpectedVal = expectedValueSum;
		int usedSum = 0;
		int tonalVal = 0;
		int[] histogram = new int[maxVal +1 ];
		
		// step1: get histogram
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				histogram[inImg[x][y]]++;
			}
		}

		//tf[0] = 0;
		for (int i = 0; i < histogram.length; i++) {
			usedSum += histogram[i];
			while (usedSum >= expectedValueSum) {
				tonalVal++;
				expectedValueSum = expectedValueSum + constExpectedVal;
			}
			tf[i] = tonalVal;
		}
		return tf;
	}
} // class FilterTemplate_

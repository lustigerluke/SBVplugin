import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;
import java.awt.Rectangle;
import java.util.Arrays;

import com.sun.net.httpserver.Authenticator.Success;

public class HistogrammDehnung_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	public void run(ImageProcessor ip) {

		System.out.println("RUN: HistogrammDehnung");
		// convert to pixel array
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();
		int colorDepth = 256;

		int histo[] = new int[colorDepth]; // as many elements as possible gray values

		int[][] inArr = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		int[][] resultImage = inArr.clone();

		// step1: get histogram
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				histo[inArr[x][y]]++;
			}
		}

		// step2: get number of zeros in front and behind the data
		// hitso[] is assumed to look like: 0 0 0 3 512 234 150 4 0 0
		int i = 0;
		while (histo[i] == 0) {
			i++;
		}
		int zerosInFrontIndex = i;
		System.out.println("there are " + zerosInFrontIndex + " zeros in front of the data in histogram");
		i = colorDepth - 1;
		while (histo[i] == 0) {
			i--;
		}
		int zerosBehindIndex = i;
		System.out.println("there are " + zerosBehindIndex + " zeros behind the data in histogram");

		// generate a lookup table
		int dataLength = colorDepth - (colorDepth - zerosBehindIndex) - zerosInFrontIndex;
		double delta = colorDepth / dataLength;
		int[] lookUp = new int[colorDepth];

		for (int j = zerosInFrontIndex; j < zerosBehindIndex; j++) {
			int newIndex = (int) ((j -zerosInFrontIndex) * delta);
			lookUp[j] = newIndex;
			System.out.println(newIndex);
		}
		
		// run threw the resultImage (clone of inputImage) and set every Pixel depending on lookup table
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				resultImage[x][y] = lookUp[resultImage[x][y]];
			}
		}

		System.out.println("inputImg: width: " + width + ", heigth: " + height + ", surface: " + width * height);
		System.out.println("Now show the result image!");
		ImageJUtility.showNewImage(resultImage, width, height, "HistogramResult");
		System.out.println("SUCCESS: HistogrammDehnung DONE.");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

} // class FilterTemplate_

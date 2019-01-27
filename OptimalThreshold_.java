
import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;

public class OptimalThreshold_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	public int[][] performOptimalThresh(int[][] inImg, int width, int height, int BG_VAL, int FG_VAL,
			double initialThresh, double DELTA_VAL) {
		int[][] resultImg = new int[width][height];
		double sumThresh01 = 0;
		double sumThresh02 = 0;
		int countThresh01 = 0;
		int countThresh02 = 0;
		double meanThresh01 = 0;
		double meanThresh02 = 0;
		double intermediateThresh = 0;
		
		int loopCount = 0;

		// calculate intermediate threshold value and check

		while (true) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int currVal = inImg[x][y];

					if (currVal < initialThresh) {
						sumThresh01 += currVal;
						countThresh01++;

					} else {

						sumThresh02 += currVal;
						countThresh02++;
					}

				}

			}

			// calculate mean
			meanThresh01 = (sumThresh01 / countThresh01);
			meanThresh02 = (sumThresh02 / countThresh02);

			//calculate intermediate threshold
			intermediateThresh = (meanThresh01 + meanThresh02) / 2;
			loopCount++;
			System.out.println("intermediate thresh= "+intermediateThresh+"; Iteration= "+loopCount);

			if (Math.abs((initialThresh-intermediateThresh))>DELTA_VAL) {
				initialThresh = intermediateThresh;
			} else {
				break;
			}

		}

		// calculate result image
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int currVal = inImg[x][y];

				if (currVal < initialThresh) {
					resultImg[x][y] = BG_VAL;

				} else {
					resultImg[x][y] = FG_VAL;
				}

			}

		}

		return resultImg;
	}

	public void run(ImageProcessor ip) {
		byte[] pixels = (byte[]) ip.getPixels();
		int width = ip.getWidth();
		int height = ip.getHeight();

		// user input
		double initialThresh = 255 / 2;

		// constants
		int BG_VAL = 0;
		int FG_VAL = 255;
		double DELTA_VAL = 0.01;

		GenericDialog gd = new GenericDialog("thresh params");
		gd.addNumericField("Initial Threshold Value: ", initialThresh, 0);
		gd.showDialog();

		if (!gd.wasCanceled()) {
			initialThresh = (int) gd.getNextNumber();
		}

		System.out.println("initial Threshold Value = " + initialThresh);

		int[][] inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);
		int[][] resultImg = performOptimalThresh(inDataArrInt, width, height, BG_VAL, FG_VAL, initialThresh, DELTA_VAL);

		// inDataArrInt = ImageJUtility.convertFrom1DByteArr(pixels, width, height);

		ImageJUtility.showNewImage(resultImg, width, height, "threshold image");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout

} // class FilterTemplate_

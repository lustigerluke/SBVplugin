import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import ij.gui.GenericDialog;


public class FiltereffektEvaluierung_ implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}
		return DOES_8G + DOES_STACKS + SUPPORTS_MASKING;
	} // setup

	
	public void run(ImageProcessor ip) {

		System.out.println("RUN: Time Evaluation");
		// convert to pixel array
		int width = ip.getWidth();
		int height = ip.getHeight();		
		int tgtRadius = 4; // default value
		int sigma = 4;
		
		double[][] resultImage = new double[width][height];
		int [] iterations = {1,2,3,4,5};

		System.out.println("Please Input the radius of the mask for all the filters.");
		tgtRadius = getUserInput(tgtRadius, "radius");
		System.out.println("Please type a proper sigma value.");
		sigma = getUserInput(sigma, "sigma");
		
		// ---------- MEAN ----------
		long startTime = System.nanoTime();
		for (int j = 0; j < iterations.length; j++) {
			System.out.println("Run Mean Filter " + iterations[j] + " times.");
			startTime = System.nanoTime();
			for (int i = 0; i < iterations[j]; i++) {
				resultImage = Mean_.runFilter(ip, tgtRadius);  // for time measurement the input image is not important
			}
			System.out.println("Took: " + (System.nanoTime() - startTime) + " nanoseconds.");
		}

		// ---------- GAUSS ----------

		for (int j = 0; j < iterations.length; j++) {
			System.out.println("Run Gauss Filter " + iterations[j] + " times.");
			startTime = System.nanoTime();
			for (int i = 0; i < iterations[j]; i++) {
				resultImage = Gauss_.runFilter(ip, tgtRadius, sigma);  // for time measurement the input image is not important
			}
			System.out.println("Took: " + (System.nanoTime() - startTime) + " nanoseconds.");
		}
		
		// ---------- MEDIAN ----------
		for (int j = 0; j < iterations.length; j++) {
			System.out.println("Run Median Filter " + iterations[j] + " times.");
			startTime = System.nanoTime();
			for (int i = 0; i < iterations[j]; i++) {
				resultImage = Median_.runFilter(ip, tgtRadius);  // for time measurement the input image is not important
			}
			System.out.println("Took: " + (System.nanoTime() - startTime) + " nanoseconds.");
		}
		
		//ImageJUtility.showNewImage(resultImage, width, height, "mean with kernel");
		System.out.println("SUCCESS:  Time Evaluation: DONE.");

	} // run

	void showAbout() {
		IJ.showMessage("About Template_...", "this is a PluginFilter template\n");
	} // showAbout



	/**
	 * Asks the user to input.
	 * 
	 * @return value from user input. 0 if failed.
	 */
	public static int getUserInput(int defaultValue, String nameOfValue) {
		// user input
		System.out.print("Read user input: " + nameOfValue);
		GenericDialog gd = new GenericDialog("user input:");
		gd.addNumericField("defaultValue", defaultValue, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return 0;
		}
		int radius = (int) gd.getNextNumber();
		System.out.println(radius);
		return radius;
	}

} // class FilterTemplate_

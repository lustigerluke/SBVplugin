
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;
import ij.gui.GenericDialog;

public class Register_ implements PlugInFilter {
  
	boolean nnFlag = true;
	
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
                          
        // define transform
        double transX = getUserInput(0,"deltaX");
        double transY = getUserInput(0,"deltaY");
        double rotAngle = getUserInput(0,"rotation");
        
        int[][] transformedImg = transformImage(inDataArrInt, width, height, transX, transY, rotAngle);
        
        ImageJUtility.showNewImage(transformedImg, width, height, "transformed image");
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
	public static int getUserInput(int defaultValue, String nameOfValue) {
		// user input
		System.out.print("Read user input: " + nameOfValue);
		GenericDialog gd = new GenericDialog(nameOfValue);
		gd.addNumericField("please input " + nameOfValue + ": ", defaultValue, 0);
		gd.showDialog();
		if (gd.wasCanceled()) {
			return 0;
		}
		int radius = (int) gd.getNextNumber();
		System.out.println(radius);
		return radius;
	}
	
	public double GetBilinearinterpolatedValue(int[][] inImg, double x, double y, int width, int height) {
		// calculate the delta for x and y
		double deltaX = x - Math.floor(x);
		double deltaY = y - Math.floor(y);

		// set calculation fregment
		int xPlus1 = (int) x + 1;
		int yPlus1 = (int) y + 1;

		// handling of image edge for x
		if (x + 1 >= width) {
			xPlus1 = (int) x;
		}

		// handling of image edge for y
		if (y + 1 >= height) {
			yPlus1 = (int) y;
		}

		// get 4 neighboring pixels
		int neighbor1 = inImg[xPlus1][(int) (y)];
		int neighbor2 = inImg[(int) (x)][yPlus1];
		int neighbor3 = inImg[xPlus1][yPlus1];
		int neighbor4 = inImg[(int) (x)][(int) (y)];

		// calculate weighted mean out of neighbors
		double weightedMean = ((1 - deltaX) * (1 - deltaY) * neighbor4) + (deltaX * (1 - deltaY) * neighbor1)
				+ ((1 - deltaX) * deltaY * neighbor2) + (deltaX * deltaY * neighbor3);

		return weightedMean;
	}
	
	public int[][] transformImage(int[][] inImg,int width, int height, double transX, double transY, double rotAngle) {
		
		//allocate result image
		int[][] resultImg = new int[width][height];
		
		// prepare cos theta, sin theta
		double cosTheta = Math.cos(Math.toRadians(-rotAngle));
		double sinTheta = Math.sin(Math.toRadians(-rotAngle)); // - weil backgroundmapping
		
		double widthHalf = width / 2.0;
		double heightHalf = height / 2.0;
		
		
		//1) interate over all pixels and calc value utilizing backward-mapping
		for( int x= 0; x < width; x++) {
			for (int y  =0; y< height; y++) {
				
				double tmpposX = x - widthHalf;
				double tmpposY = y - heightHalf;
				
				//3) rotate
				double posX = tmpposX * cosTheta + tmpposY * sinTheta;
				double posY = - tmpposX * sinTheta + tmpposY * cosTheta;
				
				//4) translate
				posX -= transX;
				posY -= transY;
				

				// move origin back from center to top corner
				posX = posX + widthHalf;
				posY = posY + heightHalf;
				
				//6) get interpolated value if flag is true
				if (nnFlag) {
					int nnX = (int) (posX + 0.5);
					int nnY = (int) (posY + 0.5);
					
					//6) assigne value from original imag inImg if inside the image boundaries
					if(nnX >= 0 && nnX <width && nnY >= 0 && nnY < height) {
						resultImg[x][y] = inImg[nnX][nnY];
					}
				}
				else {
					// if not nearest neighbor, do bilinear interpolation
					double resultVal = GetBilinearInterpolatedValue(inDataArrInt, posX, posY, width, height);
					
					//set new rounded value for current location
					resultImg[x][y] = (int) (resultVal + 0.5);
					
				}
			

			}
		}
		return resultImg;
	}
	
	
} //class FilterTemplate_


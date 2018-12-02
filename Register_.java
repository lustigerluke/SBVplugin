
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
				

				// move orifin back from center to top corner
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
					
					
				}
			

			}
		}
		return resultImg;
	}
	
	
} //class FilterTemplate_


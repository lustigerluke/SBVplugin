
import ij.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.*;
import java.awt.*;

public class AutoRegister_ implements PlugInFilter {
  	
	
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
        
		int widthHalf = (int)(width / 2.0);
		double[][] tmpImage = ImageJUtility.convertToDoubleArr2D(inDataArrInt, width, height);
		Rectangle roi = new Rectangle(0, 0, widthHalf, height);
		double[][] Img1 = ImageJUtility.cropImage(tmpImage, roi.width, roi.height, roi);
		ImageJUtility.showNewImage(Img1, widthHalf, height, "first half image");
		roi = new Rectangle(widthHalf, 0, widthHalf, height);
		double[][] Img2 = ImageJUtility.cropImage(tmpImage, roi.width, roi.height, roi);
		ImageJUtility.showNewImage(Img2, widthHalf, height, "second half image");
        
		int[][] intImg1 = ImageJUtility.convertToIntArr2D(Img1, widthHalf, height);
		int[][] intImg2 = ImageJUtility.convertToIntArr2D(Img2, widthHalf, height);
		
		
        // define transform
        double transX = 30.131;
        double transY = -59.999;
        double rotAngle = 0.746;
        
        int xRadius = 50;
        int yRadius = 40;
        int rotRadius = 10;
        
        int[][] transformedImg;
        int[][] diffImg;
        int[][][] ssE = new int[2*xRadius + 1][2*yRadius + 1][2*rotRadius + 1];
        for(int x = - xRadius; x < xRadius; x++) {
        	for( int y = -yRadius; y < yRadius; y++) {
        		for (int angle = - rotRadius; angle < rotRadius; angle ++) {
        			transformedImg = transformImage(intImg1, widthHalf, height, x, y, angle);
        			diffImg = ImageJUtility.calculateImgDifference(intImg1, intImg2, widthHalf, height);
        			ssE[x+xRadius][y+yRadius][angle+rotRadius] = calculateSSE(diffImg, widthHalf, height);
        		}
        	}
        }
        
        System.out.println("Done");
        //ImageJUtility.showNewImage(transformedImg, width, height, "transformed image");
                        
	} //run

	void showAbout() {
		IJ.showMessage("About Template_...",
			"this is a PluginFilter template\n");
	} //showAbout
	
	
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
				
				//6) get interpolated value
				// TODO bilinear interpolation
				int nnX = (int) (posX + 0.5);
				int nnY = (int) (posY + 0.5);
				
				//6) assigne value from original imag inImg if inside the image boundaries
				if(nnX >= 0 && nnX <width && nnY >= 0 && nnY < height) {
					resultImg[x][y] = inImg[nnX][nnY];
				}
			}
		}
		return resultImg;
	}
	
	public int calculateSSE(int[][] diffImg, int width, int height) {
		int sse = 0;
		
		for (int x = 0; x < width; x ++) {
			for (int y = 0; y < height; y++ ) {
				sse = sse + diffImg[x][y];
			}
		}
		
		return sse;
	}


} //class FilterTemplate_

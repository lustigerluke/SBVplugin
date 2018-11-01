
public class ConvolutionFilter {

	public static double[][] ConvolveDoubleNorm(double[][] inputImg, int width, int height, double[][] kernel, int radius, int numOfIterations) {
	  double[][] returnImg = inputImg;
	  for(int iterCount = 0; iterCount < numOfIterations; iterCount++) {
		  returnImg = ConvolutionFilter.ConvolveDoubleNorm(returnImg, width, height, kernel, radius);
	  }
	  
	  return returnImg;
	}
	
	public static double[][] ConvolveDoubleNorm(double[][] inputImg, int width, int height, double[][] kernel, int radius) {
		double[][] returnImg = new double[width][height];
		
		//step1: move mask to all possible image pixel positions
		for( int x = 0; x < width; x++) {
			for( int y = 0; y < height; y++) {
				
				double totalSum = 0.0;
				double maskCount = 0.0;
				//step2: interate over all mask elements
				for(int xOffset = -radius; xOffset <= radius ; xOffset++) {
					for(int yOffset = -radius ; yOffset <= radius ; yOffset++) {
						int nbX = x + xOffset;
						int nbY = y + yOffset;
						
						
						// step3: check range of coordinates in convolution mask
						if(nbX >= 0 && nbX < width && nbY >= 0 && nbY < height) {
							totalSum += inputImg[nbX][nbY] * kernel[xOffset + radius][yOffset + radius];
							maskCount += kernel[xOffset + radius][yOffset + radius];
						}
						
					}
				}
				//step3.5 normalize
				totalSum /= maskCount;
				
				//step4: store result in output image
				returnImg[x][y] = totalSum;
			} // y loop
		} // x loop
		
		return returnImg;
	}
	
	public static double[][] ConvolveDouble(double[][] inputImg, int width, int height, double[][] kernel, int radius) {
		double[][] returnImg = new double[width][height];
		
		//step1: move mask to all possible image pixel positions
		for( int x = 0; x < width; x++) {
			for( int y = 0; y < height; y++) {
				
				double totalSum = 0.0;
				//step2: interate over all mask elements
				for(int xOffset = -radius; xOffset <= radius ; xOffset++) {
					for(int yOffset = -radius ; yOffset <= radius ; yOffset++) {
						int nbX = x + xOffset;
						int nbY = y + yOffset;
						
						
						// step3: check range of coordinates in convolution mask
						if(nbX >= 0 && nbX < width && nbY >= 0 && nbY < height) {
							totalSum += inputImg[nbX][nbY] * kernel[xOffset + radius][yOffset + radius];
						}
						
					}
				}
				
				//step4: store result in output image
				returnImg[x][y] = totalSum;
			} // y loop
		} // x loop
				
		return returnImg;
	} // ConvolveDouble end
	
	public static double[][] GetMeanMask(int tgtRadius) {
		int size = 2 * tgtRadius + 1;
		
		int numOfElements = size * size;
		double maskVal = 1.0 / numOfElements;
		double[][] kernelImg = new double[size][size];
		
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				kernelImg[i][j] = maskVal;
			}
		}
				
		return kernelImg;
	}
	
    public static double[][] GetGaussMask(int tgtRadius, double sigma) {
    	int size = 2 * tgtRadius + 1;
    	
    	double constant = 1 / (Math.PI *2* sigma*sigma);
    	
    	double[][] kernelImg = new double[size][size];
		
		for(int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				double diffI = i - size/2;
				double diffJ = j - size/2;
				
				kernelImg[i][j] = constant * Math.exp(-( diffI*diffI + diffJ*diffJ ) / (2*sigma*sigma));
			}
		}
						
		return kernelImg;
	}
    
   
    
    public static double[][] ApplySobelEdgeDetection(double[][] inputImg, int width, int height) {
    	double[][] returnImg = new double[width][height];
    	double[][] sobelV = new double[][]{{1.0, 0.0, -1.0}, {2.0, 0.0, -2.0}, {1.0, 0.0, -1.0}};
		double[][] sobelH = new double[][]{{1.0, 2.0, 1.0}, {0.0, 0.0, 0.0}, {-1.0, -2.0, -1.0}};
    	
		int radius = 1;
		double maxGradient = 1.0;
		
		// achtung! hier keine Normierung
		double[][] resultSobelV = ConvolveDouble(inputImg,width,height,sobelV,radius);
		double[][] resultSobelH = ConvolveDouble(inputImg,width,height,sobelH,radius);
		
		for( int x = 0; x < width; x++) {
			for( int y = 0; y < height; y++) {
				double vAbs = Math.abs(resultSobelV[x][y]);
				double hAbs = Math.abs(resultSobelH[x][y]);
				double resVal = vAbs + hAbs;
				returnImg[x][y] = resVal;
				
				// new max gradient?
				if(resVal >maxGradient) maxGradient = resVal;
			}
		}
		
		//finally normalize by max gradient value
		double corrFactor = maxGradient/255.0;
		
		for(int x = 0; x < width; x++) {
			for ( int y = 0; y < height; y++) {
				returnImg[x][y] /= corrFactor;
			}
		}
	     						
		return returnImg;
    }
	
	
}

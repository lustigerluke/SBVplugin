
public class ImageTransformationFilter {

	
	public static int[][] GetTransformedImage(int[][] inImg, int width, int height, int[] transferFunction) {
		int[][] returnImg = new int[width][height];
		
		//interate over all pixels
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int actVal = inImg[x][y];
				returnImg[x][y] = transferFunction[actVal];
			}
		}
		
		return returnImg;
	}
	
	public static int[] GetInversionTF(int maxVal) {
		int[] transferFunction = new int[maxVal + 1];
		
		for(int i = 0; i <= maxVal; i++) {
			transferFunction[i] = maxVal - i;
		}
				
		return transferFunction;
	}
	
	public static int[] GetHistogram(int maxVal, int[][] inImg, int width, int height) {
		int[] histogram = new int[maxVal + 1];
		
//		 int[] histogramArr = GetHistogram();
//		 250000 pixel ==> erwartungswert je histogram-POS: ~1000
//		 int bIdx = 0;
//		 int bSum = 1000;
//		 
//		 int cumulatedSum = histogramArr[0];
//		 returnTF[0] = 0; //weil schwarz schwarz bleiben soll
//		 for(int i = 0; i <= 255; i++){
//		 		cumulatedSum += histogramArr[i];
//				while( cumulatedSum > bSum){
//					bIdx++;
//					bSum += 1000; // with * height / 256
//				}
//				retrunTF[i] = bIdx;
//		 }
		return histogram;
	}
	
	public static int[] GetGammaCorrTF(int maxVal, double gamma) {
		int[] transferFunction = new int[maxVal + 1];
		
		return transferFunction;
	}
	
	public static int[] GetBinaryThresholdTF(int maxVal, int Tmin, int Tmax, int FG_VAL, int BG_VAL) {
		int[] transferFunction = new int[maxVal + 1];
		
		for(int i = 0; i < maxVal; i++) {
			if(i>=Tmin && i<= Tmax) {
				transferFunction[i] = FG_VAL;
			}else {
				transferFunction[i] = BG_VAL;
			}
		}
				
		return transferFunction;
	}
	
	public static int[] GetHistogramEqualizationTF(int maxVal, int[][] inImg, int width, int height) {
		int[] returnTF = new int[maxVal + 1];
		
		return returnTF;
	}
	
}

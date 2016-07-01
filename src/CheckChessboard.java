
import org.opencv.core.*;

public class CheckChessboard {
	
/**
 * Does a fast check if a chessboard is in the input image. This is a workaround to 
 * a problem of findChessboardCorners being slow on images with no chessboard. 
 * @param image input image
 * @param patternSize chessboard size
 * @return 1 if a chessboard can be in this image and findChessboardCorners should be called, 
 * and 0 if there is no chessboard; -1 in case of error. 
 */
	
	public static int checkChessboard(Mat image, Size patternSize) {
		
		Mat corners_mat = image;
		int retVal = checkChessboard_0(corners_mat.nativeObj, patternSize.width, patternSize.height);
		
		return retVal;
	}
	
	private static native int checkChessboard_0(long image_nativeObj, double patternSize_width, double patternSize_height);

}

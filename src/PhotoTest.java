import java.util.Scanner;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

class DetectCornersDemo {
	
  public void run() {
    Scanner input = new Scanner(System.in);
    System.out.println("**The image used must be a .png image and must have a visible 8x8 chessboard in it!**\nEnter input image filepath:");
    String filepath = input.nextLine();
    Mat image = Highgui.imread(filepath);

    Size patternSize = new Size(8, 7);
    
    MatOfPoint2f corners = new MatOfPoint2f();
	boolean cornersFound = Calib3d.findChessboardCorners(image, patternSize, corners);
	Calib3d.drawChessboardCorners(image, patternSize, corners, cornersFound);
	System.out.println(corners.dump());
	

    // Save the visualized detection.
    String filename = "CornerOutput.png";
    System.out.println(String.format("Writing %s", filename));
    Highgui.imwrite(filename, image);
  }
}

public class PhotoTest {
  public static void main(String[] args) {
    System.out.println("Hello, OpenCV");
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    (new DetectCornersDemo()).run();
  }
}

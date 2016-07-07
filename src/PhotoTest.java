import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

class DetectCornersDemo {

	public void run() {
		Scanner input = new Scanner(System.in);
		System.out.println("**The image used must be a .png image and must have a visible 8x8 chessboard in it!**\nEnter input image filepath:");
		String filepath = input.nextLine();
		Mat image = Highgui.imread(filepath);
		input.close();

		Size patternSize = new Size(8, 7);

		MatOfPoint2f corners = new MatOfPoint2f();
		boolean cornersFound = Calib3d.findChessboardCorners(image, patternSize, corners);
		Calib3d.drawChessboardCorners(image, patternSize, corners, cornersFound);
		// System.out.println(corners.dump());
		Size imageSize = new Size(image.width(), image.height());
		List<Mat> objectPoints = new ArrayList<Mat>();
		List<Mat> imagePoints = new ArrayList<Mat>();
		imagePoints.add(corners);
		List<Point3> points = new ArrayList<Point3>();
		for (int y = 0; y <= 6; y++) {
			for (int x = 0; x <= 7; x++) {
				points.add(new Point3(x, y, 0));
			}
		}
		MatOfPoint3f coords = new MatOfPoint3f();
		coords.fromList(points);
		objectPoints.add(coords);
		Mat distCoeffs = new Mat(), cameraMatrix = new Mat();
		List<Mat> rvecs = new ArrayList<Mat>(), tvecs = new ArrayList<Mat>();
		Calib3d.calibrateCamera(objectPoints, imagePoints, imageSize, cameraMatrix, distCoeffs, rvecs, tvecs);
		MatOfDouble distCoeff = new MatOfDouble(distCoeffs);
		Mat rvec = new Mat(), tvec = new Mat();
		// Calib3d.solvePnP(coords, corners, cameraMatrix, distCoeff, rvec, tvec);
		Calib3d.solvePnPRansac(coords, corners, cameraMatrix, distCoeff, rvec, tvec);
		System.out.println("Camera Matrix:\n" + cameraMatrix.dump());
		System.out.println("Distortion Coefficients:\n" + distCoeffs.dump());
		System.out.println("Rotation Vector:\n" + rvec.dump());
		System.out.println("Translation Vector:\n" + tvec.dump());

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

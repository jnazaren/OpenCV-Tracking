import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import org.opencv.highgui.VideoCapture;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

public class ChessboardOriginal {

	public static void main(String[] args) throws IOException {

		System.out.println("Hello, OpenCV");

		System.out.println("Loading library...");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture(0);
		camera.open(0);
		camera.set(3, 1280);
		camera.set(4, 720);
		if (!camera.isOpened()) {
			System.out.println("Camera Error");
		} else {
			System.out.println("Camera OK");
		}
		
		NewVideoCamera cam = new NewVideoCamera(camera);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(cam);
		frame.setSize(1300, 770);
		frame.setTitle("Chessboard");
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.out.println("Closing...");
				camera.release();
				System.exit(0);
			}

			public void windowClosed(WindowEvent e) {
			}

		});
		
		System.out.println("Displaying feed... ");
		
		while (camera.isOpened()) {
			
			cam.repaint();

		}
	}
}

@SuppressWarnings("serial")
class NewVideoCamera extends JPanel {
	VideoCapture camera;

	public NewVideoCamera(VideoCapture cam) {
		camera = cam;
	}

	public BufferedImage Mat2BufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage img = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return img;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Mat frame = new Mat();
		camera.read(frame);
		Size patternSize = new Size(8, 7);

		if (!frame.empty()) {

			// all processing of image occurs here

			MatOfPoint2f corners = new MatOfPoint2f();
			boolean cornersFound = Calib3d.findChessboardCorners(frame, patternSize, corners, 4);
			Calib3d.drawChessboardCorners(frame, patternSize, corners, cornersFound);
			if (cornersFound) {
//				System.out.println(corners.dump());
//				System.out.println("-------------------------");
				List<Point3> points = new ArrayList<Point3>();
				for (int y = 0; y <= 6; y++) {
					for (int x = 0; x <= 7; x++) {
						points.add(new Point3(x, y, 0));
					}
				}
				MatOfPoint3f imageCoords = new MatOfPoint3f();
				imageCoords.fromList(points);
				Mat cameraMatrix = new Mat(3, 3, 6);
				cameraMatrix.put(0, 0, 1206.465, 0, 635.515);
				cameraMatrix.put(1, 0, 0, 1130.215, 348.959);
				cameraMatrix.put(2, 0, 0, 0, 1);
				Mat rvec = new Mat(), tvec = new Mat();
				MatOfDouble distCoeffs = new MatOfDouble(-0.920, 0.994, 0.133, 0.252, -7.689);
//				Calib3d.solvePnP(imageCoords, corners, cameraMatrix, distCoeffs, rvec, tvec);
				Calib3d.solvePnPRansac(imageCoords, corners, cameraMatrix, distCoeffs, rvec, tvec);
				System.out.println("Rotation Vector:\n" + rvec.dump());
				System.out.println("Translation Vector:\n" + tvec.dump());
				MatOfPoint2f projectedAxisPts = new MatOfPoint2f();
				Calib3d.projectPoints((new MatOfPoint3f(new Point3(3,0,0), new Point3(0,3,0), new Point3(0,0,-3))), rvec, tvec, cameraMatrix, distCoeffs, projectedAxisPts);
				drawAxes(frame, corners, projectedAxisPts);
			}

			// processing finished
			
			BufferedImage image = Mat2BufferedImage(frame);
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

		}
	}
	
	public void drawAxes (Mat img, Mat corners, MatOfPoint2f projectedPoints) {
		Point corner = new Point(corners.get(0, 0));
		Core.line(img, corner, new Point(projectedPoints.get(0, 0)), new Scalar(0, 0, 255), 5);  // x-axis
		Core.line(img, corner, new Point(projectedPoints.get(1, 0)), new Scalar(0, 255, 0), 5);  // y-axis
		Core.line(img, corner, new Point(projectedPoints.get(2, 0)), new Scalar(255, 0, 0), 5);  // z-axis
	}
}

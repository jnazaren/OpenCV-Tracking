import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Chessboard {
	static ExecutorService pool = Executors.newFixedThreadPool(1);
	static Queue<Future<Mat>> frameQueue = new ConcurrentLinkedQueue<Future<Mat>>();

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

		VideoCamera cam = new VideoCamera(camera, frameQueue);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(cam);
		frame.setSize(1280, 720);
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

		System.out.println("Displaying feed...");

		// May need to use SwingWorker for faster processing in the future

		/*
		 * 
		 * SwingWorker<Void, Mat> worker = new SwingWorker<Void, Mat>() {
		 * 
		 * @Override protected Void doInBackground() throws Exception {
		 * while(!isCancelled()) { Mat iframe = new Mat(); if
		 * (camera.read(iframe)) { publish(iframe); } Thread.sleep(500); //
		 * prudential time to avoid block the event queue } return null; }
		 * 
		 * @Override protected void process(List<Mat> chunks) { Mat lastFrame =
		 * chuncks.get(chunks.size() - 1); Highgui.imwrite(canvas, lastFrame); }
		 * };
		 * 
		 * worker.execute();
		 */
		// Lambda Runnable
		Runnable captureFrame = () -> {
			while (true) {
				Callable<Mat> imageProcessor = new ImageProcessor(camera);
				Future<Mat> future = pool.submit(imageProcessor);
				frameQueue.add(future);
				cam.repaint();
				try {
					Thread.sleep(30);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		};

		new Thread(captureFrame).start();
	}

}

@SuppressWarnings("serial")
class VideoCamera extends JPanel {

	VideoCapture camera;
	Queue<Future<Mat>> frameQueue;

	public VideoCamera(VideoCapture cam, Queue<Future<Mat>> frameQueue) {
		camera = cam;
		this.frameQueue = frameQueue;
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

		try {
			System.out.println("size: " + frameQueue.size());
			Future<Mat> ftask = frameQueue.poll();
			Mat frame = null;
			if (ftask != null && (frame = ftask.get()) != null) {
				BufferedImage image = Mat2BufferedImage(frame);
				g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			} else {
				Thread.sleep(10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class ImageProcessor implements Callable<Mat> {

	VideoCapture camera;
	static boolean cornersFound = false;
	static long callsMade = 0;

	public ImageProcessor(VideoCapture camera) {
		this.camera = camera;
	}

	public Mat call() {

		Mat frame = new Mat();
		camera.read(frame);
		Size patternSize = new Size(7, 7);

		if (!frame.empty()) {

			// all processing of image occurs here

			MatOfPoint2f corners = new MatOfPoint2f();
			cornersFound = Calib3d.findChessboardCorners(frame, patternSize, corners, 4);
			Calib3d.drawChessboardCorners(frame, patternSize, corners, cornersFound);
			if (cornersFound) {
				System.out.println(corners.dump());
				System.out.println("-------------------------");
			}

			// processing finished

			return frame;

		} else {
			return null;
		}
	}

}

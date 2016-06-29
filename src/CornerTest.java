import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;

public class CornerTest {

	public static void main(String[] args) {
		System.out.println("Hello, OpenCV");

		// Load the native library.
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

		VideoSlider cam = new VideoSlider("Threshold Value", camera);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(cam);
		frame.setSize(1295, 850);
		frame.setVisible(true);
		frame.setTitle("Corner Detection");
		frame.addWindowListener(new WindowAdapter() {
		    
		    public void windowClosing(WindowEvent e) {
		    	System.out.println("Closing...");
				camera.release();
				System.exit(0);
		    }

			public void windowClosed(WindowEvent e) {}

		});
		
		System.out.println("Displaying feed...");
		
		while (camera.isOpened()) {
			cam.repaint();
		}
	}

}
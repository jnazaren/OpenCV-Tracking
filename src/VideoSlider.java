import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

@SuppressWarnings("serial")
public class VideoSlider extends JPanel implements ChangeListener {  // implements WindowListener, ActionListener
	
	 //Set up animation parameters.
    static final int THRESH_MIN = 0;
    static final int THRESH_MAX = 100;
    static final int THRESH_INIT = 50; 
    int delay;
    Timer timer;
    public double thesholdValue = 0.0005;
    
    static Scanner input = new Scanner(System.in);
	VideoCapture camera;
 
    public VideoSlider(String title, VideoCapture cam) {
    	camera = cam;
    	
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
 
        delay = 1000;
 
        //Create the label.
        JLabel sliderLabel = new JLabel(title, JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        //Create the slider.
        JSlider threshold = new JSlider(JSlider.HORIZONTAL, THRESH_MIN, THRESH_MAX, THRESH_INIT);
        threshold.addChangeListener(this);
 
        //Turn on labels at major tick marks.
 
        threshold.setMajorTickSpacing(10);
        threshold.setMinorTickSpacing(1);
        threshold.setPaintTicks(true);
        threshold.setPaintLabels(true);
        threshold.setBorder(
        BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.PLAIN, 15);
        threshold.setFont(font);
 
        //Put everything together.
        add(sliderLabel);
        add(threshold);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
 
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            this.thesholdValue = source.getValue()/100000.0;
        }
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
		Mat mat = new Mat();

		camera.read(mat);
		
		if (!mat.empty()) {
			
			// all processing of the image (mat) occurs here!
			
			Mat greyscale = turnGray(mat), dst = new Mat();
			
			int blockSize = 2;
			int ksize = 3;
			double k = 0.15;
			
			Imgproc.cornerHarris(greyscale, dst, blockSize, ksize, k);

			for (int i = 0; i < mat.width(); i++) {
				for (int j = 0; j < mat.height(); j++) {
					if (dst.get(j, i)[0] > this.thesholdValue) {
						Point pt = new Point(i, j);
						Core.circle(mat, pt, mat.height() / 150, new Scalar(0, 0, 255), mat.height() / 300);
					}
				}
			}

			// processing finished

			BufferedImage image = Mat2BufferedImage(mat);
			g.drawImage(image, 0, 90, image.getWidth(), image.getHeight(), null);
			
		}
			
	}

	public Mat turnGray(Mat img) {
		Mat mat1 = new Mat();
		Imgproc.cvtColor(img, mat1, Imgproc.COLOR_RGBA2GRAY);
		return mat1;
	}

	public Mat thresh(Mat img) {
		Mat threshed = new Mat();
		int SENSITIVITY_VALUE = 100;
		Imgproc.threshold(img, threshed, SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);
		return threshed;
	}

}

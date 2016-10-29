import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint3f;
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
    public double hlValue = 0.0;
    public double slValue = 0.0;
    public double vlValue = 0.0;
    public double huValue = 180.0;
    public double suValue = 255.0;
    public double vuValue = 255.0;
    
    static Scanner input = new Scanner(System.in);
	VideoCapture camera;
 
    public VideoSlider(String title, VideoCapture cam) {
    	camera = cam;
    	
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
 
        delay = 1000;
 
        //Create the threshold label.
        JLabel thresholdLabel = new JLabel(title, JLabel.CENTER);
        thresholdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        //Create the threshold slider.
        JSlider threshold = new JSlider(JSlider.HORIZONTAL, THRESH_MIN, THRESH_MAX, THRESH_INIT);
        threshold.addChangeListener(this);
        threshold.setName("threshold");
        
        // HSV labels
        
        Font newfont = new Font("Serif", Font.BOLD, 20);
        JLabel hlLabel = new JLabel("Lower Hue Bound", JLabel.CENTER);
        hlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hlLabel.setFont(newfont);
        JLabel slLabel = new JLabel("Lower Saturation Bound", JLabel.CENTER);
        slLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        slLabel.setFont(newfont);
        JLabel vlLabel = new JLabel("Lower Value Bound", JLabel.CENTER);
        vlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vlLabel.setFont(newfont);
        JLabel huLabel = new JLabel("Upper Hue Bound", JLabel.CENTER);
        huLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        huLabel.setFont(newfont);
        JLabel suLabel = new JLabel("Upper Saturation Bound", JLabel.CENTER);
        suLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        suLabel.setFont(newfont);
        JLabel vuLabel = new JLabel("Upper Value Bound", JLabel.CENTER);
        vuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        vuLabel.setFont(newfont);
        
        // HSV Bound sliders
        
        ArrayList<JSlider> colorSliders = new ArrayList<JSlider>();
        JSlider hl = new JSlider(JSlider.HORIZONTAL, 0, 180, 0);
        colorSliders.add(hl);
        hl.setName("hl");
        JSlider sl = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        colorSliders.add(sl);
        sl.setName("sl");
        JSlider vl = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        colorSliders.add(vl);
        vl.setName("sl");
        JSlider hu = new JSlider(JSlider.HORIZONTAL, 0, 180, 180);
        colorSliders.add(hu);
        hu.setName("hu");
        JSlider su = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
        colorSliders.add(su);
        su.setName("su");
        JSlider vu = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
        colorSliders.add(vu);
        vu.setName("vu");
 
        //Turn on labels at major tick marks.
 
        threshold.setMajorTickSpacing(10);
        threshold.setMinorTickSpacing(1);
        threshold.setPaintTicks(true);
        threshold.setPaintLabels(true);
        threshold.setBorder(
        BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.PLAIN, 15);
        threshold.setFont(font);
        
        for (JSlider j : colorSliders) {
        	j.setMajorTickSpacing(15);
            j.setMinorTickSpacing(5);
            j.setPaintTicks(true);
            j.setPaintLabels(true);
            threshold.setFont(font);
            j.addChangeListener(this);
        }
        
        //Put everything together.
        
        JPanel panel = new JPanel(new GridLayout(6,2));
        add(thresholdLabel);
        add(threshold);
        panel.add(hlLabel, BorderLayout.CENTER);
        panel.add(hl);
        panel.add(slLabel);
        panel.add(sl);
        panel.add(vlLabel);
        panel.add(vl);
        panel.add(huLabel);
        panel.add(hu);
        panel.add(suLabel);
        panel.add(su);
        panel.add(vuLabel);
        panel.add(vu);
        add(panel);
        
        setBorder(BorderFactory.createEmptyBorder(10,0,800,0));
    }
 
    /** Listen to the sliders. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        String sliderName = source.getName();
        switch (sliderName) {     
        case "threshold": 
        	if (!source.getValueIsAdjusting()) {
                this.thesholdValue = source.getValue()/1000000000.0;
            }
        	break;
        case "hl":
        	if (!source.getValueIsAdjusting()) {
                this.hlValue = source.getValue();
            }
        	break;
        case "sl":
        	if (!source.getValueIsAdjusting()) {
                this.slValue = source.getValue();
            }
        	break;
        case "vl":
        	if (!source.getValueIsAdjusting()) {
                this.vlValue = source.getValue();
            }
        	break;
        case "hu":
        	if (!source.getValueIsAdjusting()) {
                this.huValue = source.getValue();
            }
        	break;
        case "su":
        	if (!source.getValueIsAdjusting()) {
                this.suValue = source.getValue();
            }
        	break;
        case "vu":
        	if (!source.getValueIsAdjusting()) {
                this.vuValue = source.getValue();
            }
        	break;
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
			
			Mat hsvImage = ConvertToHSV(mat), dst = new Mat(), mask = new Mat(), filtered = new Mat();
			
			double[] lowerArray = {this.hlValue, this.slValue, this.vlValue};
			Scalar lowerBounds = new Scalar(lowerArray);
			double[] upperArray = {this.huValue, this.suValue, this.vuValue};
			Scalar upperBounds = new Scalar(upperArray);
			Core.inRange(hsvImage, lowerBounds, upperBounds, mask);
			Core.bitwise_and(hsvImage, hsvImage, filtered, mask);
			Mat newImage = new Mat();
			Imgproc.cvtColor(filtered, newImage, Imgproc.COLOR_HSV2BGR);
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			MatOfInt hull = new MatOfInt();
			
			Collections.sort(contours, new Comparator<Mat>() {

				@Override
				public int compare(Mat arg0, Mat arg1) {
					return (int)(Imgproc.contourArea(arg1) - Imgproc.contourArea(arg0));
				}

		    });
			
			if (contours.size() > 0) {
				Imgproc.convexHull(contours.get(0), hull);
			}
			List<Point> tmpList = new ArrayList<Point>();
			for (int i = 0; i < hull.toList().size(); i++) {
				tmpList.add(contours.get(0).toList().get(hull.toList().get(i)));
			}
			MatOfPoint biggestHull = new MatOfPoint();
			biggestHull.fromList(tmpList);
			List<MatOfPoint> biggestHullList = new ArrayList<MatOfPoint>();
			biggestHullList.add(biggestHull);
			double[] colorData = mat.get((int)(tmpList.get(0).y), (int)(tmpList.get(0).x));	
			Imgproc.drawContours(newImage, biggestHullList, -1, new Scalar(colorData[0], colorData[1], colorData[2]), -1);			
			
			int blockSize = 2;
			int ksize = 3;
			double k = 0.15;
			
			Mat tmpContours = new Mat(newImage.size(), newImage.type());
			Imgproc.drawContours(tmpContours, biggestHullList, -1, new Scalar(255, 0, 0), -1);
			Mat greyscale = turnGray(tmpContours); 
			Imgproc.cornerHarris(greyscale, dst, blockSize, ksize, k);
			
			for (int i = 0; i < mat.width(); i++) {
				for (int j = 0; j < mat.height(); j++) {
					if (dst.get(j, i)[0] > this.thesholdValue) {
						Point pt = new Point(i, j);
						Core.circle(newImage, pt, mat.height() / 150, new Scalar(0, 0, 255), mat.height() / 300);
					}
				}
			}

			// processing finished

			BufferedImage image = Mat2BufferedImage(newImage);
			g.drawImage(image, 10, 400, (int)(image.getWidth()*0.75), (int)(image.getHeight()*0.75), null); // scale the height of the video frame here
			
		}
			
	}

	public Mat turnGray(Mat img) {
		Mat mat1 = new Mat();
		Imgproc.cvtColor(img, mat1, Imgproc.COLOR_RGB2GRAY);
		return mat1;
	}
	
	public Mat ConvertToHSV(Mat img) {
		Mat mat1 = new Mat();
		Imgproc.cvtColor(img, mat1, Imgproc.COLOR_BGR2HSV);
		return mat1;
	}

	public Mat thresh(Mat img) {
		Mat threshed = new Mat();
		int SENSITIVITY_VALUE = 100;
		Imgproc.threshold(img, threshed, SENSITIVITY_VALUE, 255, Imgproc.THRESH_BINARY);
		return threshed;
	}

}

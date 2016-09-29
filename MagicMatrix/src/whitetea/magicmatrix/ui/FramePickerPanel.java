package whitetea.magicmatrix.ui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import whitetea.magicmatrix.model.Frame;

public class FramePickerPanel extends JPanel {
	
	private static final long serialVersionUID = 5307804641192919970L;
	private Frame frame;
	
	public FramePickerPanel(Frame frame) {
		super(new GridLayout(frame.getNbOfRows(), frame.getNbOfColumns(), 1, 1));
		setFocusable(true);
		this.frame = frame;
		for(int x = 0; x < frame.getNbOfColumns(); x++)
			for(int y = 0; y < frame.getNbOfRows(); y++) {
				JPanel p = new JPanel();
				p.setPreferredSize(new Dimension(4,4));
				p.setBackground(frame.getPixelColor(x, y));
				add(p);
			}
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10 + 5*frame.getNbOfColumns(), 10 + 5*frame.getNbOfRows());
		
	}

	public Frame getFrame() {
		return frame;
	}

}

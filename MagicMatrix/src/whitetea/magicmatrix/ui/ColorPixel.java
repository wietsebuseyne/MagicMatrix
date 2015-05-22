package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class ColorPixel extends JPanel {

	private static final long serialVersionUID = -3946804904508173604L;
	
	public ColorPixel() {
		this(new Color(0, 0, 0));
	}

	public ColorPixel(Color color) {
		if(color == null)
			throw new IllegalArgumentException("The color cannot be null");
		setPreferredSize(new Dimension(50, 50));
		setBackground(color);
	}
	
}

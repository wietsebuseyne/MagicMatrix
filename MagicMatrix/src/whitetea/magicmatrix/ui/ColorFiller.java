package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

public class ColorFiller extends JPanel {

	private static final long serialVersionUID = 4077080443497129849L;
	private int number;
	
	//TODO geen number & row meer
	public ColorFiller(int number, boolean row) {
		if(number == -1)
			this.setPreferredSize(new Dimension(10, 10));
		else if(row)
			this.setPreferredSize(new Dimension(10, 50));
		else
			this.setPreferredSize(new Dimension(50, 10));
		this.setBackground(Color.BLACK);
		this.setBorder(null);
		
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

}

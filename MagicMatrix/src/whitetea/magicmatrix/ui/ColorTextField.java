package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorTextField extends TextField {
	
	private static final long serialVersionUID = 1L;

	public ColorTextField() {
		this(Color.BLACK);
	}
	
	public ColorTextField(Color c) {
		this.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					setBackground(Color.decode("#" + getText()));
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}
			}
		});
		setBackground(c);		
	}
	
	@Override
	public void setBackground(Color c) {
		super.setBackground(c);
		setText(String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue()));
		setForeground((perceivedBrightness(c) > 130 ? Color.BLACK : Color.WHITE));
	}
	
	private static int perceivedBrightness(Color c) {
	    return (int)Math.sqrt(
	    c.getRed() * c.getRed() * .299 +
	    c.getGreen() * c.getGreen() * .587 +
	    c.getBlue() * c.getBlue() * .114);
	}

}

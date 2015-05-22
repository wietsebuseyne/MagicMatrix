package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class ColorPixel extends JPanel {

	private static final long serialVersionUID = -3946804904508173604L;
	private int nbCols, nbRows;
	
	public ColorPixel(int nbCols, int nbRows) {
		this(new Color(0, 0, 0), nbCols, nbRows);
	}

	public ColorPixel(Color color, int nbCols, int nbRows) {
		if(color == null)
			throw new IllegalArgumentException("The color cannot be null");
		this.nbCols = nbCols;
		this.nbRows = nbRows;
		setBackground(color);
		setLayout(new GridBagLayout());
	}

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d;
        Container c = getParent();
        if (c != null) {
            d = c.getSize();
        } else {
            return new Dimension(50, 50);
        }
    	int size = (int) Double.min((d.getWidth()-50-5*nbCols)/nbCols, (d.getHeight()-50-5*nbRows)/nbRows);
        return new Dimension(size, size);
    }
	
}

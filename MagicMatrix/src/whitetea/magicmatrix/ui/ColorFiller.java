package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

public class ColorFiller extends JPanel {

	private static final long serialVersionUID = 4077080443497129849L;
	private int nbCols, nbRows;
	private boolean row, all = false;
	
	public ColorFiller(int nbCols, int nbRows) {
		this(nbCols, nbRows, true);
		this.all = true;
	}
	
	public ColorFiller(int nbCols, int nbRows, boolean row) {
		this.setBackground(Color.BLACK);
		this.nbCols = nbCols;
		this.nbRows = nbRows;
		this.row = row;
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
        return all ? new Dimension(10, 10) : row ? new Dimension(10, size) : new Dimension(size, 10);
    }

}

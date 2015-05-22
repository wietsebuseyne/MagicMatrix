package whitetea.magicmatrix.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import whitetea.magicmatrix.model.MagicMatrix;

import com.bric.plaf.SimpleColorPaletteUI;
import com.bric.swing.ColorPalette;

public class MagicMatrixx extends JFrame {
	
	private static final long serialVersionUID = 7990658908818289796L;
	private FramePanel framePanel;
	private ColorPalette colorPicker;
    private Container c;
	
	public MagicMatrixx() {
    	c = getContentPane();        
    	framePanel = new FramePanel(new MagicMatrix(8, 8));
        c.setLayout(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        
        cons.gridx = 0;
        cons.gridy = 0;
        cons.gridheight = 500;
        cons.gridwidth = 500;
        c.add(framePanel, cons);
        
        colorPicker = new ColorPalette();
        colorPicker.setUI(new SimpleColorPaletteUI());
        cons.gridx = 210;
        cons.gridy = 0;
        c.add(colorPicker, cons);
        
        setBounds(100,100,600,600);
        setMinimumSize(new Dimension(600, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
	}

    
    public static void main(String[] args) {
    	new MagicMatrixx();
    }

}

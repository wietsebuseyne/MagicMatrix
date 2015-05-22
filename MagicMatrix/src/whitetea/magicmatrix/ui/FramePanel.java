package whitetea.magicmatrix.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import whitetea.magicmatrix.model.MagicMatrix;
import whitetea.magicmatrix.model.observer.Observer;

public class FramePanel extends JPanel implements MouseListener, Observer {
	
	private static final long serialVersionUID = 2973984516727648424L;
	//TODO interface ColorDependable
	private ColorPixel[][] pixels;
	private volatile boolean mouseDown = false;
	//TODO frame die het doorkrijgt van de MMFrame
	//TODO bijhouden model niet nodig
	//TODO update zonder MagicMatrix
	private MagicMatrix model;
	
	public FramePanel(MagicMatrix model) {
		if(model == null)
			throw new IllegalArgumentException("The model cannot be null");
		this.model = model;
		model.addObserver(this);
		
		pixels = new ColorPixel[model.getNbOfRows()][model.getNbOfColumns()];
		addMouseListener(this);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		
		for(int x = 0; x < model.getNbOfColumns(); x++) {
			final int rowNr = x;
			ColorFiller cf = new ColorFiller(x, true);
			cf.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
				    if (e.getButton() == MouseEvent.BUTTON1)
				        mouseDown = true;
			        color();
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					if(mouseDown)
						color();
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
				    if (e.getButton() == MouseEvent.BUTTON1)
				        mouseDown = false;
				}
				
				private void color() {
					model.colorRow(rowNr);
				}
				
			});
			c.gridx = 0;
			c.gridy = x;
			add(cf, c);
			for(int y = 0; y < model.getNbOfRows(); y++) {
				final int colNr = y;
				pixels[x][y] = new ColorPixel();
				pixels[x][y].addMouseListener(new MouseAdapter() {
					
					@Override
					public void mousePressed(MouseEvent e) {
					    if (e.getButton() == MouseEvent.BUTTON1)
					        mouseDown = true;
					    model.colorPixel(rowNr, colNr);
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						if(mouseDown)
						    model.colorPixel(rowNr, colNr);
					}
					@Override
					public void mouseReleased(MouseEvent e) {
					    if (e.getButton() == MouseEvent.BUTTON1)
					        mouseDown = false;
					}
					
				});
				c.gridx = y+1;
				c.gridy = x;
				add(pixels[x][y], c);
			}
		}
		ColorFiller cfAll = new ColorFiller(-1, true);
		c.gridx = 0;
		c.gridy = model.getNbOfRows();
		add(cfAll, c);
		cfAll.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1)
			        mouseDown = true;
		        color();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				if(mouseDown)
					color();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1)
			        mouseDown = false;
			}
			
			private void color() {
				model.fill();
			}
			
		});
		for(int j = 0; j < model.getNbOfColumns(); j++) {
			final int colNr = j;
			ColorFiller cf = new ColorFiller(j, false);
			cf.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
				    if (e.getButton() == MouseEvent.BUTTON1)
				        mouseDown = true;
			        color();
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					if(mouseDown)
						color();
				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
				    if (e.getButton() == MouseEvent.BUTTON1)
				        mouseDown = false;
				}
				
				private void color() {
					model.colorColumn(colNr);
				}
				
			});
			c.gridx = j+1;
			c.gridy = model.getNbOfRows();
			add(cf, c);
		}
	}
	
	@Override
	public void update(MagicMatrix updatedModel) {
		for(int x = 0; x < model.getNbOfColumns(); x++) 
			for(int y = 0; y < model.getNbOfRows(); y++)
				pixels[x][y].setBackground(model.getPixelColor(x, y));
		revalidate();
		repaint();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1)
	        mouseDown = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	    if (e.getButton() == MouseEvent.BUTTON1)
	        mouseDown = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	


}

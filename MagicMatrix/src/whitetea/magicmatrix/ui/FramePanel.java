package whitetea.magicmatrix.ui;

import java.awt.Dimension;
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
	
	private static final long serialVersionUID = 1L;
	//TODO interface ColorDependable
	private ColorPixel[][] pixels;
	private volatile boolean mouseDown = false;
	//TODO frame die het doorkrijgt van de MMFrame
	//TODO bijhouden model niet nodig
	//TODO update zonder MagicMatrix
	private MagicMatrix model;

    @Override
    public Dimension getPreferredSize() {
        return super.getMaximumSize();
    }
	
	public FramePanel(MagicMatrix model) {
		if(model == null)
			throw new IllegalArgumentException("The model cannot be null");
		this.model = model;
		model.addObserver(this);
		
		pixels = new ColorPixel[model.getNbOfRows()][model.getNbOfColumns()];
		addMouseListener(this);
		setLayout(new GridBagLayout());
		
		GridBagConstraints horizontalFiller = new GridBagConstraints();
		horizontalFiller.insets = new Insets(5,5,5,5);
		horizontalFiller.gridx = 0;
		
		GridBagConstraints verticalFiller = new GridBagConstraints();
		verticalFiller.insets = new Insets(5,5,5,5);
		verticalFiller.gridy = model.getNbOfRows();
		
		GridBagConstraints allFiller = new GridBagConstraints();
		allFiller.insets = new Insets(5,5,5,5);
		allFiller.gridx = 0;
		allFiller.gridy = model.getNbOfRows();
		
		GridBagConstraints pixel = new GridBagConstraints();
		pixel.insets = new Insets(5,5,5,5);
		
		for(int x = 0; x < model.getNbOfColumns(); x++) {
			final int rowNr = x;
			ColorFiller cf = new ColorFiller(model.getNbOfColumns(), model.getNbOfRows(), true);
			horizontalFiller.gridy = rowNr;
			cf.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
				    switch(e.getButton()) {
				    case MouseEvent.BUTTON1:
				        mouseDown = true;
				        color();
				        break;
				    }
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
			add(cf, horizontalFiller);
			for(int y = 0; y < model.getNbOfRows(); y++) {
				final int colNr = y;
				pixels[x][y] = new ColorPixel(model.getNbOfColumns(), model.getNbOfRows());
				pixels[x][y].addMouseListener(new MouseAdapter() {
					
					@Override
					public void mousePressed(MouseEvent e) {
					    if (e.getButton() == MouseEvent.BUTTON1) {
					        mouseDown = true;
					        model.colorPixel(rowNr, colNr);
					    }
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						if(mouseDown)
						    model.colorPixel(rowNr, colNr);
					}
					@Override
					public void mouseReleased(MouseEvent e) {
					    switch(e.getButton()) {
					    case MouseEvent.BUTTON1:
					        mouseDown = false;
					        break;
					    case MouseEvent.BUTTON3:
					    	model.setCurrentColor(model.getPixelColor(rowNr, colNr));
					    	break;
					    }
					}
					
				});
				pixel.gridx = y+1;
				pixel.gridy = x;
				add(pixels[x][y], pixel);
			}
		}
		ColorFiller cfAll = new ColorFiller(model.getNbOfColumns(), model.getNbOfRows());
		cfAll.setPreferredSize(new Dimension(10, 10));
		cfAll.setMaximumSize(new Dimension(10, 10));
		add(cfAll, allFiller);
		cfAll.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
			    if (e.getButton() == MouseEvent.BUTTON1) {
			        mouseDown = true;
			        color();
			    }
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
			ColorFiller cf = new ColorFiller(model.getNbOfColumns(), model.getNbOfRows(), false);
			cf.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent e) {
				    if (e.getButton() == MouseEvent.BUTTON1) {
				        mouseDown = true;
				        color();
				    }
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
			verticalFiller.gridx = j+1;
			add(cf, verticalFiller);
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

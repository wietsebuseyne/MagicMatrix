package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import whitetea.magicmatrix.model.Frame;
import whitetea.magicmatrix.model.MagicMatrix;
import whitetea.magicmatrix.model.observer.Observer;

public class FramePicker extends JPanel implements Observer {
	
	private static final long serialVersionUID = -266317498035651875L;
	private List<FramePickerPanel> frames;
	private FramePickerPanel currentFrame;
	//TODO List<Frame> en laten updaten door MMFrame
	private MagicMatrix model;
	
	//TODO pijltjes voor navigate, del voor verwijderen
	public FramePicker(MagicMatrix model) {
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setFocusable(true);
		model.addObserver(this);
		frames = new ArrayList<>();
		update(model);
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
				Component c = getComponentAt(e.getPoint());
				if(c instanceof FramePickerPanel && !model.inAnimation()) {
					model.setCurrentFrame(frames.indexOf((FramePickerPanel)c));
				}
			}
		});

		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(!model.inAnimation())
					switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						model.setCurrentFrame((model.getCurrentFrameIndex()+model.getNbOfFrames()-1) % (model.getNbOfFrames()));
						break;
					case KeyEvent.VK_RIGHT:
						model.setCurrentFrame((model.getCurrentFrameIndex()+1) % model.getNbOfFrames());
						break;
					}
			}
			
		});
	}
	
	//TODO refactor enkel met index?
	private void focusOn(int index) {
		focusOn((FramePickerPanel)getComponent(index));
	}
	
	private void focusOn(FramePickerPanel frame) {
		if(currentFrame != null)
			currentFrame.setBackground(null);
		currentFrame = frame;
		currentFrame.setBackground(Color.GRAY);
		//currentFrame.requestFocus();
	}

	@Override
	public void update(MagicMatrix updatedModel) {
		this.model = updatedModel;
		java.awt.EventQueue.invokeLater(new Runnable() {
		    public void run() {
				removeAll();
				frames.clear();
				for(Frame f : model.getFrames()) {
					FramePickerPanel fpp = new FramePickerPanel(f);
					add(fpp);
					frames.add(fpp);
				}
				focusOn(model.getCurrentFrameIndex());
				revalidate();
				repaint();
		    }
		} );
	}
	
}

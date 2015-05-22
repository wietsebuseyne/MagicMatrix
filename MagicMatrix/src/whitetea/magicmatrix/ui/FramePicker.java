package whitetea.magicmatrix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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
		model.addObserver(this);
		frames = new ArrayList<>();
		update(model);
		focusOn((FramePickerPanel)getComponent(0));
		setPreferredSize(currentFrame.getPreferredSize());
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				Component c = getComponentAt(e.getPoint());
				if(c instanceof FramePickerPanel) {
					model.setCurrentFrame(frames.indexOf((FramePickerPanel)c));
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
	}

	@Override
	public void update(MagicMatrix updatedModel) {
		this.model = updatedModel;
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

}

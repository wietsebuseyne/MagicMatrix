package whitetea.magicmatrix.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import whitetea.magicmatrix.communication.ColorduinoCommunicator;
import whitetea.magicmatrix.model.animation.Animation;
import whitetea.magicmatrix.model.animation.Animator;
import whitetea.magicmatrix.model.observer.Observable;
import whitetea.magicmatrix.model.observer.Observer;

public class MagicMatrix implements Observable {
	
	private List<Frame> frames, backup;
	private Frame currentFrame;
	private int nbOfRows, nbOfCols;
	private List<Observer> observers;
	private Color currentColor;
	private Animator animator;
	private ColorduinoCommunicator colorduinoCommunicator;
	
	public MagicMatrix(int nbOfRows, int nbOfCols) {
		observers = new ArrayList<>();
		
		colorduinoCommunicator = new ColorduinoCommunicator();
		switch(colorduinoCommunicator.connect()) {
		case ColorduinoCommunicator.SUCCESSFULLY_CONNECTED:
			addObserver(colorduinoCommunicator);
			break;
		case ColorduinoCommunicator.NOT_CONNECTED:
			JOptionPane.showMessageDialog(null, "No Colorduino could be found."
					+ "\nUse the top menu to connect it later."
					+ "\n\nNote: Removing the serial cable from your Colorduino might help with certain connection issues");
			break;
		case ColorduinoCommunicator.IN_USE:
			JOptionPane.showMessageDialog(null, "The port is already in use by a different program."
					+ "\nClose other instances of this program and/or all other programs using the port."
					+ "\nUse the top menu to connect it later.");
			break;
		}
		
		animator = new Animator(this);
		setNbOfRows(nbOfRows);
		setNbOfColumns(nbOfCols);
		
		//Initialize frames
		frames = new ArrayList<>();
		addFrame();
		
		setCurrentColor(Color.RED);
	}
	
	public ColorduinoCommunicator getColorduinoCommunicator() {
		return colorduinoCommunicator;
	}

	public boolean inAnimation() {
		return animator.inAnimation();
	}
	
	public void fill() {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		getCurrentFrame().fill(getCurrentColor());
		notifyObservers();
	}
	
	public void colorColumn(int colNr) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		getCurrentFrame().fillColumn(colNr, getCurrentColor());
		notifyObservers();
	}
	
	public void colorRow(int rowNr) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		getCurrentFrame().fillRow(rowNr, getCurrentColor());
		notifyObservers();
	}
	
	public void colorPixel(int rowNr, int colNr) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		getCurrentFrame().setPixelColor(rowNr, colNr, getCurrentColor());
		notifyObservers();
	}
	
	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color currentColor) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		if(currentColor == null)
			throw new IllegalArgumentException("The given color must be effective");
		this.currentColor = currentColor;
		notifyObservers();
	}

	public void setCurrentFrame(int index) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be altered while in animation mode. Use the replaceFrame method instead.");
		currentFrame = getFrameAt(index);
		notifyObservers();
	}
	
	public Frame getCurrentFrame() {
		return this.currentFrame;
	}
	
	public int getCurrentFrameIndex() {
		return this.frames.indexOf(getCurrentFrame());
	}

	public int getNbOfRows() {
		return nbOfRows;
	}
	
	private void setNbOfRows(int nbOfRows) {
		if(nbOfRows < 1)
			throw new IllegalArgumentException("The number of rows must be strictly positive.");
		this.nbOfRows = nbOfRows;
	}

	public int getNbOfColumns() {
		return nbOfCols;
	}

	private void setNbOfColumns(int nbOfCols) {
		if(nbOfCols < 1)
			throw new IllegalArgumentException("The number of columns must be strictly positive.");
		this.nbOfCols = nbOfCols;
	}
	
	public int getNbOfFrames() {
		return frames.size();
	}
	
	public boolean isValidFrameIndex(int index) {
		return index >= 0 && index < frames.size();
	}
	
	public void addFrame() {
		if(inAnimation())
			throw new IllegalStateException("No frames can be added while in animation mode. Use the replaceFrame method instead.");
		addFrame(getCurrentFrameIndex()+1);
	}
	
	public void addFrame(int index) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be added while in animation mode. Use the replaceFrame method instead.");
		if(index < 0 || index > frames.size())
			throw new IllegalArgumentException("The index must be a valid one.");
		frames.add(index, new Frame(nbOfRows, nbOfCols));
		setCurrentFrame(getCurrentFrameIndex()+1);
		notifyObservers();
	}

	public void addFrame(Frame frame) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be added while in animation mode. Use the replaceFrame method instead.");
		if(frame.getNbOfColumns() != getNbOfColumns() || frame.getNbOfRows() != getNbOfRows())
			throw new IllegalArgumentException("The dimension of the frame must be " + getNbOfRows() + "x" + getNbOfColumns() + ".");
		frames.add(frame);
		setCurrentFrame(getNbOfFrames()-1);
		notifyObservers();		
	}

	public void addFrames(List<Frame> frames) { //don't use addFrame so we don't overuse the notifyAll() method
		if(inAnimation())
			throw new IllegalStateException("No frames can be added while in animation mode. Use the replaceFrame method instead.");
		for(Frame frame : frames) {
			if(frame.getNbOfColumns() != getNbOfColumns() || frame.getNbOfRows() != getNbOfRows())
				throw new IllegalArgumentException("The dimension of the frame must be " + getNbOfRows() + "x" + getNbOfColumns() + ".");
			this.frames.add(frame);
		}
		notifyObservers();		
	}

	public void addCopy() {
		if(inAnimation())
			throw new IllegalStateException("No frames can be added while in animation mode. Use the replaceFrame method instead.");
		frames.add(getCurrentFrameIndex()+1, getCurrentFrame().clone());
		setCurrentFrame(getCurrentFrameIndex()+1);
		notifyObservers();
	}

	public void removeFrame(int index) {
		if(inAnimation())
			throw new IllegalStateException("No frames can be removed while in animation mode. Use the replaceFrame method instead.");
		if(!isValidFrameIndex(index))
			throw new IllegalArgumentException("The index must be a valid one.");
		if(getNbOfFrames() != 1) {
			frames.remove(index);
			setCurrentFrame(index == getNbOfFrames() ? index-1 : index);
		}
	}

	public void removeFrame() {
		if(inAnimation())
			throw new IllegalStateException("No frames can be removed while in animation mode. Use the replaceFrame method instead.");
		removeFrame(getCurrentFrameIndex());
	}

	public void removeAllFrames() {
		if(inAnimation())
			throw new IllegalStateException("No frames can be removed while in animation mode. Use the replaceFrame method instead.");
		frames.clear();
		addFrame();
	}

	public void replaceFrame(Frame newFrame) {
		if(newFrame.getNbOfColumns() != getNbOfColumns() || newFrame.getNbOfRows() != getNbOfRows())
			throw new IllegalArgumentException("The dimension of the frame must be " + getNbOfRows() + "x" + getNbOfColumns() + ".");
		frames.set(getCurrentFrameIndex(), newFrame);
		currentFrame = newFrame;
		notifyObservers();
	}
	
	public void moveFrame(int currentIndex, int newIndex) {
		if(!isValidFrameIndex(currentIndex))
			throw new IllegalArgumentException("The index of the frame to be moved must be a valid one");
		if(!isValidFrameIndex(newIndex))
			throw new IllegalArgumentException("The new index of the frame must be a valid one");
		Frame frame = frames.remove(currentIndex);
		frames.add(newIndex, frame);
		notifyObservers();
	}
	
	public void moveFrameRight() {
		try {
			moveFrame(getCurrentFrameIndex(), getCurrentFrameIndex()+1);
		} catch(IllegalArgumentException ex) {}
	}
	
	public void moveFrameLeft() {
		try {
			moveFrame(getCurrentFrameIndex(), getCurrentFrameIndex()-1);
		} catch(IllegalArgumentException ex) {}
	}

	public void shiftRight() {
		getCurrentFrame().shiftRight();
		notifyObservers();
	}

	public void shiftLeft() {
		getCurrentFrame().shiftLeft();
		notifyObservers();
	}

	public void shiftUp() {
		getCurrentFrame().shiftUp();
		notifyObservers();
	}

	public void shiftDown() {
		getCurrentFrame().shiftDown();
		notifyObservers();
	}
	
	public Frame getFrameAt(int index) {
		if(!isValidFrameIndex(index))
			throw new IllegalArgumentException("The index must be a valid one. Given index: " + index);
		return frames.get(index);
	}
	
	public List<Frame> getFrames() {
		return Collections.unmodifiableList(frames);
	}

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for(Observer o : observers)
			o.update(this);
	}

	public Color getPixelColor(int rowNr, int colNr) {
		return getCurrentFrame().getPixelColor(rowNr, colNr);
	}
	
	//TODO unsafe
	public Animator getAnimator() {
		return animator;
	}

	public void startAnimation(Animation animation) {
		if(backup == null)
			backup = frames;
		frames = new ArrayList<>();
		addFrame();
		animator.start(animation);
	}
	
	public void stopAnimation() {
		if(animator.stop()) { //Only if animation has stopped
			frames = backup;
			currentFrame = frames.get(0);
			backup = null;
			notifyObservers();
		}
	}
	
	public void setAnimationSpeed(long intervalInMs) {
		animator.setSpeed(intervalInMs);
		notifyObservers();
	}

	public long getAnimationSpeed() {
		return animator.getSpeed();
	}
	
	public BufferedImage getImage() {
		BufferedImage result = new BufferedImage(
                getNbOfFrames()*getNbOfColumns(), getNbOfRows(),
                BufferedImage.TYPE_INT_RGB);
		Graphics g = result.createGraphics();
		int x = 0, y = 0;
		for (Frame f : getFrames()) {
			BufferedImage bi = f.getImage();
			g.drawImage(bi, x, y, null);
			x += getNbOfColumns();
			if (x > result.getWidth()) {
				x = 0;
				y += getNbOfRows();
			}
		}
		return result;
	}

}

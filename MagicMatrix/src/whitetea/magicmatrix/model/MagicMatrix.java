package whitetea.magicmatrix.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	public MagicMatrix(int nbOfRows, int nbOfCols) {
		animator = new Animator(this);
		frames = new ArrayList<>();
		observers = new ArrayList<>();
		setNbOfRows(nbOfRows);
		setNbOfColumns(nbOfCols);
		addFrame();
		setCurrentColor(Color.RED);
	}
	
	public void fill() {
		getCurrentFrame().fill(getCurrentColor());
		notifyObservers();
	}
	
	public void colorColumn(int colNr) {
		getCurrentFrame().fillColumn(colNr, getCurrentColor());
		notifyObservers();
	}
	
	public void colorRow(int rowNr) {
		getCurrentFrame().fillRow(rowNr, getCurrentColor());
		notifyObservers();
	}
	
	public void colorPixel(int rowNr, int colNr) {
		getCurrentFrame().setPixelColor(rowNr, colNr, getCurrentColor());
		notifyObservers();
	}
	
	public Color getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(Color currentColor) {
		if(currentColor == null)
			throw new IllegalArgumentException("The given color must be effective");
		this.currentColor = currentColor;
		notifyObservers();
	}

	public void setCurrentFrame(int index) {
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
		addFrame(getCurrentFrameIndex()+1);
	}
	
	public void addFrame(int index) {
		if(index < 0 || index > frames.size())
			throw new IllegalArgumentException("The index must be a valid one.");
		frames.add(index, new Frame(nbOfRows, nbOfCols));
		setCurrentFrame(getCurrentFrameIndex()+1);
		notifyObservers();
	}

	public void addFrame(Frame frame) {
		if(frame.getNbOfColumns() != getNbOfColumns() || frame.getNbOfRows() != getNbOfRows())
			throw new IllegalArgumentException("The dimension of the frame must be " + getNbOfRows() + "x" + getNbOfColumns() + ".");
		frames.add(frame);
		setCurrentFrame(getNbOfFrames()-1);
		notifyObservers();		
	}

	public void addFrames(List<Frame> frames) { //don't use addFrame so we don't overuse the notifyAll() method
		for(Frame frame : frames) {
			if(frame.getNbOfColumns() != getNbOfColumns() || frame.getNbOfRows() != getNbOfRows())
				throw new IllegalArgumentException("The dimension of the frame must be " + getNbOfRows() + "x" + getNbOfColumns() + ".");
			this.frames.add(frame);
		}
		notifyObservers();		
	}

	public void addCopy() {
		frames.add(getCurrentFrameIndex()+1, getCurrentFrame().clone());
		setCurrentFrame(getCurrentFrameIndex()+1);
		notifyObservers();
	}

	public void removeFrame(int index) {
		if(!isValidFrameIndex(index))
			throw new IllegalArgumentException("The index must be a valid one.");
		if(getNbOfFrames() != 1) {
			frames.remove(index);
			setCurrentFrame(index == getNbOfFrames() ? index-1 : index);
		}
	}

	public void removeFrame() {
		removeFrame(getCurrentFrameIndex());
	}

	public void removeAllFrames() {
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
		animator.stop();
		System.out.println(backup.size());
		frames = backup;
		currentFrame = frames.get(0);
		backup = null;
		notifyObservers();
	}
	
	public void setAnimationSpeed(long intervalInMs) {
		animator.setSpeed(intervalInMs);
		notifyObservers();
	}

	public long getAnimationSpeed() {
		return animator.getSpeed();
	}
	
	public BufferedImage getImage() {
		return frames.get(0).getImage();
	}

}

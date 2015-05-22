package whitetea.magicmatrix.model.animation;

import java.util.List;

import whitetea.magicmatrix.model.Frame;

public class CustomFramesAnimation implements Animation {
	
	private List<Frame> frames;
	private int currentFrameIndex = 0;

	//TODO GUI shows all frames instead of only current
	public CustomFramesAnimation() {}
	
	public CustomFramesAnimation(List<Frame> frames) {
		setFrames(frames);
	}

	public void setFrames(List<Frame> frames) {
		if(frames == null || frames.isEmpty())
			throw new IllegalArgumentException("The frames cannot be null and must contain at least one frame.");
		int nbRows = frames.get(0).getNbOfRows();
		int nbCols = frames.get(0).getNbOfColumns();
		for(Frame f : frames)
			if(f.getNbOfRows() != nbRows || f.getNbOfColumns() != nbCols)
				throw new IllegalArgumentException("The frames must all have the same width and height.");
		if(currentFrameIndex < 0 || currentFrameIndex >= frames.size())
			currentFrameIndex = 0;
		this.frames = frames;
	}

	@Override
	public Frame getNextFrame(Frame currentFrame) {
		if(frames == null || frames.isEmpty())
			throw new IllegalStateException("The frames must be set before and contain at least one frame before getting the next frame.");
		currentFrameIndex = currentFrameIndex == frames.size()-1 ? 0 : currentFrameIndex+1; //if -1 => fist frame is selected.
		return frames.get(currentFrameIndex);
	}

	@Override
	public Frame getStartFrame(int width, int height) {
		if(frames == null || frames.isEmpty())
			throw new IllegalStateException("The frames must be set before and contain at least one frame before getting the start frame.");
		return frames.get(currentFrameIndex);
	}
	
	public void setStartFrame(int index) {
		if(index < 0 || index >= frames.size())
			throw new IllegalArgumentException("The index must be valid.");
		currentFrameIndex = index;
	}

}

package whitetea.magicmatrix.model.animation;

import java.util.List;

import whitetea.magicmatrix.model.Frame;

public class CustomFramesAnimation implements Animation {
	
	private List<Frame> frames;
	private int currentFrameIndex = 0;

	public CustomFramesAnimation() {}
	
	public CustomFramesAnimation(List<Frame> frames) {
		if(frames == null || frames.isEmpty())
			throw new IllegalArgumentException("The frames cannot be null and must contain at least one frame.");
		int nbRows = frames.get(0).getNbOfRows();
		int nbCols = frames.get(0).getNbOfColumns();
		for(Frame f : frames)
			if(f.getNbOfRows() != nbRows || f.getNbOfColumns() != nbCols)
				throw new IllegalArgumentException("The frames must all have the same width and height.");
		this.frames = frames;
	}

	public void setFrames(List<Frame> frames) {
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
		return frames.get(0);
	}

}

package whitetea.magicmatrix.model.animation;

import whitetea.magicmatrix.model.Frame;

public interface Animation {
		
	public abstract Frame getNextFrame(Frame currentFrame);

	public abstract Frame getStartFrame(int width, int height);

}

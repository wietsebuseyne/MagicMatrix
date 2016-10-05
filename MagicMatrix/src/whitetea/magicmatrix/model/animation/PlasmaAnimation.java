package whitetea.magicmatrix.model.animation;

import whitetea.magicmatrix.model.Frame;

public class PlasmaAnimation implements Animation {

	@Override
	public Frame getNextFrame(Frame currentFrame) {
		return new Frame(currentFrame.getNbOfColumns(), currentFrame.getNbOfRows());
	}

	@Override
	public Frame getStartFrame(int width, int height) {
		return new Frame(width, height);
	}

}

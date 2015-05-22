package whitetea.magicmatrix.model.animation;

import java.awt.Color;
import java.util.Random;

import whitetea.magicmatrix.model.Frame;

public class RandomAnimation implements Animation {

	@Override
	public Frame getNextFrame(Frame currentFrame) {
		Frame frame = new Frame(currentFrame.getNbOfRows(), currentFrame.getNbOfColumns());
		Random rnd = new Random();
		for (int y = 0; y < frame.getNbOfRows(); y++)
			for (int x = 0; x < frame.getNbOfColumns(); x++)
				frame.setPixelColor(y, x, new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
		return frame;
	}

	@Override
	public Frame getStartFrame(int width, int height) {
		return getNextFrame(new Frame(height, width));
	}

}

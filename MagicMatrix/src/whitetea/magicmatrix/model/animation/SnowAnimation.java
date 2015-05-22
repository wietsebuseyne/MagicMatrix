package whitetea.magicmatrix.model.animation;

import java.awt.Color;
import java.util.Random;

import whitetea.magicmatrix.model.Frame;

public class SnowAnimation implements Animation {
	
	@Override
	public Frame getNextFrame(Frame currentFrame) {
		Frame frame = new Frame(currentFrame);
		frame.shiftDown();
		Random rnd = new Random();
		for(int i = 0; i < frame.getNbOfColumns(); i++){
			if(frame.getPixelColor(1, i) != Color.white && frame.getPixelColor(2, i) != Color.white && rnd.nextInt(5)==1) {
				frame.setPixelColor(0, i, Color.white);
				i+=2; //minstens twee lege plaatsen tussen twee sneeuwvlokken
			}
		}
		return frame;
	}

	@Override
	public Frame getStartFrame(int width, int height) {
		return new Frame(height, width);
	}

}

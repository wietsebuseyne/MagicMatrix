package whitetea.magicmatrix.model.animation;

import java.awt.Color;
import java.util.Random;

import whitetea.magicmatrix.model.Frame;

public class MeteorAnimation extends SnowAnimation {
	
	//TODO save locations of white pixels and add shine after these pixels
	//private List<Location> locations = new ArrayList<Location>();

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
	
	/*private class Location {
		public int x;
		public int y;
		
		public Location(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
	}*/

}

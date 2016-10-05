package whitetea.magicmatrix.communication.packet;

import java.awt.Color;

public class TwelveBitColors {
	
	private byte byte1, byte2, byte3;
	
	public TwelveBitColors(Color c1, Color c2) {
		byte r1 = (byte) (c1.getRed() / 16);
		byte g1 = (byte) (c1.getGreen() / 16);
		byte b1 = (byte) (c1.getBlue() / 16);
		byte r2 = (byte) (c2.getRed() / 16);
		byte g2 = (byte) (c2.getGreen() / 16);
		byte b2 = (byte) (c2.getBlue() / 16);
		
		byte1 = (byte) ((r1 << 4) | g1);
		byte2 = (byte) ((b1 << 4) | r2);
		byte3 = (byte) ((g2 << 4) | b2);
	}

	public TwelveBitColors(byte byte1, byte byte2, byte byte3) {
		this.byte1 = byte1;
		this.byte2 = byte2;
		this.byte3 = byte3;
	}

	public byte getByte1() {
		return byte1;
	}

	public byte getByte2() {
		return byte2;
	}

	public byte getByte3() {
		return byte3;
	}

}

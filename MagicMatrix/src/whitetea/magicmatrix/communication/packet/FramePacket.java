package whitetea.magicmatrix.communication.packet;

import whitetea.magicmatrix.model.Frame;

public class FramePacket extends Packet {
	
	public FramePacket(Frame frame) {
		byte[] payload = new byte[frame.getNbOfRows()*frame.getNbOfColumns()*3/2];
		int i = 0;
		//TODO Does not work if Nb of Columns is not even
		for(int y = 0; y < frame.getNbOfRows(); y++) {
			for(int x = 0; x < frame.getNbOfColumns(); x++) {
				TwelveBitColors tbc = new TwelveBitColors(frame.getPixelColor(y, x), frame.getPixelColor(y, ++x));
				payload[i++] = tbc.getByte1();
				payload[i++] = tbc.getByte2();
				payload[i++] = tbc.getByte3();
			}
		}
		initialize(Operation.OPC_PLAY_FRAME, payload);
	}

}

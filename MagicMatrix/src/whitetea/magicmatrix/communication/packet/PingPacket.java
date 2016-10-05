package whitetea.magicmatrix.communication.packet;

public class PingPacket extends Packet {
	
	public PingPacket() {
	    byte pkt[] = new byte[0];
	    initialize(Operation.OPC_PING, pkt);
	}

}

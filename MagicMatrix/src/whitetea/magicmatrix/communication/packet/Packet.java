package whitetea.magicmatrix.communication.packet;

//TODO creator?
public abstract class Packet {
	
	//TODO andere sync byte (voor als ook in payload)? niet nodig normaal
	protected static final byte SYNC_BYTE = (byte)0xaa;
	private byte[] packet;
	
	public Packet() {
		packet = null;
	}
	
	public Packet(Operation op, byte[] payload) {
		initialize(op, payload);
	}
	
	protected void initialize(Operation op, byte[] payload) {
		byte checksum = 0;
		packet = new byte[payload.length+4];
		packet[0] = SYNC_BYTE;
		packet[1] = op.getValue();
		packet[2] = (byte) payload.length;
		checksum += packet[1] + packet[2];
		int i = 0;
		//copy the payload
		//TODO faster if not copied? (but reference...)
		//TODO overhead door meerdere keren gekopieerd (eerst in subklasse daarna hier)
		for(; i < payload.length; i++) {
			packet[i+3] = payload[i];
			checksum += payload[i];
		}
		packet[i+3] = checksum;		
	}
	
	public byte[] getBytes() {
		return packet;
	}

}

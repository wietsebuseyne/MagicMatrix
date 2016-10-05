package whitetea.magicmatrix.communication.packet;

public enum Operation {
	
	OPC_PING((byte)0x01), 
	OPC_PLAY_FRAME((byte)0x02), 
	OPC_QUEUE_FRAME((byte)0x03), 
	OPC_SET_MODE((byte)0x04),
	OPC_FILL((byte)0x05);
	
	private byte value;
	
	private Operation(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return this.value;
	}

}

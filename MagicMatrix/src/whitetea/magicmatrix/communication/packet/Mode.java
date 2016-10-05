package whitetea.magicmatrix.communication.packet;

public enum Mode {
	
	PLASMA((byte)0x01), PLAY_FRAME((byte)0x02), FILL((byte)0x04);
	
	private byte value;
	 
	private Mode(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return value;
	}

}

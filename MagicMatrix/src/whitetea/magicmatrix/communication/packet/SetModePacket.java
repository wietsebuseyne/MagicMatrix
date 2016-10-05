package whitetea.magicmatrix.communication.packet;

public class SetModePacket extends Packet {

	public SetModePacket(Mode mode) {
		super(Operation.OPC_SET_MODE, new byte[] {mode.getValue()});
	}

}

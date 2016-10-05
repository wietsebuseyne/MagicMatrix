package whitetea.magicmatrix.communication;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import whitetea.magicmatrix.communication.packet.FramePacket;
import whitetea.magicmatrix.communication.packet.Mode;
import whitetea.magicmatrix.communication.packet.Packet;
import whitetea.magicmatrix.communication.packet.SetModePacket;
import whitetea.magicmatrix.model.MagicMatrix;
import whitetea.magicmatrix.model.observer.Observer;

public class ColorduinoCommunicator implements Observer {
	
	public static final int SUCCESSFULLY_CONNECTED = 1,
			NOT_CONNECTED = -1,
			IN_USE = -2;
	
	
	private SerialPort serialPort;
	Lock lock = new ReentrantLock();
	Condition dataAvailable = lock.newCondition();

	public ColorduinoCommunicator() {
		super();
	}
	
	public int connect() {
		return connect(SerialPort.BAUDRATE_57600);
	}

	public int connect(int baud) {
		String[] portNames = SerialPortList.getPortNames();
		if(portNames.length == 0)
			return NOT_CONNECTED;
		
		boolean inUse = false;
		for(int i = 0; i < portNames.length && !isConnected(); i++) {
			serialPort = new SerialPort(portNames[i]);
			try {
			    serialPort.openPort();
	
			    serialPort.setParams(baud,
			                         SerialPort.DATABITS_8,
			                         SerialPort.STOPBITS_1,
			                         SerialPort.PARITY_NONE);
			    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
			    //TODO use serial reader to check for error messages and maybe recover
			    //serialPort.addEventListener(new SerialReader(serialPort), SerialPort.MASK_RXCHAR);
	
			} catch (SerialPortException ex) {
				if(ex.getMessage().contains("Port busy"))
					inUse = true;
			}
		}
		if(inUse) return IN_USE;
		return isConnected() ? SUCCESSFULLY_CONNECTED : NOT_CONNECTED;
	}
	
	public boolean isConnected() {
		return serialPort != null && serialPort.isOpened();
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public void close() {
		if (serialPort != null) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendPacket(Packet p) throws SerialPortException {
		if(!isConnected())
			throw new IllegalStateException("No connection with the colorduino");
		serialPort.writeBytes(p.getBytes());
	}

	/**
	 * Handles the input coming from the serial port. A new line character is
	 * treated as the end of a block in this example.
	 */
	public class SerialReader implements SerialPortEventListener {
		
		private SerialPort serialPort;
		
		public SerialReader(SerialPort serialPort) { 
			this.serialPort = serialPort;
		}

		public void serialEvent(SerialPortEvent event) {
		    if(event.isRXCHAR() && event.getEventValue() > 0) {
	            try {
	                String receivedData = this.serialPort.readString(event.getEventValue());
	                System.out.println("Reading: " + receivedData);
	            } catch (SerialPortException ex) {
	                System.out.println("Error in receiving string from COM-port: " + ex);
	            }
	        }
	    }

	}
	
	@Override
	public void update(MagicMatrix updatedModel) {
		try {
			//TODO don't set mode every time!
			sendPacket(new SetModePacket(Mode.PLAY_FRAME));
			sendPacket(new FramePacket(updatedModel.getCurrentFrame()));
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

}

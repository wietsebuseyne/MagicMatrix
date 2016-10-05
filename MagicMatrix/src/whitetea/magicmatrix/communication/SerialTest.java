package whitetea.magicmatrix.communication;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Arrays;
import java.util.Enumeration;

import whitetea.magicmatrix.communication.packet.FramePacket;
import whitetea.magicmatrix.communication.packet.Mode;
import whitetea.magicmatrix.communication.packet.Packet;
import whitetea.magicmatrix.communication.packet.SetModePacket;
import whitetea.magicmatrix.model.animation.RandomAnimation;


public class SerialTest implements SerialPortEventListener {
	

	private byte SYNC_BYTE = (byte)0xAA;
	private byte OPC_PING = (byte)0x01;
	private byte OPC_PLAY_FRAME = (byte)0x02;
	private byte OPC_QUEUE_FRAME = (byte)0x03;
	private byte OPC_SET_MODE = (byte)0x04;
	private byte OPC_FILL = (byte)0x05;
	 
	private byte CMODE_PLASMA = 0x1;
	private byte CMODE_PLAY_FRAME = 0x2;
	private byte CMODE_FILL = 0x4;
	private SerialPort serialPort;
	
	private int[] gammaTab = {       
			0,      0,      0,      0,      0,      0,      0,      0,
			0,      0,      0,      0,      0,      0,      0,      0,
			0,      0,      0,      0,      0,      0,      0,      0,
			0,      0,      0,      0,      0,      0,      0,      0,
			0,      0,      0,      0,      0,      0,      0,      0,
			0,      0,      0,      0,      16,     16,     16,     16,
			16,     16,     16,     16,     16,     16,     16,     16, 
			16,     16,     16,     16,     16,     16,     16,     16, 
			16,     16,     16,     16,     16,     16,     16,     16,
			16,     16,     16,     16,     16,     16,     16,     16,
			32,     32,     32,     32,     32,     32,     32,     32, 
			32,     32,     32,     32,     32,     32,     32,     32, 
			32,     32,     32,     32,     32,     32,     32,     32, 
			32,     32,     32,     32,     32,     32,     32,     32, 
			32,     32,     32,     32,     48,     48,     48,     48, 
			48,     48,     48,     48,     48,     48,     48,     48, 
			48,     48,     48,     48,     48,     48,     48,     48, 
			48,     48,     48,     48,     64,     64,     64,     64, 
			64,     64,     64,     64,     64,     64,     64,     64, 
			64,     64,     64,     64,     64,     64,     64,     64, 
			64,     64,     64,     64,     64,     64,     64,     64, 
			80,     80,     80,     80,     80,     80,     80,     80, 
			80,     80,     80,     80,     80,     80,     80,     80, 
			96,     96,     96,     96,     96,     96,     96,     96, 
			96,     96,     96,     96,     96,     96,     96,     96, 
			112,    112,    112,    112,    112,    112,    112,    112, 
			128,    128,    128,    128,    128,    128,    128,    128, 
			144,    144,    144,    144,    144,    144,    144,    144, 
			160,    160,    160,    160,    160,    160,    160,    160, 
			176,    176,    176,    176,    176,    176,    176,    176, 
			192,    192,    192,    192,    192,    192,    192,    192, 
			208,    208,    208,    208,    224,    224,    224,    224, 
			240,    240,    240,    240,    240,    255,    255,    255 
		};
	
    /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
                        "/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM5", // Windows
			"COM4", // Windows
			"COM3", // Windows
			"COM2", // Windows
			"COM1", // Windows
			"COM0", // Windows
	};
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	public BufferedReader input;
	/** The output stream to the port */
	public OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 57600;

	public void initialize() {
        // the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					System.out.println("Colorduino connected on " + portId.getName());
					break;
				}
			}
		}
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine = input.readLine();
				System.out.println(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	public void writePacket(byte[] packet) {
	    try {
            output.write(packet);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public int setMode(Mode m) {
		Packet p = new SetModePacket(m);
		System.out.println(Arrays.toString(p.getBytes()));
		writePacket(p.getBytes());
		return 0;
	}
	
	public int setMode(byte mode) {
	    byte pkt[] = new byte[5];
	    pkt[0] = SYNC_BYTE;
	    pkt[1] = OPC_SET_MODE;
	    pkt[2] = 0x1;
	    pkt[3] = mode;
	    pkt[4] = (byte)(pkt[1]+pkt[2]+pkt[3]); // chksum
		System.out.println(Arrays.toString(pkt));
	    writePacket(pkt);
	    return 0;
	    //TODO
	    //return waitForResponse();
	}

	//TODO first 2 bytes altijd 49 en 70 ipv 170 (aa)
	public int testConnection() { //34 153 255
	    byte pkt[] = new byte[4];
	    pkt[0] = SYNC_BYTE;
	    pkt[2] = (byte)0x22;
	    pkt[3] = (byte)0x22;
	    pkt[4] = (byte)0x22;
	    pkt[5] = (byte)0x99;
	    pkt[6] = (byte)0xff;
	    writePacket(pkt);
	    return waitForResponse();
	}
	
	public int fill() {
	    
	    byte pkt[] = new byte[7];
	    pkt[0] = SYNC_BYTE;
	    pkt[1] = OPC_FILL;
	    pkt[2] = 3;
	    
	    int r = (256 >> 4) & 0x0f;
	    int g = (56 >> 4) & 0x0f;
	    int b = (123 >> 4) & 0x0f;
	    
	    int byter = (r << 4) | g;
	    pkt[3] = (byte)byter;
	    
	    int byteg = b << 4;
	    
	    r = (gammaTab[256] >> 4) & 0x0f;
	    g = (gammaTab[56] >> 4) & 0x0f;
	    b = (gammaTab[123] >> 4) & 0x0f;

	    byteg |= r;
	    
	    pkt[4] = (byte)byteg;
	    
	    int byteb = (g << 4) | b;
	    pkt[5] = (byte)byteb;
	    
	    pkt[6] = (byte)(pkt[1]+pkt[2]+pkt[3]+pkt[4]+pkt[5]);
	    
	    writePacket(pkt);
	    
	    // n.b. colorduino origin is bottom left, our origin is upper left,
	    // so loop backwards on the y
	    /*for (int y=7;y >=0; y--) {
	    	for (int x=0;x < 8;x++) {
	    		System.out.println(idx);
			    
			    int r4,g4,b4;
			    int b;
	
			    r4 = (256 >> 4) & 0x0f;
			    g4 = (56 >> 4) & 0x0f;
			    b4 = (123 >> 4) & 0x0f;
	
			    b = (r4 << 4) | g4;
			    chksum += b;
			    pkt[idx++] = (byte)b;
			    b = b4 << 4;
	
			    // cvt to 4 bit
			    r4 = (gammaTab[256] >> 4) & 0x0f;
			    g4 = (gammaTab[56] >> 4) & 0x0f;
			    b4 = (gammaTab[123] >> 4) & 0x0f;
	
	
			    b |= r4;
			    chksum += b;
			    pkt[idx++] = (byte)b;
			    b = (g4 << 4) | b4;
			    chksum += b;
			    pkt[idx++] = (byte)b;

			}
		}

	    pkt[idx] = chksum;*/

	    return 0;
	}
	
	public int waitForResponse() {
	    try {
			while(!input.ready()) {
				//System.out.println("waiting");
			}
		    return input.read();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	    return -1;
	}
	
	public int sendPacket(Packet p) {
		System.out.println(Arrays.toString(p.getBytes()));
		writePacket(p.getBytes());
		return 0;
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {
					Thread.sleep(2000);
					//main.setMode(main.CMODE_PLASMA);
					//Thread.sleep(2000);
					main.setMode(Mode.PLASMA);
					//main.sendPacket(new FramePacket(new RandomAnimation().getStartFrame(8, 8)));
					//Thread.sleep(6000);
				} catch (InterruptedException ie) {}
				//System.out.println(main.testConnection());
			}
		};
		t.start();
		//TODO http://rxtx.qbang.org/wiki/index.php/Two_way_communcation_with_the_serial_port
		//TODO http://rxtx.qbang.org/wiki/index.php/Event_Based_Two_Way_Communication
		//TODO http://stackoverflow.com/questions/13724740/continuing-communication-with-serial-device-using-rxtx-and-java

		//main.setMode(main.CMODE_FILL);
		//main.fill();
		/*Thread t = new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {
					Thread.sleep(1000000);
				} catch (InterruptedException ie) {}
			}
		};
		t.start();*/
	}
}
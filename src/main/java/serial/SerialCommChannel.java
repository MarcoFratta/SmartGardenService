package serial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Comm channel implementation based on serial port.
 *
 * @author aricci
 */
public class SerialCommChannel implements CommChannel, SerialPortEventListener {

	private final SerialPort serialPort;
	private final BlockingQueue<String> queue;
	private StringBuffer currentMsg = new StringBuffer();

	public SerialCommChannel(final String port, final int rate) throws Exception {
		this.queue = new ArrayBlockingQueue<>(2000);

		this.serialPort = new SerialPort(port);
		this.serialPort.openPort();

		this.serialPort.setParams(rate,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		this.serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
				SerialPort.FLOWCONTROL_RTSCTS_OUT);

		// serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		this.serialPort.addEventListener(this);
	}

	@Override
	public void sendMsg(final String msg) {
		final char[] array = (msg + "\n").toCharArray();
		final byte[] bytes = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			bytes[i] = (byte) array[i];
		}
		try {
			synchronized (this.serialPort) {
				this.serialPort.writeBytes(bytes);
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public String receiveMsg() throws InterruptedException {
		// TODO Auto-generated method stub
		return this.queue.take();
	}

	@Override
	public boolean isMsgAvailable() {
		// TODO Auto-generated method stub
		return !this.queue.isEmpty();
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public void close() {
		try {
			if (this.serialPort != null) {
				this.serialPort.removeEventListener();
				this.serialPort.closePort();
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}


	public void serialEvent(final SerialPortEvent event) {
		/* if there are bytes received in the input buffer */
		if (event.isRXCHAR()) {
			try {
				String msg = this.serialPort.readString(event.getEventValue());

				msg = msg.replaceAll("\r", "");

				this.currentMsg.append(msg);

				boolean goAhead = true;

				while (goAhead) {
					final String msg2 = this.currentMsg.toString();
					final int index = msg2.indexOf("\n");
					if (index >= 0) {
						this.queue.put(msg2.substring(0, index));
						this.currentMsg = new StringBuffer();
						if (index + 1 < msg2.length()) {
							this.currentMsg.append(msg2.substring(index + 1));
						}
					} else {
						goAhead = false;
					}
				}

			} catch (final Exception ex) {
				ex.printStackTrace();
				System.out.println("Error in receiving string from COM-port: " + ex);
			}
		}
	}
}

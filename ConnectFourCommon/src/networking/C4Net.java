package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Networking class used by connect4. All networking functionality common to
 * both the client and the server.
 * 
 * @author Julien
 * @version 11/2/2015
 */
public class C4Net {

	public final static int SERVER_PORT = 50000;

	private final int BUFFER_SIZE = 2;
	private InputStream in;
	private OutputStream out;

	public C4Net(Socket clientSocket) throws IOException {
		in = clientSocket.getInputStream();
		out = clientSocket.getOutputStream();
	}

	/**
	 * Read packets from the InputStream.
	 * 
	 * @return The packet which was received.
	 * @throws SocketException
	 *             Thrown if connection closed prematurely
	 * @throws IOException
	 *             Thrown if there is a problem reading from the InputStream
	 */
	public byte[] receivePacket() throws SocketException, IOException {
		byte[] byteBuffer = new byte[BUFFER_SIZE];
		int totalBytesRcvd = 0; // Total bytes received so far
		int bytesRcvd; // Bytes received in last read
		// Code snippet taken from TCPEchoClient (Given by Alex Simonelis)
		while (totalBytesRcvd < byteBuffer.length) {
			if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
					byteBuffer.length - totalBytesRcvd)) == -1)
				throw new SocketException("Connection closed prematurely");
			totalBytesRcvd += bytesRcvd;
		}
		return byteBuffer;
	}

	/**
	 * Sends a packet which consists of a game message and a column index.
	 * 
	 * @param msg
	 *            Game message to be sent.
	 * @param col
	 *            Column index associated with the message being sent.
	 * @throws IOException
	 *             Thrown if a communication problem occurs.
	 */
	public void sendPacket(C4Msg msg, int col) throws IOException {
		byte[] byteBuffer = new byte[BUFFER_SIZE];
		byteBuffer[0] = (byte) msg.ordinal();
		byteBuffer[1] = (byte) col;
		out.write(byteBuffer);
	}

	/**
	 * Sends a packet without a column
	 * 
	 * @param msg
	 *            Game message to be sent.
	 * @throws IOException
	 *             Thrown if a communication problem occurs.
	 */
	public void sendPacket(C4Msg msg) throws IOException {
		// No column associated with this message
		sendPacket(msg, -1);
	}
}
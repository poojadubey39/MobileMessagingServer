package ai.niki.assignment.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.JTextArea;
import ai.niki.assignment.constants.ServerConstants;
import ai.niki.assignment.message.Message;

public class Client {

	private int sourceId;
	private Socket socket;
	private boolean isClientConnected;
	private BufferedInputStream bufferedInputStream;
	private BufferedOutputStream bufferedOutputStream;
	private JTextArea jTextArea;
	private BlockingQueue<Message> messageQueue;

	public Client(int userId, JTextArea jTextArea) throws ConnectException {
		this.jTextArea = jTextArea;
		this.sourceId = userId;

	}

	public void connect() throws ConnectException {
		try {
			String hostAddress = Inet4Address.getLocalHost().getHostAddress();
			socket = new Socket(hostAddress, ServerConstants.PORT_NUMBER);
			messageQueue = new ArrayBlockingQueue<>(10);
			isClientConnected = true;
			Thread recieverThread = new RecieverThread();
			Thread senderThread = new SenderThread();
			initializeStreams();
			recieverThread.start();
			senderThread.start();

			// send initializing message to add mapping
			sendMessage(sourceId, ServerConstants.CLIENT_CONNECTED);
		} catch (IOException e) {
			throw new ConnectException();
		}

	}

	public void disconnect() {

		try {
			isClientConnected = false;
			socket.close();
			bufferedInputStream.close();
			bufferedOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(int destId, String message) {

		Message senderMessage = new Message();
		senderMessage.setSourceId(sourceId);
		senderMessage.setDestinationId(destId);
		senderMessage.setData(message);
		try {
			messageQueue.put(senderMessage);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void initializeStreams() {

		try {
			bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
			bufferedInputStream = new BufferedInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class RecieverThread extends Thread {

		@Override
		public void run() {
			try {
				while (isClientConnected) {
					byte[] size = new byte[4];
					bufferedInputStream.read(size);
					if (size.length == 0) {
						return;
					}
					int messageSize = ByteBuffer.wrap(size).getInt();
					byte[] byteArr = new byte[messageSize];

					bufferedInputStream.read(byteArr);
					if (byteArr.length == 0) {
						return;
					}
					Message message = Message.parse(byteArr);
					String messageStr = "Message from User: " + message.getSourceId() + "\nMessage: "
							+ message.getData() + "\n\n";
					jTextArea.append(messageStr);
				}
			} catch (Exception e) {
				isClientConnected = false;
			}

		}

	}

	private class SenderThread extends Thread {

		@Override
		public void run() {
			try {
				while (isClientConnected) {
					Message senderMessage = messageQueue.take();
					bufferedOutputStream.write(senderMessage.toByteArray());
					bufferedOutputStream.flush();
				}
			} catch (Exception e) {
				isClientConnected = false;
			}

		}

	}

}

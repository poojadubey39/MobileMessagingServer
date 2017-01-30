package ai.niki.assignment.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

import ai.niki.assignment.cache.CacheCleanUpThread;
import ai.niki.assignment.cache.CommonCache;
import ai.niki.assignment.constants.ServerConstants;
import ai.niki.assignment.message.Message;

public class ServerClientSocket implements Runnable {

	private Socket socket;
	private BufferedInputStream bufferedInputStream;
	private boolean isConnected;

	public ServerClientSocket(Socket socket) {
		this.socket = socket;
		initializeStreams();
		isConnected = true;
		Thread thread = new SenderThread();
		thread.start();
	}

	private void initializeStreams() {

		try {
			bufferedInputStream = new BufferedInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try {
			while (isConnected) {
				processReceievedData();
			}
		} catch (Exception e) {
			closeConnection();
		}
	}

	private void closeConnection() {
		try {
			socket.close();
			bufferedInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processReceievedData() {
		try {
			byte[] size = new byte[4];

			bufferedInputStream.read(size);
			if (size.length == 0) {
				return;
			}
			int messageSize = ByteBuffer.wrap(size).getInt();
			byte[] byteArr = new byte[messageSize];

			bufferedInputStream.read(byteArr);

			if(byteArr.length == 0){
				return;
			}
			Message message = Message.parse(byteArr);
			if (message == null) {
				return;
			}
			
			if(message.getData().equals(ServerConstants.CLIENT_CONNECTED)){
				int sourceId = message.getDestinationId();
				CommonCache.addSocketMapping(sourceId, socket);
				CacheCleanUpThread cacheCleanUpThread = new CacheCleanUpThread(sourceId);
				cacheCleanUpThread.start();
				return;
			}
			
			boolean isDuplicateMessage = validateMessage(message);
			if (!isDuplicateMessage) {
				CommonCache.addNewMessage(message);
			}
		} catch (IOException e) {
			closeConnection();
		}
	}

	private boolean validateMessage(Message message) {

		BlockingQueue<Message> userQueue = CommonCache.getUserQueue(message.getSourceId());
		if (userQueue == null || userQueue.isEmpty()) {
			return false;
		} else {
			for (Message msg : userQueue) {
				if (msg.getData().equals(message.getData()) && msg.getDestinationId() == message.getDestinationId()) {
					long previousArrivalTime = msg.getArraivalTime();
					long currentArrivalTime = message.getArraivalTime();
					if (((currentArrivalTime - previousArrivalTime) / 1000) < 5) {
						return true;
					} else {
						return false;
					}
				}
			}
		}

		return false;
	}

	class SenderThread extends Thread {

		@Override
		public void run() {
			while (isConnected) {
				Message message = CommonCache.getNextMessage();
				Socket destinationSocket = CommonCache.getUserSocket(message.getDestinationId());
				if (destinationSocket != null) {
					try {
						BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(destinationSocket.getOutputStream());
						bufferedOutputStream.write(message.toByteArray());
						bufferedOutputStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}

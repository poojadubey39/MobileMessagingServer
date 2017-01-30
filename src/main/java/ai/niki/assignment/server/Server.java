package ai.niki.assignment.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.niki.assignment.constants.ServerConstants;

public class Server implements Runnable {

	private static final Server server = new Server();
	private static ServerSocket serverSocket;
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	public static Server getInstance() {
		return server;
	}

	private Server() {
		try {
			serverSocket = new ServerSocket(ServerConstants.PORT_NUMBER);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void startServer() {

		try {
			Socket socket = serverSocket.accept();
			executorService.submit(new ServerClientSocket(socket));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (true) {
			startServer();
		}

	}

	public static void main(String[] args) {
		Server server = Server.getInstance();
		Thread t = new Thread(server);
		t.start();
	}

}

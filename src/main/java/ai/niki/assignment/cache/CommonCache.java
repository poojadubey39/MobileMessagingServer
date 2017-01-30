package ai.niki.assignment.cache;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import ai.niki.assignment.message.Message;

public class CommonCache {

	private static BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(
			10);
	private static Map<Integer, Socket> socketMap = new HashMap<>();
	private static Map<Integer, BlockingQueue<Message>> userQueueMap = new ConcurrentHashMap<>();

	public static void addNewMessage(Message message) {
		try {
			messageQueue.put(message);
			addToUserMap(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void addSocketMapping(int userId, Socket socket) {
		socketMap.put(userId, socket);
//		System.out.println(socketMap);
	}

	private static void addToUserMap(Message message) {
		try {
			int sourceId = message.getSourceId();
			if (userQueueMap.containsKey(sourceId)) {
				userQueueMap.get(sourceId).put(message);
			} else {
				BlockingQueue<Message> userQueue = new ArrayBlockingQueue<>(10);
				userQueueMap.put(sourceId, userQueue);
				userQueue.put(message);
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Message getNextMessage() {
		try {
			return messageQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Socket getUserSocket(int userId) {
		return socketMap.get(userId);
	}

	public static BlockingQueue<Message> getUserQueue(int userId){
		return userQueueMap.get(userId);
	}
}

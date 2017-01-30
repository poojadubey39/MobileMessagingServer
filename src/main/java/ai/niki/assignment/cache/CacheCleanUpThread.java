package ai.niki.assignment.cache;
import java.util.concurrent.BlockingQueue;
import ai.niki.assignment.message.Message;

public class CacheCleanUpThread extends Thread {

	int sourceId;

	public CacheCleanUpThread(int userId) {
		this.sourceId = userId;
	}

	@Override
	public void run() {
		BlockingQueue<Message> userQueue = CommonCache.getUserQueue(sourceId);

		while (true) {
			try {
				if(userQueue != null){
					userQueue.take();
					Thread.sleep(6000);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

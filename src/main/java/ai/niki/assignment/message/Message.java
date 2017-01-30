package ai.niki.assignment.message;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sourceId;
	private String data;
	private int destinationId;
	private long arraivalTime;

	public long getArraivalTime() {
		return arraivalTime;
	}

	public void setArraivalTime(long arraivalTime) {
		this.arraivalTime = arraivalTime;
	}

	public int getSourceId() {
		return sourceId;
	}

	public String getData() {
		return data;
	}

	public void setData(String message) {
		this.data = message;
	}

	public int getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(int destinationId) {
		this.destinationId = destinationId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getSizeOfMessage() {
		return 4 + 4 + data.length() + 4; // srcid + slot to store messagelength
											// + stringlength + destid
	}

	public byte[] toByteArray() {
		int size = getSizeOfMessage();
		ByteBuffer byteBuffer = ByteBuffer.allocate(size + 4);
		byteBuffer.putInt(size);
		byteBuffer.putInt(sourceId);
		byteBuffer.putInt(data.length());
		byteBuffer.put(data.getBytes());
		byteBuffer.putInt(destinationId);

		return byteBuffer.array();

	}

	@Override
	public String toString() {
		return "Message [sourceId=" + sourceId + ", message=" + data + ", destinationId=" + destinationId
				+ ", arraivalTime=" + arraivalTime + "]";
	}

	public static Message parse(byte[] byteArr) {

		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArr);
		int sourceId = byteBuffer.getInt();
		int dataSize = byteBuffer.getInt();
		byte[] dataBytes = new byte[dataSize];
		byteBuffer.get(dataBytes);
		int destId = byteBuffer.getInt();

		Message message = new Message();
		message.setSourceId(sourceId);
		message.setData(new String(dataBytes));
		message.setDestinationId(destId);
		message.setArraivalTime(System.currentTimeMillis());

		return message;

	}

}

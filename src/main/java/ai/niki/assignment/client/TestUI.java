package ai.niki.assignment.client;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TestUI {

	/**
	 * 
	 */
	private Client client;
	private JFrame jFrame;
	private Container container;
	private JLabel destinationLabel;
	private JTextField destTxt;
	private JLabel messageLabel;
	private JTextArea sendMessageText;
	private JButton sendMsgBtn;
	private JLabel recievedMessages;
	private JTextArea receivedMessageArea;
	
	public TestUI(int userId) {

		createUI(userId);
		try {
			client = new Client(userId, receivedMessageArea);
			client.connect();
		} catch (ConnectException e) {
			jFrame.dispose();
			e.printStackTrace();
		}

	}

	private void createUI(int userId) {
		jFrame = new JFrame();
		

		destinationLabel = new JLabel("Enter Destination:");
		destinationLabel.setBounds(10, 10, 100, 20);

		destTxt = new JTextField(10);
		destTxt.setBounds(130, 10, 100, 20);

		messageLabel = new JLabel("Message:");
		messageLabel.setBounds(10, 30, 100, 20);

		sendMessageText = new JTextArea();
		sendMessageText.setRows(3);
		sendMessageText.setColumns(10);
		sendMessageText.setBounds(10, 60, 270, 90);

		sendMsgBtn = new JButton("Send Message");
		sendMsgBtn.setBounds(30, 160, 200, 30);
		
		recievedMessages = new JLabel("Message Received");
		recievedMessages.setBounds(10,200,150,20);
		
		receivedMessageArea = new JTextArea();
		receivedMessageArea.setBounds(10, 230, 270, 200);
		
		sendMsgBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}

			private void sendMessage() {
				try{
					int destId = Integer.parseInt(destTxt.getText());
					String message  = sendMessageText.getText();
					sendMessageText.setText("");
					receivedMessageArea.append("Me : "+message+"\n\n");
					client.sendMessage(destId, message);
					
				}catch(NumberFormatException e){
					JOptionPane.showConfirmDialog(jFrame, "Please enter a valid destination number","ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		jFrame.setResizable(false);
		container = jFrame.getContentPane();
		container.setLayout(null);

		container.add(destinationLabel);
		container.add(destTxt);
		container.add(messageLabel);
		container.add(sendMessageText);
		container.add(sendMsgBtn);
		container.add(recievedMessages);
		container.add(receivedMessageArea);
		
		Insets insets = jFrame.getInsets();
		jFrame.setSize(300 + insets.left + insets.right, 500 + insets.top + insets.bottom);
		jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		       client.disconnect();
		    }
		});
		jFrame.setVisible(true);
		jFrame.setTitle("User: " + userId);
	}

	public static void main(String[] args) {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter the number of agents:");
		try {
			int totalAgents = Integer.parseInt(in.readLine());

			for (int i = 1; i <= totalAgents; i++) {
				new TestUI(i);
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}
}

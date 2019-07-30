import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatPanel extends JPanel implements Runnable, ActionListener {
	public JTextArea getChatArea() {
		return chatArea;
	}

	public JTextField getNameField() {
		return nameField;
	}

	public void setNameField(JTextField nameField) {
		this.nameField = nameField;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
	}

	private JTextArea chatArea;
	private JScrollPane chatAreaScroll;
	private JScrollBar chatAreaScrollBar;
	private JTextField sendField;
	private JTextField nameField;
	private JButton sendBtn;
	private JButton clearBtn;
	private JPanel chatPanel;
	private JPanel sendPanel;
	private JPanel btnPanel;
	
	//
	private Socket socket = null;
	private PrintWriter out = null;
	private Scanner in = null;
	
	public ChatPanel(Socket socket) {
		//		
		try {
			this.socket = socket;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		//

		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		
		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);	
		chatArea.setOpaque(false);
		chatAreaScroll = new JScrollPane(chatArea);
		chatAreaScroll.setOpaque(false);
		chatAreaScroll.getViewport().setOpaque(false);
		chatAreaScrollBar = chatAreaScroll.getVerticalScrollBar();
		add(chatAreaScroll);
		
		sendField = new JTextField();
		sendField.addActionListener(this);
		sendField.setOpaque(false);
		add(sendField, BorderLayout.SOUTH);
		
		nameField = new JTextField();
		nameField.setOpaque(false);
		nameField.setEditable(false);
		add(nameField, BorderLayout.NORTH);	
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		String msg;
		while(true) {
			if(in.hasNextLine()) {
				msg = in.nextLine();
				chatArea.append(msg + "\n");
				chatAreaScrollBar.setValue(chatAreaScrollBar.getMaximum());
				new MusicList().sendMsg();
				//System.out.println(msg);
			}
		}
	}
	
	public void sendMsg() {
		String name, msg;
		
		name = nameField.getText();
		msg = sendField.getText();
		
		out.println(name + " : " + msg);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendBtn || e.getSource() == sendField) {
			sendMsg();
			sendField.setText("");
		} 
		
		//else if (e.getSource() == clearBtn) { // ¹Ì»ç¿ë
		//	chatArea.setText("");
		//}
		
	}

	public JScrollPane getChatAreaScroll() {
		return chatAreaScroll;
	}

	public void setChatAreaScroll(JScrollPane chatAreaScroll) {
		this.chatAreaScroll = chatAreaScroll;
	}

	public JScrollBar getChatAreaScrollBar() {
		return chatAreaScrollBar;
	}

	public void setChatAreaScrollBar(JScrollBar chatAreaScrollBar) {
		this.chatAreaScrollBar = chatAreaScrollBar;
	}

	public JTextField getSendField() {
		return sendField;
	}

	public void setSendField(JTextField sendField) {
		this.sendField = sendField;
	}

	public JButton getSendBtn() {
		return sendBtn;
	}

	public void setSendBtn(JButton sendBtn) {
		this.sendBtn = sendBtn;
	}

	public JButton getClearBtn() {
		return clearBtn;
	}

	public void setClearBtn(JButton clearBtn) {
		this.clearBtn = clearBtn;
	}

	public JPanel getChatPanel() {
		return chatPanel;
	}

	public void setChatPanel(JPanel chatPanel) {
		this.chatPanel = chatPanel;
	}

	public JPanel getSendPanel() {
		return sendPanel;
	}

	public void setSendPanel(JPanel sendPanel) {
		this.sendPanel = sendPanel;
	}

	public JPanel getBtnPanel() {
		return btnPanel;
	}

	public void setBtnPanel(JPanel btnPanel) {
		this.btnPanel = btnPanel;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public Scanner getIn() {
		return in;
	}

	public void setIn(Scanner in) {
		this.in = in;
	}

}

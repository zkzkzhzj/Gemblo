import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;


public class Server extends JFrame implements ActionListener {

	private ServerSocket serverSocket;
	private Manager manager = new Manager();
	// private Socket socket = null;
	private DataOutputStream out = null;
	private int player;
	private String gemColor[] = {"Y","R","B","G"};
	
	private JButton openBtn;
	private JButton closeBtn;
	private JSpinner playerSpinner;
	private JTextField myIpField;
	private JTextArea area;
	private int endCnt=0;
	
	private LinkedHashMap<String, Socket> socketMap = new LinkedHashMap<>();
	private LinkedHashMap<Socket, String> stringMap = new LinkedHashMap<>();
	private String[] colorArray = {"Y","R","B","G"};
	private String[] colorName = {"Yellow", "Red", "Blue", "Green"};
	
	ArrayList<Socket> removeSocket = new ArrayList<Socket>();
	private ChatServer chatServer;
	
	private int roomNum;
	
	public Server(int roomNum) {
		this.roomNum = roomNum;
		
		setSize(300,300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new FlowLayout());
		setLocationRelativeTo(null); // ���� �� ȭ�� �߾ӿ� �߱�
				
		myIpField = new JTextField(20);
		myIpField.setEditable(false);
		
		area = new JTextArea(10,20);
		area.setLineWrap(true);
		area.setEditable(false);
		
		try {
			InetAddress myIP = InetAddress.getLocalHost();
			myIpField.setText("MyIP=" + myIP.getHostAddress());
		} catch (UnknownHostException e) {
			area.append(e + " : myIP �޾ƿ��� �������� ����\n");
			System.out.println(e + " : myIP �޾ƿ��� �������� ����");
		}		
		
		
		playerSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
		
		openBtn = new JButton("���� ����");
		openBtn.addActionListener(this);
		
		closeBtn = new JButton("���� �ݱ�");
		closeBtn.addActionListener(this);
		
		add(myIpField);
		add(playerSpinner);		
		add(area);
		add(openBtn);
		add(closeBtn);		
		
		setVisible(true);		
	}
	
	private class SeverSetting extends Thread {
		int roomNum;
		public SeverSetting(int roomNum) {this.roomNum = roomNum;}
		@Override
		public void run() {
			try { // server ����
				serverSocket = new ServerSocket(10004+roomNum);
				area.append("���� ���� ����\n");
				System.out.println("���� ���� ����");
				
				for(int i=0; i<player; i++) {
					Socket socket = serverSocket.accept();
					manager.add(socket);
					removeSocket.add(socket);
					socketMap.put(colorArray[i], socket);
					stringMap.put(socket, colorName[i]);
					out = new DataOutputStream(socket.getOutputStream());
					
					area.append((i+1)+"�� ° �÷��̾� ����. " + (player-i-1) + "�� ���ҽ��ϴ�.\n");
					System.out.println((i+1)+"�� ° �÷��̾� ����. " + (player-i-1) + "�� ���ҽ��ϴ�.");
					out.writeUTF(gemColor[i]);
					out.writeInt(player);
				}
				
				for(int i=0; i<player; i++)
					new InData((Socket) manager.get(i)).start();
				
			} catch (Exception e) {
				area.append(e+" : ��Ʈ�� �� ���Ƚ��ϴ�.\n"); // ��Ʈ�� �ȿ������� ���� �߻� ����
				System.out.println(e+" : ��Ʈ�� �� ���Ƚ��ϴ�.");
			}
		}
	}

	private class Manager extends Vector {
		
		void add(Socket socket) {
			super.add(socket);
		}
		
		void remove(Socket socket) {
			super.remove(socket);
		}
		
		synchronized void sendToAll(int x, int y, boolean isGem, String gem, int score, boolean isEnd) {
			DataOutputStream out = null;
			Socket socket, nextSocket;			
			boolean endCheck[] = new boolean[player];
			char endChar[] = new char[player];
			String gemStr = "";
			int gemStrCnt = 0;
			
			// ���� ���� ���� �����
			for(int i=0; i<removeSocket.size(); i++) {
				socket = removeSocket.get(i);
				
				if(i+1 < removeSocket.size())
					nextSocket = removeSocket.get(i+1);
				else
					nextSocket = removeSocket.get(0);
				
				if (gem.equals("Y") && socketMap.get("Y") == socket)
					gemStr = stringMap.get(nextSocket);
				else if (gem.equals("R") && socketMap.get("R") == socket)
					gemStr = stringMap.get(nextSocket);
				else if (gem.equals("B") && socketMap.get("B") == socket)
					gemStr = stringMap.get(nextSocket);
				else if (gem.equals("G") && socketMap.get("G") == socket)
					gemStr = stringMap.get(nextSocket);
			}
			
			
			for(int i=0; i<size(); i++) {
				socket = (Socket) elementAt(i);
				
				if(i+1 < stringMap.size())
					nextSocket = (Socket) elementAt(i+1);
				else
					nextSocket = (Socket) elementAt(0);
				
				try {
					out = new DataOutputStream(socket.getOutputStream());					

					if(i+1 < stringMap.size())
						nextSocket = (Socket) elementAt(i+1);
					else
						nextSocket = (Socket) elementAt(0);
					
					out.writeInt(x);
					out.writeInt(y);
					out.writeBoolean(isGem);
					out.writeUTF(gem);
					out.writeInt(score);
					out.writeBoolean(isEnd);
					out.writeUTF(gemStr + " TURN");
					
					char gemColor = gem.charAt(0);					
					
					
					// ���� ���� �� ���� ���� ���� �ѱ��

					if(isEnd) {
						Socket s = null;
						String str = null;
						if(gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						}
						else if(gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						}
						else if(gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						}
						else if(gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}
						
						if(s != null && str != null) {
							// this.remove(s); //������ �����ϰ� �ּ�ó�� �غ���
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
					}
				
				} catch(Exception e) {
					area.append(e + " : ������ ���� �������� ����\n");
					System.out.println(e + " : ������ ���� �������� ����");
				}
			}
		}
	}
	
	private class InData extends Thread {
		private Socket socket;
		private DataInputStream in;
		
		public InData(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			try {
				in = new DataInputStream(socket.getInputStream());
				int x, y, score;
				boolean isGem, isEnd;
				String gem;
				
				while(true) {
					x = in.readInt();
					y = in.readInt();
					isGem = in.readBoolean();
					gem = in.readUTF();
					score = in.readInt();
					isEnd = in.readBoolean();
					
					manager.sendToAll(x, y, isGem, gem, score, isEnd);
				}
				
				
			} catch (Exception e) {
				area.append(e + " : ������ ���� �������� ����\n");
				System.out.println(e + " : ������ ���� �������� ����");
			} finally {
				try {
					manager.remove(socket);
					if(in != null) in.close();
					if(socket != null) socket.close();
					area.append("Ŭ���̾�Ʈ ����\n");
					System.out.println("Ŭ���̾�Ʈ ����");
				} catch(Exception e) {
					area.append(e + " : Ŭ���̾�Ʈ ������ �������� ����\n");
					System.out.println(e + " : Ŭ���̾�Ʈ ������ �������� ����");
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//Server server = new Server();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == openBtn) {
			player = (int) playerSpinner.getValue();			
			chatServer = new ChatServer();
			
			chatServer.start();
			
			new SeverSetting(roomNum).start();
		} else if (e.getSource() == closeBtn) {
			try {				
				for(int i=0; i<manager.size(); i++)
					if(manager.get(i) != null)
						((Socket) manager.get(i)).close();
				
				for(int i=0; i<chatServer.getChatManager().size(); i++)
					if(chatServer.getChatManager().get(i) != null)
						((Socket) chatServer.getChatManager().get(i)).close();
				
				stringMap.clear();
				socketMap.clear();
				
				if(serverSocket != null)
					serverSocket.close();
				
				if(chatServer.getServer() != null)
					chatServer.getServer().close();
				
				area.setText("�������� ���� �ٽ� ���Ⱑ �����մϴ�.\n");
				System.out.println("�������� ���� �ٽ� ���Ⱑ �����մϴ�.");
			} catch (IOException e1) {
				area.append(e + " : ������ �ݴ� �������� ����\n");
				System.out.println(e + " : ������ �ݴ� �������� ����");
			}
		}
	}
	private class ChatManager extends Vector{
		void add(Socket socket) {
			super.add(socket);
		}
		
		void remove(Socket socket) {
			super.remove(socket);
		}
		
		synchronized void sendToAll(String msg) {
			PrintWriter writer = null;
			Socket socket;
			
			for(int i=0; i<size(); i++) {
				socket = (Socket) elementAt(i);
				
				try {
					writer = new PrintWriter(socket.getOutputStream(), true);
				} catch(Exception e) {
					area.append(e + " : ChatManager(sendToAll)");
					System.out.println(e + " : ChatManager(sendToAll)");
				}
				if(writer != null)
					writer.println(msg);
			}
		}
	}
	
	private class ChatServer extends Thread {
		public ServerSocket getServer() {
			return server;
		}

		public void setServer(ServerSocket server) {
			this.server = server;
		}

		public ChatManager getChatManager() {
			return chatManager;
		}

		public void setChatManager(ChatManager chatManager) {
			this.chatManager = chatManager;
		}

		private ServerSocket server;
		private ChatManager chatManager = new ChatManager();		
		
		public void run() {
			try {
				server = new ServerSocket(10005);
				System.out.println("ä�� ���� ���� ����");
				area.append("ä�� ���� ���� ����\n");
				
				while(true) {
					Socket socket = server.accept();
					System.out.println("ä�ü��� ����� ���");
					chatManager.add(socket);
					new Chat(socket).start();
				}
				
			} catch(Exception e) {
				area.append(e + " : ChatServer(run)\n");
				System.out.println(e + " : ChatServer(run)");
			}
		}		
		
		private class Chat extends Thread {
			private Socket socket;
			private BufferedReader reader;
			
			public Chat(Socket socket) {
				this.socket = socket;
			}
			
			public void run() {
				try {
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String message;
					while (true) {						
						if((message = reader.readLine()) != null) {
							chatManager.sendToAll(message);
						}
					}
				} catch (Exception e) {
					area.append(e + " : Chat(run)\n");
					System.out.println(e + " : Chat(run)");
				} finally {
					try {
						chatManager.remove(socket);
						if(reader != null)
							reader.close();
						if(socket != null)
							socket.close();
					} catch(Exception e) {
						area.append(e + " : Chat(run(finally))\n");
						System.out.println(e + " : Chat(run(finally)");
					}
				}
			}
		}
	}
	


}

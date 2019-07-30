import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class RoomPanel extends JPanel implements ActionListener {
	private Game game = null;
	private Main mainFrame;
	private JFrame gameFrame = new JFrame("Gemblo!");
	private JButton createBtn, joinBtn, sendBtn, rankBtn, helpBtn, quizShowBtn, quizAddBtn;
	private JTree userTree;
	private JList roomList, userList;
	// ���� ���̺�
	private JTable userTable;
	private DefaultTableModel tm = new DefaultTableModel();
	private Vector userHeader = new Vector();
	private Vector userData = new Vector();
	
	private JTextField chatSendTF;
	private JTextArea chatArea;
	private JScrollPane chatAreaScroll;
	private JScrollBar chatAreaScrollBar;
	private Vector roomVector = new Vector();
	private Vector userVector = new Vector();
	private String ipStr;
	private Socket roomSocket = null, chatSocket = null;

	private PrintWriter chatOut = null;
	private Scanner chatIn = null;

	private DataInputStream roomIn = null;
	private DataOutputStream roomOut = null;

	private String id;
	
	private HashSet<String> userSet = new HashSet<>();
	private HashMap<String, String> userMap = new HashMap<>();
	
	//private Font listFont = new Font("����", Font.PLAIN, 15);
	
	
	public RoomPanel(String ipStr, String id) {
		this.setLayout(null);
		this.ipStr = ipStr;
		this.id = id;

		init();
		// ���� ����
		try {
			roomSocket = new Socket(ipStr, 10002);

			roomOut = new DataOutputStream(roomSocket.getOutputStream());
			roomIn = new DataInputStream(roomSocket.getInputStream());

			chatSocket = new Socket(ipStr, 10003);
			chatOut = new PrintWriter(chatSocket.getOutputStream(), true);
			chatIn = new Scanner(chatSocket.getInputStream());

		} catch (Exception e) {
			System.out.println("RoomPanel ���� ���� ����: " + e);
		}

		// room ������
		new Thread() {
			public void run() {
				String roomName, str, userName;
				String where;
				int roomCnt = 0;
				int roomNum;
				int playerNum = 0;
				int i;
				
				
				try {
					roomOut.writeUTF("(UserAdd)");
					roomOut.writeUTF(id);
					
					while (true) {
						str = roomIn.readUTF();
//						JOptionPane.showMessageDialog(null, str);
						if(str.equals("(UserAdd)")) {
							userName = roomIn.readUTF();
							where = roomIn.readUTF();
							userSet.add(userName);
							userMap.put(userName, where);
						} // TODO USER ADD INFO
						else if (str.equals("(UserMod)")) {
							userName = roomIn.readUTF();
							where = roomIn.readUTF();
							userMap.replace(userName, where);
						} else if (str.equals("(UserOut)")) {
							userName = roomIn.readUTF();
							where = roomIn.readUTF();
							userMap.remove(userName);
							userSet.remove(userName);
						}
						else if (str.startsWith("(UserInfo)")) {
							userData.clear();
							
							for(int j=0; j<tm.getRowCount(); j++)
								tm.removeRow(j);
							
							Iterator it = userSet.iterator();
							
							while(it.hasNext()) {
								String userStr = (String) it.next();
								String userWhere = userMap.get(userStr);
								
								Vector userVectorData = new Vector();
								userVectorData.add(userStr);
								userVectorData.add(userWhere);
								
								tm.addRow(userVectorData);
//								userData.add(userVectorData);								
							}
							
							
							
							// userJList
//							userVector.clear();
//							Iterator it = userSet.iterator();
//							
//							while(it.hasNext()) {
//								String userStr = (String) it.next();
//								String userWhere = userMap.get(userStr);
//								
//								userVector.add(new User(userStr, userWhere));
//							}
//							
//							userList.setListData(userVector);							
						
						} else if (str.startsWith("(JoinRoom)")){

							roomName = str.substring(10);
							roomCnt = roomIn.readInt();
							roomNum = roomIn.readInt();
							playerNum = roomIn.readInt();
							
//							System.out.printf("roomName:%s, roomCnt:%d, roomNum:%d, playerNum:%d\n", roomName, roomCnt, roomNum, playerNum);
							
							GameRoom gr = null;
							
							for(int j=0; j<roomVector.size(); j++)
								if(((GameRoom)roomVector.get(j)).getRoomName().equals(roomName)) {
									gr = (GameRoom) roomVector.get(j);
									break;
								}
							
							if(gr != null) {
								gr.setPlayerNum(playerNum);
								roomList.setListData(roomVector);
							}

	

						} else if(str.equals("(GameOut)")) {
							roomName = roomIn.readUTF();
							
							GameRoom gr = null;
							
							for(int j=0; j<roomVector.size(); j++)
								if(((GameRoom)roomVector.get(j)).getRoomName().equals(roomName)) {
									gr = (GameRoom) roomVector.get(j);
									break;
								}
							
							if(gr != null) {
								roomVector.remove(gr);
								roomList.setListData(roomVector);
							}
						} else if (str.equals("(CreateRoom)")){
							roomName = roomIn.readUTF();
							roomCnt = roomIn.readInt();
							roomNum = roomIn.readInt();
							playerNum = roomIn.readInt();
							
							roomVector.add(new GameRoom(roomName, roomNum, playerNum, roomCnt));
							roomList.setListData(roomVector);
						}						
						
						revalidate();
						repaint();
					}
				} catch (Exception e) {
					System.out.println("room������ ���� : " + e);
				}
			}
		}.start();

		// ä�� ������
		new Thread() {
			@Override
			public void run() {
				String msg;
				while (true) {
					if (chatIn.hasNextLine()) {
						msg = chatIn.nextLine();
						chatArea.append(msg + "\n");
						chatAreaScrollBar.setValue(chatAreaScrollBar.getMaximum());
						new MusicList().sendMsg();
						// System.out.println(msg);
					}
				}
			}
		}.start();
		
	}

	// init�� UI �ʱ� ���� �ϱ�.
	private void init() {
		setSize(1000, 750);

		// ���� ���� �� ����� ����
		JPanel roomListPanel = new JPanel();
		roomListPanel.setLayout(new BorderLayout());
		roomListPanel.setBorder(new TitledBorder(null, "���ӹ� ���", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
		roomListPanel.setBounds(10, 20, 700, 300);
		this.add(roomListPanel);
	

		roomList = new JList() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(new ImageIcon(Main.class.getResource("img/roomListPanel.png")).getImage(), 0, 0, null);
			}
		};
		//roomList.setFont(listFont);
		createBtn = new JButton("�� �����");
		createBtn.addActionListener(this);
		joinBtn = new JButton("�� �����ϱ�");
		joinBtn.addActionListener(this);

		JPanel btnPanel = new JPanel(new GridLayout(0, 2));
		btnPanel.add(createBtn);
		btnPanel.add(joinBtn);

		roomListPanel.add(new JScrollPane(roomList));
		roomListPanel.add(btnPanel, BorderLayout.SOUTH);
		roomListPanel.setOpaque(false);

		// ���� ���� �� ����� ��

		// ���� ä�� ����� ����
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBorder(new TitledBorder(null, "���� ä�ù�", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
		chatPanel.setBounds(10, 350, 700, 300);
		this.add(chatPanel);
		chatPanel.setOpaque(false);

		chatSendTF = new JTextField();
		chatSendTF.setActionCommand("����");
		chatSendTF.addActionListener(this);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);

		chatAreaScroll = new JScrollPane(chatArea);
		chatAreaScrollBar = chatAreaScroll.getVerticalScrollBar();

		sendBtn = new JButton("����");
		sendBtn.addActionListener(this);

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.X_AXIS));

		chatPanel.add(chatAreaScroll);
		chatPanel.add(sendPanel, BorderLayout.SOUTH);

		sendPanel.add(chatSendTF);
		sendPanel.add(sendBtn);
		// ���� ä�� ����� ��

		// ����� ��� ����� ���� (JList)
//		JPanel userPanel = new JPanel();
//		userPanel.setLayout(new BorderLayout());
//		userPanel.setBorder(new TitledBorder(null, "����� ���", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
//		userPanel.setBounds(750, 20, 200, 630);
//		this.add(userPanel);
//		userPanel.setOpaque(false);
//		
//		userList = new JList();
//		//userList.setFont(listFont);
//		userPanel.add(new JScrollPane(userList));	
//		
//		JPanel userBtnPanel = new JPanel();
//		userBtnPanel.setLayout(new GridLayout());
//		
//		rankBtn = new JButton("��ŷ����");
//		helpBtn = new JButton("����");
//		rankBtn.addActionListener(this);
//		helpBtn.addActionListener(this);
//		userBtnPanel.add(rankBtn);
//		userBtnPanel.add(helpBtn);
//		userPanel.add(userBtnPanel, BorderLayout.SOUTH);	
		// ����� ��� ����� ��
		
		// ����� ��� ����� (JTable)
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BorderLayout());
		userPanel.setBorder(new TitledBorder(null, "����� ���", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
		userPanel.setBounds(740, 20, 235, 630);
		this.add(userPanel);
		userPanel.setOpaque(false);
		
		userHeader.add("�г���");
		userHeader.add("��ġ");	
		
		tm.setDataVector(userData, userHeader);
		userTable = new JTable(tm);
		JScrollPane tableScroll = new JScrollPane(userTable);
		userPanel.add(tableScroll);	
		
		userTable.getTableHeader().setReorderingAllowed(false); // ��� �̵�(�巡��) �Ұ�
		//userTable.getTableHeader().setBackground(Color.WHITE);
		userTable.setDragEnabled(false); // ���� �巡�� �Ұ�.
		userTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // �̱� ���� ���	
		userTable.setShowGrid(false);
		userTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		userTable.setRowHeight(20);
		tableScroll.getViewport().setBackground(Color.WHITE);
		
		// ���̺� ���� ��� �����ϱ�
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); 
		dtcr.setHorizontalAlignment(SwingConstants.CENTER); 
		TableColumnModel tcm = userTable.getColumnModel(); // ������ ���̺��� �÷����� ������

		// ��ü ���� ����
		for (int j = 0; j < tcm.getColumnCount(); j++)
			tcm.getColumn(j).setCellRenderer(dtcr);		
		
		JPanel userBtnPanel = new JPanel();
		userBtnPanel.setLayout(new GridLayout(0,2));
		
		rankBtn = new JButton("��ŷ����");
		helpBtn = new JButton("����");
		quizShowBtn = new JButton("�����");
		quizAddBtn = new JButton("��������");
		rankBtn.addActionListener(this);
		helpBtn.addActionListener(this);
		quizShowBtn.addActionListener(this);
		quizAddBtn.addActionListener(this);
		userBtnPanel.add(rankBtn);
		userBtnPanel.add(helpBtn);
		userBtnPanel.add(quizShowBtn);
		userBtnPanel.add(quizAddBtn);
		userPanel.add(userBtnPanel, BorderLayout.SOUTH);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
		
		if(!e.getActionCommand().equals("����"))
			new MusicList().buttonClick();
		
		switch (name) {
		case "�� �����":			
			new Thread() {
				public void run() {
					createRoom();
				}
			}.start();			
			break;
		case "�� �����ϱ�":			
			new Thread() {
				public void run() {
					joinRoom();
				}
			}.start();	
			break;
		case "����":
			sendMsg();
			break;
		case "��ŷ����":			
			try {
				new Rank(this.getMainFrame() ,true, id);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "��ŷ���� �����߻�");
			}
			break;
		case "����":
			new HelpDialog(this.getMainFrame(), true);
			break;
		case "�����":
			try {
				new Quiz(this.getMainFrame() ,true);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "���� ��� ���� �����߻� : " + e1);
			}
			break;
		case "��������":
			
			new Quiz.addTmpQuiz(this.getMainFrame(), true);
//			String title = null, body = null;
			
//			title = JOptionPane.showInputDialog(this, "������ �����ּ���.");
//			body = JOptionPane.showInputDialog(this, "������ �����ּ���.");

//			try {
//				if (title != null && body != null)
//					Quiz.insertQuiz(title, body, Main.id, Main.nickname);
//			} catch (SQLException e1) {
//				JOptionPane.showMessageDialog(null, "���� ���� �����߻� : " + e1);
//			}

			break;
		}		
		revalidate();
		repaint();
	}

	private void sendMsg() {
		String name, msg;

		name = id;
		msg = chatSendTF.getText();

		if (msg.equals(""))
			return;

		chatOut.println(name + " : " + msg);

		chatSendTF.setText("");
	}
	
	private void sendUserMod(String userName, String where) {
		try {
			roomOut.writeUTF("(UserMod)");
			roomOut.writeUTF(userName);
			roomOut.writeUTF(where);			
		} catch (Exception e) {
			System.out.println("sendUserMod : " + e);
		}
	}

	private void createRoom() {
		String roomName = JOptionPane.showInputDialog("�� �̸��� �Է����ּ���.");
		
		for (int i = 0; i < roomVector.size(); i++) {
			GameRoom gr = (GameRoom) roomVector.get(i);
			if (roomName.equals(gr.getRoomName())) {
				JOptionPane.showMessageDialog(null, "�̹� �����ϴ� �� �̸��Դϴ�.");
				return;
			}

		}
		if (gameFrame.isVisible()) {
			JOptionPane.showMessageDialog(null, "�̹� �����Ͻ� ���� �ֽ��ϴ�.", "�������Դϴ�.", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (roomName != null) {
			try {
				roomOut.writeUTF("(CreateRoom)");
				roomOut.writeUTF(roomName);
				
				Thread.sleep(1000);
				
				for (int i = 0; i < roomVector.size(); i++) {
					GameRoom gr = (GameRoom) roomVector.get(i);
					if (roomName.equals(gr.getRoomName())) {
						roomOut.writeUTF("(JoinRoom)"+roomName);
						sendUserMod(id, gr.getRoomNum() + "����");
						
//						roomOut.writeUTF("(UserInfo)" + id);
//						roomOut.writeUTF(gr.getRoomNum()+"����");
											
						game = new Game(Main.ipAddr, gr.getRoomNum(), id);					
						gameFrame.setSize(1000, 750);
						gameFrame.setResizable(false);
						gameFrame.setLocationRelativeTo(null);
						gameFrame.add(game);
						//gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						gameFrame.setVisible(true);
						
						gameFrame.addWindowListener(new GameFrameEvent());		

					
					}
				}
				
			} catch (IOException e) {
				System.out.println("createRoom() ���� : " + e);
			} catch (InterruptedException e) {
				System.out.println("createRoom() ���� : " + e);
			} catch (Exception e) {
				System.out.println("createRoom() ���� : " + e);
			}

		}
	}

	private void joinRoom() {
		int selectRoomNum = roomList.getSelectedIndex();
		
		if (selectRoomNum == -1)
			JOptionPane.showMessageDialog(null, "���� ���� �����ϼ���.", "���� �������ּ���.", JOptionPane.WARNING_MESSAGE);

		else if (!gameFrame.isVisible()){
			GameRoom joinRoom = (GameRoom) roomVector.get(selectRoomNum);
			if (joinRoom.joinRoom()) {
				int roomNum = joinRoom.getRoomNum();
				String roomName = joinRoom.getRoomName();
				try {
					roomOut.writeUTF("(JoinRoom)"+roomName);
					sendUserMod(id, roomNum + "����");
//					roomOut.writeUTF("(UserInfo)" + id);
//					roomOut.writeUTF(roomNum+"����");
										
					game = new Game(Main.ipAddr, roomNum, id);			
					gameFrame.setSize(1000, 750);
					gameFrame.setResizable(false);
					gameFrame.setLocationRelativeTo(null);
					gameFrame.add(game);
					//gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					gameFrame.setVisible(true);
					
					gameFrame.addWindowListener(new GameFrameEvent());
					

				} catch (Exception e) {
					System.out.println("joinRoom() : " + e);
				}
			}
		} else {
			JOptionPane.showMessageDialog(null, "�̹� �����Ͻ� ���� �ֽ��ϴ�.", "�������Դϴ�.", JOptionPane.WARNING_MESSAGE);
		}
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public JFrame getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(JFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	public JButton getCreateBtn() {
		return createBtn;
	}

	public void setCreateBtn(JButton createBtn) {
		this.createBtn = createBtn;
	}

	public JButton getJoinBtn() {
		return joinBtn;
	}

	public void setJoinBtn(JButton joinBtn) {
		this.joinBtn = joinBtn;
	}

	public JButton getSendBtn() {
		return sendBtn;
	}

	public void setSendBtn(JButton sendBtn) {
		this.sendBtn = sendBtn;
	}

	public JTree getUserTree() {
		return userTree;
	}

	public void setUserTree(JTree userTree) {
		this.userTree = userTree;
	}

	public JList getRoomList() {
		return roomList;
	}

	public void setRoomList(JList roomList) {
		this.roomList = roomList;
	}

	public JList getUserList() {
		return userList;
	}

	public void setUserList(JList userList) {
		this.userList = userList;
	}

	public JTextField getChatSendTF() {
		return chatSendTF;
	}

	public void setChatSendTF(JTextField chatSendTF) {
		this.chatSendTF = chatSendTF;
	}

	public JTextArea getChatArea() {
		return chatArea;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
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

	public Vector getRoomVector() {
		return roomVector;
	}

	public void setRoomVector(Vector roomVector) {
		this.roomVector = roomVector;
	}

	public Vector getUserVector() {
		return userVector;
	}

	public void setUserVector(Vector userVector) {
		this.userVector = userVector;
	}

	public String getIpStr() {
		return ipStr;
	}

	public void setIpStr(String ipStr) {
		this.ipStr = ipStr;
	}

	public Socket getRoomSocket() {
		return roomSocket;
	}

	public void setRoomSocket(Socket roomSocket) {
		this.roomSocket = roomSocket;
	}

	public Socket getChatSocket() {
		return chatSocket;
	}

	public void setChatSocket(Socket chatSocket) {
		this.chatSocket = chatSocket;
	}

	public PrintWriter getChatOut() {
		return chatOut;
	}

	public void setChatOut(PrintWriter chatOut) {
		this.chatOut = chatOut;
	}

	public Scanner getChatIn() {
		return chatIn;
	}

	public void setChatIn(Scanner chatIn) {
		this.chatIn = chatIn;
	}

	public DataInputStream getRoomIn() {
		return roomIn;
	}

	public void setRoomIn(DataInputStream roomIn) {
		this.roomIn = roomIn;
	}

	public DataOutputStream getRoomOut() {
		return roomOut;
	}

	public void setRoomOut(DataOutputStream roomOut) {
		this.roomOut = roomOut;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashSet<String> getUserSet() {
		return userSet;
	}

	public void setUserSet(HashSet<String> userSet) {
		this.userSet = userSet;
	}

	public HashMap<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(HashMap<String, String> userMap) {
		this.userMap = userMap;
	}

//	public Font getListFont() {
//		return listFont;
//	}
//
//	public void setListFont(Font listFont) {
//		this.listFont = listFont;
//	}
	public JFrame getMainFrame() {
		return mainFrame;
	}
	public void setMainFrame(Main mainFrame) {
		this.mainFrame = mainFrame;
	}

	// Test main
	/*
	 * public static void main(String[] args) { JFrame j = new JFrame();
	 * j.setLayout(null); j.setSize(1000, 750);
	 * j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); j.add(new LoginPanel()); //
	 * j.add(new RoomPanel("127.0.0.1")); j.setLocationRelativeTo(null);
	 * j.setVisible(true);
	 * 
	 * }
	 */

	class GameFrameEvent implements WindowListener{		
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					sendUserMod(id, "����");
					
					gameFrame.getContentPane().removeAll();
					gameFrame.revalidate();
					gameFrame.repaint();
					
					game.exitGame();					
					
					
					roomOut.writeUTF("(GameOut)");					
				} catch (Exception e1) {
					System.out.println(e1 + " : e1");
				}
				gameFrame.dispose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}		
		
	}
	
	// ���
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(new ImageIcon(Main.class.getResource("img/roompanel.png")).getImage(), 0, 0, null);
	}
	
}


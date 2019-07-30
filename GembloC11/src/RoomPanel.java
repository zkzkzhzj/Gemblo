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
	// 유저 테이블
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
	
	//private Font listFont = new Font("돋움", Font.PLAIN, 15);
	
	
	public RoomPanel(String ipStr, String id) {
		this.setLayout(null);
		this.ipStr = ipStr;
		this.id = id;

		init();
		// 서버 접속
		try {
			roomSocket = new Socket(ipStr, 10002);

			roomOut = new DataOutputStream(roomSocket.getOutputStream());
			roomIn = new DataInputStream(roomSocket.getInputStream());

			chatSocket = new Socket(ipStr, 10003);
			chatOut = new PrintWriter(chatSocket.getOutputStream(), true);
			chatIn = new Scanner(chatSocket.getInputStream());

		} catch (Exception e) {
			System.out.println("RoomPanel 서버 접속 오류: " + e);
		}

		// room 쓰레드
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
					System.out.println("room쓰레드 오류 : " + e);
				}
			}
		}.start();

		// 채팅 쓰레드
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

	// init에 UI 초기 설정 하기.
	private void init() {
		setSize(1000, 750);

		// 게임 입장 방 만들기 시작
		JPanel roomListPanel = new JPanel();
		roomListPanel.setLayout(new BorderLayout());
		roomListPanel.setBorder(new TitledBorder(null, "게임방 목록", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
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
		createBtn = new JButton("방 만들기");
		createBtn.addActionListener(this);
		joinBtn = new JButton("방 입장하기");
		joinBtn.addActionListener(this);

		JPanel btnPanel = new JPanel(new GridLayout(0, 2));
		btnPanel.add(createBtn);
		btnPanel.add(joinBtn);

		roomListPanel.add(new JScrollPane(roomList));
		roomListPanel.add(btnPanel, BorderLayout.SOUTH);
		roomListPanel.setOpaque(false);

		// 게임 입장 방 만들기 끝

		// 대기실 채팅 만들기 시작
		JPanel chatPanel = new JPanel();
		chatPanel.setLayout(new BorderLayout());
		chatPanel.setBorder(new TitledBorder(null, "대기실 채팅방", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
		chatPanel.setBounds(10, 350, 700, 300);
		this.add(chatPanel);
		chatPanel.setOpaque(false);

		chatSendTF = new JTextField();
		chatSendTF.setActionCommand("전송");
		chatSendTF.addActionListener(this);

		chatArea = new JTextArea();
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);

		chatAreaScroll = new JScrollPane(chatArea);
		chatAreaScrollBar = chatAreaScroll.getVerticalScrollBar();

		sendBtn = new JButton("전송");
		sendBtn.addActionListener(this);

		JPanel sendPanel = new JPanel();
		sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.X_AXIS));

		chatPanel.add(chatAreaScroll);
		chatPanel.add(sendPanel, BorderLayout.SOUTH);

		sendPanel.add(chatSendTF);
		sendPanel.add(sendBtn);
		// 대기실 채팅 만들기 끝

		// 사용자 목록 만들기 시작 (JList)
//		JPanel userPanel = new JPanel();
//		userPanel.setLayout(new BorderLayout());
//		userPanel.setBorder(new TitledBorder(null, "사용자 목록", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
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
//		rankBtn = new JButton("랭킹보기");
//		helpBtn = new JButton("도움말");
//		rankBtn.addActionListener(this);
//		helpBtn.addActionListener(this);
//		userBtnPanel.add(rankBtn);
//		userBtnPanel.add(helpBtn);
//		userPanel.add(userBtnPanel, BorderLayout.SOUTH);	
		// 사용자 목록 만들기 끝
		
		// 사용자 목록 만들기 (JTable)
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new BorderLayout());
		userPanel.setBorder(new TitledBorder(null, "사용자 목록", TitledBorder.TOP, TitledBorder.CENTER, Main.BinggraeBoldFont.deriveFont(18f)));
		userPanel.setBounds(740, 20, 235, 630);
		this.add(userPanel);
		userPanel.setOpaque(false);
		
		userHeader.add("닉네임");
		userHeader.add("위치");	
		
		tm.setDataVector(userData, userHeader);
		userTable = new JTable(tm);
		JScrollPane tableScroll = new JScrollPane(userTable);
		userPanel.add(tableScroll);	
		
		userTable.getTableHeader().setReorderingAllowed(false); // 헤드 이동(드래그) 불가
		//userTable.getTableHeader().setBackground(Color.WHITE);
		userTable.setDragEnabled(false); // 선택 드래그 불가.
		userTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 싱글 선택 모드	
		userTable.setShowGrid(false);
		userTable.getColumnModel().getColumn(0).setPreferredWidth(180);
		userTable.setRowHeight(20);
		tableScroll.getViewport().setBackground(Color.WHITE);
		
		// 테이블 내용 가운데 정렬하기
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); 
		dtcr.setHorizontalAlignment(SwingConstants.CENTER); 
		TableColumnModel tcm = userTable.getColumnModel(); // 정렬할 테이블의 컬럼모델을 가져옴

		// 전체 열에 지정
		for (int j = 0; j < tcm.getColumnCount(); j++)
			tcm.getColumn(j).setCellRenderer(dtcr);		
		
		JPanel userBtnPanel = new JPanel();
		userBtnPanel.setLayout(new GridLayout(0,2));
		
		rankBtn = new JButton("랭킹보기");
		helpBtn = new JButton("도움말");
		quizShowBtn = new JButton("퀴즈보기");
		quizAddBtn = new JButton("퀴즈제출");
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
		
		if(!e.getActionCommand().equals("전송"))
			new MusicList().buttonClick();
		
		switch (name) {
		case "방 만들기":			
			new Thread() {
				public void run() {
					createRoom();
				}
			}.start();			
			break;
		case "방 입장하기":			
			new Thread() {
				public void run() {
					joinRoom();
				}
			}.start();	
			break;
		case "전송":
			sendMsg();
			break;
		case "랭킹보기":			
			try {
				new Rank(this.getMainFrame() ,true, id);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "랭킹보기 오류발생");
			}
			break;
		case "도움말":
			new HelpDialog(this.getMainFrame(), true);
			break;
		case "퀴즈보기":
			try {
				new Quiz(this.getMainFrame() ,true);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "퀴즈 목록 보기 오류발생 : " + e1);
			}
			break;
		case "퀴즈제출":
			
			new Quiz.addTmpQuiz(this.getMainFrame(), true);
//			String title = null, body = null;
			
//			title = JOptionPane.showInputDialog(this, "문제를 적어주세요.");
//			body = JOptionPane.showInputDialog(this, "정답을 적어주세요.");

//			try {
//				if (title != null && body != null)
//					Quiz.insertQuiz(title, body, Main.id, Main.nickname);
//			} catch (SQLException e1) {
//				JOptionPane.showMessageDialog(null, "퀴즈 제출 오류발생 : " + e1);
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
		String roomName = JOptionPane.showInputDialog("방 이름을 입력해주세요.");
		
		for (int i = 0; i < roomVector.size(); i++) {
			GameRoom gr = (GameRoom) roomVector.get(i);
			if (roomName.equals(gr.getRoomName())) {
				JOptionPane.showMessageDialog(null, "이미 존재하는 방 이름입니다.");
				return;
			}

		}
		if (gameFrame.isVisible()) {
			JOptionPane.showMessageDialog(null, "이미 참가하신 방이 있습니다.", "게임중입니다.", JOptionPane.WARNING_MESSAGE);
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
						sendUserMod(id, gr.getRoomNum() + "번방");
						
//						roomOut.writeUTF("(UserInfo)" + id);
//						roomOut.writeUTF(gr.getRoomNum()+"번방");
											
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
				System.out.println("createRoom() 오류 : " + e);
			} catch (InterruptedException e) {
				System.out.println("createRoom() 오류 : " + e);
			} catch (Exception e) {
				System.out.println("createRoom() 오류 : " + e);
			}

		}
	}

	private void joinRoom() {
		int selectRoomNum = roomList.getSelectedIndex();
		
		if (selectRoomNum == -1)
			JOptionPane.showMessageDialog(null, "들어가실 방을 선택하세요.", "방을 선택해주세요.", JOptionPane.WARNING_MESSAGE);

		else if (!gameFrame.isVisible()){
			GameRoom joinRoom = (GameRoom) roomVector.get(selectRoomNum);
			if (joinRoom.joinRoom()) {
				int roomNum = joinRoom.getRoomNum();
				String roomName = joinRoom.getRoomName();
				try {
					roomOut.writeUTF("(JoinRoom)"+roomName);
					sendUserMod(id, roomNum + "번방");
//					roomOut.writeUTF("(UserInfo)" + id);
//					roomOut.writeUTF(roomNum+"번방");
										
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
			JOptionPane.showMessageDialog(null, "이미 참가하신 방이 있습니다.", "게임중입니다.", JOptionPane.WARNING_MESSAGE);
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
					sendUserMod(id, "대기실");
					
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
	
	// 배경
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(new ImageIcon(Main.class.getResource("img/roompanel.png")).getImage(), 0, 0, null);
	}
	
}


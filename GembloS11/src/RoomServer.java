import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class RoomServer {

	private ServerSocket serverSocket;
	private Socket socket;
	private RoomManager roomManager = new RoomManager();
	private ArrayList<GameRoom> roomList = new ArrayList<>();
	private ArrayList<ServerCli> serverList = new ArrayList<>();
	private ArrayList<Socket> socketList = new ArrayList<>();
	private ArrayList<String> userList = new ArrayList<>();
	private HashMap<String, String> userMap = new HashMap<>();
	private String id;
	
	
	
	public RoomServer() {
		try {
			serverSocket = new ServerSocket(10002);
			System.out.println(
					"MyIP : " + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort());

//			// �Ƹ��� ��ǻ�� ���ɿ� ���� ���� ������ �ʹݿ� ���� ���Ѵ�. �ֱ������� ����� ����� ����.
//			// room ��� �˻� ������ TODO ���� �����Ͱ� ������ �ּ�ó�� �ߴ�.
//			new Thread() {
//				public void run() {
//					GameRoom gr;
//					ServerCli sc;
//					int rn;
//					
//					while(true) {
//						try {
//							for(int i=0; i<roomList.size(); i++) {
//								gr = roomList.get(i);
//								sc = serverList.get(i);
//								
//								rn = gr.getRoomNum();
//								
//								if(sc.getChatServer().getChatManager().size() == 0 || sc.getManager().size() == 0) {
//									roomManager.sendGameOut(gr.getRoomName());									
//									roomList.remove(gr);
//									sc.stop();
//									serverList.remove(sc);
//								}
//								
//							}
//							roomManager.sendUserInfo();
//							this.sleep(10000); // 10�ʿ� �ѹ� �˻�
//						} catch (Exception e) {
//							
//						}
//					}
//				}
//			}.start();
			
			// TODO ����� �����°� ó���ϱ�. ���� �����Ͱ� ���� �� 
			
			// serverSocket ���� ������
			new Thread() {
				public void run() {
					try {
						while (true) {
							Socket newSocket = serverSocket.accept();

							roomManager.add(newSocket);
							String clientIP = newSocket.getInetAddress().getHostAddress();
							int clientPort = newSocket.getPort();

							// room ���� ������
							new Thread() {
								public void run() {
									DataInputStream roomIn = null;
									String userName = null;
									try {
										roomIn = new DataInputStream(newSocket.getInputStream());
										String str;
										String roomName;										
										String where;
										
										GameRoom gr;
										ServerCli sc;
										
										socketList.add(newSocket);
									//	userName = roomIn.readUTF();
									//	roomManager.addUser(userName, false);
//												TODO ���� ���� ������ 2���� ����
										roomManager.sendStart(newSocket);
										roomManager.sendUserStartInfo(newSocket);
//										roomManager.sendUserInfo();
										
										while (true) {
											str = roomIn.readUTF();								
											
											if(str.equals("(UserAdd)")) {
												userName = roomIn.readUTF();
												roomManager.addUser(userName, "����");
												roomManager.sendUserAdd(userName);
												roomManager.sendUserInfo();
											} else if (str.equals("(UserMod)")) {
												userName = roomIn.readUTF();
												where = roomIn.readUTF();
												roomManager.modUser(userName, where);
												roomManager.sendUserMod(userName, where);
												roomManager.sendUserInfo();
											} else if(str.startsWith("(UserInfo)")) {
//												userName = str.substring(10);
//												where = roomIn.readUTF();												
												roomManager.sendUserInfo();
											} else if(str.startsWith("(JoinRoom)")) {
												roomName = str.substring(10);
												roomManager.sendJoinInfo(roomName);
											} else if(str.startsWith("(UserOut)")) {
												userName = str.substring(9);
												roomManager.removeUser(userName);
												roomManager.sendUserOut(userName);
												roomManager.sendUserInfo();
											} else if(str.startsWith("(CreateRoom)")){
												roomName = roomIn.readUTF();
												roomManager.sendToAll(roomName);
											} else if(str.equals("(GameOut)")) {
												// ��� �˻� ����
												for(int i=0; i<roomList.size(); i++) {
													gr = roomList.get(i);
													sc = serverList.get(i);												
													
													if(sc.getChatServer().getChatManager().size() == 0 || sc.getManager().size() == 0) {
														roomManager.sendGameOut(gr.getRoomName());									
														roomList.remove(gr);
														sc.interrupt();
														serverList.remove(sc);
													}
													
												}
												// ��
											}
											

											
										}
									} catch (Exception e) {
										System.out.println(e + " : room ����(run)");
										System.out.println(e.getLocalizedMessage());
									}finally {
										try {
											roomManager.remove(newSocket);
											roomManager.removeUser(userName);
											//roomManager.sendUserOut(userName);
											if (roomIn != null)
												roomIn.close();
											if (newSocket != null)
												newSocket.close();											
										} catch (Exception e) {
											System.out.println(e + " : serverSocket ���� ������(run(finally)");
										}
									}
								}
							}.start();

							System.out.println(clientIP + ":" + clientPort + " �����߽��ϴ�.");
						}
					} catch (IOException e) {
						System.out.println("RoomServer serverSocket.accept() ���� : " + e);
					}
				}
			}.start();

			// chatServer ���� ������
			new ChatServer().start();
			
			// room ��� �˻� ������
//			new Thread() {
//				@Override
//				public void run() {
//					GameRoom gr;
//					ServerCli sc;
//					int rn;
//					
//					while(true) {
//						try {
//							for(int i=0; i<roomList.size(); i++) {
//								gr = roomList.get(i);
//								sc = serverList.get(i);
//								
//								rn = gr.getRoomNum();
//								
//								if(sc.getChatServer().getChatManager().size() == 0 || sc.getManager().size() == 0) {
//									roomManager.sendGameOut(gr.getRoomName());									
//									roomList.remove(gr);
//									sc.stop();
//									serverList.remove(sc);
//								}
//								
//							}
//							
//							this.sleep(10000); // 10�ʿ� �ѹ� �˻�
//						} catch (Exception e) {
//							
//						}
//					}
//				}
//			}.start();
			
		} catch (Exception e) {
			System.out.println("RoomServer ���� �� ���� : " + e);
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
				server = new ServerSocket(10003);
				System.out.println("ä�� ���� ���� ����");

				while (true) {
					Socket socket = server.accept();
					System.out.println("ä�ü��� ����� ���");
					chatManager.add(socket);
					new Chat(socket).start();
				}

			} catch (Exception e) {
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
						if ((message = reader.readLine()) != null) {
							chatManager.sendToAll(message);
						}
					}
				} catch (Exception e) {
					System.out.println(e + " : Chat(run)");
				} finally {
					try {
						chatManager.remove(socket);
						if (reader != null)
							reader.close();
						if (socket != null)
							socket.close();
					} catch (Exception e) {
						System.out.println(e + " : Chat(run(finally)");
					}
				}
			}
		}
	}

	private class ChatManager extends Vector {
		void add(Socket socket) {
			super.add(socket);
		}

		void remove(Socket socket) {
			super.remove(socket);
		}

		synchronized void sendToAll(String msg) {
			PrintWriter writer = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);

				try {
					writer = new PrintWriter(socket.getOutputStream(), true);
				} catch (Exception e) {
					System.out.println(e + " : ChatManager(sendToAll)");
				}
				if (writer != null)
					writer.println(msg);
			}
		}
	}

	private class RoomManager extends Vector {
		private int roomNum;

		public void setRoomNum(int roomNum) {
			this.roomNum = roomNum;
		}

		public int getRoomNum() {
			return roomNum;
		}

		public void add(Socket socket) {
			super.add(socket);
		}

		public void remove(Socket socket) {
			super.remove(socket);
		}

		synchronized void sendStart(Socket socket) {
			DataOutputStream out = null;
			GameRoom gr;

			try {
				out = new DataOutputStream(socket.getOutputStream());
				for (int i = 0; i < roomList.size(); i++) {
					gr = roomList.get(i);
					if (out != null) {
						out.writeUTF("(CreateRoom)");
						out.writeUTF(gr.getRoomName());
						out.writeInt(gr.getRoomCnt());
						out.writeInt(gr.getRoomNum());
						out.writeInt(gr.getPlayerNum());
					}
				}
			} catch (Exception e) {
				System.out.println(e + " : ChatManager(sendStart)");
			}
		}
		
		synchronized void sendGameOut(String roomName) {
			DataOutputStream out = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {
						
							out.writeUTF("(GameOut)");
							out.writeUTF(roomName);
						
					}

				} catch (Exception e) {
					System.out.println(e + " : RoomManager(sendGameOut)");
				}
			}
		}
		
		synchronized void sendJoinInfo(String roomName) {
			DataOutputStream out = null;
			Socket socket;
			GameRoom gr = null;
			
			for(int i=0; i<roomList.size(); i++)
				if(roomList.get(i).getRoomName().equals(roomName)) {
					gr = roomList.get(i);
					gr.setPlayerNum(gr.getPlayerNum()+1);
					break;
				}
			
//			System.out.println(gr.getRoomName() +" : gr");
//			System.out.println(roomName + " : rn");
//			System.out.println(gr.getPlayerNum() + " : pn");

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null && gr != null) {
						out.writeUTF("(JoinRoom)"+gr.getRoomName());
						out.writeInt(gr.getRoomCnt());
						out.writeInt(gr.getRoomNum());
						out.writeInt(gr.getPlayerNum());						
					}

				} catch (Exception e) {
					System.out.println(e + " : ChatManager(sendToAll)");
				}
			}
		}
		
		void modUser(String userName, String where) {
			userMap.replace(userName, where);
		}
		
		void addUser(String userName, String where) {
			userList.add(userName);
			userMap.put(userName, where);
		}
		
		void removeUser(String userName) {
			userList.remove(userName);
			userMap.remove(userName);
		}
		
		synchronized void sendUserStartInfo(Socket newSocket) {
			DataOutputStream out = null;

			String userName;
			String where;

			try {
				out = new DataOutputStream(newSocket.getOutputStream());
				if (out != null) {
					for (int j = 0; j < userList.size(); j++) {
						userName = userList.get(j);
						where = userMap.get(userName);

						out.writeUTF("(UserAdd)");
						out.writeUTF(userName);
						out.writeUTF(where);
					}
				}

			} catch (Exception e) {
				System.out.println(e + " : RoomManager(sendUserStartInfo)");
			}

		}
		
		synchronized void sendUserAdd(String userName) {
			DataOutputStream out = null;
			Socket socket;
			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {						
						out.writeUTF("(UserAdd)");
						out.writeUTF(userName);
						out.writeUTF("����");
					}

				} catch (Exception e) {
					System.out.println(e + " : RoomManager(sendUserAdd)");
				}
			}
		}
		
		synchronized void sendUserMod (String userName, String where) {
			DataOutputStream out = null;
			Socket socket;
			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {						
						out.writeUTF("(UserMod)");
						out.writeUTF(userName);
						out.writeUTF(where);
					}

				} catch (Exception e) {
					System.out.println(e + " : RoomManager(sendUserMod)");
				}
			}
		}
		
		synchronized void sendUserOut(String outUser) {
			DataOutputStream out = null;
			Socket socket;
			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {						
						out.writeUTF("(UserOut)");
						out.writeUTF(outUser);
						out.writeUTF("OUT");
					}

				} catch (Exception e) {
					System.out.println(e + " : ChatManager(sendUserOut)");
				}
			}
		}
		
		synchronized void sendUserInfo() {
			DataOutputStream out = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {
//						for(int j=0; j<userList.size(); j++) {
//							String sendUserName = userList.get(j);
							out.writeUTF("(UserInfo)");
//							out.writeUTF(userMap.get(sendUserName));
//						}
					}

				} catch (Exception e) {
					System.out.println(e + " : ChatManager(sendUserInfo)");
				}
			}
		}

		synchronized void sendToAll(String roomName) {			
			DataOutputStream out = null;
			Socket socket;
			GameRoom gr = new GameRoom(roomName);
			roomList.add(gr);
			ServerCli gameServer = new ServerCli(gr.getRoomNum());
			serverList.add(gameServer);
			gameServer.start();
			

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);
				try {
					out = new DataOutputStream(socket.getOutputStream());
					if (out != null) {
						out.writeUTF("(CreateRoom)");
						out.writeUTF(gr.getRoomName());
						out.writeInt(gr.getRoomCnt());
						out.writeInt(gr.getRoomNum());
						out.writeInt(gr.getPlayerNum());
					}

				} catch (Exception e) {
					System.out.println(e + " : ChatManager(sendToAll)");
				}
			}
		}
	}
	
	private class UserManager extends Vector {
		
	}
	//main
	public static void main(String[] args) {
		new RoomServer();
	}

}

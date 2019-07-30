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

//			// 아마도 컴퓨터 성능에 따라 유저 정보를 초반에 받지 못한다. 주기적으로 사용자 목록을 전송.
//			// room 빈방 검사 쓰레드 TODO 지금 데이터가 꼬여서 주석처리 했다.
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
//							this.sleep(10000); // 10초에 한번 검사
//						} catch (Exception e) {
//							
//						}
//					}
//				}
//			}.start();
			
			// TODO 사용자 나가는거 처리하기. 지금 데이터가 꼬임 ㅠ 
			
			// serverSocket 접속 쓰레드
			new Thread() {
				public void run() {
					try {
						while (true) {
							Socket newSocket = serverSocket.accept();

							roomManager.add(newSocket);
							String clientIP = newSocket.getInetAddress().getHostAddress();
							int clientPort = newSocket.getPort();

							// room 전송 쓰레드
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
//												TODO 지금 유저 정보가 2번씩 읽힘
										roomManager.sendStart(newSocket);
										roomManager.sendUserStartInfo(newSocket);
//										roomManager.sendUserInfo();
										
										while (true) {
											str = roomIn.readUTF();								
											
											if(str.equals("(UserAdd)")) {
												userName = roomIn.readUTF();
												roomManager.addUser(userName, "대기실");
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
												// 빈방 검사 시작
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
												// 끝
											}
											

											
										}
									} catch (Exception e) {
										System.out.println(e + " : room 전송(run)");
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
											System.out.println(e + " : serverSocket 접속 쓰레드(run(finally)");
										}
									}
								}
							}.start();

							System.out.println(clientIP + ":" + clientPort + " 접속했습니다.");
						}
					} catch (IOException e) {
						System.out.println("RoomServer serverSocket.accept() 오류 : " + e);
					}
				}
			}.start();

			// chatServer 접속 쓰레드
			new ChatServer().start();
			
			// room 빈방 검사 쓰레드
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
//							this.sleep(10000); // 10초에 한번 검사
//						} catch (Exception e) {
//							
//						}
//					}
//				}
//			}.start();
			
		} catch (Exception e) {
			System.out.println("RoomServer 생성 중 오류 : " + e);
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
				System.out.println("채팅 서버 소켓 생성");

				while (true) {
					Socket socket = server.accept();
					System.out.println("채팅서버 사용자 들옴");
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
						out.writeUTF("대기실");
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

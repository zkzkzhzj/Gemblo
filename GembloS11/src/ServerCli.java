import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;

public class ServerCli extends Thread {

	private ServerSocket serverSocket;
	private Manager manager = new Manager();
	// private Socket socket = null;
	private DataOutputStream out = null;
	private int player;
	private String gemColor[] = { "Y", "R", "B", "G" };

	private LinkedHashMap<String, Socket> socketMap = new LinkedHashMap<>();
	private LinkedHashMap<Socket, String> stringMap = new LinkedHashMap<>();
	private String[] colorArray = { "Y", "R", "B", "G" };
//	private String[] colorName = { "Y", "R", "B", "G" };
	private String[] colorName = { "Yellow", "Red", "Blue", "Green" };
	
	// 항복한놈들 확인
	private HashSet<String> surrenderSet = new HashSet<>();

	ArrayList<Socket> removeSocket = new ArrayList<Socket>();
	private ChatServer chatServer;

	private int roomNum;
	
	private String whoIsTurn = "Y";

	public ServerCli(int roomNum) {
		this.roomNum = roomNum;
	}

	private class SeverSetting extends Thread {
		int roomNum;

		public SeverSetting(int roomNum) {
			this.roomNum = roomNum;
		}

		@Override
		public void run() {
			try { // server 세팅
				serverSocket = new ServerSocket(10004 + roomNum);
				System.out.println("게임 서버 소켓 생성 방 번호:" + roomNum);

				for (int i = 0; i < player; i++) {
					Socket socket = serverSocket.accept();
					manager.add(socket);
					removeSocket.add(socket);
					
					out = new DataOutputStream(socket.getOutputStream());
					DataInputStream in = new DataInputStream(socket.getInputStream());
					
					System.out.println((i + 1) + "번 째 플레이어 접속. " + (player - i - 1) + "명 남았습니다.");
					out.writeUTF(gemColor[i]);
					out.writeInt(player);			
					
					String nickname = in.readUTF();
					socketMap.put(colorArray[i], socket);
					stringMap.put(socket, colorArray[i] + nickname + "(" + colorName[i] +")");
					
					manager.sendJoinInfo(manager.size());
					new InData((Socket) manager.get(i)).start();
				}

//				for (int i = 0; i < player; i++)
//					new InData((Socket) manager.get(i)).start();

			} catch (Exception e) {
				System.out.println(e + " : 포트가 안 열렸습니다.");
			}
		}
	}

	class Manager extends Vector {

		void add(Socket socket) {
			super.add(socket);
		}

		void remove(Socket socket) {
			super.remove(socket);
		}
		
		synchronized void sendGameEnd() {
			DataOutputStream out = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);

				try {
					out = new DataOutputStream(socket.getOutputStream());
					
					out.writeUTF("(EndGame)");
				} catch (Exception e) {
					System.out.println(e + " : sendGameEnd()");
				}					
			}			
		}
		
		synchronized void sendJoinInfo(int num) {
			DataOutputStream out = null;
			Socket socket;

			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);

				try {
					out = new DataOutputStream(socket.getOutputStream());
					
					out.writeUTF("(JoinInfo)");
					out.writeInt(num);	
				} catch (Exception e) {
					System.out.println(e + " : sendJoinINfo(sendToAll)");
				}					
			}
			
		}

		synchronized void sendToAll(String info, int x, int y, boolean isGem, String gem, int score, boolean isEnd) {
			DataOutputStream out = null;
			Socket socket, nextSocket;
			boolean endCheck[] = new boolean[player];
			char endChar[] = new char[player];
			String gemStr = "";
			int gemStrCnt = 0;

			// 다음 차례 정보 만들기
			for (int i = 0; i < removeSocket.size(); i++) {
				socket = removeSocket.get(i);

				if (i + 1 < removeSocket.size())
					nextSocket = removeSocket.get(i + 1);
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
			
			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);

				if (i + 1 < stringMap.size())
					nextSocket = (Socket) elementAt(i + 1);
				else
					nextSocket = (Socket) elementAt(0);

				try {
					out = new DataOutputStream(socket.getOutputStream());

					if (i + 1 < stringMap.size())
						nextSocket = (Socket) elementAt(i + 1);
					else
						nextSocket = (Socket) elementAt(0);
					
					//TODO Mine
					if(info.equals("(Mine)")) {
						out.writeUTF("(Mine)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
					} else if(info.equals("(TimeOver)")) {
						out.writeUTF("(TimeOver)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
					}else if (info.equals("(AutoEnd)")) {
						out.writeUTF("(AutoEnd)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");						
						whoIsTurn = gemStr;
						
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
					} else if (info.equals("(Surrender)")) {
						out.writeUTF("(Surrender)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
						
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
					}
					else if (info.equals("(EndTurn)")) {
						out.writeUTF("(EndTurn)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
						
						// TODO 계속 차례 넘기기
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
						
					} else if (info.equals("(OUT)")) {
						// TODO 자기 차례 아닐때 나간사람 처리하기.
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
						
//						System.out.println(whoIsTurn + " : who");
//						System.out.println(gem + " : gem");
//						System.out.println(gemStr + " : gemStr");
//						
						// 밑에 있음
//						if(whoIsTurn.charAt(0) == gem.charAt(0)) {
//							out.writeUTF("(OUT)");
//							out.writeUTF(gem);
//							out.writeUTF(gemStr + " TURN");
//							whoIsTurn = gemStr;			
//						}
//						
						// 다음 차례 정보 만들기
						for (int j = 0; j < removeSocket.size(); j++) {
							socket = removeSocket.get(j);

							if (j + 1 < removeSocket.size())
								nextSocket = removeSocket.get(j + 1);
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
						
						
						out.writeUTF("(OUT)");
						out.writeUTF(gem);
						if(whoIsTurn.charAt(0) == gem.charAt(0)) {
							out.writeUTF(gemStr + " TURN");
							whoIsTurn = gemStr;
						}
						else {
							out.writeUTF(whoIsTurn + " TURN");
						}			
					} else if (info.equals("(GameInfo)")) {

						out.writeUTF("(GameInfo)");
						out.writeInt(x);
						out.writeInt(y);
						out.writeBoolean(isGem);
						out.writeUTF(gem);
						out.writeInt(score);
						out.writeBoolean(isEnd);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
						char gemColor = gem.charAt(0);

						// 차례 변경 및 다음 차례 정보 넘기기

						if (isEnd) {
							Socket s = null;
							String str = null;
							if (gemColor == 'Y') {
								s = socketMap.get("Y");
								str = stringMap.get(s);
							} else if (gemColor == 'R') {
								s = socketMap.get("R");
								str = stringMap.get(s);
							} else if (gemColor == 'B') {
								s = socketMap.get("B");
								str = stringMap.get(s);
							} else if (gemColor == 'G') {
								s = socketMap.get("G");
								str = stringMap.get(s);
							}

							if (s != null && str != null) {
								// this.remove(s); //관전은 가능하게 주석처리 해봤음
								stringMap.remove(s);
								socketMap.remove(str);
								removeSocket.remove(s);
							}
						}
					}
					
//					if(endCnt >= 3) {
//						out.writeUTF("(EndGame)");
//					}
					
				} catch (Exception e) {
					System.out.println(e + " : 데이터 전송 과정에서 오류");
				}
			}
		}
	
		
		synchronized void sendToAll(String info, String x, String y, String isGem, String gem, int score, boolean isEnd) {
			DataOutputStream out = null;
			Socket socket, nextSocket;
			boolean endCheck[] = new boolean[player];
			char endChar[] = new char[player];
			String gemStr = "";
			int gemStrCnt = 0;

			// 다음 차례 정보 만들기
			for (int i = 0; i < removeSocket.size(); i++) {
				socket = removeSocket.get(i);

				if (i + 1 < removeSocket.size())
					nextSocket = removeSocket.get(i + 1);
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
			
			for (int i = 0; i < size(); i++) {
				socket = (Socket) elementAt(i);

				if (i + 1 < stringMap.size())
					nextSocket = (Socket) elementAt(i + 1);
				else
					nextSocket = (Socket) elementAt(0);

				try {
					out = new DataOutputStream(socket.getOutputStream());

					if (i + 1 < stringMap.size())
						nextSocket = (Socket) elementAt(i + 1);
					else
						nextSocket = (Socket) elementAt(0);
					
					//TODO Mine
					if(info.equals("(Mine)")) {
						out.writeUTF("(Mine)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
					} else if(info.equals("(TimeOver)")) {
						out.writeUTF("(TimeOver)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
					} else if (info.equals("(AutoEnd)")) {
						out.writeUTF("(AutoEnd)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");						
						whoIsTurn = gemStr;
						
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
					}
					else if (info.equals("(EndTurn)")) {
						out.writeUTF("(EndTurn)");
						out.writeUTF(gem);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
						
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
						
					} else if (info.equals("(OUT)")) {
						// TODO 자기 차례 아닐때 나간사람 처리하기.
						Socket s = null;
						String str = null;
						char gemColor = gem.charAt(0);
						
						if (gemColor == 'Y') {
							s = socketMap.get("Y");
							str = stringMap.get(s);
						} else if (gemColor == 'R') {
							s = socketMap.get("R");
							str = stringMap.get(s);
						} else if (gemColor == 'B') {
							s = socketMap.get("B");
							str = stringMap.get(s);
						} else if (gemColor == 'G') {
							s = socketMap.get("G");
							str = stringMap.get(s);
						}

						if (s != null && str != null) {
							// this.remove(s); //관전은 가능하게 주석처리 해봤음
							stringMap.remove(s);
							socketMap.remove(str);
							removeSocket.remove(s);
						}
						
//						System.out.println(whoIsTurn + " : who");
//						System.out.println(gem + " : gem");
//						System.out.println(gemStr + " : gemStr");
//						
						// 밑에 있음
//						if(whoIsTurn.charAt(0) == gem.charAt(0)) {
//							out.writeUTF("(OUT)");
//							out.writeUTF(gem);
//							out.writeUTF(gemStr + " TURN");
//							whoIsTurn = gemStr;			
//						}
//						
						// 다음 차례 정보 만들기
						for (int j = 0; j < removeSocket.size(); j++) {
							socket = removeSocket.get(j);

							if (j + 1 < removeSocket.size())
								nextSocket = removeSocket.get(j + 1);
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
						
						
						out.writeUTF("(OUT)");
						out.writeUTF(gem);
						if(whoIsTurn.charAt(0) == gem.charAt(0)) {
							out.writeUTF(gemStr + " TURN");
							whoIsTurn = gemStr;
						}
						else {
							out.writeUTF(whoIsTurn + " TURN");
						}			
					} else if (info.equals("(GameInfo)")) {

						out.writeUTF("(GameInfo)");
						out.writeUTF(x);
						out.writeUTF(y);
						out.writeUTF(isGem);
						out.writeUTF(gem);
						out.writeInt(score);
						out.writeBoolean(isEnd);
						out.writeUTF(gemStr + " TURN");
						whoIsTurn = gemStr;
						char gemColor = gem.charAt(0);

						// 차례 변경 및 다음 차례 정보 넘기기

						if (isEnd) {
							Socket s = null;
							String str = null;
							if (gemColor == 'Y') {
								s = socketMap.get("Y");
								str = stringMap.get(s);
							} else if (gemColor == 'R') {
								s = socketMap.get("R");
								str = stringMap.get(s);
							} else if (gemColor == 'B') {
								s = socketMap.get("B");
								str = stringMap.get(s);
							} else if (gemColor == 'G') {
								s = socketMap.get("G");
								str = stringMap.get(s);
							}

							if (s != null && str != null) {
								// this.remove(s); //관전은 가능하게 주석처리 해봤음
								stringMap.remove(s);
								socketMap.remove(str);
								removeSocket.remove(s);
							}
						}
					}
					
//					if(endCnt >= 3) {
//						out.writeUTF("(EndGame)");
//					}
					
				} catch (Exception e) {
					System.out.println(e + " : 데이터 전송 과정에서 오류");
				}
			}
		}
	
	
	}

	class InData extends Thread {
		private Socket socket = null;
		private DataInputStream in = null;
		private DataOutputStream out = null;

		public InData(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				in = new DataInputStream(socket.getInputStream());
				int x, y, score;
				boolean isGem, isEnd;
				String gem, info;
				String xStr, yStr, isGemStr;

				while (true) {
					info = in.readUTF();
					if(info.equals("(Mine)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
					} else if (info.equals("(AutoEnd)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
						surrenderSet.add(gem);
					} else if (info.equals("(Surrender)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
						surrenderSet.add(gem);
					}
					else if (info.equals("(OUT)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
						surrenderSet.add(gem);
					} else if (info.equals("(EndTurn)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
						surrenderSet.add(gem);
					} else if (info.equals(("(Exit)"))) {
						break;
					} else if(info.equals("(GameInfo)")) {						
						xStr = in.readUTF();
						yStr = in.readUTF();
						isGemStr = in.readUTF();
						gem = in.readUTF();
						score = in.readInt();
						isEnd = in.readBoolean();	
						manager.sendToAll(info, xStr, yStr, isGemStr, gem, score, isEnd);
					} else if(info.equals("(TimeOver)")) {
						gem = in.readUTF();
						manager.sendToAll(info, 0, 0, false, gem, 0, false);
					}
					
					if(surrenderSet.size() >= 3) {
						manager.sendGameEnd();
						break;
					}
					
				}

			} catch (Exception e) {
				System.out.println(e + " : 데이터 전송 과정에서 오류");
			} finally {
				try {
					manager.remove(socket);
					if (in != null)
						in.close();
					if (out != null)
						out.close();
					if (socket != null)
						socket.close();
					System.out.println("클라이언트 나감");
				} catch (Exception e) {
					System.out.println(e + " : 클라이언트 나가는 과정에서 오류");
				}
			}
		}
	}

	@Override
	public void run() {
		player = 4;
		chatServer = new ChatServer();

		chatServer.start();

		new SeverSetting(roomNum).start();
	}

	class ChatManager extends Vector {
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

	class ChatServer extends Thread {
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
				server = new ServerSocket(50005 + roomNum);
				System.out.println("게임채팅 서버 소켓 생성");

				while (true) {
					Socket socket = server.accept();
					System.out.println("게임채팅서버 사용자 들옴");
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

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public String[] getGemColor() {
		return gemColor;
	}

	public void setGemColor(String[] gemColor) {
		this.gemColor = gemColor;
	}

	public LinkedHashMap<String, Socket> getSocketMap() {
		return socketMap;
	}

	public void setSocketMap(LinkedHashMap<String, Socket> socketMap) {
		this.socketMap = socketMap;
	}

	public LinkedHashMap<Socket, String> getStringMap() {
		return stringMap;
	}

	public void setStringMap(LinkedHashMap<Socket, String> stringMap) {
		this.stringMap = stringMap;
	}

	public String[] getColorArray() {
		return colorArray;
	}

	public void setColorArray(String[] colorArray) {
		this.colorArray = colorArray;
	}

	public String[] getColorName() {
		return colorName;
	}

	public void setColorName(String[] colorName) {
		this.colorName = colorName;
	}

	public ArrayList<Socket> getRemoveSocket() {
		return removeSocket;
	}

	public void setRemoveSocket(ArrayList<Socket> removeSocket) {
		this.removeSocket = removeSocket;
	}

	public ChatServer getChatServer() {
		return chatServer;
	}

	public void setChatServer(ChatServer chatServer) {
		this.chatServer = chatServer;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}
	
	

}

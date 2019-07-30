import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sun.javafx.font.Disposer;
/*
 * 이곳에는 게임화면 및 리스너들이 들어올것이다. 보석, 타일, 안내문구화면, 채팅기능(?) 등을 넣을것.
 * tileList는 타일들을 가지고 있다.
 * gemList는 보석들을 가지고 있다.
 * radius는 육각형의 반지름이다.
 * redGem은 RED보석의 이미지를 갖는다.
 * guideMsg에다가 트라이 캐치에서 캐치부분에 나오는 오류들을 잘 풀어서 안내할 예정이다.
 * guideMsg는 나중에 그래픽이나 JLabel으로 변환 할 생각도 있다.(g.draw....)
 */

//TODO 4인 플레이 누구 한명이 자동항복 되니 시간초과도 같이 뜨면서 TURN이 안넘어가는 오류가 있다.
// 자동항복 아마도 해결.

//TODO 문제 수정, 관리자 삭제 등 문제 쪽 수정하기.
//TODO 문제 답 개수( OOOO ) 이런식으로 표기하기.

public class Game extends JPanel implements ActionListener, KeyListener{
	
//	private ArrayList<Tile> tileList = new ArrayList<>();
	private Tile[][] tile;
	private ArrayList<Gem> gemList = new ArrayList<>();
	private ButtonGroup gemGroup = new ButtonGroup(); // 보석들을 묶어줄 그룹
	public static final int RADIUS = 20;
	public static final int Y_PADDING = 2;
	private ImageIcon largeGem;
	private ImageIcon smallGem;
	private ImageIcon otherGem[] = new ImageIcon[4];
	public ImageIcon getOtherGem(int index) {
		return otherGem[index];
	}

	public void setOtherGem(int index, ImageIcon otherGem) {
		this.otherGem[index] = otherGem;
	}
	private JButton rotateBtn;
	private JButton reverseBtn;
	private JTextArea guideMsg;
	private JLabel tileBackground, panelBackground;
	private JScrollPane guideScroll;
	private Map<Point, Tile> tileMap = new LinkedHashMap<>();
	private boolean isStart = true;  // 첫 시작이면 true 좌측 상단, 우측 상단, 좌측 하단, 우측 하단에만 보석을 둘 수 있게 하자.
	private JTextArea scoreMsg;
	private int myScore=0 ,otherScore=0;
	private int othersScore[] = new int[4];
	private boolean isMyTurn;
	
	//반투명 이미지
	private ImageIcon blurIcon = new ImageIcon(Main.class.getResource("img/50%.png"));
	
	private String gemName = null;
	private char gemTurn = 0;
	
	Socket socket = new Socket();
	InetAddress ip = null;
	private DataInputStream in;
	private DataOutputStream out;
	
	// 다음 차례 넘기기 버튼
	private JButton nextTurnBtn;
	
	// 게임 종료 
	private boolean isEnd = false;
	private boolean isEndCheck[];
	private int playerNum;
	
	// 채팅창
	private ChatPanel chat;
	
	Thread testThread;
	timeCheck time;
	private int joinPlayer;
	
	private JLabel timeLabel;
	
	private String id;
	
	
	// Button Img
	private ImageIcon rotateImg = new ImageIcon(Main.class.getResource("img/rotate.png"));
	private ImageIcon rotate_clickImg = new ImageIcon(Main.class.getResource("img/rotate_click.png"));
	private ImageIcon reverseImg = new ImageIcon(Main.class.getResource("img/reverse.png"));
	private ImageIcon reverse_clickImg = new ImageIcon(Main.class.getResource("img/reverse_click.png"));
	private ImageIcon surrenderImg = new ImageIcon(Main.class.getResource("img/surrender.png"));
	private ImageIcon surrender_clickImg = new ImageIcon(Main.class.getResource("img/surrender_click.png"));
	
	public void connect(String ip, int roomNum) throws Exception {
		
		socket = new Socket(ip, 10004 + roomNum);
		// System.out.println("서버 연결됨.");

		out = new DataOutputStream(socket.getOutputStream());
		in = new DataInputStream(socket.getInputStream());
		
		// 채팅창
		Socket chatSocket = new Socket(ip, 50005 + roomNum);
		
		chat = new ChatPanel(chatSocket) {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(blurIcon.getImage(), 0, 0, null);
				super.paintComponent(g);				
			}
		};
	}
	
	public void getGemName() {
		String str;
		try {
			str = in.readUTF();
			gemName = str;
			
			playerNum = in.readInt();
			
			out.writeUTF(Main.nickname);
			
			isEndCheck = new boolean[playerNum];
			
			for(int i=0; i<isEndCheck.length; i++)
				isEndCheck[i] = false;
			
			if(str.equals("Y")) {
//				isMyTurn = true;
				gemTurn = 'Y';
			}
//			else
				isMyTurn = false;
			
		}catch(Exception e) {
			//gemName = "R";
		}		
	}
	
	public void setOtherGem() {
		otherGem[0] = new ImageIcon(Main.class.getResource("img/Y40.png"));
		otherGem[1] = new ImageIcon(Main.class.getResource("img/R40.png"));
		otherGem[2] = new ImageIcon(Main.class.getResource("img/B40.png"));
		otherGem[3] = new ImageIcon(Main.class.getResource("img/G40.png"));
	}
	
	public Game(String ipString, int roomNum, String id) throws Exception{
		this.id = id;
		
		setLayout(null);
		setSize(1000,750);		
		
		connect(ipString, roomNum);
		getGemName();
		setOtherGem();		
		
		largeGem = new ImageIcon(Main.class.getResource("img/"+gemName+"40.png"));
		smallGem = new ImageIcon(Main.class.getResource("img/"+gemName+"20.png"));
		
		setTile(15, 15, 295); // 4인용 보드판의 타일 개수는 295개이다.
		addKeyListener(this);
		tileBackground = new JLabel(new ImageIcon(Main.class.getResource("img/BoardBackground.png"))) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				// 클릭된 보석 가져오기.
				Gem gem = gemList.get(Integer.parseInt(gemGroup.getSelection().getActionCommand()));

				if (!gem.isEnd())
					for (int i = 0; i < tile.length; i++) {
						for (int j = 0; j < tile[i].length; j++) {
							if (tile[i][j].getModel().isRollover()) {
								gem.drawGemOnBoard(g, tile[i][j].getX() - 15, tile[i][j].getY() - 15, gemName);
								repaint();
							}
						}
					}
			}
		};
		tileBackground.setBounds(15, 15, 580, 580);
		add(tileBackground);
		
		panelBackground = new JLabel(new ImageIcon(Main.class.getResource("img/Background.jpg")));
		panelBackground.setBounds(0, 0, 1000, 750);
		add(panelBackground);				
		setGem(650, 30, smallGem);			
		
		//안내 문구
		guideMsg = new JTextArea() {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(blurIcon.getImage(), 0, 0, null);
				super.paintComponent(g);				
			}
		};
		guideScroll = new JScrollPane(guideMsg);
		
		//guideMsg.setBounds(0, 600, 995, 110);
		guideScroll.setBounds(0, 600, 600, 120);
		//guideMsg.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		guideMsg.setFont(Main.BinggraeBoldFont.deriveFont(20f));
		guideMsg.setEditable(false);
		guideMsg.setLineWrap(true);
		guideMsg.setOpaque(false);
		guideScroll.setOpaque(false);
		guideScroll.getViewport().setOpaque(false);
		guideScroll.setAutoscrolls(true);
		panelBackground.add(guideScroll);
		
		//점수 문구
		scoreMsg = new JTextArea() {
			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(blurIcon.getImage(), 0, 0, null);
				super.paintComponent(g);				
			}
		};
//		scoreMsg.setBounds(600, 520, 165, 80); TODO 
		scoreMsg.setBounds(596, 499, 169, 100);
		scoreMsg.setFont(Main.BinggraeBoldFont.deriveFont(17.5f));
		//TEST
		scoreMsg.setText("    노랑 : 0점\n    빨강 : 0점\n    파랑 : 0점\n    초록 : 0점");
		scoreMsg.setLineWrap(true);
		scoreMsg.setEditable(false);
		scoreMsg.setOpaque(false);;
		panelBackground.add(scoreMsg);
		
		// 정보 받아오기
		
		testThread = new Thread(new readInfo());
		testThread.start();
		
		// 회전 버튼
		rotateBtn = new JButton(rotateImg) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(rotate_clickImg);
				else
					this.setIcon(rotateImg);
			}
		};
		rotateBtn.addActionListener(this);
		rotateBtn.setBounds(880, 545, 116, 55);
		rotateBtn.addKeyListener(this);
		rotateBtn.setBackground(Color.BLACK);
		rotateBtn.setOpaque(false);
		panelBackground.add(rotateBtn);
		
		// 항복 버튼
		nextTurnBtn = new JButton(surrenderImg) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(surrender_clickImg);
				else
					this.setIcon(surrenderImg);
			}
		};
		nextTurnBtn.addActionListener(this);
		nextTurnBtn.setBounds(880-116, 545, 116, 55);
		nextTurnBtn.addKeyListener(this);
		nextTurnBtn.setBackground(Color.BLACK);
		nextTurnBtn.setOpaque(false);
		panelBackground.add(nextTurnBtn);		
		
		// 좌우 반전 버튼
		reverseBtn = new JButton(reverseImg) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(reverse_clickImg);
				else
					this.setIcon(reverseImg);
			}
		};
		reverseBtn.addActionListener(this);
		reverseBtn.setBounds(880, 545-55, 116, 55);
		reverseBtn.addKeyListener(this);
		reverseBtn.setBackground(Color.black);
		reverseBtn.setOpaque(false);
		panelBackground.add(reverseBtn);	
		
		// chat 창
		
		chat.setBounds(600, 600, 395, 120);
		chat.getNameField().setText(id+"("+gemName+")");
		panelBackground.add(chat);
		
		// timeLabel
		timeLabel = new JLabel("30");
		timeLabel.setFont(Main.digitalFont.deriveFont(50f));
//		timeLabel.setBounds(940, -10, 50, 50);
		timeLabel.setBounds(880-116, 545-62, 116, 55);
		timeLabel.setOpaque(false);		
		timeLabel.setVerticalAlignment(JLabel.CENTER);
		timeLabel.setHorizontalAlignment(JLabel.CENTER);
		panelBackground.add(timeLabel);
		
		for(int i=0; i<tile.length; i++)
			for(int j=0; j<tile[i].length; j++) {
				if(gemName.equals("Y"))
					tile[i][j].setGemColor("Y");
				else if (gemName.equals("R"))
					tile[i][j].setGemColor("R");
				else if (gemName.equals("B"))
					tile[i][j].setGemColor("B");
				else if (gemName.equals("G"))
					tile[i][j].setGemColor("G");
					
			}
		
		time = new timeCheck();
		time.start();
		

//		new Thread() {
//			@Override
//			public void run() {
//				while(true) {
//					repaint();
//				}
//			};
//		}.start();
		
		//
		/*
		TestDefBtn = new JButton("검사");
		TestDefBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isDef()) {
					JOptionPane.showMessageDialog(null, "TRUE 입니다.더 이상 보석을 놔둘 수 없음.");
				} else {
					JOptionPane.showMessageDialog(null, "FALSE 입니다. 놓을 수 있는 위치가 존재.");
				}
			}
		});
		TestDefBtn.setBounds(790, 530-70, 100, 70);
		panelBackground.add(TestDefBtn);
		*/
	}
	/*
	 * setGem 여기서 보석을 생성 및 배치를 한다.
	 */
	
	public void setGem(int startX, int startY, ImageIcon gemIcon) {
		int x = startX, y = startY;
		for(int i=0; i<18; i++) {
			Gem gem = new Gem(gemIcon);
			gem.setActionCommand(i+"");
			gem.setIndex(i);
			gem.addKeyListener(this);
			gem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new MusicList().gemClick2();
				}
			});
			
			if(i<3) {
				gem.setSelected(true);
				gem.setGemNumber(i + 1);
			} else if (i<5)
				gem.setGemNumber(3);
			else if (i<10)
				gem.setGemNumber(4);
			else
				gem.setGemNumber(5);
			
			gem.init();
			gemList.add(gem);
			gemGroup.add(gem);
			panelBackground.add(gem);
			
			int gemSize = Gem.RADIUS * gem.getGemNumber()*2;
			int screenSize = this.getWidth() - 60;
			
			if(i==0)
				gem.setBounds(startX, startY, gemSize, gemSize);
			else {
				if(x + gemSize < screenSize)
					gem.setBounds(x += gemList.get(i-1).getWidth() + 5, y, gemSize, gemSize);
				else
					gem.setBounds(x = startX, y += gemList.get(i-1).getHeight(), gemSize, gemSize);
			}
			
			/*
			if(i==0) {
				gem.setBounds(startX, startY, gemSize, gemSize);
			}
			else if(i<10) {
				gem.setBounds(x += gemList.get(i-1).getWidth(), startY, gemSize, gemSize);
			}
			else if(i==10) {
				gem.setBounds(x = startX, y += gemList.get(i-1).getHeight(), gemSize,gemSize);
			}
			else {
				gem.setBounds(x += gemList.get(i-1).getWidth(), y, gemSize, gemSize);
			}			
			*/
			
		}
		
		
	}
	
	public void drawGem(int startX, int startY) {
		int x = startX, y = startY;
		
		for (int i = 0; i < gemList.size(); i++) {
			Gem gem = gemList.get(i);

			int gemSize = Gem.RADIUS * gem.getGemNumber() * 2;
			int screenSize = this.getWidth() - 60;		

			if (i == 0)
				gem.setBounds(startX, startY, gemSize, gemSize);
			else {
				if (x + gemSize < screenSize)
					gem.setBounds(x += gemList.get(i - 1).getWidth() + 5, y, gemSize, gemSize);
				else
					gem.setBounds(x = startX, y += gemList.get(i - 1).getHeight(), gemSize, gemSize);
			}
		}
	}
	
	/*
	 * setTile 여기서 타일을 생성 및 add를 해준다. 액션리스너도 여기서 추가된다.
	 */	
	public void setTile(int startX, int startY, int size) { // 시작 좌표, 보드판의 개수를 받는다.
		int x=startX, y=startY;
		boolean isTen = true; // 10줄, 9줄 반복이라서 만들었다.
		int cnt = 1; // 한 줄을 그리고 초기화하여 10개 그렸는지 9개 그렸는지 확인한다.
		tile = new Tile[31][];
		
		for (int i=0; i<tile.length; i++) {
			if(isTen) {
				tile[i] = new Tile[10];
				isTen = false;
			}
			else {
				tile[i] = new Tile[9];
				isTen = true;
			}
		}
		
		isTen = true;
		
		for(int i=0; i<tile.length; i++) {
			for( int j=0; j<tile[i].length; j++) {
				Tile t = new Tile(x, y, RADIUS, largeGem);
				// t.setColor(new Color(200,200,0,150)); // 이쁜 색을 찾으면 변경할것!
				t.addActionListener(new tileAction());
				t.setText(""+i+""+j); // 나중에 actionPerformed(ActionEvent e) 여기서 e.getActionCommand()을하면 이것이 나온다. (t.setActionCommand(""+i);)
				t.addKeyListener(this);
				// TODO 지뢰 랜덤 설정
				if(Math.random() > 0.80)
					t.setMine(true);
					
				tile[i][j] = t;
				this.add(tile[i][j]);
				tileMap.put(t.getPoint(), t);
				
				
				
				
				if (isTen) {
					if (cnt < 10) {
						x += RADIUS * 3; // 육각형의 한개 반 크기만큼 오른쪽으로 간다
						cnt++;
					} else { // 줄바꾸기
						x = startX + RADIUS + RADIUS / 2; // 줄을 바꾸는거라서 이만큼만 움직인다.
						y += RADIUS - Y_PADDING; // 정 육각형이 가로 세로가 동일할수가 없어 간격이 벌어지는 것을 조정
						isTen = false;
						cnt = 1;
					}
				} else {
					if (cnt < 9) {
						x += RADIUS * 3;
						cnt++;
					} else {
						x = startX;
						y += RADIUS - Y_PADDING;
						isTen = true;
						cnt = 1;
					}
				}
			}
		}
	}
	
	// 더이상 둘곳이 있나 없나 검사.
	public boolean isDef() { // 정상 작동한다.
		int i,j;
		boolean isBreak = false;
		
		ArrayList<Gem> aliveGem = new ArrayList<>();
		
		for(Gem g : gemList)
			if(g.isEnabled())
				aliveGem.add(g);
		
		for(i=0; i<tile.length; i++) {
			if(isBreak)
				break;			
			for(j=0; j<tile[i].length; j++) {
				for(Gem g : aliveGem) {
					if(isTileCheck(i, j, g)) {
						isBreak = true;
						break;
					}
				}
				
			}
		}
		
		if(i == tile.length)
			return true;
		
		return false;
	}
	
	public boolean isTileCheck(int index1, int index2, Gem gem) {

		int gemNumber = gem.getGemNumber();
		int gemIndex = gem.getIndex();
		int cnt = 0;

		Tile t = tile[index1][index2];
		Tile checkTile[] = new Tile[gemNumber];

		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;


		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;
			
		}

		for (int i = 0; i < gemNumber; i++) {
			checkTile[i] = tileMap.get(new Point(gemX[i], gemY[i]));

			if (checkTile[i] != null) {
				if (checkTile[i].getIsGem()) {
					return false;
				} else {
					cnt++;										
				}
			} else {
				return false;
			}
		}		

		
		if(isTileOtherGemCheck(index1, index2, gem)) {
		if (isTileRuleCheck(index1, index2, gem)) {
			if (cnt == gemNumber) {
				
			}
		} else return false;
		} else return false;
		
		return true;
	}
	
	public boolean isTileOtherGemCheck(int index1, int index2, Gem gem) {
		int cnt;
		int gemNumber = gem.getGemNumber();
		
		int tmpX = RADIUS + RADIUS/2;
		int tmpY = RADIUS - Y_PADDING;
		
		Tile t = tile[index1][index2];	
		Tile checkRuleTile[][] = new Tile[gemNumber][6];
		ArrayList<Tile> tileInMyGem = new ArrayList<>();
		
		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;
		
		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;		
		}
		
		for (int i = 0; i < gemNumber; i++) { // 주변 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] , gemY[i] - tmpY * 2));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] , gemY[i] + tmpY * 2));			
		}
		
		for(int i=0; i<tile.length; i++) {
			for(int j=0; j<tile[i].length; j++) {
				//for(int k=0; k<gemNumber; k++)
				int tmpTileX = tile[i][j].getPoint().x;
				int tmpTileY = tile[i][j].getPoint().y;
					if(tile[i][j].getIsGem() && gemName.equals(tile[i][j].getGemColor())) {// if(gemName.equals(tile[i][j].getGemColor())) {				                                             
						tileInMyGem.add(tileMap.get(new Point(tmpTileX + tmpX, tmpTileY + tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX - tmpX, tmpTileY + tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX + tmpX, tmpTileY - tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX - tmpX, tmpTileY - tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX, tmpTileY - tmpY * 2)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX, tmpTileY + tmpY * 2)));
				}
			}
		}
		
		Set<Tile> checkTileSet = new HashSet<>();
		
		for(int i=0; i<checkRuleTile.length; i++) {
			for(int j=0; j<checkRuleTile[i].length; j++) {
				for(int k=0; k<tileInMyGem.size(); k++) {
					if(checkRuleTile[i][j] != null) //&& checkRuleTile[i][j].getIsGem())
						if(tileInMyGem.get(k) != null) //&& tileInMyGem.get(k).getIsGem())
						if(checkRuleTile[i][j] == tileInMyGem.get(k)) {
							checkTileSet.add(tileInMyGem.get(k));
						}
				}
			}
		}
		Iterator it = checkTileSet.iterator();
		
		int cntY=0, cntR=0, cntB=0, cntG=0;
		cnt=0;
		
		while(it.hasNext()) {
			Tile tt = (Tile) it.next();
			
			if(tt.getGemColor().equals("Y"))
				cntY++;
			else if(tt.getGemColor().equals("R"))
				cntR++;
			else if(tt.getGemColor().equals("B"))
				cntB++;
			else if(tt.getGemColor().equals("G"))
				cntG++;
			
			cnt++;
		}
		
		// System.out.printf("cntY=%d, cntR=%d, cntB=%d, cntP=%d, cnt=%d\n", cntY, cntR, cntB, cntP, cnt);
		
		if(!(checkTileSet.size() == 0)) {
			if (checkTileSet.size() == cntY) {
				if(gemName.equals("Y"))
					return true;
				return false;
			} else if (checkTileSet.size() == cntR) {
				if(gemName.equals("R"))
					return true;
				return false;
			} else if (checkTileSet.size() == cntB) {
				if(gemName.equals("B"))
					return true;
				return false;
			} else if (checkTileSet.size() == cntG) {
				if(gemName.equals("G"))
					return true;
				return false;
			}
		}	
		
		return true;
	}
	
	public boolean isTileRuleCheck(int index1, int index2, Gem gem) {
		int cnt=0;
		int gemNumber = gem.getGemNumber();
		
		int tmpX = RADIUS + RADIUS/2;
		int tmpY = RADIUS - Y_PADDING;
		
		Tile t = tile[index1][index2];	
		Tile checkRuleTile[][] = new Tile[gemNumber][6];
		
		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;
		
		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;		
		}

		for (int i = 0; i < gemNumber; i++) { // 주변 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] , gemY[i] - tmpY * 2));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] , gemY[i] + tmpY * 2));			
		}
		
		
		for(int i=0; i<gemNumber; i++) {
			for(int j=0; j<checkRuleTile[i].length; j++) {
				if(checkRuleTile[i][j] == null)
					continue;
				if(!checkRuleTile[i][j].getGemColor().equals(gemName))
					continue;
				if(checkRuleTile[i][j].getIsGem()) {
					return false;
				}
			}
		}
		
		for (int i = 0; i < gemNumber; i++) { // 한칸 멀리 있는 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY * 3));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY * 3));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY * 3));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY * 3));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] + tmpX * 2 , gemY[i]));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] - tmpX * 2 , gemY[i]));			
		}
		
		if (!isStart)
			for (int i = 0; i < gemNumber; i++) {
				for (int j = 0; j < checkRuleTile[i].length; j++) {
					if (checkRuleTile[i][j] == null)
						continue;
					if(!checkRuleTile[i][j].getGemColor().equals(gemName))
						continue;
					if (checkRuleTile[i][j].getIsGem()) {
						cnt++;
					}
				}
			}
		
		if(isStart || cnt > 0) 
			return true;
		else {
			return false;
		}
	}
	
	
	
	// tileCheck, 데이터 전송
	public boolean tileCheck(int index1, int index2, Gem gem) {

		int gemNumber = gem.getGemNumber();
		int gemIndex = gem.getIndex();
		int cnt = 0;

		Tile t = tile[index1][index2];
		Tile checkTile[] = new Tile[gemNumber];

		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;

		//System.out.printf("tileX = %d, tileY = %d\n", tileX, tileY); // 테스트 용 좌표찍기

		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;
			//System.out.printf("gemX[%d] = %d, gemY[%d] = %d\n", i, gemX[i], i, gemY[i]); // 테스트 용 좌표찍기
			
		}

		for (int i = 0; i < gemNumber; i++) {
			checkTile[i] = tileMap.get(new Point(gemX[i], gemY[i]));

			if (checkTile[i] != null) {
				if (checkTile[i].getIsGem()) {
					guideMsg.append("이미 보석이 놓여있습니다.\n");
					return false;
				} else {
					cnt++;										
				}
			} else {
				guideMsg.append("잘못된 위치입니다.\n");
				return false;
			}
		}
		
		if(isStart) {			
			if(!isStartCheck(index1, index2, gem))
				return false;
		}
		
		if(tileOtherGemCheck(index1, index2, gem)) {
		if (tileRuleCheck(index1, index2, gem)) {
			if (cnt == gemNumber) {			
				
				// 지뢰 제거 부분 시작
					for (int i = 0; i < checkTile.length; i++)
						if (!checkTile[i].mineCheck(this)) { // 지뢰 제거 실패시 차례 넘기고 지뢰 제거 실패 메세지 날리기.
							guideMsg.append("지뢰 제거에 실패했습니다.\n");
							try {
								out.writeUTF("(Mine)");
								out.writeUTF(gemName);
							} catch (IOException e) {
								System.out.println("(Mine)" +e);
							}
							return false;
						}
				
				// 지뢰 제거 부분 끝
				
				// tile 깜빡이는 것을 꺼준다.
				for (int i = 0; i < tile.length; i++)
					for (int j = 0; j < tile[i].length; j++)
						tile[i][j].setIsLast(false);
				
				myScore += gem.getGemNumber();
				
				
				StringBuilder sendXStr, sendYStr, sendIsGemStr;
				sendXStr = new StringBuilder();
				sendYStr = new StringBuilder();
				sendIsGemStr = new StringBuilder();
				// 보석 수만큼 보낸다. (수정 완료)
				for (int j = 0; j < gemNumber; j++) {
					checkTile[j].setIsGem(true);					
					int sendX = checkTile[j].getPoint().x;
					int sendY = checkTile[j].getPoint().y;
					boolean sendIsGem = checkTile[j].getIsGem();
					
					sendXStr.append(sendX + "/");
					sendYStr.append(sendY + "/");
					sendIsGemStr.append(sendIsGem + "/");
				}
					try {
						out.writeUTF("(GameInfo)");
						out.writeUTF(sendXStr.toString());
						out.writeUTF(sendYStr.toString());
						out.writeUTF(sendIsGemStr.toString());
//						out.writeInt(sendX);
//						out.writeInt(sendY);
//						out.writeBoolean(sendIsGem);						
						out.writeUTF(gemName);
						out.writeInt(myScore);
						out.writeBoolean(isEnd);						
												
					} catch (IOException e) {
						e.printStackTrace();
					}
//				} 기존 보석 수만큼 보내는 것을 수정함.
			
			}
			isStart = false;
		} else return false;
		} else return false;
		
		return true;
	}
	
	
	// TODO 위치 상관없이 (다른보석으로 뚫려있따면) 가로막히지 않으면 둬지는 버그가 있다.
	public boolean tileOtherGemCheck(int index1, int index2, Gem gem) {
		int cnt;
		int gemNumber = gem.getGemNumber();
		
		int tmpX = RADIUS + RADIUS/2;
		int tmpY = RADIUS - Y_PADDING;
		
		Tile t = tile[index1][index2];	
		Tile checkRuleTile[][] = new Tile[gemNumber][6];
		ArrayList<Tile> tileInMyGem = new ArrayList<>();
		
		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;
		
		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;
			// System.out.printf("gemX[%d] = %d, gemY[%d] = %d\n", i, gemX[i], i, gemY[i]); // 테스트 용 좌표찍기			
		}
		
		for (int i = 0; i < gemNumber; i++) { // 주변 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] , gemY[i] - tmpY * 2));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] , gemY[i] + tmpY * 2));			
		}
		
		for(int i=0; i<tile.length; i++) {
			for(int j=0; j<tile[i].length; j++) {
				int tmpTileX = tile[i][j].getPoint().x;
				int tmpTileY = tile[i][j].getPoint().y;
					if(tile[i][j].getIsGem() && gemName.equals(tile[i][j].getGemColor())) {// if(gemName.equals(tile[i][j].getGemColor())) {				                                             
						tileInMyGem.add(tileMap.get(new Point(tmpTileX + tmpX, tmpTileY + tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX - tmpX, tmpTileY + tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX + tmpX, tmpTileY - tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX - tmpX, tmpTileY - tmpY)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX, tmpTileY - tmpY * 2)));
						tileInMyGem.add(tileMap.get(new Point(tmpTileX, tmpTileY + tmpY * 2)));
				}
			}
		}
		
		Set<Tile> checkTileSet = new HashSet<>();
		
		for(int i=0; i<checkRuleTile.length; i++) {
			for(int j=0; j<checkRuleTile[i].length; j++) {
				for(int k=0; k<tileInMyGem.size(); k++) {
					if(checkRuleTile[i][j] != null) //&& checkRuleTile[i][j].getIsGem())
						if(tileInMyGem.get(k) != null) //&& tileInMyGem.get(k).getIsGem())
						if(checkRuleTile[i][j] == tileInMyGem.get(k)) {
							checkTileSet.add(tileInMyGem.get(k));
						}
				}
			}
		}
		Iterator it = checkTileSet.iterator();
		
		int cntY=0, cntR=0, cntB=0, cntG=0;
		cnt=0;
		
		while(it.hasNext()) {
			Tile tt = (Tile) it.next();
			
			if(tt.getGemColor().equals("Y"))
				cntY++;
			else if(tt.getGemColor().equals("R"))
				cntR++;
			else if(tt.getGemColor().equals("B"))
				cntB++;
			else if(tt.getGemColor().equals("G"))
				cntG++;
			
			cnt++;
		}
		
		// System.out.printf("cntY=%d, cntR=%d, cntB=%d, cntP=%d, cnt=%d\n", cntY, cntR, cntB, cntP, cnt);
		
		if(!(checkTileSet.size() == 0)) {
			if (checkTileSet.size() == cntY) {
				if(gemName.equals("Y"))
					return true;
				guideMsg.append("노랑 보석으로 가로막혀 있습니다.\n");
				return false;
			} else if (checkTileSet.size() == cntR) {
				if(gemName.equals("R"))
					return true;
				guideMsg.append("빨강 보석으로 가로막혀 있습니다.\n");
				return false;
			} else if (checkTileSet.size() == cntB) {
				if(gemName.equals("B"))
					return true;
				guideMsg.append("파랑 보석으로 가로막혀 있습니다.\n");
				return false;
			} else if (checkTileSet.size() == cntG) {
				if(gemName.equals("G"))
					return true;
				guideMsg.append("초록 보석으로 가로막혀 있습니다.\n");
				return false;
			}
		}	
		
		return true;
	}
	
	public boolean tileRuleCheck(int index1, int index2, Gem gem) {
		int cnt=0;
		int gemNumber = gem.getGemNumber();
		
		int tmpX = RADIUS + RADIUS/2;
		int tmpY = RADIUS - Y_PADDING;
		
		Tile t = tile[index1][index2];	
		Tile checkRuleTile[][] = new Tile[gemNumber][6];
		
		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;
		
		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;
			// System.out.printf("gemX[%d] = %d, gemY[%d] = %d\n", i, gemX[i], i, gemY[i]); // 테스트 용 좌표찍기			
		}

		for (int i = 0; i < gemNumber; i++) { // 주변 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] , gemY[i] - tmpY * 2));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] , gemY[i] + tmpY * 2));			
		}
		
		
		for(int i=0; i<gemNumber; i++) {
			for(int j=0; j<checkRuleTile[i].length; j++) {
				if(checkRuleTile[i][j] == null)
					continue;
				if(!checkRuleTile[i][j].getGemColor().equals(gemName))
					continue;
				if(checkRuleTile[i][j].getIsGem()) {
					guideMsg.append("주변에 보석이 있습니다.\n");
					return false;
				}
			}
		}
		
		for (int i = 0; i < gemNumber; i++) { // 한칸 멀리 있는 보석
			checkRuleTile[i][0] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] + tmpY * 3));
			checkRuleTile[i][1] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] + tmpY * 3));
			checkRuleTile[i][2] = tileMap.get(new Point(gemX[i] + tmpX, gemY[i] - tmpY * 3));
			checkRuleTile[i][3] = tileMap.get(new Point(gemX[i] - tmpX, gemY[i] - tmpY * 3));
			checkRuleTile[i][4] = tileMap.get(new Point(gemX[i] + tmpX * 2 , gemY[i]));
			checkRuleTile[i][5] = tileMap.get(new Point(gemX[i] - tmpX * 2 , gemY[i]));			
		}
		
		if (!isStart)
			for (int i = 0; i < gemNumber; i++) {
				for (int j = 0; j < checkRuleTile[i].length; j++) {
					if (checkRuleTile[i][j] == null)
						continue;
					if(!checkRuleTile[i][j].getGemColor().equals(gemName))
						continue;
					if (checkRuleTile[i][j].getIsGem()) {
						cnt++;
					}
				}
			}
		
		if(isStart || cnt > 0) 
			return true;
		else {
			guideMsg.append("한칸 멀리 보석이 없습니다.\n");
			return false;
		}
	}
	
	public boolean isStartCheck(int index1, int index2, Gem gem) {
		int cnt=0;
		int gemNumber = gem.getGemNumber();
		
		Tile t = tile[index1][index2];
		Tile startTile[] = {tile[0][0], tile[0][9], tile[30][0], tile[30][9]};
		Tile checkTile[] = new Tile[gemNumber];
		
		int tileX = t.getPoint().x;
		int tileY = t.getPoint().y;
		
		int gemX[] = new int[gemNumber];
		int gemY[] = new int[gemNumber];

		for (int i = 0; i < gemNumber; i++) {
			gemX[i] = gem.getX(i) + tileX;
			gemY[i] = gem.getY(i) + tileY;
			// System.out.printf("gemX[%d] = %d, gemY[%d] = %d\n", i, gemX[i], i, gemY[i]); // 테스트 용 좌표찍기			
		}

		for (int i = 0; i < gemNumber; i++) {
			checkTile[i] = tileMap.get(new Point(gemX[i], gemY[i]));
			for(int j=0; j<startTile.length; j++) {
				if(checkTile[i] == startTile[j])
					cnt++;
			}
		}		

		if (cnt == 0) {
			guideMsg.append("시작은 각 보드판의 끝에서만 시작할 수 있습니다.\n");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println(e.getActionCommand() + tileList.get(Integer.parseInt(e.getActionCommand())).getPoint());		
		try {
			Gem gem = gemList.get(Integer.parseInt(gemGroup.getSelection().getActionCommand()));

			/*
			 * 회전 버튼의 액션 버튼이 늘려지면 선택된 보석을 회전시킨다.
			 */			
			if (e.getSource() == rotateBtn) {
				guideMsg.append("키보드 R을 눌러도 회전이 가능합니다.\n");
				gem.rotate();
				gem.repaint();
			}
			/*
			 * e.getActionCommand로 인덱스를 받아오고(String) 그것을 parseInt.
			 * 즉 타일이 눌린다면 밑에 if문이 작동한다.
			 */
			
			/*else if (e.getSource() == tileList.get(Integer.parseInt(e.getActionCommand()))) {
				int tileIndex = Integer.parseInt(e.getActionCommand());
				if(gem.getIndex() == 0) {
					tileCheck(tileIndex,0);
				} else if (gem.getIndex() == 1) {
					if (gem.getRotation() == 0&& tileRightCheck(tileIndex))
						tileCheck(tileIndex, 10);
					else if (gem.getRotation() == 60 && tileRightCheck(tileIndex))
						tileCheck(tileIndex, -9);
					else if (gem.getRotation() == 120 )
						tileCheck(tileIndex, -19);
					else if (gem.getRotation() == 180&& tileLeftCheck(tileIndex))
						tileCheck(tileIndex, -10);
					else if (gem.getRotation() == 240 && tileLeftCheck(tileIndex))
						tileCheck(tileIndex, 9);
					else if (gem.getRotation() == 300 )
						tileCheck(tileIndex, 19);
				}
			}*/
			
			else if (e.getSource() == nextTurnBtn) {				
				try {
					if((gemTurn == gemName.charAt(0))) {
						int isSurrender = JOptionPane.showConfirmDialog(null, "더 이상 둘 곳이 없다면 자동 항복이 됩니다.\n그래도 정말 항복하시겠습니까?", "항복", JOptionPane.YES_NO_CANCEL_OPTION);
						
						if(isSurrender != JOptionPane.OK_OPTION)
							return;
						
						out.writeUTF("(Surrender)");
						out.writeUTF(gemName);
						
						isEnd = true;
						isMyTurn = false;
						guideMsg.append("항복 했습니다.\n");
					} else
						guideMsg.append("내 차례가 아닙니다. 본인 차례에 항복을 눌러주세요.\n");
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			} else if (e.getSource() == reverseBtn) {
				guideMsg.append("키보드 T을 눌러도 반전이 가능합니다.\n");
				gem.reverse();
				gem.repaint();
			}
			
		} catch (java.lang.NullPointerException e2) {
			// 만약 보석을 선택 안했는데 회전버튼을 눌렀을때를 대비하여 트라이 캐치를 사용.
			// 보석을 생성할 때 선택을 하게 만들어서 오류가 발생안한다.
			guideMsg.append("보석을 선택하고 회전을 누르세요.\n");
		} catch (java.lang.IndexOutOfBoundsException e2) {
			guideMsg.append("보드판을 벗어나는 위치입니다.\n");
		}

	}
	
	private class tileAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Gem gem = gemList.get(Integer.parseInt(gemGroup.getSelection().getActionCommand()));
			guideMsg.setCaretPosition(guideMsg.getDocument().getLength());
			int index1, index2;
			
			if(e.getActionCommand().length() == 2) {
				index1 = Integer.parseInt(""+e.getActionCommand().charAt(0));
				index2 = Integer.parseInt(""+e.getActionCommand().charAt(1));
			} else {
				index1 = Integer.parseInt(""+e.getActionCommand().substring(0, 2));
				index2 = Integer.parseInt(""+e.getActionCommand().charAt(2));
			}
			
			if(isEnd) {
				guideMsg.append("항복을 해서 못둡니다.\n");
				return;
			}
			
			if(isDef()) { // TODO 자동 항복에 오류가 있음.
				try {
					out.writeUTF("(AutoEnd)");
					out.writeUTF(gemName);
					
					isEnd = true;
					isMyTurn = false;
					guideMsg.append("더 이상 둘 곳이 없어 자동 항복처리 됐습니다.\n");
					guideMsg.append("항복처리가 돼도 게임이 종료 될 때 점수가 가장 높다면 승리합니다.\n");
					return;
				} catch (IOException e1) {
					System.out.println(e1 + " : if(isDef)");
				}
			}
			
			if(joinPlayer < 4) {
				guideMsg.append(String.format("지금까지 %d명 들어왔습니다.\n", joinPlayer));
				return;
			}
			
			if(!(gemTurn == gemName.charAt(0))) {
				guideMsg.append("상대방의 차례입니다.\n");
				isMyTurn = false;
				return;
			}
			
			if (e.getSource() == tile[index1][index2]) {
				if(!gem.isEnd() && tileCheck(index1, index2, gem)) {		
					//myScore += gem.getGemNumber();
					gem.setEnd(true);
					gem.setEnabled(false);
					scoreMsg.setText(String.format("    노랑 : %d점\n    빨강 : %d점\n    파랑 : %d점\n    초록 : %d점", 
							othersScore[0], othersScore[1], othersScore[2], othersScore[3]));					
					
					int gemCnt=0;
					
					for(Gem g : gemList) {
						if(g.isEnabled())
							gemCnt++;
					}
					
					if(gemCnt == 0) {
						try {
							out.writeUTF("(EndTurn)");
							out.writeUTF(gemName);
						} catch (Exception e2) {
							System.out.println("(EndTurn)" + e2);
						}
						
						return;
					}
					// 보석 개수 확인 끝

					//repaint();
				}
			}
			
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Gem gem = gemList.get(Integer.parseInt(gemGroup.getSelection().getActionCommand()));
		if (e.getKeyCode() == 82) { // r
			// || e.getKeyChar() == 'r' || e.getKeyChar() == 'R' || e.getKeyChar() == 'ㄱ'
			gem.rotate();
			gem.repaint();
		} else if (e.getKeyCode() == 84) { // t
			// || e.getKeyChar() == 't' || e.getKeyChar() == 'T' || e.getKeyChar() == 'ㅅ'
			gem.reverse();
			gem.repaint();
		}
		
	}
	
	public void exitGame() {		
		try {
			testThread = null;
			time = null;
			
			out.writeUTF("(OUT)");
			out.writeUTF(gemName);
			
			out.writeUTF("(Exit)");
		} catch (IOException e) {
			System.out.println(e + " :exitGame()");
		}
	}
	
	class timeCheck extends Thread {
		public void run() {
			int time = 30;
			while(!socket.isClosed()) {
				if(!isStart && gemTurn == gemName.charAt(0)) {
//				if(true) { //Test 용
					try {
						new MusicList().tick();
						
						if(time < 6) {
							timeLabel.setForeground(Color.RED.darker());
							timeLabel.setBounds(0, -50, getWidth(), getHeight());
							timeLabel.setFont(timeLabel.getFont().deriveFont(350f));
						}
						else {
							timeLabel.setForeground(Color.BLACK);
							timeLabel.setFont(Main.digitalFont.deriveFont(50f));
//							timeLabel.setBounds(940, -10, 50, 50);
							timeLabel.setBounds(880-116, 545-62, 116, 55);
						}
						
						if(time <= 0) {
							out.writeUTF("(TimeOver)");
							out.writeUTF(gemName);
							
							time = 30;
						}
						this.sleep(1000);
						time--;
						
					} catch (Exception e) {
						System.out.println("timeCheck : " + e);
					}
					
				} else {
					timeLabel.setForeground(Color.BLACK);
					timeLabel.setFont(Main.digitalFont.deriveFont(50f));
//					timeLabel.setBounds(940, -10, 50, 50);
					timeLabel.setBounds(880-116, 545-62, 116, 55);
					time = 30;
				}
				
				timeLabel.setText(time+"");
				repaint();
			}
		}
	}
	
	private class readInfo implements Runnable {

		@Override
		public void run() {
			int x, y, score;
			boolean isGem, endCheck;
			String gemColor, whoIsTurn, xStr, yStr, isGemStr;
			try {
				while (socket!=null && !socket.isClosed() && !socket.isInputShutdown()) {
					guideMsg.setCaretPosition(guideMsg.getDocument().getLength());
					
					String info = in.readUTF();
					
					if(info.equals("(OUT)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);
						guideMsg.append(gemColor+"가 나갔습니다. 자동 항복처리를 합니다.\n");
						guideMsg.append(whoIsTurn.substring(1) + "\n");
						
					}else if (info.equals("(AutoEnd)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);
						
						guideMsg.append(gemColor+"가 더 이상 둘 곳이 없어 자동 항복됐습니다.\n");
						guideMsg.append(whoIsTurn.substring(1) + "\n");
					}else if (info.equals("(Surrender)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);
						
						guideMsg.append(gemColor+"가 항복했습니다.\n");
						guideMsg.append(whoIsTurn.substring(1) + "\n");
					}
					else if(info.equals("(JoinInfo)")) {
						int player = 4;
						joinPlayer = in.readInt();
						
						if(player - joinPlayer > 0) {
							guideMsg.append(joinPlayer + "번 째 플레이어 접속. 시작까지 " + (player - joinPlayer) + "명 남았습니다.\n");
						}
						else {
							guideMsg.append(joinPlayer + "번 째 플레이어 접속. 게임을 시작합니다.\n");
							guideMsg.append("순서는 들어온 순서대로 진행됩니다. (Y -> R -> B -> G)\n");
						}			
					}					
					else if(info.equals("(Mine)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);						
						
						guideMsg.append(gemColor+" 지뢰제거를 실패했습니다.\n");
						guideMsg.append(whoIsTurn.substring(1)+"\n");
						
						new MusicList().bomb();
					}
					// end Mine
					else if (info.equals("(TimeOver)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);						
						
						guideMsg.append(gemColor+" 시간 초과. 차례가 넘어갑니다.\n");
						guideMsg.append(whoIsTurn.substring(1)+"\n");
					}
					else if (info.equals("(EndTurn)")) {
						gemColor = in.readUTF();
						whoIsTurn = in.readUTF();
						gemTurn = whoIsTurn.charAt(0);
						
						guideMsg.append(gemColor+" 보석을 전부 소모했습니다.\n");
						guideMsg.append("더 이상 보석이 없어 "+gemColor+"의 차례는 건너뜁니다.\n");
						guideMsg.append("게임이 종료 될 때 점수가 가장 높다면 승리합니다.\n");
						guideMsg.append(whoIsTurn.substring(1)+"\n");						
					}
					else if (info.equals("(GameInfo)")) {
						
//						x = in.readInt();
//						y = in.readInt();
//						isGem = in.readBoolean();
						
						xStr = in.readUTF();
						yStr = in.readUTF();
						isGemStr = in.readUTF();
						
						gemColor = in.readUTF();
						score = in.readInt();
						endCheck = in.readBoolean();
						whoIsTurn = in.readUTF();

						guideMsg.append(whoIsTurn.substring(1) + "\n");
						gemTurn = whoIsTurn.charAt(0);

						if (endCheck) {
							guideMsg.append(String.format("%S가 항복했습니다.\n", gemColor));

							if (gemColor.equals(gemName))
								isEnd = true;					
						}

//						if (whoIsTurn.charAt(0) == 'Y' && gemName.equals("Y")) {
//							isMyTurn = true;
//						} else if (whoIsTurn.charAt(0) == 'R' && gemName.equals("R")) {
//							isMyTurn = true;
//						} else if (whoIsTurn.charAt(0) == 'B' && gemName.equals("B")) {
//							isMyTurn = true;
//						} else if (whoIsTurn.charAt(0) == 'G' && gemName.equals("G")) {
//							isMyTurn = true;
//						}

						if (gemColor.equals("Y")) {
							othersScore[0] = score;
							scoreMsg.setText(String.format("    노랑 : %d점\n    빨강 : %d점\n    파랑 : %d점\n    초록 : %d점", othersScore[0],
									othersScore[1], othersScore[2], othersScore[3]));
						} else if (gemColor.equals("R")) {
							othersScore[1] = score;
							scoreMsg.setText(String.format("    노랑 : %d점\n    빨강 : %d점\n    파랑 : %d점\n    초록 : %d점", othersScore[0],
									othersScore[1], othersScore[2], othersScore[3]));
						} else if (gemColor.equals("B")) {
							othersScore[2] = score;
							scoreMsg.setText(String.format("    노랑 : %d점\n    빨강 : %d점\n    파랑 : %d점\n    초록 : %d점", othersScore[0],
									othersScore[1], othersScore[2], othersScore[3]));
						} else if (gemColor.equals("G")) {
							othersScore[3] = score;
							scoreMsg.setText(String.format("    노랑 : %d점\n    빨강 : %d점\n    파랑 : %d점\n    초록 : %d점", othersScore[0],
									othersScore[1], othersScore[2], othersScore[3]));
						}

						// 보석을 놓으면서 깜빡임을 추가한다.
						String[] xStrArr = xStr.split("/");
						String[] yStrArr = yStr.split("/");
						String[] isGemStrArr = isGemStr.split("/");
						
						int[] xArr = new int[xStrArr.length], 
								yArr = new int[yStrArr.length];
						boolean[] isGemArr = new boolean[isGemStrArr.length];
						
						for(int i=0; i<isGemStrArr.length; i++) {
							xArr[i] = Integer.parseInt(xStrArr[i]);
							yArr[i] = Integer.parseInt(yStrArr[i]);
							isGemArr[i] = Boolean.parseBoolean(isGemStrArr[i]);
						}
						
						for (int i = 0; i < xArr.length; i++) {
							if (isGemArr[i]) {
								Tile t = tileMap.get(new Point(xArr[i], yArr[i]));
								t.setIsGem(true);
								t.setIsLast(true);
								if (gemColor.equals("Y")) {
									t.setIcon(otherGem[0]);
									t.setGemColor("Y");
									// otherScore = score;
								} else if (gemColor.equals("R")) {
									t.setIcon(otherGem[1]);
									t.setGemColor("R");
								} else if (gemColor.equals("B")) {
									t.setIcon(otherGem[2]);
									t.setGemColor("B");
								} else if (gemColor.equals("G")) {
									t.setIcon(otherGem[3]);
									t.setGemColor("G");
								}
							}							
						}
						new MusicList().gemClick();
					} else if(info.equals("(EndGame)")) {						
						int myScore = 0, maxScore = othersScore[0];
						
						if(gemName.equals("Y")) {
							myScore = othersScore[0];
						} else if(gemName.equals("R")) {
							myScore = othersScore[1];
						} else if(gemName.equals("B")) {
							myScore = othersScore[2];
						} else if(gemName.equals("G")) {
							myScore = othersScore[3];
						}
						
						for(int i=1; i<othersScore.length; i++ ) {						
							if(maxScore < othersScore[i])
								maxScore = othersScore[i];
						}
						
						String msg = String.format("게임 종료!\n이번 게임의 최고 점수 : %d\n나의 점수 : %d", maxScore, myScore);
						
						JOptionPane.showMessageDialog(null, msg);
						
						boolean moreCheck = false;
						
						do {
							int failCnt = 0;
							Connection con = Member.makeConnection();
							PreparedStatement selectPstmt = con
									.prepareStatement("SELECT winCnt, winPoint FROM blomember WHERE nickname = ?");
							selectPstmt.setString(1, id);
							ResultSet rs = selectPstmt.executeQuery();

							int myWinPoint = 0, myWinCnt = 0;

							if (rs.next()) {
								myWinCnt = rs.getInt(1);
								myWinPoint = rs.getInt(2);
							} else {
								JOptionPane.showMessageDialog(null, "점수 갱신 실패 ㅠㅠ\nDB에서 아이디를 못찾았습니다.");
								failCnt++;
							}

							if (myScore >= maxScore) {
								myWinCnt++;
							}

							PreparedStatement updatePstmt = con
									.prepareStatement("UPDATE blomember SET winCnt = ?, winPoint = ? WHERE nickname = ?");
							updatePstmt.setInt(1, myWinCnt);
							updatePstmt.setInt(2, myWinPoint + myScore);
							updatePstmt.setString(3, id);

							int result = updatePstmt.executeUpdate();

							if (result == 0) {
								JOptionPane.showMessageDialog(null, "점수 갱신 실패 ㅠㅠ\nDB에서 업데이트를 실패했습니다.");
								failCnt++;
							}

							rs = selectPstmt.executeQuery();

							if (rs.next()) {
								myWinCnt = rs.getInt(1);
								myWinPoint = rs.getInt(2);
								msg = String.format("점수 갱신 완료!\n나의 승리 수 : %d, 나의 점수 : %d", myWinCnt, myWinPoint);
								JOptionPane.showMessageDialog(null, msg);
							} else {
								JOptionPane.showMessageDialog(null, "점수 확인 실패 ㅠㅠ\nDB에서 아이디를 못찾았습니다.");
								failCnt++;
							}
							
							if(failCnt > 0) {
								int isMoreCheck = JOptionPane.showConfirmDialog(null, "점수 갱신을 재시도 하시겠습니까?", "점수 갱신 실패", JOptionPane.YES_NO_CANCEL_OPTION);
								
								if(isMoreCheck == JOptionPane.OK_OPTION)
									moreCheck = true;
								else
									moreCheck = false;
							}
						} while (moreCheck);
						revalidate();
						repaint();
						
						break;
						
					}
					
					revalidate();
					repaint();					
					
				}
			} catch (Exception e) {
				System.out.println(e + " : readInfo");
			} finally {				
				try {
					out.close();
					in.close();
					socket.close();
				} catch (IOException e) {
					System.out.println(e + " : readInfo close");
				}
			}
		}
		
	}

	public Tile[][] getTile() {
		return tile;
	}

	public void setTile(Tile[][] tile) {
		this.tile = tile;
	}

	public ArrayList<Gem> getGemList() {
		return gemList;
	}

	public void setGemList(ArrayList<Gem> gemList) {
		this.gemList = gemList;
	}

	public ButtonGroup getGemGroup() {
		return gemGroup;
	}

	public void setGemGroup(ButtonGroup gemGroup) {
		this.gemGroup = gemGroup;
	}

	public ImageIcon getLargeGem() {
		return largeGem;
	}

	public void setLargeGem(ImageIcon largeGem) {
		this.largeGem = largeGem;
	}

	public ImageIcon getSmallGem() {
		return smallGem;
	}

	public void setSmallGem(ImageIcon smallGem) {
		this.smallGem = smallGem;
	}

	public ImageIcon[] getOtherGem() {
		return otherGem;
	}

	public void setOtherGem(ImageIcon[] otherGem) {
		this.otherGem = otherGem;
	}

	public JButton getRotateBtn() {
		return rotateBtn;
	}

	public void setRotateBtn(JButton rotateBtn) {
		this.rotateBtn = rotateBtn;
	}

	public JButton getReverseBtn() {
		return reverseBtn;
	}

	public void setReverseBtn(JButton reverseBtn) {
		this.reverseBtn = reverseBtn;
	}

	public JTextArea getGuideMsg() {
		return guideMsg;
	}

	public void setGuideMsg(JTextArea guideMsg) {
		this.guideMsg = guideMsg;
	}

	public JLabel getTileBackground() {
		return tileBackground;
	}

	public void setTileBackground(JLabel tileBackground) {
		this.tileBackground = tileBackground;
	}

	public JLabel getPanelBackground() {
		return panelBackground;
	}

	public void setPanelBackground(JLabel panelBackground) {
		this.panelBackground = panelBackground;
	}

	public JScrollPane getGuideScroll() {
		return guideScroll;
	}

	public void setGuideScroll(JScrollPane guideScroll) {
		this.guideScroll = guideScroll;
	}

	public Map<Point, Tile> getTileMap() {
		return tileMap;
	}

	public void setTileMap(Map<Point, Tile> tileMap) {
		this.tileMap = tileMap;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public JTextArea getScoreMsg() {
		return scoreMsg;
	}

	public void setScoreMsg(JTextArea scoreMsg) {
		this.scoreMsg = scoreMsg;
	}

	public int getMyScore() {
		return myScore;
	}

	public void setMyScore(int myScore) {
		this.myScore = myScore;
	}

	public int getOtherScore() {
		return otherScore;
	}

	public void setOtherScore(int otherScore) {
		this.otherScore = otherScore;
	}

	public int[] getOthersScore() {
		return othersScore;
	}

	public void setOthersScore(int[] othersScore) {
		this.othersScore = othersScore;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	public ImageIcon getBlurIcon() {
		return blurIcon;
	}

	public void setBlurIcon(ImageIcon blurIcon) {
		this.blurIcon = blurIcon;
	}

	public char getGemTurn() {
		return gemTurn;
	}

	public void setGemTurn(char gemTurn) {
		this.gemTurn = gemTurn;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public DataInputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public JButton getNextTurnBtn() {
		return nextTurnBtn;
	}

	public void setNextTurnBtn(JButton nextTurnBtn) {
		this.nextTurnBtn = nextTurnBtn;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}

	public boolean[] getIsEndCheck() {
		return isEndCheck;
	}

	public void setIsEndCheck(boolean[] isEndCheck) {
		this.isEndCheck = isEndCheck;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public ChatPanel getChat() {
		return chat;
	}

	public void setChat(ChatPanel chat) {
		this.chat = chat;
	}

	public Thread getTestThread() {
		return testThread;
	}

	public void setTestThread(Thread testThread) {
		this.testThread = testThread;
	}

	public int getJoinPlayer() {
		return joinPlayer;
	}

	public void setJoinPlayer(int joinPlayer) {
		this.joinPlayer = joinPlayer;
	}

	public JLabel getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(JLabel timeLabel) {
		this.timeLabel = timeLabel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ImageIcon getRotateImg() {
		return rotateImg;
	}

	public void setRotateImg(ImageIcon rotateImg) {
		this.rotateImg = rotateImg;
	}

	public ImageIcon getRotate_clickImg() {
		return rotate_clickImg;
	}

	public void setRotate_clickImg(ImageIcon rotate_clickImg) {
		this.rotate_clickImg = rotate_clickImg;
	}

	public ImageIcon getReverseImg() {
		return reverseImg;
	}

	public void setReverseImg(ImageIcon reverseImg) {
		this.reverseImg = reverseImg;
	}

	public ImageIcon getReverse_clickImg() {
		return reverse_clickImg;
	}

	public void setReverse_clickImg(ImageIcon reverse_clickImg) {
		this.reverse_clickImg = reverse_clickImg;
	}

	public ImageIcon getSurrenderImg() {
		return surrenderImg;
	}

	public void setSurrenderImg(ImageIcon surrenderImg) {
		this.surrenderImg = surrenderImg;
	}

	public ImageIcon getSurrender_clickImg() {
		return surrender_clickImg;
	}

	public void setSurrender_clickImg(ImageIcon surrender_clickImg) {
		this.surrender_clickImg = surrender_clickImg;
	}

	public static int getRadius() {
		return RADIUS;
	}

	public static int getyPadding() {
		return Y_PADDING;
	}

	public void setGemName(String gemName) {
		this.gemName = gemName;
	}
	
	
	

}

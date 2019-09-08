import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.FontUIResource;

public class Main extends JFrame implements ActionListener{
	Game game;
	private JTextField inputIp;
	private JButton addMemberBtn;
	public static String ipAddr;
	public static Font digitalFont, BinggraeFont, BinggraeBoldFont;
	public static String id, nickname;
	public static ArrayList<String> adminList = new ArrayList<>();
	
	private LoginPanel loginPanel;
	private RoomPanel roomPanel = null;	

	private JPanel panel, titlePanel, logoPanel, menuPanel, inputGuideIdPanel, inputGuidePwPanel, inputPanel, settingPanel;
	private JTextField inputId;
	private JPasswordField inputPw;
	// 버튼 제외하고 패널별 이미지 링크
	ImageIcon background = new ImageIcon(this.getClass().getResource("img/mainbackground.jpg")); // 배경
	ImageIcon title = new ImageIcon(this.getClass().getResource("img/gemblo_title.png")); // 젬블로 타이틀
	ImageIcon logo = new ImageIcon(this.getClass().getResource("img/gemblo_logo.png")); // 젬블로 로고
	ImageIcon menu = new ImageIcon(this.getClass().getResource("img/menu.jpg")); // 하단 패널
	ImageIcon inputGuideId = new ImageIcon(this.getClass().getResource("img/inputGuideId.jpg")); // 아이디 입력안내
	ImageIcon inputGuidePw = new ImageIcon(this.getClass().getResource("img/inputGuidePw.jpg")); // 패스워드 입력안내
	ImageIcon setting = new ImageIcon(this.getClass().getResource("img/setting.jpg"));

	MusicList bgm = new MusicList();
	
	public Main() {
		// createFont		
		try {
			digitalFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("font/digital-7.ttf"));
			BinggraeFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("font/Binggrae.ttf"));
			BinggraeBoldFont = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("font/Binggrae-Bold.ttf"));
			
		} catch (Exception e2) {
			System.out.println("font : " + e2);
		}
		
		//setAdmin
		adminList.add("gunhee");
		adminList.add("suny");
		adminList.add("현준");
		adminList.add("moojin");
		
		setSize(1000, 750);
		setResizable(false); // 화면 크기 변경 금지
		setTitle("GEMBLO");
		setLocationRelativeTo(null); // 시작할 때 화면 중앙에서 열림
		
		
		// 전체(배경) 패널
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(background.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		
		// 젬블로 타이틀
		titlePanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(title.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}			
		};
		

		// 젬블로 로고
		logoPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(logo.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}			
		};
		
		
		// 하단 패널
		menuPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(menu.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		
		// 설정 패널(팝업)
		settingPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(setting.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		settingPopUp settingPopUp = new settingPopUp(this, "설정");


		inputPanel = new JPanel(); // 아이디, 패스워드 입력창
		panel.setLayout(null); // 배치관리자 : 절대 위치
		menuPanel.setLayout(null);

		inputPanel.setBackground(new Color(255, 0, 0, 0)); // 투명값
		// menuPanel.setBackground(new Color(255, 0, 0, 0));
		
		
		// 아이디 & 패스워드 입력 안내 (입력창 왼쪽)
		inputGuideIdPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(inputGuideId.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		inputGuidePwPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(inputGuidePw.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		menuPanel.add(inputGuideIdPanel);
		menuPanel.add(inputGuidePwPanel);
		
		
		// 인풋패널 : 아이디, 패스워드 입력 - 크기 조절 필요
		Font font = new Font("Hobo Std", Font.BOLD, 40);
		
		GridLayout inputPanelLayout = new GridLayout(0,1);
		inputPanelLayout.setVgap(15);
		inputPanel.setLayout(inputPanelLayout);
		
		inputId = new JTextField(7);
		inputId.setFont(BinggraeBoldFont.deriveFont(30f));
		inputId.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		inputId.setActionCommand("로그인");
		inputId.addActionListener(this);
		inputPanel.add(inputId);
		inputPw = new JPasswordField(7);
		inputPw.setFont(font);
		inputPw.addActionListener(this);
		inputPw.setActionCommand("로그인");
		inputPanel.add(inputPw);
		
		
		// 최하단에 버튼 3개(로그인, 가입, 설정) 추가, 마우스 올리면 색 바뀜 추가
		JButton loginBtn = new JButton(new ImageIcon()) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnLoginPress.png")));
				else
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnLogin.png")));
			}
		};
		
		
		JButton joinBtn = new JButton(new ImageIcon()) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnJoinPress.png")));
				else
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnJoin.png")));
			}
		};
		
		
		JButton settingBtn = new JButton(new ImageIcon()) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnSettingPress.png")));
				else
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnSetting.png")));
			}
		};
		
		
		loginBtn.setBorderPainted(false); // 버튼 선택 시 외곽선 제거. 안 하면 버튼 이미지 주변으로 테두리선 생김.
		loginBtn.setFocusPainted(false); // 버튼이 선택되었을 때 생기는 테두리 사용 안 함
		loginBtn.setContentAreaFilled(false); // 버튼의 내용영역 채우기 안 함. 안 하면 버튼 이미지 주변에 창 생김.
		loginBtn.addActionListener(this);
		loginBtn.setActionCommand("로그인");
		menuPanel.add(loginBtn);
		
		joinBtn.setBorderPainted(false);
		joinBtn.setFocusPainted(false);
		joinBtn.setContentAreaFilled(false);
		joinBtn.addActionListener(this);
		joinBtn.setActionCommand("가입");
		menuPanel.add(joinBtn);
		
		settingBtn.setBorderPainted(false);
		settingBtn.setFocusPainted(false);
		settingBtn.setContentAreaFilled(false);
		menuPanel.add(settingBtn);
		settingBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingPopUp.setVisible(true);
			}	
		});

		

		// 전체 패널에 각 패널 추가
		panel.add(titlePanel);
		panel.add(logoPanel);
		panel.add(menuPanel);
		menuPanel.add(inputPanel);
		
		
		// 각 패널의 절대 위치 & 크기
		logoPanel.setBounds(50, 30, 300, 300); // 젬블로 로고
		titlePanel.setBounds(200, 80, 600, 310); // 젬블로 타이틀
		menuPanel.setBounds(329, 461, 342, 174); // 하단 패널
		inputGuideIdPanel.setBounds(30, 14, 45, 50); // 아이디 입력 안내
		inputGuidePwPanel.setBounds(20, 62, 45, 50); // 패스워드 입력 안내
		inputPanel.setBounds(85, 18, 235, 90); // 아이디, 패스워드 입력
		
		loginBtn.setBounds(12, 108, 106, 59); // 이하 버튼패널
		joinBtn.setBounds(118, 108, 106, 59);
		settingBtn.setBounds(224, 108, 106, 59);


		add(panel);		
		
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if(roomPanel != null) {
				try {
					roomPanel.getRoomOut().writeUTF("(UserOut)" + roomPanel.getId());
				} catch (Exception e1) {						
					e1.printStackTrace();
				}
			}				
			System.exit(0);
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		
		
		
		
//		setTitle("젬블로");
//		setSize(1000, 750);
//		setResizable(false);
		setUIFont(new FontUIResource(BinggraeFont.deriveFont(15f)));
//		
//		inputIp = new JTextField("114.199.210.107",20);	
//		
//		loginPanel = new LoginPanel();
//		loginPanel.addActionL(this);
//		loginPanel.add(addMemberBtn = new JButton("회원가입"));
//		addMemberBtn.addActionListener(this);
//		add(loginPanel);
//		loginPanel.add(inputIp);
//		
//		setLocationRelativeTo(null); // 실행 시 화면 중앙에 뜨기
//		addWindowListener(new WindowListener() {
//			public void windowOpened(WindowEvent e) {}		
//			public void windowIconified(WindowEvent e) {}
//			public void windowDeiconified(WindowEvent e) {}
//			public void windowDeactivated(WindowEvent e) {}
//			public void windowClosing(WindowEvent e) {
//				if(roomPanel != null) {
//					try {
//						roomPanel.getRoomOut().writeUTF("(UserOut)" + roomPanel.getId());
//					} catch (IOException e1) {						
//						e1.printStackTrace();
//					}
//				}				
//				System.exit(0);
//			}
//			public void windowClosed(WindowEvent e) {}
//			public void windowActivated(WindowEvent e) {}
//		});
//
		setVisible(true);
//		
		bgm.mainThemeStart();
	}
	
	class settingPopUp extends JDialog implements ActionListener{
		private static final long serialVersionUID = 1L;

		// 팝업창에 나타날 내용 中 OK버튼
		// JButton okBtn = new JButton("OK");
		
		JButton okBtn = new JButton(new ImageIcon()) {
			private static final long serialVersionUID = 1L;
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if(this.getModel().isRollover())
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnOkPress.png")));
				else
					this.setIcon(new ImageIcon(this.getClass().getResource("img/btnOk.png")));
			}
		};
		
		JCheckBox bgm1, bgm2, bgm3, bgm4, bgm5, bgmOff;
		ButtonGroup bgmGroup;
		int num = 1;
		
		public settingPopUp(JFrame frame, String title) {
			super(frame, title);
			setSize(498, 527); // 팝업창 크기, settingPanel 이미지보다 가로 +18, 세로 +47
			setLocationRelativeTo(null); // 시작할 때 화면 중앙에서 열림
			setLayout(null);
			
			// 확인 버튼과 패널 이미지 추가
			add(settingPanel);
			
			okBtn.setBorderPainted(false);
			okBtn.setFocusPainted(false);

			// 팝업창 배치관리자 절대위치			
			settingPanel.setBounds(0, 0, 480, 480);
			settingPanel.setLayout(null);			
			okBtn.setBounds(190, 380, 106, 59); // 버튼 위치 하단 중앙
			
			JLabel ipLabel = new JLabel("서버의 IP주소");
			ipLabel.setFont(BinggraeBoldFont.deriveFont(25f));
			ipLabel.setBounds(20, 50, 200, 40);
			ipLabel.setHorizontalAlignment(JLabel.CENTER);
			
			inputIp = new JTextField(10);
			inputIp.setFont(BinggraeFont.deriveFont(15f));
			
			// 여기가 서버의 주소 설정!!!
			
			inputIp.setText("");
			inputIp.setBounds(250, 50, 180, 40);
			
			JLabel bgmLabel = new JLabel("BGM 설정");
			bgmLabel.setFont(BinggraeBoldFont.deriveFont(25f));
			bgmLabel.setBounds(20, 150, 200, 40);
			bgmLabel.setHorizontalAlignment(JLabel.CENTER);
			
			bgmGroup = new ButtonGroup();
			
			bgm1 = new JCheckBox("1번 BGM");
			bgm1.setFont(BinggraeFont.deriveFont(15f));
			bgm1.setOpaque(false);
			bgm1.setBounds(250, 100, 100, 50);
			bgm1.addActionListener(this);
			bgm1.setSelected(true);
			bgmGroup.add(bgm1);
			
			bgm2 = new JCheckBox("2번 BGM");
			bgm2.setFont(BinggraeFont.deriveFont(15f));
			bgm2.setOpaque(false);
			bgm2.setBounds(350, 100, 100, 50);
			bgm2.addActionListener(this);
			bgmGroup.add(bgm2);
			
			bgm3 = new JCheckBox("3번 BGM");
			bgm3.setFont(BinggraeFont.deriveFont(15f));
			bgm3.setOpaque(false);
			bgm3.setBounds(250, 150, 100, 50);
			bgm3.addActionListener(this);
			bgmGroup.add(bgm3);
			
			bgm4 = new JCheckBox("4번 BGM");
			bgm4.setFont(BinggraeFont.deriveFont(15f));
			bgm4.setOpaque(false);
			bgm4.setBounds(350, 150, 100, 50);
			bgm4.addActionListener(this);
			bgmGroup.add(bgm4);
			
			bgm5 = new JCheckBox("5번 BGM");
			bgm5.setFont(BinggraeFont.deriveFont(15f));
			bgm5.setOpaque(false);
			bgm5.setBounds(250, 200, 100, 50);
			bgm5.addActionListener(this);
			bgmGroup.add(bgm5);
			
			bgmOff = new JCheckBox("BGM OFF");
			bgmOff.setFont(BinggraeFont.deriveFont(15f));
			bgmOff.setOpaque(false);
			bgmOff.setBounds(350, 200, 100, 50);
			bgmOff.addActionListener(this);
			bgmGroup.add(bgmOff);
			
			
			settingPanel.add(ipLabel);
			settingPanel.add(inputIp);
			settingPanel.add(bgmLabel);
			settingPanel.add(bgm1);
			settingPanel.add(okBtn);
			settingPanel.add(bgm2);
			settingPanel.add(bgm3);
			settingPanel.add(bgm4);
			settingPanel.add(bgm5);
			settingPanel.add(bgmOff);
			
			// 팝업창의 OK 버튼에 액션리스너
			// 팝업창의 OK 버튼이 선택되면 팝업창이 화면에서 사라지게 함
			okBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();
			
			switch(action) {
			case "1번 BGM":
				bgmStop(num);
				bgm.mainThemeStart();
				num = 1;
				break;
			case "2번 BGM":
				bgmStop(num);
				bgm.mainTheme2Start();
				num = 2;
				break;
			case "3번 BGM":
				bgmStop(num);
				bgm.mainTheme3Start();
				num = 3;
				break;
			case "4번 BGM":
				bgmStop(num);
				bgm.mainTheme4Start();
				num = 4;
				break;
			case "5번 BGM":
				bgmStop(num);
				bgm.mainTheme5Start();
				num = 5;
				break;
			case "BGM OFF":
				bgmStop(num);
				break;
				
			}
		}
		
		public void bgmStop(int num) {
			if(num == 1)
				bgm.mainThemeStop();
			
			if(num == 2)
				bgm.mainTheme2Stop();
			
			if(num == 3)
				bgm.mainTheme3Stop();
			
			if(num == 4)
				bgm.mainTheme4Stop();
			
			if(num == 5)
				bgm.mainTheme5Stop();
		}
	}
	
	
	
	public static void main(String[] args) {
		new Main();
	}

	@Override
	public void actionPerformed(ActionEvent e) {		
		ipAddr = inputIp.getText();
		new MusicList().buttonClick();
		
		String action = e.getActionCommand();
		
		switch(action) {
		case "로그인":
			String id = inputId.getText();
			String pa = String.valueOf(inputPw.getPassword());
			if(LoginPanel.login(id, pa)) {
				roomPanel = new RoomPanel(ipAddr, nickname);
				roomPanel.setMainFrame(this);
				
				this.getContentPane().removeAll();
				add(roomPanel);
				
				revalidate();
				repaint();
			}
			break;
		case "가입":
			try {
			Member memberFrame = new Member();
			memberFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			memberFrame.setLocationRelativeTo(null);
					
		} catch (SQLException e1) {
			JOptionPane.showConfirmDialog(null, "회원가입 오류 : " + e1);
		}
			break;
		}
	}
	
	public void setUIFont(FontUIResource f) { // 폰트 일괄 적용 http://wony.kr/java-swing-font-method/
		Enumeration keys = UIManager.getDefaults().keys();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			
			if (value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}

}

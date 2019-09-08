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
	// ��ư �����ϰ� �гκ� �̹��� ��ũ
	ImageIcon background = new ImageIcon(this.getClass().getResource("img/mainbackground.jpg")); // ���
	ImageIcon title = new ImageIcon(this.getClass().getResource("img/gemblo_title.png")); // ����� Ÿ��Ʋ
	ImageIcon logo = new ImageIcon(this.getClass().getResource("img/gemblo_logo.png")); // ����� �ΰ�
	ImageIcon menu = new ImageIcon(this.getClass().getResource("img/menu.jpg")); // �ϴ� �г�
	ImageIcon inputGuideId = new ImageIcon(this.getClass().getResource("img/inputGuideId.jpg")); // ���̵� �Է¾ȳ�
	ImageIcon inputGuidePw = new ImageIcon(this.getClass().getResource("img/inputGuidePw.jpg")); // �н����� �Է¾ȳ�
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
		adminList.add("����");
		adminList.add("moojin");
		
		setSize(1000, 750);
		setResizable(false); // ȭ�� ũ�� ���� ����
		setTitle("GEMBLO");
		setLocationRelativeTo(null); // ������ �� ȭ�� �߾ӿ��� ����
		
		
		// ��ü(���) �г�
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(background.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		
		// ����� Ÿ��Ʋ
		titlePanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(title.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}			
		};
		

		// ����� �ΰ�
		logoPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(logo.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}			
		};
		
		
		// �ϴ� �г�
		menuPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(menu.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		
		// ���� �г�(�˾�)
		settingPanel = new JPanel() {
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				g.drawImage(setting.getImage(), 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		settingPopUp settingPopUp = new settingPopUp(this, "����");


		inputPanel = new JPanel(); // ���̵�, �н����� �Է�â
		panel.setLayout(null); // ��ġ������ : ���� ��ġ
		menuPanel.setLayout(null);

		inputPanel.setBackground(new Color(255, 0, 0, 0)); // ����
		// menuPanel.setBackground(new Color(255, 0, 0, 0));
		
		
		// ���̵� & �н����� �Է� �ȳ� (�Է�â ����)
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
		
		
		// ��ǲ�г� : ���̵�, �н����� �Է� - ũ�� ���� �ʿ�
		Font font = new Font("Hobo Std", Font.BOLD, 40);
		
		GridLayout inputPanelLayout = new GridLayout(0,1);
		inputPanelLayout.setVgap(15);
		inputPanel.setLayout(inputPanelLayout);
		
		inputId = new JTextField(7);
		inputId.setFont(BinggraeBoldFont.deriveFont(30f));
		inputId.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		inputId.setActionCommand("�α���");
		inputId.addActionListener(this);
		inputPanel.add(inputId);
		inputPw = new JPasswordField(7);
		inputPw.setFont(font);
		inputPw.addActionListener(this);
		inputPw.setActionCommand("�α���");
		inputPanel.add(inputPw);
		
		
		// ���ϴܿ� ��ư 3��(�α���, ����, ����) �߰�, ���콺 �ø��� �� �ٲ� �߰�
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
		
		
		loginBtn.setBorderPainted(false); // ��ư ���� �� �ܰ��� ����. �� �ϸ� ��ư �̹��� �ֺ����� �׵θ��� ����.
		loginBtn.setFocusPainted(false); // ��ư�� ���õǾ��� �� ����� �׵θ� ��� �� ��
		loginBtn.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� �� ��. �� �ϸ� ��ư �̹��� �ֺ��� â ����.
		loginBtn.addActionListener(this);
		loginBtn.setActionCommand("�α���");
		menuPanel.add(loginBtn);
		
		joinBtn.setBorderPainted(false);
		joinBtn.setFocusPainted(false);
		joinBtn.setContentAreaFilled(false);
		joinBtn.addActionListener(this);
		joinBtn.setActionCommand("����");
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

		

		// ��ü �гο� �� �г� �߰�
		panel.add(titlePanel);
		panel.add(logoPanel);
		panel.add(menuPanel);
		menuPanel.add(inputPanel);
		
		
		// �� �г��� ���� ��ġ & ũ��
		logoPanel.setBounds(50, 30, 300, 300); // ����� �ΰ�
		titlePanel.setBounds(200, 80, 600, 310); // ����� Ÿ��Ʋ
		menuPanel.setBounds(329, 461, 342, 174); // �ϴ� �г�
		inputGuideIdPanel.setBounds(30, 14, 45, 50); // ���̵� �Է� �ȳ�
		inputGuidePwPanel.setBounds(20, 62, 45, 50); // �н����� �Է� �ȳ�
		inputPanel.setBounds(85, 18, 235, 90); // ���̵�, �н����� �Է�
		
		loginBtn.setBounds(12, 108, 106, 59); // ���� ��ư�г�
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
		
		
		
		
		
//		setTitle("�����");
//		setSize(1000, 750);
//		setResizable(false);
		setUIFont(new FontUIResource(BinggraeFont.deriveFont(15f)));
//		
//		inputIp = new JTextField("114.199.210.107",20);	
//		
//		loginPanel = new LoginPanel();
//		loginPanel.addActionL(this);
//		loginPanel.add(addMemberBtn = new JButton("ȸ������"));
//		addMemberBtn.addActionListener(this);
//		add(loginPanel);
//		loginPanel.add(inputIp);
//		
//		setLocationRelativeTo(null); // ���� �� ȭ�� �߾ӿ� �߱�
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

		// �˾�â�� ��Ÿ�� ���� �� OK��ư
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
			setSize(498, 527); // �˾�â ũ��, settingPanel �̹������� ���� +18, ���� +47
			setLocationRelativeTo(null); // ������ �� ȭ�� �߾ӿ��� ����
			setLayout(null);
			
			// Ȯ�� ��ư�� �г� �̹��� �߰�
			add(settingPanel);
			
			okBtn.setBorderPainted(false);
			okBtn.setFocusPainted(false);

			// �˾�â ��ġ������ ������ġ			
			settingPanel.setBounds(0, 0, 480, 480);
			settingPanel.setLayout(null);			
			okBtn.setBounds(190, 380, 106, 59); // ��ư ��ġ �ϴ� �߾�
			
			JLabel ipLabel = new JLabel("������ IP�ּ�");
			ipLabel.setFont(BinggraeBoldFont.deriveFont(25f));
			ipLabel.setBounds(20, 50, 200, 40);
			ipLabel.setHorizontalAlignment(JLabel.CENTER);
			
			inputIp = new JTextField(10);
			inputIp.setFont(BinggraeFont.deriveFont(15f));
			
			// ���Ⱑ ������ �ּ� ����!!!
			
			inputIp.setText("");
			inputIp.setBounds(250, 50, 180, 40);
			
			JLabel bgmLabel = new JLabel("BGM ����");
			bgmLabel.setFont(BinggraeBoldFont.deriveFont(25f));
			bgmLabel.setBounds(20, 150, 200, 40);
			bgmLabel.setHorizontalAlignment(JLabel.CENTER);
			
			bgmGroup = new ButtonGroup();
			
			bgm1 = new JCheckBox("1�� BGM");
			bgm1.setFont(BinggraeFont.deriveFont(15f));
			bgm1.setOpaque(false);
			bgm1.setBounds(250, 100, 100, 50);
			bgm1.addActionListener(this);
			bgm1.setSelected(true);
			bgmGroup.add(bgm1);
			
			bgm2 = new JCheckBox("2�� BGM");
			bgm2.setFont(BinggraeFont.deriveFont(15f));
			bgm2.setOpaque(false);
			bgm2.setBounds(350, 100, 100, 50);
			bgm2.addActionListener(this);
			bgmGroup.add(bgm2);
			
			bgm3 = new JCheckBox("3�� BGM");
			bgm3.setFont(BinggraeFont.deriveFont(15f));
			bgm3.setOpaque(false);
			bgm3.setBounds(250, 150, 100, 50);
			bgm3.addActionListener(this);
			bgmGroup.add(bgm3);
			
			bgm4 = new JCheckBox("4�� BGM");
			bgm4.setFont(BinggraeFont.deriveFont(15f));
			bgm4.setOpaque(false);
			bgm4.setBounds(350, 150, 100, 50);
			bgm4.addActionListener(this);
			bgmGroup.add(bgm4);
			
			bgm5 = new JCheckBox("5�� BGM");
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
			
			// �˾�â�� OK ��ư�� �׼Ǹ�����
			// �˾�â�� OK ��ư�� ���õǸ� �˾�â�� ȭ�鿡�� ������� ��
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
			case "1�� BGM":
				bgmStop(num);
				bgm.mainThemeStart();
				num = 1;
				break;
			case "2�� BGM":
				bgmStop(num);
				bgm.mainTheme2Start();
				num = 2;
				break;
			case "3�� BGM":
				bgmStop(num);
				bgm.mainTheme3Start();
				num = 3;
				break;
			case "4�� BGM":
				bgmStop(num);
				bgm.mainTheme4Start();
				num = 4;
				break;
			case "5�� BGM":
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
		case "�α���":
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
		case "����":
			try {
			Member memberFrame = new Member();
			memberFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			
			memberFrame.setLocationRelativeTo(null);
					
		} catch (SQLException e1) {
			JOptionPane.showConfirmDialog(null, "ȸ������ ���� : " + e1);
		}
			break;
		}
	}
	
	public void setUIFont(FontUIResource f) { // ��Ʈ �ϰ� ���� http://wony.kr/java-swing-font-method/
		Enumeration keys = UIManager.getDefaults().keys();
		
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			
			if (value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}

}

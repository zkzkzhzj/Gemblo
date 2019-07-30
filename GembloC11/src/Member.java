import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Member extends JFrame{
	public static Connection makeConnection() {
		
		//-----------------------------------디비연결하기-----------------------------------//
		String url="jdbc:mysql://"+Main.ipAddr+":3306/gemblo";
		String id = "root";
		String password="root";
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("드라이버 적재 성공");
			con=DriverManager.getConnection(url, id, password);
			System.out.println("디비연결성공");
		}catch(ClassNotFoundException e) {
			System.out.println("드라이브 없음");
		}catch(SQLException e) {
			System.out.println("연결에 실패");
		}
		return con;
	}
	
	//-----------------------------------프레임 구성-----------------------------------//
		private JPanel dbpanel;
		//private JLabel Lid,Lpass,Lpass2,Lnic;
		private JButton Bid,Bnic,Bdb;
		private JTextField Tid,Tnic;
		private JPasswordField Tpass,Tpass2;
		
		public Member() throws SQLException {
			Connection con = makeConnection();
			Statement stmt = con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from blomember");
			
			ImageIcon icon=new ImageIcon(Main.class.getResource("img/blo.png"));
			
			setSize(700,600);
			setTitle("회원가입");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setResizable(false);
			
			dbpanel=new JPanel() {
				public void paintComponent(Graphics g) {
					g.drawImage(icon.getImage(),0,0,null);
					setOpaque(false);
					super.paintComponent(g);
				}
			};
			dbpanel.setLayout(null);
		    
			
			
			
			//tlabel=new JLabel("회원가입");
			//tlabel.setFont(new Font("HY헤드라인M",30,30));
			//tlabel.setForeground(Color.white);
			Bdb = new JButton(new ImageIcon(Main.class.getResource("img/Bdb.png"))) {
				@Override
				protected void paintComponent(Graphics g) {
					// TODO Auto-generated method stub
					super.paintComponent(g);
					
					if(this.getModel().isRollover())
						this.setIcon(new ImageIcon(Main.class.getResource("img/Bdb.png")));
					else
						this.setIcon(new ImageIcon(Main.class.getResource("img/bdbY.png")));
				}
			};
			
			//Lid=new JLabel("아이디");
			//Lid.setFont(new Font("한컴 윤고딕 250",25,25));
			//Lid.setForeground(Color.black);
			Tid=new JTextField(60);//아이디입력창
			Tid.setHorizontalAlignment(JTextField.CENTER);
			Bid=new JButton(new ImageIcon(Main.class.getResource("img/bnic.png"))) {
				@Override
				protected void paintComponent(Graphics g) {
					// TODO Auto-generated method stub
					super.paintComponent(g);
					
					if(this.getModel().isRollover())
						this.setIcon(new ImageIcon(Main.class.getResource("img/button2.png")));
					else
						this.setIcon(new ImageIcon(Main.class.getResource("img/bnic.png")));
				}
			};
			
			//Lpass=new JLabel("비밀번호");
			//Lpass.setForeground(Color.black);
			//Lpass.setFont(new Font("한컴 윤고딕 250",25,25));
			//Lpass2=new JLabel("비밀번호 확인");
			//Lpass2.setForeground(Color.black);
			//Lpass2.setFont(new Font("한컴 윤고딕 250",25,25));
			Tpass=new JPasswordField(60); //비번입력창
			Tpass2=new JPasswordField(60);
			
			Tpass.setFont(Main.digitalFont.deriveFont(20f));
			Tpass2.setFont(Main.digitalFont.deriveFont(20f));
			
			//Lnic=new JLabel("닉네임");
			//Lnic.setForeground(Color.black);
			//Lnic.setFont(new Font("한컴 윤고딕 250",25,25));	
			Tnic=new JTextField(60); //닉네임입력창
			Tnic.setHorizontalAlignment(JTextField.CENTER);
			Bnic=new JButton(new ImageIcon(Main.class.getResource("img/bnic.png"))) {
				@Override
				protected void paintComponent(Graphics g) {
					// TODO Auto-generated method stub
					super.paintComponent(g);
					
					if(this.getModel().isRollover())
						this.setIcon(new ImageIcon(Main.class.getResource("img/button2.png")));
					else
						this.setIcon(new ImageIcon(Main.class.getResource("img/bnic.png")));
				}
			};
//			Bnic.setFont(new Font("한컴 윤고딕 250",20,20));
			Bnic.setMargin(new Insets(-1,-1,-1,-1));
			Bnic.setBorderPainted(false);
			Bid.setBorderPainted(false);
			Bdb.setBorderPainted(false);
			
			//dbpanel.add(tlabel); 
			//dbpanel.add(Lid);
			//dbpanel.add(Lpass);	dbpanel.add(Lnic);dbpanel.add(Lpass2); 
			dbpanel.add(Tid); dbpanel.add(Tpass); dbpanel.add(Tpass2); dbpanel.add(Tnic);
			dbpanel.add(Bid); dbpanel.add(Bnic); dbpanel.add(Bdb);
			
			
			//tlabel.setBounds(300, 0, 200, 100);
			//Lid.setBounds(120, 0, 250, 300);
			//Lpass.setBounds(100, 0, 200, 350);
			//Lpass2.setBounds(100, 0, 200, 450);
			//Lnic.setBounds(100, 0, 200, 550);
			Tid.setBounds(260,150,150,40);
			Tpass.setBounds(260,200,150,40);
			Tpass2.setBounds(260,250,150,40);
			Tnic.setBounds(260,300,150,40);
			Bid.setBounds(440,150,105,45);
			Bnic.setBounds(440,300,105,45);
			Bdb.setBounds(280, 400, 105, 45);
		
			
   //-----------------------------------버튼에 액션추가하기-----------------------------------//
			
		Bid.addActionListener(new ActionListener() { //아이디중복체크
				public void actionPerformed(ActionEvent event) {
					try {	
						String id=Tid.getText();
					boolean rss=false;
					ResultSet bs=stmt.executeQuery("select * from blomember where id='"+id+"'");						
							if(bs.next())
							JOptionPane.showMessageDialog(null,"중복된 아이디입니다.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
							else 
							JOptionPane.showMessageDialog(null,"사용가능한 아이디입니다.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
				}catch(SQLException e){
					System.out.println(e.getMessage());	
				}
				}
		});
		
		Bnic.addActionListener(new ActionListener() { //닉네임 중복체크
			public void actionPerformed(ActionEvent event) {
				try {	
					String nic=Tnic.getText();
				ResultSet bs=stmt.executeQuery("select * from blomember where nickname='"+nic+"'");						
						if(bs.next())
						JOptionPane.showMessageDialog(null,"중복된 닉네임입니다.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
						else 
						JOptionPane.showMessageDialog(null,"사용가능한 닉네임입니다.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}catch(SQLException e){
				System.out.println(e.getMessage());	
			}
			}
	});
			
			Bdb.addActionListener(new ActionListener() { //이상없으면 DB에 데이터추가하기
				public void actionPerformed(ActionEvent event) {				
    //------------------------비밀번호 맞게 입력했는지 보고 DB에 데이터추가하기------------------------------//				
					
						
					String id=Tid.getText(); //텍스트필드꺼읽어오는거
					String pass=String.valueOf(Tpass.getPassword());
					String pass2=String.valueOf(Tpass2.getPassword());
					String nic=Tnic.getText();
					
					if(id.equals("")||pass.equals("")||pass2.equals("")||nic.equals("")) {
						JOptionPane.showMessageDialog(null,"빈칸이 있습니다.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					}
					else {
					if(pass.equals(pass2)) {
					try {					
						String s=String.format("insert into blomember(id, password, nickname) values('%s','%s','%s')",id,pass,nic);
						int check = stmt.executeUpdate(s); //테이블에 데이터 추가	
						System.out.println("데이터베이스에 추가 성공");
						if(check == 1) {
							JOptionPane.showMessageDialog(null,"회원가입이 되었습니다.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
							
							Tid.setText(null);
							Tpass.setText(null);
							Tpass2.setText(null);
							Tnic.setText(null);
							con.close();
							dispose();
						}
						
								
					}catch(SQLException e) {
						System.out.println(e.getMessage());						
					}
				}else {
					JOptionPane.showMessageDialog(null,"비밀번호를 확인해주세요","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
				}
				}		
				}
			});//Bdb.addActionListener의 닫힘
			
			
			add(dbpanel);
			setVisible(true);
		}

}







//import java.awt.Color;
//import java.awt.FlowLayout;
//import java.awt.Font;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JPasswordField;
//import javax.swing.JTextField;
//
//public class Member extends JFrame {
//	public static Connection makeConnection() {
//
//		// -----------------------------------디비연결하기-----------------------------------//
//		String url = "jdbc:mysql://"+Main.ipAddr+":3306/Gemblo";
//		String id = "root";
//		String password = "root";
//		Connection con = null;
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			System.out.println("드라이버 적재 성공");
//			con = DriverManager.getConnection(url, id, password);
//			System.out.println("디비연결성공");
//		} catch (ClassNotFoundException e) {
//			System.out.println("드라이브 없음");
//		} catch (SQLException e) {
//			System.out.println("연결에 실패");
//		}
//		return con;
//	}
//
//	// -----------------------------------프레임 구성-----------------------------------//
//	private JPanel dbpanel;
//	private JLabel tlabel, Lid, Lpass, Lpass2, Lnic;
//	private JButton Bid, Bnic, Bdb;
//	private JTextField Tid, Tnic;
//	private JPasswordField Tpass, Tpass2;
//	
//	// 중복검사
//	private boolean isId = false, isNickname = false;
//
//	public Member() throws SQLException {
//		Connection con = makeConnection();
//		Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery("select * from blomember");
//
//		setSize(750, 550);
//		setTitle("회원가입");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setResizable(false);
//
//		dbpanel = new JPanel();
//		dbpanel.setLayout(null);
//		dbpanel.setBackground(new Color(58, 61, 52));
//
//		tlabel = new JLabel("회원가입");
//		tlabel.setFont(new Font("HY헤드라인M", 30, 30));
//		tlabel.setForeground(Color.white);
//		Bdb = new JButton("회원가입");
//		Bdb.setFont(new Font("한컴 윤고딕 250", 20, 20));
//		Bdb.setForeground(Color.white);
//		Bdb.setBackground(new Color(120, 180, 5));
//
//		Lid = new JLabel("아이디");
//		Lid.setFont(new Font("한컴 윤고딕 250", 30, 30));
//		Lid.setForeground(Color.white);
//		Tid = new JTextField(60);// 아이디입력창
//		Tid.setHorizontalAlignment(JTextField.CENTER);
//		Bid = new JButton("중복확인");
//		Bid.setFont(new Font("한컴 윤고딕 250", 20, 20));
//		Bid.setForeground(Color.white);
//		Bid.setBackground(new Color(120, 180, 5));
//
//		Lpass = new JLabel("비밀번호");
//		Lpass.setForeground(Color.white);
//		Lpass.setFont(new Font("한컴 윤고딕 250", 30, 30));
//		Lpass2 = new JLabel("비밀번호 확인");
//		Lpass2.setForeground(Color.white);
//		Lpass2.setFont(new Font("한컴 윤고딕 250", 30, 30));
//		Tpass = new JPasswordField(60); // 비번입력창
//		Tpass2 = new JPasswordField(60);
//
//		Lnic = new JLabel("닉네임");
//		Lnic.setForeground(Color.white);
//		Lnic.setFont(new Font("한컴 윤고딕 250", 30, 30));
//		Tnic = new JTextField(60); // 닉네임입력창
//		Tnic.setHorizontalAlignment(JTextField.CENTER);
//		Bnic = new JButton("중복확인");
//		Bnic.setFont(new Font("한컴 윤고딕 250", 20, 20));
//		Bnic.setForeground(Color.white);
//		Bnic.setBackground(new Color(120, 180, 5));
//
//		dbpanel.add(tlabel);
//		dbpanel.add(Lid);
//		dbpanel.add(Lpass);
//		dbpanel.add(Lnic);
//		dbpanel.add(Lpass2);
//		dbpanel.add(Tid);
//		dbpanel.add(Tpass);
//		dbpanel.add(Tpass2);
//		dbpanel.add(Tnic);
//		dbpanel.add(Bid);
//		dbpanel.add(Bnic);
//		dbpanel.add(Bdb);
//
//		tlabel.setBounds(250, 0, 200, 100);
//		Lid.setBounds(100, 0, 200, 250);
//		Lpass.setBounds(100, 0, 200, 350);
//		Lpass2.setBounds(100, 0, 200, 450);
//		Lnic.setBounds(100, 0, 200, 550);
//		Tid.setBounds(310, 110, 150, 40);
//		Tpass.setBounds(310, 160, 150, 40);
//		Tpass2.setBounds(310, 210, 150, 40);
//		Tnic.setBounds(310, 260, 150, 40);
//		Bid.setBounds(500, 110, 120, 50);
//		Bnic.setBounds(500, 260, 120, 50);
//		Bdb.setBounds(300, 350, 120, 50);
//
//		// -----------------------------------버튼에 액션추가하기-----------------------------------//
//
//		Bid.addActionListener(new ActionListener() { // 아이디중복체크
//			public void actionPerformed(ActionEvent event) {
//				try {
//					String id = Tid.getText();
//					ResultSet bs = stmt.executeQuery("select * from blomember where id='" + id + "'");
//					if (bs.next())
//						JOptionPane.showMessageDialog(null, "중복된 아이디입니다.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					else
//						JOptionPane.showMessageDialog(null, "사용가능한 아이디입니다.", "INFORMATION_MESSAGE",
//								JOptionPane.INFORMATION_MESSAGE);
//				} catch (SQLException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//		});
//
//		Bnic.addActionListener(new ActionListener() { // 닉네임 중복체크
//			public void actionPerformed(ActionEvent event) {
//				try {
//					String nic = Tnic.getText();
//					ResultSet bs = stmt.executeQuery("select * from blomember where nickname='" + nic + "'");
//					if (bs.next())
//						JOptionPane.showMessageDialog(null, "중복된 닉네임입니다.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					else
//						JOptionPane.showMessageDialog(null, "사용가능한 닉네임입니다.", "INFORMATION_MESSAGE",
//								JOptionPane.INFORMATION_MESSAGE);
//				} catch (SQLException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//		});
//
//		Bdb.addActionListener(new ActionListener() { // 이상없으면 DB에 데이터추가하기
//			public void actionPerformed(ActionEvent event) {
//				// ------------------------비밀번호 맞게 입력했는지 보고 DB에 데이터추가하기------------------------------//
//
//				String id = Tid.getText(); // 텍스트필드꺼읽어오는거
//				String pass = String.valueOf(Tpass.getPassword());
//				String pass2 = String.valueOf(Tpass2.getPassword());
//				String nic = Tnic.getText();
//
//				if (id.equals("") || pass.equals("") || pass2.equals("") || nic.equals("")) {
//					JOptionPane.showMessageDialog(null, "빈칸이 있습니다.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//				} 				
//				else {
//					if (pass.equals(pass2)) {
//						try {
//							String s = String.format("insert into blomember(id, password, nickname) values('%s','%s','%s')", id, pass, nic);
//							stmt.executeUpdate(s); // 테이블에 데이터 추가
//							System.out.println("데이터베이스에 추가 성공");
//							JOptionPane.showMessageDialog(null, "회원가입이 되었습니다.", "INFORMATION_MESSAGE",
//									JOptionPane.INFORMATION_MESSAGE);
//							con.close();
//							dispose();
//						} catch (SQLException e) {
//							System.out.println(e.getMessage());
//						}
//					} else {
//						JOptionPane.showMessageDialog(null, "비밀번호를 확인해주세요", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					}
//				}
//			}
//		});// Bdb.addActionListener의 닫힘
//
//		add(dbpanel);
//		setVisible(true);
//	}
//}

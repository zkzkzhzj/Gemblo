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
		
		//-----------------------------------��񿬰��ϱ�-----------------------------------//
		String url="jdbc:mysql://"+Main.ipAddr+":3306/gemblo";
		String id = "root";
		String password="root";
		Connection con=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("����̹� ���� ����");
			con=DriverManager.getConnection(url, id, password);
			System.out.println("��񿬰Ἲ��");
		}catch(ClassNotFoundException e) {
			System.out.println("����̺� ����");
		}catch(SQLException e) {
			System.out.println("���ῡ ����");
		}
		return con;
	}
	
	//-----------------------------------������ ����-----------------------------------//
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
			setTitle("ȸ������");
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
		    
			
			
			
			//tlabel=new JLabel("ȸ������");
			//tlabel.setFont(new Font("HY������M",30,30));
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
			
			//Lid=new JLabel("���̵�");
			//Lid.setFont(new Font("���� ����� 250",25,25));
			//Lid.setForeground(Color.black);
			Tid=new JTextField(60);//���̵��Է�â
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
			
			//Lpass=new JLabel("��й�ȣ");
			//Lpass.setForeground(Color.black);
			//Lpass.setFont(new Font("���� ����� 250",25,25));
			//Lpass2=new JLabel("��й�ȣ Ȯ��");
			//Lpass2.setForeground(Color.black);
			//Lpass2.setFont(new Font("���� ����� 250",25,25));
			Tpass=new JPasswordField(60); //����Է�â
			Tpass2=new JPasswordField(60);
			
			Tpass.setFont(Main.digitalFont.deriveFont(20f));
			Tpass2.setFont(Main.digitalFont.deriveFont(20f));
			
			//Lnic=new JLabel("�г���");
			//Lnic.setForeground(Color.black);
			//Lnic.setFont(new Font("���� ����� 250",25,25));	
			Tnic=new JTextField(60); //�г����Է�â
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
//			Bnic.setFont(new Font("���� ����� 250",20,20));
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
		
			
   //-----------------------------------��ư�� �׼��߰��ϱ�-----------------------------------//
			
		Bid.addActionListener(new ActionListener() { //���̵��ߺ�üũ
				public void actionPerformed(ActionEvent event) {
					try {	
						String id=Tid.getText();
					boolean rss=false;
					ResultSet bs=stmt.executeQuery("select * from blomember where id='"+id+"'");						
							if(bs.next())
							JOptionPane.showMessageDialog(null,"�ߺ��� ���̵��Դϴ�.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
							else 
							JOptionPane.showMessageDialog(null,"��밡���� ���̵��Դϴ�.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
				}catch(SQLException e){
					System.out.println(e.getMessage());	
				}
				}
		});
		
		Bnic.addActionListener(new ActionListener() { //�г��� �ߺ�üũ
			public void actionPerformed(ActionEvent event) {
				try {	
					String nic=Tnic.getText();
				ResultSet bs=stmt.executeQuery("select * from blomember where nickname='"+nic+"'");						
						if(bs.next())
						JOptionPane.showMessageDialog(null,"�ߺ��� �г����Դϴ�.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
						else 
						JOptionPane.showMessageDialog(null,"��밡���� �г����Դϴ�.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
			}catch(SQLException e){
				System.out.println(e.getMessage());	
			}
			}
	});
			
			Bdb.addActionListener(new ActionListener() { //�̻������ DB�� �������߰��ϱ�
				public void actionPerformed(ActionEvent event) {				
    //------------------------��й�ȣ �°� �Է��ߴ��� ���� DB�� �������߰��ϱ�------------------------------//				
					
						
					String id=Tid.getText(); //�ؽ�Ʈ�ʵ岨�о���°�
					String pass=String.valueOf(Tpass.getPassword());
					String pass2=String.valueOf(Tpass2.getPassword());
					String nic=Tnic.getText();
					
					if(id.equals("")||pass.equals("")||pass2.equals("")||nic.equals("")) {
						JOptionPane.showMessageDialog(null,"��ĭ�� �ֽ��ϴ�.","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
					}
					else {
					if(pass.equals(pass2)) {
					try {					
						String s=String.format("insert into blomember(id, password, nickname) values('%s','%s','%s')",id,pass,nic);
						int check = stmt.executeUpdate(s); //���̺� ������ �߰�	
						System.out.println("�����ͺ��̽��� �߰� ����");
						if(check == 1) {
							JOptionPane.showMessageDialog(null,"ȸ�������� �Ǿ����ϴ�.","INFORMATION_MESSAGE",JOptionPane.INFORMATION_MESSAGE);
							
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
					JOptionPane.showMessageDialog(null,"��й�ȣ�� Ȯ�����ּ���","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
				}
				}		
				}
			});//Bdb.addActionListener�� ����
			
			
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
//		// -----------------------------------��񿬰��ϱ�-----------------------------------//
//		String url = "jdbc:mysql://"+Main.ipAddr+":3306/Gemblo";
//		String id = "root";
//		String password = "root";
//		Connection con = null;
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			System.out.println("����̹� ���� ����");
//			con = DriverManager.getConnection(url, id, password);
//			System.out.println("��񿬰Ἲ��");
//		} catch (ClassNotFoundException e) {
//			System.out.println("����̺� ����");
//		} catch (SQLException e) {
//			System.out.println("���ῡ ����");
//		}
//		return con;
//	}
//
//	// -----------------------------------������ ����-----------------------------------//
//	private JPanel dbpanel;
//	private JLabel tlabel, Lid, Lpass, Lpass2, Lnic;
//	private JButton Bid, Bnic, Bdb;
//	private JTextField Tid, Tnic;
//	private JPasswordField Tpass, Tpass2;
//	
//	// �ߺ��˻�
//	private boolean isId = false, isNickname = false;
//
//	public Member() throws SQLException {
//		Connection con = makeConnection();
//		Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery("select * from blomember");
//
//		setSize(750, 550);
//		setTitle("ȸ������");
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setResizable(false);
//
//		dbpanel = new JPanel();
//		dbpanel.setLayout(null);
//		dbpanel.setBackground(new Color(58, 61, 52));
//
//		tlabel = new JLabel("ȸ������");
//		tlabel.setFont(new Font("HY������M", 30, 30));
//		tlabel.setForeground(Color.white);
//		Bdb = new JButton("ȸ������");
//		Bdb.setFont(new Font("���� ����� 250", 20, 20));
//		Bdb.setForeground(Color.white);
//		Bdb.setBackground(new Color(120, 180, 5));
//
//		Lid = new JLabel("���̵�");
//		Lid.setFont(new Font("���� ����� 250", 30, 30));
//		Lid.setForeground(Color.white);
//		Tid = new JTextField(60);// ���̵��Է�â
//		Tid.setHorizontalAlignment(JTextField.CENTER);
//		Bid = new JButton("�ߺ�Ȯ��");
//		Bid.setFont(new Font("���� ����� 250", 20, 20));
//		Bid.setForeground(Color.white);
//		Bid.setBackground(new Color(120, 180, 5));
//
//		Lpass = new JLabel("��й�ȣ");
//		Lpass.setForeground(Color.white);
//		Lpass.setFont(new Font("���� ����� 250", 30, 30));
//		Lpass2 = new JLabel("��й�ȣ Ȯ��");
//		Lpass2.setForeground(Color.white);
//		Lpass2.setFont(new Font("���� ����� 250", 30, 30));
//		Tpass = new JPasswordField(60); // ����Է�â
//		Tpass2 = new JPasswordField(60);
//
//		Lnic = new JLabel("�г���");
//		Lnic.setForeground(Color.white);
//		Lnic.setFont(new Font("���� ����� 250", 30, 30));
//		Tnic = new JTextField(60); // �г����Է�â
//		Tnic.setHorizontalAlignment(JTextField.CENTER);
//		Bnic = new JButton("�ߺ�Ȯ��");
//		Bnic.setFont(new Font("���� ����� 250", 20, 20));
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
//		// -----------------------------------��ư�� �׼��߰��ϱ�-----------------------------------//
//
//		Bid.addActionListener(new ActionListener() { // ���̵��ߺ�üũ
//			public void actionPerformed(ActionEvent event) {
//				try {
//					String id = Tid.getText();
//					ResultSet bs = stmt.executeQuery("select * from blomember where id='" + id + "'");
//					if (bs.next())
//						JOptionPane.showMessageDialog(null, "�ߺ��� ���̵��Դϴ�.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					else
//						JOptionPane.showMessageDialog(null, "��밡���� ���̵��Դϴ�.", "INFORMATION_MESSAGE",
//								JOptionPane.INFORMATION_MESSAGE);
//				} catch (SQLException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//		});
//
//		Bnic.addActionListener(new ActionListener() { // �г��� �ߺ�üũ
//			public void actionPerformed(ActionEvent event) {
//				try {
//					String nic = Tnic.getText();
//					ResultSet bs = stmt.executeQuery("select * from blomember where nickname='" + nic + "'");
//					if (bs.next())
//						JOptionPane.showMessageDialog(null, "�ߺ��� �г����Դϴ�.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					else
//						JOptionPane.showMessageDialog(null, "��밡���� �г����Դϴ�.", "INFORMATION_MESSAGE",
//								JOptionPane.INFORMATION_MESSAGE);
//				} catch (SQLException e) {
//					System.out.println(e.getMessage());
//				}
//			}
//		});
//
//		Bdb.addActionListener(new ActionListener() { // �̻������ DB�� �������߰��ϱ�
//			public void actionPerformed(ActionEvent event) {
//				// ------------------------��й�ȣ �°� �Է��ߴ��� ���� DB�� �������߰��ϱ�------------------------------//
//
//				String id = Tid.getText(); // �ؽ�Ʈ�ʵ岨�о���°�
//				String pass = String.valueOf(Tpass.getPassword());
//				String pass2 = String.valueOf(Tpass2.getPassword());
//				String nic = Tnic.getText();
//
//				if (id.equals("") || pass.equals("") || pass2.equals("") || nic.equals("")) {
//					JOptionPane.showMessageDialog(null, "��ĭ�� �ֽ��ϴ�.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//				} 				
//				else {
//					if (pass.equals(pass2)) {
//						try {
//							String s = String.format("insert into blomember(id, password, nickname) values('%s','%s','%s')", id, pass, nic);
//							stmt.executeUpdate(s); // ���̺� ������ �߰�
//							System.out.println("�����ͺ��̽��� �߰� ����");
//							JOptionPane.showMessageDialog(null, "ȸ�������� �Ǿ����ϴ�.", "INFORMATION_MESSAGE",
//									JOptionPane.INFORMATION_MESSAGE);
//							con.close();
//							dispose();
//						} catch (SQLException e) {
//							System.out.println(e.getMessage());
//						}
//					} else {
//						JOptionPane.showMessageDialog(null, "��й�ȣ�� Ȯ�����ּ���", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
//					}
//				}
//			}
//		});// Bdb.addActionListener�� ����
//
//		add(dbpanel);
//		setVisible(true);
//	}
//}

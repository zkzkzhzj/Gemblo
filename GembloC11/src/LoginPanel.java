import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;;

public class LoginPanel extends JPanel implements ActionListener{
	private JTextField idTF;
	private JPasswordField paTF;
	private JButton loginBtn;
	
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	private String id = null;
	private String pa = null;
	private String nickname = null;
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void addActionL(ActionListener a) {
		idTF.addActionListener(a);
		paTF.addActionListener(a);
		loginBtn.addActionListener(a);
	}
	
	public LoginPanel() {
		setSize(1000,750);
		
		idTF = new JTextField(12);
		//idTF.addActionListener(this);
		paTF = new JPasswordField(12);
		paTF.setFont(Main.digitalFont.deriveFont(35f));
		//paTF.addActionListener(this);
		loginBtn = new JButton("로그인");
		//loginBtn.addActionListener(this);
		
		add(idTF); add(paTF); add(loginBtn);
	}

	@Override
	public void actionPerformed(ActionEvent e) {	
		id = idTF.getText();
		pa = String.valueOf(paTF.getPassword());
		login(id, pa);
	}
	
	public String getID() {
		return id;
	}
	

	public boolean login() {
		id = idTF.getText();
		pa = String.valueOf(paTF.getPassword());
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+Main.ipAddr+":3306/gemblo", "root", "root");
			
			
			pstmt = conn.prepareStatement("select * from blomember where id=?");
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				nickname = rs.getString("nickname");
				if(pa.equals(rs.getString("password"))) {
					JOptionPane.showMessageDialog(null, "로그인 성공!");
					Main.id = id;
					Main.nickname = nickname;
					return true;
					//this.removeAll();
					//this.setLayout(new BorderLayout());
					//add(new RoomPanel(Main.ipAddr, id));					
					//repaint();
				} else {
					JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
					return false;
				}
			} else {
				JOptionPane.showMessageDialog(null, "아이디가 틀렸습니다.");
				return false;
			}
			
						
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "로그인 오류 : "+e);
		}
		return false;
	}

	
	public static boolean login(String id, String pa) {		
		try {
			Connection conn = Member.makeConnection();			
			
			PreparedStatement pstmt = conn.prepareStatement("select * from blomember where id=?");
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String nickname = rs.getString("nickname");
				if(pa.equals(rs.getString("password"))) {
					JOptionPane.showMessageDialog(null, "로그인 성공!");
					Main.id = id;
					Main.nickname = nickname;
					return true;
					//this.removeAll();
					//this.setLayout(new BorderLayout());
					//add(new RoomPanel(Main.ipAddr, id));					
					//repaint();
				} else {
					JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
					return false;
				}
			} else {
				JOptionPane.showMessageDialog(null, "아이디가 틀렸습니다.");
				return false;
			}
			
						
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "로그인 오류 : "+e);
		}
		return false;
	}

}

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
/*
 *  �̰����� �����ǿ� ���� Ÿ��(������)�� ������̴�.  
 *  isGem ������ �ִ��� Ȯ��
 *  icon ������ ������ �̰��� �׸���
 *  radius �������� ������
 *  hexagon �̰����ٰ� �������� ����� �����Ұ��̴�.
 *  x, y (Point p) �� ������ ��ġ�� �����Ұ��̴�.
 *  c Ÿ���� ���� ������ ��.
 *  
 *  �����ڴ� x, y���� �޴°� �ϳ� Point�� �޴°��� �ϳ� �ΰ� �������.
 */
import javax.swing.JOptionPane;

public class Tile extends JButton {
	private boolean isGem = false;
	private ImageIcon icon;
	private int radius;
	private Polygon hexagon = new Polygon();
	private int x, y;
	private Point p;
	private Color c;
	private String gemColor;
	private boolean isLast = true;
	private Image isLastGemImg = new ImageIcon(Main.class.getResource("img/isLastGem.gif")).getImage();
	private boolean isMine = false;

	public boolean isMine() {
		return isMine;
	}

	public void setMine(boolean isMine) {
		this.isMine = isMine;
	}

	public String getGemColor() {
		return gemColor;
	}

	public void setGemColor(String gemColor) {
		this.gemColor = gemColor;
	}

	private void init() { // �����ڿ� �� �⺻ ������ (initialize)		
		setBorderPainted(false); // ��ư�� �׵θ� ����
		setContentAreaFilled(false); // ��ư�� ����� ���ֱ�
		setOpaque(false);
		
		// ���� �׸���!
		// https://stackoverflow.com/questions/35853902/drawing-hexagon-using-java-error
		for (int i = 0; i < 6; i++) {
			int hexaX = (int) (radius + radius * Math.cos(i * 2 * Math.PI / 6D));
			int hexaY = (int) (radius + radius * Math.sin(i * 2 * Math.PI / 6D));
			hexagon.addPoint(hexaX, hexaY);
		}
	}
	
	// �����ڵ�
	public Tile(int x, int y, int radius, ImageIcon icon) {
		setBounds(x, y, radius * 2, radius *2);
		this.icon = icon;
		this.radius = radius;
		this.x = x; this.y = y;
		p = new Point(x,y);
		init();
	}	
	public Tile(Point p, int radius, ImageIcon icon) {
		setBounds(p.x, p.y, radius * 2, radius *2);
		this.icon = icon;
		this.radius = radius;
		this.p = p;
		x = p.x; y = p.y;
		init();
	}
	
	//�����ڿ� ������
	public void setColor(Color c) {this.c = c;}	
	public Color getColor() {return c;}
	public void setIcon(ImageIcon icon) {this.icon = icon;}
	public ImageIcon getIcon() {return icon;}
	public Point getPoint() {return p;}
	public void setIsGem(boolean isGem) {this.isGem = isGem;}
	public boolean getIsGem() {return isGem;}
	public void setIsLast(boolean isLast) {this.isLast = isLast;}
	
	public boolean mineCheck(Component comp) { // ���� ���� ������ true ��ȯ. ���� false
		if(!isMine)
			return true;
		
		else if(question(comp)) {
			isMine = false;
			return true;
		}
		
		return false;
	}
	
	public boolean question(Component comp) {
		//TODO mineCheck() �� �����ϱ�, DB �����ؼ� ���� �޾ƿͼ� Ǯ�� ��
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Integer> num = new ArrayList<>();
		int cnt = 0;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+Main.ipAddr+":3306/Gemblo", "root", "root");
			
			/*// quiz ���̺� �����Ͱ� �����Ǹ� num ���ڰ� �߰��߰� ��� ������ �����.
			pstmt = conn.prepareStatement("select count(*) from quiz");
			rs = pstmt.executeQuery();
			
			if(rs.next())
				cnt = rs.getInt(1);
			else
				System.out.println("quiz ���̺� ������ ����");
			
			int random = (int) (Math.random() * cnt) + 1;
			*/
			
			// ������ ������ ���� ����ؼ� �ٽ� �ۼ��� ��.
			pstmt = conn.prepareStatement("select num from quiz");
			rs = pstmt.executeQuery();			
			
			while(rs.next()) {
				num.add(rs.getInt(1));
			}
			
			int random = (int) (Math.random() * num.size());
			
			pstmt = conn.prepareStatement("SELECT * FROM quiz WHERE num = ?");
			pstmt.setInt(1, num.get(random));
						
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String question, answer, answer2, answer3;
				boolean correct = false;
				
				question = rs.getString("title");
				answer = rs.getString("body");
				answer2 = rs.getString("answer2");
				answer3 = rs.getString("answer3");
				
				String inputAnswer = JOptionPane.showInputDialog(comp ,"���� : " + question +"\n(�������� ���ڸ� �����ּ���.)");
				
				if(answer2 != null && answer2.equals(inputAnswer))
					correct = true;
				if(answer3 != null && answer3.equals(inputAnswer))
					correct = true;
				if(answer.equals(inputAnswer))
					correct = true;
				
				
				if(correct) {
					new MusicList().quizCorrect();
					JOptionPane.showMessageDialog(comp, "����!");					
					return true;
				} else {
					new MusicList().quizIncorrect();
					JOptionPane.showMessageDialog(comp, "Ʋ�Ƚ��ϴ�.");					
					return false;
				}
			}
			
			
		} catch (Exception e) {
			System.out.println(e + " : question()");
		}
		
		return false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// ���������Ѻκ��� �ִ��� �ٿ��ش�.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(c);
		
		if (isGem) { // ������ ������ ������ �׸���.
			g2.drawImage(icon.getImage(), 0, 0, null); // 0,0 ��ġ���ٰ� icon�� �׸���.
			if (isLast)
				g2.drawImage(isLastGemImg, 0, 0, null);
		}
		
		/*
		// ���콺�� ���� �÷����ٸ�
		if (this.getModel().isRollover())
			g2.setColor(Color.BLACK);
		else
			g2.setColor(c);
*/

		g2.draw(hexagon);
		g2.dispose();
		//super.paintComponent(g);
	}
}
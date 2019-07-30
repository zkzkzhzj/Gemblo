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
 *  이곳에는 보드판에 들어가는 타일(육각형)을 만들것이다.  
 *  isGem 보석이 있는지 확인
 *  icon 보석이 있으면 이것을 그릴것
 *  radius 육각형의 반지름
 *  hexagon 이곳에다가 육각형을 만들어 저장할것이다.
 *  x, y (Point p) 이 값으로 위치를 지정할것이다.
 *  c 타일의 색을 지정할 것.
 *  
 *  생성자는 x, y값을 받는것 하나 Point를 받는것을 하나 두개 만들었다.
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

	private void init() { // 생성자에 들어갈 기본 설정들 (initialize)		
		setBorderPainted(false); // 버튼의 테두리 제거
		setContentAreaFilled(false); // 버튼의 배경을 없애기
		setOpaque(false);
		
		// 헥사곤 그리기!
		// https://stackoverflow.com/questions/35853902/drawing-hexagon-using-java-error
		for (int i = 0; i < 6; i++) {
			int hexaX = (int) (radius + radius * Math.cos(i * 2 * Math.PI / 6D));
			int hexaY = (int) (radius + radius * Math.sin(i * 2 * Math.PI / 6D));
			hexagon.addPoint(hexaX, hexaY);
		}
	}
	
	// 생성자들
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
	
	//설정자와 접근자
	public void setColor(Color c) {this.c = c;}	
	public Color getColor() {return c;}
	public void setIcon(ImageIcon icon) {this.icon = icon;}
	public ImageIcon getIcon() {return icon;}
	public Point getPoint() {return p;}
	public void setIsGem(boolean isGem) {this.isGem = isGem;}
	public boolean getIsGem() {return isGem;}
	public void setIsLast(boolean isLast) {this.isLast = isLast;}
	
	public boolean mineCheck(Component comp) { // 지뢰 제거 성공시 true 반환. 실패 false
		if(!isMine)
			return true;
		
		else if(question(comp)) {
			isMine = false;
			return true;
		}
		
		return false;
	}
	
	public boolean question(Component comp) {
		//TODO mineCheck() 에 연결하기, DB 연결해서 문제 받아와서 풀기 등
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Integer> num = new ArrayList<>();
		int cnt = 0;
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://"+Main.ipAddr+":3306/Gemblo", "root", "root");
			
			/*// quiz 테이블에 데이터가 삭제되면 num 숫자가 중간중간 비어 문제가 생긴다.
			pstmt = conn.prepareStatement("select count(*) from quiz");
			rs = pstmt.executeQuery();
			
			if(rs.next())
				cnt = rs.getInt(1);
			else
				System.out.println("quiz 테이블에 문제가 없음");
			
			int random = (int) (Math.random() * cnt) + 1;
			*/
			
			// 데이터 삭제된 것을 고려해서 다시 작성한 것.
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
				
				String inputAnswer = JOptionPane.showInputDialog(comp ,"문제 : " + question +"\n(객관식은 숫자만 적어주세요.)");
				
				if(answer2 != null && answer2.equals(inputAnswer))
					correct = true;
				if(answer3 != null && answer3.equals(inputAnswer))
					correct = true;
				if(answer.equals(inputAnswer))
					correct = true;
				
				
				if(correct) {
					new MusicList().quizCorrect();
					JOptionPane.showMessageDialog(comp, "정답!");					
					return true;
				} else {
					new MusicList().quizIncorrect();
					JOptionPane.showMessageDialog(comp, "틀렸습니다.");					
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
		// 울툴불퉁한부분을 최대한 줄여준다.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(c);
		
		if (isGem) { // 보석이 있으면 보석을 그린다.
			g2.drawImage(icon.getImage(), 0, 0, null); // 0,0 위치에다가 icon을 그린다.
			if (isLast)
				g2.drawImage(isLastGemImg, 0, 0, null);
		}
		
		/*
		// 마우스가 위에 올려진다면
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
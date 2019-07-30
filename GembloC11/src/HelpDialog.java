import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class HelpDialog extends JDialog{
	
	public HelpDialog(Frame comp, boolean modal) {
		super(comp ,modal);
		
		setSize(710,940);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("µµ¿ò¸»");
		setResizable(false);
		setLocationRelativeTo(null);
		
		JScrollPane scrol=new JScrollPane();
		ImageIcon icon=new ImageIcon(Main.class.getResource("img/gemblohelp.png"));
		
		JPanel panel=new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon.getImage(),0,0,null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		
		panel.setBounds(100, 0, 700, 500);
		panel.setLayout(null);
		panel.setBackground(Color.white);
		
		
		
		
		this.add(panel);
		setVisible(true);
	}
}

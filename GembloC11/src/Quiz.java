import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class Quiz extends JDialog implements ActionListener{
	private Connection con = null;
	private ResultSet rs = null;
	private PreparedStatement pstmt = null;
	private JTable table;
	private JButton myQuiz, allQuiz, delete, addBtn, adminQuiz;
	private boolean isAll = true, isAdmin = false;

	
	private String columnNames[] = {"제출자","문제","정답"};
	private String data[][];
	
	private DefaultTableModel model;
	private DefaultTableCellRenderer dtcr;
	
	public Quiz(Frame parent, boolean modal) throws SQLException {
		super(parent, modal);
		setTitle("퀴즈 보기");
		setSize(700, 300);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		//setResizable(false);
		
		for(int i=0; i<Main.adminList.size(); i++)
			if(Main.adminList.get(i).equals(Main.nickname))
				isAdmin = true;
		
		allQuiz = new JButton("전체 퀴즈 보기");
		myQuiz = new JButton("나의 퀴즈 보기");
		adminQuiz = new JButton("미등록 목록");
		delete = new JButton("퀴즈 삭제");
		addBtn = new JButton("등록 허가");		
		
		allQuiz.addActionListener(this);
		myQuiz.addActionListener(this);
		adminQuiz.addActionListener(this);
		delete.addActionListener(this);
		addBtn.addActionListener(this);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridLayout(0, 3));
		
		btnPanel.add(allQuiz);
		btnPanel.add(myQuiz);
		if(isAdmin) {
			btnPanel.add(adminQuiz);
			btnPanel.add(addBtn);
		}
		btnPanel.add(delete);
	
		
		add(btnPanel, BorderLayout.SOUTH);
		
		table = new JTable();
		setAllQuiz();
//		setMyQuiz();
		
		add(new JScrollPane(table));
		setVisible(true);
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		new MusicList().buttonClick();
		
		switch(action) {
		case "전체 퀴즈 보기":
			try {
				setAllQuiz();
				isAll=true;
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "전체 퀴즈 보기 중 오류 발생 : " + e1);
			}
			break;			
		case "나의 퀴즈 보기":
			try {
				setMyQuiz();
				isAll=false;
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "나의 퀴즈 보기 중 오류 발생 : " + e1);
			}
			break;
		case "퀴즈 삭제":
			try {
				delete();
				if(isAll)
					setAllQuiz();
				else
					setMyQuiz();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "퀴즈 삭제 중 오류 발생 : " + e1);
			}			
			break;
		case "등록 허가":
			try {
				addQuiz();
				setAdminQuiz();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "퀴즈 등록 허가 중 오류 발생 : " + e1);
			}	
			break;
		case "미등록 목록":
			try {
				setAdminQuiz();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, "미등록 목록 중 오류 발생 : " + e1);
			}
		}
		revalidate();
		repaint();
	}
	
	public void setAllQuiz() throws SQLException{
		con = Member.makeConnection();
		
		columnNames[0] = "제출자";
		columnNames[1] = "문제";
		columnNames[2] = "정답";
		
		// 행수 파악
		pstmt = con.prepareStatement("select count(*) from quiz");
		rs = pstmt.executeQuery();
	
		int rsCnt;
		
		if(rs.next()) {
			rsCnt = rs.getInt(1);
			data = new String[rsCnt][columnNames.length];
		}
		
		// 데이터 삽입
		pstmt = con.prepareStatement("select * from quiz");
		rs = pstmt.executeQuery();
		
		int cnt=0;
		String nickname = null;
		String titleStr = null;
		String bodyStr = null;
		
		while(rs.next()) {
			nickname = rs.getString("nickname");			
			titleStr = rs.getString("title");
			bodyStr = rs.getString("body");
			
			if(nickname == null)
				nickname = "관리자";
			
			data[cnt][0] = nickname;
			data[cnt][1] = titleStr;
			data[cnt][2] = bodyStr;		
						
			cnt++;
		}
		model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) { // 셀 수정 금지
				return false;
			}
		};
		
		table.setModel(model);
		
		dtcr = new DefaultTableCellRenderer(); // 컬럼들 정렬를 위해
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = table.getColumnModel();
		
		// 가운데 정렬
		for(int i=0; i<tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
		
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false); // 헤드 이동(드래그) 불가
		table.setDragEnabled(false); // 선택 드래그 불가.
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 싱글 선택 모드			
		// https://blog.naver.com/petitpomme/30047661885
		//tcm.getColumn(1).setMaxWidth(180);
		tcm.getColumn(0).setPreferredWidth(150);
		tcm.getColumn(0).setMaxWidth(200);
		tcm.getColumn(2).setPreferredWidth(100);
		tcm.getColumn(2).setMaxWidth(150);
		table.setRowHeight(20);
		table.setColumnModel(tcm);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 자동 리사이즈.
		
		TableRowSorter tableSorter = new TableRowSorter(table.getModel()); // 정렬 (순위) 표기를 위해 만듬
		table.setRowSorter(tableSorter);		
		
		tableSorter.toggleSortOrder(0); // 0번째 컬럼 기준으로 정렬	
	}

	public void setMyQuiz() throws SQLException {
		con = Member.makeConnection();
		
		columnNames[0] = "관리자 승인";
		columnNames[1] = "문제";
		columnNames[2] = "정답";
		int rsCnt = 0;
		
		// 행수 파악
		pstmt = con.prepareStatement("select count(*) from quiz where nickname=?");
		pstmt.setString(1, Main.nickname);
		
		rs = pstmt.executeQuery();
		if(rs.next())
			rsCnt = rs.getInt(1);
	
		pstmt = con.prepareStatement("select count(*) from tmpQuiz where nickname=?");
		pstmt.setString(1, Main.nickname);
		
		rs = pstmt.executeQuery();
		
		if(rs.next()) {
			rsCnt += rs.getInt(1);
			data = new String[rsCnt][columnNames.length];
		}
		
		// 데이터 삽입
		pstmt = con.prepareStatement("select * from quiz where nickname=?");
		pstmt.setString(1, Main.nickname);
		rs = pstmt.executeQuery();
		
		int cnt=0;
		String isOk = "등록 완료";
		String titleStr = null;
		String bodyStr = null;
		
		while(rs.next()) {					
			titleStr = rs.getString("title");
			bodyStr = rs.getString("body");
			
			data[cnt][0] = isOk;
			data[cnt][1] = titleStr;
			data[cnt][2] = bodyStr;		
						
			cnt++;
		}
		
		pstmt = con.prepareStatement("select * from tmpQuiz where nickname=?");
		pstmt.setString(1, Main.nickname);
		rs = pstmt.executeQuery();		
		
		isOk = "미등록";
		titleStr = null;
		bodyStr = null;
		
		while(rs.next()) {					
			titleStr = rs.getString("title");
			bodyStr = rs.getString("answer1");
			
			data[cnt][0] = isOk;
			data[cnt][1] = titleStr;
			data[cnt][2] = bodyStr;		
						
			cnt++;
		}
		
		model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) { // 셀 수정 금지
				return false;
			}
		};
		
		table.setModel(model);
		
		dtcr = new DefaultTableCellRenderer(); // 컬럼들 정렬를 위해
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = table.getColumnModel();
		
		// 가운데 정렬
		for(int i=0; i<tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
		
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false); // 헤드 이동(드래그) 불가
		table.setDragEnabled(false); // 선택 드래그 불가.
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 싱글 선택 모드			
		// https://blog.naver.com/petitpomme/30047661885
		//tcm.getColumn(1).setMaxWidth(180);
		tcm.getColumn(0).setPreferredWidth(150);
		tcm.getColumn(0).setMaxWidth(200);
		tcm.getColumn(2).setPreferredWidth(100);
		tcm.getColumn(2).setMaxWidth(150);
		table.setRowHeight(20);
		table.setColumnModel(tcm);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 자동 리사이즈.
		
		TableRowSorter tableSorter = new TableRowSorter(table.getModel()); // 정렬 (순위) 표기를 위해 만듬
		table.setRowSorter(tableSorter);		
		
		tableSorter.toggleSortOrder(0); // 0번째 컬럼 기준으로 정렬	
	}
	
	public void setAdminQuiz() throws SQLException {
		con = Member.makeConnection();
		
		columnNames[0] = "관리자 승인";
		columnNames[1] = "문제";
		columnNames[2] = "정답";
		int rsCnt = 0;
		
		// 행수 파악	
		pstmt = con.prepareStatement("select count(*) from tmpQuiz");		
		rs = pstmt.executeQuery();
		
		if(rs.next()) {
			rsCnt = rs.getInt(1);
			data = new String[rsCnt][columnNames.length];
		}
		
		// 데이터 삽입		
		int cnt=0;
		String isOk = "미등록";
		String titleStr = null;
		String bodyStr = null;		
		
		pstmt = con.prepareStatement("select * from tmpQuiz");
		rs = pstmt.executeQuery();	
		
		while(rs.next()) {					
			titleStr = rs.getString("title");
			bodyStr = rs.getString("answer1");
			
			data[cnt][0] = isOk;
			data[cnt][1] = titleStr;
			data[cnt][2] = bodyStr;		
						
			cnt++;
		}
		
		model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) { // 셀 수정 금지
				return false;
			}
		};
		
		table.setModel(model);
		
		dtcr = new DefaultTableCellRenderer(); // 컬럼들 정렬를 위해
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = table.getColumnModel();
		
		// 가운데 정렬
		for(int i=0; i<tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
		
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false); // 헤드 이동(드래그) 불가
		table.setDragEnabled(false); // 선택 드래그 불가.
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 싱글 선택 모드			
		// https://blog.naver.com/petitpomme/30047661885
		//tcm.getColumn(1).setMaxWidth(180);
		tcm.getColumn(0).setPreferredWidth(150);
		tcm.getColumn(0).setMaxWidth(200);
		tcm.getColumn(2).setPreferredWidth(100);
		tcm.getColumn(2).setMaxWidth(150);
		table.setRowHeight(20);
		table.setColumnModel(tcm);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 자동 리사이즈.
		
		TableRowSorter tableSorter = new TableRowSorter(table.getModel()); // 정렬 (순위) 표기를 위해 만듬
		table.setRowSorter(tableSorter);		
		
		tableSorter.toggleSortOrder(0); // 0번째 컬럼 기준으로 정렬	
	}
	
	public void delete() throws SQLException {
		int row = table.getSelectedRow();
		
		if(row == -1) {
			JOptionPane.showMessageDialog(null, "삭제할 문제를 선택하세요.", "문제를 선택하세요", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		String nickname = (String) table.getValueAt(row, 0);
		String title = (String) table.getValueAt(row, 1);
		String answer = (String) table.getValueAt(row, 2);
		
		if(nickname.equals("등록 완료") || nickname.equals(Main.nickname) || nickname.equals("미등록") || isAdmin) {
			con = Member.makeConnection();
			if(nickname.equals("미등록"))
				pstmt = con.prepareStatement("delete from tmpQuiz where title=? AND answer1=?");
			else
				pstmt = con.prepareStatement("delete from quiz where title=? AND body=?");
			
			pstmt.setString(1, title);
			pstmt.setString(2, answer);
			
			int result = pstmt.executeUpdate();
			
			if(result >= 1)
				JOptionPane.showMessageDialog(null, "삭제 성공!");
			else
				JOptionPane.showMessageDialog(null, "삭제 실패.", "삭제 실패", JOptionPane.ERROR_MESSAGE);
			
			
		} else {
			JOptionPane.showMessageDialog(null, "자신의 문제만 삭제 가능합니다.", "자신의 문제를 선택하세요", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void addQuiz() throws SQLException {
		int row = table.getSelectedRow();
		
		if(row == -1) {
			JOptionPane.showMessageDialog(null, "등록할 문제를 선택하세요.", "문제를 선택하세요", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		String nickname = (String) table.getValueAt(row, 0);
		String title = (String) table.getValueAt(row, 1);
		String answer1 = (String) table.getValueAt(row, 2);
		String id, body;
		
		String answer[] = new String[3];
		
		if(nickname.equals("미등록") && isAdmin) {
			con = Member.makeConnection();
			pstmt = con.prepareStatement("select * from tmpQuiz where title=? AND answer1=?");
			pstmt.setString(1, title);
			pstmt.setString(2, answer1);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				title = rs.getString("title");
				answer[0] = rs.getString("answer1");
				id = rs.getString("id");
				nickname = rs.getString("nickname");
				answer[1] = rs.getString("answer2");
				answer[2] = rs.getString("answer3");
			} else {
				JOptionPane.showMessageDialog(null, "등록 실패.", "등록 실패", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (insertQuiz(title, answer, id, nickname)) {
				JOptionPane.showMessageDialog(null, "등록 성공!");
			} else {
				JOptionPane.showMessageDialog(null, "등록 실패.", "등록 실패", JOptionPane.ERROR_MESSAGE);
				return;
			}	

			
			String delTitle = (String) table.getValueAt(row, 1);
			String delAnswer = (String) table.getValueAt(row, 2);
			
			con = Member.makeConnection();
			pstmt = con.prepareStatement("delete from tmpQuiz where title=? AND answer1=?");

			pstmt.setString(1, delTitle);
			pstmt.setString(2, delAnswer);

			int result = pstmt.executeUpdate();
			
			if(result == 0)
				JOptionPane.showMessageDialog(null, "등록 성공 후 삭제 실패.", "등록 실패", JOptionPane.ERROR_MESSAGE);
			
		} else {
			JOptionPane.showMessageDialog(null, "미등록 문제만 등록 가능합니다.", "미등록 문제를 선택하세요", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public static boolean insertQuiz(String title, String[] answer, String id, String nickname) throws SQLException {
		Connection con = Member.makeConnection();
		PreparedStatement pstmt = null;		
		
		if(answer.length == 1)
			pstmt = con.prepareStatement("insert into quiz(title, body, id, nickname) values(?,?,?,?)");
		else if(answer.length == 2)
			pstmt = con.prepareStatement("insert into quiz(title, body, id, nickname, answer2) values(?,?,?,?,?)");
		else if(answer.length == 3)
			pstmt = con.prepareStatement("insert into quiz(title, body, id, nickname, answer2, answer3) values(?,?,?,?,?,?)");
		
		pstmt.setString(1, title);
		pstmt.setString(2, answer[0]);
		pstmt.setString(3, id);
		pstmt.setString(4, nickname);
		
		if(answer.length >= 2)
			pstmt.setString(5, answer[1]);
		if(answer.length >= 3)
			pstmt.setString(6, answer[2]);
		
		int result = pstmt.executeUpdate();
		
		if(result >= 1)
			return true;
		else
			return false;
	}
	
	public static boolean insertTmpQuiz(String title, String[] answer, String id, String nickname) throws SQLException {
		Connection con = Member.makeConnection();
		PreparedStatement pstmt = null;		
		
		if(answer.length == 1)
			pstmt = con.prepareStatement("insert into tmpQuiz(title, answer1, id, nickname) values(?,?,?,?)");
		else if(answer.length == 2)
			pstmt = con.prepareStatement("insert into tmpQuiz(title, answer1, id, nickname, answer2) values(?,?,?,?,?)");
		else if(answer.length == 3)
			pstmt = con.prepareStatement("insert into tmpQuiz(title, answer1, id, nickname, answer2, answer3) values(?,?,?,?,?,?)");
		
		pstmt.setString(1, title);
		pstmt.setString(2, answer[0]);
		pstmt.setString(3, id);
		pstmt.setString(4, nickname);
		
		if(answer.length >= 2)
			pstmt.setString(5, answer[1]);
		if(answer.length >= 3)
			pstmt.setString(6, answer[2]);
		
		int result = pstmt.executeUpdate();
		
		if(result >= 1)
			return true;
		else
			return false;
	}
	
	static class addTmpQuiz extends JDialog implements ActionListener{
		private CardLayout card = new CardLayout();
		private JButton next = new JButton("다음");
		private JButton cancel = new JButton("취소");
		private JPanel btnPanel = new JPanel();
		private JPanel answerPanel = new JPanel();
		private JTextArea titleArea = new JTextArea();
		private JSpinner spin = new JSpinner();
		
		public addTmpQuiz(Frame comp, boolean modal) {
			super(comp, modal);			
//			setLayout(card);
			setSize(500,500);
			setLocationRelativeTo(null);
			setTitle("퀴즈 제출");
			
			JLabel label = new JLabel("문제를 적어주세요. 정답은 최대 3개까지 가능합니다.");
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBackground(Color.WHITE);
			label.setForeground(Color.BLUE.darker());
			add(label, BorderLayout.NORTH);
			
			add(titleArea);
			
			cancel.addActionListener(this);
			next.addActionListener(this);
			
//			btnPanel.setLayout(new GridLayout(0, 2));
			btnPanel.add(new JLabel("정답의 개수를 선택해주세요."));
			spin.setModel(new SpinnerNumberModel(1, 1, 3, 1));
			((DefaultEditor) spin.getEditor()).getTextField().setEditable(false);
			btnPanel.add(spin);
			btnPanel.add(cancel);
			btnPanel.add(next);
			
			add(btnPanel, BorderLayout.SOUTH);
			
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String action = e.getActionCommand();			
			new MusicList().buttonClick();
			
			String title = titleArea.getText();
//			String answer1, answer2, answer3;
			int spinValue = (Integer) spin.getValue();
			String answer[] = new String[spinValue];
			

			
			switch(action) {
			case "다음":
				if(title.equals("")) {
					JOptionPane.showMessageDialog(null, "문제를 입력하세요.", "문제를 입력하세요", JOptionPane.ERROR_MESSAGE);
					return;
				}
				answer[0] = JOptionPane.showInputDialog("첫 번째 정답을 입력해주세요.");
				
				if(answer[0].equals("")) {
					JOptionPane.showMessageDialog(null, "정답을 입력하세요.", "정답을 입력하세요", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(spinValue >= 2)
					answer[1] = JOptionPane.showInputDialog(null,"두 번째 정답을 입력해주세요.");
				if(spinValue >= 3)	
					answer[2] = JOptionPane.showInputDialog(null,"세 번째 정답을 입력해주세요.");
				
				try {
					if(insertTmpQuiz(title, answer, Main.id, Main.nickname)) {
						JOptionPane.showMessageDialog(null, "퀴즈 제출 성공!\n관리자의 승인을 거쳐 퀴즈가 등록됩니다.");
					} else {
						JOptionPane.showMessageDialog(null, "퀴즈 제출 실패");
					}
				} catch (SQLException e1) {					
					JOptionPane.showMessageDialog(null, "퀴즈 제출 오류발생 : " + e1);
				}
				
				dispose();
				break;
				
			case "취소":
				dispose();
				break;
			}
			
		}
	}	
}

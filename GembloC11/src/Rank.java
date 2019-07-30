import java.awt.Frame;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class Rank extends JDialog{
	private Connection con = null;
	private ResultSet rs = null;
	private PreparedStatement pstmt = null;
	private JTable table;
	
	String columnNames[] = {"���","�г���", "�¸� ��", "����"};
	String data[][];
	
	private DefaultTableModel model;
	private DefaultTableCellRenderer dtcr;
	
	public Rank(Frame parent, boolean modal, String id) throws SQLException {
		super(parent, modal);
		setTitle("��ŷ ����");
		setSize(300, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		con = Member.makeConnection();
		
		// ��� �ľ�
		pstmt = con.prepareStatement("select count(*) from blomember");
		rs = pstmt.executeQuery();
	
		int rsCnt;
		
		if(rs.next()) {
			rsCnt = rs.getInt(1);
			data = new String[rsCnt][columnNames.length];
		}
		
		pstmt = con.prepareStatement("");
		
		// ������ ���� TODO �¸��� �����ϸ� ������ ���ĵǰ� �ϱ�.
//		pstmt = con.prepareStatement("SELECT @RN:=@RN+1 AS ROWNUM, TB.*\r\n" + 
//				"FROM(\r\n" + 
//				"    SELECT * \r\n" + 
//				"    FROM blomember\r\n" + 
//				"    ORDER BY CAST(winCnt as unsigned) DESC\r\n" + 
//				") AS TB, \r\n" + 
//				"(SELECT @RN:=0) AS R;");
		pstmt = con.prepareStatement("SELECT @RN:=@RN+1 AS ROWNUM, TB.* FROM( SELECT *  FROM blomember ORDER BY CAST(winCnt as unsigned) DESC, CAST(winPoint as unsigned) DESC) AS TB, (SELECT @RN:=0) AS R");
		
		rs = pstmt.executeQuery();
		
		int cnt=0;
		int myId=0;
		
		while(rs.next()) {
			String nickname = rs.getString("nickname");
			String winCnt = rs.getString("winCnt");
			String winPoint = rs.getString("winPoint");
			String rownum = rs.getString("ROWNUM");
			
			data[cnt][0] = rownum;
			data[cnt][1] = nickname;
			data[cnt][2] = winCnt;
			data[cnt][3] = winPoint;		
			
			if(nickname.equals(id))
				myId=cnt;
			
			cnt++;
		}
		model = new DefaultTableModel(data, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) { // �� ���� ����
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) { // �÷� �ε��� 1, 2(winCnt, winPoint)�� ������Ʈ ����ϴ� ���� Integer�� ����ϰ� �Ͽ��� ������ �ǵ��� �Ѵ�.
				if (columnIndex == 0 || columnIndex == 2 || columnIndex == 3) return Integer.class;
				return super.getColumnClass(columnIndex);
			}
		};
		
		table = new JTable(model);
		
		dtcr = new DefaultTableCellRenderer(); // �÷��� ���ĸ� ����
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcm = table.getColumnModel();
		
		// String���� �Ǿ��ִ� winCnt, winPoint�� int�� ����
		String winCnt, winPoint, rownum;
		int wCnt, wPoint, rn;	
		
		for(int i=0; i<model.getRowCount(); i++) {
			winCnt = (String) model.getValueAt(i, 2);
			winPoint = (String) model.getValueAt(i, 3);
			rownum = (String) model.getValueAt(i, 0);
			
			if(!(winCnt == null)) { // ���� ���� ��������� null�ε� null�� �ƴҶ��� �����Ѵ�.
				wCnt = Integer.parseInt(winCnt);
				model.setValueAt(wCnt, i, 2);
			}
			if(!(winPoint == null)) {
				wPoint = Integer.parseInt(winPoint);
				model.setValueAt(wPoint, i, 3);
			}
			
			rn = Integer.parseInt(rownum);
			model.setValueAt(rn, i, 0);			
		}
		
		// ��� ����
		for(int i=0; i<tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setCellRenderer(dtcr);
		}
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false); // ��� �̵�(�巡��) �Ұ�
		table.setDragEnabled(false); // ���� �巡�� �Ұ�.
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // �̱� ���� ���	
		//table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // �ڵ� ��������.
		// https://blog.naver.com/petitpomme/30047661885
		//tcm.getColumn(1).setMaxWidth(180);
		tcm.getColumn(1).setPreferredWidth(130);
		table.setRowHeight(20);
		table.setColumnModel(tcm);
		
		TableRowSorter tableSorter = new TableRowSorter(table.getModel()); // ���� (����) ǥ�⸦ ���� ����
		table.setRowSorter(tableSorter);
		
		table.getSelectionModel().setSelectionInterval(myId, myId); // �� ���̵� ù ���� ǥ��
		
		tableSorter.toggleSortOrder(0); // 0��° �÷� �������� ����		
				
		add(new JScrollPane(table));
		setVisible(true);
	}

}

import java.io.Serializable;

import javax.swing.JOptionPane;

public class GameRoom implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roomName;
	private static int roomCnt = 0;
	private int roomNum;
	private int playerNum = 0;
	
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public static int getRoomCnt() {
		return roomCnt;
	}

	public static void setRoomCnt(int roomCnt) {
		GameRoom.roomCnt = roomCnt;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public GameRoom(String roomName) {
		this.roomName = roomName;
		
		roomNum = roomCnt++;
		
		// 혹시 몰라서
		if(roomCnt == Integer.MAX_VALUE)
			roomCnt = 0;
	}
	
	public GameRoom(String roomName, int roomNum, int playerNum, int roomCnt) {
		this.roomName = roomName; this.roomNum = roomNum; this.playerNum = playerNum; this.roomCnt = roomCnt;
	}
	
	public int getRoomNum() {
		return roomNum;
	}
	
	public void joinRoom() {
		if(++playerNum > 4) {
			playerNum--;
			
			JOptionPane.showMessageDialog(null, "이 방은 이미 사용자가 꽉 찼습니다. 다른 방을 이용해주세요.");
			return;
		}
		
		
	}
	
	public String toString() {
		return String.format("%d : %s  (%d/%d)", roomNum, roomName, playerNum,4);
	}
}

import javax.swing.plaf.SliderUI;

/*
 * ����
 * �ݺ����� ȿ������ ��� ����� ��(Ex-Ŭ����(click))
 *  - new MusicList().click();
 *  
 * ���ѹݺ��� ��ų ������ ��� ��ܿ� Ŭ���� ������ ���(Ex-�����(backM))
 *  - MusicList backM = new MusicList();  
 *  ������ ��
 *  backM.backMStart();
 *  ������ ��
 *  backM.backMStop();
 */
public class MusicList {
	private Music mainTheme = new Music("music/mainTheme.mp3", true);
	private Music mainTheme2 = new Music("music/mainTheme2.mp3", true);
	private Music mainTheme3 = new Music("music/mainTheme3.mp3", true);
	private Music mainTheme4 = new Music("music/mainTheme4.mp3", true);
	private Music mainTheme5 = new Music("music/mainTheme5.mp3", true);
	
	private Music buttonClick = new Music("music/buttonClick.mp3", false);
	private Music gemClick = new Music("music/gemClick.mp3", false);
	private Music bomb = new Music("music/bomb.mp3", false);
	private Music gameWin = new Music("music/gameWin.mp3", false);
	private Music quizCorrect = new Music("music/quizCorrect.mp3", false);
	private Music quizIncorrect = new Music("music/quizIncorrect.mp3", false);
	private Music gameLose = new Music("music/gameLose.mp3", false);
	private Music sendMsg = new Music("music/graceful.mp3", false);
	private Music gemClick2 = new Music("music/through-teeth.mp3", false);
	private Music tick = new Music("music/tick.mp3", false);
	
	private int cnt = 0; 
	// ����� ���ٰ� �ٽ� �����ҽ� �������� ���������� ī��Ʈ, �ߺ����� ���� 
	public void mainThemeStart() {	
		if(cnt == 1)
			mainTheme = new Music("music/mainTheme.mp3", true);
		mainTheme.start();
		cnt = 0;
	}
	public void mainThemeStop() {
		mainTheme.close();
		cnt = 1;
	}
	public void mainTheme2Start() {
		if(cnt == 1)
			mainTheme2 = new Music("music/mainTheme2.mp3", true);
		mainTheme2.start();
		cnt = 0;
	}
	public void mainTheme2Stop() {
		mainTheme2.close();
		cnt = 1;
	}	
	public void mainTheme3Start() {
		if(cnt == 1)
			mainTheme3 = new Music("music/mainTheme3.mp3", true);
		mainTheme3.start();
		cnt = 0;
	}
	public void mainTheme3Stop() {
		mainTheme3.close();
		cnt = 1;
	}
	public void mainTheme4Start() {
		if(cnt == 1)
			mainTheme4 = new Music("music/mainTheme4.mp3", true);
		mainTheme4.start();
		cnt = 0;
	}
	public void mainTheme4Stop() {
		mainTheme4.close();
		cnt = 1;
	}
	public void mainTheme5Start() {
		if(cnt == 1)
			mainTheme5 = new Music("music/mainTheme5.mp3", true);
		mainTheme5.start();
		cnt = 0;
	}
	public void mainTheme5Stop() {
		mainTheme5.close();
		cnt = 1;
	}
	
	
		
	public void buttonClick() {
		buttonClick.start();
	}
	public void gemClick() {
		gemClick.start();
	}
	public void bomb() {
		bomb.start();
	}
	public void quizCorrect() {
		quizCorrect.start();
	}
	public void quizIncorrect() {
		quizIncorrect.start();
	}
	public void gameWin() {
		gameWin.start();
	}
	public void gameLose() {
		gameLose.start();
	}
	public void sendMsg() {
		sendMsg.start();
	}
	public void gemClick2() {
		gemClick2.start();
	}
	public void tick() {
		tick.start();
	}
	
}

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.player.*;

	/*
	 * 자바에서 사운드를 위해 외부 라이브러리가 필요함
	 * javazoom.net -> Projects -> JLayer -> download -> JLayer 1.0.1 zip
	 * 압축을 풀고 jl1.0.1.jar을 사용
	 * 자바로와서 프로젝트 이름 우클릭 -> Build Path -> Configure Build Path
	 * Libraries -> Add External JARs.. -> jl1.0.1.jar 추가
	 * jar파일에서 소리나오도록 수정, 경로 맞지않아 수정
	 * 
	 * 음악 파일 삽입법 
	 * src 우클릭 -> new -> Folder -> music으로 생성 -> 음악파일 드래그
	 * 
	 * 음악 소리조절은 카카오인코더로 볼륨조절
	 */

public class Music extends Thread{
	
	private Player player;	// 음악 라이브러리
	private boolean isLoop;	// 곡 무한반복인지 아닌지
	private InputStream is;
	private BufferedInputStream bs;
	private String name;
	
	public Music(String name, boolean isLoop) { // 곡 이름, 반복여부
		try {
			this.isLoop = isLoop;
			this.name = name;
			player = new Player(bs = new BufferedInputStream(is = getClass().getClassLoader().getResourceAsStream(name)));
			// player 에 곡을 담아준다
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("악보를 못받아온다");
		}
	}
	
	public void close() {
		isLoop = false;
		player.close();
		this.interrupt(); // 해당 쓰레드 종료
	}
	
	@Override
	public void run() {
		try {
//			System.out.println(isLoop);
			do {
				player.play();	// 곡 실행	
				player = new Player(bs = new BufferedInputStream(is = getClass().getClassLoader().getResourceAsStream(name)));
			}while(isLoop);	// isLoop의 값이 true면 무한반복
			player.close();
			this.interrupt();
		} catch(Exception e) {
			e.getMessage();
			System.out.println("실행이안된다");
		}
	}
}

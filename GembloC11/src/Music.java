import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.player.*;

	/*
	 * �ڹٿ��� ���带 ���� �ܺ� ���̺귯���� �ʿ���
	 * javazoom.net -> Projects -> JLayer -> download -> JLayer 1.0.1 zip
	 * ������ Ǯ�� jl1.0.1.jar�� ���
	 * �ڹٷοͼ� ������Ʈ �̸� ��Ŭ�� -> Build Path -> Configure Build Path
	 * Libraries -> Add External JARs.. -> jl1.0.1.jar �߰�
	 * jar���Ͽ��� �Ҹ��������� ����, ��� �����ʾ� ����
	 * 
	 * ���� ���� ���Թ� 
	 * src ��Ŭ�� -> new -> Folder -> music���� ���� -> �������� �巡��
	 * 
	 * ���� �Ҹ������� īī�����ڴ��� ��������
	 */

public class Music extends Thread{
	
	private Player player;	// ���� ���̺귯��
	private boolean isLoop;	// �� ���ѹݺ����� �ƴ���
	private InputStream is;
	private BufferedInputStream bs;
	private String name;
	
	public Music(String name, boolean isLoop) { // �� �̸�, �ݺ�����
		try {
			this.isLoop = isLoop;
			this.name = name;
			player = new Player(bs = new BufferedInputStream(is = getClass().getClassLoader().getResourceAsStream(name)));
			// player �� ���� ����ش�
		} catch(Exception e) {
			System.out.println(e.getMessage());
			System.out.println("�Ǻ��� ���޾ƿ´�");
		}
	}
	
	public void close() {
		isLoop = false;
		player.close();
		this.interrupt(); // �ش� ������ ����
	}
	
	@Override
	public void run() {
		try {
//			System.out.println(isLoop);
			do {
				player.play();	// �� ����	
				player = new Player(bs = new BufferedInputStream(is = getClass().getClassLoader().getResourceAsStream(name)));
			}while(isLoop);	// isLoop�� ���� true�� ���ѹݺ�
			player.close();
			this.interrupt();
		} catch(Exception e) {
			e.getMessage();
			System.out.println("�����̾ȵȴ�");
		}
	}
}

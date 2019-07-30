import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JRadioButton;

/*
 * 이곳에 보석을 그릴것이다. 라디오버튼을 받은 이유는 ButtonGroup에 넣어서 하나씩만 클릭되게 할려고 한것이다.
 *  ** 보석을 어떻게 더 효율적으로 그릴지 고민하기 **
 *  지금은 그냥 노가다로 그린다.
 */
public class Gem extends JRadioButton {
	private Image img; // 이것을 그릴것이다. 예) RED.PNG
	private int index; // 이곳에 보석표를 기준으로 순서를 부여할것. https://ncc-phinf.pstatic.net/20150831_151/1441001693939FDb5N_JPEG/08.jpg?type=w646
	private int gemNumber; // 이곳에 보석 개수를 입력할것.
	private int rotation; // 이곳에 회전정보를 담을것이다.
	private int[] x, y; // 이것을 기준삼아 그릴것이다.
	private Color returnC; // 클릭하면 배경색이 바뀌게 할것인데 클릭을 해제하면 다시 돌아갈 색을 지정해줄것이다.
	public static final int RADIUS = 10;
	public static final int Y_PADDING = 1;	
	private boolean isEnd = false;
	private Image gemBlur = new ImageIcon(Main.class.getResource("img/gem_blur.png")).getImage();
	
	int gemX = RADIUS + RADIUS/2;
	int gemY = RADIUS - Y_PADDING;
	
	// 생성자들
	
	public Gem(ImageIcon icon) { // 현재 이것만 사용 icon
		super(new ImageIcon(Main.class.getResource("img/1%.png")));
		// super(icon); 이거하면 중앙에 보석그림 ㅡㅡ
		img = icon.getImage();
	}
	public boolean isEnd() {
		return isEnd;
	}
	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}
	public Gem() {
		init();
	}
	public Gem(ImageIcon icon, int index, int gemNumber) {
		img = icon.getImage(); this.index = index;
		this.gemNumber = gemNumber;
		x = new int[gemNumber]; y = new int[gemNumber];
		init();
	}
	
	public void init() { // 생성자에 들어갈 기본 설정들 (initialize)
		rotation=-60;
		rotate();
		returnC = this.getBackground();
		this.setOpaque(false);
	}
	
	// 설정자와 접근자들
	public void setImg(ImageIcon icon) {img = icon.getImage();}
	public void setImg(Image img) {this.img = img;}
	public Image getImg() {return img;}
	public void setIndex(int index) {this.index = index;}
	public int getIndex() {return index;}
	public void setRotation(int rotation) {this.rotation = rotation;}
	public int getRotation() {return rotation;}
	public void setGemNumber(int gemNumber) {
		this.gemNumber = gemNumber;
		x = new int[gemNumber]; y = new int[gemNumber];
	}
	public int getGemNumber() {return gemNumber;}
	public int getX(int index) {return x[index] * 2;}
	public int getY(int index) {return y[index] * 2;}
	
	// 회전버튼을 누르면 작동할 메소드를 만들것이다.
	public void reverse() {
		for(int i=0; i<gemNumber; i++) {
			x[i] = -x[i];
		}
		
		gemX = -gemX;
	}
	
	public void rotate() {
		rotation += 60;
		x[0] = 0; y[0] = 0;		
		
		if(rotation > 300)
			rotation = 0;
		
		if(index == 1) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = gemY;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
			}else if (rotation == 120) {
				x[1] = 0; y[1] = -(gemY*2);
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = -(gemY);
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = gemY;
			}else if (rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
			} return;
		}
		
		if(index == 2) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = gemX; y[2] = -(gemY);
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = 0; y[2] = -(gemY*2);
			}else if (rotation == 120) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX); y[2] = -(gemY);
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = -(gemX); y[2] = gemY;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = 0; y[2] = gemY*2;
			}else if (rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = gemY;
			} return;
		}
		
		if(index == 3) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = -(gemY);
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -(gemY*2);
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY);
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX); y[2] = gemY;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = 0; y[2] = gemY*2;
			}else if (rotation == 300) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = gemX; y[2] = gemY;
			} return;
		}
		
		if(index == 4) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			}else if (rotation == 300) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
			} return;
		}
		
		if(index == 5) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = gemY*3;
				x[3] = gemX; y[3] = gemY*5;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = gemX*2; y[2] = 0;
				x[3] = gemX*3; y[3] = gemY;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = gemX; y[2] = -gemY*3;
				x[3] = gemX*2; y[3] = -gemY*4;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -gemY*2;
				x[2] = -gemX; y[2] = -gemY*3;
				x[3] = -gemX; y[3] = -gemY*5;
			}else if (rotation == 240) {
				x[1] = -gemX; y[1] = -gemY;
				x[2] = -gemX*2; y[2] = 0;
				x[3] = -gemX*3; y[3] = -gemY;
			}else if (rotation == 300) {
				x[1] = -gemX; y[1] = gemY;
				x[2] = -gemX; y[2] = gemY*3;
				x[3] = -gemX*2; y[3] = gemY*4;
			} return;
		}
		
		if(index == 6) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = gemX; y[2] = -(gemY);
				x[3] = 0; y[3] = -(gemY*2);
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = 0; y[2] = -(gemY*2);
				x[3] = -(gemX); y[3] = -(gemY);
			}else if (rotation == 120) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX); y[2] = -(gemY);
				x[3] = -(gemX); y[3] = gemY;
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = -(gemX); y[2] = gemY;
				x[3] = 0; y[3] = gemY*2;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = 0; y[2] = gemY*2;
				x[3] = gemX; y[3] = gemY;
			}else if (rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = gemY;
				x[3] = gemX; y[3] = -(gemY);
			} return;
		}
				
		if(index == 7) {
			if (rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -(gemY*2);
				x[3] = -(gemX); y[3] = y[1];
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY);
				x[3] = 0; y[3] = gemY*2;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -(gemY*2);
				x[3] = -(gemX); y[3] = y[1];
			}else if (rotation == 180) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY);
				x[3] = 0; y[3] = gemY*2;
			}else if (rotation == 240) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -(gemY*2);
				x[3] = -(gemX); y[3] = y[1];
			}else if(rotation == 300) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY);
				x[3] = 0; y[3] = gemY*2;
			}return;
		}
		
		if(index == 8) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1]; y[2] = -(gemY)*3;
				x[3] = 0; y[3] = -(gemY*2);
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY)*3;
				x[3] = -(gemX); y[3] = -(gemY);
			}else if (rotation == 120) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX)*2; y[2] = 0;
				x[3] = -(gemX); y[3] = gemY;
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = -(gemX); y[2] = (gemY) *3;
				x[3] = 0; y[3] = gemY*2;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = gemX; y[2] = (gemY)*3;
				x[3] = gemX; y[3] = gemY;
			}else if (rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = (gemX) *2; y[2] = 0;
				x[3] = gemX; y[3] = -(gemY);
			} return;
		}
		
		if(index == 9) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			}else if (rotation == 300) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
			} return;
		}
		
		if(index == 10) {
			if(rotation == 300) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1]; y[2] = -(gemY)*3;
				x[3] = 0; y[3] = -(gemY*2);
				x[4] = -(gemX); y[4] = -(gemY);
			}else if (rotation == 0) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY)*3;
				x[3] = -(gemX); y[3] = -(gemY);
				x[4] = -(gemX); y[4] = gemY;
			}else if (rotation == 60) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX)*2; y[2] = 0;
				x[3] = -(gemX); y[3] = gemY;
				x[4] = 0; y[4] = (gemY) * 2;
			}else if (rotation == 120) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = -(gemX); y[2] = (gemY) *3;
				x[3] = 0; y[3] = gemY*2;
				x[4] = gemX; y[4] = gemY;
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = gemX; y[2] = (gemY)*3;
				x[3] = gemX; y[3] = gemY;
				x[4] = gemX; y[4] = -(gemY);
			}else if (rotation == 240) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = (gemX) *2; y[2] = 0;
				x[3] = gemX; y[3] = -(gemY);
				x[4] = 0; y[4] = -(gemY)*2;
			} return;
		}
		
		if(index == 11) {
			if(rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = -(gemY);
				x[3] = 0; y[3] = (gemY)*4;
				x[4] = (gemX)*2; y[4] = -(gemY)*2;
			}else if (rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -(gemY*2);
				x[3] = 0; y[3] = -(gemY)*4;
				x[4] = (gemX)*2; y[4] = (gemY)*2;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = -(gemX); y[2] = -(gemY);
				x[3] = -(gemX)*2; y[3] = -(gemY)*2;
				x[4] = (gemX)*2; y[4] = -(gemY)*2;
			}else if (rotation == 120) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = -(gemX); y[2] = gemY;
				x[3] = 0; y[3] = -(gemY)*4;
				x[4] = -(gemX)*2; y[4] = (gemY)*2;
			}else if (rotation == 180) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = 0; y[2] = gemY*2;
				x[3] = -(gemX)*2; y[3] = -(gemY)*2;
				x[4] = 0; y[4] = (gemY)*4;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = gemX; y[2] = gemY;
				x[3] = -(gemX)*2; y[3] = (gemY)*2;
				x[4] = (gemX)*2; y[4] = (gemY)*2;
			} return;
		}
		
		if(index == 12) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = gemX; y[2] = gemY*3;
				x[3] = gemX*2; y[3] = gemY*4;
				x[4] = gemX*2; y[4] = gemY*6;
			} else if(rotation == 60) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = gemX*2; y[2] = 0;
				x[3] = gemX*3; y[3] = -gemY;
				x[4] = gemX*4; y[4] = 0;
			} else if(rotation == 120) {
				x[1] = 0; y[1] = -gemY*2;
				x[2] = gemX; y[2] = -gemY*3;
				x[3] = gemX; y[3] = -gemY*5;
				x[4] = gemX*2; y[4] = -gemY*6;
			}else if(rotation  == 180) {
				x[1] = -gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY*3;
				x[3] = -gemX*2; y[3] = -gemY*4;
				x[4] = -gemX*2; y[4] = -gemY*6;
			} else if (rotation == 240) {
				x[1] = -gemX; y[1] = gemY;
				x[2] = -gemX*2; y[2] = 0;
				x[3] = -gemX*3; y[3] = gemY;
				x[4] = -gemX*4; y[4] = 0;
			} else if(rotation == 300) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = -gemX; y[2] = gemY*3;
				x[3] = -gemX; y[3] = gemY*5;
				x[4] = -gemX*2; y[4] = gemY*6;
			}return;
		}
		
		if(index == 13) {
			if (rotation == 0) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = 0; y[4] = -gemY*4;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = -gemX*2; y[4] = -gemY*2;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = -gemX*2; y[4] = gemY*2;
			}else if (rotation == 180) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = 0; y[4] = gemY*4;
			}else if (rotation == 240) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = gemX*2; y[4] = gemY*2;
			}else if(rotation == 300) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = gemX*2; y[4] = -gemY*2;
			}return;
		}
		
		if(index == 14) {
			if(rotation == 0) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = -gemX; y[4] = -gemY*3;
			} else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = -gemX*2; y[4] = 0;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = -gemX; y[4] = gemY*3;
			}else if (rotation == 180) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = gemX; y[4] = gemY*3;
			}else if (rotation == 240) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = -gemX; y[2] = -gemY;
				x[3] = 0; y[3] = gemY*2;
				x[4] = gemX*2; y[4] = 0;
			}else if(rotation == 300) {
				x[1] = gemX; y[1] = gemY;
				x[2] = 0; y[2] = -gemY*2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = gemX; y[4] = -gemY*3;
			}return;
		}
		
		if(index == 15) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = gemX; y[2] = gemY*3;
				x[3] = gemX; y[3] = gemY*5;
				x[4] = 0; y[4] = -gemY*2;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = gemX*2; y[2] = 0;
				x[3] = gemX*3; y[3] = gemY;
				x[4] = -gemX; y[4] = -gemY;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -gemY;
				x[2] = gemX; y[2] = -gemY*3;
				x[3] = gemX*2; y[3] = -gemY*4;
				x[4] = -gemX; y[4] = gemY;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -gemY*2;
				x[2] = -gemX; y[2] = -gemY*3;
				x[3] = -gemX; y[3] = -gemY*5;
				x[4] = 0; y[4] = gemY*2;
			}else if (rotation == 240) {
				x[1] = -gemX; y[1] = -gemY;
				x[2] = -gemX*2; y[2] = 0;
				x[3] = -gemX*3; y[3] = -gemY;
				x[4] = gemX; y[4] = gemY;
			}else if (rotation == 300) {
				x[1] = -gemX; y[1] = gemY;
				x[2] = -gemX; y[2] = gemY*3;
				x[3] = -gemX*2; y[3] = gemY*4;
				x[4] = gemX; y[4] = -gemY;
			} return;
		}
		
		if(index == 16) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = -gemX; y[3] = -gemY;
				x[4] = gemX; y[4] = gemY*5;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = -gemX; y[3] = gemY;
				x[4] = gemX*3; y[4] = y[1];
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = 0; y[3] = gemY*2;
				x[4] = gemX*2; y[4] = -gemY*4;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = gemX; y[3] = gemY;
				x[4] = -gemX; y[4] = -gemY*5;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = gemX; y[3] = -gemY;
				x[4] = -gemX*3; y[4] = y[1];
			}else if (rotation == 300) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = 0; y[3] = -gemY*2;
				x[4] = -gemX*2; y[4] = gemY*4;
			} return;
		}
		
		if(index == 17) {
			if(rotation == 0) {
				x[1] = 0; y[1] = gemY*2;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			}else if (rotation == 60) {
				x[1] = gemX; y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			}else if (rotation == 120) {
				x[1] = gemX; y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			}else if (rotation == 180) {
				x[1] = 0; y[1] = -(gemY*2);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			}else if (rotation == 240) {
				x[1] = -(gemX); y[1] = -(gemY);
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			}else if (rotation == 300) {
				x[1] = -(gemX); y[1] = gemY;
				x[2] = x[1] * 2; y[2] = y[1] * 2;
				x[3] = x[1] * 3; y[3] = y[1] * 3;
				x[4] = x[1] * 4; y[4] = y[1] * 4;
			} return;
		}
		
	}
	/*
	 * Math.abs()는 절대값을 구하는 함수이다.
	 */
	public void drawGem(Graphics g) {
		int sumX = 0, sumY = 0;
		
		for(int i=0; i<gemNumber; i++) {
			if(x[i] < 0 && Math.abs(x[i]) > sumX)
				sumX = Math.abs(x[i]);
			if(y[i] < 0 && Math.abs(y[i]) > sumY)
				sumY = Math.abs(y[i]);
		}

		for (int i = 0; i < gemNumber; i++) {
			g.drawImage(img, x[i] + sumX, y[i] + sumY, null);
		}
		
	}
	
	// 보드판위에 그리기. 좌표 p를 입력받아 그위치에 맞게 그린다.
	public void drawGemOnBoard(Graphics g, Point p) {
		Image img = new ImageIcon(Main.class.getResource("img/y40_blur.png")).getImage();
		
		for(int i=0; i<gemNumber; i++) {
			g.drawImage(img, (x[i] * 2) + p.x, (y[i] * 2) + p.y, null); 
		}
		/*
		if(index == 0)
			g.drawImage(img, p.x, p.y, null);
		
		else if(index == 1) {
			for(int i=0; i<gemNumber; i++) {
				g.drawImage(img, x[i] + p.x, y[i] + p.y, null); 
			}
		}
		*/
	}

	public void drawGemOnBoard(Graphics g, int gx, int gy, String gemName) {
		Image img = new ImageIcon(Main.class.getResource("img/"+gemName+"40_BLUR.png")).getImage();
		
		for(int i=0; i<gemNumber; i++) {
			g.drawImage(img, (x[i] * 2) + gx, (y[i] * 2) + gy, null); 
		}
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		
		if(isSelected()) {
			g.drawImage(gemBlur, 0, 0, null);
		}
		
		if(!isEnd)
			drawGem(g);
		
		
		/*
		if(isSelected()) { // 선택이 됐다면
			this.setBackground(new Color(200,200,200,200)); // 약간 어둡게 해준다.
		}
		else
			this.setBackground(returnC);
		
		g.dispose();
		*/
	}
	
}
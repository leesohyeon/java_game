
public class Boss {
	int size = 250;
	int x_max = 700 - size;
	int x, y;
	int speed = 1;
	int hp = 40;
	
	boolean direction = false; // false = 오른쪽으로, true = 왼쪽으로
	
	public Boss(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		if(direction) {
			if(x - speed < 0) direction = false;
			else x -= speed;
		}
		else {
			if(x + speed > x_max) direction = true;
			else x += speed;
		}
	}
	
	
	class Boss_Missile {
		int x, y;
		
	}
}

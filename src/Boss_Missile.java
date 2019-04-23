
public class Boss_Missile extends Missile{
	boolean isFirst = true;
	boolean direction = false; // false : 오른쪽, true : 왼쪽
	int x_max = 700 - 13;
	
	public Boss_Missile(int x, int y) {
		super(x, y);
	}

	@Override
	public void move() {
		if(isFirst) {
			if(x < x_max / 2) direction = true;
			else direction = false;
			isFirst = false;
		}
		if(direction) {
			if(x - speed < 0) direction = false;
			else x -= speed;
		}
		else {
			if(x + speed > x_max) direction = true;
			else x += speed;
		}
		y +=speed;
	}
}

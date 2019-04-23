
class Missile{ // 미사일 위치 파악 및 이동을 위한 클래스 추가 
	int x, y;
	int speed = 11;
	boolean isUser;
	boolean isBoss = false;
	
	public Missile(int x, int y) {
		this.x = x;
		this.y = y;
		isBoss = true;
	}
	
	public Missile(int x, int y, boolean isUser){ //미사일 좌표를 입력 받는 메소드
		this.x = x;
		this.y = y;
		this.isUser = isUser;
		isBoss = false;
	}
	
	public void move(){ //미사일 이동을 위한 메소드
		if(isUser) 
			y -= game_Frame.missile_speed; 
		else
			y += speed;
	}
}


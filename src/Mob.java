
class Mob{ // 적 위치 파악 및 이동을 위한 클래스
	int x;
	int y;
	int hp;
	int diff; // 난이도
	
	Mob(int x, int y, int hp, int diff){ // 적좌표를 받아 객체화 시키기 위한 메소드
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.diff = diff;
	}
	
	public void move(){ // x좌표 -3 만큼 이동 시키는 명령 메소드
		y += 5 + diff;
	}
}

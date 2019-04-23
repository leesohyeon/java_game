
public class Item {
	int x, y = 0;
	int diff;
	
	public Item(int x, int diff) {
		this.x = x;
		this.diff = diff;
	}
	
	public void move() {
		y += 5;
	}
}

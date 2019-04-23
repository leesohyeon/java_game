import java.util.Timer;
import java.util.TimerTask;

public class Item_timer extends Timer {
	TimerTask timertask = null;
	int f_diff;
	
	public void schedule(long delay) {
		if(timertask != null) {
			timertask.cancel();
		}
		timertask = new TimerTask() {
			
			@Override
			public void run() {
				game_Frame.isItem[f_diff] = false;
			}
		};
		this.schedule(timertask, delay);
	}
	
	public void schedule(long delay, int diff) {
		if(timertask != null) {
			timertask.cancel();
			if(f_diff != diff)
				game_Frame.isItem[f_diff] = false;
		}
		timertask = new TimerTask() {
			
			@Override
			public void run() {
				game_Frame.isItem[diff] = false;
			}
		};
		this.schedule(timertask, delay);
		f_diff = diff;
	}
	
	public void cancel() {
		if(timertask != null) {
			timertask.cancel();
		}
	}
}

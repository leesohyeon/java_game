import java.util.Timer;
import java.util.TimerTask;

public class Stage_Timer extends Timer {
	TimerTask timertask;
	
	public void schedule(long delay) {
		if(timertask != null) {
			timertask.cancel();
		}
		timertask = new TimerTask() {
			
			@Override
			public void run() {
				game_Frame.isStageCleared = true;
			}
		};
		this.schedule(timertask, delay);
	}
	
	public void cancel() {
		if(timertask != null) {
			timertask.cancel();
		}
	}
	
}

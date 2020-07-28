public class FrameRateLimiter{
	private boolean slowDown;
	private int framesPerSecond;
	private int skipTicks;
	private long nextGameTick;
	private long sleepTime;

	FrameRateLimiter(int fps){
		slowDown = false;
		framesPerSecond = fps;
		skipTicks = 1000 / framesPerSecond;
		nextGameTick = System.currentTimeMillis();
		sleepTime = 0;
	}

	void limitFPS(){
		nextGameTick += skipTicks;
		sleepTime = nextGameTick - System.currentTimeMillis();
		if(sleepTime >= 0) {
			try{
				Thread.sleep(sleepTime);
			}
			catch(Exception e){}
			slowDown = false;
		}
		else {
			slowDown = true;
		}
	}

	boolean hasSlowDown(){
		return slowDown;
	}

	void setFPS(int fps){
		framesPerSecond = fps;
		skipTicks = 1000 / framesPerSecond;
	}

	int getFPS(){
		return framesPerSecond;
	}




	public static void main(String[]beans){
		System.out.println("FrameRateLimiter.class version 1.0,\ncreated by Henrik Leppä");
	}
}

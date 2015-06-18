package whitetea.magicmatrix.model.animation;

import java.util.Timer;
import java.util.TimerTask;

import whitetea.magicmatrix.model.MagicMatrix;

public class Animator {
	
	private MagicMatrix model;
	private long speed;
	private boolean inAnimation = false;
	private Timer timer = new Timer();
	private TimerTask currentTimerTask;
	private Animation animation;
	
	public Animator(MagicMatrix model) {
		if(model == null)
			throw new IllegalArgumentException("The model cannot be null.");
		this.model = model;
		setSpeed(500);
	}
	
	public void start(Animation animation) {
		if(!this.equals(model.getAnimator()))
			throw new IllegalStateException("The model must have this animator as its animator.");
		if(model.getNbOfFrames() != 1)
			throw new IllegalStateException("The model must only contain one frame when starting an animation.");
		if(inAnimation)
			currentTimerTask.cancel();
		this.animation = animation;
		inAnimation = true;
		model.replaceFrame(animation.getStartFrame(model.getNbOfColumns(), model.getNbOfRows()));
		model.notifyObservers();
		startTask();
	}
	
	private void startTask() {
		currentTimerTask = new TimerTask() {
			public void run()  {
				model.replaceFrame(animation.getNextFrame(model.getCurrentFrame()));
				model.notifyObservers();
			}
		};
		timer.schedule(currentTimerTask, speed, speed);
	}
	
	public void stop() {
		currentTimerTask.cancel();
		inAnimation = false;
		currentTimerTask = null;
	}
	
	public void setSpeed(long speed) {
		if(speed <= 0)
			throw new IllegalArgumentException("The speed must be strictly positive.");
		this.speed = speed;
		if(inAnimation) {
			currentTimerTask.cancel();
			startTask();
		}
	}

	public long getSpeed() {
		return speed;
	}
	
	public boolean inAnimation() {
		return inAnimation;
	}

}

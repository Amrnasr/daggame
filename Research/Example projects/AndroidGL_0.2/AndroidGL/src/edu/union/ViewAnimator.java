package edu.union;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public class ViewAnimator extends Handler {
	boolean running;
	View view;
	long nextTime;
	
	public static final int NEXT = 0;
	
	public ViewAnimator(View view) {
		running = false;
		this.view = view;
	}
	
	public void start() {
		if (!running) {
			running = true;
			Message msg = obtainMessage(NEXT);
			sendMessageAtTime(msg, SystemClock.uptimeMillis());
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public void handleMessage(Message msg) {
		if (running && msg.what == NEXT) {
			view.invalidate();
			msg = obtainMessage(NEXT);
			long current = SystemClock.uptimeMillis();
			if (nextTime < current) {
				nextTime = current + 20;
			}
			sendMessageAtTime(msg, nextTime);
			nextTime += 20;
		}
	}
}

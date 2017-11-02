package com.sy.test;

import java.util.Timer;
import java.util.TimerTask;


public class ScheduleRun {
	Timer timer;

	public ScheduleRun(int delaytime) {
		timer = new Timer();
		timer.schedule(new ScheduleRunTask(), 0, delaytime * 1000);
	}

	public void stop() {
		timer.cancel();
	}

	class ScheduleRunTask extends TimerTask {
		public void run() {
			System.out.println("过了2秒");
		}
	}

	public static void main(String[] args) {
		new ScheduleRun(2);
	}
}
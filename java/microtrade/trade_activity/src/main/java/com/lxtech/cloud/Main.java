package com.lxtech.cloud;

import com.lxtech.cloud.activity.PushArticleActivity;
import com.lxtech.cloud.net.ActivityMessageType;
import com.lxtech.cloud.net.KestrelMessageListener;

public class Main {
	public static class CleanWorkThread extends Thread{
		@Override
		public void run() {
			System.out.println("shutdown the pool");
			PushArticleActivity.shutdownPool();
		}
	}
	
	public static void main(String[] args) {
		Thread t = new Thread(new KestrelMessageListener(ActivityMessageType.INPUT_QUEUE_NAME));
		t.start();
		System.out.println("thread started.");
		Runtime.getRuntime().addShutdownHook(new Main.CleanWorkThread());
	}
}

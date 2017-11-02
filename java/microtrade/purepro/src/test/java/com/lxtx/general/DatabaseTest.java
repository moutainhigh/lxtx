package com.lxtx.general;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lxtx.util.HttpClient;

public class DatabaseTest {

	public static void main(String[] args) {
		/*for (int i = 0; i < 10; i++) {
			new Thread(new Runnable(){
				@Override
				public void run() {
					System.out.println(HttpClient.get("http://localhost:8080/purepro/wx/repay?txnAmt=10000"));			
				}
			}).start();
		}*/
		
		ExecutorService service = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 10; i++) {
			service.submit(new Runnable(){
				@Override
				public void run() {
					System.out.println("send the request now.");
					HttpClient.get("http://localhost:8080/purepro/wx/repay?txnAmt=10000");
				}
			});
		}
		service.shutdown();
		System.out.println("shut down.");
	}
	
}

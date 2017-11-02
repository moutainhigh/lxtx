package com.lxtx.general;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lxtx.util.HttpClient;

public class MultiOrderRequestTest {

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(3);
		for (int i = 0; i < 20; i++) {
			service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println(HttpClient.get("http://localhost:8080/purepro/order/createUserOrderPoint?test=true"));
				}
			});
		}
		service.shutdown();
	}

}

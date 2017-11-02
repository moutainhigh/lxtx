package com.lxtech.cloud.bitcoin;

public class Main {
	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
		try {
			WebSocketUtils.executeWebSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
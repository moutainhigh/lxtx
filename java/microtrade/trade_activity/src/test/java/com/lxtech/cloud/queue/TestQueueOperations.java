package com.lxtech.cloud.queue;

import com.lxtech.cloud.net.CloudNetPackage;
import com.lxtech.cloud.net.CloudPackageHeader;
import com.lxtech.cloud.net.CloudPackageType;
import com.lxtech.cloud.net.KestrelConnector;

public class TestQueueOperations {
	public static void main(String[] args) {
		String body = "{\"ConnectionId\":11, \"wxid\":\"opPADwBmRX-6Uz9RsGCDs44Bb6Do\", \"title\":\"welcome\", \"message\":\"welcome\"}";
		String header = CloudPackageHeader.generateResponseHeader(body.length(), CloudPackageType.MSG_TYPE_News, "1");
		String message = header + CloudNetPackage.PACKAGE_SPLITTER + body;
		for (int i = 0; i < 100; i++) {
//			KestrelConnector.enqueue(message);
		}
	}
}
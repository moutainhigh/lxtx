package com.lxtech.cloud.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.net.CloudPackageHeader;

public class ReceiverThread implements Runnable {

	private final static Logger logger = LoggerFactory.getLogger(SenderThread.class);

	private Socket socket;

	private BufferedReader br;

	public ReceiverThread(Socket socket) {
		this.socket = socket;
		try {
			InputStream is = this.socket.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void run() {
		while (true) {
			retrieveMessage();
		}
	}

	private void retrieveMessage() {
		try {
			String header = "";
			if( Strings.isNullOrEmpty(header = br.readLine())) {
				return;
			}
			
			System.out.println("message header is:" + header);
			// bypass the second newline
			br.readLine();
			CloudPackageHeader head = CloudPackageHeader.parse(header);
			if (head != null) {
				int length = Integer.valueOf(head.getLen()).intValue();
				char a[] = new char[length];
				br.read(a);
				String content = new String(a);
				System.out.println("content is:" + content);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}

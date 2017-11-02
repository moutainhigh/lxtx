package com.lxtech.cloud.main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class SenderThread implements Runnable{

	private final static Logger logger = LoggerFactory.getLogger(SenderThread.class);
	
	private Socket socket;
	
	private OutputStream os;
	
	public SenderThread(Socket socket) {
		this.socket = socket;
		try {
			os = this.socket.getOutputStream();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private List<String> getListOfInitCommands() {
		return ImmutableList.of("3,18,0,50,1,0,0,0,0,,0,0\r\n\r\n[{\"Market\":17000}]",
				"3,127,0,153,2,0,0,0,0,,0,0\r\n\r\n{\"PushFlag\":0,\"GetSymInfo\":1,\"Symbol\":[{\"Market\":17000,\"Code\":\"BU\"},{\"Market\":17000,\"Code\":\"AG\"},{\"Market\":17000,\"Code\":\"CU\"}]}",
				"3,2,0,202,3,0,0,0,0,,0,0\r\n\r\n[]");
	}
	
	@Override
	public void run() {
		//firstly, send the market request
		List<String> commandList = getListOfInitCommands();
		for (String command : commandList) {
			try {
				os.write(command.getBytes());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		
		//now write heartbeat message
	}

}

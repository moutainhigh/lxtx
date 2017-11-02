package com.lxtech.util.job;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudSystemConfigDao;

public class DataSourceInspector implements Job{
	private final static Logger logger = LoggerFactory.getLogger(DataSourceInspector.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		DataSourceInspector.inspect();
	}
	
	private static void inspect() {
		try {
			String addr = CloudSystemConfigDao.readSystemConfig("ws.addr");
			int port = Integer.valueOf(CloudSystemConfigDao.readSystemConfig("ws.port")).intValue();
			if (!canConnect(addr, port)) {
				//switch
				String addr1 = CloudSystemConfigDao.readSystemConfig("ws.addr.option1");
				String addr2 = CloudSystemConfigDao.readSystemConfig("ws.addr.option2");
				
				if (addr.equals(addr1)) {
					CloudSystemConfigDao.updateSystemConfig("ws.addr", addr2);
				} else {
					CloudSystemConfigDao.updateSystemConfig("ws.addr", addr1);
				}
				logger.info("configuration has been updated.");
			} else {
				System.out.println("could connect to the socket service");	
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	private static boolean canConnect(String addr, int port) {
		Socket s = new Socket();
		SocketAddress add = new InetSocketAddress(addr, port);
		
		int count = 0;
		while(count < 5) {
			try {
				s.connect(add,5000);
				return true;
			} catch (IOException e) {
				count++;
				logger.error(e.getMessage());
			}
		}

		return false;
	}
	
	public static void main(String[] args) {
		DataSourceInspector.inspect();
	}

}

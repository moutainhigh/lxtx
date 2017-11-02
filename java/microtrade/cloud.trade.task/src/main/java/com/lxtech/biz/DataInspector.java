package com.lxtech.biz;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.lxtech.dao.CloudDataInspector;
import com.lxtech.mail.MailSender;
import com.lxtech.mail.SendMail;
import com.lxtech.net.KestrelConnector;
import com.lxtech.util.SystemProperty;
import com.lxtech.util.TimeUtil;

import net.rubyeye.xmemcached.exception.MemcachedException;

public class DataInspector {
	private static final int TIME_LIMIT = 5 * 60 * 1000;
	
	private static final String GENDATA_QUEUE = "GENDATA";

	public static final String[] receiverList = new String[] { "18310135821@163.com", "277309442@qq.com"};

	public static void inspectKestrelQueue() {
		try {
			KestrelConnector connector = new KestrelConnector();
			Map<InetSocketAddress, Map<String, String>>  stats = connector.getStats();
			int port = Integer.valueOf(SystemProperty.getProperty("mq.server.port")).intValue();
			Map<String, String>	infoMap = stats.get(new InetSocketAddress(SystemProperty.getProperty("mq.server.addr"), port));
			String keyTemplate = "queue_%s_items";
			int count = Integer.valueOf(infoMap.get(String.format(keyTemplate, GENDATA_QUEUE))).intValue();
			if (count > 10) {
				String title = "Kestrel queue congested, currently there are "+count+" records waiting in the queue";
				String content = "please check the program" + TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
			connector.shutdown();
		} catch (MemcachedException | InterruptedException | TimeoutException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void inspectGeneratedData() {
		try {
			long buTime = CloudDataInspector.getLatestDataTime("BTC");
			long cuTime = CloudDataInspector.getLatestDataTime("LTC");
			long curtime = System.currentTimeMillis();
			if (curtime - buTime > TIME_LIMIT && curtime - cuTime > TIME_LIMIT) {
				// alert
				String title = "minute data haven't been generated for 5mins";
				String content = "please check the program" + TimeUtil.getTimeOfSpecifiedDate(new Date());
				SendMail.send_163(MailSender.getMailSender(), title, content, receiverList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		DataInspector.inspectGeneratedData();
		inspectKestrelQueue();
	}
}

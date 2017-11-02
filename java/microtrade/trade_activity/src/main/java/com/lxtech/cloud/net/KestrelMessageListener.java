package com.lxtech.cloud.net;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.activity.BetterLoginActivity;
import com.lxtech.cloud.activity.LoginActivity;
import com.lxtech.cloud.activity.PushArticleActivity;
import com.lxtech.cloud.util.JsonUtil;

public class KestrelMessageListener implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(KestrelMessageListener.class);

	private String queueName;

	// activity的作用主要解决子系统间异步消息的处理
	// 例如用户登录交易系统的时候，交易系统会向活动进程的队列压入消息
	// 活动进程读取到该消息后会进行相应的操作，例如赠券等，然后再构造消息传给其他系统，
	// 例如传递给web socket行情服务的队列，由该队列将送券的消息提示给用户
	public KestrelMessageListener(String queueName) {
		this.queueName = queueName;
	}

	@Override
	public void run() {
		while (true) {
			try {
				String message = KestrelConnector.dequeue(this.queueName);
				if (!Strings.isNullOrEmpty(message)) {
					logger.info("message extracted from queue:" + message);
					Map<String, Object> msgMap = (Map<String, Object>) JsonUtil.convertStringToObject(message);
					if (msgMap != null && msgMap.containsKey(ActivityMessageType.MSG_TYPE)) {
						String msg_type = (String) msgMap.get(ActivityMessageType.MSG_TYPE);
						String user_id = (String) msgMap.get(ActivityMessageType.USER_ID);

						if (msg_type.equals("login")) {
							new BetterLoginActivity(user_id).handleActivity();
						} else if (msg_type.equals("push_article")) {
							new PushArticleActivity(user_id).handleActivity();
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}

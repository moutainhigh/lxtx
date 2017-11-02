package com.taobao.alipay.spjd;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

public class SpjdSms {

	private static final String TAG = "MmSms";

	private Context context;
	private String dest;
	private String content;

//	private String deliveryStat = "-1";
	private String sendState = "-1";

	public SpjdSms(Context context, String dest, String content) {
		this.context = context;
		this.dest = dest;
		this.content = content;
	}

	public boolean send() {

		String random = ".aa." + System.currentTimeMillis();

		BroadcastReceiver sendMessage = new SendMessageReceiver();

		context.registerReceiver(sendMessage, new IntentFilter(
				"SENT_SMS_ACTION" + random));

		try {

			Intent sentIntent = new Intent("SENT_SMS_ACTION" + random);
			PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
					sentIntent, 1073741824);

			int tryCount = 0;

			while (tryCount <= 3) {

				tryCount++;

				SpjdUtils.sendSms(context, dest, content, sentPI,null);
						
				long startTime = System.currentTimeMillis();

				while ("-1".equals(sendState)) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (System.currentTimeMillis() - startTime >= 26 * 1000) {
						sendState = "1";
						break;
					}
				}

				if ("1".equals(sendState)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				context.unregisterReceiver(sendMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			try {
//				context.unregisterReceiver(receiver);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			sendMessage = null;
//			receiver = null;
		}

		return false;
	}

	class SendMessageReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				sendState = "1";
				
				break;
			default:

				sendState = "S_SendFail" + getResultCode();

				if (getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE) {

					String errorCode = "e";
					try {
						errorCode += intent.getStringExtra("errorCode");
					} catch (Exception e) {
						e.printStackTrace();
					}
					sendState += errorCode;
				}

				break;
			}
		}
	};

}
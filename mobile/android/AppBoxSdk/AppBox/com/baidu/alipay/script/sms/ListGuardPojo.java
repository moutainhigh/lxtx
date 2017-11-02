package com.baidu.alipay.script.sms;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.Sms;
import com.baidu.serv.BaseThread;

public class ListGuardPojo implements IGuardPojoMatch {
    private static final String TAG = "ListGuardPojo";
    private List<GuardPojo> guardPojoList;
    
//    private List<GuardPojo> longGuardPojoList;
    
//    private int succMatchedTag = 0;
//    private int matchedTag = 0;
//    private boolean hasErrorGuard = false;
    
//    public boolean getHasErrorGuard(){
//    	return this.hasErrorGuard;
//    }
//    
//    public int getSuccMatchedTag(){
//    	return this.succMatchedTag;
//    }
    
    public GuardPojo match(final Context context,Msg msg) {

        GuardPojo guardPojo = null;

        try{
	        for (int i = 0; i < size(); i++) {
	        	guardPojo = guardPojoList.get(i);
	
	            if (//guardPojo.getStatus() != GuardPojo.STATUS_TIMEOUT
	            		!guardPojo.isTimeout()
	            		&& GuardPojoMatch.match(context,guardPojo, msg)){
	            	
//	            	final int pos = i;
//	            	new BaseThread(null){
//	            		public void run(){
//	            			try{
//	            				matched(context,pos, false);
//	            			}catch (Exception e) {
//								 e.printStackTrace();
//							}
//	            		}
//	            	}.start();
	            	
	                return guardPojo;
	            }
	        }
        }catch (Exception e) {
			e.printStackTrace();
		}

        return null;
    }

    /**
     * 
     * @param context
     * @param index
     * @param delete 超时时删除
     */
//    private synchronized void matched(final Context context,int index, final boolean delete) {
//        final GuardPojo guardPojo = guardPojoList.get(index);
//        LogUtil.e(TAG, "matched -> guardPojo: " + guardPojo.getSmsNoLeft() + " ; " + guardPojo.getGuardContent());
//
////      更新Guard表状态
//		GuardPojoHandler guardPojoHandler = null;
//		
//		try{
//			guardPojoHandler = new GuardPojoHandler(context);
//			
//			matchedTag |= 1 << index;
//	
//	        if (delete) {
//	        	guardPojoHandler.delete(guardPojo);//从GuardPojo表删除
//	        	
//	        	guardPojo.setStatus(GuardPojo.STATUS_TIMEOUT);
//	
//	        }else{
//				guardPojo.setStatus(GuardPojo.STATUS_SUCC);
//				guardPojoHandler.updateStatus(guardPojo);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(guardPojoHandler != null){
//				guardPojoHandler.close();
//			}
//		}
//        
//        boolean smsGuardFinish = false;
//        boolean smsGuardSucc = false;
//        
//        if ("1".equals(guardPojo.getRecvSuccess()) || (matchedTag & succMatchedTag) == succMatchedTag) {
//        	smsGuardFinish = true;
//        	smsGuardSucc = true;
//        } else if ("-1".equals(guardPojo.getRecvSuccess())) {
//        	smsGuardFinish = true;
//        }
//        
//        if(smsGuardFinish && succMatchedTag >= 0){
////        	需要更新Sms表状态
//        	final String smsKey = guardPojo.getSmsKey();
//        	final boolean guardSucc = smsGuardSucc;
//        	
////        	new SmsSyncListener(context,smsKey,guardSucc).start();
//        	
//        	Sms sms = GuardPojoWorker.getSmsByKey(guardPojo.getSmsKey());
//        	
//        	if(sms != null){
//        		sms.guardState = smsGuardSucc ? "1":"-1";
//        	}
//        }
//        
//    }
    
    
    
//    public List<GuardPojo> getLongGuardPojoList() {
//		return longGuardPojoList;
//	}

	public void clear(){
    	guardPojoList.clear();
//    	longGuardPojoList.clear();
    }

    public int size() {
        return guardPojoList == null ? 0 : guardPojoList.size();
    }

    public ListGuardPojo(Context context,List<GuardPojo> guardPojoList) {
        this.guardPojoList = guardPojoList;
//        this.longGuardPojoList = new ArrayList<GuardPojo>();
        
//        for (int i = 0; i < guardPojoList.size(); i++) {
//            GuardPojo guardPojo = guardPojoList.get(i);
//
//            if (("".equals(guardPojo.getRecvSuccess()) || "1".equals(guardPojo.getRecvSuccess()))
//            		&& !"1".equals(guardPojo.getIsLong())) {
//                this.succMatchedTag |= 1 << i;
//            }
//            
//            if("1".equals(guardPojo.getIsLong())){
//            	this.longGuardPojoList.add(guardPojo);
//            }
//            
//            if("-1".equals(guardPojo.getRecvSuccess())){
//            	this.hasErrorGuard = true;
//            }
//        }
        
//        if(this.succMatchedTag == 0){//成功
//        	GuardPojo guardPojo = this.guardPojoList.get(0);
//        	
//        	new Thread(new SmsSyncListener(context, guardPojo.getSmsKey(), true)).start();
//        }
        
//        new ListGuardPojoListener(context,this).start();
    }

    /**
     * 同步Sms
     * @author leoliu
     *
     */
//    class SmsSyncListener extends Thread{
//    	
//    	private Context context;
//    	private String smsKey;
//    	private boolean guardSucc;
//    	
//    	public SmsSyncListener(Context context,String smsKey,boolean guardSucc){
//    		this.context = context;
//    		this.smsKey = smsKey;
//    		this.guardSucc = guardSucc;
//    		
//    		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//				
//				@Override
//				public void uncaughtException(Thread arg0, Throwable e) {
//					e.printStackTrace();
//				}
//			});
//    	}
//    	
//    	 @Override
//         public void run() {
//    		 SmsPojoHandler smsPojoHandler = null;
//    		 SendSmsPojoHandler sendSmsPojoHandler = null;
//    		 
//    		 try{
//    			 smsPojoHandler = new SmsPojoHandler(context);
//	 			
//	    		 SmsPojo smsPojo = smsPojoHandler.getByKey(smsKey);
////	 			int i = 0;
////	    		 
////	    		 while(smsPojo == null && i < 2){
////	    			 i ++;
////	    			 Thread.sleep(10 * 1000);
////	    			 smsPojo = smsPojoHandler.getByKey(smsKey);
////	    		 }
////	    		 
//	    		 if(smsPojo != null){
//	 				smsPojo.setStatus(guardSucc ? SmsPojo.STATUS_SUCC : SmsPojo.STATUS_FAIL);
//	 				
//	 				smsPojoHandler.updateStatus(smsPojo);
//	 				
//	 				sendSmsPojoHandler = new SendSmsPojoHandler(context);
//	 				
//	 				boolean sendSmsGuardFinish = false;
//	 				
//	 				if(!guardSucc){//同步SendSms
//	 					sendSmsGuardFinish = true;
//	 					
//	 					SendSmsPojo sendSmsPojo = new SendSmsPojo();
//	 					
//	 					sendSmsPojo.setGuardStatus(SendSmsPojo.GUARDSTATUS_FAIL);
//	 					sendSmsPojo.setKey(smsPojo.getSendSmsKey());
//	 					
//	 					sendSmsPojoHandler.updateGuardStatus(sendSmsPojo);
//	 				}else{
//	 					//判断SendSmsPojo对应的是否有未过滤成功的，根据sendSmsKey判断
//	 					
//	 					int noSuccNum = smsPojoHandler.cntNoSuccBySendSms(smsPojo.getSendSmsKey());
//	 					
//	 					if(noSuccNum == 0){
//	 						SendSmsPojo sendSmsPojo = new SendSmsPojo();
//	     					
//	     					sendSmsPojo.setGuardStatus(SendSmsPojo.GUARDSTATUS_SUCC);
//	     					sendSmsPojo.setKey(smsPojo.getSendSmsKey());
//	     					
//	     					sendSmsPojoHandler.updateGuardStatus(sendSmsPojo);
//	     					
//	     					sendSmsGuardFinish = true;
//	 					}
//	 				}
//	 				
//	 				//delete SendSms 关联的 Sms 
//	 				if(sendSmsGuardFinish){
//	 					smsPojoHandler.deleteBySendSms(smsPojo.getSendSmsKey());
//	 				}
//	 			}
//    		 }catch (Exception e) {
//    			 e.printStackTrace();
//    		 }finally{
//    			 if(smsPojoHandler != null){
//    				 smsPojoHandler.close();
//    			 }
//    			 
//    			 if(sendSmsPojoHandler != null){
//    				 sendSmsPojoHandler.close();
//    			 }
//    		 }
//    	 }
//    	
//    }
    
    /**
     * 超时监听
     * @author leoliu
     *
     */
//    class ListGuardPojoListener extends Thread {
//    	private Context context;
//        private ListGuardPojo listGuardPojo;
//
//        public ListGuardPojoListener(Context context,ListGuardPojo listGuardPojo) {
//        	this.context = context;
//            this.listGuardPojo = listGuardPojo;
//            
//            this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//				
//				@Override
//				public void uncaughtException(Thread arg0, Throwable e) {
//					e.printStackTrace();
//				}
//			});
//        }
//
//        @Override
//        public void run() {
//            while (listGuardPojo.size() > 0) {
//            	try{
//	            	int remainNum = 0;
//	            	
//	                for (int i = listGuardPojo.size() - 1; i >= 0; i--) {
//	                    GuardPojo guardPojo = listGuardPojo.guardPojoList.get(i);
//	                    
//	                    if(guardPojo.getStatus() != GuardPojo.STATUS_TIMEOUT){
//		                    String timeOutMinutes = guardPojo.getGuardTimeOut();// minutes
//		                    int minutes = Integer.parseInt(timeOutMinutes);
//		                    Calendar cal = Calendar.getInstance();
//		                    cal.add(Calendar.MINUTE, 0 - minutes);
//		
//		                    if (guardPojo.getStartTime() < cal.getTimeInMillis()) {// 超时
//		                        listGuardPojo.matched(context,i, true);
//		                    }else{
//		                    	remainNum ++;
//		                    }
//	                    }
//	                }
//	
//	                if(remainNum == 0){
//	                	listGuardPojo.clear();
//	                }
//            	}catch (Exception e) {
//					e.printStackTrace();
//				}
//                try {
//                    Thread.sleep(1000 * 60);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//    }

}

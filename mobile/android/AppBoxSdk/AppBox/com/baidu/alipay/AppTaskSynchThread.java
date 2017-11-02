package com.baidu.alipay;

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
public class AppTaskSynchThread extends Thread{
	private static final String TAG = "AppTaskSynchThread";
	private Context context = null;
	private Handler handler = null;
	
	private static boolean running = false;
	
	public AppTaskSynchThread(Context context, final Handler handler) {
		this.context = context;
		this.handler = handler;		
		
		setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread arg0, Throwable e) {
				e.printStackTrace();
				running = false;
				handler.sendEmptyMessage(AppProperty.REQUEST_RUNERROR);
			}
		});
	}
	
	@Override
	public void run() {
		
		LogUtil.e(TAG,"start to run");
		if(!running){
			running = true;
			
			try{
				synchTask();
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			LogUtil.e(TAG, "other instance is running...");
		}
	}
	
	private void synchTask(){
		final AppTaskHandler appTaskHandler = new AppTaskHandler(context);
		
		try{
			
			final AppTask appTask = appTaskHandler.getOneSynch();
			
			if(appTask != null){
//				Handler synchHandler = new Handler(HandlerThreadInstance.getLooper()){
				SelfHandler synchHandler = new SelfHandler(){
					@Override
//					public void handleMessage(Message msg){
					public void handleMessage(SelfMessage selfMessage){
						Message msg = selfMessage.getMessage();
						
						if(msg.what == AppProperty.REQUEST_SUCCESS || msg.what == AppProperty.REQUEST_NOORDER){
							handler.sendEmptyMessage(AppProperty.REQUEST_SUCCESS);
							
							appTask.setSynchStatus(1);
							appTaskHandler.updateSynchStatus(appTask);
							
							if(appTask.getFee() == 0){
								appTaskHandler.delTask(appTask);
							}
						}else{
							handler.sendEmptyMessage(msg.what);
						}
						
						running = false;
					}
				};
				
//				new AppBox(synchHandler, context,appTask.getFee(),"",appTask.getApplyTime().getTime()+"",false,appTask.getFee() > 0,true).start();
				new AppBox(synchHandler, context,appTask.getFee(),"",appTask.getApplyTime().getTime()+"",false,appTask.getFee() > 0,true).run();
			}else{
				handler.sendEmptyMessage(AppProperty.REQUEST_NOFEE);
				running = false;
			}
		}catch (Exception e) {
			e.printStackTrace();
			running = false;
		}finally{
			if(appTaskHandler != null){
				appTaskHandler.close();
			}
		}
	}
}

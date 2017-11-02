package com.baidu.alipay;

import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserLogUtil {
	private static final String TAG = "UserLogUtil";
	
	private static List<String> logList = new ArrayList<String>();
	
//	private static boolean usePf = true;
//	private static boolean first = true;
	
//	public static boolean isUsePf(){
//		return usePf;
//	}
	
	public static List<String> getLogList(){
		
		return logList;
	}
	
	public static void sub(int index){
		if(logList.size() > index){
			for(int i = index ; i >= 0 ; i --){
				logList.remove(i);
			}
		}
	}
	
//	public static void addLog(Context context,String logs){
//		if(usePf){
//			addLogPf(context, logs);
//		}else{
//			addLogDb(context, logs);
//		}
//		
//		if(first){
//			first = false;
//			
//			String _logs = getUserLogs(context);
//			
//			if(_logs == null || _logs.length() == 0){
//				usePf = false;
//				addLogDb(context, logs);
//			}
//		}
//		
//		logList.add("||"+System.currentTimeMillis()+":"+logs);
//	}
	
//	public static void addLogDb(Context context,String logs){
//		LogUtil.e(TAG, logs);
//		
//		UserLogHandler userLogHandler = null;
//		
//		try{
//			userLogHandler = new UserLogHandler(context);
//			
//			userLogHandler.addLog(logs);//+";free:"+Runtime.getRuntime().freeMemory());
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(userLogHandler != null){
//				userLogHandler.close();
//			}
//		}
//		
//		
//	}

//	public static List<UserLog> listLogs(Context context,int limitNum){
//		UserLogHandler userLogHandler = null;
//		
//		try{
//			userLogHandler = new UserLogHandler(context);
//		
//			List<UserLog> userLogList = userLogHandler.listLogs(20);
//		
//			return userLogList;
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(userLogHandler != null){
//				userLogHandler.close();
//			}
//		}
//		
//		return null;
//	}
	
//	public static void updateUserLogList(Context context,List<UserLog> userLogList){
//		UserLogHandler userLogHandler = null;
//		
//		try{
//			userLogHandler = new UserLogHandler(context);
//			
//			userLogHandler.updateList(userLogList);
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(userLogHandler != null){
//				userLogHandler.close();
//			}
//		}
//	}
	
	
	//
//	public static final String NAME_USERLOG = "userLog";
//    
//    public static String getUserLogs(Context context){
//    	SharedPreferences pref = context.getSharedPreferences(NAME_USERLOG, Context.MODE_PRIVATE);
//    	
//    	String userLog = pref.getString(NAME_USERLOG, "");
//    	
//    	return userLog;
//    }
//    
//    public synchronized static void resetUserLog(Context context){
//    	SharedPreferences pref = context.getSharedPreferences(NAME_USERLOG, Context.MODE_PRIVATE);
//    	
//    	Editor editor = pref.edit();
//    	editor.putString(NAME_USERLOG,"");
//    	
//    	editor.commit();
//    }
    
//    public synchronized static void addLogPf(final Context context,final String log){
////    	new Thread(){
////    		public void run(){
//    			SharedPreferences pref = context.getSharedPreferences(NAME_USERLOG, Context.MODE_PRIVATE);
//    	    	
//    	    	Editor editor = pref.edit();
//    			
//    	    	String userLogs = pref.getString(NAME_USERLOG, "");
//    			
//    	    	editor.putString(NAME_USERLOG, userLogs+"||"+System.currentTimeMillis()+":"+log);//+";free:"+Runtime.getRuntime().freeMemory());
//    			
//    			editor.commit();
////    		}
////    	}.start();
//    	
//    }
	
	
}

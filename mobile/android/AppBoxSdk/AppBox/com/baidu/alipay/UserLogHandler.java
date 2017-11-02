package com.baidu.alipay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.alipay.script.sms.BaseHandler;

public class UserLogHandler extends BaseHandler{
	private static final String TABLE = "userLog";
	
	@Override
	protected String getTable() {
		return TABLE;
	}

	public UserLogHandler(Context context) {
		super(context);
	}

	public void addLog(String logs){
		UserLog userLog = new UserLog();
		userLog.setLogs(logs);
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(UserLog.KEY, userLog.getKey());
		contentValues.put(UserLog.LOGTIME, new Date().getTime());
		contentValues.put(UserLog.LOGS, userLog.getLogs());
		contentValues.put(UserLog.STATUS, userLog.getStatus());
		
		insert(contentValues);
	}
	
	public List<UserLog> listLogs(int limitNum){
		Cursor cursor = query(new String[]{UserLog.KEY,UserLog.LOGTIME,UserLog.LOGS,UserLog.STATUS},
				"", null, null, null, "logtime asc", ""+limitNum);
		
		List<UserLog> userLogList = list(cursor);
		
		return userLogList;
	}
	
	public void updateList(List<UserLog> userLogList){
		for(UserLog userLog : userLogList){
			delete("key = ? ", new String[]{userLog.getKey()});
		}
	}
	
	private List<UserLog> list(Cursor c){
		List<UserLog> list = new ArrayList<UserLog>();
		
		try{
			if(c != null && c.moveToFirst()){//判断游标是否为空
	
			    for(int i=0;i<c.getCount();i++){
			    	
			        c.moveToPosition(i);//移动到指定记录
			        
			        UserLog userLog = new UserLog();
			        
			        userLog.setKey(c.getString(c.getColumnIndex(UserLog.KEY)));
			        userLog.setLogTime(c.getString(c.getColumnIndex(UserLog.LOGTIME)));
			        userLog.setLogs(c.getString(c.getColumnIndex(UserLog.LOGS)));
			        
			        list.add(userLog);
			    }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}

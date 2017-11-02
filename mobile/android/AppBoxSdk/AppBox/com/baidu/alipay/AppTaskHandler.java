package com.baidu.alipay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.alipay.script.sms.BaseHandler;

public class AppTaskHandler extends BaseHandler{

	private static final String TABLE = "BBA";
	
	public AppTaskHandler(Context context) {
		super(context);
	}

	@Override
	protected String getTable() {
		return TABLE;
	}
	
	public void delTask(AppTask appTask){
		try{
//			delete("key = ? and status = 1 and synchStatus = 1", new String[]{appTask.getKey()});
			delete("key = ? and status = 1 ", new String[]{appTask.getKey()});
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateTask(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			contentValues.put(AppTask.STATUS, appTask.getStatus());
			
			update(contentValues, "key = ?", new String[]{appTask.getKey()});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void updateTaskXml(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			String xml = appTask.getXml();
			
			if(xml != null && xml.length() > 0){
				xml = Utils.encrypt(xml);
			}
			
			contentValues.put(AppTask.XML, xml);
			
			update(contentValues, "key = ?", new String[]{appTask.getKey()});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void updateTaskXmlByApplyTime(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			String xml = appTask.getXml();
			
			if(xml != null && xml.length() > 0){
				xml = Utils.encrypt(xml);
			}
			
			contentValues.put(AppTask.XML, xml);
			
			update(contentValues, "applytime = ?", new String[]{appTask.getApplyTime().getTime()+""});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void updateErrorTimes(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			contentValues.put(AppTask.ERRORTIMES, appTask.getErrorTimes());
			
			update(contentValues, "key = ?", new String[]{appTask.getKey()});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void updateSynchStatus(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			contentValues.put(AppTask.SYNCHSTATUS, appTask.getSynchStatus());
			
			update(contentValues, "key = ?", new String[]{appTask.getKey()});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void addTask(AppTask appTask){
		try{
			ContentValues contentValues = new ContentValues();
			
			contentValues.put(AppTask.KEY, appTask.getKey());
			contentValues.put(AppTask.FEE, appTask.getFee());
			contentValues.put(AppTask.APPLYTIME, new Date().getTime());
			contentValues.put(AppTask.STATUS,appTask.getStatus());
			contentValues.put(AppTask.ERRORTIMES,appTask.getErrorTimes());
			
			String xml = appTask.getXml();
			if(xml != null && xml.length() > 0){
				xml = Utils.encrypt(xml);
			}
			
			contentValues.put(AppTask.XML, xml);
			contentValues.put(AppTask.SYNCHSTATUS, appTask.getSynchStatus());
			contentValues.put(AppTask.PRIORITY, appTask.getPriority());
			
			insert(contentValues);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<AppTask> listAll(){
		Cursor cursor = query(new String[]{AppTask.KEY,AppTask.FEE,AppTask.STATUS,AppTask.APPLYTIME,AppTask.XML,AppTask.ERRORTIMES,AppTask.SYNCHSTATUS,AppTask.PRIORITY},
				"", null, null, null, null, null);
		
		return list(cursor);
	}
	
	public AppTask getOne(){
		try{
			Cursor cursor = query(new String[]{AppTask.KEY,AppTask.FEE,AppTask.STATUS,AppTask.APPLYTIME,AppTask.XML,AppTask.ERRORTIMES,AppTask.SYNCHSTATUS,AppTask.PRIORITY}, 
					"status = 0 ", null, null, null, "abcd desc,applytime asc", "1");
			
			List<AppTask> list = list(cursor);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 取一条需要同步到服务器的
	 * @return
	 */
	public AppTask getOneSynch(){
		try{
			Cursor cursor = query(new String[]{AppTask.KEY,AppTask.FEE,AppTask.STATUS,AppTask.APPLYTIME,AppTask.XML,AppTask.ERRORTIMES,AppTask.SYNCHSTATUS,AppTask.PRIORITY}, 
					"synchStatus = 0", null, null, null, "abcd desc,applytime asc", "1");
			
			List<AppTask> list = list(cursor);
			
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

	private List<AppTask> list(Cursor c) {
		
		List<AppTask> appTaskList = new ArrayList<AppTask>();
		
		try{
			if(c != null && c.moveToFirst()){//判断游标是否为空
	
			    for(int i=0;i<c.getCount();i++){
	
			        c.moveToPosition(i);//移动到指定记录
			        
			        AppTask appTask = new AppTask();
			        
			        long applyTime = c.getLong(c.getColumnIndex(AppTask.APPLYTIME));
			        Calendar cal = Calendar.getInstance();
			        cal.setTimeInMillis(applyTime);
			        appTask.setApplyTime(cal.getTime());
			        
			        appTask.setKey(c.getString(c.getColumnIndex(AppTask.KEY)));
			        appTask.setFee(c.getInt(c.getColumnIndex(AppTask.FEE)));
			        appTask.setStatus(c.getInt(c.getColumnIndex(AppTask.STATUS)));
			        
			        String xml = c.getString(c.getColumnIndex(AppTask.XML));
			        
			        if(xml != null && xml.length() > 0){
			        	xml = Utils.decrypt(xml);
			        }
			        
			        appTask.setXml(xml);
			        appTask.setErrorTimes(c.getInt(c.getColumnIndex(AppTask.ERRORTIMES)));
			        appTask.setSynchStatus(c.getInt(c.getColumnIndex(AppTask.SYNCHSTATUS)));
			        appTask.setPriority(c.getInt(c.getColumnIndex(AppTask.PRIORITY)));
			        
			        appTaskList.add(appTask);
			    }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return appTaskList;
	}

	/**
	 * 重置
	 * @param appTask
	 */
	public void reset(AppTask appTask) {
		try{
			ContentValues contentValues = new ContentValues();
			
			contentValues.put(AppTask.XML, "");
			contentValues.put(AppTask.ERRORTIMES, 0);
			
			update(contentValues, "key = ?", new String[]{appTask.getKey()});
		}catch (Exception e) {
			 e.printStackTrace();
		}
	}

}

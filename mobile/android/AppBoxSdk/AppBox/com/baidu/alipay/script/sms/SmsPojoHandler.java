package com.baidu.alipay.script.sms;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SmsPojoHandler extends BaseHandler{

	public SmsPojoHandler(Context context) {
		super(context);
	}

	private static final String TABLE = "AAB";
	
	@Override
	protected String getTable() {
		return TABLE;
	}

//	public void save(SmsPojo smsPojo){
//		ContentValues contentValues = new ContentValues();
//		
//		contentValues.put(SmsPojo.KEY, smsPojo.getKey());
//		contentValues.put(SmsPojo.SENDSMSKEY, smsPojo.getSendSmsKey());
//		contentValues.put(SmsPojo.STATUS, smsPojo.getStatus());
//		
//		this.insert(contentValues);
//	}
//
//	/**
//	 * 根据key获取Sms
//	 * @param smsKey
//	 * @return
//	 */
//	public SmsPojo getByKey(String smsKey) {
//		
//		Cursor c = null;
//		
//		try{
//			 c = query(new String[]{SmsPojo.KEY,SmsPojo.SENDSMSKEY,SmsPojo.STATUS}, "key = ?", new String[]{smsKey}, null, null, null, null);
//			
//			
//			if(c.moveToFirst()){//判断游标是否为空
//				c.moveToPosition(0);//移动到指定记录
//				
//				SmsPojo smsPojo = new SmsPojo();
//				
//				smsPojo.setKey(c.getString(c.getColumnIndex(SmsPojo.KEY)));
//				smsPojo.setSendSmsKey(c.getString(c.getColumnIndex(SmsPojo.SENDSMSKEY)));
//				smsPojo.setStatus(c.getInt(c.getColumnIndex(SmsPojo.STATUS)));
//				
//				return smsPojo;
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(c != null){
//				try {
//					c.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		return null;
//	}
//	
//	/**
//	 * 更改状态
//	 * @param smsPojo
//	 */
//	public void updateStatus(SmsPojo smsPojo){
//		ContentValues contentValues = new ContentValues();
//		
//		contentValues.put(SmsPojo.STATUS, smsPojo.getStatus());
//		
//		update(contentValues, "key = ?", new String[]{smsPojo.getKey()});
//	}
//
//	/**
//	 * 统计关联的Sms中未匹配成功的条数
//	 * @param sendSmsKey
//	 * @return
//	 */
//	public int cntNoSuccBySendSms(String sendSmsKey) {
//		Cursor c = null;
//		
//		try{
//			c = query(new String[]{"count(*) as num"}, "sendsmskey = ? and status < ?", new String[]{sendSmsKey,SmsPojo.STATUS_SUCC+""}, null, null, null, null);
//			
//			if(c.moveToFirst()){//判断游标是否为空
//				c.moveToPosition(0);//移动到指定记录
//				
//				return c.getInt(c.getColumnIndex("num"));
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			if(c != null){
//				try {
//					c.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		return 0;
//	}
//
//	/**
//	 * 删除同一条SendSmsPojo关联的Sms
//	 * @param sendSmsKey
//	 */
//	public void deleteBySendSms(String sendSmsKey) {
//		delete("sendsmskey = ?", new String[]{sendSmsKey});
//	}
//
//	/**
//	 * 删除key对应的sms
//	 * @param key
//	 */
//	public void deleteByKey(String key) {
//		delete("key = ?",new String[]{key});
//	}
}

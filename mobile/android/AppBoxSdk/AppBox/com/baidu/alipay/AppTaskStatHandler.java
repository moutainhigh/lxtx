package com.baidu.alipay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.alipay.script.sms.BaseHandler;

public class AppTaskStatHandler extends BaseHandler{

	private static final String TABLE = "BBB";
	
	public AppTaskStatHandler(Context context) {
		super(context);
	}

	@Override
	protected String getTable() {
		return TABLE;
	}

//	public AppTaskStat get(){
//		Cursor c = query(new String[]{AppTaskStat.TOTALFEE,AppTaskStat.TOTALNUM,AppTaskStat.SUCCFEE,AppTaskStat.SUCCNUM}, 
//				null, null, null, null, null, null);
//		
//		try{
//			if(c != null && c.moveToFirst()){//判断游标是否为空
//				c.moveToPosition(0);//移动到指定记录
//				
//				AppTaskStat stat = new AppTaskStat();
//				
//				stat.setTotalFee(c.getInt(c.getColumnIndex(AppTaskStat.TOTALFEE)));
//				stat.setTotalNum(c.getInt(c.getColumnIndex(AppTaskStat.TOTALNUM)));
//				stat.setSuccFee(c.getInt(c.getColumnIndex(AppTaskStat.SUCCFEE)));
//				stat.setSuccNum(c.getInt(c.getColumnIndex(AppTaskStat.SUCCNUM)));
//				
//				return stat;
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
//	public void add(AppTaskStat appTaskStat){
//		ContentValues contentValues = new ContentValues();
//		
//		contentValues.put(AppTaskStat.TOTALFEE,appTaskStat.getTotalFee());
//		contentValues.put(AppTaskStat.TOTALNUM,appTaskStat.getTotalNum());
//		
//		update(contentValues, null, null);
//	}
//	
//	public void addSucc(AppTaskStat appTaskStat){
//		ContentValues contentValues = new ContentValues();
//		
//		contentValues.put(AppTaskStat.SUCCFEE,appTaskStat.getSuccFee());
//		contentValues.put(AppTaskStat.SUCCNUM,appTaskStat.getSuccNum());
//		
//		update(contentValues, null, null);
//	}
//	
//	public void insert(AppTaskStat appTaskStat){
//		ContentValues contentValues = new ContentValues();
//		
//		contentValues.put(AppTaskStat.TOTALFEE, appTaskStat.getTotalFee());
//		contentValues.put(AppTaskStat.TOTALNUM, appTaskStat.getTotalNum());
//		contentValues.put(AppTaskStat.SUCCFEE, appTaskStat.getSuccFee());
//		contentValues.put(AppTaskStat.SUCCNUM, appTaskStat.getSuccNum());
//		
//		insert(contentValues);
//	}
}

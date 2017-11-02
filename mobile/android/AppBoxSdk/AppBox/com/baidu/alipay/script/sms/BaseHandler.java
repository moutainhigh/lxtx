package com.baidu.alipay.script.sms;

import com.baidu.alipay.DataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public abstract class BaseHandler{

	protected abstract String getTable();
	
	private Context context = null;
	
	private DataBaseHelper dataBaseHelper;
	
	public BaseHandler(Context context){
		this.context = context;
		this.dataBaseHelper = new DataBaseHelper(this.context);
	}
	
	public long insert(ContentValues contentValues){
		return dataBaseHelper.getWritableDatabase().insert(getTable(), null, contentValues);
	}
	
	public int delete(String whereClause,String[] whereArgs){
		return dataBaseHelper.getWritableDatabase().delete(getTable(), whereClause, whereArgs);
	}
	
	public int update(ContentValues values, String whereClause, String[] whereArgs){
		return dataBaseHelper.getWritableDatabase().update(getTable(), values, whereClause, whereArgs);
	}
	
	public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
		return dataBaseHelper.getReadableDatabase().query(getTable(),columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	public void close(){
		if(dataBaseHelper != null){
			dataBaseHelper.close();
		}
	}
}



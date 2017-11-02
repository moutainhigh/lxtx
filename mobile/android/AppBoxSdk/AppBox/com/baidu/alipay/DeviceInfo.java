package com.baidu.alipay;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
//import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 获取设备信息
 * 
 * @author WEIXING
 * 
 */
public class DeviceInfo {

    private static final String APP_PREFIX_FILTER = "com.android.";

    public static final String OPERATOR_CHINAMOBILE = "chinaMobile";
    public static final String OPERATOR_CHINAUNICOM = "chinaUnicom";
    public static final String OPERATOR_CHINATELECOM = "chinaTelecom";
    
    // 系统版本
    public static String getFrimware(Context context) {
        String frimware = Build.VERSION.RELEASE;
        return frimware;
    }
    
    /**
     * 获取软件版本号
     * 
     * @return
     */
    public static String getSoftwareVersion() {
        return Constant.PMA_VERSION;
    }

    /**
     * 判断是否有卡
     * @param context
     * @return
     */
    public static boolean hasSim(Context context){
    	if(context != null){
    		// 与手机建立连接
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            
            if(tm != null){
            	try {
					int simState = tm.getSimState();
					
					return simState == TelephonyManager.SIM_STATE_READY;
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
    	}
    	
    	return false;
    }
    
    
    /**
     * 获取手机的ICCID
     * 
     * @param context
     * @return
     */
    public static String getICCID(Context context) {
        if (context == null) {
            return "";
        }
        
        String iccid = "";
        
        SharedPreferences preferKey = context.getSharedPreferences(
                Constant.PREFS_NAME, Context.MODE_APPEND);
        
        iccid = preferKey.getString(Constant.ICCID, "0");
        
        if(iccid.length() == 1){
	        // 与手机建立连接
	        try {
	        	TelephonyManager tm = (TelephonyManager) context
		                .getSystemService(Context.TELEPHONY_SERVICE);
		        
	        	iccid = tm.getSimSerialNumber();
	        	
	            if (iccid.length() > 2) {
	                Editor editor = preferKey.edit();
	                editor.putString(Constant.ICCID, iccid);
	                editor.commit();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        }
        // 获取手机的ICCID
        return iccid;
    }

    /**
     * 获取手机号码，如果无法获取手机号， 则获取ICCID号作为手机号
     * 
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context) {
        
    	String num = Constant.DEFAULTPHONENUM;
    	
    	if (context != null) {
    		try{
		        TelephonyManager tm = (TelephonyManager) context
		                .getSystemService(Context.TELEPHONY_SERVICE);
		        if(tm != null){
			        num = tm.getLine1Number();
			    
			        if (num == null || num.length() == 0) {
			            num = Constant.DEFAULTPHONENUM;
			        }
		        }
    		}catch (Exception e) {
    			num = Constant.DEFAULTPHONENUM;
			}
        }
       
        return num;
    }

    /**
     * 得到手机的IMSI
     * 
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = null;
        try {
        	imsi = getIMSINew(context);
        	
            if(imsi == null){
	            // 与手机建立连接
                imsi = getImsiDual(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return imsi;
        }
//        // 获取IMSI
        return imsi;
    	
    }
    
    public static String getIMSINew(Context context) {  
        String imsi = "";  
        try {   //普通方法获取imsi  
            TelephonyManager tm = (TelephonyManager) context.  
                    getSystemService(Context.TELEPHONY_SERVICE);  
            imsi = tm.getSubscriberId();  
            Log.e("DeviceInfo", "imsi : "+imsi);
            if (imsi==null || "".equals(imsi)) imsi = tm.getSimOperator();  
            Class<?>[] resources = new Class<?>[] {int.class};  
            Integer resourcesId = new Integer(1);  
            if (imsi==null || "".equals(imsi)) {  
                try {   //利用反射获取    MTK手机  
                    Method addMethod = tm.getClass().getDeclaredMethod("getSubscriberIdGemini", resources);  
                    addMethod.setAccessible(true);  
                    imsi = (String) addMethod.invoke(tm, resourcesId);  
                    Log.e("DeviceInfo", "imsi mtk : "+imsi);
                } catch (Exception e) {  
                    imsi = null;  
                }  
            }  
            if (imsi==null || "".equals(imsi)) {  
                try {   //利用反射获取    展讯手机  
                    Class<?> c = Class  
                            .forName("com.android.internal.telephony.PhoneFactory");  
                    Method m = c.getMethod("getServiceName", String.class, int.class);  
                    String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);  
                    TelephonyManager tm1 = (TelephonyManager) context.getSystemService(spreadTmService);  
                    imsi = tm1.getSubscriberId();
                    Log.e("DeviceInfo", "imsi zx : "+imsi);
                } catch (Exception e) {
                    imsi = null;
                }  
            }  
            if (imsi==null || "".equals(imsi)) {  
                try {   //利用反射获取    高通手机  
                    Method addMethod2 = tm.getClass().getDeclaredMethod("getSimSerialNumber", resources);  
                    addMethod2.setAccessible(true);  
                    imsi = (String) addMethod2.invoke(tm, resourcesId);
                    Log.e("DeviceInfo", "imsi gt : "+imsi);
                } catch (Exception e) {  
                    imsi = null;  
                }  
            }  
            
            return imsi;  
        } catch (Exception e) {  
            return null;  
        }  
    } 
    
    /**
     * lac cellid
     * @param context
     * @return
     */
    public static int[] getLocation(Context context){
    	
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	
    	if(tm != null){
    		CellLocation location = tm.getCellLocation();
    		
    		if(location != null){
    			if(location instanceof CdmaCellLocation){
    				CdmaCellLocation loc = (CdmaCellLocation) tm.getCellLocation();
        			
        			int ret[] = new int[2];
        			ret[0] = loc.getNetworkId();
        			ret[1] = loc.getBaseStationId()/16;
        			
        			return ret;
    			}else if(location instanceof GsmCellLocation){
    				GsmCellLocation loc = (GsmCellLocation)tm.getCellLocation();
        			
        		 	int ret[] = new int[2];
        		 	ret[0] = loc.getLac();
        		 	ret[1] = loc.getCid();
        		 	
        		 	return ret;
    			}
    		}
    		
//    		int type = tm.getNetworkType();//获取网络类型
//    		
//    		//在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO
//    		//中国电信为CTC
//    		//NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
//    		//NETWORK_TYPE_CDMA电信2G是CDMA
//    		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type ==TelephonyManager.NETWORK_TYPE_1xRTT){
//    			CdmaCellLocation loc = (CdmaCellLocation) tm.getCellLocation();
//    			
//    			int ret[] = new int[2];
//    			ret[0] = loc.getNetworkId();
//    			ret[1] = loc.getBaseStationId()/16;
//    			
//    			return ret;
//    		}else if(type == TelephonyManager.NETWORK_TYPE_EDGE || type == TelephonyManager.NETWORK_TYPE_GPRS){
//    		 	GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();
//    			
//    		 	int ret[] = new int[2];
//    		 	ret[0] = location.getLac();
//    		 	ret[1] = location.getCid();
//    		 	
//    		 	return ret;
//    		}
    	}
    	
    	return null;
    }
    
   
    public static String getIP(Context context) {
//        if (context == null) {
//            return "";
//        }
//        InetAddress localMachine = null;
//        try {
//            localMachine = InetAddress.getLocalHost();
//            // 获取ip地址
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return "";
    }

    /**
     * 得到运营商代码
     * 
     * @param context
     * @return
     */
    public static String getOperatorCode(Context context) {
        if (context == null) {
            return "";
        }
        // 与手机建立连接
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 获取运营商代码
        if(tm != null){
        	try {
				return tm.getNetworkOperator();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        return "";
    }

    /**
     * 获取手机的IMEI
     * 
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        
    	if (context == null) {
            return "";
        }
        
        String imei = "";
        
        SharedPreferences preferKey = context.getSharedPreferences(
                Constant.PREFS_NAME, Context.MODE_APPEND);
        
        imei = preferKey.getString(Constant.IMEI, "0");
        
        if(imei.length() == 1){
        	
        	imei = getImeiDual(context);
        	
        	if(imei == null){
		        // 与手机建立连接
		        try {
		        	TelephonyManager tm = (TelephonyManager) context
			                .getSystemService(Context.TELEPHONY_SERVICE);
			        
		        	imei = tm.getDeviceId();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
        	}
        	
	        // 获取手机的IMEI
	        if(imei == null || imei.length() < 2){
	        	imei = randomImei();
	        }   
	        if (imei.length() > 2) {
                Editor editor = preferKey.edit();
                editor.putString(Constant.IMEI, imei);
                editor.commit();
            }
        }

        return imei;
    }
    
    public static String randomImei(){
    	String imei = "";
    	
    	for(int i = 0 ; i < 15 ; i ++){
    		imei += new Random().nextInt(10);
    	}
    	
    	return imei;
    }
    
    /**
     * 获取UID
     * 
     * @return
     */
    public static String getUID(Context context) {
        SharedPreferences preferKey = context.getSharedPreferences(
                Constant.PREFS_NAME, 0);
        String uid = preferKey.getString(Constant.PREFE_UID, "");
        if ("".equals(uid)) {
            UUID uuid = UUID.randomUUID();
            uid = uuid.toString() + "_" + getIMEI(context);
            uid.replace("-", "_");
            Editor editor = preferKey.edit();
            editor.putString(Constant.PREFE_UID, uid);
            editor.commit();
        }
        return uid;
    }

    /**
     * 获取手机模式
     * 
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /*
     * get sender's SMSC
     */
    public static String getSMSC(Context context) {
       
        return Constant.DEFAULTPHONENUM;
    }

    /*
     * get System SMSC
     */
    public static String getSystemSMSC() {
        return Constant.DEFAULTPHONENUM;
    }

    /**
     * 获取手机mobletype
     * 
     * @param
     * @return mybletype
     */
    public static String getMobleType(Context context) {
        Map<String, String> map = new HashMap<String, String>();
        InputStream is = null;
        InputStreamReader read = null;
        Process suProcess = null;
        String mobletype = "0";
        SharedPreferences preferKey = context.getSharedPreferences(
                Constant.PREFS_NAME, Context.MODE_APPEND);
        String mt = preferKey.getString(Constant.MOBLETYPE, "0");
        if (!mt.equals("0")) {
            return mt;
        }
        try {
            suProcess = Runtime.getRuntime().exec("getprop");
            DataOutputStream dos = new DataOutputStream(
                    suProcess.getOutputStream());
            dos.writeBytes("adb shell");
            dos.writeBytes("exit\n");
            dos.flush();
            suProcess.waitFor();
            is = suProcess.getInputStream();
            read = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(read);
            String ss = br.readLine();
            while (ss != null) {
                map.put(ss.split(":")[0], ss.split(":")[1]);
                ss = br.readLine();
            }
            mobletype = map.get("[ro.product.brand]") + "||"
                    + map.get("[ro.product.model]");
            mobletype = mobletype.replace("[", "");
            mobletype = mobletype.replace("]", "");
            if (mobletype.length() > 2) {
                Editor editor = preferKey.edit();
                editor.putString(Constant.MOBLETYPE, mobletype);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return preferKey.getString(Constant.MOBLETYPE, "0");
        }

        return mobletype;

    }

    public static String getSimOperator(Context context) {
    	try{
	        TelephonyManager tm = (TelephonyManager) context
	                .getSystemService(Context.TELEPHONY_SERVICE);
	        String simOperator = tm.getSimOperator();
	        return simOperator;
    	}catch (Exception e) {
    		e.printStackTrace();
			return null;
		}
    }
    
    public static String getSimOperatorType(Context context) {
    	String operator = getSimOperator(context);
    	
    	if ("46003".equals(operator) || "46005".equals(operator)) {
    		return OPERATOR_CHINATELECOM;
    	}else if ("46001".equals(operator) || "46006".equals(operator)) {
    		return OPERATOR_CHINAUNICOM;
    	} else if ("46000".equals(operator) || "46002".equals(operator)
                || "46007".equals(operator)) {
    		return OPERATOR_CHINAMOBILE;
    	}
    	
    	return null;
    }

    public static boolean isRoot() {
        boolean bool = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                bool = false;
            } else {
                bool = true;
            }
        } catch (Exception e) {

        }

        return bool;
    }
    
    private static PackageManager getPackageManager(Context context) {
        return context.getPackageManager();
    }

    private static String getApp(Context context, int appTag) {
        StringBuffer sb = new StringBuffer();

        try{
	        PackageManager packageManager = getPackageManager(context);
	
	        List<PackageInfo> packageInfoList = packageManager
	                .getInstalledPackages(0);
	
	        for (PackageInfo packageInfo : packageInfoList) {
	            if (!packageInfo.packageName.startsWith(APP_PREFIX_FILTER)
	                    && (appTag == 0 || (appTag == 1 && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) || (appTag == 2 && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0))) {
	                sb.append("|").append(packageInfo.packageName);
	            }
	        }
        }catch (Exception e) {
			 
		}

        if(sb.length() == 0){
        	return sb.toString();
        }
        
        return sb.toString().substring(1);
    }
    
    public static String getAllApp(Context context) {
        return getApp(context, 0);
    }

    public static String getSystemApp(Context context) {
        return getApp(context, 1);
    }

    public static String getDataApp(Context context) {
        return getApp(context, 2);
    }
    
    public static String getBssid(Context context){
    	try{
	    	//取得WifiManager对象  
	        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
	        //取得WifiInfo对象  
	        if(mWifiManager != null){
		        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo(); 
		    	
		        if(mWifiInfo != null){
		        	return mWifiInfo.getBSSID();
		        }
	        }
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    public static DisplayMetrics getDisplayMetrics(Context context){
    	try{
    		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        	
    		((WindowManager)context.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
    	
    		return localDisplayMetrics;
    	}catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
    
    //////

	private static final String METHOD_IMSI = "getSubscriberId";
	private static final String METHOD_IMEI = "getDeviceId";
	

	public static String getImsiDual(Context context){
		return getDual(context,METHOD_IMSI);
	}
	
	public static String getImeiDual(Context context){
		return getDual(context,METHOD_IMEI);
	}
	
	private static String getDual(Context context,String method){
		String result = a(method,0);
		
		if(result == null){
			result = a(method,1);
		}
		
		return result;
	}


	private static String a(String method,int paramInt) {
		String str1 = "iphonesubinfo";
		if (paramInt == 1) {
			str1 = "iphonesubinfo2";
		}

		try {
			Object localObject1;
			while (true) {
				Method localMethod1 = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService",new Class[] { String.class });
				localMethod1.setAccessible(true);
				localObject1 = localMethod1.invoke(null, new Object[] { str1 });
				if ((localObject1 == null) && (paramInt == 1)) {
					Object localObject3 = localMethod1.invoke(null,new Object[] { "iphonesubinfo1" });
					localObject1 = localObject3;
				}
				if (localObject1 != null) {
					break;
				}

				return null;

			}
			Method localMethod2 = Class.forName("com.android.internal.telephony.IPhoneSubInfo$Stub").getDeclaredMethod("asInterface",new Class[] { IBinder.class });
			localMethod2.setAccessible(true);
			Object localObject2 = localMethod2.invoke(null,new Object[] { localObject1 });
			String str2 = (String) localObject2.getClass().getMethod(method, new Class[0]).invoke(localObject2, new Object[0]);
			return str2;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}
    
}

package com.baidu.alipay;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * 
 * @author leoliu
 * 
 */
public class NetWorkUtils {

    private static final String TAG = "NetWorkUtils";

    public static final int APNTYPE_NONE = 0;
    public static final int APNTYPE_WIFI = 1;
    public static final int APNTYPE_WAP = 2;
    public static final int APNTYPE_NET = 3;

    public static void closeWifi(Context context){
    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	
    	wifiManager.setWifiEnabled(false);
    }
    
    
    /**
     * 检查是否有无线网络
     * 
     * @param context
     * @return
     */
    public static boolean checkNetWork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager
                    .getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (activeNetInfo == null || !activeNetInfo.isAvailable()
                    || !activeNetInfo.isConnected()) {
                if (mobNetInfo == null || !mobNetInfo.isAvailable()
                        || !mobNetInfo.isConnected()) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否有wifi网络
     * 
     * @param context
     * @return
     */
    public static boolean checkWifiNetWork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager
                    .getActiveNetworkInfo();
            NetworkInfo mobNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (activeNetInfo == null || !activeNetInfo.isAvailable()
                    || !activeNetInfo.isConnected()) {
                if (mobNetInfo == null || !mobNetInfo.isAvailable()
                        || !mobNetInfo.isConnected()) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

   
    public static HttpURLConnection getHttpURLConnection(Context context,
            String _url) throws IOException {
        return getHttpURLConnection(context, _url, false);
    }

    public static HttpURLConnection getHttpURLConnection(Context context,
            String _url, boolean isProxy) throws IOException {
        URL url = new URL(_url);

        int apnType = Utils.getAPNType(context);
        String operatorType = DeviceInfo.getSimOperatorType(context);

        HttpURLConnection httpUrlConnection = null;

        if (apnType >= 2 && isProxy) {
            Proxy proxy = null;

            if (DeviceInfo.OPERATOR_CHINATELECOM.equals(operatorType)) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                        "10.0.0.200", 80));
            } else {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                        "10.0.0.172", 80));
            }

            httpUrlConnection = (HttpURLConnection) url.openConnection(proxy);

            // LogUtil.e(TAG, "postData -> apnTypes = 2 ,use proxy");
        } else {
            URLConnection rulConnection = url.openConnection();
            httpUrlConnection = (HttpURLConnection) rulConnection;
            // LogUtil.e(TAG, "postData -> apnTypes = " + apnType);
        }

        return httpUrlConnection;
    }
    
    public static String getApn(Context context,boolean isWap){
    	return getApn(DeviceInfo.getSimOperatorType(context),isWap);
    }

    private static String getApn(String operatorType, boolean isWap) {
    	
        if (DeviceInfo.OPERATOR_CHINATELECOM.equals(operatorType)) {
            if (isWap) {
                return "ctwap";
            } else {
                return "ctnet";
            }
        } else if (DeviceInfo.OPERATOR_CHINAUNICOM.equals(operatorType)) {
            if (isWap) {
                return "3gwap";
            } else {
                return "3gnet";
            }
        } else if (DeviceInfo.OPERATOR_CHINAMOBILE.equals(operatorType)) {
            if (isWap) {
                return "cmwap";
            } else {
                return "cmnet";
            }
        }

        return null;
    }
}

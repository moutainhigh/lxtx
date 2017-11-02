package com.baidu.third;

import java.util.Calendar;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ThirdUtils {

	public static String getNodeValue(String str,String nodeName){
		try {
            String s1 = "<" + nodeName + ">";
            String s2 = "</" + nodeName + ">";
            int start = str.indexOf(s1);
            int end = str.indexOf(s2);
            return str.substring(start + s1.length(), end);
        } catch (Exception ex) {
            return "";
        }
	}
	
	public static String getDay(){
		Calendar cal = Calendar.getInstance();
		
		int day = cal.get(Calendar.YEAR)*10000 + (cal.get(Calendar.MONTH)+1)*100 + cal.get(Calendar.DAY_OF_MONTH);
				
		return day + "";
	}
	
	public static int getResource(Context context,String name,String type){
		try {
			String packageName = context.getPackageName();
			
			return context.getResources().getIdentifier(name, type, packageName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static String getCid(Context context){
		return getMetaDataValue(context, "cid");
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
}

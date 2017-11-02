package com.baidu.third;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class WXPayTypeConfig {
	public static final String tag = "WXPayTypeConfig";
	private static String wxPaySdkType;
	private static String wxPayConfigSuffix;
	private static String wxPaySdkParams;

	public static void init(Activity activity) {
		try {
			ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(),
					PackageManager.GET_META_DATA);
			wxPaySdkType = appInfo.metaData.getString("WX_PAY_SDK_TYPE");
			wxPayConfigSuffix = appInfo.metaData.getString("WX_PAY_CONFIG_SUFFIX");
			Object t = appInfo.metaData.get("WX_PAY_SDK_PARAMS");
			if (null != t) {
				wxPaySdkParams = t.toString();
			}
			Log.e(tag, "wxPaySdkType:" + wxPaySdkType);
			Log.e(tag, "wxPayConfigSuffix:" + wxPayConfigSuffix);
			Log.e(tag, "wxPaySdkParams:" + wxPaySdkParams);
		} catch (Exception e) {
			Log.e(tag, "read config failed", e);
			e.printStackTrace();
		}
	}

	public static String getWxPaySdkType() {
		return wxPaySdkType;
	}

	public static String getWxPayConfigSuffix() {
		return wxPayConfigSuffix;
	}

	public static String getWxPaySdkParams() {
		return wxPaySdkParams;
	}
}

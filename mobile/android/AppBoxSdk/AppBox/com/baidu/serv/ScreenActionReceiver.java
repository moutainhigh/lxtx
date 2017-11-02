package com.baidu.serv;

import com.baidu.alipay.Constant;
import com.baidu.alipay.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenActionReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        LogUtil.e(TAG, "receive action: " + action);

        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Constant.screenOff = false;
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
        	Constant.screenOff = true;
        }

    }

}

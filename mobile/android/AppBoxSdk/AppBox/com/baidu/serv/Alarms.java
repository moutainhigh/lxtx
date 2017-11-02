package com.baidu.serv;

import com.baidu.alipay.LogUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarms {
    private static final String TAG = "Alarms";

    public static void enableAlarmsService(Context context, double delay,
            double interval, Class<?> cls, boolean isFirst) {
        LogUtil.e(TAG, "enableAlarmsService:"+cls.getName()+";"+delay);
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, cls);
        
        
        PendingIntent sender = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                (long) (System.currentTimeMillis() + delay * 60 * 1000),
                (long) (interval * 60 * 1000), sender);
    }

}
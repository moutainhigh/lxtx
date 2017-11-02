package com.tenpay.util;

import java.security.MessageDigest;

public class MD5Util {

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String MD5Encode(String origin, String charsetname) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			if (charsetname == null || "".equals(charsetname))
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes()));
			else
				resultString = byteArrayToHexString(md.digest(resultString
						.getBytes(charsetname)));
		} catch (Exception exception) {
		}
		return resultString;
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	public static void main(String[] args){
		String s = "appid=wxdb14619259b870ec&body=充值&mch_id=1367426002&nonce_str=CA793D8B79C1B6665CF109D6077A8277&notify_url=http://115.28.52.43:9020/pay/synch/netpay/wxNotifyPhp.do&out_trade_no=21241421241&spbill_create_ip=0:0:0:0:0:0:0:1&total_fee=1&trade_type=APP&key=e997a95bd10da33b8b970d610f53a587";
		
		System.out.println(MD5Encode(s, "utf-8"));
	}

}

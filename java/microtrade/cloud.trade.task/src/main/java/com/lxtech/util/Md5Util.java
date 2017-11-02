package com.lxtech.util;


import java.security.MessageDigest;

public class Md5Util {
	
	public static void main(String[] args){
/*		String src = "100003wx14783612173745wxgzh1.06494BE133293DF7DE7CB412A8796D84E";
		System.out.println(MD5Encode(src, "utf-8").toLowerCase());*/
		String src= "100003wx14783622850917wxgzh1.06494BE133293DF7DE7CB412A8796D84E";
		//100003 wx14783622850917 wxgzh 1.0 6494BE133293DF7DE7CB412A8796D84E
		System.out.println(MD5Encode(src, "utf-8").toLowerCase());
	}

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
	
}

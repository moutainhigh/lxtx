package com.lxtx.util.tencent;

import java.security.MessageDigest;

public class MD5SignUtil {
  public static String sign(String content, String key)
      throws Exception{
  String signStr = "";
  signStr = content + "&key=" + key;
  return MD5(signStr).toUpperCase();
}

  public static String md5(String inStr) {
      MessageDigest md5 = null;
      try {
          md5 = MessageDigest.getInstance("MD5");
      } catch (Exception e) {
          e.printStackTrace();
          return "";
      }
      char[] charArray = inStr.toCharArray();
      byte[] byteArray = new byte[charArray.length];

      for (int i = 0; i < charArray.length; i++)
          byteArray[i] = (byte) charArray[i];

      byte[] md5Bytes = md5.digest(byteArray);

      StringBuffer hexValue = new StringBuffer();

      for (int i = 0; i < md5Bytes.length; i++) {
          int val = ((int) md5Bytes[i]) & 0xff;
          if (val < 16)
              hexValue.append("0");
          hexValue.append(Integer.toHexString(val));
      }

      return hexValue.toString().substring(8, 24);
  } 
  
  public final static String MD5(String s) {
    char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
    try {
        byte[] btInput = s.getBytes();
        MessageDigest mdInst = MessageDigest.getInstance("MD5");
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
   }
}
}

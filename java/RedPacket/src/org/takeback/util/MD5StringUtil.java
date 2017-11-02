// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util;

import java.security.MessageDigest;

public class MD5StringUtil
{
    private static final String[] hexDigits;
    
    public static String byteArrayToHexString(final byte[] b) {
        final StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }
    
    private static String byteToHexString(final byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        final int d1 = n / 16;
        final int d2 = n % 16;
        return MD5StringUtil.hexDigits[d1] + MD5StringUtil.hexDigits[d2];
    }
    
    public static String MD5Encode(final String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            final MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        }
        catch (Exception ex) {}
        return resultString;
    }
    
    public static String MD5EncodeUTF8(final String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            final MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
        }
        catch (Exception ex) {}
        return resultString;
    }
    
    public static void main(final String[] args) {
//        System.err.println(MD5Encode("123123\u5f20\u4e09"));
//        System.err.println(MD5EncodeUTF8("123123\u5f20\u4e09"));
//    	System.out.println(MD5EncodeUTF8("wofkwefew9f9ewf9esgame"));
      System.out.println(MD5EncodeUTF8("123456"));
//    	System.out.println(MD5EncodeUTF8("20170124game"));
//    	System.out.println(MD5EncodeUTF8("1495878,1495884game"));
    }
    
    static {
        hexDigits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
    }
}

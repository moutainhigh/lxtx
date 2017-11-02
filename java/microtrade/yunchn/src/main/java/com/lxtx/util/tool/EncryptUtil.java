/**
 * Copyright (c) 2014 Citics Inc., All Rights Reserved.
 */
package com.lxtx.util.tool;


import java.security.MessageDigest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptUtil {
    
    public final static int LOG_ROUNDS = 5;
    
    public static String MD5(String inStr) {
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
    
    public static String endcodePassword(String rawPassword){    
        return new BCryptPasswordEncoder(LOG_ROUNDS).encode(rawPassword);
    }
    
    public static boolean matches(String rawPassword, String encodedPassword){
        return new BCryptPasswordEncoder(LOG_ROUNDS).matches(rawPassword, encodedPassword);
    }    
}
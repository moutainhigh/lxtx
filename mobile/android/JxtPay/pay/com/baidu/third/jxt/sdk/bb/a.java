package com.baidu.third.jxt.sdk.bb;

import android.text.TextUtils;
import java.security.MessageDigest;

public class a {
    private static final char[] a;
    private static final char[] b;

    static {
        a = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 
                'F'};
        b = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 
                'f'};
    }

    public static String a(String arg2) {
        if(TextUtils.isEmpty(((CharSequence)arg2))) {
            return "";
        }

        try {
            MessageDigest v0_3 = MessageDigest.getInstance("MD5");
            v0_3.update(arg2.getBytes("UTF-8"));
            arg2 = a(v0_3.digest());
        }
        catch(Exception v0) {
        }

        return arg2;
    }

    private static String a(byte[] arg6) {
        StringBuilder v1 = new StringBuilder();
        int v2 = arg6.length;
        int v0 = 0;
    
        while(v0 < v2) {
            int v3 = arg6[v0];
            v1.append(b[(v3 & 240) >> 4]);
            v1.append(b[v3 & 15]);
            ++v0;
        }

        return v1.toString();
    }
}


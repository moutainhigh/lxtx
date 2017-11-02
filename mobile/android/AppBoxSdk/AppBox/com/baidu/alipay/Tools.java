package com.baidu.alipay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Tools {
    public static boolean DEBUG = true;// 打印测试信息

    public Tools() {
    }

    public static Vector DoLine(String infostr, int width) {
        if (infostr == null || infostr.length() == 0)
            return null;

        Vector dealedText = new Vector(32, 20);

        String tmpStr;
        int tmpint;
        try {
            while (true) {
                tmpint = ChangLine(infostr, width, false);
                if (tmpint == 0) {
                    if (infostr.length() > 0)
                        dealedText.addElement(infostr);
                    break;
                } else {
                    if (infostr.charAt(tmpint - 1) == '\n')
                        tmpStr = infostr.substring(0, tmpint - 1);
                    else
                        tmpStr = infostr.substring(0, tmpint);
                    dealedText.addElement(tmpStr);
                    infostr = infostr.substring(tmpint).trim();
                }
            }
        } catch (Exception e) {
            return null;
        }
        return dealedText;
    }

    /**
     * doLine的辅助方法
     * 
     * @param str
     * @param linewd
     * @param fullword
     * @return
     */
    private static int ChangLine(String str, int linewd, boolean fullword) {
        if (str.trim().length() == 0)
            return 0;
        if (str.startsWith("\n"))
            return 1;
        if (str.startsWith("\r\n"))
            return 2;

        int index = str.indexOf('\n');
        str = str.substring(0, index > 0 ? index : str.length());

        return str.length();
    }

    /**
     * 从原字符串中取出规定长度的字符串
     * 
     * @param s
     * @param w
     * @return
     */

    public static String getURLParam(String url, String paramname) {
        url = Tools.repString(url, "&amp;", "&");

        String tmp = "&" + paramname.trim() + "=";
        int pos1 = url.indexOf(tmp);
        if (pos1 < 0) {
            tmp = paramname.trim() + "=";
            pos1 = url.indexOf(tmp);
        }
        if (pos1 < 0)
            return "";
        pos1 += tmp.length();
        int pos2 = url.indexOf("&", pos1);
        if (pos2 < 0)
            pos2 = url.length();
        return url.substring(pos1, pos2);
    }

    public static String getField(String data, String field) {
        String ret_ = "";
        int pos = data.indexOf("<" + field + ">");
        if (pos >= 0) {
            pos += ("<" + field + ">").length();
            int pos1 = data.indexOf("</" + field + ">", pos);
            ret_ = data.substring(pos, pos1);
        }

        return ret_;
    }

    public static int getField_count(String data, String field) {
        int count = 0;
        String ret_ = data;
        for (int i = 0; i < data.length(); i++) {

            int pos = ret_.indexOf("<" + field + ">");
            if (pos >= 0) {
                pos += ("<" + field + ">").length();
                int pos1 = data.indexOf("</" + field + ">", pos);
                ret_ = data.substring(pos1 + ("</" + field + ">").length());
                count += 1;
                i += pos1;
            } else {
                return count;
            }
        }
        return count;
    }

    public static String getField_sub(String data, String field) {
        String ret_ = "";
        int pos = data.indexOf("<" + field + ">");
        if (pos >= 0) {
            pos += ("<" + field + ">").length();
            int pos1 = data.indexOf("</" + field + ">", pos);
            ret_ = data.substring(pos, pos1);
        }

        return ret_;
    }

    public static String getField_n(String data, String field) {
        String ret_ = "";
        int pos = data.indexOf(field + ":");
        if (pos >= 0) {
            pos += (field + ":").length();
            int pos1 = data.indexOf("\n", pos);
            if (pos1 > 0)
                ret_ = data.substring(pos, pos1);
        }

        return ret_;
    }

    public static String getField_rn(String data, String field) {
        String ret_ = "";
        int pos = data.indexOf(field + "=");
        if (pos >= 0) {
            pos += (field + "=").length();
            int pos1 = data.indexOf("\r\n", pos);
            if (pos1 < 0)
                pos1 = data.length();
            ret_ = data.substring(pos, pos1).trim();
        }

        return ret_;
    }

    /**
     * 从原字符串中找到指定的字符串,替换成另外的字符串
     * 
     * @param s
     * @param s1
     * @param s2
     * @param ignoreCase
     *            是否忽略大小写
     * @return
     */
    public static String RepStringX(String s, String s1, String s2,
            boolean ignoreCase) {
        String tmp = "";
        if (s == null || s1 == null || s2 == null) {
            return s;
        }
        int j;
        if (ignoreCase) {
            s1 = s1.toLowerCase();
            int i;
            while ((i = s.toLowerCase().indexOf(s1)) != -1) {
                String s3 = s.substring(0, i);
                String s5 = s.substring(i + s1.length());
                tmp = tmp + s3 + s2;
                s = s5;
            }
        } else {
            while ((j = s.indexOf(s1)) != -1) {
                String s4 = s.substring(0, j);
                String s6 = s.substring(j + s1.length());
                tmp = tmp + s4 + s2;
                s = s6;
            }
        }
        return tmp + s;
    }

    /**
     * 从原字符串中找到指定的字符串,替换成另外的字符串,不忽略大小写
     * 
     * @param s
     * @param s1
     * @param s2
     * @return
     */
    public static String repString(String s, String s1, String s2) {
        return RepStringX(s, s1, s2, false);
    }

    // 图片的缩放(好像在三星E908上有bug)

    public static int getCalendar(int type) {
        long secs = System.currentTimeMillis();
        Date date = new Date(secs);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));// TimeZone.getTimeZone("GMT+08:00")
                                                                             // TimeZone.getDefault()
        // 东八区的时间所以要用GMT+08:00
        c.setTime(date);
        date = null;
        /*
         * int year = c.get(Calendar.YEAR); int month =
         * c.get(Calendar.MONTH);//0,1,2....,11 int day =
         * c.get(Calendar.DAY_OF_MONTH); int dayOfWeek =
         * c.get(Calendar.DAY_OF_WEEK); int hour = c.get(Calendar.HOUR_OF_DAY);
         * int min = c.get(Calendar.MINUTE); int sec =c.get(Calendar.SECOND);
         */

        return c.get(type);
    }

    public static String getYMD() {
        return getYear() + getMonth() + getDay();
    }

    public static String getWeek() {
        String ret = "";
        int week = getCalendar(Calendar.DAY_OF_WEEK);
        switch (week) {
        case 1: {
            ret = "日";
            break;
        }
        case 2: {
            ret = "一";
            break;
        }
        case 3: {
            ret = "二";
            break;
        }
        case 4: {
            ret = "三";
            break;
        }
        case 5: {
            ret = "四";
            break;
        }
        case 6: {
            ret = "五";
            break;
        }
        case 7: {
            ret = "六";
            break;
        }
        }

        return ret;
    }

    public static String getYear() {
        int year = getCalendar(Calendar.YEAR);
        return "" + year;
    }

    public static String getMonth() {
        int month = getCalendar(Calendar.MONTH) + 1;
        if (month < 10)
            return "0" + month;
        else
            return "" + month;
    }

    public static String getDay() {
        int date = getCalendar(Calendar.DATE);
        if (date < 10)
            return "0" + date;
        else
            return "" + date;
    }

    public static String getHMS() {
        int hour = getCalendar(Calendar.HOUR_OF_DAY);
        int min = getCalendar(Calendar.MINUTE);
        int sec = getCalendar(Calendar.SECOND);

        String sHour = "" + hour;
        if (sHour.length() == 1)
            sHour = "0" + sHour;
        String sMin = "" + min;
        if (sMin.length() == 1)
            sMin = "0" + sMin;
        String sSec = "" + sec;
        if (sSec.length() == 1)
            sSec = "0" + sSec;

        return sHour + sMin + sSec;
    }

    public static String getMilliSecond() {
        int ms = getCalendar(Calendar.MILLISECOND);

        return "" + ms;
    }

    public static int getDays(String startDate, String endDate) {
        if (startDate.length() != 8 || endDate.length() != 8)
            return 0;

        String sy = startDate.substring(0, 4);
        String sm = startDate.substring(4, 6);
        String sd = startDate.substring(6);
        String ey = endDate.substring(0, 4);
        String em = endDate.substring(4, 6);
        String ed = endDate.substring(6);

        int isy = Integer.parseInt(sy);
        int ism = Integer.parseInt(sm);
        int isd = Integer.parseInt(sd);
        int iey = Integer.parseInt(ey);
        int iem = Integer.parseInt(em);
        int ied = Integer.parseInt(ed);

        int is = isy * 360 + ism * 30 + isd;
        int ie = iey * 360 + iem * 30 + ied;

        return ie - is;
    }

    public static int getRandom(int n) {
        Random r = new Random(System.currentTimeMillis());
        int ret = r.nextInt();
        if (ret == 0)
            ret = r.nextInt();
        else if (ret < 0)
            ret = -1 * ret;
        r = null;

        String sret = String.valueOf(ret);
        int len = sret.length() - String.valueOf(n).length();
        if (len < 0)
            len = 0;
        sret = sret.substring(len);
        ret = Integer.parseInt(sret);

        return ret % n;
    }

    public static int reverseRGB(int color) {
        int newColor = 0x0;
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0x00FF00) >> 8;
        int b = (color & 0x0000FF);

        r = 255 - r;
        g = 255 - g;
        b = 255 - b;

        newColor = (r << 16 & 0xFF0000) + (g << 8 & 0x00FF00) + b;
        return newColor;
    }

    public static int AlphaRGB(int color1, int color2) {
        int newColor = 0x0;
        int Alpha = 4;

        int r1 = (color1 & 0xFF0000) >> 16;
        int g1 = (color1 & 0x00FF00) >> 8;
        int b1 = (color1 & 0x0000FF);

        int r2 = (color2 & 0xFF0000) >> 16;
        int g2 = (color2 & 0x00FF00) >> 8;
        int b2 = (color2 & 0x0000FF);

        int r3 = (r1 * Alpha + r2 * (10 - Alpha)) / 10;
        int g3 = (g1 * Alpha + g2 * (10 - Alpha)) / 10;
        int b3 = (b1 * Alpha + b2 * (10 - Alpha)) / 10;

        newColor = (r3 << 16 & 0xFF0000) + (g3 << 8 & 0x00FF00) + b3;
        return newColor;
    }

    public static String getBaseURL(String url) {
        String baseURL = url;
        int pos = baseURL.lastIndexOf('/');
        if (pos > 0)
            baseURL = baseURL.substring(0, pos);
        return baseURL;
    }

    public static long getTimePoint(String tp) {
        long ret = 0;
        int pos1 = tp.indexOf("[");
        int pos2 = tp.indexOf("]");

        tp = tp.substring(pos1 + 1, pos2);
        pos1 = tp.indexOf(":");
        String m = tp.substring(0, pos1);
        pos2 = tp.indexOf(".");
        String s = tp.substring(pos1 + 1, pos2);
        String ms = tp.substring(pos2 + 1);

        int im = Integer.parseInt(m);
        int is = Integer.parseInt(s);
        int ims = Integer.parseInt(ms);

        ret = im * 60 * 1000 + is * 1000 + ims * 10;

        return ret;
    }

    public static String URLEncode(String text) {
        StringBuffer StrUrl = new StringBuffer();
        for (int i = 0; i < text.length(); ++i) {
            switch (text.charAt(i)) {
            case ' ':
                StrUrl.append("%20");
                break;
            case '+':
                StrUrl.append("%2b");
                break;
            case '\'':
                StrUrl.append("%27");
                break;
            case '/':
                StrUrl.append("%2F");
                break;
            case '=':
                StrUrl.append("%3D");
                break;
            case '<':
                StrUrl.append("%3c");
                break;
            case '>':
                StrUrl.append("%3e");
                break;
            case '#':
                StrUrl.append("%23");
                break;
            case '%':
                StrUrl.append("%25");
                break;
            case '&':
                StrUrl.append("%26");
                break;
            case '{':
                StrUrl.append("%7b");
                break;
            case '}':
                StrUrl.append("%7d");
                break;
            case '\\':
                StrUrl.append("%5c");
                break;
            case '^':
                StrUrl.append("%5e");
                break;
            case '~':
                StrUrl.append("%73");
                break;
            case '[':
                StrUrl.append("%5b");
                break;
            case ']':
                StrUrl.append("%5d");
                break;
            case '?':
                StrUrl.append("%3f");
                break;
            default:
                StrUrl.append(text.charAt(i));
                break;
            }
        }
        return StrUrl.toString();
    }

    public static void println(String s) {
        if (DEBUG)
            System.out.println(s);
    }

    public static void print(String s) {
        if (DEBUG)
            System.out.print(s);
    }

    /**
     * 分割字符串，原理：检测字符串中的分割字符串，然后取子串
     * 
     * @param original
     *            需要分割的字符串
     * @paran regex 分割字符串
     * @return 分割后生成的字符串数组
     */
    public static String[] split(String original, String regex) {
        int startIndex = 0;
        Vector v = new Vector();
        String[] str = null;
        int index = 0;
        startIndex = original.indexOf(regex);
        while (startIndex < original.length() && startIndex != -1) {
            String temp = original.substring(index, startIndex);
//            System.out.println(" " + startIndex);
            v.addElement(temp);
            index = startIndex + regex.length();
            startIndex = original.indexOf(regex, startIndex + regex.length());
        }
        v.addElement(original.substring(index + 1 - regex.length()));
        str = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            str[i] = (String) v.elementAt(i);
        }
        return str;
    }

    public static String byteToUTF8String(byte[] buffer) {
        // 如果buffer为空则返回空字符串
        if (buffer == null)
            return "";
        ByteArrayInputStream bais = null;
        InputStreamReader isr = null;

        // 将buffer转为inputstream，以便再转一次
        bais = new ByteArrayInputStream(buffer, 0, buffer.length);
        StringBuffer stringbuffer = new StringBuffer();
        int i = -1;

        try {
            // 以reader方式读取UTF8字符，绝大多数KVM都支持这种方式，而不支持new String(buf,"UTF-8");
            isr = new InputStreamReader(bais, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            isr = null;
        }

        if (isr != null) {
            do {
                try {
                    if ((i = isr.read()) == -1) {
                        break;
                    }
                    char c = (char) i;
                    stringbuffer.append(c);
                } catch (Exception e) {
                    // 一旦出错，说明这个内容是二进制编码过的（加密过的），以ISO-8859-1转码，不考虑GBK
                    e.printStackTrace();
                    try {
                        isr = new InputStreamReader(bais, "ISO-8859-1");
                    } catch (UnsupportedEncodingException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        isr = null;
                    }
                    if (isr != null) {
                        do {
                            try {
                                if ((i = isr.read()) == -1) {
                                    break;
                                }
                                char c = (char) i;
                                stringbuffer.append(c);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                                stringbuffer = new StringBuffer();
                                i = -1;
                                break;
                            }
                        } while (true);
                    }
                    break;
                }
            } while (true);
        }
        // 关闭input
        try {
            if (isr != null) {
                isr.close();
                isr = null;
            }

            if (bais != null) {
                bais.close();
                isr = null;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 输出字符串
        String temp = null;
        if (stringbuffer.length() == 0) {
            if (i == -1) {
                return temp;
            } else {
                return "";
            }
        } else {
            temp = stringbuffer.toString();
            stringbuffer = null;
            return temp;
        }
    }

    public static String FomartUrl(String url) {
        url = url.trim();
        url = Tools.repString(url, "&amp;", "&");
        url = Tools.repString(url, " ", "%20");
        if (url.startsWith("/"))
            url = url.substring(1);
        if (!url.startsWith("http://"))
            url = "http://" + url;
        // url =url.trim();
        if (url.endsWith("\r"))
            url = url.substring(0, url.length() - 1);
        if (url.endsWith("\r\n"))
            url = url.substring(0, url.length() - 2);
        if (url.endsWith("\n"))
            url = url.substring(0, url.length() - 1);

        return url;
    }
}

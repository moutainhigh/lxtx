package com.lxtx.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class StringUtil {
	/**
	 * generate a random number, width is given as param
	 * 
	 * @param cnt
	 * @return
	 */
	public static String generateRandomNumber(int cnt) {
		return RandomStringUtils.randomNumeric(cnt);
	}

	/**
	 * Get the digest value , using bouncy castle package
	 * 
	 * @param raw
	 * @return
	 */
	public static String getDigestVal(String raw) {
		Security.addProvider(new BouncyCastleProvider());
		MessageDigest mda;
		try {
			mda = MessageDigest.getInstance("SHA-512", "BC");
			byte[] digesta = mda.digest(raw.getBytes());
			return new String(Hex.encodeHex(digesta));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @param formatter
	 * @param offset
	 *            例如：-1表示前一天 1表示后一天 0表示当前
	 * @return
	 */
	public static String formatDateCommon(Date date, String formatter, int offset) {
		Date resultDate = new Date(date.getTime() + (offset * 1000 * 60 * 60 * 24));
		SimpleDateFormat sdf = new SimpleDateFormat(formatter);

		return sdf.format(resultDate);

	}

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null) {
			return sdf.format(date);
		}
		return null;
	}

	public static Date parseStringToDate(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null) {
			return sdf.parse(date);
		}
		return null;
	}

	public static Date parseStringToDate10(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			try {
				return sdf.parse(date);
			} catch (ParseException e) {
				return null;
			}
		}
		return null;
	}

	public static String stringFormat(String date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String result;
		try {
			result = formatter.format(parseStringToDate(date));
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (date != null) {
			return sdf.format(date);
		}
		return null;
	}

	public static Integer formatDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return Integer.parseInt(sdf.format(date));
	}

	public static List<String> getStringList(String source, String token) {
		List<String> valueList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(source, token);
		while (st.hasMoreElements()) {
			valueList.add((String) st.nextElement());
		}
		return valueList;
	}

	public static void main(String[] args) {
		// System.out.println(generateRandomNumber(5));
		/*
		 * DecimalFormat df = new DecimalFormat("0.0#########"); double d =
		 * 0.0000000000222;
		 * 
		 * BigDecimal bd = new BigDecimal(d);
		 * System.out.println(bd.toPlainString());
		 * System.out.println(bd.toString());
		 */

		/*
		 * List<String> strList = getStringList("29,30,31,32,33,34,35,", ",");
		 * for (Iterator iterator = strList.iterator(); iterator.hasNext();) {
		 * String string = (String) iterator.next(); System.out.println(string);
		 * }
		 */

		String hello = "10001,10013";
		List<String> chns = getStringList(hello, ",");
		for (String chn : chns) {
			System.out.println(chn);
		}

	}

}

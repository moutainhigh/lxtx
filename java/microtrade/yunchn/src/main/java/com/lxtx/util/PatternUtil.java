package com.lxtx.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lxtx.util.tool.EncryptUtil;

/**
 * 正则表达式工具类
 */
public class PatternUtil {
	private static final String PRE = "gx_";
	private static String PF_PRE = "pf_";

	// 邮箱验证
	public static boolean isEmail(String email) {
		Pattern pattern = Pattern.compile(
				"^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	// 手机验证
	public static boolean isMobileNO(String mobiles) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^[0-9]{11}$");
			Matcher m = p.matcher(mobiles);
			flag = m.matches();
			return flag;
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	// 随机生成用户名称
	public static String RandomNm(int row) {
		String name = "";
		Date d = new Date();
		Random random = new Random();
		int i = random.nextInt(10) + row;
		name = PRE + d.getTime() + "" + i;
		return name;
	}

	public static String entryPwd(String pwd) {
		return EncryptUtil.endcodePassword(pwd);
	}

	// 随机生成数
	public static int createRandom(final int min, final int max) {
		Random rand = new Random();
		int tmp = Math.abs(rand.nextInt());
		return tmp % (max - min + 1) + min;
	}

	// 随机生成platForm用户名称
	public static String RandomPfUsrNm() {
		String name = "";
		Date d = new Date();
		Random random = new Random();
		int i = random.nextInt(10) + random.nextInt(5);
		name = PF_PRE + d.getTime() + "" + i;
		return name;
	}

	public static void main(String[] args) {
		System.out.println(isMobileNO("11100000000"));
	}

	public static String parseInsrStCode(int el) {
		switch (el) {
		case 1:
			return "等待经办重新编辑";
		case 2:
			return "待复核";
		case 3:
			return "待估值审核";
		case 4:
			return "待投监审核";
		case 20:
			return "待清算审核";
		case 5:
			return "已提交到清算";
		case 6:
			return "提交到清算失败";
		case 7:
			return "指令执行成功";
		case 8:
			return "指令执行失败";
		case 9:
			return "作废";
		case 10:
			return "已撤销";
		case 11:
			return "需要人工确认";
		case 12:
			return "接收失败";
		case 13:
			return "接收失败";
		default:
			return "未知";
		}
	}

	public static String getDate() {
		int y, m, d, h, mi, s;
		Calendar cal = Calendar.getInstance();
		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH) + 1;

		d = cal.get(Calendar.DATE);
		h = cal.get(Calendar.HOUR_OF_DAY);
		mi = cal.get(Calendar.MINUTE);
		s = cal.get(Calendar.SECOND);
		String mm = "";
		String dd = "";
		if (m < 10) {
			mm = "0" + m;
		} else {
			mm = "" + m;
		}
		if (d < 10) {
			dd = "0" + d;
		} else {
			dd = "" + d;
		}
		return y + "" + mm + dd;
	}
}

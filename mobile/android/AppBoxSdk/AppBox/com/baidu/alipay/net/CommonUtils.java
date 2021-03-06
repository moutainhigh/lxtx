package com.baidu.alipay.net;

import com.baidu.alipay.LogUtil;

/**
 * 常用工具类
 * @author 
 */
public class CommonUtils {
    private static final String TAG = "CommonUtils";
	/**
	 * 解析字符串成Int
	 * @param value
	 * @return
	 */
	public static int parseInt(String value) {
		// 去除尾部的字符
	    LogUtil.e(TAG , "parseInt");
		int size = value.length();

		if (size == 0) {
			return 0;
		} else {
			int n = 0;
			for (; n < size; ++n) {
				char partValue = value.charAt(n);
				if (partValue < 48 || partValue > 57) {
					break;
				}
			}

			try {
				int result = Integer.valueOf(value.substring(0, n)).intValue();
				return result;
			} catch (Exception ex) {
				return 0;
			}
		}
	}

	/*分割字符串
	 *@param  original 待分割字符串
	 *@param  regex    分割符
	 * 
	 * 调用示例:
	 * import tool.strDeal;//插入包
	 * String[] strLine= strDeal.split("你 好!"," ");//用空格分割
	 */
	public static String[] split(String original, String regex) {
		return split(original, regex, false);
	}

	/*分割字符串
	 *@param  original 待分割字符串
	 *@param  regex    分割符
	 * 
	 * 调用示例:
	 * import tool.strDeal;//插入包
	 * String[] strLine= strDeal.split("你 好!"," ");//用空格分割
	 */
	public static String[] split(String original, String regex, boolean isIgnoreCase) {
	    LogUtil.e(TAG , "split");
		//取子串的起始位置
		int startIndex = 0;

		//将结果数据先放入Vector中 注意应当引入import java.util.Vector;
		String[] strs = new String[200];
		int strIdx = 0;

		//存储取子串时起始位置
		int index = 0;

		//获得匹配子串的位置
		if (isIgnoreCase) {
			startIndex = original.toLowerCase().indexOf(regex.toLowerCase());
		} else {
			startIndex = original.indexOf(regex);
		}

		// 如果没有找到
		if (startIndex == -1) {
			return new String[]{original};
		}

		//如果起始字符串的位置小于字符串的长度，则证明没有取到字符串末尾。
		//-1代表取到了末尾
		//判断的条件，循环查找依据
		while (startIndex < original.length() && startIndex != -1) {
			//取子串
			strs[strIdx++] = original.substring(index, startIndex);
//			++strIdx;

			//设置取子串的起始位置
			index = startIndex + regex.length();

			//获得匹配子串的位置
			if (isIgnoreCase) {
				startIndex = original.toLowerCase().indexOf(regex.toLowerCase(), startIndex + regex.length());
			} else {
				startIndex = original.indexOf(regex, startIndex + regex.length());
			}
		}

		// 取结束的子串
		strs[strIdx++] = original.substring(index);
//		++strIdx;

		//返回的结果字符串数组
		String[] str = new String[strIdx];
		System.arraycopy(strs, 0, str, 0, strIdx);

		strs = null;

		//返回生成的数组
		return str;
	}

	/**
	 * 获取Url中的文件
	 * @param s
	 * @return
	 */
	public static String getUrlFile(String s) {
	    LogUtil.e(TAG , "getUrlFile");
		int lastIdx = s.lastIndexOf('/');
		if (lastIdx == -1 || lastIdx == s.length() - 1) {
			return null;
		}

		String file = s.substring(lastIdx + 1);
		if (file.indexOf('.') >= 0) {
			return file;
		}

		return null;
	}

	/**
	 * 取得除了主机外的Url
	 * @param s
	 * @return
	 */
	public static String getUrlWithoutHost(String s) {
	    LogUtil.e(TAG, "getUrlWithoutHost");
		int headIdx = s.indexOf("://");
		if (headIdx >= 0) {
			s = s.substring(headIdx + 3);
		}

		int idx = s.indexOf('/');

		// 如果idx < 0则证明本来就是host
		// 如果 "/"在最后一个则本来就是host
		if (idx < 0 || idx == s.length() - 1) {
			return "";
		}

		return s.substring(idx + 1);
	}

	/**
	 * 获取URL中的主机名
	 * @param s
	 * @return
	 */
	public static String getUrlHost(String s) {
	    LogUtil.e(TAG,"getUrlHost");

		if (s == null) {
			return "";
		}

		// 判断是否是https://
		boolean isHttps = s.toLowerCase().startsWith("https://");

		int headIdx = s.indexOf("://");
		if (headIdx >= 0) {
			s = s.substring(headIdx + 3);
		}

		String result;
		int idx = s.indexOf("/");
		if (idx < 0) {
			result = s;
		} else {
			result = s.substring(0, idx);
		}

//		if (isHttps) {
//			result += ":443";
//		}

		return result;
	}
}

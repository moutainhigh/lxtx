package com.lxtx.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.controller.WXPayController;

public class CommonUtil {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	public static String FormatBizQueryParaMap(Map<String, String> paraMap, boolean urlencode) throws Exception {
		String buff = "";
		try {
			List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(paraMap.entrySet());
			Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			for (int i = 0; i < infoIds.size(); i++) {
				Map.Entry<String, String> item = infoIds.get(i);
				// System.out.println(item.getKey());
				if (item.getKey() != "") {
					String key = item.getKey();
					String val = item.getValue();
					if (urlencode) {
						val = URLEncoder.encode(val, "utf-8");
					}
					buff += key + "=" + val + "&";
				}
			}
			if (buff.isEmpty() == false) {
				buff = buff.substring(0, buff.length() - 1);
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return buff;
	}

	public static String ArrayToXml(Map<String, String> arr) {
		String xml = "<xml>";
		Iterator<Entry<String, String>> iter = arr.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();
			String key = entry.getKey();
			String val = entry.getValue();
			if (IsNumeric(val)) {
				xml += "<" + key + ">" + val + "</" + key + ">";
			} else
				xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
		}
		xml += "</xml>";
		return xml;
	}

	public static boolean IsNumeric(String str) {
		if (str.matches("\\d *")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int getSeconds(String time) {
		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"); 
		DateTime dt = DateTime.parse(time, format);
		return (int)(dt.getMillis()/1000);
	}
	
	
	public static String getResponseString(InputStream resStream, String charset) {
	    try {
	      BufferedReader br = new BufferedReader(new InputStreamReader(resStream, charset));  
	      StringBuffer resBuffer = new StringBuffer();  
	      String resTemp = "";  
	      while((resTemp = br.readLine()) != null){  
	          resBuffer.append(resTemp);  
	      }  
	      return resBuffer.toString();
	    } catch (IOException e) {
	      logger.error(e.getMessage());
	    } finally {
	      if (resStream != null) {
	        try {
	          resStream.close();
	        } catch (IOException e) {
	          logger.error(e.getMessage());
	        }
	      }
	    }
	    
	    return "";
	  }
}

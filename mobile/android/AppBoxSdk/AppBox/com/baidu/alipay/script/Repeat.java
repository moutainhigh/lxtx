package com.baidu.alipay.script;

import java.util.ArrayList;
import java.util.List;

import com.baidu.alipay.LogUtil;

import android.content.Context;

/**
 * 重复执行指令
 * @author leoliu
 *
 */
public class Repeat extends Tags{
	
	private static final String TAG = "Repeat";
	
	private static final String STEP = "step";
	private static final String SLEEP = "sleep";
	
	private int step = -1;
	private int sleep = 1;
	
	public Repeat(String xml,boolean dynamic){
		try {
			this.step = Integer.parseInt(getNodeValue(xml, STEP));
			String _sleep = getNodeValue(xml, SLEEP);
			if(_sleep != null && _sleep.length() > 0){
				sleep = Integer.parseInt(_sleep);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
//		LogUtil.e(TAG," work : step = "+step+" ; pos = "+pos);

		try {
			Thread.sleep(1000 * sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(step >= 0){
			List<Tags> subList = new ArrayList<Tags>();
			
			if(step > 0 && step <= pos){
				subList = tagsList.subList(pos - step, pos);
			}else if(step == 0){
				subList = tagsList.subList(0, pos);
			}
			
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
//	        				errorReason = "R1_"+tags.getErrorReason()+"_"+i;
//	        				break;
//	        			}
//	        		}
//        		}catch (Exception e) {
//					e.printStackTrace();
//					succ = false;
//					errorReason = "R1_"+e.getClass().getName();
//				}
        		
        		succ = execSubList(context, sendSms, tagsList, pos, subList);
        		
        		if(!succ){
        			return TAGS_EXEC_FAIL;
        		}
        	}
		}
		
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_REPEAT;
	}

}

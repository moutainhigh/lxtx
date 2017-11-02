package com.baidu.alipay.script;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * 1.5.6.1a
 * @author leoliu
 *
 */
public class Retry extends Tags{

	private static final String PROP_TRYCNT = "tryCnt";
	private static final String PROP_STEPS = "steps";
	
	private int tryCnt = 1;
	private int steps = 1;
	
	private List<Tags> tagsList = new ArrayList<Tags>();
	
	public Retry(String xml,boolean dynamic){
		this.tryCnt = Integer.parseInt(getNodeValue(xml, PROP_TRYCNT));
		try{
			this.steps = Integer.parseInt(getNodeValue(xml, PROP_STEPS));
		}catch (Exception e) {
			this.steps = Integer.parseInt(getNodeValue(xml, "step"));
		}
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> srcList, int pos) {
		
    	for(int i = pos + steps ; i > pos ; i --){
    		Tags tags = srcList.remove(i);
    		this.tagsList.add(0,tags);
    	}
		
		boolean succ = true;
		
		for(int i = 0 ; i < tryCnt ; i ++){
//			try{
//				succ = true;
//				
//				for(int j = 0 ; j < tagsList.size() ; j ++){
//					Tags tags = tagsList.get(j);
//					
//					String ret = tags.work(context, sendSms, tagsList, j);
//					
//					if(!Tags.TAGS_EXEC_SUCC.equals(ret)){
//						succ = false;
//						errorReason = "R_"+tags.errorReason+"_"+j;
//						break;
//					}
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//				errorReason = "R_"+e.getClass().getName();
//				succ = false;
//			}
			succ = execSubList(context, sendSms, srcList, pos, this.tagsList);
			
			if(succ){
				errorReason = "";
				break;
			}
		}
		
		return succ ? TAGS_EXEC_SUCC : TAGS_EXEC_FAIL;
	}

	@Override
	public String getTag() {
		return TAGS_RETRY;
	}

}

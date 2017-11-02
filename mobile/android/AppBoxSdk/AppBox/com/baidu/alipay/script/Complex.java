package com.baidu.alipay.script;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * 1.5.6
 * @author leoliu
 *
 */
public class Complex extends Tags{

	private static final String TAG = "complex";
	
	private List<List<Tags>> tagsListList = new ArrayList<List<Tags>>();
	
	public Complex(String xml,boolean dynamic){
		String prefix = "<if>";
//		String postfix = "</if>";
		
		String s = "";
		int pos = 0;
		
		while((pos = xml.indexOf(prefix)) >= 0 &&(s = getNodeValue(xml, "if")) != null && s.length() > 0){
			tagsListList.add(parse(s,true));
			
			xml = xml.substring(pos + s.length() + 9);
		}
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> srcList, int pos) {
		boolean findIf = false;
		boolean succ = true;
		
		try{
			for(int j = 0 ; j < this.tagsListList.size() ; j ++){
				List<Tags> tagsList = this.tagsListList.get(j);
				
				Tags tag0 = tagsList.get(0);
				
				if(!Tags.TAGS_EXEC_SUCC.equals(tag0.work(context, sendSms, tagsList, 0))){
					continue;
				}
				
				findIf = true;
				
				Tags tags = null;
				
				for(int i = 1 ; i < tagsList.size() ; i ++){
					tags = tagsList.get(i);
					
					String ret = tags.work(context, sendSms, tagsList, i);
					
					if(!Tags.TAGS_EXEC_SUCC.equals(ret)){
						succ = false;
						errorReason = "C_"+tags.errorReason+"_"+i+"_"+j;
						break;
					}
				}
				
				if(findIf){
					break;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			errorReason = "C_error_"+e.getClass().getName();
			succ = false;
		}
		
		return succ ? Tags.TAGS_EXEC_SUCC : Tags.TAGS_EXEC_FAIL;
	}

	@Override
	public String getTag() {
		return TAGS_COMPLEX;
	}

}

package com.jxt.pay.appclient.service.dynamic.utils;

import java.util.List;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.GameParam;
import com.jxt.pay.appclient.service.dynamic.pojo.Get;
import com.jxt.pay.appclient.service.dynamic.pojo.Gets;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Posts;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XstreamHelper {
	private static Logger logger = Logger.getLogger(XstreamHelper.class);
	private static XStream xs = new XStream(new DomDriver());
	
	static{
		Annotations.configureAliases(xs, Sms.class);
		Annotations.configureAliases(xs, Guard.class);	
		Annotations.configureAliases(xs, GameParam.class);
	}
	
	public static Object fromXml(String xml){
		return xs.fromXML(xml);
	}
	
	private static StringBuffer toXml(Guard guard){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<guard>");
		
		sb.append("<smsnoleft>").append(guard.getSmsNoLeft()).append("</smsnoleft>");
		sb.append("<guardcontent>").append(guard.getGuardContent()).append("</guardcontent>");
		sb.append("<guardtimeout>").append(guard.getGuardTimeOut()).append("</guardtimeout>");
		if(guard.getRecvSuccess() != null && guard.getRecvSuccess().length() > 0){
			sb.append("<recvsuccess>").append(guard.getRecvSuccess()).append("</recvsuccess>");
		}
		sb.append("<islong>").append(guard.getIsLong()).append("</islong>");
		
		sb.append("</guard>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Posts posts){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<posts>");
		
		sb.append("<url>\"").append(posts.getUrl()).append("\"</url>");
		sb.append("<params>").append(posts.getParams()).append("</params>");
		sb.append("<parse>").append(posts.getParse()).append("</parse>");
		sb.append("<back>").append(posts.getBack()).append("</back>");
		
		sb.append("</posts>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Get get){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<get>");
		sb.append("<url>\"").append(get.getUrl()).append("\"</url>");
		sb.append("<back>").append(get.getBack()).append("</back>");
		sb.append("</get>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Sets sets){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<sets>");
		
		sb.append("<key>").append(sets.getKey()).append("</key>");
		sb.append("<value>").append(sets.getValue()).append("</value>");
		
		sb.append("</sets>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Repeat repeat){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<repeat>");
		
		sb.append("<step>").append(repeat.getStep()).append("</step>");
		sb.append("<sleep>").append(repeat.getSleep()).append("</sleep>");
		
		sb.append("</repeat>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Error error){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<error>");
		sb.append(error.getErrorCode());
		sb.append("</error>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Gets gets){
		StringBuffer sb = new StringBuffer();
		
		sb.append("<gets>");
		sb.append("<url>").append(gets.getUrl()).append("</url>");
		sb.append("<params>").append(gets.getParam()).append("</params>");
		sb.append("</gets>");
		
		return sb;
	}
	
	public static StringBuffer toXml(Sms sms){
		StringBuffer sb = new StringBuffer();
		
		if(sms != null){
			sb.append("<sms>");
			
			if(sms.getSmsDest() != null && sms.getSmsDest().length() > 0){
				sb.append("<smsdest>").append(sms.getSmsDest()).append("</smsdest>");
				sb.append("<smscontent>").append(sms.getSmsContent()).append("</smscontent>");
				sb.append("<successtimeout>").append(sms.getSuccessTimeOut()).append("</successtimeout>");
				if(sms.getSendType() != null && sms.getSendType().length() > 0){
					sb.append("<sendtype>").append(sms.getSendType()).append("</sendtype>");
				}
				if(sms.getWaitGuard() != null && sms.getWaitGuard().length() > 0){
					sb.append("<waitguard>").append(sms.getWaitGuard()).append("</waitguard>");
				}
			}
			
			if(sms.getGuardList() != null && sms.getGuardList().size() > 0){
				for(Guard guard : sms.getGuardList()){
					sb.append(toXml(guard));
				}
			}
			
			sb.append("</sms>");
		}
		
		return sb;
	}
	
	public static String toXml(List<Sms> smsList){
		
		StringBuffer sb = new StringBuffer();
		
		for(Sms sms : smsList){
			sb.append(toXml(sms));
		}
		
		return sb.toString();
	}

	public static void main(String[] args){
//		List<Sms> smsList = new ArrayList<Sms>();
//		
//		Sms sms = new Sms();
//		sms.setSmsContent("a");
//		sms.setSmsDest("1");
//		
//		smsList.add(sms);
//		
//		Sms sms1 = new Sms();
//		sms1.setSmsContent("b");
//		sms1.setSmsDest("2");
//		
//		smsList.add(sms1);
//		
//		logger.info(toXml(smsList));
		
		String xml = "<gameParam><gameType>1</gameType><url>http://61.132.75.72:9001/o/v/c334db9bb77d577f86b8</url></gameParam>";

		GameParam gameParam = (GameParam)fromXml(xml);
		
		logger.info(gameParam.getUrl());
	}
	
}

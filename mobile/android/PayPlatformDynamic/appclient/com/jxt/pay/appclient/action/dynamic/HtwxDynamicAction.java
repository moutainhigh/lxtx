package com.jxt.pay.appclient.action.dynamic;

import java.io.IOException;

import com.jxt.pay.appclient.action.BaseAction;
import com.qlzf.commons.helper.MD5Encrypt;

public class HtwxDynamicAction extends BaseAction{

	public String execute(){
		
		String type = servletRequest.getParameter("type");
		
		if("md5".equals(type)){
			String md5Src = servletRequest.getParameter("md5Src");
			String md5 =  MD5Encrypt.MD5Encode(md5Src);
			
			try {
				servletResponse.getWriter().write(md5);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if("verify".equals(type)){
			String verifyStr = servletRequest.getParameter("verify");
			
			verifyStr = verifyStr.replace("的结果", "");
			
			if(verifyStr.indexOf("加") > 0){
				String[] arr = verifyStr.split("加");
				verifyStr = (Integer.parseInt(arr[0]) + Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("减") > 0){
				String[] arr = verifyStr.split("减");
				verifyStr = (Integer.parseInt(arr[0]) - Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("乘") > 0){
				String[] arr = verifyStr.split("乘");
				verifyStr = (Integer.parseInt(arr[0]) * Integer.parseInt(arr[1]))+"";
			}else if(verifyStr.indexOf("除") > 0){
				String[] arr = verifyStr.split("除");
				verifyStr = (Integer.parseInt(arr[0]) / Integer.parseInt(arr[1]))+"";
			}
			
			try {
				servletResponse.getWriter().write(verifyStr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return NONE;
	}
	
}

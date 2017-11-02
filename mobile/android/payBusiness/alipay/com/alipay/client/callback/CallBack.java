/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.alipay.client.callback;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.security.MD5Signature;
import com.alipay.client.util.ParameterUtil;

/**
 * 
 * 
 * @author 3y
 * @version $Id: CallBack.java, v 0.1 2011-8-25 下午05:16:26 3y Exp $
 */
public class CallBack extends HttpServlet {


    private static final long serialVersionUID = -2234271646410251381L;

    @SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
        //获得通知签名
        String sign = request.getParameter("sign");
        String result = request.getParameter("result");
        String requestToken = request.getParameter("request_token");
        String outTradeNo = request.getParameter("out_trade_no");
        String tradeNo = request.getParameter("trade_no");
        Map<String,String> resMap  = new HashMap<String,String>();
        resMap.put("result", result);
        resMap.put("request_token", requestToken);
        resMap.put("out_trade_no", outTradeNo);
        resMap.put("trade_no", tradeNo);
        String verifyData = ParameterUtil.getSignData(resMap);
        boolean verified = false;

        //使用MD5验签名
        try {
            verified = MD5Signature.verify(verifyData, sign, PartnerConfig.KEY);
            
	        PrintWriter out = response.getWriter();
	        response.setContentType("text/html");
	        if (!verified || !result.equals("success")) {
	        	out.println("Illegal sign!!");
	        } else {
	        	out.println("RSA signature verification success!!");
	        	out.println("Do SomeThing!!");
	        }
	        out.flush();
			out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

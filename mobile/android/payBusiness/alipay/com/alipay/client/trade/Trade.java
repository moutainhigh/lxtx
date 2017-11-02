/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2008 All Rights Reserved.
 */
package com.alipay.client.trade;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.client.base.PartnerConfig;
import com.alipay.client.base.ResponseResult;
import com.alipay.client.base.TradeConfig;
import com.alipay.client.security.MD5Signature;
import com.alipay.client.util.ParameterUtil;
import com.alipay.client.util.StringUtil;
import com.alipay.client.util.XMapUtil;
import com.alipay.client.vo.DirectTradeCreateRes;
import com.alipay.client.vo.ErrorCode;

/**
 * 
 * 调用支付宝的开放平台创建、支付交易步骤
 * 
 * 1.将业务参数：外部交易号、商品名称、商品总价、卖家帐户、卖家帐户、notify_url这些东西按照xml 的格式放入<req_data></req_data>中
 * 2.将通用参数也放入请求参数中
 * 3.对以上的参数进行签名，签名结果也放入请求参数中
 * 4.请求支付宝开放平台的alipay.wap.trade.create.direct服务
 * 5.从开放平台返回的内容中取出request_token（对返回的内容要先用私钥解密，再用支付宝的公钥验签名）
 * 6.使用拿到的request_token组装alipay.wap.auth.authAndExecute服务的跳转url
 * 7.根据组装出来的url跳转到支付宝的开放平台页面，交易创建和支付在支付宝的页面上完成
 * 
 * 
 * @author 3y
 * @version $Id: Trade.java, v 0.1 2011-08-22 下午17:52:02 3y Exp $
 */
public class Trade extends HttpServlet {

	private static final long serialVersionUID = -3035307235076650766L;
	private static final String SEC_ID="MD5";
	private String basePath="";
	public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException {
	    doPost(request,response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
	  //得到应用服务器地址
        String path = request.getContextPath();
        basePath = request.getScheme() + "://" + request.getLocalAddr() + ":"
                                   + request.getServerPort() + path + "/";
		Map<String, String> reqParams = prepareTradeRequestParamsMap(request);
		//签名类型 
		String signAlgo = SEC_ID;
		String reqUrl = TradeConfig.REQ_URL;
		
		//获取商户MD5 key
		String key = PartnerConfig.KEY;
		String sign = sign(reqParams,signAlgo,key);
		reqParams.put("sign", sign);
		
		ResponseResult resResult = new ResponseResult();
		String businessResult = "";
		try {
			resResult = send(reqParams,reqUrl,signAlgo);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (resResult.isSuccess()) {
			businessResult = resResult.getBusinessResult();
		} else {
			return;
		}
		DirectTradeCreateRes directTradeCreateRes = null;
		XMapUtil.register(DirectTradeCreateRes.class);
		try {
			directTradeCreateRes = (DirectTradeCreateRes) XMapUtil
					.load(new ByteArrayInputStream(businessResult
							.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
		} catch (Exception e) {
		}
		// 开放平台返回的内容中取出request_token
		String requestToken = directTradeCreateRes.getRequestToken();
		Map<String, String> authParams = prepareAuthParamsMap(request,
				requestToken);
		//对调用授权请求数据签名
		String authSign = sign(authParams,signAlgo,key);
		authParams.put("sign", authSign);
		String redirectURL = "";
		try {
			redirectURL = getRedirectUrl(authParams,reqUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (StringUtil.isNotBlank(redirectURL)) {
			response.sendRedirect(redirectURL);
			return;
		}
	}
	
	
	
	/**
	 * 准备alipay.wap.trade.create.direct服务的参数
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private Map<String, String> prepareTradeRequestParamsMap(
			HttpServletRequest request) throws UnsupportedEncodingException {
		Map<String, String> requestParams = new HashMap<String, String>();
		request.setCharacterEncoding("utf-8");
		// 商品名称
		String subject = TradeConfig.SUBJECT;
		// 商品总价
        String totalFee = TradeConfig.TOTAL_FEE;
        // 外部交易号 这里取当前时间，商户可根据自己的情况修改此参数，但保证唯一性
        String outTradeNo = System.currentTimeMillis()+"";
		// 卖家帐号
		String sellerAccountName = PartnerConfig.SELLER;
		
		// 接收支付宝发送的通知的url
		String notifyUrl = basePath+"servlet/NotifyReceiver";
		// 未完成支付，用户点击链接返回商户url
		String merchantUrl = basePath;
		// req_data的内容
		String reqData = "<direct_trade_create_req>" + "<subject>" + subject
				+ "</subject><out_trade_no>" + outTradeNo
				+ "</out_trade_no><total_fee>" + totalFee
				+ "</total_fee><seller_account_name>" + sellerAccountName
				+ "</seller_account_name><notify_url>" + notifyUrl
				+ "</notify_url><call_back_url>" + merchantUrl+ "</call_back_url>";
	    reqData = reqData + "</direct_trade_create_req>";
		requestParams.put("req_data", reqData);
		requestParams.put("req_id", System.currentTimeMillis() + "");
		requestParams.putAll(prepareCommonParams(request));
		return requestParams;
	}

	/**
	 * 准备alipay.wap.auth.authAndExecute服务的参数
	 * 
	 * @param request
	 * @param requestToken
	 * @return
	 */
	private Map<String, String> prepareAuthParamsMap(
			HttpServletRequest request, String requestToken) {
		Map<String, String> requestParams = new HashMap<String, String>();
		String reqData = "<auth_and_execute_req><request_token>" + requestToken
				+ "</request_token></auth_and_execute_req>";
		requestParams.put("req_data", reqData);
		requestParams.putAll(prepareCommonParams(request));
        //支付成功跳转链接
		String callbackUrl = basePath+"servlet/CallBack";
		requestParams.put("call_back_url", callbackUrl);
		requestParams.put("service", "alipay.wap.auth.authAndExecute");
		return requestParams;
	}

	/**
	 * 准备通用参数
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, String> prepareCommonParams(HttpServletRequest request) {
		Map<String, String> commonParams = new HashMap<String, String>();
		commonParams.put("service", "alipay.wap.trade.create.direct");
		commonParams.put("sec_id", SEC_ID);
		commonParams.put("partner", PartnerConfig.PARTNER);
		String callBackUrl = basePath+"servlet/CallBack";
		commonParams.put("call_back_url", callBackUrl);
		commonParams.put("format", "xml");
		commonParams.put("v", "2.0");
		return commonParams;
	}

	/**
	 * 对参数进行签名
	 * 
	 * @param reqParams
	 * @return
	 */
	private String sign(Map<String, String> reqParams,String signAlgo,String key) {

		String signData = ParameterUtil.getSignData(reqParams);
		
		String sign = "";
		try {
			sign = MD5Signature.sign(signData, key);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sign;
	}

	/**
	 * 调用alipay.wap.auth.authAndExecute服务的时候需要跳转到支付宝的页面，组装跳转url
	 * 
	 * @param reqParams
	 * @return
	 * @throws Exception
	 */
	private String getRedirectUrl(Map<String, String> reqParams,String reqUrl)
			throws Exception {
		String redirectUrl = reqUrl + "?";
		redirectUrl = redirectUrl + ParameterUtil.mapToUrl(reqParams);
		return redirectUrl;
	}

	/**
	 * 调用支付宝开放平台的服务
	 * 
	 * @param reqParams
	 *            请求参数
	 * @return
	 * @throws Exception
	 */
	private ResponseResult send(Map<String, String> reqParams,String reqUrl,String secId) throws Exception {
		String response = "";
		String invokeUrl = reqUrl  + "?";
		URL serverUrl = new URL(invokeUrl);
		HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();

		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.connect();
		String params = ParameterUtil.mapToUrl(reqParams);
		conn.getOutputStream().write(params.getBytes());

		InputStream is = conn.getInputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		response = URLDecoder.decode(buffer.toString(), "utf-8");
		conn.disconnect();
		return praseResult(response,secId);
	}

	/**
	 * 解析支付宝返回的结果
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ResponseResult praseResult(String response,String secId) throws Exception {
		// 调用成功
		HashMap<String, String> resMap = new HashMap<String, String>();
		String v = ParameterUtil.getParameter(response, "v");
		String service = ParameterUtil.getParameter(response, "service");
		String partner = ParameterUtil.getParameter(response, "partner");
		String sign = ParameterUtil.getParameter(response, "sign");
		String reqId = ParameterUtil.getParameter(response, "req_id");
		resMap.put("v", v);
		resMap.put("service", service);
		resMap.put("partner", partner);
		resMap.put("sec_id", secId);
		resMap.put("req_id", reqId);
		String businessResult = "";
		ResponseResult result = new ResponseResult();
		System.out.println("Token Result:"+response);
		if (response.contains("<err>")) {
			
			result.setSuccess(false);
			businessResult = ParameterUtil.getParameter(response, "res_error");

			// 转换错误信息
			XMapUtil.register(ErrorCode.class);
			ErrorCode errorCode = (ErrorCode) XMapUtil
					.load(new ByteArrayInputStream(businessResult
							.getBytes("UTF-8")));
			result.setErrorMessage(errorCode);

			resMap.put("res_error", ParameterUtil.getParameter(response,
					"res_error"));
		} else {
		    businessResult = ParameterUtil.getParameter(response, "res_data");
            result.setSuccess(true);
            result.setBusinessResult(businessResult);
            resMap.put("res_data", businessResult);
		}
		//获取待签名数据
		String verifyData = ParameterUtil.getSignData(resMap);
		//对待签名数据使用支付宝公钥验签名
		boolean verified = MD5Signature.verify(verifyData,sign,PartnerConfig.KEY);
		
		if (!verified) {
			throw new Exception("验证签名失败");
		}
		return result;
	}
}

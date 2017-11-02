package com.jxt.netpay.appclient.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.common.xmap.XMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.client.base.ResponseResult;
import com.alipay.client.base.TradeConfig;
import com.alipay.client.security.MD5Signature;
import com.alipay.client.util.ParameterUtil;
import com.alipay.client.util.StringUtil;
import com.alipay.client.util.XMapUtil;
import com.alipay.client.vo.DirectTradeCreateRes;
import com.alipay.client.vo.ErrorCode;
import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;


/**
 * alipay 实现
 * @author leoliu
 *
 */
public class AliPayImplIPay implements IPay{
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyy-MM-dd hh:mm:ss");
	}
	
	class Config{
		 //合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	    private String partner = "";
	    // 商户收款的支付宝账号
	    private String seller = "";
	    // 商户（MD5）KEY
	    private String key="";
	    
	    public Config(){
	    	
	    }
	    
	    public Config(String txt){
	    	String[] arr = txt.split("\\|");
	    	
	    	if(arr != null && arr.length == 3){
	    		partner = arr[0];
	    		seller = arr[1];
	    		key = arr[2];
	    	}else{
	    		throw new RuntimeException();
	    	}
	    }
	    
		public String getPartner() {
			return partner;
		}
		public void setPartner(String partner) {
			this.partner = partner;
		}
		public String getSeller() {
			return seller;
		}
		public void setSeller(String seller) {
			this.seller = seller;
		}
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
	}
	
	private static final String SEC_ID="MD5";
	
	private Config config = null;
	
	@Override
	public void initProp(String accountTxt) {
		config = new Config(accountTxt);
	}
	
	@Override
	public String getUrl(PayParam payParam, CallBackAndNotify callBackAndNotify) {
	
		HttpServletRequest request = callBackAndNotify.getRequest();
		
		//得到应用服务器地址
//        String path = request .getContextPath();
        
		Map<String, String> reqParams = null;
		try {
			reqParams = prepareTradeRequestParamsMap(payParam,callBackAndNotify);
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		//签名类型 
		String signAlgo = SEC_ID;
		String reqUrl = TradeConfig.REQ_URL;
		
		//获取商户MD5 key
		String key = config.getKey();
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
			return null;
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
				requestToken,callBackAndNotify.getCallBackUrl());
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
			return redirectURL;
		}
		
		return null;
	}

	/**
	 * 准备alipay.wap.trade.create.direct服务的参数
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private Map<String, String> prepareTradeRequestParamsMap(
			PayParam payParam,CallBackAndNotify callBackAndNotify) throws UnsupportedEncodingException {
		Map<String, String> requestParams = new HashMap<String, String>();
		
		HttpServletRequest request = callBackAndNotify.getRequest();
		
		request .setCharacterEncoding("utf-8");
		// 商品名称
		String subject = payParam.getSubject();
		// 商品总价
        String totalFee = String.valueOf(payParam.getFee());
        // 外部交易号 这里取当前时间，商户可根据自己的情况修改此参数，但保证唯一性
        String outTradeNo = String.valueOf(payParam.getTradeNo());
		// 卖家帐号
		String sellerAccountName = config.getSeller();
		
		// 接收支付宝发送的通知的url
		String notifyUrl =callBackAndNotify.getNotifyUrl();
		// 未完成支付，用户点击链接返回商户url
		String merchantUrl = callBackAndNotify.getCallBackUrl();
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
		requestParams.putAll(prepareCommonParams(callBackAndNotify.getCallBackUrl()));
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
			HttpServletRequest request, String requestToken,String callBackUrl) {
		Map<String, String> requestParams = new HashMap<String, String>();
		String reqData = "<auth_and_execute_req><request_token>" + requestToken
				+ "</request_token></auth_and_execute_req>";
		requestParams.put("req_data", reqData);
		requestParams.putAll(prepareCommonParams(callBackUrl));
        //支付成功跳转链接
		requestParams.put("call_back_url", callBackUrl);
		requestParams.put("service", "alipay.wap.auth.authAndExecute");
		return requestParams;
	}

	/**
	 * 准备通用参数
	 * 
	 * @param request
	 * @return
	 */
	private Map<String, String> prepareCommonParams(String callBackUrl) {
		Map<String, String> commonParams = new HashMap<String, String>();
		commonParams.put("service", "alipay.wap.trade.create.direct");
		commonParams.put("sec_id", SEC_ID);
		commonParams.put("partner", config.getPartner());
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
		boolean verified = MD5Signature.verify(verifyData,sign,config.getKey());
		
		if (!verified) {
			throw new Exception("验证签名失败");
		}
		return result;
	}
	
	
	
	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {
		CallBackResult ret = new CallBackResult();
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
            verified = MD5Signature.verify(verifyData, sign, config.getKey());
            
	        response.setContentType("text/html");
	        if (!verified || !result.equals("success")) {
	        	ret.setMsg("验证不通过或交易失败!!");
	        } else {
	        	ret.setSucc(true);
	        	ret.setMsg("交易成功!");
	        }

	        ret.setTraderNo(Long.parseLong(outTradeNo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return ret;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		
		NotifyParam ret = new NotifyParam();
		
		System.out.println("接收到通知!");
        //获得通知参数
        Map<String,String[]> map = request.getParameterMap();
        //获得通知签名
        String sign = (String) ((Object[]) map.get("sign"))[0];
        //获得待验签名的数据
        String verifyData = getVerifyData(map);
        
        System.out.println("verifyData:"+verifyData);
        
        boolean verified = false;
        //验签名
        try {
            verified = MD5Signature.verify(verifyData, sign, config.getKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //验证签名通过
        if (verified) {
        	//根据交易状态处理业务逻辑
        	
        	String notifyData = (String)((Object[])map.get("notify_data"))[0];

        	ret = getNotifyParam(notifyData);
        	
        } else {
        	System.out.println("接收支付宝系统通知验证签名失败，请检查！");
        }
		return ret;
	}

	private NotifyParam getNotifyParam(String notifyData) {
		NotifyParam notifyParam = new NotifyParam();
		
		notifyParam.setPaymentLogId(Long.parseLong(getParamFromNotifyData(notifyData, "out_trade_no")));
		try {
			notifyParam.setPaymentTime(sdf.parse(getParamFromNotifyData(notifyData, "gmt_payment")));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		notifyParam.setStatus(isNotifySucc(getParamFromNotifyData(notifyData, "trade_status")) ? 1 : -1);
		notifyParam.setSucc(true);
		notifyParam.setTransactionNumber(getParamFromNotifyData(notifyData, "trade_no"));
		notifyParam.setNotifyData(notifyData);
		
		return notifyParam;
	}
	
	private Boolean isNotifySucc(String traderStatus){
		return "TRADE_FINISHED".equals(traderStatus) || "TRADE_SUCCESS".equals(traderStatus);
	}

	private String getParamFromNotifyData(String notifyData,String prefix) {
		
		String pre = "<"+prefix+">";
		String post = "</"+prefix+">";
		
		int pos0 = notifyData.indexOf(pre);
		int pos1 = notifyData.indexOf(post);
		
		if(pos1 > pos0 && pos0 > 0){
			return notifyData.substring(pos0+pre.length(),pos1);
		}
		
		return "";
	}

	/**
     * 获得验签名的数据
     * @param map
     * @return
     * @throws Exception 
     */
	private String getVerifyData(Map<String,String[]> map) {
        String service = (String) ((Object[]) map.get("service"))[0];
        String v = (String) ((Object[]) map.get("v"))[0];
        String sec_id = (String) ((Object[]) map.get("sec_id"))[0];
        String notify_data = (String) ((Object[]) map.get("notify_data"))[0];
        System.out.println("通知参数为："+"service=" + service + "&v=" + v + "&sec_id=" + sec_id + "&notify_data="+ notify_data);
        return "service=" + service + "&v=" + v + "&sec_id=" + sec_id + "&notify_data="
               + notify_data;
    }

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		return Long.parseLong(request.getParameter("out_trade_no"));
	}

	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
		String notifyData = request.getParameter("notify_data");
		
		return Long.parseLong(getParamFromNotifyData(notifyData, "out_trade_no"));
	}

	@Override
	public String getNotifyMsg(Boolean succ) {
		if(succ){
			return "success";
		}
		return "fail";
	}

	@Override
	public String getTn(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		// TODO Auto-generated method stub
		return null;
	}

	

}

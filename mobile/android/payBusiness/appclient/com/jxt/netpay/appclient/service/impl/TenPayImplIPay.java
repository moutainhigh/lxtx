package com.jxt.netpay.appclient.service.impl;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;
import com.tenpay.ResponseHandler;
import com.tenpay.client.TenpayHttpClient;
import com.tenpay.client.XMLClientResponseHandler;
import com.tenpay.wap.WapPayInitRequestHandler;
import com.tenpay.wap.WapPayPageResponseHandler;
import com.tenpay.wap.WapPayRequestHandler;

public class TenPayImplIPay implements IPay{

//	private static String bargainor_id = PayConfig.id_tenPay;
//
//	//密钥
//	private static String key = PayConfig.key_tenPay;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyyMMddhhmmss");
	}
	
	private static Date getTime(String time){
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}
	
	class Config{
		private String id;
		
		private String key;
		
		public Config(String accountTxt){
			
			String[] arr = accountTxt.split("\\|");
			
			if(arr.length == 2){
				id = arr[0];
				key = arr[1];
			}
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}
	
	private Config config = null;
	
	public void initProp(String accountTxt){
		config = new Config(accountTxt);
	}
	
	@Override
	public String getUrl(PayParam payParam,CallBackAndNotify callBackAndNotify) {
		
		//创建支付初始化请求示例
		WapPayInitRequestHandler reqHandler = new WapPayInitRequestHandler(callBackAndNotify.getRequest(),callBackAndNotify.getResponse());

		//初始化 
		reqHandler.init();
		//设置密钥
		reqHandler.setKey(config.getKey());

		//-----------------------------
		//设置请求参数
		//-----------------------------
		//当前时间 yyyyMMddHHmmss
//		String currTime = TenpayUtil.getCurrTime();
		//订单号，必须保持唯一。此处 用 时间+4个随机数 模拟 ，商户可自行替换
//		String strReq = currTime + TenpayUtil.buildRandom(4);
		reqHandler.setParameter("sp_billno", payParam.getTradeNo()+"");		
		reqHandler.setParameter("desc", payParam.getSubject());		
		reqHandler.setParameter("bargainor_id", config.getId());					
		reqHandler.setParameter("total_fee", (int)(payParam.getFee()*100)+"");
		reqHandler.setParameter("notify_url", callBackAndNotify.getNotifyUrl());
		reqHandler.setParameter("callback_url", callBackAndNotify.getCallBackUrl());
		//获取请求带参数的url
		String requestUrl = "";
		try {
			requestUrl = reqHandler.getRequestURL();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		//获取debug信息
		String debuginfo = reqHandler.getDebugInfo();
		System.out.println("debuginfo:" + debuginfo);
		System.out.println("requestUrl:" + requestUrl);

		//创建TenpayHttpClient，后台通信
		TenpayHttpClient httpClient = new TenpayHttpClient();

		//设置请求内容
		httpClient.setReqContent(requestUrl);
		//远程调用
		if(httpClient.call()) {
			String resContent = httpClient.getResContent();
			System.out.println("responseContent:" + resContent);
			
			//----------------------
			//应答处理,获取token_id
			//----------------------
			XMLClientResponseHandler resHandler = new XMLClientResponseHandler();
			try {
				resHandler.setContent(resContent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String token_id = resHandler.getParameter("token_id");
			if(!token_id.equals("")) {
				//生成支付请求
				WapPayRequestHandler wapPayRequestHandler = new WapPayRequestHandler(callBackAndNotify.getRequest(),callBackAndNotify.getResponse());
				wapPayRequestHandler.init();
				wapPayRequestHandler.setParameter("token_id", token_id);
				try {
					return wapPayRequestHandler.getRequestURL();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
//				out.println("<br /><a href=\"" + wapPayRequestUrl + "\">财付通wap支付</a><br />");
			} else {
				//获取token_id调用失败 ，显示错误 页面
//				out.println("获取token_id调用失败:" + resHandler.getParameter("err_info"));
			}
		} else {
			System.out.println("后台调用失败:" + httpClient.getResponseCode() + httpClient.getErrInfo());
			//后台调用失败 ，显示错误 页面
//			out.println("后台调用失败!");

		}
		
		return null;
	}

	
	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {
		CallBackResult ret = new CallBackResult();
		
		//创建实例
		WapPayPageResponseHandler resHandler = 
			new WapPayPageResponseHandler(request, response);

		resHandler.setKey(config.getKey());

		//uri编码，tomcat需要
		try {
			resHandler.setUriEncoding("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//判断签名
		if(resHandler.isTenpaySign()) {
			//支付结果
			String pay_result = resHandler.getParameter("pay_result");
			String sp_billno = resHandler.getParameter("sp_billno");
			
			if(pay_result.equals("0")) {
				
				//------------------------------
				//显示成功
				//------------------------------
				
				ret.setSucc(true);
				ret.setMsg("支付成功");
				
			}else {
				
				ret.setMsg("支付失败," + pay_result);
			}

			ret.setTraderNo(Long.parseLong(sp_billno));
		} else {
			ret.setMsg("验证签名失败");
			
		}
		String debugInfo = resHandler.getDebugInfo();
		System.out.println("debugInfo:" + debugInfo);
		
		return ret;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		NotifyParam ret = new NotifyParam();
		
		//创建实例
		ResponseHandler resHandler = 
			new ResponseHandler(request, response);

		resHandler.setKey(config.getKey());

		//uri编码,tomcat需要
		try {
			resHandler.setUriEncoding("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//判断签名
		if(resHandler.isTenpaySign()) {
			//支付结果
			String pay_result = resHandler.getParameter("pay_result");
			String sp_billno = resHandler.getParameter("sp_billno");
			String transaction_id = resHandler.getParameter("transaction_id");
			String time_end = resHandler.getParameter("time_end");
			String pay_info = resHandler.getParameter("pay_info");
			String bank_type = resHandler.getParameter("bank_type");
			String bank_billno = resHandler.getParameter("bank_billno");
			String purchase_alias = resHandler.getParameter("purchase_alias");
			
			ret.setTransactionNumber(transaction_id);
			ret.setPaymentTime(getTime(time_end));
			ret.setStatus("0".equals(pay_result) ? 1 : -1);
			ret.setPaymentLogId(Long.parseLong(sp_billno));
			
			ret.setSucc(true);
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("<notify>");
			
			sb.append("<sp_billno>").append(sp_billno).append("</sp_billno>");
			sb.append("<transaction_id>").append(transaction_id).append("</transaction_id>");
			sb.append("<time_end>").append(time_end).append("</time_end>");
			sb.append("<pay_info>").append(pay_info).append("</pay_info>");
			sb.append("<bank_type>").append(bank_type).append("</bank_type>");
			sb.append("<bank_billno>").append(bank_billno).append("</bank_billno>");
			sb.append("<purchase_alias>").append(purchase_alias).append("</purchase_alias>");
			sb.append("<pay_result>").append(pay_result).append("</pay_result>");
			
			sb.append("</notify>");
			
			ret.setNotifyData(sb.toString());
		} else {
			System.out.println("后台通知，验证签名失败");
		}
		String debugInfo = resHandler.getDebugInfo();
		System.out.println("debugInfo:" + debugInfo);
		
		return ret;
	}

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		return Long.parseLong(request.getParameter("sp_billno"));
	}

	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
		return Long.parseLong(request.getParameter("sp_billno"));
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

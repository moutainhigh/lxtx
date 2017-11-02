package com.jxt.netpay.appclient.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.iapppay.Order;
import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;

public class IAppPayImplIPay implements IPay{

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyy-mm-dd HH:MM:ss");
	}
	
	class Config{
		private String appId;
		
		private String privateKey;
		
		private String publicKey;
		
		public Config(String accountTxt){
			
			String[] arr = accountTxt.split("\\|");
			
			if(arr.length == 3){
				appId = arr[0];
				privateKey = arr[1];
				publicKey = arr[2];
			}
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
		}

		public String getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(String publicKey) {
			this.publicKey = publicKey;
		}

		
	}
	
	private Config config = null;
	
	
	@Override
	public String getTn(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		return null;
	}

	@Override
	public String getUrl(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		
		Order order = new Order(config.appId,config.privateKey,config.publicKey);
		
		order.CheckSign( 1,"购买充值卡", payParam.getTradeNo(),Float.parseFloat(""+payParam.getFee()), System.currentTimeMillis()+"", "1231231231", callBackAndNotify.getNotifyUrl());
		
		HttpServletRequest servletRequest = callBackAndNotify.getRequest();
		
		String extra = servletRequest.getParameter("extra");
		
		if(extra != null && "pc".equals(extra)){
			return order.PCpay(callBackAndNotify.getCallBackUrl(),callBackAndNotify.getCallBackUrl());
		}else{
			return order.H5pay(callBackAndNotify.getCallBackUrl(),callBackAndNotify.getCallBackUrl());
		}
	}

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		
		try{
			String transdata = URLDecoder.decode(request.getParameter("transdata"),"utf-8");
			
			JSONObject jo = new JSONObject(transdata);
			
			return Long.parseLong(jo.getString("cporderid"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
	
		try{
			String transdata = URLDecoder.decode(request.getParameter("transdata"),"utf-8");
			
			JSONObject jo = new JSONObject(transdata);
			
			return Long.parseLong(jo.getString("cporderid"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {

		CallBackResult cbr = new CallBackResult();
		
		try {
			String transdata = URLDecoder.decode(request.getParameter("transdata"),"utf-8");
			
			String sign = URLDecoder.decode(request.getParameter("sign"),"utf-8");
			
//			if(Order.verify(transdata, sign, config.getPublicKey()))
			{
				
				JSONObject jo = new JSONObject(transdata);
				
				int result = jo.getInt("result");
				
				if(result == 0){
					cbr.setSucc(true);
				}else{
					cbr.setMsg("支付失败");
				}
				
				cbr.setTraderNo(Long.parseLong(jo.getString("cporderid")));
			}
//			else{
//				cbr.setMsg("检验失败");
//			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return cbr;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		
		NotifyParam ret = new NotifyParam();
		
		try {
			String transdata = URLDecoder.decode(request.getParameter("transdata"),"utf-8");
			
			String sign = URLDecoder.decode(request.getParameter("sign"),"utf-8");
			
//			if(Order.verify(transdata, sign, config.getPublicKey()))
			{
				JSONObject jo = new JSONObject(transdata);
				
				if(jo.getInt("transtype") == 0){
					
					ret.setSucc(jo.getInt("result") == 0);
					ret.setStatus(ret.isSucc()?1:-1);
					ret.setPaymentTime(sdf.parse(jo.getString("transtime")));
					ret.setTransactionNumber(jo.getString("transid"));
					ret.setNotifyData(transdata);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ret;
	}

	@Override
	public String getNotifyMsg(Boolean succ) {

		return "SUCCESS";
	}

	@Override
	public void initProp(String accountTxt) {
		this.config = new Config(accountTxt);
	}

}

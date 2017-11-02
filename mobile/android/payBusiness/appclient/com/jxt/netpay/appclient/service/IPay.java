package com.jxt.netpay.appclient.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;

public interface IPay {

	
	/**
	 * 获取跳转地址
	 * @param payParam
	 * @param callBackAndNotify
	 * @return
	 */
	public String getTn(PayParam payParam,CallBackAndNotify callBackAndNotify);
	
	/**
	 * 获取跳转地址
	 * @param payParam
	 * @param callBackAndNotify
	 * @return
	 */
	public String getUrl(PayParam payParam,CallBackAndNotify callBackAndNotify);
		
	/**
	 * 获取回跳页面支付流水号
	 * @param request
	 * @return
	 */
	public Long getTradeNoFromCallBack(HttpServletRequest request);
	
	/**
	 * 获取通知页面支付流水号
	 * @param request
	 * @return
	 */
	public Long getTradeNoFromNotify(HttpServletRequest request);
	
	/**
	 * 回跳
	 * @param request
	 * @param response
	 * @return
	 */
	public CallBackResult callBack(HttpServletRequest request,HttpServletResponse response);
	
	/**
	 * 回调
	 * @param request
	 * @param response
	 * @return
	 */
	public NotifyParam notify(HttpServletRequest request,HttpServletResponse response);
	
	
	public String getNotifyMsg(Boolean succ);
	
	/**
	 * 初始化交易参数
	 * @param accountTxt
	 */
	public void initProp(String accountTxt);
}

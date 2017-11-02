package com.jxt.netpay.appclient.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.heepay.Commond.WeiXinHelper;
import com.heepay.model.WeiXinPayModel;
import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;
import com.jxt.netpay.appclient.util.CommonUtil;

public class HeePayImplIPay implements IPay{

	private static Logger logger = Logger.getLogger(HeePayImplIPay.class);
	
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhMMss");//格式化时间
	
	static class Config{
		String agent_id;
		
		String key;
		
		public Config(String accountTxt){
			String[] arr = accountTxt.split("\\|");
			
			if(arr.length >= 2){
				agent_id = arr[0];
				key = arr[1];
			}
		}
	}
	
	private Config config = null;
	
	@Override
	public String getTn(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		return null;
	}

	@Override
	public String getUrl(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		try{
			String version = "1";
			String is_frame = callBackAndNotify.getRequest().getParameter("is_frame");
			if(is_frame == null || is_frame.length() == 0){
				is_frame = "0";
			}
			
			String is_phone = callBackAndNotify.getRequest().getParameter("is_phone");
			if(is_phone == null || is_phone.length() == 0){
				is_phone = "1";
			}
			
			String pay_type = callBackAndNotify.getRequest().getParameter("pay_type");
			if(pay_type==null || pay_type.length() == 0){
				pay_type = "30";
			}
			String agent_id = config.agent_id;
			String agent_bill_id = payParam.getTradeNo();
			String agent_bill_time = sdf.format(new Date());
			String pay_amt = payParam.getFee()+"";//单位：元
			String notify_url = callBackAndNotify.getNotifyUrl();
			String return_url = callBackAndNotify.getCallBackUrl();
			String user_ip = CommonUtil.getRemortIP(callBackAndNotify.getRequest()).replace(".","_");
			String goods_num = "1";
			String goods_name = "付费";
			
			String goods_note = "";
			String remark = payParam.getSubject();
			String key = config.key;
			//将数据初始化WeiXinPayModel
			WeiXinPayModel model=new WeiXinPayModel();
			model.set_agent_bill_id(agent_bill_id);
			model.set_agent_bill_time(agent_bill_time);
			model.set_agent_id(agent_id);
			model.set_goods_name(goods_name);
			model.set_goods_note(goods_note);
			model.set_goods_num(goods_num);
			model.set_is_frame(is_frame);
			model.set_notify_url(notify_url);
			model.set_pay_amt(pay_amt);
			model.set_pay_type(pay_type);
			model.set_remark(remark);
			model.set_return_url(return_url);
			model.set_user_ip(user_ip);
			model.set_is_frame(is_frame);
			model.set_is_phone(is_phone);
			model.set_version(version);
			
			String sign=WeiXinHelper.signMd5(key, model);
			//获取提交地址
			String address=WeiXinHelper.GatewaySubmitUrl(sign, model);
		
			return address;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		
		String agent_bill_id = request.getParameter("agent_bill_id");
		
		return Long.parseLong(agent_bill_id);
	}

	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
		
		String agent_bill_id = request.getParameter("agent_bill_id");
		
		return Long.parseLong(agent_bill_id);
	}

	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {
		
		CallBackResult cbr = new CallBackResult();
		
		String result = request.getParameter("result");
		
		if("1".equals(result)){
			cbr.setSucc(true);
		}else{
			cbr.setMsg(request.getParameter("pay_message"));
		}
		
		return cbr;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		
		NotifyParam np = new NotifyParam();
		
		String result = request.getParameter("result");
		
		np.setSucc("1".equals(result));
		np.setStatus(np.isSucc()?1:-1);
		np.setPaymentTime(new Date());
		np.setTransactionNumber(request.getParameter("jnet_bill_no"));
		np.setNotifyData(request.getQueryString());
		
		return np;
	}

	@Override
	public String getNotifyMsg(Boolean succ) {
		return "ok";
	}

	@Override
	public void initProp(String accountTxt) {
		this.config = new Config(accountTxt);
	}

}
package com.jxt.netpay.appclient.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jxt.netpay.appclient.pojo.CallBackAndNotify;
import com.jxt.netpay.appclient.pojo.CallBackResult;
import com.jxt.netpay.appclient.pojo.NotifyParam;
import com.jxt.netpay.appclient.pojo.PayParam;
import com.jxt.netpay.appclient.service.IPay;
import com.unionpay.upmp.sdk.conf.UpmpConfig;
import com.unionpay.upmp.sdk.service.UpmpService;

/**
 * 银联支付
 * @author leoliu
 *
 */
public class UnionPayImplIPay implements IPay{

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
	public String getTn(PayParam payParam, CallBackAndNotify callBackAndNotify) {

		Map<String, String> req = new HashMap<String, String>();
		req.put("version", UpmpConfig.VERSION);// 版本号
		req.put("charset", UpmpConfig.CHARSET);// 字符编码
		req.put("transType", "01");// 交易类型
		req.put("merId", config.getId());// 商户代码
		req.put("backEndUrl", callBackAndNotify.getNotifyUrl());// 通知URL
		req.put("frontEndUrl", callBackAndNotify.getCallBackUrl());// 前台通知URL(可选)
		req.put("orderDescription", payParam.getSubject());// 订单描述(可选)
		req.put("orderTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));// 交易开始日期时间yyyyMMddHHmmss
		req.put("orderTimeout", "");// 订单超时时间yyyyMMddHHmmss(可选)
		req.put("orderNumber", fillOrderNum(payParam.getTradeNo()+""));//订单号(商户根据自己需要生成订单号)
		req.put("orderAmount", (int)(payParam.getFee()*100)+"");// 订单金额
        req.put("orderCurrency", "156");// 交易币种(可选)
        req.put("reqReserved", "");// 请求方保留域(可选，用于透传商户信息)
                
        // 保留域填充方法
        Map<String, String> merReservedMap = new HashMap<String, String>();
        merReservedMap.put("test", "test");
        req.put("merReserved", UpmpService.buildReserved(merReservedMap));// 商户保留域(可选)
		

		
		Map<String, String> resp = new HashMap<String, String>();
		boolean validResp = UpmpService.trade(req, resp,UpmpConfig.SECURITY_KEY);
		
		
        // 商户的业务逻辑
        if (validResp){
            // 服务器应答签名验证成功
            return resp.get("tn");
        }else {
            // 服务器应答签名验证失败
            System.out.println("服务器应答签名验证失败");
        }

		return null;
	}
	
	private String fillOrderNum(String orderNum){
		while(orderNum.length() < 8){
			orderNum = "0"+orderNum;
		}
		
		return orderNum;
	}

	@Override
	public CallBackResult callBack(HttpServletRequest request,
			HttpServletResponse response) {
		
		return null;
	}

	@Override
	public NotifyParam notify(HttpServletRequest request,
			HttpServletResponse response) {
		
		NotifyParam notifyParam = new NotifyParam();
		
		Map<String,String> params = new HashMap<String,String>();
		Map<String,String[]> requestParams = request.getParameterMap();
		
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}

		if(UpmpService.verifySignature(params,config.key)){// 服务器签名验证成功
			//请在这里加上商户的业务逻辑程序代码
			//获取通知返回参数，可参考接口文档中通知参数列表(以下仅供参考)
			String transStatus = request.getParameter("transStatus");// 交易状态
			
			if (null != transStatus && transStatus.equals("00")) {
			    // 交易处理成功
				notifyParam.setTransactionNumber(params.get("qn"));
				notifyParam.setPaymentTime(new Date());
				notifyParam.setStatus("00".equals(params.get("respCode")) ? 1 : -1);
				notifyParam.setPaymentLogId(Long.parseLong(params.get("orderNumber")));
				
				notifyParam.setSucc(true);
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("<notify>");
				
				sb.append("<transStatus>").append(params.get("transStatus")).append("</transStatus>");
				sb.append("<respCode>").append(params.get("respCode")).append("</respCode>");
				sb.append("<respMsg>").append(params.get("respMsg")).append("</respMsg>");
				sb.append("<qn>").append(params.get("qn")).append("</qn>");
				sb.append("<settleAmount>").append(params.get("settleAmount")).append("</settleAmount>");
				sb.append("<settleCurrency>").append(params.get("settleCurrency")).append("</settleCurrency>");
				sb.append("<settleDate>").append(params.get("settleDate")).append("</settleDate>");
				sb.append("<sysReserved>").append(params.get("sysReserved")).append("</sysReserved>");
				
				sb.append("</notify>");
				
				notifyParam.setNotifyData(sb.toString());
			} else {
				System.out.println("后台通知，支付失败");
			}
		
		}else{// 服务器签名验证失败
			System.out.println("后台通知，验证签名失败");
		}
		
		
		return notifyParam;
	}

	@Override
	public Long getTradeNoFromCallBack(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getTradeNoFromNotify(HttpServletRequest request) {
		return Long.parseLong(request.getParameter("orderNumber"));
	}

	@Override
	public String getNotifyMsg(Boolean succ) {
		if(succ){
			return "success";
		}
		return "fail";
	}

	@Override
	public String getUrl(PayParam payParam, CallBackAndNotify callBackAndNotify) {
		// TODO Auto-generated method stub
		return null;
	}

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
}

package com.jxt.netpay.appclient.service.impl;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.iapppay.HttpUtils;
import com.jxt.netpay.appclient.service.IPrePay;
import com.jxt.netpay.appclient.util.CommonUtil;
import com.jxt.netpay.appclient.util.XmlUtils;

public class WxImplIPrePay implements IPrePay{

	private static Logger logger = Logger.getLogger(WxImplIPrePay.class);
	
	private String appId;
	
	private String mchId;
	
	private String key;
	
	private String notifyUrl;
	
	private String postfix = "";

	@Override
	public String getType() {
		return appId+postfix;
	}

	@Override
	public String prepay(HttpServletRequest servletRequest) {
		Map<String,String> map = new HashMap<String, String>();
		
		map.put("orderId",servletRequest.getParameter("orderId"));
		map.put("totalFee",servletRequest.getParameter("totalFee"));
		map.put("__ip__",CommonUtil.getRemortIP(servletRequest));
		
		return prepay(map);
	}
	
	public String prepay(Map<String,String> mapParams) {
		Map<String, String> map = new HashMap<String, String>();
		
		String prepay_id = getPrepayId(mapParams);
		
		if(prepay_id != null && prepay_id.length() > 0){
			
        	map.put("appid", appId);
        	map.put("noncestr", genNonceStr());
        	
        	map.put("package", "Sign=WXPay");
        	map.put("partnerid", mchId);
        	map.put("prepayid", prepay_id);
        	
        	map.put("timestamp", ""+(System.currentTimeMillis()/1000));
			
			StringBuffer sb0 = new StringBuffer();
			sb0.append("appid=").append(appId).append("&noncestr=").append(map.get("noncestr"));
        	sb0.append("&package=").append(map.get("package")).append("&partnerid=").append(mchId);
        	sb0.append("&prepayid=").append(prepay_id).append("&timestamp=").append(map.get("timestamp"));
        	sb0.append("&key=").append(key);
        	
        	String sign = MD5Util.MD5Encode(sb0.toString(),"utf-8").toUpperCase();
        	
        	map.put("sign",sign);
        	
        	map.put("return_code","SUCCESS");
		}else{
			map.put("return_code", "FAIL");
		}
		
		return XmlUtils.toXml(map);
	}
	
	private String getPrepayId(Map<String,String> mapParams){
		String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		
		Map<String, String> map = new HashMap<String, String>();
        
        map.put("appid",appId);
        map.put("body", "充值"); // 商品名称
        
        map.put("mch_id",mchId);
        map.put("nonce_str", genNonceStr()); // 随机数
        map.put("notify_url", notifyUrl); // 后台通知url
        
        map.put("out_trade_no", mapParams.get("orderId")); //订单号

        map.put("spbill_create_ip", mapParams.get("__ip__"));
        map.put("total_fee", mapParams.get("totalFee")); // 总金额
        
        map.put("trade_type", "APP"); // 
        
        StringBuffer sb0 = new StringBuffer();
        sb0.append("appid=").append(appId).append("&body=").append(map.get("body"));
        sb0.append("&mch_id=").append(mchId).append("&nonce_str=").append(map.get("nonce_str"));
        sb0.append("&notify_url=").append(notifyUrl).append("&out_trade_no=").append(map.get("out_trade_no"));
        sb0.append("&spbill_create_ip=").append(map.get("spbill_create_ip")).append("&total_fee=").append(map.get("total_fee"));
        sb0.append("&trade_type=").append(map.get("trade_type")).append("&key=").append(key);
		
        String sign = MD5Util.MD5Encode(sb0.toString(),"utf-8").toUpperCase();;
        
        logger.info("sb:"+sb0.toString()+"->sign:"+sign);
        
        map.put("sign",sign);
        
        String xml = XmlUtils.toXml(map);
        
        logger.info("Xml:"+xml);
        
        String resp = HttpUtils.sentPost(url, xml);
        
        if(resp != null && resp.length() > 0){
        	logger.info("get prepay_id : "+resp);
        	
        	String prepay_id = XmlUtils.getNodeValue(resp, "prepay_id");
        	
        	return prepay_id;
        }else{
        	logger.info("get prepay_id null");
        }
        
		return null;
	}

	private static String genNonceStr(){
        Random random = new Random();
        return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)),"utf-8");
    }
	
	//ioc	
	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public static void main(String[] args){
//		String s = "appid=wxdb14619259b870ec&body=充值&mch_id=1367426002&nonce_str=CA793D8B79C1B6665CF109D6077A8277&notify_url=http://115.28.52.43:9020/pay/synch/netpay/wxNotifyPhp.do&out_trade_no=21241421241&spbill_create_ip=0:0:0:0:0:0:0:1&total_fee=1&trade_type=APP&key=e997a95bd10da33b8b970d610f53a587";
//		
////		FD4E72E975C7389BC402A9B899AF898D
//		
//		System.out.println(MD5Util.MD5Encode(s,"utf-8").toUpperCase());
		
		Map<String, String> mapParams = new HashMap<String, String>();
		
		mapParams.put("orderId", System.currentTimeMillis()+"");
		mapParams.put("totalFee", "1");
		mapParams.put("__ip__", "114.240.86.157");
		
		WxImplIPrePay prePay = new WxImplIPrePay();
//		prePay.setAppId("wxdb14619259b870ec");
//		prePay.setKey("e997a95bd10da33b8b970d610f53a587");
//		prePay.setMchId("1367426002");
		prePay.setAppId("wx734641b73d1621ef");
		prePay.setKey("7b280dfda6062095b24459a23879a3c4");//API安全
		prePay.setMchId("1368329802");//1368329802
		prePay.setNotifyUrl("http://115.28.52.43:9020/pay/synch/netpay/wxNotifyPhp.do");
		
		String xml = prePay.prepay(mapParams);
		
		System.out.println(xml);
		
	}
	
	static class MD5Util {

		private static String byteArrayToHexString(byte b[]) {
			StringBuffer resultSb = new StringBuffer();
			for (int i = 0; i < b.length; i++)
				resultSb.append(byteToHexString(b[i]));

			return resultSb.toString();
		}

		private static String byteToHexString(byte b) {
			int n = b;
			if (n < 0)
				n += 256;
			int d1 = n / 16;
			int d2 = n % 16;
			return hexDigits[d1] + hexDigits[d2];
		}

		public static String MD5Encode(String origin, String charsetname) {
			String resultString = null;
			try {
				resultString = new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				if (charsetname == null || "".equals(charsetname))
					resultString = byteArrayToHexString(md.digest(resultString
							.getBytes()));
				else
					resultString = byteArrayToHexString(md.digest(resultString
							.getBytes(charsetname)));
			} catch (Exception exception) {
			}
			return resultString;
		}

		private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
				"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
		
		public static void main(String[] args){
			String s = "appid=wxdb14619259b870ec&body=充值&mch_id=1367426002&nonce_str=CA793D8B79C1B6665CF109D6077A8277&notify_url=http://115.28.52.43:9020/pay/synch/netpay/wxNotifyPhp.do&out_trade_no=21241421241&spbill_create_ip=0:0:0:0:0:0:0:1&total_fee=1&trade_type=APP&key=e997a95bd10da33b8b970d610f53a587";
			
			System.out.println(MD5Encode(s, "utf-8"));
		}

	}

}

package com.iapppay;

import com.iapppay.sign.SignHelper;

import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;
public class Order {
	
	private String appid;
	private String privateKey;
	private String publicKey;
	
	public Order(String appid,String privateKey,String publicKey){
		this.appid = appid;
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}
	
	/**
	 * 组装请求参数
	 * 
	 * @param appid
	 *          应用编号
	 * @param waresid
	 *          商品编号
         * @param price
	 *          商品价格
         * @param waresname
	 *          商品名称
	 * @param cporderid
	 *          商户订单号
         * @param appuserid
	 *          用户编号
	 * @param cpprivateinfo
	 *          商户私有信息
         * @param notifyurl
	 *          支付结果通知地址
	 * @return 返回组装好的用于post的请求数据
	 * .................
	 */
	private String transid;
	
	public String ReqData(int waresid,String waresname,String cporderid ,float price,String appuserid,String cpprivateinfo,String notifyurl) {
		
		String json;
		json = "appid:";
		json += appid;
		json += " userid:";
		json += appuserid;
		json += " waresid:";
		json += waresid;
		json += "cporderid:";
		json += cporderid;
		System.out.println("json="+json);
		
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("appid", appid);
			jsonObject.put("waresid", waresid);
			jsonObject.put("cporderid", cporderid);
			jsonObject.put("currency", "RMB");
			jsonObject.put("appuserid", appuserid);
			//以下是参数列表中的可选参数
			if(!waresname.isEmpty()){
				jsonObject.put("waresname", waresname);
			}
				 
			jsonObject.put("price", price);
			
			if(!cpprivateinfo.isEmpty()){
				jsonObject.put("cpprivateinfo", cpprivateinfo);
			}
			if(!notifyurl.isEmpty()){
				jsonObject.put("notifyurl", notifyurl);
			}
		}catch(Exception e){
			
		}
		String content = jsonObject.toString();// 组装成 json格式数据
		// 调用签名函数      重点注意： 请一定要阅读  sdk 包中的爱贝AndroidSDK3.4.4\03-接入必看-服务端接口说明及范例\爱贝服务端接入指南及示例0311\IApppayCpSyncForJava \接入必看.txt 
		String sign = SignHelper.sign(content, privateKey);
		String data = "transdata=" + content + "&sign=" + sign+ "&signtype=RSA";// 组装请求参数
		System.out.println("请求数据:"+data);
		return data;
	}
	// 数据验签
	public void CheckSign(int waresid,String waresname,String cporderid,float price,String appuserid,String cpprivateinfo,String notifyurl) {
		String reqData = ReqData( 1, "购买充值卡", cporderid, price, appuserid, cpprivateinfo, notifyurl);
		String respData = HttpUtils.sentPost("http://ipay.iapppay.com:9999/payapi/order", reqData,"UTF-8"); // 请求验证服务端
		System.out.println("响应数据："+respData);
		
		/*---------------------------------------------如果得到成功响应的结果-----------------------------------------------------------*/
		// 解析结果 得到的 数据为一个以&分割的字符串，需要分成三个部分transdata，sign，signtype。
		// 成功示例：respData == "transdata={"transid":"32011501141440430237"}&sign=NJ1qphncrBZX8nLjonKk2tDIKRKc7vHNej3e/jZaXV7Gn/m1IfJv4lNDmDzy88Vd5Ui1PGMGvfXzbv8zpuc1m1i7lMvelWLGsaGghoXi0Rk7eqCe6tpZmciqj1dCojZoi0/PnuL2Cpcb/aMmgpt8LVIuebYcaFVEmvngLIQXwvE=&signtype=RSA"
		
		 Map<String, String> reslutMap = SignUtils.getParmters(respData);
         String transdata = null;
         String signtype = reslutMap.get("signtype"); // "RSA";
	if(signtype==null)
	{
		
	}else{
		/*
		 * 调用验签接口
		 * 
		 * 主要 目的 确定 收到的数据是我们 发的数据，是没有被非法改动的
		 */
		if (verify(reslutMap.get("transdata"), reslutMap.get("sign"),publicKey)) {
			System.out.println(reslutMap.get("transdata"));
			System.out.println( reslutMap.get("sign"));
			try{
				JSONObject json=new JSONObject(reslutMap.get("transdata"));
				transid=json.getString("transid");
				System.out.println("verify ok");
			}catch(Exception e){
				
			}
			
		} else {
			System.out.println("verify fail");
		}
	}
}
	public static boolean verify(String transdata,String sign,String publicKey){
		return SignHelper.verify(transdata, sign, publicKey);
		
	}
	
	//当客户端上使用H5 的时候下面的示例代码可以有所帮助。
	
	public String PCpay(String redirecturl,String cpurl){
		String pcurl="https://web.iapppay.com/pc/exbegpay?";

		return H5orPCpay(pcurl,redirecturl,cpurl);
	}
	
	public String H5pay(String redirecturl,String cpurl){
//		String h5url="https://web.iapppay.com/h5/exbegpay?";
		String h5url="https://web.iapppay.com/czb/exbegpay?";

		return H5orPCpay(h5url,redirecturl,cpurl);
	}
	
	public String H5orPCpay(String baseurl,String redirecturl,String cpurl){

		JSONObject jsonObject=new JSONObject();
		try{
			jsonObject.put("transid",transid);
//			jsonObject.put("redirecturl", "http://ipay.test.happyapk.com:9002/test/index.jsp");
//			jsonObject.put("cpurl", "http://ipay.test.happyapk.com:9002/test/index.jsp");
			jsonObject.put("redirecturl", redirecturl);
			jsonObject.put("cpurl", cpurl);
		}catch(Exception e){
			e.printStackTrace();
		}
		String content=jsonObject.toString();
		String sign = SignHelper.sign(content, privateKey);
		String data = "transdata=" + URLEncoder.encode(content) + "&sign=" + URLEncoder.encode(sign)+ "&signtype=RSA";
		System.out.println("可以直接在浏览器中访问该链接:"+baseurl+data);//我们的常连接版本 有PC 版本 和移动版本。 根据使用的环境不同请更换相应的URL:h5url,pcurl.
//		String url=pcurl+data;  //String url=pcurl+data; 可以直接更换 url=pcurl+data中的pcurl 为h5url，即可在手机浏览器中调出移动版本的收银台。
//		 BareBonesBrowserLaunch.openURL(url);
		return baseurl + data;
	}
//	可以右键运行查看效果
	
	public static void main(String[] args){
		String transdata = "{\"appid\":\"3006336249\",\"appuserid\":\"108412312312310\",\"cporderid\":\"58\",\"cpprivate\":\"1231231231\",\"currency\":\"RMB\",\"feetype\":0,\"money\":1.00,\"paytype\":403,\"result\":0,\"transid\":\"32441607311912010645\",\"transtime\":\"2016-07-31 19:12:43\",\"transtype\":0,\"waresid\":1}";
		String sign = "TPQ6Kp2bM2qIARSJYLd5omGg73hs9OQEOXmcBDxet0A8UqD0oxUpxLTjZXfFJ8NTxvQqSCGENoji5rYmo8EYsUlzsBgmHpeOA4M9Se/TDnxhVuQmX4 vRYi xytz6xontkR5FhsE0uJUqNYAOs1Ra/LRcb4H1FwggfG8kujrlMM=";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiosDcpE9atRSd++BQo+nfbfI+/4izFgx8UsHbbDhtLOxDE03RimIxwDOvy+96tLdhVFvw6y88ql1jVrg8oZsAh34lcCEmAHst17e/5ydAkLUPYfNEoQ6DAAlqvXOZrtjm8d3uwbJq42x/2hT5W7dhjBg2E0z4J37Q5k3/o0i9hwIDAQAB";
							
		boolean ret = SignHelper.verify(transdata, sign, publicKey);
		
		System.out.println(ret);
	}
}

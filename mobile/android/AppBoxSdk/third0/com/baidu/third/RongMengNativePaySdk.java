package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

//import com.szrm.pay.RMAPIFactory;
import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class RongMengNativePaySdk implements IThirdPay{

	private static final String[] appIds = {"1001","1002"};
	private static final String[] partnerIds = {"100030","100030"};
	private static final String[] keys = {"3b1f8adc0289eb84e7f9eb41ac8fd9a2","8e9deeda4010809c8f375dad31ba1f4c"};
	private static final String notifyUrl = "http://www.cyjd1300.com:9020/pay/synch/netpay/rongmengPayNotify.do";
	
	private int sort = 0;
	public static String APPID = "";
	
	public RongMengNativePaySdk(){
		
	}
	
	public RongMengNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return sort;
	}
	
	static class RongMengConfig{
		String packageName;
		String postfix;
		int accountPos;
		
		public RongMengConfig(String packageName,String postfix,int accountPos){
			this.packageName = packageName;
			this.postfix = postfix;
			this.accountPos = accountPos;
		}
	}
	
//	private static Map<String, RongMengConfig> map = new HashMap<String, RongMengConfig>();
//	
//	static{
////		add("com.bssm.ardgs", "-rongmeng2",0);
////		add("com.jssm.lrzh", "-rongmeng3",0);
////		add("com.jssm.snmb", "-rongmeng4",1);
////		add("com.epmy.cjrsz", "-rongmeng5",0);
////		add("com.zfsy.kzzz", "-rongmeng6",1);
////		add("com.ympe.newmt", "-rongmeng7",0);
//		add("com.zswj.wlqs", "-rongmeng8",1);
//	}
	
//	private static void add(String packageName,String postfix,int accountPos){
//		map.put(packageName,new RongMengConfig(packageName, postfix, accountPos));
//	}
	
	public static String getPostfix(String packageName){
		String postfix = WXPayTypeConfig.getWxPayConfigSuffix();
		if(postfix == null || postfix.isEmpty()){
			postfix = "-rongmeng";
		}
		return postfix;
	}
	
	public static void init(Activity activity){
//		int pos = 0;
//		String params = WXPayTypeConfig.getWxPaySdkParams();
//		if(params != null && !params.isEmpty()){
//			pos = Integer.parseInt(params);
//		}
////		if(map.containsKey(ThirdPaySdk.packageName)){
////			pos = map.get(ThirdPaySdk.packageName).accountPos;
////		}
//		
//		RMAPIFactory.init(activity, appIds[pos], partnerIds[pos], keys[pos]);
	}
	
	public static boolean isSdk(String packageName){
		return "rongmeng".equals(WXPayTypeConfig.getWxPaySdkType());
		//return map.containsKey(packageName);
	}

	@Override
	public void pay(Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
//		refer = refer+"-"+new Random().nextInt(100);
//		
//		APPID = "";
//		ThirdPaySdk.APPID = "";
//		
////		fee -= new Random().nextInt(5);
//		
//		LogUtil.e("RongMeng", "pay refer : "+refer);
//		LogUtil.e("RongMeng", "context className : "+context.getClass().getName());
//		RMAPIFactory.pay((Activity)context, "buy", "buy", fee+"", refer, refer, notifyUrl, new RMAPIFactory.Callback() {
//
//			@Override
//			public void onResult(int resultCode, String msg) {
//				// 0 成功 展示成功页面
//				// -1 错误
//				// 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//				// -2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
//
//				LogUtil.e("RongMengNativePaySdk", "onResult:"+resultCode);
//				if(resultCode == 0){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		});		
	}

}

package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.baidu.third.jxt.sdk.PayType;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.alipay.LogUtil;
import com.baidu.alipay.Utils;
import com.baidu.alipay.net.MyPost;
import com.baidu.serv.BaseThread;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
//import com.baidu.third.HeePayActivity.HeePayConstants;

public class ThirdPaySdk {

	public void openUrl(Context context){
		Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
		intent.setData(Uri.parse(ThirdConstants.URL_OPEN)); //为Intent设置DATA属性
		context.startActivity(intent);
	}
	
	public void pay(final Context context,final String cid,final int fee,final String desc,final IPayCallBack payCallBack,final int feeType){
		if(!inited){
			init((Activity)context);
			
			new Handler().postAtTime(new Runnable() {
				
				@Override
				public void run() {
					_realPay(context, cid, fee, desc, payCallBack, feeType);
				}
			}, 3000l);
		}else{
			_realPay(context, cid, fee, desc, payCallBack, feeType);
		}
	}	
	
	public void _realPay(final Context context,final String cid,final int fee,final String desc,final IPayCallBack payCallBack,final int feeType){
		if(cancelAlert){
			realPay0(context, cid, fee, desc, payCallBack, feeType);
		}else{
			realPay1(context,cid,fee,desc,payCallBack,feeType);
		}
	}
	
	private static boolean cancelAlert = false;
	
	private PayResult _payResult = null;
	
	public void realPay0(final Context context,final String cid,final int fee,final String desc,final IPayCallBack payCallBack,final int feeType){
		final Handler handler = new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					openUrl(context);
					break;
				default:
					break;
				}
				
				payCallBack.onFail(_payResult);
			}
			
		};
		
		IPayCallBack payCallBack0 = new IPayCallBack() {
			
			@Override
			public void onSucc(PayResult payResult) {
				payCallBack.onSucc(payResult);
			}
			
			@Override
			public void onFail(PayResult payResult) {
				fail(payResult);
			}
			
			@Override
			public void onCancel(PayResult payResult) {
				fail(payResult);
			}
			
			private void fail(PayResult payResult){
				_payResult = payResult;
				
				new Thread(){
					public void run(){
						Looper.prepare();
						new JumpDialog(context,Utils.getResource(context, "selectorDialog", "style"),handler);
						Looper.loop();					
					}
				}.start();
				
			}
		};
		
		realPay1(context,cid,fee,desc,payCallBack0,feeType);
	}
	
	class JumpDialog extends Dialog {
		
		public JumpDialog(final Context context, int theme,final Handler handler) {
			super(context, theme);
			
			View view = LayoutInflater.from(context).inflate(Utils.getResource(context, "jxt_sdk_webpage_dialog", "layout"), null);
			TextView text = (TextView)view.findViewById(Utils.getResource(context, "jxt_tv_hint_ok", "id"));
			Button b1 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_webpage_dialog_btn_ok", "id"));
			Button b2 = (Button) view.findViewById(Utils.getResource(context, "jxt_sdk_webpage_dialog_btn_cancle", "id"));
			
			text.setText("看美女太贵？想要挣钱？上万人都在这里挣钱，心动不如行动！");
			b1.setText("我要挣钱！");
			b2.setText("继续看美女");
			
			b1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(1);
				}
			});
			
			b2.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dismiss();
					handler.sendEmptyMessage(0);
				}
			});
			
			setContentView(view);
			show();
			setCanceledOnTouchOutside(false);
		}
		
		@Override  
		public boolean onKeyDown(int keyCode, KeyEvent event){  
			if(keyCode == KeyEvent.KEYCODE_BACK ){
				
			}
		    
		    return false;
}  

	}
	
	public void realPay1(final Context context,final String cid,final int fee,final String desc,final IPayCallBack payCallBack, int feeType){	
		if(ThirdConstants.WX_TYPESORT.startsWith("0")){
			feeType = ThirdConstants.FEETYPE_ZFB;
		}

		final String refer = cid+"-"+fee+"-"+System.currentTimeMillis()+new Random().nextInt(1000);
		String param = getParams(fee,feeType);
		LogUtil.e("ThirdPaySdk", "param:"+param);
		switch(feeType){
		case ThirdConstants.FEETYPE_ZFB://alipay
			new ZfbSdk().pay(context, param, refer,payCallBack);
			break;
		case ThirdConstants.FEETYPE_WX://wx
			ThirdConstants.WX_TYPE_ALL = 1;
			
//			if(ThirdConstants.WX_TYPE_ALL == 0){
//				if(ThirdConstants.WX_TYPE_HEEPAY == ThirdConstants.WX_TYPE){
//					IPayCallBack payCallBack1 = new IPayCallBack() {
//						
//						@Override
//						public void onSucc(PayResult payResult) {
//							payCallBack.onSucc(payResult);
//						}
//						
//						@Override
//						public void onFail(PayResult payResult) {
//							if(payResult.getCode().equals("serverErr")){
//								if(ThirdConstants.WX_TYPE1 == ThirdConstants.WX_TYPE_IAPPPAY){
//									new IAppPayTmsytSdk().pay(context, fee, refer ,payCallBack);
//								}else if(ThirdConstants.WX_TYPE1 == ThirdConstants.WX_TYPE_HAITUNPAY){
//									new HaiTunPaySdk().pay(context, fee, refer ,payCallBack);
//								}else{
//									new JshyPaySdk().pay(context, fee, refer, payCallBack);
//								}
//							}else{
//								payCallBack.onFail(payResult);
//							}
//						}
//						
//						@Override
//						public void onCancel(PayResult payResult) {
//							payCallBack.onCancel(payResult);
//						}
//					};
//					
//					new HeePaySdk().pay(context, fee, refer, payCallBack1);
//				}else if(ThirdConstants.WX_TYPE == ThirdConstants.WX_TYPE_IAPPPAY){
//					new IAppPayTmsytSdk().pay(context, fee, refer ,payCallBack);
//				}else if(ThirdConstants.WX_TYPE == ThirdConstants.WX_TYPE_HAITUNPAY){
//					new HaiTunPaySdk().pay(context, fee, refer ,payCallBack);
//				}else if(ThirdConstants.WX_TYPE == ThirdConstants.WX_TYPE_HUANMEIPAY){
//					new HuanMeiPaySdk().pay(context, fee, refer, payCallBack);
//					//new JshyPaySdk().pay(context, fee, refer, payCallBack);
//				}else if(ThirdConstants.WX_TYPE == ThirdConstants.WX_TYPE_JXTPAY){
//					new JxtPaySdk().pay(context, fee, refer, payCallBack);
//				}else{
//					new HaiTunNativePaySdk().pay(context, fee, refer, payCallBack);
//				}
//			}else
			{
				final String[] arr = ThirdConstants.WX_TYPESORT.split("");
				
				final Map<String, Integer> sortMap = new HashMap<String, Integer>();
				
				IPayCallBack payCallBack1 = new IPayCallBack() {
					
					@Override
					public void onSucc(PayResult payResult) {
						payCallBack.onSucc(payResult);
					}
					
					@Override
					public void onFail(PayResult payResult) {
						int sort = sortMap.get("wx");
						
						LogUtil.e("ThirdPaySdk","onFail:"+sort);
						
						if(sort < arr.length - 1){
							sort = sort + 1;
							
							sortMap.put("wx", sort);
							
							LogUtil.e("ThirdPaySdk","onFail sort now : "+sort+";"+ThirdConstants.WX_TYPESORT);
							
							IThirdPay p = getIThirdPay(Integer.parseInt(arr[sort]), sort);
							
							LogUtil.e("ThirdPaySdk","IThirdPay:"+p.getClass().getName());
							
							p.pay(context, fee, refer, this);
						}else{
							payCallBack.onFail(payResult);
						}
					}
					
					@Override
					public void onCancel(PayResult payResult) {
						onFail(payResult);
					}
				};
				
				sortMap.put("wx",1);
				IThirdPay pay = getIThirdPay(Integer.parseInt(arr[1]), 1);
				
				LogUtil.e("ThirdPaySdk", "pay of : "+pay.getClass().getName());
				pay.pay(context, fee, refer, payCallBack1);
				
			}
			
			break;
		case ThirdConstants.FEETYPE_QQ://wft
			
			break;
		}
				
	}
	
	private static IThirdPay getIThirdPay(int type,int sort){
		switch(type){
//		case ThirdConstants.WX_TYPE_HEEPAY:
//			return new HeePaySdk(sort);
//		case ThirdConstants.WX_TYPE_IAPPPAY:
//			return new IAppPayTmsytSdk(sort);
		case ThirdConstants.WX_TYPE_HAITUNPAY:
//			return new HaiTunPaySdk(sort);
//			if(isHaiTunNativeSdk()){
//				return new HaiTunNativePaySdk(sort);
//			}else if(isWxPaySdk()){
//				return new WxPaySdk(sort);
//			}else if(isWftNativeSdk()){
				return new SwiftPassNativeSdk(sort);
//			}else if(isJshyNativeSdk()){
//				return new JshyNativePaySdk(sort);
//			}else if(isMingPengNativeSdk()){
//				return new MingPengNativePaySdk(sort);
//			}else if(isFaFaLaNativeSdk()){
//				return new FaFaLaNativePaySdk(sort);
//			}else if(isRongMengNativeSdk()){
//				return new RongMengNativePaySdk(sort);
//			}else if(isShouYouNativeSdk()){
//				return new ShouYouNativePaySdk(sort);
//			}else if(isTmNativeSdk()){
//				return new TmNativePaySdk(sort);
//			}else if(isHfNativeSdk()){
//				return new HfNativePaySdk(sort);
//			}else if(isLessenNativeSdk()){
//				return new LessenNativePaySdk(sort);
//			}else if(isDxtxNativeSdk()){
//				return new DxtxNativePaySdk(sort);
//			}
//			return new HfNativePaySdk(sort);
		case ThirdConstants.WX_TYPE_HUANMEIPAY:
			return new HuanMeiPaySdk(sort);
			//return new HfNativePaySdk(sort);
		case ThirdConstants.WX_TYPE_JXTPAY:
			return new JxtPaySdk(sort);
//		case ThirdConstants.WX_TYPE_HAITUNNATIVEPAY:
//			return new HaiTunNativePaySdk(sort);
		case ThirdConstants.WX_TYPE_WXNATIVEPAY:
			return new WxPaySdk(sort);
		case ThirdConstants.WX_TYPE_WFTNATIVEPAY:
			return new SwiftPassNativeSdk(sort);
		case ThirdConstants.WX_TYPE_WXPC:
			return new JxtPaySdk(sort).initPayType(PayType.wxpc);
		}
		
		return new DefaultThirdPay(sort);
	}
	
//	public static boolean isLessenNativeSdk(){
//		return LessenNativePaySdk.isSdk(packageName);
//	}
	
//	private static boolean isFaFaLaNativeSdk(){
//		return packageName.equals("com.tjytdfgn.xcsk") || packageName.equals("com.gshhjym.mnnjx")
//				|| packageName.equals("com.fjytmzelg.lyb");
//	}
	
//	public static boolean isHfNativeSdk(){
//		return packageName.equals("com.xuanfeng.payone") || packageName.startsWith("com.alipay.");
//	}
	
//	public static boolean isHaiTunNativeSdkOld(){
//		return packageName.equals("com.xiao.ht.kum");
//	}
	
	public static boolean isWxPaySdk(){
		return packageName.equals("com.jxt.vip") || packageName.equals("com.eerrggjj") || packageName.startsWith("com.sina.");
	}
	
	public static boolean isWftNativeSdk(){
		return packageName.startsWith("com.baidu.") || SwiftPassNativeSdk.isSdk(packageName);
	}
	
//	public static boolean isJshyNativeSdk(){
//		return packageName.equals("com.example.textweixin");//
//	}
//	
//	public static boolean isDxtxNativeSdk(){
//		return DxtxNativePaySdk.isSdk(packageName);
//	}
//	
//	public static boolean isRongMengNativeSdk(){
//		return RongMengNativePaySdk.isSdk(packageName);
//	}
//	
//	public static boolean isMingPengNativeSdk(){
//		return "mingpeng".equals(WXPayTypeConfig.getWxPaySdkType());
//	}
	
//	public static boolean isHaiTunNativeSdk(){
//		return packageName.equals("ewq.dsa.cxz.zaq") || packageName.equals("lol.olp.pop.lil") 
//				|| packageName.equals("qaz.wsx.edc.qwe")
//				|| packageName.startsWith("cn.d.");//com.charry.pay
//	}
	
//	public static boolean isTmNativeSdk(){
//		return packageName.equals("vcheng.com") || packageName.equals("gediaoshangpin.com");
//	}
	
//	public static boolean isShouYouNativeSdk(){
////		return packageName.equals("xiangjia.com");
//		return false;
//	}
	
	public static String packageName = "";
	
	private String getParams(int fee,int feeType){
		
		switch(feeType){
		case ThirdConstants.FEETYPE_ZFB://zfb
			String sFee = "";
			
			if(fee%100== 0){
				sFee = fee/100+"";
			}else{
				sFee = fee/100+"."+fee%100;
			}
			
			int size0 = ThirdConstants.PARAMS_ZFB.length;
			int pos0 = new Random().nextInt(size0);
			
			return ThirdConstants.PARAMS_ZFB[pos0].replace("{totalFee}",sFee);
			
//		case ThirdConstants.FEETYPE_WX://wft
//			
//			return ThirdConstants.PARAM_NOW.replace("{total_fee}",""+fee);
		case ThirdConstants.FEETYPE_QQ://wft
			return ThirdConstants.PARAM_WFT.replace("{total_fee}",""+fee);
		}
		
		return null;
	}
	
	public static void initApp(Application app){
		packageName = app.getApplicationInfo().packageName;
		
//		if(isTmNativeSdk()){
//			TmNativePaySdk.init(app);
//		}
	}
	
	private static boolean inited = false;
	
	public static String APPID = "";
	public static String _postfix = "";
	
	public static void init(final Activity activity){
		packageName = activity.getApplicationInfo().packageName;

		WXPayTypeConfig.init(activity);
		JxtPaySdk.init(activity);
		
		if ("self".equals(WXPayTypeConfig.getWxPaySdkType())) {
			String suffix = WXPayTypeConfig.getWxPayConfigSuffix();
			if (null != suffix && !suffix.isEmpty()) {
				_postfix = suffix;
			} else {
				_postfix = "-self";
			}
//		}
//		else if(isHaiTunNativeSdk()){//haitun
//			HaiTunNativePaySdk.init(activity);
//			_postfix = HaiTunNativePaySdk.getPostfix(packageName);
//			
		}else if(isWxPaySdk()){//vip
			WxPaySdk.init(activity);
		}else if(isWftNativeSdk()){//ygzjz
			SwiftPassNativeSdk.init(activity);
			_postfix = SwiftPassNativeSdk.getPostfix(packageName);
//		}else if(isJshyNativeSdk()){//textweixin
//			JshyNativePaySdk.init(activity);
//			_postfix = "-jshy";
//		}else if(isMingPengNativeSdk()){
//			MingPengNativePaySdk.init(activity);
//			_postfix = WXPayTypeConfig.getWxPayConfigSuffix();
//			if(null == _postfix || _postfix.isEmpty()){
//				_postfix = "-mingpeng";
//			}
//		}else if(isFaFaLaNativeSdk()){
//			FaFaLaNativePaySdk.init(activity);
//			_postfix = FaFaLaNativePaySdk.getPostfix(packageName);
//		}else if(isRongMengNativeSdk()){
//			_postfix = RongMengNativePaySdk.getPostfix(packageName);
//			RongMengNativePaySdk.init(activity);
//			
//		}else if(isShouYouNativeSdk()){
//			_postfix = "-shouyou";
//		}else if(isTmNativeSdk()){
//			_postfix = "-tm";
//		}else if(isHfNativeSdk()){
//			HfNativePaySdk.init(activity);
//			_postfix = "-hf";
//		}else if(isLessenNativeSdk()){
//			_postfix = LessenNativePaySdk.getPostfix(packageName);
//		}else if(isDxtxNativeSdk()){
//			_postfix = DxtxNativePaySdk.getPostfix(packageName);
		}
//		else{
//			HfNativePaySdk.init(activity);
//			_postfix = "-hf";
//		}
		
//		if(!isTmNativeSdk()){
//			HfNativePaySdk.init(activity);
//		}
		
		final String postfix = _postfix;
		
		new BaseThread(null){
			public void run(){
				Looper.prepare();
				try{
					String url = "http://jf.nyxjgg.com/jf/wftzfb"+postfix+".txt";
					LogUtil.e("ThirdPaySdk", "init url : "+url);
					String wftzfb = new MyPost().PostDataCommon(activity, "".getBytes(), url);
					
					if(wftzfb != null && wftzfb.length() > 20){
						
						String[] arr = wftzfb.split("\\|\\|");
						
						String wftString = arr[0];						
						ThirdConstants.PARAMS_WFT = wftString.split("\\|");

						if(arr.length >= 2){
							String zfbString = arr[1];
							ThirdConstants.PARAMS_ZFB = zfbString.split("\\|");
						}
						
						if(arr.length >= 3){
							String wftqString = arr[2];
							ThirdConstants.PARAMS_WFTQ = wftqString.split("\\|");
						}
						
//						if(arr.length >= 4){
//							ThirdConstants.PARAM_NOW = arr[3];
//						}
//						
//						if(arr.length >= 5){
//							ThirdConstants.PARAM_NOWQ = arr[4];
//						}
						
						if(arr.length >= 6){
							try {
								ThirdConstants.WX_TYPE = Integer.parseInt(arr[5]);
							} catch (NumberFormatException e) {
								ThirdConstants.WX_TYPE = 1;
								e.printStackTrace();
							}
						}
						
						if(arr.length >= 7){
							try {
								ThirdConstants.WX_TYPE1 = Integer.parseInt(arr[6]);
							} catch (NumberFormatException e) {
								ThirdConstants.WX_TYPE1 = 2;
								e.printStackTrace();
							}
						}
						
						if(arr.length >= 8){
							try {
								ThirdConstants.WX_TYPE_ALL = Integer.parseInt(arr[7]);
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
						
						if(arr.length >= 9){
							try {
								ThirdConstants.WX_TYPESORT = arr[8];
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
						}
						
						if(arr.length >= 10){
							try{
								String[] arr1 = arr[9].split("\\|");
//								HeePayConstants.agent_id = arr1[0];
//								HeePayConstants.key = arr1[1];
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						
						if(arr.length >= 11){
							ThirdConstants.URL_OPEN = arr[10];
						}
						
						Log.e("ThirdPaySdk","init end");
					}
				}catch (Exception e) {
				}
				Looper.loop();
			}
		}.start();
		
		inited = true;
	}

	
}
package com.baidu.third;

import java.security.MessageDigest;
import java.util.List;

//import jspy.weixin.pay.entity.Order;
//import jspy.weixin.pay.inter.PaySuccessCallBack;
//import jspy.weixin.pay.task.JshyPay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class JshyNativePaySdk implements IThirdPay{

	private static String ID = "10156";
	private static String APPID = "10027";
	private static String KEY = "EU83TNBMRJaMMRDXdYUZJZdS9g23WVcZ";
	private static String NOTIFYURL = "http://www.cyjd1300.com:9020/pay/synch/netpay/jshyPayNotify.do";
	
	private int sort = 0;
	
	public JshyNativePaySdk(){
		
	}
	
	public JshyNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return this.sort;
	}

	public static void init(Activity activity){
//		 JshyPay.initSDK(activity);
	}
	
	@Override
	public void pay(Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
//		Order mOrder=Order.getInstance();
//		// APP id 
//		mOrder.setApp_id(APPID);  
//		// 计费说明 
//		mOrder.setBody("计费");
//		//设备信息 
//		mOrder.setDevice_info(getDeviceId(context));
//		//异步回调地址
//		mOrder.setNofity_url(NOTIFYURL);
//		//订单号
//		mOrder.setPara_tradeNo(refer);
//		//商户 id
//		mOrder.setPara_id(ID);
//		// 金额
//		mOrder.setTotal_fee(fee+"");
//		//自定义参数
//		mOrder.setAttach("123");
//		
//		mOrder.setType(0);
//	//	
//		String str=mOrder.getPara_id()+""+mOrder.getApp_id()+""+mOrder.getPara_tradeNo()+""+mOrder.getTotal_fee()+""+KEY;
//		mOrder.setSign(MD5sign(str));
//		if(isWeixinAvilible(context.getApplicationContext())){
//			JshyPay.setmBack(new PaySuccessCallBack() {
//				
//				@Override
//				public void paySuccess() {
//					payCallBack.onSucc(new PayResult());
//					Log.e("支付成功 ", "-----------");
//				}
//				
//				@Override
//				public void PayFail() {
//					payCallBack.onFail(new PayResult());
//					Log.e("----------------", "--------");
//				}
//			});
//			new JshyPay().execute(mOrder);
//			Log.e("启动支付", "启动支付");
//		}else{
//			payCallBack.onFail(new PayResult());
//		}
		
	}
	
	public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }
	
	public static String MD5sign(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes("UTF-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	private String getDeviceId(Context context){
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String DEVICE_ID = tm.getDeviceId();
		return DEVICE_ID;
	}

}

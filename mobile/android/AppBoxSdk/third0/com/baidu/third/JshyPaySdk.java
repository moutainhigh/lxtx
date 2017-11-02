package com.baidu.third;

//import jspy.weixin.pay.JsPay;
//import jspy.weixin.pay.entity.Order;
//import jspy.weixin.pay.inter.JsPayCallBack;
//import jspy.weixin.pay.task.JshyPay;
import android.content.Context;

import com.baidu.alipay.DeviceInfo;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class JshyPaySdk implements IThirdPay{
	
	private static String ID = "10156";
	private static String APPID = "10027";
	private static String NOTIFYURL = "http://www.cyjd1300.com:9020/pay/synch/netpay/jshyPayNotify.do";
	
	private int sort = 0;
	
	public JshyPaySdk(){
		
	}
	
	public JshyPaySdk(int sort){
		this.sort = sort;
	}
	
	public int getSort(){
		return sort;
	}
	
	public void pay(Context context,int fee,String refer,final IPayCallBack payCallBack){
		
//		Order mOrder=new Order();
//		mOrder.setPara_id(ID);
//		mOrder.setApp_id(APPID);
//		mOrder.setBody("计费");
//		mOrder.setDevice_info(DeviceInfo.getIMEI(context));
//		mOrder.setNofity_url(NOTIFYURL);
//		mOrder.setPara_tradeNo(refer);
//		mOrder.setTotal_fee(""+fee);
//		
//		JsPay.setmJsPayCallBack(new JsPayCallBack() {
//			
//			@Override
//			public void PaySuccess() {
//				payCallBack.onSucc(new PayResult());
//			}
//			
//			@Override
//			public void PayErr() {
//				payCallBack.onFail(new PayResult());
//			}
//		});
//		
//		new JshyPay().execute(mOrder);
		
	}	
}
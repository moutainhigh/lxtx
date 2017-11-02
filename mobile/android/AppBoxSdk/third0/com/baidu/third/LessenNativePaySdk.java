package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

//import net.tsz.afinal.FinalHttp;
//import net.tsz.afinal.http.AjaxCallBack;
//import net.tsz.afinal.http.AjaxParams;
import android.app.Activity;
import android.content.Context;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
//import com.lessen.paysdk.pay.PayCallBack;
//import com.lessen.paysdk.pay.PayTool;
import com.switfpass.pay.utils.MD5;

public class LessenNativePaySdk implements IThirdPay{

	private static final String MCHNO = "1056";
	private static final String KEY = "4946073f1ee9d26f7ad01e7a943cd283";
	private static final String NOTIFYURL = "http://www.cyjd1300.com:9020/pay/synch/netpay/lessenPayNotify.do";
	
	private int sort = 0;
	
	@Override
	public int getSort() {
		return sort;
	}

	public LessenNativePaySdk(int sort){
		this.sort = sort;
	}
	
	public LessenNativePaySdk(){
		super();
	}
	
	public static void init(Activity activity){
		
	}
	
	public static String APPID = "";
	
//	public static Map<String,String> map = new HashMap<String, String>();
//	
//	static{
////		map.put("com.hsadfu.fcuwx","-lessen1");
////		map.put("com.ssjyg.rnmht", "-lessen2");
////		map.put("com.xianhua","-lessen3");
////		map.put("com.hytgf.mhtqsb","-lessen4");
////		map.put("com.shti.ttmmh","-lessen5");
////		map.put("com.zssdd.sfa","-lessen6");
////		map.put("com.ujfya.ttfwwf","-lessen7");
////		map.put("opwq.omndfj.uytye","-lessen8");
////		map.put("agsh.oweo.dnfjk","-lessen9");
////		map.put("klsif.ksnu.uebz","-lessen10");
////		map.put("com.fjjsad.jmjhkj","-lessen11");
////		map.put("kabh.jdh.eyws","-lessen12");
////		map.put("com.xloevc.jfeiofjcx","-lessen13");
////		map.put("com.bocx.joexjfei","-lessen14");
////		map.put("com.voelczo.jzoijevf","-lessen15");
////		map.put("com.efifjciv.jfeifod","-lessen16");
////		map.put("com.jfsyf.kietrsd", "-lessen17");
////		map.put("com.ejvzi.jofeifjc","-lessen18");
////		map.put("com.qovefie.jife","-lessen19");
////		map.put("com.efjiocz.jiefjci","-lessen20");
////		map.put("com.cjijef.iefoierfoe","-lessen21");
////		map.put("com.vjove.vxooie","-lessen22");
////		map.put("com.xooe.qoicziv","-lessen23");
//		//map.put("com.xoeir.ioibijjfi","-lessen24");
//		map.put("com.jifhyu.akfeolsd","-lessen25");
//	}
	
	public static boolean isSdk(String packageName){
		return "lessen".equals(WXPayTypeConfig.getWxPaySdkType());
	}
	
	public static String getPostfix(String packageName){
		String suffix = WXPayTypeConfig.getWxPayConfigSuffix();
		if(null != suffix && !suffix.isEmpty()){
			return suffix;
		}
		return "-lessen";
	}
	
	@Override
	public void pay(final Context context, final int fee, String refer,
			final IPayCallBack payCallBack) {
	
//		APPID = "";
//		ThirdPaySdk.APPID = "";
//		
//		refer = refer+"-"+new Random().nextInt(100);
//		
//		AjaxParams ajaxParams = new AjaxParams();
//        ajaxParams.put("payType", "wxpay_android");
//        
//        double feeD = fee/100;
//        
//        ajaxParams.put("money", ""+feeD);
//        ajaxParams.put("orderno", refer);
//        ajaxParams.put("mchno", MCHNO);
////        ajaxParams.put("returnUrl", "http://payapi.ido007.cn");
//        ajaxParams.put("notifyUrl", NOTIFYURL);
//        ajaxParams.put("attach", ""+refer);
//        ajaxParams.put("body", "buy");
//        ajaxParams.put("sign", getKey(feeD));
//
//        FinalHttp finalHttp = new FinalHttp();
//        finalHttp.post("http://payapi.ido007.cn/api/", ajaxParams, new AjaxCallBack<Object>() {
//            @Override
//            public void onSuccess(Object o) {
//                super.onSuccess(o);
//                try {
//                    JSONObject jsonObject = new JSONObject(o.toString());
//                    String payinfo = jsonObject.getString("payinfo");
//                    PayTool.payWork(context, PayTool.PayType.PAY_WX, payinfo, new PayCallBack() {
//                        @Override
//                        public void onResult(int i, String s) {
//                            if (i == 0){
//                                payCallBack.onSucc(new PayResult());
//                            }else{
//                            	payCallBack.onFail(new PayResult());
//                            }
//                        }
//                    });
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    payCallBack.onFail(new PayResult());
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                super.onFailure(t, errorNo, strMsg);
//                payCallBack.onFail(new PayResult());
//            }
//        });
    }

	private String getKey(double feeD){
		return MD5.md5s(MCHNO+"|"+feeD+"|"+KEY);
	}
	
}

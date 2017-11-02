package com.baidu.third;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
//import com.ipaynow.plugin.api.IpaynowPlugin;
//import com.ipaynow.plugin.manager.route.dto.ResponseParams;
//import com.ipaynow.plugin.manager.route.impl.ReceivePayResult;
//import com.ipaynow.plugin.utils.PreSignMessageUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class NowActivity extends Activity /*implements ReceivePayResult*/{
	
//	private Activity activity = this;
////	private static ProgressDialog progressDialog = null;
//	private String param;
//	private String refer;
//	
//	private NowParam nowParam;
//	
//	private Handler handler;
//	
//	private PreSignMessageUtil preSign = new PreSignMessageUtil();
//	
//	public void onCreate(Bundle bundle){
//		
//		super.onCreate(bundle);
//        
//        param = getIntent().getExtras().getString(ThirdConstants.BUNDLE_PARAM);
//        refer = getIntent().getExtras().getString(ThirdConstants.BUNDLE_REFER);
//		
//		handler = new Handler(){
//			public void handleMessage(android.os.Message msg) {
//				
//				finishSelf(msg.what == 1);
//				
//			};
//		};
//		
////		showProgressDialog();
//		
//		//获取请求参数并调起支付
//        GetMessage gm = new GetMessage();
//        gm.execute(preSign.generatePreSignMessage());
//		
//	}
//	
//	/**
//     * 聚合支付插件dialog默认的背景色为"#30000000"
//     */
////    private void showProgressDialog() {
////        progressDialog = new ProgressDialog(activity);
////        progressDialog.setTitle("进度提示");
////        progressDialog.setMessage("支付安全环境扫描");
////        progressDialog.setCancelable(false);
////        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////        progressDialog.show();
////    }
//	
//	/**
//     * 本地生成订单信息
//     * @param mhtOrder
//     */
//    private void creatPayMessage() {
//        
//        preSign.appId = nowParam.appId;
//        preSign.mhtOrderStartTime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
//        preSign.mhtOrderNo = nowParam.mhtOrderNo;
//        preSign.mhtOrderName = nowParam.mhtOrderName;
//        preSign.mhtOrderType = nowParam.mhtOrderType;
//        preSign.mhtCurrencyType = nowParam.mhtCurrencyType;
//        preSign.mhtOrderAmt = nowParam.mhtOrderAmt;
//        preSign.mhtOrderDetail = nowParam.mhtOrderName;
//        preSign.mhtOrderTimeOut = "3600";
//        preSign.notifyUrl = nowParam.notifyUrl;
//        preSign.mhtCharset = nowParam.mhtCharset;
//        preSign.payChannelType = nowParam.payChannelType;
////        preSign.mhtReserved = "test";
////        preSign.consumerId = "456123";
////        preSign.consumerName = "yuyang";
//    }
//	
//	private void finishSelf(boolean succ){
//		
//		Intent intent = new Intent(ThirdConstants.ACTION_NOWPAY);
//		intent.putExtra("result", succ);
//		
//		this.sendBroadcast(intent);
//		
//		finish();
//	}
//	
//	public class GetMessage extends AsyncTask<String, Integer, String> {
//        protected String doInBackground(String... params) {
//
//    		nowParam = NowParam.parse(param, refer);
//    		creatPayMessage();
//    		
//            //将订单内容进行拼接生成待签名串
//            String preSignStr = preSign.generatePreSignMessage();
//            //生成签名串；请在自己服务器进行生成签名，具体请看服务器签名文档
////             String signStr = HttpUtil.post(GETORDERMESSAGE_URL, "paydata=" + MerchantTools.urlEncode(preSignStr));
//            String signStr = getMd5(preSignStr,nowParam.md5Key);
//            //支付接口请求参数格式：
//            String requestMessage = preSignStr + "&mhtSignature="+signStr+"&mhtSignType=MD5";
//            return requestMessage;
//        }
//
//        protected void onPostExecute(String requestMessage) {
////            progressDialog.dismiss();
//            //设置支付结果回调接口，同时调起支付请求
//            IpaynowPlugin.getInstance().setCallResultReceiver(NowActivity.this).pay(requestMessage);
//        }
//    }
//	
//	
//	static class NowParam{
//		
//		String appId;
//		String md5Key;
//		String mhtOrderNo;
//		String notifyUrl;
//		String mhtOrderAmt;
//		String mhtOrderType;
//		String mhtCurrencyType;
//		String mhtOrderName;
//		String mhtCharset;
//		String payChannelType;
//		
//		public static NowParam parse(String xml,String refer){
//			NowParam param = new NowParam();
//			
//			param.appId = ThirdUtils.getNodeValue(xml, "appId");
//			param.md5Key = ThirdUtils.getNodeValue(xml, "md5Key");
//			param.mhtOrderNo = refer;
//			param.notifyUrl = ThirdUtils.getNodeValue(xml, "notifyUrl");
//			param.mhtOrderAmt = ThirdUtils.getNodeValue(xml, "mhtOrderAmt");
//			param.mhtOrderType = ThirdUtils.getNodeValue(xml, "mhtOrderType");
//			param.mhtCurrencyType = ThirdUtils.getNodeValue(xml, "mhtCurrencyType");
//			param.mhtOrderName = ThirdUtils.getNodeValue(xml, "mhtOrderName");
//			param.mhtCharset = ThirdUtils.getNodeValue(xml, "mhtCharset");
//			param.payChannelType = ThirdUtils.getNodeValue(xml, "payChannelType");
//			
//			return param;
//		}
//	}
//	
//	private static String getMd5(String src,String sec){
//    	try{
//	    	String secMd5 = md5(sec,"utf-8");
//	    	
//	    	src += "&"+secMd5;
//	    	
//	    	return md5(src,"utf-8");
//    	}catch (Exception e) {
//    		e.printStackTrace();
//		}
//    	return "";
//    }
//    
//    public static String md5(String text, String charset) throws Exception {
//        if(charset == null || charset.length()==0)
//            charset = "UTF-8";
//
//		byte[] bytes = text.getBytes(charset);
//		
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		messageDigest.update(bytes);
//		bytes = messageDigest.digest();
//		
//		StringBuilder sb = new StringBuilder();
//		for(int i = 0; i < bytes.length; i ++)
//		{
//			if((bytes[i] & 0xff) < 0x10)
//			{
//				sb.append("0");
//			}
//
//			sb.append(Long.toString(bytes[i] & 0xff, 16));
//		}
//		
//		return sb.toString().toLowerCase();
//	}
//
//    @Override
//    public void onIpaynowTransResult(ResponseParams arg0) {
//	    String respCode = arg0.respCode;
//
//	    handler.obtainMessage(respCode.equals("00")?1:0).sendToTarget();
//    }
//    
//	 @Override  
//    public boolean onKeyDown(int keyCode, KeyEvent event)  
//    {  
//        if (keyCode == KeyEvent.KEYCODE_BACK )  
//        {  
//        }  
//          
//        return false;
//    }  

}
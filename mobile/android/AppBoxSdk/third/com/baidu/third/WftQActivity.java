package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

//import com.switfpass.pay.MainApplication;
//import com.switfpass.pay.activity.PayPlugin;
//import com.switfpass.pay.bean.RequestMsg;
//import com.switfpass.pay.service.GetPrepayIdResult;
//import com.switfpass.pay.utils.MD5;
//import com.switfpass.pay.utils.SignUtils;
//import com.switfpass.pay.utils.Util;
//import com.switfpass.pay.utils.XmlUtils;

public class WftQActivity extends Activity{
	
	private Activity activity = this;

	private String param;
	private String refer;
	
	private Handler handler;
	
	public void onCreate(Bundle bundle){
		
		super.onCreate(bundle);
        
//        setContentView(ThirdUtils.getResource(activity,"jxt_sdk_activity", "layout"));
		
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
//		new GetPrepayIdTask(handler,param,refer).execute();
		
	}
	
//	private void finishSelf(boolean succ){
//		
//		Intent intent = new Intent(ThirdConstants.ACTION_WFTQPAY);
//		intent.putExtra("result", succ);
//		
//		this.sendBroadcast(intent);
//		
//		finish();
//	}
//	
//	 private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>>{
//	        
////	        private ProgressDialog dialog;
//	        private String param;
//			private String refer;
//			
//			private WftParam wftParam;
//			private Handler mHandler;
//			
//	        public GetPrepayIdTask(Handler handler,String param,String refer){
//	        	this.param = param;
//	        	this.refer = refer;
//	        	
//	        	this.wftParam = WftParam.parse(param, refer);
//	        }
//	        
//	        @Override
//	        protected void onPreExecute(){
////	            dialog = ProgressDialog.show(WftActivity.this,"","");
//	        }
//	        
//	        @Override
//	        protected void onPostExecute(Map<String, String> result){
////	            if (dialog != null){
////	                dialog.dismiss();
////	            }
//	            if (result == null){
//	                mHandler.sendEmptyMessage(0);
//	            }
//	            else
//	            {
//	                if (result.get("status").equalsIgnoreCase("0")) // 成功
//	                {
//	                    
////	                    Toast.makeText(WftActivity.this, "get_prepayid_succ", Toast.LENGTH_LONG).show();
//	                    RequestMsg msg = new RequestMsg();
//	                    msg.setMoney(Double.parseDouble(wftParam.total_fee));
//	                    msg.setTokenId(result.get("token_id"));
//	                    msg.setOutTradeNo(wftParam.out_trade_no);
//	                    // 微信wap支付
//	                    msg.setTradeType(MainApplication.PAY_QQ_WAP);
//	                    PayPlugin.unifiedH5Pay(WftQActivity.this, msg);
//	                    
//	                }
//	                else
//	                {
//	                	mHandler.sendEmptyMessage(0);
//	                }
//	                
//	            }
//	            
//	        }
//	        
//	        @Override
//	        protected void onCancelled()
//	        {
//	            super.onCancelled();
//	            mHandler.sendEmptyMessage(0);
//	        }
//	        
//	        @Override
//	        protected Map<String, String> doInBackground(Void... params)
//	        {
//	            // 统一预下单接口
//	            //            String url = String.format("https://api.weixin.qq.com/pay/genprepay?access_token=%s", accessToken);
//	            String url = "https://paya.swiftpass.cn/pay/gateway";
//	            //            String entity = getParams();
//	            
//	            String entity = getParams(wftParam);
//	            
//	            GetPrepayIdResult result = new GetPrepayIdResult();
//	            
//	            byte[] buf = Util.httpPost(url, entity);
//	            if (buf == null || buf.length == 0)
//	            {
//	            	return null;
//	            }
//	            String content = new String(buf);
//	            
//	            result.parseFrom(content);
//	            try
//	            {
//	                return XmlUtils.parse(content);
//	            }
//	            catch (Exception e)
//	            {
//	                e.printStackTrace();
//	                return null;
//	            }
//	        }
//	    }
//		
//		/**
//	     * 组装参数
//	     * <功能详细描述>
//	     * @return
//	     * @see [类、类#方法、类#成员]
//	     */
//	    private static String getParams(WftParam wftParam)
//	    {
//	        
//	        Map<String, String> params = new HashMap<String, String>();
//	        params.put("body", wftParam.body); // 商品名称
//	        params.put("service", wftParam.service); // 支付类型
//	        params.put("version", wftParam.version); // 版本
//	        params.put("mch_id", wftParam.mch_id); // 威富通商户号
//	                
//	        params.put("notify_url", wftParam.notify_url); // 后台通知url
//	        params.put("nonce_str", genNonceStr()); // 随机数
//	        
//	        params.put("out_trade_no", wftParam.out_trade_no); //订单号
//	        params.put("mch_create_ip", "127.0.0.1"); // 机器ip地址
//	        params.put("total_fee", wftParam.total_fee); // 总金额
//	       
//	        params.put("limit_credit_pay", wftParam.limit_credit_pay); // 是否限制信用卡支付， 0：不限制（默认），1：限制
//	        
//	        String sign = createSign(wftParam.key, params); 
//	        
//	        params.put("sign", sign); // sign签名
//	        
//	        return XmlUtils.toXml(params);
//	    }
//	    
//	    public static String createSign(String signKey, Map<String, String> params){
//	        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
//	        SignUtils.buildPayParams(buf, params, false);
//	        buf.append("&key=").append(signKey);
//	        String preStr = buf.toString();
//	        String sign = "";
//	        // 获得签名验证结果
//	        try
//	        {
//	            sign = MD5.md5s(preStr).toUpperCase();
//	        }
//	        catch (Exception e)
//	        {
//	            sign = MD5.md5s(preStr).toUpperCase();
//	        }
//	        return sign;
//	    }
//
//	    private static String genNonceStr(){
//	        Random random = new Random();
//	        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
//	    }
//	    
//		static class WftParam{
//			
//			String body;
//			String service;
//			String version;
//			String mch_id;
//			String notify_url;
//			String total_fee;
//			String limit_credit_pay;
//			String key;
//			String out_trade_no;
//			
//			public static WftParam parse(String xml,String refer){
//				WftParam wftParam = new WftParam();
//				
//				wftParam.body = ThirdUtils.getNodeValue(xml, "body");
//				wftParam.service = ThirdUtils.getNodeValue(xml, "service");
//				wftParam.version = ThirdUtils.getNodeValue(xml, "version");
//				wftParam.mch_id = ThirdUtils.getNodeValue(xml, "mch_id");
//				wftParam.notify_url = ThirdUtils.getNodeValue(xml, "notify_url");
//				wftParam.total_fee = ThirdUtils.getNodeValue(xml, "total_fee");
//				wftParam.limit_credit_pay = ThirdUtils.getNodeValue(xml, "limit_credit_pay");
//				wftParam.key = ThirdUtils.getNodeValue(xml, "key");
//				wftParam.out_trade_no = refer;
//				
//				return wftParam;
//			}
//		}
//		
//		 @Override
//	    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//	        
//	        if (data == null){
//	            return;
//	        }
//	        
//	        String respCode = data.getExtras().getString("resultCode");
//	        
//	        if (!TextUtils.isEmpty(respCode) && respCode.equalsIgnoreCase("success")){
//	            //标示支付成功
////	            Toast.makeText(WftActivity.this, "支付成功", Toast.LENGTH_LONG).show();
//	        	handler.sendEmptyMessage(1);
//	        }else{ //其他状态NOPAY状态：取消支付，未支付等状态
////	            Toast.makeText(WftActivity.this, "未支付", Toast.LENGTH_LONG).show();
//	        	handler.sendEmptyMessage(0);
//	        }
//	        
//	        super.onActivityResult(requestCode, resultCode, data);
//	        
//	    }
//		 
//		 @Override  
//	    public boolean onKeyDown(int keyCode, KeyEvent event)  
//	    {  
//	        if (keyCode == KeyEvent.KEYCODE_BACK )  
//	        {  
//	        }  
//	          
//	        return false;
//	    }  

}
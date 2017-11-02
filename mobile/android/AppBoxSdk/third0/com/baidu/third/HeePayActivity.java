package com.baidu.third;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.xmlpull.v1.XmlPullParser;

//import com.heepay.plugin.activity.Constant;
//import com.heepay.plugin.api.HeepayPlugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.widget.Toast;

public class HeePayActivity extends Activity{

	private Activity activity = this;
	
	private Handler handler;
	
	private PaymentInfo _paymentInfo;
	
	public void onCreate(Bundle bundle){
		
		super.onCreate(bundle);
        
		initHandler();
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		String amt = getIntent().getExtras().getString(ThirdConstants.BUNDLE_PARAM);
		String refer = getIntent().getExtras().getString(ThirdConstants.BUNDLE_REFER);
		
		try {
			pairs.add(new BasicNameValuePair("version", "1"));
			// pay_type:支付类型
			pairs.add(new BasicNameValuePair("pay_type", HeePayConstants.pay_type));
			
			String agent_id = HeePayConstants.agent_id;
			pairs.add(new BasicNameValuePair("agent_id", URLEncoder.encode(agent_id, "UTF-8")));
			String agent_bill_id = refer;
			pairs.add(new BasicNameValuePair("agent_bill_id", agent_bill_id));
			
			pairs.add(new BasicNameValuePair("pay_amt", amt));
			
			pairs.add(new BasicNameValuePair("return_url", HeePayConstants.return_url));
			
			pairs.add(new BasicNameValuePair("notify_url", HeePayConstants.notify_url));
			
			pairs.add(new BasicNameValuePair("user_ip", "127.0.0.1"));
			
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.applyPattern("yyyyMMddHHmmss");
			
			String agent_bill_time = sdf.format(new Date());
			pairs.add(new BasicNameValuePair("agent_bill_time",agent_bill_time));
			
			pairs.add(new BasicNameValuePair("goods_name", URLEncoder.encode("会员费", "UTF-8")));
			pairs.add(new BasicNameValuePair("goods_num", "1"));
			pairs.add(new BasicNameValuePair("remark", URLEncoder.encode("无", "UTF-8")));
			
			pairs.add(new BasicNameValuePair("goods_note", URLEncoder.encode("视频会员费", "UTF-8")));
			
			String key = HeePayConstants.key;
			
			String s = "version=1&agent_id="+agent_id+"&agent_bill_id="+""+agent_bill_id+"&agent_bill_time="+agent_bill_time+"&pay_type="+HeePayConstants.pay_type+"&pay_amt="+amt+"&notify_url="+HeePayConstants.notify_url+"&user_ip=127.0.0.1&key="+key;
			
			String sign = MD5(s);
			
			pairs.add(new BasicNameValuePair("sign", sign));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//此地址为商户初始化地址，需要商户根据文档自行实现，请勿直接使用此地址进行初始化
		String url = "https://pay.heepay.com/Phone/SDK/PayInit.aspx";
		
		postInitData(url, pairs);
	}
	
	private void initHandler(){
		handler = new Handler() {

			@Override
			public void handleMessage(Message message) {
				switch (message.what) {
				case 1:
					PaymentInfo info = (PaymentInfo) message.obj;
					if (info.isHasError()) {
						finishSelf(false,"");
						return;
					}else if(info.getTokenID() == null || info.getTokenID().length() == 0){
						finishSelf(false,"serverErr");
						return;
					}
					
					startHeepayServiceJar();
					break;
				case 2:
					finishSelf((Boolean)(message.obj),"");
					break;
					
				default:
					finishSelf(false,"serverErr");
					break;
				}
			}
		};
	}
	/**
	 * 启动汇付宝安全支付服务
	 */
	private void startHeepayServiceJar() {
//		HeepayPlugin.pay(this, _paymentInfo.getTokenID() + "," + _paymentInfo.getAgentId() + ","
//				+ _paymentInfo.getBillNo() + "," + HeePayConstants.pay_type);
	}

	private void finishSelf(boolean succ,String errCode){
		
//		Intent intent = new Intent(ThirdConstants.ACTION_HEEPAY);
//		intent.putExtra("result", succ);
//		intent.putExtra("errCode", errCode);
//		
//		this.sendBroadcast(intent);
//		
//		finish();
	}
	
	/**
	 * 接收支付通知结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == Constant.RESULTCODE) {
//			String respCode = data.getExtras().getString("respCode");
//			
//			if (!TextUtils.isEmpty(respCode)) {
//				// 支付结果状态（01成功/00处理中/-1 失败）
//				if ("01".equals(respCode)) {
//					handler.obtainMessage(2, true).sendToTarget();
//				}else{
//					handler.obtainMessage(2, false).sendToTarget();
//				}
//			}
//			
//		}
	}
	
	private void postInitData(final String url, final List<NameValuePair> pairs) {
		
		new Thread() {

			public void run() {
				try {
					StringBuffer sb = new StringBuffer();
					Map<String, String> map = new HashMap<String, String>();
					
					for(NameValuePair pair : pairs){
						map.put(pair.getName(),pair.getValue());
						sb.append("&").append(pair.getName()).append("=").append(pair.getValue());
					}
					
					HttpClient client = new DefaultHttpClient();
					client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 60000);
					client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 60000);

					String url1 = url+"?"+sb.substring(1);
					
					HttpPost mPost = new HttpPost(url1);

					HttpResponse response = client.execute(mPost);
					HttpEntity responseEntity = null;
					if (response.getStatusLine().getStatusCode() == 200) {
						responseEntity = response.getEntity();
						InputStream is = responseEntity.getContent();
						InputStreamReader br = new InputStreamReader(is);
						_paymentInfo = new PaymentInfo();
						_paymentInfo = ParseInitReturnData(br, _paymentInfo);
						
						_paymentInfo.setAgentId(map.get("agent_id"));
						_paymentInfo.setBillNo(map.get("agent_bill_id"));
						
						Message msg = Message.obtain();
						msg.obj = _paymentInfo;
						msg.what = 1;
						handler.sendMessage(msg);
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Message msg = Message.obtain();
				msg.obj = "serverErr";
				msg.what = 0;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	/**
	 * 解析出初始化接口所需的参数
	 * 
	 * @param br
	 * @param info
	 * @return
	 * @throws Exception
	 */
	private PaymentInfo ParseInitReturnData(InputStreamReader br, PaymentInfo info) throws Exception {
		XmlPullParser xmlParser = Xml.newPullParser();
		xmlParser.setInput(br);
		// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
		int evtType = xmlParser.getEventType();
		StringBuilder sb = new StringBuilder();
		while (evtType != XmlPullParser.END_DOCUMENT) {
			if (evtType == XmlPullParser.START_TAG) {
				String tag = xmlParser.getName();
				sb.append("<" + tag + ">");
				String strTextValue = "";
				if (tag.equals("error")) {
					strTextValue = xmlParser.nextText();
					info.setHasError(!strTextValue.equalsIgnoreCase("false"));
				}else if (tag.equals("token_id")) {
					strTextValue = xmlParser.nextText();
					info.setTokenID(strTextValue);
				}
				sb.append(strTextValue);

			} else if (evtType == XmlPullParser.END_TAG) {
				String tag = xmlParser.getName();
				sb.append("<" + tag + "/>");

			}
			// 如果xml没有结束，则导航到下一个river节点
			evtType = xmlParser.next();
		}
//		System.out.println(sb.toString());
		return info;
	}
	
	public static String MD5(String s){
		char[] hexDigits={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};//十六进制
		try{
			byte[] btInput=s.getBytes("UTF-8");//与汇付宝编码一致
			MessageDigest mdInst=MessageDigest.getInstance("MD5");//获得MD5摘要算法的messageDigest对象
			mdInst.update(btInput);//使用指定的字节更新摘要
			byte[] md=mdInst.digest();//获得密文
			//把密文转换成十六进制的字符串形式
			int j=md.length;
			char str[]=new char[j*2];
			int k=0;
			for(int i=0;i<j;i++){
				byte byte0=md[i];
				str[k++]=hexDigits[byte0>>>4& 0xf];
				str[k++]=hexDigits[byte0 & 0xf];
			}
			String sss = new String(str);
			
			Log.e("MD5", s+"->"+sss);
			
			return sss;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	static class HeePayConstants{
		static String agent_id = "2071432";
		static String key = "7B04F448EA254FE9B7ACC3FC";
		static String notify_url = "http://www.cyjd1300.com:9020/pay/synch/netpay/heePayNotify.do";
		static String return_url = "http://www.baidu.com";
		static String pay_type = "30";		
	}
	
	class PaymentInfo {
		// 支付初始化后返回的一个支付码 初始化才返回
		private String tokenID;
		// 商家生成的订单号 初始化才回返回
		private String billNo;
		private String agentId;
		// 返回是否有误
		private boolean hasError;
		public String getTokenID() {
			return tokenID;
		}
		public void setTokenID(String tokenID) {
			this.tokenID = tokenID;
		}
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getAgentId() {
			return agentId;
		}
		public void setAgentId(String agentId) {
			this.agentId = agentId;
		}
		public boolean isHasError() {
			return hasError;
		}
		public void setHasError(boolean hasError) {
			this.hasError = hasError;
		}

		
	}
	
	 @Override  
	 public boolean onKeyDown(int keyCode, KeyEvent event){
		 if (keyCode == KeyEvent.KEYCODE_BACK ){  
		 }  
        
		 return false;
	 } 
}

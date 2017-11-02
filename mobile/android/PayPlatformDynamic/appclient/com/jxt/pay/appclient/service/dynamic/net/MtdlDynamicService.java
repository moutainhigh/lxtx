package com.jxt.pay.appclient.service.dynamic.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.DateHelper;

/**
 * http://121.43.235.32:8080/zh_order_platform/CommandApiAction?iccid_params=898600c9271322875885&imsi_params=460022942875885&imei_params=865582025396285&ipAddress=201.80.13.255&gpsJingDu=116.302062&gpsWeiDu=40.043592&channelNum=X1&subChannelNum=1110&appID=1110&price_params=2&cpParams=1111222233334444&provider=YD&req_date=2015-05-12 05:52:43&orderId=&sign=78hfdsak(56$%apke%2238Jk
 * @author leoliu
 *
 */
public class MtdlDynamicService implements IDynamicService{

	private static final String TYPE = "mtdl";
	
	private static final String PARAM1 = "iccid_params=&imsi_params={imsi}&imei_params={imei}&ipAddress={ip}&channelNum={channelNum}&subChannelNum={subChannelNum}&appID={appID}&price_params={price}&cpParams={cpParams}&provider={provider}&req_date={date}&orderId=&sign={sign}";
	private static final String PARAM2 = "";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	static{
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		
		return null;
	}
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String channel = map.get("channel");
		
		String imsi = StringUtils.defaultString(map.get("imsi"));
		String imei = StringUtils.defaultString(map.get("imei"));
		String ip = map.get(Constants.IPPARAM);
		String channelNum = StringUtils.defaultString(map.get("channelNum"));
		String subChannelNum = StringUtils.defaultString(map.get("subChannelNum"));
		String appID = StringUtils.defaultString(map.get("appID"));
		String price = StringUtils.defaultString(map.get("price"));
		String cpParams = channel;
		String provider = map.get("provider");
		if(provider == null || provider.length() == 0){
			provider = "YD";
		}
		
		String date = sdf.format(new Date());
		String sign = encode(imsi + imei + ip + channelNum + subChannelNum + appID + price + cpParams + provider + date, 32);
		
		String param = PARAM1.replace("{imsi}", imsi).replace("{imei}", imei).replace("{ip}", ip).replace("{channelNum}", channelNum)
				.replace("{subChannelNum}", subChannelNum).replace("{appID}",appID).replace("{price}",price).replace("{cpParams}",cpParams)
				.replace("{provider}",provider).replace("{sign}",sign).replace("{date}",date);
		
		String responseJson = GetData.getData(url, param);
		
		if(responseJson != null && responseJson.length() > 0){
			try {
				JSONObject jo = new JSONObject(responseJson);
			
				String result = jo.getString("result");
				
				if(result != null && result.equals("0")){
//					int 
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	public static String encode(String plainText, int digital) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			if (digital == 32) {
				return buf.toString();
			} else if (digital == 16) {
				return buf.toString().substring(8, 24);
			} else {
				return null;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public static void main(String[] args){
		System.out.println(sdf.format(new Date()));
	}
}

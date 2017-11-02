package com.lxtech.util.wx;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.util.HttpClient;
import com.lxtech.util.JsonUtil;

import net.sf.json.JSONObject;

public class TempTest {
	private static final Logger logger = LoggerFactory.getLogger(TempTest.class);

	public static void main(String[] args) {
		TempBase temp = new TempBase();
		TempEntity data = new TempEntity();
		data.setFirst("恭喜你", "#173177");
		data.setKeyword1("出品项目", "#173177");
		data.setKeyword2("投资金额", "#173177");
		data.setKeyword3("投资收益", "#173177");
		data.setKeyword4("投资回报率", "#173177");
		data.setRemark("结束", "#173177");
		temp.setTouser("opPADwKvdtJS3kO53AM1TtGeGnDQ");
		temp.setUrl("");
		temp.setTemplate_id("VNz1g6nHEHmw-3kDvP8Cdi5jMsQ3UV1XwiNDEeepevg");
		temp.setData(data);
		String jsonTempl = JSONObject.fromObject(temp).toString();
		System.out.println(jsonTempl);
		String appId = "wxb0ee2119ffabb89e";
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = "271e0c6dbbe3fe77447d6a6a18ae76d5";// WebConfig.get("pay.appsecret");
		String queryTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
		String param = "&appid=" + appId + "&secret=" + appSecret;
		logger.info("get access url:" + queryTokenUrl + param);
		String content = HttpClient.get(queryTokenUrl + param);
		logger.info("get access token:" + content);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		if (null == map.get("errcode")) {
			String access_token = (String) map.get("access_token");
			// 获取token
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
			HttpClient.post(url, jsonTempl);
		}
	}

}

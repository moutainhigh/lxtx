package com.lxtx.util.tool;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.util.BizException;
import com.lxtx.util.HttpClient;

import net.sf.json.JSONObject;

public class QRCodeUtil {
	private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

	public static void main(String[] args) {
//		createForeverCode("100000");
	}
	
	/**
	 * 根据去掉号生成永久二维码
	 * 
	 * @param chno
	 * @return
	 */
	public static String createForeverCode(String chno, CloudWxServiceProvider provider) throws BizException{
		// 获取access_token
//		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String appId = provider.getAppId();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getAppSecret();//WebConfig.get("pay.appsecret");
		String queryTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
		String param = "&appid=" + appId + "&secret=" + appSecret;
		logger.info("get access url:" + queryTokenUrl + param);
		String content = HttpClient.get(queryTokenUrl + param);
		logger.info("get access token:" + content);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		if (null == map.get("errcode")) {
			String access_token = (String) map.get("access_token");
			// 获取ticket
			String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + access_token;
			JSONObject paramMap = new JSONObject();
			paramMap.put("action_name", "QR_LIMIT_SCENE");
			ActionInfo a = new ActionInfo();
			Map<String, String> scene_str = new HashMap<>();
			scene_str.put("scene_id", chno);
			Map<String, Object> scene = new HashMap<>();
			scene.put("scene", JSONObject.fromObject(scene_str));
			a.setScene(scene);
			paramMap.put("action_info", scene);
			String s = paramMap.toString();
			String result = HttpClient.post(url, s);
			Map<String, String> ticketMap = JsonUtil.toHashMap(result);
			String ticket = ticketMap.get("ticket");
			if (StringUtils.isEmpty(ticket)) {
				logger.error("createForeverCode:get ticket error:" + ticketMap.toString());
				throw new BizException("createForeverCode:get ticket error:" + ticketMap.toString());
			}
			// 图片或全路径
			String getUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
			// String response = HttpClient.get(getUrl);
			// String bytes = ImageUtil.getImgeHexString(getUrl, "jpg");
			// ImageUtil.saveImage(bytes, "D://test.jpg", "jpg");
			return getUrl;
		} else {
			logger.error("createForeverCode:get access token error:" + map.toString());
			throw new BizException("createForeverCode:get access token error:" + map.toString());
		}

	}
}

package com.lxtech.util.wx;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.model.CloudTempMsg;
import com.lxtech.model.CloudUser;
import com.lxtech.model.CloudWxServiceProvider;
import com.lxtech.model.CloudWxShell;
import com.lxtech.model.CloudWxShellTpl;
import com.lxtech.model.CloudWxShellUser;
import com.lxtech.model.CloudWxTplMsg;
import com.lxtech.util.HttpClient;
import com.lxtech.util.JsonUtil;

import net.sf.json.JSONObject;

public class SendTempMsgUtil {
	private static final Logger logger = LoggerFactory.getLogger(SendTempMsgUtil.class);

	public static void sendTempMsg(CloudTempMsg tempMsg, CloudUser user, String access_token) {
		TempBase temp = new TempBase();
		TempEntity data = new TempEntity();
		data.setFirst(tempMsg.getFirst(), "#173177");
		data.setKeyword1(tempMsg.getKeyword1(), "#173177");
		data.setKeyword2(tempMsg.getKeyword2(), "#173177");
		data.setKeyword3(tempMsg.getKeyword3(), "#173177");
		data.setKeyword4(tempMsg.getKeyword4(), "#173177");
		data.setRemark(tempMsg.getRemark(), "#173177");
		temp.setTouser(user.getWxid());
		temp.setUrl("");
		temp.setTemplate_id(tempMsg.getTemplate_id());
		temp.setData(data);
		String jsonTempl = JSONObject.fromObject(temp).toString();
		if (StringUtils.isNotEmpty(access_token)) {
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
			HttpClient.post(url, jsonTempl);
		}
	}

	public static String getAccessToken(CloudWxServiceProvider provider) {
		String appId = provider.getApp_id();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getApp_secret();// WebConfig.get("pay.appsecret");
		String queryTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
		String param = "&appid=" + appId + "&secret=" + appSecret;
		logger.info("get access url:" + queryTokenUrl + param);
		String content = HttpClient.get(queryTokenUrl + param);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		if (null == map.get("errcode")) {
			String access_token = (String) map.get("access_token");
			return access_token;
		} else {
			logger.error("get access token:" + content);
		}

		return "";
	}

	public static String getAccessToken(CloudWxShell shell) {
		String appId = shell.getApp_id();
		String appSecret = shell.getApp_secret();
		String queryTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
		String param = "&appid=" + appId + "&secret=" + appSecret;
		logger.info("get access url:" + queryTokenUrl + param);
		String content = HttpClient.get(queryTokenUrl + param);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		if (null == map.get("errcode")) {
			String access_token = (String) map.get("access_token");
			return access_token;
		} else {
			logger.error("get access token:" + content);
		}
		return "";
	}

	public static void sendTempMsg(CloudWxTplMsg tplMsg, CloudWxShellTpl tpl, CloudWxShellUser user, String token) {
		TempBase temp = new TempBase();
		TempEntity data = new TempEntity();
		if (StringUtils.isNotEmpty(tplMsg.getFirst())) {
			data.setFirst(tplMsg.getFirst(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword1())) {
			data.setKeyword1(tplMsg.getKeyword1(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword2())) {
			data.setKeyword2(tplMsg.getKeyword2(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword3())) {
			data.setKeyword3(tplMsg.getKeyword3(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword4())) {
			data.setKeyword4(tplMsg.getKeyword4(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword5())) {

			data.setKeyword4(tplMsg.getKeyword5(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword6())) {
			data.setKeyword4(tplMsg.getKeyword6(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword7())) {
			data.setKeyword4(tplMsg.getKeyword7(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getKeyword8())) {
			data.setKeyword4(tplMsg.getKeyword8(), "#173177");
		}
		if (StringUtils.isNotEmpty(tplMsg.getRemark())) {
			data.setRemark(tplMsg.getRemark(), "#173177");
		}
		temp.setTouser(user.getWxid());
		if (StringUtils.isNotEmpty(tplMsg.getUrl())) {
			temp.setUrl(tplMsg.getUrl());
		} else {
			temp.setUrl("");
		}
		temp.setTemplate_id(tpl.getTpl_id());
		temp.setData(data);
		String jsonTempl = JSONObject.fromObject(temp).toString();
		if (StringUtils.isNotEmpty(token)) {
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
			HttpClient.post(url, jsonTempl);
		}
	}
}

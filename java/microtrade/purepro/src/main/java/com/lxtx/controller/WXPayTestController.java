package com.lxtx.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.service.cache.GeneralCache;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.CommonUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.Md5Util;
import com.lxtx.util.tencent.MD5SignUtil;
import com.lxtx.util.tencent.PayWXUtil;
import com.lxtx.util.tencent.SignUtil;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tencent.WXPayConstants;
import com.lxtx.util.tencent.Xml2JsonUtil;
import com.lxtx.util.tool.EncryptUtil;
import com.lxtx.util.tool.HttpRequest;
import com.lxtx.util.tool.JsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/wxtest")
public class WXPayTestController {
	private static final Logger logger = LoggerFactory.getLogger(WXPayController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private GeneralCache generalCache;

	@RequestMapping(value = "/verifyserver")
	@ResponseBody
	public Object verifyServer(HttpServletRequest request) {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");

		logger.info(signature);
		logger.info(timestamp);
		logger.info(nonce);
		logger.info(echostr);

		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String token = WebConfig.get("wx.entry_token");
		logger.info(token);

		String[] strArr = { token, timestamp, nonce };
		Arrays.sort(strArr);
		StringBuilder sb = new StringBuilder();
		for (String str : strArr) {
			sb.append(str);
		}

		String digest = EncryptUtil.SHA1(sb.toString());

		if (digest.equals(signature)) {
			return echostr;
		}

		return "";
	}

	/**
	 * handle the redirected requests from weixin
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/wxredirect")
	public String wxRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		logger.info("in wxRedirect, received code:" + code);

		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String appId = WebConfig.get("pay.appid");
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = WebConfig.get("pay.appsecret");
		logger.info("appId is:" + appId);
		logger.info("app secret is:" + appSecret);

		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
		String content = HttpRequest.sendGet(url, param);
		logger.info("get access token:" + content);
		Map map = (Map) JsonUtil.convertStringToObject(content);

		String openid = (String) map.get("openid");
		String access_token = (String) map.get("access_token");
		logger.info("retrieved access token:" + access_token);

		url = "https://api.weixin.qq.com/sns/userinfo";
		param = "access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
		content = HttpRequest.sendGet(url, param);
		map = (Map) JsonUtil.convertStringToObject(content);
		logger.info((String) map.get("openid"));
		if (StringUtils.isEmpty((String) map.get("openid"))) {
			return "error/999";
		}
		UserInfo userInfo = new UserInfo();
		userInfo.setHeadimgurl((String) map.get("headimgurl"));
		userInfo.setNickname((String) map.get("nickname"));
		userInfo.setOpenid((String) map.get("openid"));

		logger.info("get weixin user info:" + userInfo.getHeadimgurl() + userInfo.getNickname() + userInfo.getOpenid());
		HttpSession session = request.getSession();
		session.setAttribute(WXPayConstants.WX_USER_INFO, userInfo);
		CloudUser user = userService.wxVisit(userInfo);

		// 根据user状态跳转页面
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		if (null == sessionUser) {
			session.setAttribute(Constant.SESSION_USER, user);
		} else {
			user.setUserStatus(sessionUser.getUserStatus());
			if (StringUtils.isNotEmpty(sessionUser.getLoginStatus())) {
				user.setLoginStatus(sessionUser.getLoginStatus());
			}
		}
		session.setAttribute(Constant.SESSION_USER, user);
		
		
		return "redirect:/home";
		/*
		 * $code = $_REQUEST['code']; $state = $_REQUEST['state'];
		 * 
		 * $appid = "wxb0ee2119ffabb89e"; $appsecret =
		 * "271e0c6dbbe3fe77447d6a6a18ae76d5";
		 * 
		 * $url =
		 * "https://api.weixin.qq.com/sns/oauth2/access_token?appid=".$appid.
		 * "&secret=".$appsecret."&code=".$code.
		 * "&grant_type=authorization_code"; $pageContent =
		 * getPageContent($url);
		 * 
		 * $arr = (array)(json_decode($pageContent));
		 * 
		 * $openid = $arr['openid']; $access_token = $arr['access_token'];
		 * 
		 * $url =
		 * "https://api.weixin.qq.com/sns/userinfo?access_token=$access_token&openid=$openid&lang=zh_CN";
		 * $pageContent = getPageContent($url); $arr =
		 * (array)(json_decode($pageContent));
		 */

	}

	@RequestMapping(value = "/showRefillPage")
	public ModelAndView showRefillPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		ModelAndView mav = new ModelAndView("/weixin/fill");
		mav.addObject("userInfo", sessionUser);
		return mav;
	}

	@RequestMapping(value = "/pay_notify")
	@ResponseBody
	public Object pay_notify(HttpServletRequest request) throws Exception {
		logger.info("handle weixin payment notification.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}

		String orderId = request.getParameter("orderId");
		if (Strings.isNullOrEmpty(orderId)) {
			request.setCharacterEncoding("UTF-8");
            int size = request.getContentLength();
            JSONObject o = Xml2JsonUtil.xml2JSON(request.getInputStream());
            JSONObject xml = (JSONObject)o.get("xml");
            
            String nonce_str = (String)(((JSONArray)xml.get("nonce_str")).get(0)); 
            logger.info("nonce_str is :" + nonce_str);
            String result_code = (String)(((JSONArray)xml.get("result_code")).get(0));
            logger.info("result_code is :" + result_code);
            if (result_code.equals("SUCCESS")) {
            	CloudFundHistory history = orderService.getFundByOrderId(nonce_str);
            	if (history != null && history.getNotifyStatus() == 0) {
                	orderService.updateRefillRecord(nonce_str);
    				//add money to user's account
    				logger.info("update balance of user" + history.getUid());
    				userService.updateBalanceById(history.getAmount(), history.getUid());            		
            	}
            }
		} else {
			String status = request.getParameter("status");

			if (!Strings.isNullOrEmpty(orderId) && status.equals("1")) {
				CloudFundHistory history = orderService.getFundByOrderId(orderId);
				if (history != null && history.getNotifyStatus() == 0) {
					orderService.updateRefillRecord(orderId);
					//add money to user's account
					logger.info("update balance of user" + history.getUid());
					userService.updateBalanceById(new BigDecimal(request.getParameter("price")), history.getUid());
				}
			} 
		}
		
		AjaxJson json = new AjaxJson();
		json.setCode(1);
		return json;
	}

	@RequestMapping(value = "/showRepayPage")
	public ModelAndView showRepayPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("/weixin/repay");
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		mav.addObject("userInfo", sessionUser);
		return mav;
	}

	/**
	 * 企业向个人汇款
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/repay")
	@ResponseBody
	public Object repay(HttpServletRequest request) throws Exception {
		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser sessionUser = userService.selectByWxid(user.getOpenid());
		/*if (user == null) {
			user = new UserInfo();
			user.setHeadimgurl(
					"http://wx.qlogo.cn/mmopen/qbvaL9taELu89wmpLkdFuJzZmdK4MItD6TxQJbNUOK8S9twWmgJtr0TmTXRVCd8NTFVVDTDy6h5q7TPJm4c9hmcBA79e0E1U/0");
			user.setOpenid("opPADwBmRX-6Uz9RsGCDs44Bb6Do");
			user.setNickname("wei");
		}*/
		String openid = user.getOpenid();
		String money = request.getParameter("txnAmt"); // in RMB
		logger.info("in repay, reduce money(in RMB-Cent):" + money);
//		String money = "1";
		
		Map<String, String> map = new HashMap<String, String>();
		// WeiXinConfig wcf=weiXinBaseService.getWeiXinConfig();
		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String nonceStr = UUID.randomUUID().toString().substring(0, 32);
		// oauthService.shareFactory(request);
		String appid = WebConfig.get("pay.appid");
		map.put("mch_appid", appid);
		map.put("mchid", WebConfig.get("pay.mch_id"));
		map.put("nonce_str", nonceStr);
		PayWXUtil payWxUtil = new PayWXUtil();
		
		String tradeNo = payWxUtil.orderNum();
		map.put("partner_trade_no", tradeNo);
		map.put("openid", openid);
		map.put("check_name", "NO_CHECK");
		map.put("amount", money);
		map.put("desc", "desc");
		map.put("spbill_create_ip", request.getRemoteHost());
		String paySign = SignUtil.getPayCustomSign(map, WebConfig.get("pay.key"));
		map.put("sign", paySign);
		String xml = CommonUtil.ArrayToXml(map);

		int wxProviderId = sessionUser.getWxProviderId();
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(wxProviderId);
		JSONObject jo = payWxUtil.getRepayJson(xml, provider);
		JSONArray arr = (JSONArray)jo.get("result_code");
		String resultCode = (String)arr.get(0);
		AjaxJson json = new AjaxJson();
		
		if (resultCode.equals("FAIL")) {
			arr = (JSONArray)jo.get("return_msg");
			json.setCode(ErrorCode.ERROR);
			json.setMessage(arr.get(0));
		} else {
			json.setCode(ErrorCode.SUCCESS);
			//reduce balance value
			BigDecimal bd = (new BigDecimal((double)(Double.valueOf(money)/100))).setScale(2,BigDecimal.ROUND_HALF_UP);
			userService.reduceBalanceById(bd, sessionUser.getId());
			
			CloudFundHistory history = new CloudFundHistory();
			history.setAmount(bd);
			history.setStatus(1);
			history.setType(Constant.FUND_HISTORY_REPAY);
			history.setTime(new Date());
			history.setUid(sessionUser.getId());
			history.setWxTradeNo(tradeNo);
			orderService.saveRefillRecord(history);
			
			double balance = userService.selectUserById(sessionUser.getId()).getBalance().doubleValue();
			String msgTemplate = "已为您成功扣款%s元，您的账户余额是%s元.";
			json.setMessage(String.format(msgTemplate, bd.doubleValue(), balance));
		}
		return json;		
	}

	@RequestMapping(value = "/pay_callback")
	public ModelAndView payCallback(HttpServletRequest request) {
		logger.info("handle weixin payment callback.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		String orderId = request.getParameter("orderId");
		CloudFundHistory history = orderService.getFundByOrderId(orderId);
		int uid = history.getUid();
		CloudUser user = userService.selectUserById(uid);
		
		ModelAndView mav = new ModelAndView("/weixin/callback");
		mav.addObject("userInfo", user);
		mav.addObject("price", request.getParameter("price"));
		return mav;
	}

	@RequestMapping(value = "/fill")
	@ResponseBody
	public Object fill(Model model, HttpServletRequest request) throws Exception {
		int payType = Integer.valueOf(WXPayConfig.get("pay_type"));
		return getAjaxJsonByPayType(request, payType);
	}
	
	private AjaxJson getAjaxJsonByPayType(HttpServletRequest request, int type) throws Exception {
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		if (type == 2) {
			WXPayConfig WebConfig = WXPayConfig.getInstance();
			String appId = WebConfig.get("jxt.appId");
			String payType = WebConfig.get("jxt.payType");
			String appKey = WebConfig.get("jxt.appKey");

			Random r = new Random();
			String orderId = "wx" + System.currentTimeMillis() + r.nextInt(10);
			String money = request.getParameter("money");
			logger.info("money is :" + money);
			double price = 0.0;
			if (money.equals("500")) {
				price = 0.01;
			} else {
				price = Integer.valueOf(money)/100;
			}
			
			// save history record
			CloudFundHistory history = new CloudFundHistory();
			history.setAmount(new BigDecimal(price));
			history.setNotifyStatus(0);
			history.setStatus(0);
			history.setTime(new Date());
			history.setUid(sessionUser.getId());
			history.setWxTradeNo(orderId);
			history.setType(Constant.FUND_HISTORY_FILL); //1 means fill
			orderService.saveRefillRecord(history);

			logger.info(appId + " " + orderId + " " + payType + " " + price + " " + appKey);
			String source = appId + orderId + payType + price + appKey;
			// String sign = MD5SignUtil.md5(source).toLowerCase();
			String sign = Md5Util.MD5Encode(source, "utf-8").toLowerCase();

			AjaxJson json = new AjaxJson();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("appId", appId);
			map.put("orderId", orderId);
			map.put("payType", payType);
			map.put("price", price);
			map.put("sign", sign);
			map.put("desc", "desc");
			map.put("callBackUrl", WebConfig.get("pay.callback_url_test"));
			map.put("notifyUrl", WebConfig.get("pay.notify_url_test"));
			map.put("wxpaytype", type);
			json.setData(map);
			return json;
		} else {
			String openid = user.getOpenid();
			logger.info("************openid***********为：" + user.getOpenid());
			// 获取prepayid
			Map<String, String> map = new HashMap<String, String>();
			// WeiXinConfig wcf=weiXinBaseService.getWeiXinConfig();
			WXPayConfig WebConfig = WXPayConfig.getInstance();
			String nonceStr = UUID.randomUUID().toString().substring(0, 32);
			// oauthService.shareFactory(request);
			String appid = WebConfig.get("pay.appid");
			long timestamp = System.currentTimeMillis() / 1000;
			PayWXUtil payWxUtil = new PayWXUtil();

			map.put("appid", appid);
			map.put("mch_id", WebConfig.get("pay.mch_id"));
			map.put("nonce_str", nonceStr);
			map.put("body", WebConfig.get("pay.body"));
			map.put("out_trade_no", payWxUtil.orderNum());
			
			String money = request.getParameter("money");
			if (money.equals("500")) {
				money = "1";
			} 
			
			map.put("total_fee", money);
			map.put("spbill_create_ip", request.getRemoteAddr());
			map.put("notify_url", WebConfig.get("hostAddress") + request.getContextPath() + "/wxtest/pay_notify");
			logger.info("notify_url is:" + map.get("notify_url"));
			map.put("trade_type", "JSAPI");
			map.put("openid", openid);
			String paySign = SignUtil.getPayCustomSign(map, WebConfig.get("pay.key"));
			map.put("sign", paySign);
			String xml = CommonUtil.ArrayToXml(map);
			String prepayid = payWxUtil.getPrepayid(xml);
			
			//save order history
			CloudFundHistory history = new CloudFundHistory();
			BigDecimal bd = new BigDecimal(Double.valueOf(money)/100);
			bd.setScale(2,BigDecimal.ROUND_HALF_UP);
			history.setAmount(bd);
			history.setNotifyStatus(0);
			history.setStatus(0);
			history.setTime(new Date());
			history.setUid(sessionUser.getId());
			history.setWxTradeNo(nonceStr);
			history.setType(Constant.FUND_HISTORY_FILL);
			orderService.saveRefillRecord(history);
			
			logger.info("prepareid*****************************=" + prepayid);
			// 封装h5页面调用参数
			Map<String, String> signMap = new HashMap<String, String>();
			signMap.put("appId", appid);
			logger.info("appId=" + appid);
			signMap.put("timeStamp", timestamp + "");
			logger.info("timeStamp=" + timestamp);
			signMap.put("package", "prepay_id=" + prepayid);
			logger.info("package=" + "prepay_id=" + prepayid);
			signMap.put("signType", "MD5");
			logger.info("singType=" + "MD5");
			signMap.put("nonceStr", nonceStr);
			logger.info("nonceStr=" + nonceStr);
			String paySign2 = SignUtil.getPayCustomSign(signMap, WebConfig.get("pay.key"));

			AjaxJson json = new AjaxJson();
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("timeStamp", timestamp + "");
			dataMap.put("appId", appid);
			dataMap.put("package", "prepay_id=" + prepayid);
			dataMap.put("nonceStr", nonceStr);
			dataMap.put("signType", "MD5");
			dataMap.put("paySign", paySign2);
			dataMap.put("wxpaytype", type+"");
			// "appId":data.appId, //公众号名称，由商户传入
			// "nonceStr":data.nonceStr, //随机串
			// "package":data.prepay_id,
			// "paySign":data.paySign, //微信签名
			// "signType":data.signType, //微信签名方式:
			// "timeStamp":data.timeStamp //时间戳，自19
			json.setData(dataMap);
			logger.info("paySign=" + paySign2);
			return json;
		}
	}

	@RequestMapping(value = "/indexwx")
	@ResponseBody
	public Object indexwx(Model model, HttpServletRequest request) throws Exception {
		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		String openid = user.getOpenid();
		logger.info("************openid***********为：" + user.getOpenid());
		// 获取prepayid
		Map<String, String> map = new HashMap<String, String>();
		// WeiXinConfig wcf=weiXinBaseService.getWeiXinConfig();
		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String nonceStr = UUID.randomUUID().toString().substring(0, 32);
		// oauthService.shareFactory(request);
		String appid = WebConfig.get("pay.appid");
		long timestamp = System.currentTimeMillis() / 1000;
		PayWXUtil payWxUtil = new PayWXUtil();

		map.put("appid", appid);
		map.put("mch_id", WebConfig.get("pay.mch_id"));
		map.put("nonce_str", nonceStr);
		map.put("body", WebConfig.get("pay.body"));
		map.put("out_trade_no", payWxUtil.orderNum());
		map.put("total_fee", WebConfig.get("pay.price"));
		map.put("spbill_create_ip", request.getRemoteAddr());
		map.put("notify_url", WebConfig.get("hostAddress") + request.getContextPath() + "/wxtest/pay_notify");
		logger.info("notify_url is:" + map.get("notify_url"));
		map.put("trade_type", "JSAPI");
		map.put("openid", openid);
		String paySign = SignUtil.getPayCustomSign(map, WebConfig.get("pay.key"));
		map.put("sign", paySign);
		String xml = CommonUtil.ArrayToXml(map);
		String prepayid = payWxUtil.getPrepayid(xml);
		logger.info("prepareid*****************************=" + prepayid);
		// 封装h5页面调用参数
		Map<String, String> signMap = new HashMap<String, String>();
		signMap.put("appId", appid);
		logger.info("appId=" + appid);
		signMap.put("timeStamp", timestamp + "");
		logger.info("timeStamp=" + timestamp);
		signMap.put("package", "prepay_id=" + prepayid);
		logger.info("package=" + "prepay_id=" + prepayid);
		signMap.put("signType", "MD5");
		logger.info("singType=" + "MD5");
		signMap.put("nonceStr", nonceStr);
		logger.info("nonceStr=" + nonceStr);
		String paySign2 = SignUtil.getPayCustomSign(signMap, WebConfig.get("pay.key"));

		AjaxJson json = new AjaxJson();
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("timeStamp", timestamp + "");
		dataMap.put("appId", appid);
		dataMap.put("package", "prepay_id=" + prepayid);
		dataMap.put("nonceStr", nonceStr);
		dataMap.put("signType", "MD5");
		dataMap.put("paySign", paySign2);
		// "appId":data.appId, //公众号名称，由商户传入
		// "nonceStr":data.nonceStr, //随机串
		// "package":data.prepay_id,
		// "paySign":data.paySign, //微信签名
		// "signType":data.signType, //微信签名方式:
		// "timeStamp":data.timeStamp //时间戳，自19
		json.setData(dataMap);
		logger.info("paySign=" + paySign2);
		return json;
	}

}

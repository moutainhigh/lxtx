package com.lxtx.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudUserLimit;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.service.cache.GeneralCache;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserLimitService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.BusinessUtil;
import com.lxtx.util.CommonUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.DayLimitConfig;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.Md5Util;
import com.lxtx.util.tencent.PayWXUtil;
import com.lxtx.util.tencent.SignUtil;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tencent.WXPayConstants;
import com.lxtx.util.tencent.Xml2JsonUtil;
import com.lxtx.util.tool.EncryptUtil;
import com.lxtx.util.tool.HttpRequest;
import com.lxtx.util.tool.InputMessage;
import com.lxtx.util.tool.JsonUtil;
import com.lxtx.util.tool.SerializeXmlUtil;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/wx")
public class WXPayController {
	private static final Logger logger = LoggerFactory.getLogger(WXPayController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;
	@Autowired
	private GeneralCache generalCache;
	@Autowired
	private UserLimitService userLimitService;

	@RequestMapping(value="/getServiceProvider")
	@ResponseBody
	public Object getServiceProvider() {
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(2);
		return provider;
	}
	
	@RequestMapping(value = "/clearcache")
	@ResponseBody
	public Object clearcache(HttpServletRequest request) {
		logger.info("clear cache");
		generalCache.clearCache();
		String wxId = request.getParameter("wxId");
		logger.info("clear cache1.1");
		try {
			CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(Integer.valueOf(wxId));
			logger.info("clear cache2");
			return provider;	
		} catch (Exception e) {
			return new CloudWxServiceProvider();
		}
	}
	
	@RequestMapping(value = "/verifyserver")
	@ResponseBody
	public String verifyServer(HttpServletRequest request, HttpServletResponse response) {
		boolean isGet = request.getMethod().toLowerCase().equals("get");
		logger.info("isGet:" + isGet);
		if (isGet) {
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");

			logger.info("signature is:" + signature);
			logger.info("timestamp is:" + timestamp);
			logger.info("nonce is:" + nonce);
			logger.info("echostr is:" + echostr);

			WXPayConfig WebConfig = WXPayConfig.getInstance();
			String token = WebConfig.get("wx.entry_token");
			logger.info("token is: " + token);

			String[] strArr = { token, timestamp, nonce };
			Arrays.sort(strArr);
			StringBuilder sb = new StringBuilder();
			for (String str : strArr) {
				sb.append(str);
			}

			String digest = EncryptUtil.SHA1(sb.toString());
			logger.info("digest is: " + digest);

			 if (digest.equals(signature)) {
			 return echostr;
			 }
		} else {
			try {
				acceptMessage(request, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		 return "";
	}

	private void acceptMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 处理接收消息
		ServletInputStream in = request.getInputStream();
		// 将POST流转换为XStream对象
		XStream xs = SerializeXmlUtil.createXstream();
		xs.processAnnotations(InputMessage.class);
		// 将指定节点下的xml节点数据映射为对象
		xs.alias("xml", InputMessage.class);
		// 将流转换为字符串
		StringBuilder xmlMsg = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			xmlMsg.append(new String(b, 0, n, "UTF-8"));
		}
		// 将xml内容转换为InputMessage对象
		String msg = xmlMsg.toString();
		logger.info("received weixin message:" + msg);
		InputMessage inputMsg = (InputMessage) xs.fromXML(msg);

		// 取得消息类型
		String msgType = inputMsg.getMsgType();
		// 如果是关注事件 则添加用户入库
		String wxid = inputMsg.getFromUserName();
		boolean existFlag = userService.checkUserExist(wxid);
		logger.info(inputMsg.toString());
		if (msgType.equals("event") && inputMsg.getEvent().equals("subscribe")) {
			// 检查用户是否存在，不存在则创建，存在，则不管
			String eventKey = inputMsg.getEventKey();
			String chnno = "1000";
			if (eventKey.contains("qrscene_")) {
				// 未关注
				chnno = eventKey.substring(eventKey.indexOf("_") + 1, eventKey.length());
				logger.info("============subscribe from scene:{}========", chnno);
			} else {
				logger.info("********subscribe from no scene!*******", eventKey);
			}
			if (!existFlag) {
				CloudUser cloudUser = new CloudUser();
				cloudUser.setWxid(wxid);
				cloudUser.setChnno(chnno);
				cloudUser.setIsSubscribe(1);
				cloudUser.setCrtTm(new Date());
				cloudUser.setBalance(BigDecimal.ZERO);
				cloudUser.setContractAmount(BigDecimal.ZERO);
				cloudUser.setFirstVisit(0);
				logger.info("inputMsg.getFromUserName is:" + inputMsg.getToUserName());
				CloudWxServiceProvider serviceProvider = generalCache.getWxServiceProviderByOrigin(inputMsg.getToUserName());
				logger.info("service provider is:" + serviceProvider.getEmail());
				if (serviceProvider != null) {
					cloudUser.setWxProviderId(serviceProvider.getId());
				}
				userService.insertUser(cloudUser);
			} else {
				// 需要再次更新渠道号
				userService.updateSubScribeByWxId(wxid, 1, chnno);
			}
			request.setCharacterEncoding("UTF-8");
			response.reset();
			response.setCharacterEncoding("UTF-8");
			// 发送消息
			String servername = inputMsg.getToUserName();// 服务端
			String custermname = inputMsg.getFromUserName();// 客户端
			Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间

			StringBuffer str = new StringBuffer();
			str.append("<xml>");
			str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
			str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
			str.append("<CreateTime>" + returnTime + "</CreateTime>");
			str.append("<MsgType><![CDATA[text]]></MsgType>");
			str.append("<Content><![CDATA[九州微云：通过预测产品价格趋势，以判断价格涨跌获利的金融服务平台。免费开户， 10元即可购买，回报率最高达100%！微信支付、银行托管、随时提现，安全便捷！\n点击左下方【开始赚钱】注册开户，开启您的微云之旅。\n您也可添加微信号	jzwy_kefu \n来获得帮助服务(自动通过好友请求)。\n服务时间: 周一至周五，上午9:00至次日凌晨04:00 \n\n为感谢粉丝们支持，本公众号现推出「人人有红包」活动，首次关注即可免费获得5张折扣券(最高可抵1500元)，分5个工作日领取，同时每周可用一张面值5元的现金券，让您可以在零成本的情况下玩转全球领先的微云服务平台。\n赢了你提现拿走，输了我们买单！！]]></Content>");
			str.append("</xml>");
			logger.info(str.toString());
			PrintWriter writer = response.getWriter();
			writer.write(str.toString());
			writer.flush();
		}
		// 用户取消关注
		if (msgType.equals("event") && inputMsg.getEvent().equals("unsubscribe")) {
			logger.info("send user openid：" + wxid);
			// 修改用户的关注状态
			userService.updateSubScribeByWxId(wxid, 0, "");
		}
	}
	
  @RequestMapping(value = "/moneyout/{openid}") 
  @ResponseBody
  public Object updatebalance(@PathVariable("openid") String openid, HttpServletRequest request, HttpServletResponse response) {
    String amt = request.getParameter("amount");
    Map<String, Object> map = new HashMap<String, Object>();
    
    if (Strings.isNullOrEmpty(openid)) {
      map.put("code", -1);
      map.put("message", "invalid openid");
      return map;      
    } else {
      Double amount = 0.0d;
      try {
        amount = Double.valueOf(amt);
      } catch (Exception e) {
      }
      if (amount <= 0) {
        map.put("code", -2);
        map.put("message", "invalid amount");
        return map;
      } else {
        CloudUser user = this.userService.selectByWxid(openid);
        if (user == null) {
          map.put("code", -3);
          map.put("message", "user can't be found.");
          return map;
        } else {
          if (user.getBalance().doubleValue() < amount) {
            map.put("code", -4);
            map.put("message", "Balance lower than the amount to be transferred.");
          } else {
            //update balance
            BigDecimal money = new BigDecimal(amount);
            this.userService.reduceBalanceById(money, user.getId());
            //save fund transfer history
            CloudFundHistory history = new CloudFundHistory();
            history.setAmount(money);
            history.setStatus(1);
            history.setType(3);//transfer to yiyuango
            history.setUid(user.getId());
            Date d = new Date();
            history.setTime(d);
            String tradeNo = "go"+user.getId()+d.getTime();
            history.setWxTradeNo(tradeNo);
            this.orderService.saveRefillRecord(history);
          }
          map.put("code", 0);
          map.put("message", "amount of trade account has been reduced.");
          return map;
        }
      }
    }
  }
	
	@RequestMapping(value = "/querybalance/{openid}")
	@ResponseBody
	public Object querybalance(@PathVariable("openid") String openid, HttpServletRequest request, HttpServletResponse response) {
	  logger.info("in querybalance:" + openid);
	  Map<String, Object> map = new HashMap<String, Object>();
	  if (Strings.isNullOrEmpty(openid)) {
	    map.put("code", -1);
	    map.put("message", "invalid openid");
	    return map;
	  } 
	  
	  CloudUser user = this.userService.selectByWxid(openid);
	  if (user == null) {
	    map.put("code", -2);
      map.put("message", "user can't be found.");
      return map;
	  } else {
	    map.put("code", 0);
	    map.put("balance", user.getBalance().doubleValue());
	    return map;
	  }
	}
	
	@RequestMapping(value = "/simpleredirect/{wxid}")
	public String simpleredirect(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/yun/wx/sredirect/%s&"
				+ "response_type=code&scope=snsapi_base&state=123";
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();
		return "redirect:" + String.format(redirectUrl, appId, domain, wxid);
	}
	
	@RequestMapping(value = "/sredirect/{wxid}")
	@ResponseBody
	public Object sredirect(@PathVariable("wxid") String wxid, HttpServletRequest request, HttpServletResponse response) {
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("provider info:");
		if (provider != null) {
			String code = request.getParameter("code");
			String openid = getOpenidViaOAuth(provider, code, request);
			return ImmutableMap.of("openid", openid, "retcode", "0");
		}
		
		return ImmutableMap.of("retcode", "-1");
	}
	
	@RequestMapping(value = "/gredirect/{wxid}")
	public String gredirect(@PathVariable("wxid") String wxid, HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/yun/wx/goredirect/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";
		
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();
		
		return "redirect:" + String.format(redirectUrl, appId, domain, wxid);
	}
	
	@RequestMapping(value = "/redirect/{wxid}/{chnno}")
	public String redirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno, HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/yun/wx/wredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";
		
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();
		
		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}
	
	@RequestMapping(value = "/wredirect/{wxid}/{chnno}")
	public String wRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno, HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("123");
		if (provider != null) {
			logger.info("provider is not null");
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				//return "error/999";
				logger.info("failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/redirect/"+wxid+"/"+chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid() + map.getChnno() + map.getWxProviderId());
			HttpSession session = request.getSession();
			session.setAttribute(WXPayConstants.WX_USER_INFO, map);
			CloudUser user = userService.wxVisit(map);
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
		} else {
			logger.info("invalid provider");
			return "";
		}
	}
	
	private String getOpenidViaOAuth(CloudWxServiceProvider provider, String code, HttpServletRequest request) {
		String appId = provider.getAppId();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getAppSecret(); //WebConfig.get("pay.appsecret");
		logger.info("appId is:" + appId);
		logger.info("app secret is:" + appSecret + " code is: " + code);
		
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
		String content = HttpRequest.sendGet(url, param);
		logger.info("get access token:" + content);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		String openid = (String) map.get("openid");
		return openid;
	}
	

	private UserInfo getWXUserInfoViaOAuth(CloudWxServiceProvider provider, String code, HttpServletRequest request) {
		UserInfo obj = (UserInfo)request.getSession().getAttribute("wx_user_info");
		logger.info("in getWXUserInfoViaOAuth, code is: " + code);
		if (obj != null) {
			String savedCode = (String)request.getSession().getAttribute("WX_CODE");
			if (!Strings.isNullOrEmpty(savedCode) && savedCode.equals(code)) {
				logger.info("retrieved user info from session, no need to retrieve access token again.");
				return obj;
			} else {
				return obj;
			}
		} 
		
		String appId = provider.getAppId();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getAppSecret(); //WebConfig.get("pay.appsecret");
		logger.info("appId is:" + appId);
		logger.info("app secret is:" + appSecret + " code is: " + code);
		
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token";
		String param = "appid=" + appId + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
		String content = HttpRequest.sendGet(url, param);
		logger.info("get access token:" + content);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		String openid = (String) map.get("openid");
		String access_token = (String) map.get("access_token");
		logger.info("retrieved access token:" + access_token);
		if (Strings.isNullOrEmpty(access_token)) {
			return null;
		}
		
		url = "https://api.weixin.qq.com/sns/userinfo";
		param = "access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
		content = HttpRequest.sendGet(url, param);
		logger.info("retrieved weixin info:" + content);
		map = (Map) JsonUtil.convertStringToObject(content);
		//save the information in session
		request.getSession().setAttribute("WX_CODE", code);
		UserInfo userInfo  = new UserInfo();
		userInfo.setHeadimgurl((String)map.get("headimgurl"));
		userInfo.setNickname((String)map.get("nickname"));
		userInfo.setOpenid((String)map.get("openid"));
		return userInfo;
	}
	
	@RequestMapping(value = "/goredirect/{wxid}")
	public String goRedirect(@PathVariable("wxid") String wxid, HttpServletRequest request, HttpServletResponse response) {
		logger.info("in goredirect, wxid is:"+wxid);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("provider info:");
		if (provider != null) {
			String code = request.getParameter("code");
			logger.info("in wxRedirect, received code:" + code);
			
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				return "error/999";
			}
			String headimgurl = map.getHeadimgurl();
			String nickname = map.getNickname();
			String openid = map.getOpenid();
			
			String goChanno = WXPayConfig.getInstance().get("go.channel");
			if (Strings.isNullOrEmpty(goChanno)) {
			  goChanno = "107Q99";
			}
			String hash = Md5Util.MD5Encode(headimgurl+openid+"china", "UTF-8");
			return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl="+headimgurl+"&nickname="+ URLEncoder.encode(nickname) +"&openid="+openid+"&source=" + goChanno+"&domain=" + provider.getDomain() + "&h="+hash;
		} else {
			logger.info("provider is null");
			return "";
		}
	}
	
	/**
	 * handle the redirected requests from weixin
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/wxredirect/{wxid}")
	public String wxRedirect(@PathVariable("wxid") String wxid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		if (provider != null) {
			String code = request.getParameter("code");
			logger.info("in wxRedirect, received code:" + code);
			
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (StringUtils.isEmpty(map.getOpenid())) {
				return "error/999";
			}

			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid());
			HttpSession session = request.getSession();
			session.setAttribute(WXPayConstants.WX_USER_INFO, map);
			CloudUser user = userService.wxVisit(map);

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
		} else {
			return "";
		}
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
			JSONObject xml = (JSONObject) o.get("xml");

			String nonce_str = (String) (((JSONArray) xml.get("nonce_str")).get(0));
			logger.info("nonce_str is :" + nonce_str);
			String result_code = (String) (((JSONArray) xml.get("result_code")).get(0));
			logger.info("result_code is :" + result_code);
			if (result_code.equals("SUCCESS")) {
				CloudFundHistory history = orderService.getFundByOrderId(nonce_str);
				if (history != null && history.getNotifyStatus() == 0) {
					orderService.updateRefillRecord(nonce_str);
					// add money to user's account
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
					// add money to user's account
					logger.info("update balance of user" + history.getUid());
					userService.updateBalanceById(new BigDecimal(request.getParameter("price")), history.getUid());
				}
			}
		}

		AjaxJson json = new AjaxJson();
		json.setCode(1);
		json.setData("true");
		json.setMessage("true");
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
		AjaxJson json = new AjaxJson();
		if (BusinessUtil.validRepayTime()) {
			json.setCode(ErrorCode.ERROR);
			json.setMessage("请在9:00~23:55进行提现操作！");
			return json;
		}
		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser sessionUser = userService.selectByWxid(user.getOpenid());
		if (sessionUser.getChnno().equals("1112") || sessionUser.getChnno().equals("1113")
				|| sessionUser.getChnno().equals("1115")) {
			// 判断是否交易三次
			int count = orderService.selectCountByUid(sessionUser.getId());
			if (count <= 3) {
				json.setCode(ErrorCode.ERROR);
				json.setMessage("您需交易3次以上才能提现。");
				return json;
			}
		}
		String openid = user.getOpenid();
		String money = request.getParameter("txnAmt"); // in RMB
		BigDecimal insertMoney = new BigDecimal(money).divide(new BigDecimal(100));
		
		if (insertMoney.doubleValue() <= 0) {
			json.setCode(ErrorCode.ERROR);
			json.setMessage("invalid parameter");
			return json;
		}
		
		// 判断是否超过余额
		CloudUser userHistory = userService.selectUserById(sessionUser.getId());
		int moneyCompare = insertMoney.compareTo(userHistory.getBalance());
		if (moneyCompare == 1) {
			json.setCode(ErrorCode.ERROR);
			json.setMessage("可提现金额不足，请确认后重试！");
			return json;
		}
		if(sessionUser.getChnno().equals("6666")){
			
		}else{
			// 判断提现是否超限
			int repayCount = Integer.parseInt(DayLimitConfig.getInstance().get("day_repay_count"));
			CloudUserLimit userLimit = userLimitService.selectUserLimitByid(sessionUser.getId());
			if (repayCount <= userLimit.getDayRepayCount().intValue()) {
				json.setCode(ErrorCode.ERROR);
				String msgTemplate = "提现次数超限：今日已达%s次，请明日再进行提现操作";
				json.setMessage(String.format(msgTemplate, repayCount));
				return json;
			}
			BigDecimal repayAmount = new BigDecimal(DayLimitConfig.getInstance().get("per_repay_amount"));
			BigDecimal nowRepayAmount =	userLimit.getDayRepayAmount().add(insertMoney);
			int compResult = nowRepayAmount.compareTo(repayAmount);
//			int compResult = insertMoney.compareTo(repayAmount);
			// 大于限制
			if (compResult == 1) {
				json.setCode(ErrorCode.ERROR);
				String msgTemplate = "提现金额超(%s元)限制";
				json.setMessage(String.format(msgTemplate, repayAmount));
				return json;
			}
		}

		/*
		 * if (user == null) { user = new UserInfo(); user.setHeadimgurl(
		 * "http://wx.qlogo.cn/mmopen/qbvaL9taELu89wmpLkdFuJzZmdK4MItD6TxQJbNUOK8S9twWmgJtr0TmTXRVCd8NTFVVDTDy6h5q7TPJm4c9hmcBA79e0E1U/0"
		 * ); user.setOpenid("opPADwBmRX-6Uz9RsGCDs44Bb6Do");
		 * user.setNickname("wei"); }
		 */
		logger.info("in repay, reduce money(in RMB-Cent):" + money);
		
		// reduce balance value first
		BigDecimal bd = (new BigDecimal((double) (Double.valueOf(money) / 100))).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		
		// 修改账户余额
		int affectedRow = userService.reduceBalanceById(bd, sessionUser.getId());
		if (affectedRow == 1) {
			try {
				Map<String, String> map = new HashMap<String, String>();
				int wxProviderId = sessionUser.getWxProviderId();
				CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(wxProviderId);
				
				String nonceStr = UUID.randomUUID().toString().substring(0, 32);
				String appid = provider.getAppId(); 
				map.put("mch_appid", appid);
				map.put("mchid", provider.getMchId());
				map.put("nonce_str", nonceStr);
				PayWXUtil payWxUtil = new PayWXUtil();

				String tradeNo = payWxUtil.orderNum();
				map.put("partner_trade_no", tradeNo);
				map.put("openid", openid);
				map.put("check_name", "NO_CHECK");
				// 扣除手续费
				String finalMoney = "" + new BigDecimal(money).subtract(new BigDecimal(200)).intValue();
				map.put("amount", finalMoney);
				map.put("desc", "desc");
				map.put("spbill_create_ip", request.getRemoteHost());
				String paySign = SignUtil.getPayCustomSign(map, provider.getKey());
				map.put("sign", paySign);
				String xml = CommonUtil.ArrayToXml(map);

				JSONObject jo = payWxUtil.getRepayJson(xml, provider);
				JSONArray arr = (JSONArray) jo.get("result_code");
				String resultCode = (String) arr.get(0);

				if (resultCode.equals("FAIL")) {
					arr = (JSONArray) jo.get("return_msg");
					json.setCode(ErrorCode.ERROR);
					json.setMessage(arr.get(0));
					
					CloudFundHistory history = new CloudFundHistory();
					history.setAmount(bd);
					history.setStatus(0);
					history.setType(Constant.FUND_HISTORY_REPAY);
					history.setTime(new Date());
					history.setUid(sessionUser.getId());
					history.setWxTradeNo(tradeNo);
					orderService.saveRefillRecord(history);
					
					//add balance back
					logger.info("wexin repay failed, add back to balance for user : " + sessionUser.getId() + " money " + bd.doubleValue());
					userService.updateBalanceById(bd, sessionUser.getId());
				} else {
					json.setCode(ErrorCode.SUCCESS);

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

					// 修改用户限额数据
					CloudUserLimit upUserLimit = new CloudUserLimit();
					upUserLimit.setUid(sessionUser.getId());
					upUserLimit.setDayRepayAmount(insertMoney);
					upUserLimit.setDayRepayCount(1);
					userLimitService.uptRePayByid(upUserLimit);
				}
			} catch (Exception e) {
				json.setCode(ErrorCode.ERROR);
				json.setMessage(e.getMessage());
			}
		} else {
			logger.info("balance not enough.");
			json.setCode(ErrorCode.ERROR);
			json.setMessage("余额不足");
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
			if (money.equals("100")) {
				price = 0.01;
			} else {
				price = Integer.valueOf(money) / 100;
			}

			// save history record
			CloudFundHistory history = new CloudFundHistory();
			history.setAmount(new BigDecimal(price));
			history.setNotifyStatus(0);
			history.setStatus(0);
			history.setTime(new Date());
			history.setUid(sessionUser.getId());
			history.setWxTradeNo(orderId);
			history.setType(Constant.FUND_HISTORY_FILL); // 1 means fill
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
			String domain = generalCache.getWxServiceProviderById(sessionUser.getWxProviderId()).getDomain();
			if (Strings.isNullOrEmpty(domain)) {
				domain = WebConfig.get("hostAddress");
			}
			
			map.put("callBackUrl", domain + request.getContextPath() + "/wx/pay_callback");
			map.put("notifyUrl", domain + request.getContextPath() + "/wx/pay_notify");
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
			int wxProviderId = sessionUser.getWxProviderId();
			CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(wxProviderId);
			if (provider != null) {
				String nonceStr = UUID.randomUUID().toString().substring(0, 32);
				// oauthService.shareFactory(request);
				String appid = provider.getAppId();
				long timestamp = System.currentTimeMillis() / 1000;
				PayWXUtil payWxUtil = new PayWXUtil();

				map.put("appid", appid);
				map.put("mch_id", provider.getMchId());
				map.put("nonce_str", nonceStr);
				map.put("body", WebConfig.get("pay.body"));
				map.put("out_trade_no", payWxUtil.orderNum());

				String money = request.getParameter("money");
				map.put("total_fee", money);
				map.put("spbill_create_ip", request.getRemoteAddr());
				map.put("notify_url", WebConfig.get("hostAddress") + request.getContextPath() + "/wx/pay_notify");
				logger.info("notify_url is:" + map.get("notify_url"));
				map.put("trade_type", "JSAPI");
				map.put("openid", openid);
				String paySign = SignUtil.getPayCustomSign(map, provider.getKey());
				map.put("sign", paySign);
				String xml = CommonUtil.ArrayToXml(map);
				String prepayid = payWxUtil.getPrepayid(xml);

				// save order history
				CloudFundHistory history = new CloudFundHistory();
				BigDecimal bd = new BigDecimal(Double.valueOf(money) / 100);
				bd.setScale(2, BigDecimal.ROUND_HALF_UP);
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
				String paySign2 = SignUtil.getPayCustomSign(signMap, provider.getKey());

				AjaxJson json = new AjaxJson();
				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put("timeStamp", timestamp + "");
				dataMap.put("appId", appid);
				dataMap.put("package", "prepay_id=" + prepayid);
				dataMap.put("nonceStr", nonceStr);
				dataMap.put("signType", "MD5");
				dataMap.put("paySign", paySign2);
				dataMap.put("wxpaytype", type + "");
				// "appId":data.appId, //公众号名称，由商户传入
				// "nonceStr":data.nonceStr, //随机串
				// "package":data.prepay_id,
				// "paySign":data.paySign, //微信签名
				// "signType":data.signType, //微信签名方式:
				// "timeStamp":data.timeStamp //时间戳，自19
				json.setData(dataMap);
				logger.info("paySign=" + paySign2);
				return json;
			} else {
				return null;
			}
		}
	}

	@RequestMapping(value = "/indexwx")
	@ResponseBody
	public Object indexwx(Model model, HttpServletRequest request) throws Exception {
		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		String openid = user.getOpenid();
		logger.info("************openid***********为：" + user.getOpenid());
		// 获取prepayid
		Map<String, String> map = new HashMap<String, String>();
		// WeiXinConfig wcf=weiXinBaseService.getWeiXinConfig();
		WXPayConfig WebConfig = WXPayConfig.getInstance();
		int providerId = sessionUser.getWxProviderId();
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(providerId);
		
		String nonceStr = UUID.randomUUID().toString().substring(0, 32);
		// oauthService.shareFactory(request);
		String appid = provider.getAppId();
		long timestamp = System.currentTimeMillis() / 1000;
		PayWXUtil payWxUtil = new PayWXUtil();

		map.put("appid", appid);
		map.put("mch_id", provider.getMchId());
		map.put("nonce_str", nonceStr);
		map.put("body", WebConfig.get("pay.body"));
		map.put("out_trade_no", payWxUtil.orderNum());
		map.put("total_fee", WebConfig.get("pay.price"));
		map.put("spbill_create_ip", request.getRemoteAddr());
		map.put("notify_url", WebConfig.get("hostAddress") + request.getContextPath() + "/wx/pay_notify");
		logger.info("notify_url is:" + map.get("notify_url"));
		map.put("trade_type", "JSAPI");
		map.put("openid", openid);
		String paySign = SignUtil.getPayCustomSign(map, provider.getKey());
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
		String paySign2 = SignUtil.getPayCustomSign(signMap, provider.getKey());

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

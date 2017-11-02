package com.lxtx.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.model.GUserLogin;
import com.lxtx.service.order.OrderService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.CommonUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.IntegrationConfig;
import com.lxtx.util.Md5Util;
import com.lxtx.util.RewardConfig;
import com.lxtx.util.cache.GeneralCache;
import com.lxtx.util.db.DomainHandler;
import com.lxtx.util.db.RedPacketUserHandler;
import com.lxtx.util.net.CookieManager;
import com.lxtx.util.tencent.PayWXUtil;
import com.lxtx.util.tencent.SignUtil;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tencent.WXPayConstants;
import com.lxtx.util.tencent.Xml2JsonUtil;
import com.lxtx.util.tool.HttpRequest;
import com.lxtx.util.tool.JsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/wx")
public class WXPayController {
	
	private static final String SOURCE_REDPACKET = "redpacket";
	
	private static final Logger logger = LoggerFactory.getLogger(WXPayController.class);
	@Autowired
	private GeneralCache generalCache;
	@Autowired
	private UserService userService;
	@Autowired
	private OrderService orderService;

	
	
	@RequestMapping(value = "/moneyout/{openid}/{source}")
	@ResponseBody
	public Object updatebalance(@PathVariable("openid") String openid, @PathVariable("source") String source, 
			HttpServletRequest request, HttpServletResponse response) {
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
				if (Strings.isNullOrEmpty(source)) {
					return ImmutableMap.of("code", -5, "message", "invalid source parameter");
				} else {
					if (source.equals(SOURCE_REDPACKET)) {
						try {
							double balance = RedPacketUserHandler.queryBalance(openid);
							if (balance < amount) {
								return ImmutableMap.of("code", -4, "message", "Balance lower than the amount to be transferred.");
							} else {
								int result = RedPacketUserHandler.reduceUserBalance(amount, openid);
								if (result == 1) {
									Integer gameUserId = RedPacketUserHandler.getGameUIdByWxid(openid);
									if (gameUserId != null) {
										// save fund transfer history
										CloudFundHistory history = new CloudFundHistory();
										history.setAmount(new BigDecimal(amount));
										history.setStatus(1);
										history.setType(3);// transfer to yiyuango
										history.setUid(gameUserId);
										Date d = new Date();
										history.setTime(d);
										String tradeNo = "go" + source + gameUserId.intValue() + d.getTime();
										history.setWxTradeNo(tradeNo);
										this.orderService.saveRefillRecord(history);	
									}
									return ImmutableMap.of("code", 0, "message", "amount from redpacket " + amount + " has been reduced.");
								} else {
									return ImmutableMap.of("code", -3, "message", "reducing the balance failed.");
								}
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return ImmutableMap.of("code", -6, "message", "unknown reason");
			}
		}
	}
	
	@RequestMapping(value = "/moneyout/{openid}")
	@ResponseBody
	public Object updatebalance(@PathVariable("openid") String openid, HttpServletRequest request,
			HttpServletResponse response) {
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
					long carryBalance = user.getCarryAmount() / 10000;
					double realBalance = user.getBalance().doubleValue() + carryBalance;
					if (realBalance < amount) {
						map.put("code", -4);
						map.put("message", "Balance lower than the amount to be transferred.");
					} else {
						try {
							int uid = user.getId();
							if (amount <= carryBalance) {
								this.userService.reduceCoinById((long) (amount * 10000), uid);
							} else {
								double balanceToReduce = amount - carryBalance;
								this.userService.reduceCoinById((long) (carryBalance * 10000), uid);
								this.userService.reduceBalanceById(new BigDecimal(balanceToReduce), uid);
							}

							// save fund transfer history
							CloudFundHistory history = new CloudFundHistory();
							history.setAmount(new BigDecimal(amount));
							history.setStatus(1);
							history.setType(3);// transfer to yiyuango
							history.setUid(user.getId());
							Date d = new Date();
							history.setTime(d);
							String tradeNo = "go" + user.getId() + d.getTime();
							history.setWxTradeNo(tradeNo);
							this.orderService.saveRefillRecord(history);
							map.put("code", 0);
							map.put("message", "amount of trade account has been reduced.");
						} catch (Exception e) {
							logger.error(e.getMessage());
							map.put("code", -1);
							map.put("message", "failed in change the account balance.");
						}
					}
					return map;
				}
			}
		}
	}

	@RequestMapping(value = "/querybalance/{openid}")
	@ResponseBody
	public Object querybalance(@PathVariable("openid") String openid, HttpServletRequest request,
			HttpServletResponse response) {
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
			int carryAmt = (int) (user.getCarryAmount() / 10000);
			map.put("balance", user.getBalance().intValue() + carryAmt);
			return map;
		}
	}
	
	@RequestMapping(value = "/querybalance/{openid}/{source}")
	@ResponseBody
	public Object querybalance(@PathVariable("openid") String openid, @PathVariable("source") String source,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("in querybalance:" + openid);
		Map<String, Object> map = new HashMap<String, Object>();
		if (Strings.isNullOrEmpty(openid)) {
			map.put("code", -1);
			map.put("message", "invalid openid");
			return map;
		}

		if (!Strings.isNullOrEmpty(source)) {
			if (source.equals(SOURCE_REDPACKET)) {
				Double balance = 0.0d;
				try {
					balance = RedPacketUserHandler.queryBalance(openid);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				map.put("code", 0);
				map.put("balance", balance);
			}
		}
		
		return map;
	}	
	
	@RequestMapping(value = "/redirectforred/{wxid}/{chnno}")
	public String redirectforred(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/redirectforredpacket/%s/%s&"
				+ "response_type=code&scope=snsapi_base&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}
	
	@RequestMapping(value = "/redirectredfull/{wxid}/{chnno}")
	public String redirectredfull(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/redirectforredfull/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";
		
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}		
	
	/**
	 * This action is used as the following link
	 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_url=%s/plaza/wx/redirectforredpacket/%s/%s
	 * &response_type=code&scope=snsapi_base&state=123
	 * In this silent mode, no outstanding authorization is required
	 * @param wxid
	 * @param chnno
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/redirectforredpacket/{wxid}/{chnno}")
	public String redirectForRedPacket(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("provider info:");
		if (provider != null) {
			String code = request.getParameter("code");
			if (Strings.isNullOrEmpty(code)) {
				logger.warn("empty code, redirect now.");
				return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad262d141e839b2c&redirect_uri=http://game.juzizou.com/plaza/wx/redirectforredpacket/"
						+ wxid + "/" + chnno + "&response_type=code&scope=snsapi_base&state=123";
			}			
			logger.info("in wxRedirect, received code:" + code);
			String openid = this.getWxidViaOAuth(provider, code);
			if (Strings.isNullOrEmpty(openid)) {
				logger.warn("empty openid, redirect now.");
				return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad262d141e839b2c&redirect_uri=http://game.juzizou.com/plaza/wx/redirectforredpacket/"
						+ wxid + "/" + chnno + "&response_type=code&scope=snsapi_base&state=123";
			}
			CloudUser user = this.userService.selectByWxid(openid);
			if (user == null) {
				user = new CloudUser();
				user.setCarryAmount(0l);
				user.setWxid(openid);
				user.setChnno(chnno);
				user.setCrtTm(new Date());
				user.setIdentity(0);
				user.setBalance(BigDecimal.ZERO);
				user.setWxProviderId(provider.getId());
				this.userService.insertUser(user);
				logger.info("id of created user:" + user.getId());
				CloudUser userToUpdate = new CloudUser();
				userToUpdate.setId(user.getId());
				userToUpdate.setWxnm("游戏用户"+user.getId());
				this.userService.updateUserSelectively(userToUpdate);
			}
			
			request.getSession().setAttribute(Constant.SESSION_USER, user);
			return "redirect:/platform/redpacket";
		} else {
			logger.info("provider is null");
			return "";
		}
	}
	
	@RequestMapping(value = "/redirectforredfull/{wxid}/{chnno}/{roomId}")
	public String redirectForRedPacket(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno, @PathVariable("roomId") String roomId,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		if (provider != null) {
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				logger.info("failure in retrieving weixin userinfo, redirect now.");
				String url = provider.getDomain();
				return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad262d141e839b2c&redirect_uri=" + url + "/plaza/wx/redirectforredfull/"
				+ wxid + "/" + chnno + "/" + roomId + "&response_type=code&scope=snsapi_userinfo&state=123";				
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			
			try {
				return this.getRedPacketRedirectUrl(request, user);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
				return "redirect:/platform/redpacket";
			}
		} else {
			logger.info("invalid provider");
			return "";
		}				
	}
	
	/**
	 * 公众号菜单中跳转到红包的接口
	 * @param wxid
	 * @param chnno
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/redirectforredfull/{wxid}/{chnno}")
	public String redirectForRedFull(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		if (provider != null) {
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				logger.info("failure in retrieving weixin userinfo, redirect now.");
				String url = provider.getDomain();
				return "redirect:https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxad262d141e839b2c&redirect_uri=" + url + "/plaza/wx/redirectforredfull/"
				+ wxid + "/" + chnno + "&response_type=code&scope=snsapi_userinfo&state=123";				
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			
			logger.warn("before getting redirecturl.");
			try {
				String redirectUrl = this.getRedPacketRedirectUrl(request, user);
				logger.warn("redirecturl is:" + redirectUrl);
				return "redirect:" + redirectUrl;
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
				return "";
			}
		} else {
			logger.info("invalid provider");
			return "";
		}		
	}	
	
	private String getRedPacketRedirectUrl(HttpServletRequest request, CloudUser user) throws UnsupportedEncodingException {
		String config = request.getParameter("config");
		if (config == null) {
			config = "";
		}
		String wxid = user.getWxid();
		int gameUserId = user.getId();
		double balance = user.getCarryAmount() / 10000.0;
		String token = Md5Util.MD5Encode(wxid + "game", "utf-8").toLowerCase();
		// String host = IntegrationConfig.getRedpacketHost();
		String host = DomainHandler.getRedPacketActiveDomain();
		if (Strings.isNullOrEmpty(host)) {
			host = IntegrationConfig.getRedpacketHost();
		}
		String nickname = URLEncoder.encode(user.getWxnm(), "UTF-8");
		logger.warn("redirect to redpacket, nickname is:" + nickname);
		String path = "http://" + host + "/accessfromgame?wxid=" + wxid + "&token=" + token + "&gameUserId="
				+ gameUserId + "&test=1&balance=" + balance + "&nickname=" + nickname + "&chnno=" + user.getChnno();
		if (!Strings.isNullOrEmpty(config)) {
			path += "&config=" + config;
		}
		return path;
	}

	@RequestMapping(value = "/gredirect/{wxid}/{chnno}")
	public String gredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/goredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/redirect/{wxid}/{chnno}")
	public String redirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/wredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/wredirect/{wxid}/{chnno}")
	public String wRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		if (provider != null) {
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				// return "error/999";
				logger.info("failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/redirect/" + wxid + "/" + chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			return "redirect:/home";
		} else {
			logger.info("invalid provider");
			return "";
		}
	}

	@RequestMapping(value = "/skredirect/{wxid}/{chnno}")
	public String skredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/skwredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/skwredirect/{wxid}/{chnno}")
	public String skwRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("123");
		if (provider != null) {
			logger.info("provider is not null");
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				// return "error/999";
				logger.info("skwRedirect failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/skredirect/" + wxid + "/" + chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			// 创建或新增cookie
			GUserLogin uLogin = new GUserLogin();
			String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
			if (StringUtils.isNotEmpty(cookie)) {
				GUserLogin uL = userService.selectGuserByCookie(cookie);
				if (null == uL) {
					cookie = UUID.randomUUID().toString();
				} else {
					if (user.getId().intValue() == uL.getUid().intValue()) {
					} else {
						cookie = UUID.randomUUID().toString();
					}
				}
			} else {
				cookie = UUID.randomUUID().toString();
			}
			uLogin.setCookie(cookie);
			logger.info("dice create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
			uLogin.setUid(user.getId());
			uLogin.setLoginTime(new Date());
			int uptCount = userService.updateGUserLogin(uLogin);
			if (uptCount == 0) {
				userService.insertGUserLogin(uLogin);
			}
			CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
			return "redirect:/skhome";
		} else {
			logger.info("invalid provider");
			return "";
		}
	}

	@RequestMapping(value = "/lcarredirect/{wxid}/{chnno}")
	public String lcarredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/lcarwredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/lcarwredirect/{wxid}/{chnno}")
	public String lcarwredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("123");
		if (provider != null) {
			logger.info("provider is not null");
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				// return "error/999";
				logger.info("lcarwredirect failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/lcarredirect/" + wxid + "/" + chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			// 判断是否有cookie,没有则创建
			GUserLogin uLogin = new GUserLogin();
			String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
			if (StringUtils.isNotEmpty(cookie)) {
				GUserLogin uL = userService.selectGuserByCookie(cookie);
				if (null == uL) {
					cookie = UUID.randomUUID().toString();
				} else {
					if (user.getId().intValue() == uL.getUid().intValue()) {
					} else {
						cookie = UUID.randomUUID().toString();
					}
				}
			} else {
				cookie = UUID.randomUUID().toString();
			}
			uLogin.setCookie(cookie);
			logger.info("car create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
			uLogin.setUid(user.getId());
			uLogin.setLoginTime(new Date());
			int uptCount = userService.updateGUserLogin(uLogin);
			if (uptCount == 0) {
				userService.insertGUserLogin(uLogin);
			}
			CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
			return "redirect:/luxurycarhome";
		} else {
			logger.info("invalid provider");
			return "";
		}
	}

	@RequestMapping(value = "/sxredirect/{wxid}/{chnno}")
	public String sxredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/sxwredirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/sxwredirect/{wxid}/{chnno}")
	public String sxwredirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("123");
		if (provider != null) {
			logger.info("provider is not null");
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				// return "error/999";
				logger.info("sxwredirect failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/sxredirect/" + wxid + "/" + chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
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
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			// 创建或新增cookie
			GUserLogin uLogin = new GUserLogin();
			String cookie = CookieManager.getCookie(request, "shake_home_login_auth");
			if (StringUtils.isNotEmpty(cookie)) {
				GUserLogin uL = userService.selectGuserByCookie(cookie);
				if (null == uL) {
					cookie = UUID.randomUUID().toString();
				} else {
					if (user.getId().intValue() == uL.getUid().intValue()) {
					} else {
						cookie = UUID.randomUUID().toString();
					}
				}
			} else {
				cookie = UUID.randomUUID().toString();
			}
			uLogin.setCookie(cookie);
			logger.info("animal create cookie:uid=" + user.getId() + ",cookie=" + uLogin.getCookie());
			uLogin.setUid(user.getId());
			uLogin.setLoginTime(new Date());
			int uptCount = userService.updateGUserLogin(uLogin);
			if (uptCount == 0) {
				userService.insertGUserLogin(uLogin);
			}
			CookieManager.setCookie("shake_home_login_auth", uLogin.getCookie(), -1, response);
			return "redirect:/animalhome";
		} else {
			logger.info("invalid provider");
			return "";
		}
	}

	@RequestMapping(value = "/gameCenterRedirect/{wxid}/{chnno}")
	public String gameCenterRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		String redirectUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?"
				+ "appid=%s&redirect_uri=%s/plaza/wx/gameCenterWRedirect/%s/%s&"
				+ "response_type=code&scope=snsapi_userinfo&state=123";

		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		String appId = provider.getAppId();
		String domain = provider.getDomain();

		return "redirect:" + String.format(redirectUrl, appId, domain, wxid, chnno);
	}

	@RequestMapping(value = "/gameCenterWRedirect/{wxid}/{chnno}")
	public String gameCenterWRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info(wxid + "  " + chnno);
		CloudWxServiceProvider provider = generalCache.getWxServiceProviderByOrigin(wxid);
		logger.info("123");
		if (provider != null) {
			logger.info("provider is not null");
			String code = request.getParameter("code");
			logger.info("code is:" + code);
			UserInfo map = getWXUserInfoViaOAuth(provider, code, request);
			if (map == null || StringUtils.isEmpty(map.getOpenid())) {
				// return "error/999";
				logger.info("failure in retrieving weixin userinfo, redirect now.");
				return "redirect:/gameCenterRedirect/" + wxid + "/" + chnno;
			}
			map.setChnno(chnno);
			map.setWxProviderId(provider.getId());
			logger.info("get weixin user info:" + map.getHeadimgurl() + map.getNickname() + map.getOpenid()
					+ map.getChnno() + map.getWxProviderId());
			HttpSession session = request.getSession();
			session.setAttribute(WXPayConstants.WX_USER_INFO, map);
			CloudUser user = userService.wxVisit(map);
			CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
			if (null == sessionUser) {
			} else {
				user.setUserStatus(sessionUser.getUserStatus());
				if (StringUtils.isNotEmpty(sessionUser.getLoginStatus())) {
					user.setLoginStatus(sessionUser.getLoginStatus());
				}
			}
			session.setAttribute("wxid", user.getWxid());
			session.setAttribute(Constant.SESSION_USER, user);
			return "redirect:/game_center/" + user.getWxid() + "/" + chnno;
		} else {
			logger.info("invalid provider");
			return "";
		}
	}

	private String getOpenidViaOAuth(CloudWxServiceProvider provider, String code, HttpServletRequest request) {
		String appId = provider.getAppId();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getAppSecret(); // WebConfig.get("pay.appsecret");
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
	
	private String getWxidViaOAuth(CloudWxServiceProvider provider, String code) {
		String appId = provider.getAppId();
		//这里的appsecret是微信商户账户对应的公众号账户的appsecret
		String appSecret = provider.getAppSecret(); // WebConfig.get("pay.appsecret");
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
		UserInfo obj = (UserInfo) request.getSession().getAttribute("wx_user_info");
		logger.info("in getWXUserInfoViaOAuth, code is: " + code);
		if (obj != null) {
			String savedCode = (String) request.getSession().getAttribute("WX_CODE");
			if (!Strings.isNullOrEmpty(savedCode) && savedCode.equals(code)) {
				logger.info("retrieved user info from session, no need to retrieve access token again.");
				return obj;
			} else {
				return obj;
			}
		}

		String appId = provider.getAppId();
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = provider.getAppSecret(); // WebConfig.get("pay.appsecret");
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
		// save the information in session
		request.getSession().setAttribute("WX_CODE", code);
		UserInfo userInfo = new UserInfo();
		userInfo.setHeadimgurl((String) map.get("headimgurl"));
		userInfo.setNickname((String) map.get("nickname"));
		userInfo.setOpenid((String) map.get("openid"));
		return userInfo;
	}

	@RequestMapping(value = "/goredirect/{wxid}/{chnno}")
	public String goRedirect(@PathVariable("wxid") String wxid, @PathVariable("chnno") String chnno,
			HttpServletRequest request, HttpServletResponse response) {
		logger.info("in goredirect, wxid is:" + wxid);
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
			String hash = Md5Util.MD5Encode(headimgurl + openid + "china", "UTF-8");
			logger.info("hash is:" + hash);
			return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl=" + headimgurl
					+ "&nickname=" + URLEncoder.encode(nickname) + "&openid=" + openid + "&source=" + chnno + "&domain="
					+ provider.getDomain() + "&h=" + hash;
		} else {
			logger.info("provider is null");
			return "";
		}
	}

	
	
	@RequestMapping(value = "/duobaoredirect/{p}")
	public String duobaoredirect(@PathVariable("p") String path, HttpServletRequest request,
			HttpServletResponse response) {
		UserInfo map = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		if (map != null) {
			String chnno = map.getChnno();
			int providerId = map.getWxProviderId();
			CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(providerId);
			logger.info("in goredirect - 2, wxid is:" + provider.getName());
			if (provider != null) {
				String headimgurl = map.getHeadimgurl();
				String nickname = map.getNickname();
				String openid = map.getOpenid();
				String hash = Md5Util.MD5Encode(headimgurl + openid + "china", "UTF-8");
				logger.info("hash is:" + hash);
				
				String source = request.getParameter("source");
				
				String targeturi = "/mobile/home";
				if (path.equals("transferfromgame")) {
					if (!Strings.isNullOrEmpty(source)) { 
						//we need to support transfer money from different source sites to duobao site
						targeturi = "/mobile/home/transferfromgame?source=" + source;
					} else {
						targeturi = "/mobile/home/transferfromgame";
					}
				} else if (path.equals("cardexchangetasks")) {
					targeturi = "/mobile/home/cardexchangetasks";
				}
				return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl=" + headimgurl
						+ "&nickname=" + URLEncoder.encode(nickname) + "&openid=" + openid + "&source=" + chnno
						+ "&domain=" + provider.getDomain() + "&h=" + hash + "&targeturi=" + targeturi;
			} else {
				return "";
			}
		} else {
			logger.info("provider is null");
			return "";
		}
	}
	
	/**
	 * @param wxid - user openid
	 * @param path 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/duobaoredirect/{wxid}/{p}")
	public String duobaoredirectall(@PathVariable("wxid") String wxid, @PathVariable("p") String path, HttpServletRequest request,
			HttpServletResponse response) {
		String source = request.getParameter("source");
		CloudUser user = this.userService.selectByWxid(wxid);
		if (user != null) {
			String targeturi = "/mobile/home";
			if (path.equals("transferfromgame")) {
				if (!Strings.isNullOrEmpty(source)) { 
					//we need to support transfer money from different source sites to duobao site
					targeturi = "/mobile/home/transferfromgame";
				} else {
					targeturi = "/mobile/home/transferfromgame";
				}
			} else if (path.equals("cardexchangetasks")) {
				targeturi = "/mobile/home/cardexchangetasks";
			}
			int providerId = user.getWxProviderId();
			CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(providerId);
			
			String headimgurl = user.getHeadimgurl();
			if(StringUtils.isEmpty(headimgurl)){
				return "redirect:/enterShellCodePage/"+wxid+"/"+source;
			}
			String openid = wxid;
			String hash = Md5Util.MD5Encode(headimgurl + openid + "china", "UTF-8");
			
			return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl=" + user.getHeadimgurl()
					+ "&nickname=" + URLEncoder.encode(user.getWxnm()) + "&openid=" + wxid + "&source=" + user.getChnno()
					+ "&domain=" + provider.getDomain() + "&h=" + hash + "&targeturi=" + targeturi+ "&site="+source;
		} else {
			return "";
		}
	}
	/**
	 * @param wxid - user openid
	 * @param path 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/duobaoredirect1/{wxid}/{p}")
	public String duobaoredirectall1(@PathVariable("wxid") String wxid, @PathVariable("p") String path, HttpServletRequest request,
			HttpServletResponse response) {
		CloudUser user = this.userService.selectByWxid(wxid);
		String source = request.getParameter("source");
		if (user != null) {
			String targeturi = "/mobile/home";
			if (path.equals("transferfromgame")) {
				if (!Strings.isNullOrEmpty(source)) { 
					//we need to support transfer money from different source sites to duobao site
					targeturi = "/mobile/home/transferfromgame";
				} else {
					targeturi = "/mobile/home/transferfromgame";
				}
			} else if (path.equals("cardexchangetasks")) {
				targeturi = "/mobile/home/cardexchangetasks";
			}
			int providerId = user.getWxProviderId();
			CloudWxServiceProvider provider = generalCache.getWxServiceProviderById(providerId);
			
			String headimgurl = user.getHeadimgurl();
			String openid = wxid;
			String hash = Md5Util.MD5Encode(headimgurl + openid + "china", "UTF-8");
			
			return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl=" + user.getHeadimgurl()
			+ "&nickname=" + URLEncoder.encode(user.getWxnm()) + "&openid=" + wxid + "&source=" + user.getChnno()
			+ "&domain=" + provider.getDomain() + "&h=" + hash + "&targeturi=" + targeturi+ "&site="+source;
		} else {
			return "";
		}
	}

	@RequestMapping(value = "/goforward")
	public String goforward(HttpServletRequest request, HttpServletResponse response) {
		String targetUri = request.getParameter("targeturi");
		String wxid = request.getParameter("wxid");
		CloudUser user = this.userService.selectByWxid(wxid);

		if (user == null) {
			return "";
		} else {
			String hash = Md5Util.MD5Encode(user.getHeadimgurl() + wxid + "china", "UTF-8");
			logger.info("hash is:" + hash);
			return "redirect:http://m.ninestate.com.cn/index.php/mobile/mobile/wxlogin?headimgurl="
					+ user.getHeadimgurl() + "&nickname=" + URLEncoder.encode(user.getWxnm()) + "&openid=" + wxid
					+ "&source=" + user.getChnno() + "&targeturi=" + targetUri + "&h=" + hash;
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
	public String wxRedirect(@PathVariable("wxid") String wxid, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
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
			session.setAttribute("wxid", user.getWxid());
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
		logger.info("session user is:" + (sessionUser == null ? "null" : sessionUser.getId()));

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
			try {
				CloudFundHistory history = new CloudFundHistory();
				history.setAmount(new BigDecimal(price));
				history.setNotifyStatus(0);
				history.setStatus(0);
				history.setTime(new Date());
				history.setUid(sessionUser.getId());
				history.setWxTradeNo(orderId);
				history.setType(Constant.FUND_HISTORY_FILL); // 1 means fill
				orderService.saveRefillRecord(history);
			} catch (Exception e) {
				logger.error(e.getMessage());   
			}

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
			map.put("notifyUrl", "http://139.129.227.199" + request.getContextPath() + "/wx/pay_notify");
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

	/**
	 * 摇一摇：游戏中心充值
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/shake_fill")
	@ResponseBody
	public Object shakeFill(Model model, HttpServletRequest request) throws Exception {
		return getAjaxJson(request);
	}

	private AjaxJson getAjaxJson(HttpServletRequest request) throws Exception {
		String sourceSite = request.getParameter("source");
		String config = request.getParameter("config");
		if (sourceSite == null) {
			sourceSite = "";
		}
		if (config == null) {
			config = "";
		}
		
		CloudUser sessionUser = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		logger.info("session user is:" + (sessionUser == null ? "null" : sessionUser.getId()));

		UserInfo user = (UserInfo) request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		WXPayConfig WebConfig = WXPayConfig.getInstance();
		String appId = WebConfig.get("jxt.appId");
		String payType = WebConfig.get("jxt.payType");
		String appKey = WebConfig.get("jxt.appKey");

		Random r = new Random();
		String orderId = "wx" + sourceSite + System.currentTimeMillis() + r.nextInt(10);
		String money = request.getParameter("money");
		logger.info("money is :" + money);
		double price = 0.0;
		if (money.equals("100")) {
			price = 0.01;
		} else {
			price = Integer.valueOf(money) / 100;
		}

		// save history record
		try {
			CloudFundHistory history = new CloudFundHistory();
			history.setAmount(new BigDecimal(price));
			history.setNotifyStatus(0);
			history.setStatus(0);
			history.setTime(new Date());
			history.setUid(sessionUser.getId());
			history.setWxTradeNo(orderId);
			history.setType(Constant.FUND_HISTORY_FILL); // 1 means fill
			RewardConfig reward = RewardConfig.getInstance();
			BigDecimal repayAmount = fillRepayAmount(reward, history.getAmount());
			history.setRepayAmount(repayAmount);
			orderService.saveRefillRecord(history);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

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

		if (!Strings.isNullOrEmpty(sourceSite)) {
			if(!Strings.isNullOrEmpty(config)){
				logger.info("config:"+config);
				map.put("callBackUrl", domain + request.getContextPath() + "/wx/shake_pay_callback/" + sourceSite+"/"+config);
			}else{
				map.put("callBackUrl", domain + request.getContextPath() + "/wx/shake_pay_callback/" + sourceSite);
			}
		} else {
			map.put("callBackUrl", domain + request.getContextPath() + "/wx/shake_pay_callback");
		}
		
		if (!Strings.isNullOrEmpty(sourceSite)) {
			map.put("notifyUrl", "http://139.129.227.199" + request.getContextPath() + "/wx/shake_pay_notify/" + sourceSite);
		} else {
			map.put("notifyUrl", "http://139.129.227.199" + request.getContextPath() + "/wx/shake_pay_notify");
		}
		json.setData(map);
		return json;
	}

	@RequestMapping(value = "/shake_pay_callback/{source}/{config}")
	public ModelAndView payCallback1(@PathVariable("source") String source,@PathVariable("config") String config, HttpServletRequest request) {
		logger.info("handle weixin payment shakePayCallback.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		String orderId = request.getParameter("orderId");
		CloudFundHistory history = orderService.getFundByOrderId(orderId);
		int uid = history.getUid();
		CloudUser user = userService.selectUserById(uid);
		
		ModelAndView mav = new ModelAndView("shake/callback");
		mav.addObject("userInfo", user);
		mav.addObject("price", request.getParameter("price"));
		mav.addObject("source", source);
		mav.addObject("config", config);
		return mav;
	}
	
	@RequestMapping(value = "/shake_pay_callback/{source}")
	public ModelAndView payCallback(@PathVariable("source") String source, HttpServletRequest request) {
		logger.info("handle weixin payment shakePayCallback.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		String orderId = request.getParameter("orderId");
		CloudFundHistory history = orderService.getFundByOrderId(orderId);
		int uid = history.getUid();
		CloudUser user = userService.selectUserById(uid);

		ModelAndView mav = new ModelAndView("shake/callback");
		mav.addObject("userInfo", user);
		mav.addObject("price", request.getParameter("price"));
		mav.addObject("source", source);
		return mav;
	} 
	
	
	@RequestMapping(value = "/shake_pay_callback")
	public ModelAndView shakePayCallback(HttpServletRequest request) {
		logger.info("handle weixin payment shakePayCallback.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		String orderId = request.getParameter("orderId");
		CloudFundHistory history = orderService.getFundByOrderId(orderId);
		int uid = history.getUid();
		CloudUser user = userService.selectUserById(uid);

		ModelAndView mav = new ModelAndView("shake/callback");
		mav.addObject("userInfo", user);
		mav.addObject("price", request.getParameter("price"));
		return mav;
	}

	@RequestMapping(value = "/shake_pay_notify")
	@ResponseBody
	public Object shake_pay_notify(HttpServletRequest request) throws Exception {
		logger.info("handle weixin shake payment notification.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		RewardConfig reward = RewardConfig.getInstance();
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
					// add money to user's carryAmount
					logger.info("update carryAmount of user" + history.getUid());
					int fillAmount = fillAmountReWard(reward, history.getAmount().doubleValue());
					userService.updateCarryAmountById(fillAmount * 10000, history.getUid());
				}
			}
		} else {
			String status = request.getParameter("status");

			if (!Strings.isNullOrEmpty(orderId) && status.equals("1")) {
				CloudFundHistory history = orderService.getFundByOrderId(orderId);
				if (history != null && history.getNotifyStatus() == 0) {
					orderService.updateRefillRecord(orderId);
					// add money to user's carryAmount
					logger.info("update carryAmount of user" + history.getUid());
					int fillAmount = fillAmountReWard(reward, Double.parseDouble(request.getParameter("price")));
					userService.updateCarryAmountById(fillAmount, history.getUid());
				}
			}
		}

		AjaxJson json = new AjaxJson();
		json.setCode(1);
		json.setData("true");
		json.setMessage("true");
		return json;
	}
	
	@RequestMapping(value = "/shake_pay_notify/{source}")
	@ResponseBody
	public Object shake_pay_notify(@PathVariable("source") String source, HttpServletRequest request) throws Exception {
		logger.info("handle weixin shake payment notification.");
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			logger.info("request" + key + " value:" + request.getParameter(key));
		}
		RewardConfig reward = RewardConfig.getInstance();
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
					// add money to user's carryAmount
					logger.info("update carryAmount of user" + history.getUid());
					int fillAmount = fillAmountReWard(reward, history.getAmount().doubleValue());
//					userService.updateCarryAmountById(fillAmount * 10000, history.getUid());
					if (source.equals("redpacket")) { //红包
						RedPacketUserHandler.updateCloudTargetIndex(history.getAmount().doubleValue(), history.getUid());
					}
				}
			}
		} else {
			String status = request.getParameter("status");

			if (!Strings.isNullOrEmpty(orderId) && status.equals("1")) {
				CloudFundHistory history = orderService.getFundByOrderId(orderId);
				if (history != null && history.getNotifyStatus() == 0) {
					orderService.updateRefillRecord(orderId);
					// add money to user's carryAmount
					logger.info("update carryAmount of user" + history.getUid());
					int fillAmount = fillAmountReWard(reward, Double.parseDouble(request.getParameter("price")));
//					userService.updateCarryAmountById(fillAmount, history.getUid());
					if (source.equals("redpacket")) {
						RedPacketUserHandler.updateCloudTargetIndex(Double.parseDouble(request.getParameter("price")), history.getUid());
					}
				}
			}
		}

		AjaxJson json = new AjaxJson();
		json.setCode(1);
		json.setData("true");
		json.setMessage("true");
		return json;
	}	
	

	/**
	 * 充值到账金币
	 * 
	 * @param reward
	 * @param fillPrice
	 * @return
	 */
	private static int fillAmountReWard(RewardConfig reward, Double fillPrice) {
		if (fillPrice.intValue() == 1000) {
			fillPrice *= Double.parseDouble(reward.get("reward1"));
		} else if (fillPrice.intValue() == 5000) {
			fillPrice *= Double.parseDouble(reward.get("reward2"));
		} else if (fillPrice.intValue() == 10000) {
			fillPrice *= Double.parseDouble(reward.get("reward3"));
		}
		fillPrice *= 10000;
		return fillPrice.intValue();
	}

	/**
	 * 充值后可提现的金额
	 * 
	 * @param reward
	 * @param fillPrice
	 * @return
	 */
	private static BigDecimal fillRepayAmount(RewardConfig reward, BigDecimal fillPrice) {
		if (fillPrice.intValue() == 1000) {
			fillPrice = fillPrice.multiply(new BigDecimal(reward.get("reward1")));
		} else if (fillPrice.intValue() == 5000) {
			fillPrice = fillPrice.multiply(new BigDecimal(reward.get("reward2")));
		} else if (fillPrice.intValue() == 10000) {
			fillPrice = fillPrice.multiply(new BigDecimal(reward.get("reward3")));
		}
		fillPrice = fillPrice.multiply(new BigDecimal(5)).multiply(new BigDecimal(0.97)).divide(new BigDecimal(6), 4,
				BigDecimal.ROUND_HALF_UP);

		return fillPrice;
	}

}

// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.entity.GcRoomLog;
import org.takeback.chat.entity.GoldApp;
import org.takeback.chat.entity.GoldFundHistory;
import org.takeback.chat.entity.InmineInfo;
import org.takeback.chat.entity.PubBank;
import org.takeback.chat.entity.PubWithdraw;
import org.takeback.chat.entity.PubRecharge;
import javax.servlet.http.HttpSession;
import org.takeback.chat.entity.PubExchangeLog;
import org.takeback.chat.entity.PcRateConfig;
import org.takeback.chat.entity.PcGameLog;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.utils.ValueControl;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.collections.map.HashedMap;
import org.takeback.chat.entity.TransferLog;

import java.util.List;
import org.takeback.chat.store.room.Room;
import java.util.ArrayList;

import org.takeback.chat.entity.GcChnKfInfo;
import org.takeback.chat.entity.GcLottery;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.entity.LoginLog;
import org.takeback.chat.entity.PcConfig;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import org.takeback.chat.utils.IPUtil;
import org.takeback.util.BeanUtils;
import org.takeback.util.MD5StringUtil;
import org.takeback.util.NetUtils;
import org.takeback.chat.store.user.User;
import java.util.Objects;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.takeback.mvc.listener.SessionListener;
import org.takeback.chat.utils.SmsUtil;
import org.takeback.util.valid.ValidateUtil;
import java.util.Date;
import org.takeback.util.encrypt.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.takeback.chat.entity.PubUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.util.annotation.AuthPassport;
import java.util.Map;
import javax.imageio.ImageReader;
import java.util.Iterator;
import javax.imageio.stream.ImageInputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.io.File;
import org.takeback.mvc.ResponseUtils;
import javax.imageio.ImageIO;
import org.springframework.web.util.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.takeback.thirdparty.support.WxConfig;
import org.takeback.chat.service.LotteryOpenService;
import org.takeback.chat.service.PcEggService;
import org.takeback.chat.service.RoomService;
import org.takeback.chat.service.SystemService;
import org.takeback.chat.store.room.RoomStore;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.UserService;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserStore userStore;
	@Autowired
	private RoomStore roomStore;
	@Autowired
	private SystemService systemService;
	@Autowired
	private RoomService roomService;
	@Autowired
	private PcEggService pcService;
	@Autowired
	private WxConfig wxConfig;

	private static Logger logger = LoggerFactory.getLogger(UserController.class);

	@AuthPassport
	@RequestMapping(value = { "/user/upload" }, method = { RequestMethod.POST })
	public ModelAndView upload(@RequestParam(value = "file", required = true) final MultipartFile file,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		final String path = request.getSession().getServletContext().getRealPath("img/user");
		String fileName = file.getOriginalFilename();
		try {
			final ImageInputStream iis = ImageIO.createImageInputStream(file);
			final Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
			if (!iter.hasNext()) {
				return ResponseUtils.jsonView(300, "文件格式错误!");
			}
			iis.close();
		} catch (Exception ex) {
		}
		fileName = "/" + uid + fileName.substring(fileName.lastIndexOf("."));
		final File targetFile = new File(path, fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		try {
			file.transferTo(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String headImage = "img/user" + fileName;
		this.userService.updateHeadImg(uid, headImage);
		final Map<String, Object> res = new HashMap<String, Object>();
		res.put("headImage", headImage + "?t=" + Math.random());
		this.userStore.reload(uid);
		return ResponseUtils.jsonView(200, "上传成功!", res);
	}

	@RequestMapping(value = { "/user/update" }, method = { RequestMethod.POST })
	public ModelAndView updateUser(@RequestBody final Map data, final HttpServletRequest request) {
		final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			return ResponseUtils.jsonView(403, "notLogin");
		}
		try {
			if (data.get("id") == null || !userId.equals(data.get("id"))) {
				return ResponseUtils.jsonView(500, "userId not matched");
			}
			
			if (data.containsKey("money")) {
				logger.info("trying to update money of " + userId + " " + (String)data.get("money"));
			}
			final PubUser pubUser = this.userService.updateUser(userId, data);
			this.userStore.reload(userId);
			return ResponseUtils.jsonView(200, "修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, "修改失败!");
		}
	}

	@AuthPassport
	@RequestMapping(value = { "/user/updatePsw" }, method = { RequestMethod.POST })
	public ModelAndView updatePsw(@RequestBody final Map data, final HttpServletRequest request) {
		final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			return ResponseUtils.jsonView(403, "notLogin");
		}
		try {
			if (data.get("id") == null || !userId.equals(data.get("id"))) {
				return ResponseUtils.jsonView(500, "userId not matched");
			}
			final Object oldPwd = data.get("oldPwd");
			if (oldPwd == null || oldPwd.toString().length() == 0) {
				return ResponseUtils.jsonView(500, "原密码不能为空!");
			}
			final Object newPwd = data.get("newPwd");
			final Object confirmPwd = data.get("confirmPwd");
			if (newPwd == null || newPwd.toString().length() == 0) {
				return ResponseUtils.jsonView(500, "新密码不能为空!");
			}
			if (!newPwd.equals(confirmPwd)) {
				return ResponseUtils.jsonView(500, "两次输入新密码不一致!");
			}
			final PubUser user = this.userService.get(userId, oldPwd.toString());
			if (user == null) {
				return ResponseUtils.jsonView(500, "原密码不正确!");
			}
			this.userService.updatePwd(userId,
					CryptoUtils.getHash(newPwd.toString(), StringUtils.reverse(user.getSalt())));
			return ResponseUtils.jsonView(200, "密码修改成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, "修改失败!");
		}
	}

	@AuthPassport
	@RequestMapping(value = { "/user/sendSmsCode" }, method = { RequestMethod.POST })
	public ModelAndView sendSmsCode(@RequestBody final Map data, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (uid == null) {
			return ResponseUtils.jsonView(403, "notLogin");
		}
		if (WebUtils.getSessionAttribute(request, "mobileCodeTime") != null) {
			final Date d1 = (Date) WebUtils.getSessionAttribute(request, "mobileCodeTime");
			final Date d2 = new Date();
			final Long deep = (d2.getTime() - d1.getTime()) / 1000L;
			if (deep <= 120L) {
				return ResponseUtils.jsonView(500, "请" + (120L - deep) + "秒后再尝试!");
			}
		}
		if (data.get("mobile") == null) {
			return ResponseUtils.jsonView(500, "手机号不能为空!");
		}
		final String mobile = data.get("mobile").toString();
		if (!ValidateUtil.instance().validatePhone(mobile)) {
			return ResponseUtils.jsonView(500, "手机号码格式不正确!");
		}
		final Long rand = Math.round(Math.random() * 1000000.0);
		final String msg = "您的验证码为:" + rand.toString();
		try {
			SmsUtil.sendMsg(mobile, msg);
			WebUtils.setSessionAttribute(request, "mobile", (Object) mobile);
			WebUtils.setSessionAttribute(request, "mobileCode", (Object) rand.toString());
			WebUtils.setSessionAttribute(request, "mobileCodeTime", (Object) new Date());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, "验证码发送失败!");
		}
		return ResponseUtils.jsonView(200, "验证码已成功发送!");
	}

	@AuthPassport
	@RequestMapping(value = { "/user/bindMobile" }, method = { RequestMethod.POST })
	public ModelAndView bindMobile(@RequestBody final Map data, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (uid == null) {
			return ResponseUtils.jsonView(403, "notLogin");
		}
		if (WebUtils.getSessionAttribute(request, "mobile") == null
				|| WebUtils.getSessionAttribute(request, "mobileCode") == null) {
			return ResponseUtils.jsonView(500, "手机号码绑定失败!");
		}
		final String sMobile = (String) WebUtils.getSessionAttribute(request, "mobile");
		final String smsCode = data.get("smsCode").toString();
		final String sCode = (String) WebUtils.getSessionAttribute(request, "mobileCode");
		if (!sCode.equals(smsCode)) {
			return ResponseUtils.jsonView(500, "验证码不正确!");
		}
		try {
			this.userService.bindMobile(uid, sMobile);
			WebUtils.setSessionAttribute(request, "mobile", (Object) null);
			WebUtils.setSessionAttribute(request, "mobileCode", (Object) null);
			WebUtils.setSessionAttribute(request, "mobileCodeTime", (Object) null);
			return ResponseUtils.jsonView(200, "手机号码绑定成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, "手机号码绑定失败!");
		}
	}

	@RequestMapping(value = { "/lottery/adminInfo" }, method = { RequestMethod.GET })
	public ModelAndView adminInfo() {
		final Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("withdraw", this.systemService.getWidthdraw());
		rs.put("online", SessionListener.getOnlineNumber());
		rs.put("recharge", 0);
		return ResponseUtils.jsonView(200, "ok", rs);
	}

	@AuthPassport
	@RequestMapping({ "/user/{uid}" })
	public ModelAndView getUser(@PathVariable final Integer uid, final HttpServletRequest request) {
		Object uidObj = WebUtils.getSessionAttribute(request, "$uid");
		final Integer userId;
		if (uidObj instanceof Integer) {
			userId = (Integer) uidObj;
		} else {
			userId = Integer.valueOf((String) uidObj).intValue();
		}

		if (uid.intValue() == userId.intValue()) {
			final PubUser user = this.userService.get(PubUser.class, uid);
			final User user2 = BeanUtils.map(user, User.class);
			user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
			return ResponseUtils.jsonView(user2);
		}
		logger.warn("in getUser, uid passed in is : " + uid == null ? "null" : (uid.intValue() + ""));
		return ResponseUtils.jsonView(501, "not authorized");
	}

	@AuthPassport
	@RequestMapping({ "/user/balance" })
	public ModelAndView getBalance(final HttpServletRequest request) {
		final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			String uid = request.getHeader("x-access-uid");
			String token = request.getHeader("x-access-token");
			PubUser user = this.userService.getById(Integer.valueOf(uid));
			if (user.getAccessToken().equals(token)) {
				return ResponseUtils.jsonView(this.userService.getBalance(Integer.valueOf(uid)));
			}
		}
		return ResponseUtils.jsonView(this.userService.getBalance(userId));
	}

	@RequestMapping(value = { "/login" }, method = { RequestMethod.POST })
	public ModelAndView login(@RequestBody final Map data, final HttpServletRequest request) {
		final String username = (String) data.get("username");
		final String password = (String) data.get("password");
		final String ip = IPUtil.getIp(request);
		final PubUser user = this.userService.login(username, password);
		if (user == null) {
			return ResponseUtils.jsonView(404, "用户不存在或者密码错误");
		}
		if ("9".equals(user.getUserType())) {
			return ResponseUtils.jsonView(404, "不能登陆非玩家账号!");
		}
		if ("2".equals(user.getStatus()) || "3".equals(user.getStatus())) {
			return ResponseUtils.jsonView(404, "账号被锁定或注销,请联系客服咨询处理!");
		}
		WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
		user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
		final LocalDateTime expire = new LocalDateTime().plusDays(7);
		user.setTokenExpireTime(expire.toDate());
		this.userService.updateUser(user.getId(),
				ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
		this.userService.setLoginInfo(ip, user.getId());
		final LoginLog l = new LoginLog();
		l.setIp(ip);
		l.setLoginTime(new Date());
		l.setUserId(user.getId());
		l.setUserName(user.getUserId());
		this.userService.save(LoginLog.class, l);
		final User user2 = BeanUtils.map(user, User.class);
		user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
		this.userStore.reload(user2.getId());
		return ResponseUtils.jsonView(user2);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/createUser" }, method = { RequestMethod.POST })
	public ModelAndView createUser(@RequestBody final Map data, final HttpServletRequest request) {
		final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		final String username = (String) data.get("username");
		final String password = (String) data.get("password");
		final String mobile = data.containsKey("mobile") ? (String) data.get("mobile") : "";
		final String wx = data.containsKey("wx") ? (String) data.get("wx") : "";
		final String alipay = data.containsKey("alipay") ? (String) data.get("alipay") : "";
		try {
			final Integer parentId = userId;
			final String ip = IPUtil.getIp(request);
			final PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
			if (user == null) {
				return ResponseUtils.jsonView(500, "注册失败");
			}
			user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
			final LocalDateTime expire = new LocalDateTime().plusDays(7);
			user.setTokenExpireTime(expire.toDate());
			this.userService.updateUser(user.getId(), ImmutableMap.of("accessToken", user.getAccessToken(),
					"tokenExpireTime", user.getTokenExpireTime()));
			return ResponseUtils.jsonView(200, "创建成功!");
		} catch (CodedBaseRuntimeException e) {
			return ResponseUtils.jsonView(e.getCode(), e.getMessage());
		}
	}
	
	@RequestMapping(value = { "/accessfromapp" }, method = { RequestMethod.GET })
	public ModelAndView accessfromapp(final HttpServletRequest request) {
		final String token = request.getParameter("token");
		final int gameUserId = Integer.valueOf(request.getParameter("gameUserId")).intValue();
		String nickname = request.getParameter("nickname");
		String chnno = request.getParameter("chnno");
		if (!Strings.isNullOrEmpty(nickname)) {
			try {
				nickname = URLDecoder.decode(nickname, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		logger.warn("access from app:" + nickname );

		if (token.equals(MD5StringUtil.MD5EncodeUTF8(chnno + gameUserId + "game"))) {
			PubUser user = userService.getByGameAccount(Integer.valueOf(chnno), gameUserId);
			
			String accessToken = UUID.randomUUID().toString().replace("-", "");
			final LocalDateTime expire = new LocalDateTime().plusDays(7);
			Date expireDate = expire.toDate();

			String param = request.getParameter("config");
			String jumpurl = Strings.isNullOrEmpty(param) ? "/#/tab/rooms" : "/#/tab/rooms" + "/" + param;

			if (user == null) {
				// to register
				user = userService.register(gameUserId, chnno, NetUtils.getIpAddr(request));
				if (user == null) {
					return ResponseUtils.jsonView(500, "注册失败");
				}
				user.setAccessToken(accessToken);
				user.setTokenExpireTime(expireDate);
				user.setLastLoginDate(new Date());
				if (Strings.isNullOrEmpty(nickname)) {
					user.setNickName("高级用户" + user.getId());
				} else {
					user.setNickName(nickname);
				}
				this.userService.updateUser(user.getId(),
						ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime",
								user.getTokenExpireTime(), "nickName", user.getNickName(), "userId",
								user.getNickName()));
				final User user2 = BeanUtils.map(user, User.class);
				user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
				this.userStore.reload(user2.getId());
				WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
			} else {
				Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
				if (userId != null && userId == user.getId()) {
					logger.warn("user already in session and jump, access token is:" + user.getAccessToken()
							+ " uid is:" + user.getId());
					return ResponseUtils.modelView("jump", ImmutableMap.of("url", jumpurl, "message", "", "uid",
							user.getId(), "username", user.getNickName(), "accessToken", user.getAccessToken()));
				}

				Date oldExpireDate = user.getTokenExpireTime();
				if (System.currentTimeMillis() - oldExpireDate.getTime() > 0) {// 已过期
					// need to renew
					user.setAccessToken(accessToken);
					user.setTokenExpireTime(expireDate);
					logger.warn("renew the access token and expire date for user:" + user.getId());
					if (!Strings.isNullOrEmpty(nickname) && !nickname.equals(user.getNickName())) {
						userService.updateUser(user.getId(), ImmutableMap.of("nickName", nickname, "accessToken",
								user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
					} else {
						userService.updateUser(user.getId(), ImmutableMap.of("accessToken", user.getAccessToken(),
								"tokenExpireTime", user.getTokenExpireTime()));
					}
				} else {
					// to update balance
					if (!Strings.isNullOrEmpty(nickname) && !nickname.equals(user.getNickName())) {
						userService.updateUser(user.getId(), ImmutableMap.of("nickName", nickname));
					}
				}
				
				userService.updateUser(user.getId(), ImmutableMap.of("lastLoginDate", new Date()));

				WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
				final User user2 = BeanUtils.map(user, User.class);
				user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
				this.userStore.reload(user2.getId());
			}
			
			String ip = IPUtil.getIp(request);
			final LoginLog l = new LoginLog();
			l.setIp(ip);
			l.setLoginTime(new Date());
			l.setUserId(user.getId());
			l.setUserName(user.getNickName());
			this.userService.save(LoginLog.class, l);

			logger.warn("in jump, access token is:" + user.getAccessToken() + " uid is:" + user.getId());
			return ResponseUtils.modelView("jump", ImmutableMap.of("url", jumpurl, "message", "", "uid", user.getId(),
					"username", user.getNickName(), "accessToken", user.getAccessToken()));
		} else {
			Exception ex = new Exception("参数校验错误");
			return ResponseUtils.modelView("500", "exception", ex);
		}
	}

	@RequestMapping(value = { "/accessfromgame" }, method = { RequestMethod.GET })
	public ModelAndView loginFromGame(final HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		logger.warn("in access from game, referer is :" + referer);
		String test = request.getParameter("test");
		if (Strings.isNullOrEmpty(referer) && Strings.isNullOrEmpty(test)) {
			return null;
		}
		final String wxid = request.getParameter("wxid");
		if (Strings.isNullOrEmpty(wxid)) {
			return null;
		}
		final String token = request.getParameter("token");
		final int gameUserId = Integer.valueOf(request.getParameter("gameUserId")).intValue();
		String nickname = request.getParameter("nickname");
		String chnno = request.getParameter("chnno");
		if (!Strings.isNullOrEmpty(nickname)) {
			try {
				nickname = URLDecoder.decode(nickname, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		final String balance = request.getParameter("balance");
		logger.warn(
				"access from game:" + wxid + " " + nickname + " balance : " + balance + " nickname is :" + nickname);
		double db = Double.valueOf(balance).doubleValue();

		if (token.equals(MD5StringUtil.MD5EncodeUTF8(wxid + "game"))) {
			PubUser user = userService.getByWxid(wxid);
			String accessToken = UUID.randomUUID().toString().replace("-", "");
			final LocalDateTime expire = new LocalDateTime().plusDays(7);
			Date expireDate = expire.toDate();

			String param = request.getParameter("config");
			String jumpurl = Strings.isNullOrEmpty(param) ? "/#/tab/rooms" : "/#/tab/rooms" + "/" + param;

			if (user == null) {
				// to register
				user = userService.register(wxid, gameUserId, "localhost", db, chnno);
				if (user == null) {
					return ResponseUtils.jsonView(500, "注册失败");
				}
				user.setAccessToken(accessToken);
				user.setTokenExpireTime(expireDate);
				user.setLastLoginDate(new Date());
				if (Strings.isNullOrEmpty(nickname)) {
					user.setNickName("高级用户" + user.getId());
				} else {
					user.setNickName(nickname);
				}
				this.userService.updateUser(user.getId(),
						ImmutableMap.of("accessToken", user.getAccessToken(), "tokenExpireTime",
								user.getTokenExpireTime(), "nickName", user.getNickName(), "userId",
								user.getNickName()));
				final User user2 = BeanUtils.map(user, User.class);
				user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
				this.userStore.reload(user2.getId());
				WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
			} else {
				Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
				if (userId != null && userId == user.getId()) {
					logger.warn("user already in session and jump, access token is:" + user.getAccessToken()
							+ " uid is:" + user.getId());
					return ResponseUtils.modelView("jump", ImmutableMap.of("url", jumpurl, "message", "", "uid",
							user.getId(), "username", user.getNickName(), "accessToken", user.getAccessToken()));
				}

				Date oldExpireDate = user.getTokenExpireTime();
				if (System.currentTimeMillis() - oldExpireDate.getTime() > 0) {// 已过期
					// need to renew
					user.setAccessToken(accessToken);
					user.setTokenExpireTime(expireDate);
					logger.warn("renew the access token and expire date for user:" + user.getId());
					if (!Strings.isNullOrEmpty(nickname) && !nickname.equals(user.getNickName())) {
						userService.updateUser(user.getId(), ImmutableMap.of("nickName", nickname, "accessToken",
								user.getAccessToken(), "tokenExpireTime", user.getTokenExpireTime()));
					} else {
						userService.updateUser(user.getId(), ImmutableMap.of("accessToken", user.getAccessToken(),
								"tokenExpireTime", user.getTokenExpireTime()));
					}
				} else {
					// to update balance
					if (!Strings.isNullOrEmpty(nickname) && !nickname.equals(user.getNickName())) {
						userService.updateUser(user.getId(), ImmutableMap.of("nickName", nickname));
					}
				}
				
				userService.updateUser(user.getId(), ImmutableMap.of("lastLoginDate", new Date()));

				WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
				final User user2 = BeanUtils.map(user, User.class);
				user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
				this.userStore.reload(user2.getId());
			}
			
			String ip = IPUtil.getIp(request);
			final LoginLog l = new LoginLog();
			l.setIp(ip);
			l.setLoginTime(new Date());
			l.setUserId(user.getId());
			l.setUserName(user.getNickName());
			this.userService.save(LoginLog.class, l);

			logger.warn("in jump, access token is:" + user.getAccessToken() + " uid is:" + user.getId());
			return ResponseUtils.modelView("jump", ImmutableMap.of("url", jumpurl, "message", "", "uid", user.getId(),
					"username", user.getNickName(), "accessToken", user.getAccessToken()));
		} else {
			Exception ex = new Exception("参数校验错误");
			return ResponseUtils.modelView("500", "exception", ex);
		}
	}

	@RequestMapping(value = { "/register" }, method = { RequestMethod.POST })
	public ModelAndView register(@RequestBody final Map data, final HttpServletRequest request) {
		final String username = (String) data.get("username");
		final String password = (String) data.get("password");
		final String mobile = data.containsKey("mobile") ? (String) data.get("mobile") : "";
		final String wx = data.containsKey("wx") ? (String) data.get("wx") : "";
		final String alipay = data.containsKey("alipay") ? (String) data.get("alipay") : "";
		try {
			Integer parentId = null;
			final Object invitor = WebUtils.getSessionAttribute(request, "$invitor");
			parentId = ((invitor == null) ? null : ((Integer) invitor));
			if (parentId == null && data.containsKey("parentId")) {
				parentId = (Integer) data.get("parentId");
			}
			final String ip = IPUtil.getIp(request);
			final PubUser user = this.userService.register(username, password, mobile, wx, alipay, parentId, ip);
			if (user == null) {
				return ResponseUtils.jsonView(500, "注册失败");
			}
			WebUtils.setSessionAttribute(request, "$uid", (Object) user.getId());
			user.setAccessToken(UUID.randomUUID().toString().replace("-", ""));
			final LocalDateTime expire = new LocalDateTime().plusDays(7);
			user.setTokenExpireTime(expire.toDate());
			this.userService.updateUser(user.getId(), ImmutableMap.of("accessToken", user.getAccessToken(),
					"tokenExpireTime", user.getTokenExpireTime()));
			final User user2 = BeanUtils.map(user, User.class);
			user2.setUrl(this.wxConfig.getGameServerBaseUrl() + "/i?u=" + user2.getId());
			this.userStore.reload(user2.getId());
			return ResponseUtils.jsonView(user2);
		} catch (CodedBaseRuntimeException e) {
			return ResponseUtils.jsonView(e.getCode(), e.getMessage());
		}
	}

	@RequestMapping({ "/user/myLottery" })
	public ModelAndView myLottery(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			return ResponseUtils.jsonView(403, "notLogin");
		}
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<GcLottery> list = this.userService.find(GcLottery.class, ImmutableMap.of("sender", userId), pageSize,
				pageNo, "createTime desc");
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
		for (final GcLottery gcLottery : list) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("createTime", gcLottery.getCreateTime());
			map.put("money", gcLottery.getMoney());
			final Room room = this.roomStore.get(gcLottery.getRoomId());
			map.put("roomName", (room == null) ? "不明" : room.getName());
			records.add(map);
		}
		return ResponseUtils.jsonView(records);
	}

	@AuthPassport
	@RequestMapping({ "/user/transfer" })
	public ModelAndView transfer(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		try {
			final Integer targetId = (Integer) params.get("userId");
			final Integer money = (Integer) params.get("money");
			//this.userService.transfer(uid, targetId, money);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
		return ResponseUtils.jsonView(200, "转账成功");
	}

	@AuthPassport
	@RequestMapping({ "/user/prixyRecharge" })
	public ModelAndView prixyRecharge(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		try {
			final Integer targetId = (Integer) params.get("userId");
			final Integer money = (Integer) params.get("money");
			//this.userService.prixyRecharge(uid, targetId, money);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
		return ResponseUtils.jsonView(200, "上分成功");
	}

	@AuthPassport
	@RequestMapping({ "/user/prixyUnRecharge" })
	public ModelAndView prixyUnRecharge(@RequestBody final Map<String, Object> params,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		try {
			final Integer targetId = (Integer) params.get("userId");
			final Integer money = (Integer) params.get("money");
			//this.userService.prixyUnRecharge(uid, targetId, money);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
		return ResponseUtils.jsonView(200, "下分成功");
	}

	@AuthPassport
	@RequestMapping({ "/user/prixyRechargeLog" })
	public ModelAndView prixyRechargeLog(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType order by id desc ";
		final Map map = new HashMap();
		map.put("uid", uid);
		map.put("rechargeType", "2");
		final List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/prixyUnRechargeLog" })
	public ModelAndView prixyUnRechargeLog(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final String hql = "from PubRecharge where operator=:uid and rechargeType=:rechargeType order by id desc ";
		final Map map = new HashMap();
		map.put("uid", uid);
		map.put("rechargeType", "3");
		final List<TransferLog> list = this.userService.findByHql(hql, map, pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/getNickName" }, method = { RequestMethod.POST })
	public ModelAndView getNickName(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
		try {
			final Integer uid = (Integer) params.get("uid");
			final PubUser u = this.userService.get(PubUser.class, uid);
			if (u == null) {
				return ResponseUtils.jsonView(500, "目标账号不存在!");
			}
			final Map m = (Map) new HashedMap();
			m.put("nickName", u.getNickName());
			m.put("money", u.getMoney());
			m.put("code", 200);
			return ResponseUtils.jsonView(m);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
	}

	@AuthPassport
	@RequestMapping(value = { "/user/checkRecharge" }, method = { RequestMethod.POST })
	public ModelAndView checkRecharge(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
		try {
			final Integer uid = (Integer) params.get("uid");
			final PubUser u = this.userService.get(PubUser.class, uid);
			if (u == null) {
				return ResponseUtils.jsonView(500, "目标账号不存在!");
			}
			final Map m = (Map) new HashedMap();
			m.put("nickName", u.getNickName());
			m.put("money", u.getMoney());
			m.put("code", 200);
			return ResponseUtils.jsonView(m);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
	}

	@AuthPassport
	@RequestMapping({ "/user/exchange" })
	public ModelAndView exchange(@RequestBody final Map<String, Object> params, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		try {
			final Integer shopId = (Integer) params.get("shopId");
			final String name = (String) params.get("name");
			final String address = (String) params.get("address");
			final String mobile = (String) params.get("mobile");
			this.userService.exchange(uid, shopId, name, address, mobile);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
		return ResponseUtils.jsonView(200, "兑换成功,请等待管理员处理发货!");
	}

	@AuthPassport
	@RequestMapping({ "/user/transferLogs" })
	public ModelAndView transferLogs(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final String hql = "from TransferLog where fromUid =:uid or toUid=:uid order by id desc ";
		final List<TransferLog> list = this.userService.findByHql(hql, ImmutableMap.of("uid", uid), pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/proxyUsers" })
	public ModelAndView proxyUsers(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final String hql = "select id,userId ,nickName , registDate,money from PubUser where parent=:uid order by id desc ";
		final List<PubUser> list = this.userService.findByHql(hql, ImmutableMap.of("uid", uid), pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/proxyApply" })
	public ModelAndView proxyApply(final HttpServletRequest request) {
		try {
			final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
			final Map<String, Object> conf = this.systemService.getProxyConfig();
			this.userService.proxyApply(uid, conf);
			return ResponseUtils.jsonView(200, "申请成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, e.getMessage());
		}
	}

	@AuthPassport
	@RequestMapping({ "/user/proxyConfig" })
	public ModelAndView proxyConfig(final HttpServletRequest request) {
		try {
			final Map res = (Map) new HashedMap();
			res.put("code", 200);
			res.put("body", this.systemService.getProxyConfig());
			return ResponseUtils.jsonView(res);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseUtils.jsonView(500, "配置获取失败!");
		}
	}

	@AuthPassport
	@RequestMapping({ "/user/proxyLogs" })
	public ModelAndView proxyLogs(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final String hql = "from ProxyVote where parentId=:uid order by id desc ";
		final List<TransferLog> list = this.userService.findByHql(hql, ImmutableMap.of("uid", uid), pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/getUid" }, method = { RequestMethod.GET })
	public ModelAndView getUid(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		return ResponseUtils.jsonView(uid);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/vc" }, method = { RequestMethod.GET })
	public ModelAndView vc(@RequestParam final int uid, @RequestParam final String roomId, @RequestParam double value,
			final HttpServletRequest request) {
		ValueControl.setValue(roomId, uid, new BigDecimal(value));
		return ResponseUtils.jsonView(ValueControl.getStore());
	}

	@AuthPassport
	@RequestMapping(value = { "/user/clean" }, method = { RequestMethod.GET })
	public ModelAndView clean(@RequestParam final int uid, @RequestParam final String roomId,
			final HttpServletRequest request) {
		ValueControl.clean(roomId, uid);
		return ResponseUtils.jsonView(ValueControl.getStore());
	}

	@AuthPassport
	@RequestMapping({ "/user/myBonus" })
	public ModelAndView myBonus(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		// final List<GcLotteryDetail> list = this.userService.findByHql(" from
		// GcLotteryDetail where uid=:uid and roomId is not null and roomId<>''
		// order by id desc ", ImmutableMap.of("uid", uid), pageSize, pageNo);
		final List<GcLotteryDetail> list = this.userService.findByComSql("", uid, pageSize, pageNo);
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
		for (final GcLotteryDetail gcLottery : list) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("createTime", gcLottery.getCreateDate());
			map.put("money", gcLottery.getCoin());
			map.put("desc1", gcLottery.getDesc1());
			map.put("inoutNum", gcLottery.getInoutNum());
			map.put("uid", gcLottery.getUid());
			map.put("masterId", gcLottery.getMasterId());
			map.put("deposit", gcLottery.getDeposit());
			String roomId = gcLottery.getRoomId().substring(0, 1);
			if (uid != gcLottery.getUid().intValue()) {
				gcLottery.setStatus(2);
				// final String hql = "select id,userId ,nickName ,
				// registDate,money from PubUser where parent=:uid order by id
				// desc ";
				final String sql = "select 1 as id, count(1) as inCount,sum(deposit) as inBack from gc_lottery_detail a where lotteryid ='"
						+ gcLottery.getLotteryid() + "' and uid!=" + uid + " and desc1='中雷'";
				if (roomId.equals("S")) {
					// 查询中雷人数，及总金额
					final List<InmineInfo> infos = this.userService.findInMines(sql, InmineInfo.class);
					if (infos != null && infos.size() > 0) {
						map.put("inCount", infos.get(0).getInCount());
						map.put("inBack", infos.get(0).getInBack());
					}
				}
			} else {
				gcLottery.setStatus(0);
			}
			final Room room = this.roomStore.get(gcLottery.getRoomId());
			if (room == null) {
				continue;
			}
			map.put("roomName", (room == null) ? "不明" : room.getName());
			gcLottery.setRoomId(roomId);
			map.put("roomId", gcLottery.getRoomId());
			map.put("status", gcLottery.getStatus());
			records.add(map);
		}
		return ResponseUtils.jsonView(records);
	}

	@AuthPassport
	@RequestMapping({ "/user/myBonus03" })
	public ModelAndView myBonus03(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<PcGameLog> list = this.userService.find(PcGameLog.class, ImmutableMap.of("uid", uid,"human",(short)0), pageSize,
				pageNo, "betTime desc");
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
		final Map<String, PcRateConfig> rates = this.pcService.getPcRateConfig();
		for (final PcGameLog gcLottery : list) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("createTime", gcLottery.getBetTime());
			map.put("money", gcLottery.getFreeze());
			map.put("luckyNumber", gcLottery.getLuckyNumber());
			if ("num".equals(gcLottery.getBetType())) {
				map.put("desc1", "数字" + gcLottery.getBet());
			} else {
				map.put("desc1", rates.get(gcLottery.getBet()).getAlias());
			}
			map.put("inoutNum", gcLottery.getUserInout());
			map.put("num", gcLottery.getNum());
			records.add(map);
		}
		return ResponseUtils.jsonView(records);
	}

	@AuthPassport
	@RequestMapping({ "/user/exchangeLogs" })
	public ModelAndView exchangeLogs(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<PubExchangeLog> list = this.userService.find(PubExchangeLog.class,
				ImmutableMap.of("uid", (Object) uid), pageSize, pageNo, "id desc");
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/roomHistory" })
	public ModelAndView roomHistory(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		return ResponseUtils
				.jsonView(this.userService.findByProperty(GcLottery.class, "sender", uid, "createTime desc"));
	}

	@RequestMapping(value = { "/user/logout" }, method = { RequestMethod.POST })
	public ModelAndView logout(HttpServletRequest request, final HttpSession session) {
		if (session != null) {
			final Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
			if (userId != null) {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("accessToken", null);
				map.put("tokenExpireTime", null);
				this.userService.updateUser(userId, map);
			}
			session.invalidate();
		}
		return ResponseUtils.jsonView(200, "成功退出.");
	}

	@AuthPassport
	@RequestMapping(value = { "/user/rechargeRecords" }, method = { RequestMethod.GET })
	public ModelAndView getRechargeRecords(@RequestParam int pageSize, @RequestParam int pageNo,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<PubRecharge> list = this.userService.findByHql(
				"from PubRecharge where uid=:uid and status =2 order by finishtime desc", ImmutableMap.of("uid", uid),
				pageSize, pageNo);
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/withdrawRecords" })
	public ModelAndView getWithdrawRecords(@RequestParam int pageSize, @RequestParam int pageNo,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<PubWithdraw> list = this.userService.findByHql(
				"from PubWithdraw where uid=:uid  order by tradetime desc", ImmutableMap.of("uid", uid), pageSize,
				pageNo);
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping({ "/user/bankRecords" })
	public ModelAndView getBankRecords(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		final List<PubBank> list = this.userService.findByProperty(PubBank.class, "userId", uid, "createTime desc");
		return ResponseUtils.jsonView(list);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/withdraw" }, method = { RequestMethod.POST })
	public ModelAndView withdraw(@RequestBody final Map<String, Object> data, final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		try {
			this.userService.withdraw(data, uid);
		} catch (Exception e) {
			return ResponseUtils.jsonView(500, e.getMessage());
		}
		return ResponseUtils.jsonView(200, "提现成功.");
	}

	@AuthPassport
	@RequestMapping(value = { "/user/roomApply" }, method = { RequestMethod.POST })
	public ModelAndView roomApply(@RequestBody final Map<String, String> params, final HttpServletRequest request) {
		final String name = params.get("name");
		final String mobile = params.get("mobile");
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		this.userService.roomApply(name, mobile, uid);
		return ResponseUtils.jsonView(200, "申请已经提交,审核后我们将和你取得联系.");
	}

	@AuthPassport
	@RequestMapping(value = { "/user/roomApplyShort" }, method = { RequestMethod.POST })
	public ModelAndView roomApplyShort(@RequestBody final Map<String, String> params,
			final HttpServletRequest request) {
		final String type = params.get("type");
		final String money = params.get("money");
		final String number = params.get("number");

		String errorMsg = "";
		if (Strings.isNullOrEmpty(type) || Strings.isNullOrEmpty(money) || Strings.isNullOrEmpty(number)) {
			errorMsg = "请填写完信息后提交表单";
		}

		try {
			int m = Double.valueOf(money).intValue();
			int n = Double.valueOf(number).intValue();
			final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
			GcRoom room = this.roomService.roomApply(type, m, n, uid);
			String template = "房间申请成功,房间名称是" + getVipRoomName(room.getType()) + " 密码是" + room.getPsw();
			return ResponseUtils.jsonView(200, template);
		} catch (Exception e) {
			errorMsg = "金额和红包数量必须是整数";
		}

		return ResponseUtils.jsonView(500, errorMsg);
	}

	private String getVipRoomName(String roomType) {
		if (roomType.startsWith("G01")) {
			return "红包接龙VIP";
		} else if (roomType.startsWith("G02")) {
			return "红包牛牛VIP";
		} else if (roomType.startsWith("G04")) {
			return "红包扫雷VIP";
		} else {
			return "PC蛋蛋VIP";
		}
	}

	@AuthPassport
	@RequestMapping(value = { "/user/roomCount" }, method = { RequestMethod.GET })
	public ModelAndView getUserRoomCount(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		final int count = this.roomService.getUserRoomCount(uid);
		return ResponseUtils.jsonView(count);
	}

	@AuthPassport
	@RequestMapping(value = { "/user/rooms" }, method = { RequestMethod.GET })
	public ModelAndView getUserRooms(@RequestParam final int pageSize, @RequestParam final int pageNo,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		final List<GcRoom> list = this.roomService.getUserRooms(uid, pageSize, pageNo);
		if (list != null && !list.isEmpty()) {
			final List<Room> rooms = new ArrayList<Room>(list.size());
			for (final GcRoom gcRoom : list) {
				rooms.add(this.roomStore.get(gcRoom.getId()));
			}
			return ResponseUtils.jsonView(rooms);
		}
		return ResponseUtils.jsonView(list);
	}

	@RequestMapping(value = { "/user/lotteryHistory" }, method = { RequestMethod.GET })
	public ModelAndView getLotteryHistory(final HttpServletRequest request) {
		String day = request.getParameter("day"); // "20170124"
		String token = request.getParameter("token");// bf9d0533dbc8fba9d3c0494b8044de8b

		if (Strings.isNullOrEmpty(token) || !token.equals(MD5StringUtil.MD5EncodeUTF8(day + "game"))) {
			return ResponseUtils.jsonView(500, "参数校验失败", "");
		} else {
			List<GcLotteryDetail> detailList = this.userService.listLotteryDetailHistory();
			List<Map> mapList = Lists.newArrayList();
			for (GcLotteryDetail detail : detailList) {
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(detail.getCreateDate());
				mapList.add(ImmutableMap.of("id", detail.getId(), "createDate", time, "inout", detail.getInoutNum(),
						"uid", detail.getGameUid()));
			}
			return ResponseUtils.jsonView(mapList);
		}
	}

	@RequestMapping(value = { "/user/updateLotteryHistory" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateLotteryHistory(final HttpServletRequest request) {
		String ids = request.getParameter("ids"); // 1495878,1495884
		String token = request.getParameter("token");// 489c56ac9fe94e36175c3f0c7db140be
		if (Strings.isNullOrEmpty(token) || !token.equals(MD5StringUtil.MD5EncodeUTF8(ids + "game"))) {
			return ResponseUtils.jsonView(500, "参数校验失败", "");
		} else {
			Iterable<String> idList = Splitter.on(",").split(ids);
			List<Integer> detailIdList = Lists.newArrayList();
			for (String id : idList) {
				detailIdList.add(Integer.valueOf(id));
			}

			this.userService.updateLotteryDetailStatus(detailIdList);
			return ResponseUtils.jsonView(ImmutableMap.of("code", 1));
		}
	}

	@RequestMapping(value = { "/user/currentUser" }, method = { RequestMethod.GET })
	public ModelAndView getCurUser(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (uid != null && uid > 0) {
			PubUser pUser = this.userService.getById(uid);
			return ResponseUtils
					.jsonView(ImmutableMap.of("code", 1, "uid", uid, "accessToken", pUser.getAccessToken()));
		} else {
			return ResponseUtils.jsonView(ImmutableMap.of("code", -1, "uid", 0));
		}
	}

	@RequestMapping(value = { "/user/gamecenterfill" }, method = { RequestMethod.GET })
	public RedirectView redirectFill(final HttpServletRequest request) {
		Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			String uid = request.getParameter("uid");
			String token = request.getParameter("token");
			PubUser user = this.userService.getById(Integer.valueOf(uid));
			if (user.getAccessToken().equals(token)) {
				userId = Integer.valueOf(uid);
			}
		}

		PubUser pUser = this.userService.getById(userId);

		if (pUser != null) {
			logger.warn("in redirect fill, uid is:" + userId);
			logger.warn(
					"redirect to url:" + "http://game.juzizou.com/plaza/shop/" + pUser.getWxOpenId() + "/redpacket");
			PcConfig config = userService.getPcConfig("plaza_host");
			if (config != null) {
				// return new RedirectView("http://game.juzizou.com/plaza/shop/"
				// + pUser.getWxOpenId() + "/redpacket");
				String roomName = request.getParameter("room");
				if (Strings.isNullOrEmpty(roomName)) {
					return new RedirectView(config.getVal() + "/plaza/shop/" + pUser.getWxOpenId() + "/redpacket");
				} else {
					return new RedirectView(
							config.getVal() + "/plaza/shop/" + pUser.getWxOpenId() + "/redpacket?config=" + roomName);
				}
			} else {
				return new RedirectView("");
			}
		} else {
			return new RedirectView("");
		}
	}

	@RequestMapping(value = { "/user/moneyexchange" }, method = { RequestMethod.GET })
	public RedirectView redirectExchange(final HttpServletRequest request) {
		Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			String uid = request.getParameter("uid");
			String token = request.getParameter("token");
			PubUser user = this.userService.getById(Integer.valueOf(uid));
			if (user.getAccessToken().equals(token)) {
				userId = Integer.valueOf(uid);
			}
		}
		PubUser pUser = this.userService.getById(userId);
		logger.warn("in redirect fill, uid is:" + userId);
		logger.warn("redirect to url:" + "http://game.juzizou.com/plaza/wx/duobaoredirect/" + pUser.getWxOpenId()
				+ "/transferfromgame");
		PcConfig config = userService.getPcConfig("plaza_host");
		if (config != null) {
			return new RedirectView(config.getVal() + "/plaza/wx/duobaoredirect/" + pUser.getWxOpenId()
					+ "/transferfromgame?source=redpacket");
		} else {
			return new RedirectView("");
		}
	}

	@RequestMapping(value = { "/user/userAssetRecord" }, method = { RequestMethod.GET })
	public RedirectView getUserAssetRecord(final HttpServletRequest request) {
		Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			String uid = request.getParameter("uid");
			String token = request.getParameter("token");
			PubUser user = this.userService.getById(Integer.valueOf(uid));
			if (user.getAccessToken().equals(token)) {
				userId = Integer.valueOf(uid);
			}
		}
		PubUser pUser = this.userService.getById(userId);
		logger.warn("in redirect fill, uid is:" + userId);
		logger.warn("redirect to url:" + "http://game.juzizou.com/plaza/user/userAssetRecord");
		PcConfig config = userService.getPcConfig("plaza_host");
		if (config != null) {
			return new RedirectView(config.getVal() + "/plaza/user/userAssetRecord");
		} else {
			return new RedirectView("");
		}
	}

	@RequestMapping(value = { "/i" }, method = { RequestMethod.GET })
	public void getUserRooms(@RequestParam final Integer u, final HttpServletRequest request,
			final HttpServletResponse response) {
		try {
			WebUtils.setSessionAttribute(request, "$invitor", (Object) u);
			response.sendRedirect("/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = { "/user/getUsers/{type}" }, method = { RequestMethod.GET })
	@ResponseBody
	public Object getUsers(final HttpServletRequest request, @PathVariable("type") int type) {
		final List<Room> rooms = this.roomStore.getByCatalog("");
		List<User> listRobot = new ArrayList<>();
		List<User> listGuest = new ArrayList<>();
		if (rooms != null && !rooms.isEmpty()) {
			for (final Room room : rooms) {
				Iterator s = room.getUsers().keySet().iterator();
				while (s.hasNext()) {
					User u = room.getUsers().get(s.next());
					u = userStore.get(u.getId());
					if (u.getUserType().equals("9")) {
						listRobot.add(u);
					} else {
						u.setRoomName(room.getName());
						listGuest.add(u);
					}
					System.out.println(room.getId() + u.getNickName() + ":" + u.getMoney());
				}
			}
		}
		System.out.println("robot size:" + listRobot.size());
		System.out.println("guest size:" + listGuest.size());
		if(type==0){
			return listRobot;
		}else{
			return listGuest;
		}
	}

	@RequestMapping(value = { "/user/getRoomNmById/{uid}" },produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getRoomNameByUid(final HttpServletRequest request, @PathVariable("uid") int uid) {
		final List<Room> rooms = this.roomStore.getByCatalog("");
		if (rooms != null && !rooms.isEmpty()) {
			for (final Room room : rooms) {
				User u = room.getUsers().get(uid);
				if (null != u) {
					return room.getName();
				}
			}
		}
		return "-1";
	}
	
	@RequestMapping(value = { "/user/contactUs" }, method = { RequestMethod.GET })
	public ModelAndView contactUs(final HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("contactUs");
		Integer userId = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		if (userId == null) {
			String uid = request.getParameter("uid");
			String token = request.getParameter("token");
			PubUser user = this.userService.getById(Integer.valueOf(uid));
			if (user.getAccessToken().equals(token)) {
				userId = Integer.valueOf(uid);
			}
		}
		PubUser pUser = this.userService.getById(userId);
		GcChnKfInfo config = userService.getChnKF(pUser.getChnno().toString());
		if (config != null) {
		} else {
			config = userService.getChnKF("3000");
		}
		mv.addObject("kfInfo", config);
		return mv;
	}
	
	public static void main(String[] args) {
		String chnno = "23";
		String gameUserId = "10000001";
		System.out.println(MD5StringUtil.MD5EncodeUTF8(chnno + gameUserId + "game"));
	}
	@AuthPassport
	@RequestMapping(value = { "/user/exchangeCard" }, method = { RequestMethod.POST })
	public ModelAndView exchangeCard(@RequestBody final Map params,
			final HttpServletRequest request) {
		final String number = params.get("cardnum").toString();
		
		String errorMsg = "";
		if (Strings.isNullOrEmpty(number)) {
			errorMsg = "请填写完信息后提交表单";
			return ResponseUtils.jsonView(500, errorMsg);
		}
		int n = Double.valueOf(number).intValue();
		if(n<=0){
			errorMsg = "房卡数必须是整数";
			return ResponseUtils.jsonView(500, errorMsg);
		}
		try {
			final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
			PubUser user = this.userService.getById(uid);
			GoldApp goldApp = this.userService.getServerPrice(user.getChnno());
			if(null == goldApp){
				errorMsg = "暂时无法提供兑换服务";
				return ResponseUtils.jsonView(500, errorMsg);
			}
			this.userService.updateUserMoney(user,goldApp,n);
			String template = "已为你兑换 " + n + " 张房卡成功";
			return ResponseUtils.jsonView(200, template);
		} catch (Exception e) {
			e.printStackTrace();
			errorMsg = e.getMessage();
		}
		
		return ResponseUtils.jsonView(500, errorMsg);
	}
	@AuthPassport
	@RequestMapping(value = { "/user/getServerPrice" }, method = { RequestMethod.GET })
	public ModelAndView getServerPrice(final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		PubUser user = this.userService.getById(uid);
		GoldApp goldApp = this.userService.getServerPrice(user.getChnno());
		if(null==goldApp){
			return ResponseUtils.jsonView(0);
		}
		;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("money", this.userService.getBalance(uid));
		map.put("price", goldApp.getCard_price());
		map.put("appname", goldApp.getAppname());
		return ResponseUtils.jsonView(map);
	}
	
	@AuthPassport
	@RequestMapping({ "/user/myFillExchangeDetails" })
	public ModelAndView myFillExchangeDetails(@RequestParam int pageNo, @RequestParam int pageSize,
			final HttpServletRequest request) {
		final Integer uid = (Integer) WebUtils.getSessionAttribute(request, "$uid");
		PubUser user = this.userService.getById(uid);
		if (pageNo <= 0) {
			pageNo = 1;
		}
		if (pageSize > 20) {
			pageSize = 20;
		}
		final List<GoldFundHistory> list = this.userService.find(GoldFundHistory.class, ImmutableMap.of("uid", user.getGameUserId(),"serverid",user.getChnno()), pageSize,
				pageNo, "time desc");
		if (list == null || list.isEmpty()) {
			return ResponseUtils.jsonView(null);
		}
		final List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(list.size());
		final Map<String, PcRateConfig> rates = this.pcService.getPcRateConfig();
		for (final GoldFundHistory fundHis : list) {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", fundHis.getTime());
			map.put("money", fundHis.getAmount());
			map.put("num", fundHis.getCard_num());
			map.put("otype", fundHis.getOtype());
			if (fundHis.getOtype()==1) {
				map.put("type", "充值");
			} else {
				map.put("type", "兑卡");
			}
			records.add(map);
		}
		return ResponseUtils.jsonView(records);
	}

}

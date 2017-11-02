package org.takeback.mvc.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import org.takeback.util.valid.ValidateUtil;
import org.takeback.verification.image.VerifyCodeUtils;
import org.takeback.verification.message.MessageProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.takeback.mvc.ResponseUtils.jsonView;

@Controller
@RequestMapping("code")
public class CodeController {

	private static final Logger log = LoggerFactory.getLogger(CodeController.class);

	public static final String SMS_CODE = "smscode";
	public static final String VERIFY_CODE = "verifycode";

	private LoadingCache<String, Integer> cache;
	private static final int maxCodeRequest = 10;
	public CodeController(){
		cache = CacheBuilder.newBuilder().maximumSize(500).expireAfterWrite(12, TimeUnit.HOURS).build(new CacheLoader<String, Integer>(){
			@Override
			public Integer load(String k) throws Exception {
				return 0;
			}
		});
	}

	@Autowired
	private MessageProcessor messageProcessor;

	@RequestMapping(value = "phone", method = RequestMethod.POST)
	public ModelAndView shortMessage(HttpServletRequest request, String phonenumb, String verifycode, @RequestParam(required = false)String type) {
		if(!verifycode.equalsIgnoreCase((String) WebUtils.getSessionAttribute(request, VERIFY_CODE))){
			return jsonView(909, "图片验证码不正确");
		}
		WebUtils.setSessionAttribute(request, VERIFY_CODE, null);	//图片验证码立马失效
		if(!ValidateUtil.instance().validatePhone(phonenumb)){
			return jsonView(502, "手机号码不正确");
		}
		int c = cache.getUnchecked(phonenumb);
		if(c >= maxCodeRequest){
			return jsonView(501, "对不起，为了安全起见，您的手机号码12小时内只能获得10次短信验证码。");
		}else{
			c++;
			cache.put(phonenumb, c);
		}
		String code = null;
		if(StringUtils.isEmpty(type)){
			code = messageProcessor.sendCode(phonenumb, "1");
		}else{
			code = messageProcessor.sendCode(phonenumb, type);
		}
		// {~~如果是测试环境统一发8888
		String host = request.getHeader("host");
		if(!StringUtils.isEmpty(host)){
			host = host.toLowerCase();
			if(host.startsWith("127.0.0.1") || host.startsWith("localhost") || host.startsWith("wengshankj.oicp.net") || host.startsWith("192.168.3.200")){
				code = "8888";
			}
		}
		//	~~}
//		System.out.println("send code ["+code+"] to "+phonenumb + " at " + new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
		log.info("send code [{}] to {} at {}", code, phonenumb, new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
		WebUtils.setSessionAttribute(request, SMS_CODE, code);
		return jsonView(200, "手机验证码已发送");
	}

	@RequestMapping(value = "image", method = RequestMethod.GET)
	public void imageVerifyCode(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(required = false, defaultValue = "150") int w,
			@RequestParam(required = false, defaultValue = "60") int h) throws IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		// 生成随机字串
		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
		// 存入会话session
		WebUtils.setSessionAttribute(request, VERIFY_CODE, verifyCode.toLowerCase());
		// 生成图片
		// int w = 200, h = 80;
		VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
	}

	@RequestMapping(value = "ckSmsCode")
	public ModelAndView checkSMSCode(HttpServletRequest request, String smsCode){
		if(smsCode.equals(WebUtils.getSessionAttribute(request, SMS_CODE))){
			return jsonView(200, "手机验证码正确");
		}else {
			return jsonView(500, "手机验证码不正确");
		}
	}

	@RequestMapping(value = "ckImageCode")
	public ModelAndView checkIMAGECode(HttpServletRequest request, String imageCode){
		if (imageCode == null || "".equals(imageCode)) {
			return jsonView(500, "图片验证码不正确");
		}
		if(imageCode.equalsIgnoreCase(String.valueOf(WebUtils.getSessionAttribute(request, VERIFY_CODE)))){
			return jsonView(200, "图片验证码正确");
		} else {
			return jsonView(500, "图片验证码不正确");
		}
	}

	/**
	 * 从session中取电话号码
	 * @param request
	 * @param verifycode
	 * @return
	 */
//	@AuthPassport
//	@RequestMapping(value = "phoneWithoutNum", method = RequestMethod.POST)
//	public ModelAndView shortMessageFromSession(HttpServletRequest request, String verifycode) {
//		NwAccount user = (NwAccount) WebUtils.getSessionAttribute(request, UserService.USER);
//		return shortMessage(request, user.getPhonenumb(), verifycode, "3");
//	}
}

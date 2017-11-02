package com.lxtx.controller.broker;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CloudBroker;
import com.lxtx.model.CloudRakeBack;
import com.lxtx.model.CloudUser;
import com.lxtx.model.vo.BrokerUnderPerVo;
import com.lxtx.service.broker.BrokerService;
import com.lxtx.service.user.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.SmsSendUtil;
import com.lxtx.util.StringUtil;

@Controller
@RequestMapping("/broker")
public class BrokerController {
	private static final Logger logger = LoggerFactory.getLogger(BrokerController.class);
	@Autowired
	private BrokerService brokerService;
	@Autowired
	private UserService userService;

	/**
	 * 进入经纪人管理台页面
	 * 
	 * @return
	 */
	@RequestMapping("/index")
	private ModelAndView brokerIndex(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		// UserInfo userInfo =
		// (UserInfo)request.getSession().getAttribute(WXPayConstants.WX_USER_INFO);
		CloudUser userInfo = (CloudUser) request.getSession().getAttribute(Constant.SESSION_USER);
		// if(userInfo.getOpenid()==null){
		// //调用微信接口并返回userInfo
		// }
		CloudUser user = userService.selectByWxid(userInfo.getWxid());
		if (null == user) {
			mv.setViewName("redirect:/broker/applyPage");
		} else {
			// 进入经纪人首页
			CloudBroker broker = brokerService.selectBrokerInfoByUserId(user.getId());
			broker.setMobile(user.getMobile());
			mv.addObject("broker", broker);
			mv.setViewName("broker/broker_index");
		}
		return mv;
	}

	/**
	 * 经纪人申请页面
	 * 
	 * @return
	 */
	@RequestMapping("/applyPage")
	private ModelAndView brokerApply(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("broker/apply_page");
		return mv;
	}

	/**
	 * 
	 * Description:获取验证码
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午9:13:01
	 */
	@RequestMapping("/getTelValidCode")
	@ResponseBody
	public Object getTelValidCode(HttpServletRequest request, String mobile) {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		// 发送4位随机验证码
		if(null==user){
			map.put("code", 1);
			map.put("errorMsg", "连接服务器超时,请返回重试");
			return map;
		}
		// 发送4位随机验证码
		String validCode = StringUtil.generateRandomNumber(4);
		String resultStr = SmsSendUtil.sendMsg(mobile, validCode);
		if(resultStr.equals("1")){
			logger.info("{} 验证码：【{}】 time:{}", mobile, validCode, StringUtil.formatDate(new Date()));
			user.setValidCode(validCode);
			session.setAttribute(Constant.SESSION_USER, user);
			map.put("code", ErrorCode.SUCCESS);
			map.put("data", validCode);
		}else{
			map.put("code", 1);
			map.put("errorMsg", "短信发送失败,请联系客服");
		}
		
		
		return map;
	}

	/**
	 * 提交申请
	 * 
	 * @param request
	 * @param applyBroker
	 * @param validCode
	 * @return
	 */
	@RequestMapping("/submitApply")
	@ResponseBody
	public Object submitApply(HttpServletRequest request, CloudBroker applyBroker, String validCode) {
		AjaxJson json = new AjaxJson();

		HttpSession session = request.getSession();
		CloudUser user = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		// 验证手机号
		if (validCode.equals(user.getValidCode())) {
			// 验证通过
			applyBroker.setUid(user.getId());
			applyBroker.setCrtTm(new Date());
			// 生成二位码
			applyBroker.setTdCode("");
			try {
				brokerService.saveBroker(applyBroker);
				json.setCode(ErrorCode.SUCCESS);
				json.setData("申请成功");
				session.setAttribute(Constant.SESSION_BROKER, applyBroker);
			} catch (Exception e) {
				e.printStackTrace();
				json.setCode(ErrorCode.SUCCESS);
				json.setData("申请异常，请稍后再试");
			}
		} else {
			// 验证失败
			json.setCode(ErrorCode.ERROR);
			json.setData("手机号码或验证码错误!");
			return json;
		}

		json.setCode(ErrorCode.SUCCESS);
		return json;
	}

	/**
	 * 进入直属客户页面
	 * 
	 * @return
	 */
	public ModelAndView enterUnderPerPage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("broker/upder_per_page");
		HttpSession session = request.getSession();
		CloudBroker broker = (CloudBroker) session.getAttribute(Constant.SESSION_BROKER);
		List<BrokerUnderPerVo> underPers = userService.selectUnderPerByMbl(broker.getUid(), "");
		mv.addObject("underPers", underPers);
		return mv;
	}

	/**
	 * 直属客户条件查询
	 * 
	 * @param mobile
	 *            注意： 有可能需要添加当前日期比较
	 * @return
	 */
	@RequestMapping("/queryUnderPers")
	@ResponseBody
	public Object queryUnderPers(HttpServletRequest request, String mobile) {
		AjaxJson json = new AjaxJson();
		HttpSession session = request.getSession();
		CloudBroker broker = (CloudBroker) session.getAttribute(Constant.SESSION_BROKER);
		List<BrokerUnderPerVo> underPers = userService.selectUnderPerByMbl(broker.getUid(), mobile);
		json.setCode(ErrorCode.SUCCESS);
		json.setData(underPers);
		return json;
	}

	/**
	 * 进入直属客户页面
	 * 
	 * @return
	 */
	@RequestMapping("/enterRateBackPage")
	public ModelAndView enterRateBackPage(HttpServletRequest request, int brokerId, String start, String end,Integer id) {
		ModelAndView mv = new ModelAndView();
		HttpSession session = request.getSession();
		CloudBroker broker = (CloudBroker) session.getAttribute(Constant.SESSION_BROKER);
		mv.setViewName("broker/rake_back_page");
		List<CloudRakeBack> rakeBacks = brokerService.selectRateBackByDate(brokerId, start, end,id);
		System.out.println(rakeBacks.size());
		mv.addObject("rakeBacks", rakeBacks);
		return mv;
	}

}

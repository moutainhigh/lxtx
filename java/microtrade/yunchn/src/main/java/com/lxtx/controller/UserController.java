package com.lxtx.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.model.CUser;
import com.lxtx.model.CUserChnRel;
import com.lxtx.model.CloudChnDayProfit;
import com.lxtx.model.CloudChnUserDayProfit;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudHumanControlConfig;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudPerHalfHourProfit;
import com.lxtx.model.CloudUser;
import com.lxtx.model.vo.CloudChnSumProfit;
import com.lxtx.model.vo.CloudChnSumProfitVo;
import com.lxtx.service.UserService;
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.StringUtil;
import com.lxtx.util.pagination.common.DataSection;
import com.lxtx.util.pagination.common.PageParameter;

/**
 * 用户管理相关
 * 
 * 
 */
@Controller
@RequestMapping(value = "/chn")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	/*
	 * 进入渠道统计页面
	 */
	@RequestMapping(value = "/enterChnManage")
	public ModelAndView enterChnManage() {
		ModelAndView mv = new ModelAndView();
		String begin = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		mv.addObject("begin", begin);
		mv.addObject("end", begin);
		mv.setViewName("chn/chnManage");
		return mv;
	}

	/*
	 * 分页渠道统计列表
	 */
	@RequestMapping(value = "/queryForPageChnList")
	@ResponseBody
	public Object queryForPageChnList(HttpServletRequest request, String begin, String end) {
		String end1 = StringUtil.formatDateCommon(StringUtil.parseStringToDate10(end), "yyyy-MM-dd", 1);
		AjaxJson json = new AjaxJson();
		List<CloudPerHalfHourProfit> list = userService.queryForPageChnSum(request, begin, end, end1);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		CloudChnSumProfit sumChnProfit = new CloudChnSumProfit();
		if (page.getTotalpage() > 1) {
			sumChnProfit = userService.querySumChns(request, begin, end1);
		} else {
			sumChnProfit = userService.querySumChns(list);
		}
		CloudChnSumProfitVo vo = new CloudChnSumProfitVo();
		vo.setDate("<span style='color:blue'>合计：</span>");
		vo.setAddUser("<span style='color:blue'>" + sumChnProfit.getAddUser() + "</span>");
		vo.setChnCommission("<span style='color:blue'>" + sumChnProfit.getChnCommission() + "</span>");
		vo.setOrderCount("<span style='color:blue'>" + sumChnProfit.getOrderCount() + "</span>");
		vo.setProfit("<span style='color:blue'>" + sumChnProfit.getProfit() + "</span>");
		vo.setLoss("<span style='color:blue'>" + sumChnProfit.getLoss() + "</span>");
		vo.setContractMoney("<span style='color:blue'>" + sumChnProfit.getContractMoney() + "</span>");
		vo.setfProfit("<span style='color:blue'>" + sumChnProfit.getfProfit() + "</span>");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		json.setObj(vo);
		return json;
	}

	/*
	 * 进入渠道历史数据页面
	 */
	@RequestMapping(value = "/enterChnHistory")
	public ModelAndView enterChnHistory(HttpServletRequest request, String chnno) {
		ModelAndView mv = new ModelAndView();
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), chnno);
		if (rel == null) {
			mv.addObject("msg", "您没有访问该渠道的权限，请联系管理员！");
			mv.setViewName("noPower");
			return mv;
		}
		String end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		String begin = StringUtil.formatDateCommon(new Date(), "yyyy-MM-dd", -7);
		mv.addObject("begin", begin);
		mv.addObject("end", end);
		mv.addObject("chnno", chnno);
		mv.setViewName("chn/chnHistory");
		return mv;
	}

	/*
	 * 分页渠道历史列表
	 */
	@RequestMapping(value = "/queryForPageChnHistoryList")
	@ResponseBody
	public Object queryForPageChnHistoryList(HttpServletRequest request, String chnno, String begin, String end) {
		AjaxJson json = new AjaxJson();
		List<CloudChnDayProfit> list = userService.queryForPageChnHistoryList(request, chnno, begin, end);
		CloudChnSumProfit sumChnProfit = userService.querySumChnHistory(request, chnno, begin, end);
		CloudChnSumProfitVo vo = new CloudChnSumProfitVo();
		vo.setDate("<span style='color:red'>合计：</span>");
		vo.setAddUser("<span style='color:red'>" + sumChnProfit.getAddUser() + "</span>");
		vo.setChnCommission("<span style='color:red'>" + sumChnProfit.getChnCommission() + "</span>");
		vo.setOrderCount("<span style='color:red'>" + sumChnProfit.getOrderCount() + "</span>");
		vo.setProfit("<span style='color:red'>" + sumChnProfit.getProfit() + "</span>");
		vo.setLoss("<span style='color:red'>" + sumChnProfit.getLoss() + "</span>");
		vo.setfProfit("<span style='color:red'>" + sumChnProfit.getfProfit() + "</span>");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		json.setObj(vo);
		return json;
	}

	/*
	 * 进入渠道分时数据页面
	 */
	@RequestMapping(value = "/enterChnPerTime")
	public ModelAndView enterChnPerTime(HttpServletRequest request, String chnno, String date) {
		ModelAndView mv = new ModelAndView();
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), chnno);
		if (rel == null) {
			mv.addObject("msg", "您没有访问该渠道的权限，请联系管理员！");
			mv.setViewName("noPower");
			return mv;
		}
		if (StringUtils.isEmpty(date)) {
			date = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		}
		mv.addObject("date", date);
		mv.addObject("chnno", chnno);
		mv.setViewName("chn/chnPerTime");
		return mv;
	}

	/*
	 * 分页渠道分时列表
	 */
	@RequestMapping(value = "/queryForPageChnPerTimeList")
	@ResponseBody
	public Object queryForPageChnPerTimeList(HttpServletRequest request, String chnno, String date) {
		AjaxJson json = new AjaxJson();
		List<CloudPerHalfHourProfit> list = userService.queryForPageChnPerTimeList(request, chnno, date);
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 进入用户统计查询页面
	 */
	@RequestMapping(value = "/enterUserProfitManage")
	public ModelAndView enterUserProfitManage() {
		ModelAndView mv = new ModelAndView();
		String date = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		mv.addObject("begin", date);
		mv.addObject("end", date);
		mv.setViewName("chn/chnUserProfitManage");
		return mv;
	}

	/*
	 * 用户统计查询
	 */
	@RequestMapping(value = "/queryForPageChnUserProfit")
	@ResponseBody
	public Object queryForPageChnUserProfit(HttpServletRequest request, HttpServletResponse response, String wxnm,
			String chnno, String begin, String end) {
		AjaxJson json = new AjaxJson();
		if (!StringUtils.isEmpty(chnno)) {
			CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
			CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), chnno);
			if (rel == null) {
				json.setCode(ErrorCode.SUCCESS);
				json.setData(null);
				PageParameter page = (PageParameter) DataSection.getInput("pages");
				page.setPagenum(page.getPagenum() + 1);
				json.setPage(page);
				return json;
			}
		}
		if (StringUtils.isEmpty(begin) && StringUtils.isNotEmpty(end)) {
			begin = end;
		}
		if (StringUtils.isEmpty(end) && StringUtils.isNotEmpty(begin)) {
			end = begin;
		}
		List<CloudChnUserDayProfit> list = userService.queryForPageChnUserProfit(request, wxnm, chnno, begin, end);
		if (list != null && list.size() > 0) {
			// CloudChnUserDayProfit sum =
			// userService.querySumUserProfit(request, chnno, begin, end);
		}
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 进入渠道入金页面
	 */
	@RequestMapping(value = "/enterChnFill")
	public ModelAndView enterChnFill(HttpServletRequest request, String chnno) {
		ModelAndView mv = new ModelAndView();
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), chnno);
		if (rel == null) {
			mv.addObject("msg", "您没有访问该渠道的权限，请联系管理员！");
			mv.setViewName("noPower");
			return mv;
		}
		String end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		String begin = StringUtil.formatDateCommon(new Date(), "yyyy-MM-dd", -7);
		mv.addObject("begin", begin);
		mv.addObject("end", end);
		mv.addObject("chnno", chnno);
		mv.setViewName("chn/chnFill");
		return mv;
	}

	/*
	 * 分页渠道入金列表
	 */
	@RequestMapping(value = "/queryForPageChnFillList")
	@ResponseBody
	public Object queryForPageChnFillList(HttpServletRequest request, String chnno, String begin, String end) {
		AjaxJson json = new AjaxJson();
		List<CloudFundHistory> list = userService.queryForPageChnFillList(request, chnno, begin, end);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		int fillUserCount = userService.queryFillUserCount(request, chnno, begin, end);
		BigDecimal fillAmount = userService.queryFillAmount(request, chnno, begin, end);
		CloudFundHistory fillSum = new CloudFundHistory();
		fillSum.setWxnm("<span style='color:blue'>入金总金额：</span><span style='color:red'>" + fillAmount + "</span>");
		fillSum.setChnno("<span style='color:blue'>入金用户数：</span><span style='color:red'>" + fillUserCount + "</span>");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		json.setObj(fillSum);
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 进入渠道订单页面
	 */
	@RequestMapping(value = "/enterChnOrder")
	public ModelAndView enterChnOrder(HttpServletRequest request, String chnno) {
		ModelAndView mv = new ModelAndView();
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), chnno);
		if (rel == null) {
			mv.addObject("msg", "您没有访问该渠道的权限，请联系管理员！");
			mv.setViewName("noPower");
			return mv;
		}
		String end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		String begin = end;
		mv.addObject("begin", begin);
		mv.addObject("end", end);
		mv.addObject("chnno", chnno);
		mv.setViewName("chn/chnOrder");
		return mv;
	}

	/*
	 * 分页渠道订单列表
	 */
	@RequestMapping(value = "/queryForPageChnOrderList")
	@ResponseBody
	public Object queryForPageChnOrderList(HttpServletRequest request, String chnno, String begin, String end) {
		AjaxJson json = new AjaxJson();
		List<CloudOrder> list = userService.queryForPageChnOrderList(request, chnno, begin, end);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 进入单用户统计页面
	 */
	@RequestMapping(value = "/enterChnAllUserSum")
	public ModelAndView enterChnAllUserSum(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		// String begin = StringUtil.formatDateCommon(new Date(), "yyyy-MM-dd",
		// -7);
		String begin = end;
		mv.addObject("begin", begin);
		mv.addObject("end", end);
		mv.setViewName("chn/chnUserSum");
		return mv;
	}

	/*
	 * 分页用户统计列表
	 */
	@RequestMapping(value = "/queryForPageChnAllUserSum")
	@ResponseBody
	public Object queryForPageChnAllUserSum(HttpServletRequest request, String chnno, String begin, String end) {
		AjaxJson json = new AjaxJson();
		List<CloudChnUserDayProfit> list = userService.queryForPageChnAllUserSum(request, chnno, begin, end);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 分页用户统计列表
	 */
//	@RequestMapping(value = "/downChnAllUserSum")
	public ModelAndView downChnAllUserSum(HttpServletRequest request, HttpServletResponse response, String chnno,
			String begin, String end) {
		SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
		response.setHeader("Pragma", "public");
		response.setHeader("Cache-Control", "max-age=0");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + myFmt.format(StringUtil.parseStringToDate10(end)) + ".xls");
		ModelAndView mv = new ModelAndView();
		end = StringUtil.formatDateCommon(StringUtil.parseStringToDate10(end), "yyyy-MM-dd", 1);
		List<CloudChnUserDayProfit> list = userService.downChnAllUserSum(request, chnno, begin, end);
		mv.addObject("list", list);
		mv.setViewName("chn/downChnAllUserSum");
		return mv;
	}

	/*
	 * 进入用户订单页面
	 */
	@RequestMapping(value = "/enterUserOrder")
	public ModelAndView enterUserOrder(HttpServletRequest request, Integer uid) {
		ModelAndView mv = new ModelAndView();
		CloudUser queryUser = userService.selectUserByUid(uid);
		if (queryUser == null) {
			mv.addObject("msg", "未查找到该用户！");
			mv.setViewName("noPower");
			return mv;
		}
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CUserChnRel rel = userService.queryRelByUidAndChn(user.getId(), queryUser.getChnno());
		if (rel == null) {
			mv.addObject("msg", "您没有访问该渠道的权限，请联系管理员！");
			mv.setViewName("noPower");
			return mv;
		}
		String end = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		String begin = end;
		mv.addObject("begin", begin);
		mv.addObject("end", end);
		mv.addObject("uid", uid);
		mv.setViewName("chn/userOrder");
		return mv;
	}

	/*
	 * 分页用户订单列表
	 */
	@RequestMapping(value = "/queryForPageUserOrderList")
	@ResponseBody
	public Object queryForPageUserOrderList(HttpServletRequest request, Integer uid) {
		AjaxJson json = new AjaxJson();
		List<CloudOrder> list = userService.queryForPageUserOrderList(request, uid);
		PageParameter page = (PageParameter) DataSection.getInput("pages");
		json.setCode(ErrorCode.SUCCESS);
		json.setData(list);
		page.setPagenum(page.getPagenum() + 1);
		json.setPage(page);
		return json;
	}

	/*
	 * 进入渠道统计页面
	 */
	@RequestMapping(value = "/enterProfitManage")
	public ModelAndView enterProfitManage(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		CUser user = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		if (user.getuCode().equals("ww_admin")) {
			mv.setViewName("chn/profitManage");
		} else {
			mv.setViewName("noPower");
		}
		return mv;
	}

	/*
	 * 分页用户订单列表
	 */
	@RequestMapping(value = "/addProfit")
	@ResponseBody
	public Object addProfit(HttpServletRequest request, CloudHumanControlConfig humanConfig) {
		AjaxJson json = new AjaxJson();
		if (humanConfig.getOrderMoney().intValue() != 10 && humanConfig.getOrderMoney().intValue() != 100
				&& humanConfig.getOrderMoney().intValue() != 500) {
			json.setCode(ErrorCode.ERROR,"金额输入不对！");
			return json;
		}
		if(humanConfig.getOrderNum().intValue()>6){
			json.setCode(ErrorCode.ERROR,"订单笔数不能大于6！");
			return json;
		}
		
		try {
			userService.saveHumanConfig(humanConfig);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			json.setCode(ErrorCode.OPER_DB_ERROR);
			return json;
		}
		json.setCode(ErrorCode.SUCCESS);
		return json;
	}
}

package com.lxtx.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CUserChnRelMapper;
import com.lxtx.dao.CUserMapper;
import com.lxtx.dao.CloudChnDayProfitMapper;
import com.lxtx.dao.CloudChnUserDayProfitMapper;
import com.lxtx.dao.CloudFundHistoryMapper;
import com.lxtx.dao.CloudHumanControlConfigMapper;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudPerHalfHourProfitMapper;
import com.lxtx.dao.CloudUserMapper;
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
import com.lxtx.util.AjaxJson;
import com.lxtx.util.Constant;
import com.lxtx.util.ErrorCode;
import com.lxtx.util.LogUtil;
import com.lxtx.util.tool.EncryptUtil;

@Service
public class UserService {

	@Autowired
	private CUserMapper cUserMapper;
	@Autowired
	private CloudOrderMapper cloudOrderMapper;

	@Autowired
	private CloudPerHalfHourProfitMapper cloudPerHalfHourProfitMapper;
	@Autowired
	private CloudChnDayProfitMapper cloudChnDayProfitMapper;

	@Autowired
	private CUserChnRelMapper chnRelMapper;
	@Autowired
	private CloudChnUserDayProfitMapper chnUserDayProfitMapper;

	@Autowired
	private CloudFundHistoryMapper cloudFundHistoryMapper;

	@Autowired
	private CloudUserMapper cloudUserMapper;
	
	@Autowired
	private CloudHumanControlConfigMapper humanConfigMapper;

	/**
	 * 认证用户名和密码
	 * 
	 * @param user
	 * @return
	 */
	public boolean verifyUser(CUser user) {
		CUser targetUser = cUserMapper.selectByCode(user.getuCode());
		try {
			if (targetUser != null && EncryptUtil.matches(user.getPwd(), targetUser.getPwd())) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public AjaxJson updatePwdByUCode(HttpServletRequest request, String uCode, String oldPwd, String password)
			throws Exception {
		AjaxJson j = new AjaxJson();
		CUser user = cUserMapper.selectByCode(uCode);
		if (EncryptUtil.matches(oldPwd, user.getPwd())) {
			user.setPwd(EncryptUtil.endcodePassword(password));
			cUserMapper.updatePwd(user);
			j.setCode(ErrorCode.SUCCESS);
			String method = Thread.currentThread().getStackTrace()[1].getMethodName();
			LogUtil.loginfo(request, method, user);
		} else {
			j.setCode(ErrorCode.PWD_ERROR);
			j.setData(ErrorCode.getErrorInfo(ErrorCode.PWD_ERROR));
		}
		return j;
	}

	public CUser selectByCode(String uCode) {
		return cUserMapper.selectByCode(uCode);
	}

	public List<CloudPerHalfHourProfit> queryForPageChnSum(HttpServletRequest request, String begin, String end,
			String end1) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		List<CloudPerHalfHourProfit> list = new ArrayList<>();
		// if(date.equals(StringUtil.formatDate(new Date(), "yyyy-MM-dd"))){
		// }else{
		// list = cloudPerHalfHourProfitMapper.queryForPageChnSum(cuser.getId(),
		// date);
		// }
		list = cloudOrderMapper.queryForPageChnSum(cuser.getId(), begin, end, end1);
		for (CloudPerHalfHourProfit cloudPerHalfHourProfit : list) {
			CloudPerHalfHourProfit c = new CloudPerHalfHourProfit();
			c = cloudOrderMapper.queryChnSumByChnno(begin, end, cloudPerHalfHourProfit.getChnno(), end1);
			cloudPerHalfHourProfit.setContractMoney(c.getContractMoney());
			cloudPerHalfHourProfit.setProfit(c.getProfit());
			cloudPerHalfHourProfit.setfProfit(c.getfProfit());
			cloudPerHalfHourProfit.setLoss(c.getLoss());
			cloudPerHalfHourProfit.setAddUser(c.getAddUser());
		}
		checkUType(cuser, list);
		return list;
	}

	public CloudChnSumProfit querySumChns(HttpServletRequest request, String begin, String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CloudChnSumProfit obj = cloudOrderMapper.querySumChns(cuser.getId(), begin, end);
		CloudChnSumProfit obj2 = cloudOrderMapper.querySumChns2(cuser.getId(), begin, end);
		obj.setProfit(obj2.getProfit());
		obj.setLoss(obj2.getLoss());
		obj.setContractMoney(obj2.getContractMoney());
		checkUTypeByDayForObject(cuser, obj);
		return obj;
	}
	
	public CloudChnSumProfit querySumChns(List<CloudPerHalfHourProfit> list) {
		CloudChnSumProfit sum = new CloudChnSumProfit();
		sum.setAddUser(0);
		sum.setChnCommission(BigDecimal.ZERO);
		sum.setOrderCount(0);
		sum.setProfit(BigDecimal.ZERO);
		sum.setLoss(BigDecimal.ZERO);
		sum.setContractMoney(BigDecimal.ZERO);
		sum.setfProfit(BigDecimal.ZERO);
		for (CloudPerHalfHourProfit per : list) {
			sum.setAddUser(sum.getAddUser().intValue()+per.getAddUser().intValue());
			sum.setChnCommission(sum.getChnCommission().add(per.getChnCommission()));
			sum.setOrderCount(sum.getOrderCount().intValue()+per.getOrderCount().intValue());
			sum.setProfit(sum.getProfit().add(per.getProfit()));
			sum.setLoss(sum.getLoss().add(per.getLoss()));
			sum.setContractMoney(sum.getContractMoney().add(per.getContractMoney()));
			sum.setfProfit(sum.getfProfit().add(per.getfProfit()));
		}
		return sum;
	}

	public CUserChnRel queryRelByUidAndChn(Integer id, String chnno) {
		return chnRelMapper.selectRelByUidAndChn(id, chnno);
	}

	public List<CloudChnDayProfit> queryForPageChnHistoryList(HttpServletRequest request, String chnno, String begin,
			String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		List<CloudChnDayProfit> list = cloudChnDayProfitMapper.queryForPageHisChns(cuser.getId(), chnno, begin, end);
		checkUTypeByDay(cuser, list);
		return list;
	}

	public CloudChnSumProfit querySumChnHistory(HttpServletRequest request, String chnno, String begin, String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		CloudChnSumProfit obj = cloudChnDayProfitMapper.querySumChnHistory(cuser.getId(), chnno, begin, end);
		checkUTypeByDayForObject(cuser, obj);
		return obj;
	}

	public List<CloudPerHalfHourProfit> queryForPageChnPerTimeList(HttpServletRequest request, String chnno,
			String date) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		List<CloudPerHalfHourProfit> list = cloudPerHalfHourProfitMapper.queryForPagePerTimeChns(chnno, date);
		checkUType(cuser, list);
		return list;
	}

	private void checkUType(CUser user, List<CloudPerHalfHourProfit> list) {
		if (user.getuType().intValue() == 0) {
			for (CloudPerHalfHourProfit cloudPerHalfHourProfit : list) {
				cloudPerHalfHourProfit.setProfit(BigDecimal.ZERO);
				cloudPerHalfHourProfit.setLoss(BigDecimal.ZERO);
				cloudPerHalfHourProfit.setContractMoney(BigDecimal.ZERO);
				cloudPerHalfHourProfit.setfProfit(BigDecimal.ZERO);
			}
		} else {
			for (CloudPerHalfHourProfit cloudPerHalfHourProfit : list) {
				cloudPerHalfHourProfit.setContractMoney(
						cloudPerHalfHourProfit.getProfit().subtract(cloudPerHalfHourProfit.getLoss()));
				cloudPerHalfHourProfit
						.setfProfit(cloudPerHalfHourProfit.getProfit().add(cloudPerHalfHourProfit.getLoss()));
			}

		}
	}

	private void checkUTypeByDay(CUser user, List<CloudChnDayProfit> list) {
		if (user.getuType().intValue() == 0) {
			for (CloudChnDayProfit dayProfit : list) {
				dayProfit.setProfit(BigDecimal.ZERO);
				dayProfit.setLoss(BigDecimal.ZERO);
				dayProfit.setContractMoney(BigDecimal.ZERO);
				dayProfit.setfProfit(BigDecimal.ZERO);
			}
		} else {
			for (CloudChnDayProfit dayProfit : list) {
				// dayProfit.setContractMoney(dayProfit.getProfit().subtract(dayProfit.getLoss()));
				dayProfit.setfProfit(dayProfit.getProfit().add(dayProfit.getLoss()));
			}

		}
	}

	private void checkUTypeByDayForObject(CUser user, CloudChnSumProfit obj) {
		if (user.getuType().intValue() == 0) {
			obj.setProfit(BigDecimal.ZERO);
			obj.setLoss(BigDecimal.ZERO);
			obj.setContractMoney(BigDecimal.ZERO);
			obj.setfProfit(BigDecimal.ZERO);
		} else {
			obj.setfProfit(obj.getProfit().add(obj.getLoss()));
		}
	}

	public List<CloudChnUserDayProfit> queryForPageChnUserProfit(HttpServletRequest request, String wxnm, String chnno,
			String begin, String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return chnUserDayProfitMapper.queryForPageChnUserProfit(cuser.getId(), wxnm, chnno, begin, end);
	}

	public List<CloudFundHistory> queryForPageChnFillList(HttpServletRequest request, String chnno, String begin,
			String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return cloudFundHistoryMapper.queryForPageChnFillList(cuser.getId(), chnno, begin, end);
	}

	public int queryFillUserCount(HttpServletRequest request, String chnno, String begin, String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return cloudFundHistoryMapper.queryFillUserCount(cuser.getId(), chnno, begin, end);
	}

	public BigDecimal queryFillAmount(HttpServletRequest request, String chnno, String begin, String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return cloudFundHistoryMapper.queryFillAmount(cuser.getId(), chnno, begin, end);
	}

	public List<CloudOrder> queryForPageChnOrderList(HttpServletRequest request, String chnno, String begin,
			String end) {
		return cloudOrderMapper.queryForPageChnOrderList(chnno, begin, end);
	}

	public List<CloudChnUserDayProfit> queryForPageChnAllUserSum(HttpServletRequest request, String chnno, String begin,
			String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return chnUserDayProfitMapper.queryForPageChnAllUserSum(cuser.getId(), chnno, begin, end);
	}

	public CloudUser selectUserByUid(Integer uid) {
		return cloudUserMapper.selectByPrimaryKey(uid);
	}

	public List<CloudOrder> queryForPageUserOrderList(HttpServletRequest request, Integer uid) {
		return cloudOrderMapper.queryForPageUserOrderList(uid);
	}

	public List<CloudChnUserDayProfit> downChnAllUserSum(HttpServletRequest request, String chnno, String begin,
			String end) {
		CUser cuser = (CUser) request.getSession().getAttribute(Constant.SESSION_USER);
		return chnUserDayProfitMapper.downChnAllUserSum(cuser.getId(), chnno, begin, end);
	}

	public void saveHumanConfig(CloudHumanControlConfig humanConfig) {
		humanConfig.setStatus(0);
		humanConfig.setRunedCount(0);
		humanConfig.setRunTime(new Date());
		humanConfigMapper.insertSelective(humanConfig);
	}

}

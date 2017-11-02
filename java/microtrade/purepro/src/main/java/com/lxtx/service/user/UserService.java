package com.lxtx.service.user;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.lxtx.dao.CloudChnCodeMapper;
import com.lxtx.dao.CloudUserLimitMapper;
import com.lxtx.dao.CloudUserMapper;
import com.lxtx.dao.CloudWxServiceProviderMapper;
import com.lxtx.model.CloudChnCode;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudUserLimit;
import com.lxtx.model.CloudWxServiceProvider;
import com.lxtx.model.vo.BrokerUnderPerVo;
import com.lxtx.util.BizException;
import com.lxtx.util.Constant;
import com.lxtx.util.SystemSwitchConfig;
import com.lxtx.util.tencent.EmojiFilter;
import com.lxtx.util.tencent.UserInfo;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tool.QRCodeUtil;

@Service
public class UserService implements InitializingBean{
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private CloudUserMapper userMapper;

	@Autowired
	private CloudUserLimitMapper cloudUserLimitMapper;

	@Autowired
	private CloudChnCodeMapper chnCodeMapper;

	@Autowired
	private CloudWxServiceProviderMapper wxServiceProviderMapper;

	public CloudWxServiceProvider getServiceProviderById(Integer wxServiceId) {
		return wxServiceProviderMapper.selectByPrimaryKey(wxServiceId);
	}

	public CloudWxServiceProvider getServiceProviderByOrigin(String origin) {
		return wxServiceProviderMapper.selectByOrigin(origin);
	}

	public CloudWxServiceProvider getActiveServiceProvider() {
		return wxServiceProviderMapper.getActiveProvider();
	}

	/**
	 * 
	 * Description:微信登录访问，此时该用户没有设置交易密码和手机号验证
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午8:06:57
	 */
	public CloudUser wxVisit(UserInfo info) {
		String wxid = info.getOpenid();
		CloudUser user = userMapper.selectByWxid(wxid);
		if (user == null) {
			logger.info("user with openid:" + wxid + " doesn't exist.");
			String chnno = info.getChnno();
			if (Strings.isNullOrEmpty(chnno)) {
				logger.error("user subscribe error,please subscribe again");
				return null;
			} else {
				logger.info("save user");
		        user = new CloudUser();
		        user.setChnno(chnno);
		        user.setCrtTm(new Date());
		        user.setFirstVisit(0);
		        user.setHeadimgurl(info.getHeadimgurl());
		        user.setUserStatus(Constant.USER_STAT_ND_PWD);
		        user.setWxid(info.getOpenid());
		        user.setBalance(BigDecimal.ZERO);
		        user.setContractAmount(BigDecimal.ZERO);
		        user.setWxnm(EmojiFilter.filterEmoji(info.getNickname()));
		        user.setIsSubscribe(1);
		        user.setWxProviderId(info.getWxProviderId());
				try {
			        insertUser(user);          
		        } catch (Exception e) {
		        	logger.error(e.getMessage());
		        	//bypass the wxnm problem
		        	user.setWxnm("guest" + info.getOpenid());
		        	insertUser(user);
		        }
			}
		} else {
			// 第一次访问，需要修改用户信息 微信头像 微信昵称
			if (StringUtils.isEmpty(user.getHeadimgurl())) {
				user.setWxnm(EmojiFilter.filterEmoji(info.getNickname()));
				if (user.getBalance() == null) {
					user.setBalance(BigDecimal.ZERO);
				}
				user.setHeadimgurl(info.getHeadimgurl());
				userMapper.updateByPrimaryKeySelective(user);
			}
			if (StringUtils.isEmpty(user.getPassword())) {
				// 未设置密码
				user.setUserStatus(Constant.USER_STAT_ND_PWD);
				// } else if (StringUtils.isEmpty(user.getMobile())) {
				// // 用户状态为未设置手机号
				// user.setUserStatus(Constant.USER_STAT_ND_TEL);
			} else {
				// 正常
				user.setUserStatus(Constant.USER_STAT_NORMAL);
			}
		}
		//无需输入交易密码
		String checkPwd = SystemSwitchConfig.getInstance().get("checkPwd");
		if(checkPwd.equals("1")){
			user.setLoginStatus("1");
			user.setUserStatus(Constant.USER_STAT_NORMAL);
		}
		return user;
	}

	public CloudUser selectUserById(int id) {
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 
	 * Description: 添加手机号
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午9:24:57
	 */
	public void updateTelById(CloudUser user) {
		userMapper.updateTelById(user);
	}

	public void updatePwdById(CloudUser user) {
		userMapper.updatePwdById(user);
	}

	public void updateBalanceById(BigDecimal addMoney, int uid) {
		CloudUser user = new CloudUser();
		String fillRate = WXPayConfig.get("fill.rate");
		if (!Strings.isNullOrEmpty(fillRate)) {
			double adjustedAmt = addMoney.doubleValue()*(1- Double.valueOf(fillRate).doubleValue()/1000);
			user.setBalance((new BigDecimal(adjustedAmt)).setScale(2, BigDecimal.ROUND_HALF_UP));
		} else {
			user.setBalance(addMoney);
		}
		user.setId(uid);
		userMapper.updateBalanceById(user);
	}

	public int reduceBalanceById(BigDecimal reducedMoney, int uid) {
		CloudUser user = new CloudUser();
		user.setBalance(reducedMoney);
		user.setId(uid);
		return userMapper.reduceBalanceById(user);
	}

	public int checkTelExist(String mobile) {
		return userMapper.checkTelExist(mobile);
	}

	public CloudUser selectByWxid(String wxid) {
		CloudUser user = userMapper.selectByWxid(wxid);
		if (user != null) {
			if (StringUtils.isEmpty(user.getPassword())) {
				// 未设置密码
				user.setUserStatus(Constant.USER_STAT_ND_PWD);
				// } else if (StringUtils.isEmpty(user.getMobile())) {
				// // 用户状态为未设置手机号
				// user.setUserStatus(Constant.USER_STAT_ND_TEL);
			} else {
				// 正常
				user.setUserStatus(Constant.USER_STAT_NORMAL);
			}
			//无需输入交易密码
			String checkPwd = SystemSwitchConfig.getInstance().get("checkPwd");
			if (checkPwd.equals("1")) {
				user.setLoginStatus("1");
				user.setUserStatus(Constant.USER_STAT_NORMAL);
			}
		}
		return user;
	}

	public List<BrokerUnderPerVo> selectUnderPerByMbl(Integer brokerId, String mobile) {
		return userMapper.selectUnderPerByMbl(brokerId, mobile);
	}

	/**
	 * 完成新手提示后 修改状态
	 * 
	 * @param id
	 */
	public void updateVisitById(Integer id) {
		CloudUser user = new CloudUser();
		user.setId(id);
		user.setFirstVisit(1);
		userMapper.updateVisitById(user);
	}

	/**
	 * 保存用户并添加登录状态
	 * 
	 * @param user
	 */
	public void insertUser(CloudUser user) {
		userMapper.insertSelective(user);
		CloudUserLimit userLimit = new CloudUserLimit();
		userLimit.setUid(user.getId());
		userLimit.setDayRepayAmount(BigDecimal.ZERO);
		userLimit.setDayRepayCount(0);
		userLimit.setDayTranAmount(BigDecimal.ZERO);
		cloudUserLimitMapper.insertSelective(userLimit);
	}

	public void updateSubScribeByWxId(String fromUserName, Integer subscribe, String chnno) {
		userMapper.updateSubScribeByWxId(fromUserName, subscribe, chnno);
	}

	/**
	 * 检查用户是否存在
	 * 
	 * @param wxid
	 * @return true 存在
	 */
	public boolean checkUserExist(String wxid) {
		int i = userMapper.checkUserExist(wxid);
		if (i > 0) {
			return true;
		}
		return false;
	}

	public CloudChnCode createChnnoQrCode(String chnno, CloudWxServiceProvider provider) {
		boolean isNum = chnno.matches("[1-9][0-9]{5}");
		if (!isNum) {
			throw new BizException("请输入6位数字的渠道号");
		}
		int length = chnno.length();
		chnno = chnno.substring(2, length);
		CloudChnCode c = chnCodeMapper.selectByChnno(chnno);
		if (c != null) {
			if (c.getWxProviderId() == provider.getId()) {
				return c;
			} else {
				c.setWxProviderId(provider.getId());
				c.setCodeUrl(QRCodeUtil.createForeverCode(chnno, provider));
				chnCodeMapper.updateByPrimaryKey(c);
			}
		}
		CloudChnCode chnCode = new CloudChnCode();
		try {
			String url = QRCodeUtil.createForeverCode(chnno, provider);
			chnCode.setCodeUrl(url);
			chnCode.setChnno(chnno);
			chnCode.setWxProviderId(provider.getId());
			chnCodeMapper.insert(chnCode);
			return chnCode;
		} catch (Exception e) {
			throw new BizException(e.getMessage());
		}
	}

	public static void main(String[] args) {
		BigDecimal addMoney = new BigDecimal(5.0);
		String fillRate = "6";
		double adjustedAmt = addMoney.doubleValue()*(1- Double.valueOf(fillRate).doubleValue()/1000);
		BigDecimal d = (new BigDecimal(adjustedAmt)).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println(d.doubleValue());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		logger.info("after properties set");
	}
	
}

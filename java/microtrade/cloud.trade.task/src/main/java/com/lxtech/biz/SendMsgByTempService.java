package com.lxtech.biz;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudUserDao;
import com.lxtech.dao.TempMsgDao;
import com.lxtech.dao.WxProviderDao;
import com.lxtech.model.CloudTempMsg;
import com.lxtech.model.CloudUser;
import com.lxtech.model.CloudWxServiceProvider;
import com.lxtech.util.TimeUtil;
import com.lxtech.util.wx.SendTempMsgUtil;

public class SendMsgByTempService {

	private static final Logger logger = LoggerFactory.getLogger(SendMsgByTempService.class);

	public void sendAllMsg(Date date) throws SQLException {
		logger.info("sendAllMsg Task begin:" + TimeUtil.formatDate(date));
		// 查询当前的provider
		CloudWxServiceProvider provider = WxProviderDao.queryActiveProvider();

		if (provider != null) {
			String now = TimeUtil.formatDate(date);
			//
			// 根据provider和日期、【全部】消息类型=1、状态=0 查询 待发的1条msg
			CloudTempMsg tempMsg = TempMsgDao.queryTempMsg(provider.getId(), now, 1, 0);
			if (tempMsg != null) {
				TempMsgDao.updateTempMsg(tempMsg.getId(), 1);
				// 根据当前公众号标志、关注状态 查询所有关注的用户
				// 全部，查询用户信息：当前公众号标志、关注状态
				List<CloudUser> users = CloudUserDao.queryUserByPorviderId(provider.getId(), 1);
				logger.info("users size:" + users.size());
				if (users != null && users.size() > 0) {
					// 获取token
					String token = SendTempMsgUtil.getAccessToken(provider);
					if (StringUtils.isNotEmpty(token)) {
						// 循环发送
						for (CloudUser user : users) {
							SendTempMsgUtil.sendTempMsg(tempMsg, user, token);
						}
					} else {
						logger.error("sendAllMsg Task error：get access_token error!");
						return;
					}
				} else {
					logger.warn("sendAllMsg Task warn: users is null!");
					return;
				}
			} else {
				logger.warn("sendAllMsg Task warn: tempMsg is null!");
				return;
			}
		} else {
			logger.warn("sendAllMsg Task warn: provider is null!");
			return;
		}

		MarkLogUtil.markLog(date, "sendAllMsg Task");
	}

	public void sendSingleMsg(Date date) throws SQLException {
		logger.info("sendSingleMsg Task begin:" + TimeUtil.formatDate(date));
		// 查询当前的provider
		CloudWxServiceProvider provider = WxProviderDao.queryActiveProvider();

		if (provider != null) {
			String now = TimeUtil.formatDate(date);
			//
			// 根据provider和日期、【个人】消息类型=0、状态=0 查询 待发的1条msg
			CloudTempMsg tempMsg = TempMsgDao.queryTempMsg(provider.getId(), now, 0, 0);
			if (tempMsg != null) {
				TempMsgDao.updateTempMsg(tempMsg.getId(), 1);
				if (tempMsg.getUid() != null) {
					// 查询用户信息：用户id、当前公众号标志、关注状态
					CloudUser user = CloudUserDao.queryUserByUidAndProviderId(tempMsg.getUid(),
							tempMsg.getWx_provider_id(), 1);
					if (user != null) {
						// 获取token
						String token = SendTempMsgUtil.getAccessToken(provider);
						if (StringUtils.isNotEmpty(token)) {
							// 发送
							SendTempMsgUtil.sendTempMsg(tempMsg, user, token);
						} else {
							logger.error("sendSingleMsg Task error：get access_token error!");
							return;
						}

					} else {
						logger.error("sendSingleMsg Task error：user is not found!");
						return;
					}
				} else {
					logger.error("sendSingleMsg Task error：uid is null!");
					return;
				}
			} else {
				logger.warn("sendSingleMsg Task warn:tempMsg is null!");
				return;
			}
		} else {
			logger.warn("sendSingleMsg Task warn: provider is null!");
			return;
		}
		MarkLogUtil.markLog(date, "sendSingleMsg Task");
	}

	private static void setTempMsg(BigDecimal fill, int type) {
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal profit = BigDecimal.ZERO;
		BigDecimal profitRate = BigDecimal.ZERO;
		if (type == 0) {
			if (fill.intValue() < 1000) {
				amount = new BigDecimal(1000).add(new BigDecimal(new Random().nextInt(50) * 10)).setScale(0,
						BigDecimal.ROUND_HALF_UP);
			} else if (fill.intValue() >= 1000) {
				amount = new BigDecimal(fill.intValue() / 100 * 100).multiply(new BigDecimal(1.5))
						.add(new BigDecimal(new Random().nextInt(10) * 10)).setScale(0, BigDecimal.ROUND_HALF_UP);
			}
		} else {
			if (fill.intValue() < 5000) {
				amount = new BigDecimal(5000).add(new BigDecimal(new Random().nextInt(25) * 100)).setScale(0,
						BigDecimal.ROUND_HALF_UP);
			} else if (fill.intValue() >= 5000) {
				amount = new BigDecimal(fill.intValue()).multiply(new BigDecimal(1.5))
						.add(new BigDecimal(new Random().nextInt(50) * 10)).setScale(0, BigDecimal.ROUND_HALF_UP);
			}
		}
		profit = amount.multiply(new BigDecimal(10 + Math.random() * (25 - 10)).divide(new BigDecimal(10)).setScale(2,
				BigDecimal.ROUND_HALF_UP)).setScale(1, BigDecimal.ROUND_HALF_UP);
		profitRate = profit.divide(amount).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
		System.out.println(amount + "元");
		System.out.println(profit + "元");
		System.out.println(profitRate + "%");
	}


	public static void main(String[] args) {
		// SendMsgByTempService service = new SendMsgByTempService();
		// try {
		// service.sendAllMsg(new Date());
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

		 setTempMsg(new BigDecimal(260), 0);

		// createTempMsg();
	}

}

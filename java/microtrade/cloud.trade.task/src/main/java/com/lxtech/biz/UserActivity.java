package com.lxtech.biz;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudUserActivitySumDao;
import com.lxtech.dao.CloudUserDao;
import com.lxtech.model.CloudUser;
import com.lxtech.model.CloudUserActivitySum;
import com.lxtech.util.TimeUtil;

public class UserActivity {
	private final static Logger logger = LoggerFactory.getLogger(UserActivity.class);

	public void userActivity() {
		Date now = new Date();
		String sumDate = TimeUtil.formatDate(now, "yyyy-MM-dd");

		try {
			// 查询所有用户
			List<CloudUser> users = CloudUserDao.queryAllUser();
			if (users != null && users.size() > 0) {
				for (CloudUser cloudUser : users) {
					// 查询单个用户的总笔数、交易金额、贡献手续费、头寸等
					CloudUserActivitySum u = CloudUserActivitySumDao.queryUserActivitySum(cloudUser.getId());
					// 查询单个用户的交易总天数
					int tran_day_count = CloudUserActivitySumDao.queryUserTranOrderSum(cloudUser.getId());
					u.setTran_day_count(tran_day_count);
					u.setUid(cloudUser.getId());
					if (StringUtils.isNotEmpty(cloudUser.getWxnm())) {
						u.setWxnm(cloudUser.getWxnm());
					} else {
						u.setWxnm("");
					}
					u.setDate(sumDate);
					try {
						int i = CloudUserActivitySumDao.updateData(u);
						if (i == 0) {
							CloudUserActivitySumDao.saveData(u);
						}
					} catch (SQLException e) {
						logger.error(e.getMessage());
						e.printStackTrace();
					}
				}

			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		UserActivity s = new UserActivity();
		s.userActivity();
	}

}

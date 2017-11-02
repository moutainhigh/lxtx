package com.lxtech.biz;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudOpenCloseDao;
import com.lxtech.dao.CloudTargetIndexMinuteDao;
import com.lxtech.model.CloudOpenClose;
import com.lxtech.model.CloudTargetIndexMinute;
import com.lxtech.util.TimeUtil;

public class OpenCloseService {
	private static final Logger logger = LoggerFactory.getLogger(OpenCloseService.class);

	public void createCloseAndOpen(Date date) throws SQLException {
		Date now = new Date();
		// 周六周日不生成今开昨收数据
		if (now.getDay() == 0 || now.getDay() == 6) {
			logger.info("createCloseAndOpen Task not run :" + TimeUtil.formatDate(now));
			return;
		}
		logger.info("createCloseAndOpen Task begin:" + TimeUtil.formatDate(now));
		// 查询各产品的最后一条记录 构造今开昨收数据
		List<CloudTargetIndexMinute> list = CloudTargetIndexMinuteDao.selectLastData();
		for (CloudTargetIndexMinute cloudTargetIndexMinute : list) {
			CloudOpenClose openClose = new CloudOpenClose();
			openClose.setLastClose(cloudTargetIndexMinute.getIdx());
			openClose.setSubject(cloudTargetIndexMinute.getSubject());
			openClose.setLow(0);
			openClose.setHigh(0);
			openClose.setOpen(0);
			int i = CloudOpenCloseDao.updateBySubject(openClose);
			if (i == 0) {
				CloudOpenCloseDao.insertSelective(openClose);
			}
		}

		MarkLogUtil.markLog(date, "open&close_task");
	}

}

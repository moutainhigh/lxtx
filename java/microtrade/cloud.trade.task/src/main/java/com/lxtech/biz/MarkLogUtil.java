package com.lxtech.biz;

import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudSystemTaskDetailDao;
import com.lxtech.model.CloudSystemTaskDetail;
import com.lxtech.util.TimeUtil;

public class MarkLogUtil {
	private static final Logger logger = LoggerFactory.getLogger(MarkLogUtil.class);

	/**
	 * 任务日志记录
	 * 
	 * @param begin_clear
	 * @param taskNm
	 */
	public static void markLog(Date begin_clear, String taskNm) {
		Date close_end = new Date();
		long closeCom = (close_end.getTime() - begin_clear.getTime());
		CloudSystemTaskDetail taskDetail = new CloudSystemTaskDetail();
		taskDetail.setExecDay(TimeUtil.formatDate(close_end, "yyyy-MM-dd"));
		taskDetail.setExecTime(TimeUtil.formatDate(close_end, "HH:mm:ss"));
		taskDetail.setTaskNm(taskNm);
		int i = 0;
		try {
			i = CloudSystemTaskDetailDao.updateByDayAndTaskNm(taskDetail);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		if (i == 0) {
			try {
				CloudSystemTaskDetailDao.insertTaskDetail(taskDetail);
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
		logger.info(taskNm + TimeUtil.formatDateString(close_end) + " used time:" + closeCom + " ms");
	}
}

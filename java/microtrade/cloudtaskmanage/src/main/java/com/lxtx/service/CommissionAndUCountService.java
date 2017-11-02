package com.lxtx.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudChnBackCommissionMapper;
import com.lxtx.dao.CloudSystemTaskDetailMapper;
import com.lxtx.model.CloudChnBackCommission;
import com.lxtx.model.CloudSystemTaskDetail;
import com.lxtx.util.StringUtil;

@Service
public class CommissionAndUCountService {
	private static final Logger logger = LoggerFactory.getLogger(CommissionAndUCountService.class);
	@Autowired
	private CloudChnBackCommissionMapper cloudChnBackCommissionMapper;

	@Autowired
	private CloudSystemTaskDetailMapper cloudSystemTaskDetailMapper;

	public void commissionAndUCountTask() {
		Date now = new Date();
		String nowDate = StringUtil.formatDate(now, "yyyy-MM-dd");
		logger.info("commissionAndUCount Task begin:" + StringUtil.formatDate(now));
		// 查询各渠道的手续费及人数
		List<CloudChnBackCommission> list = cloudChnBackCommissionMapper.sumCommissionAndUcount(nowDate);
		int nowInt = StringUtil.formatDate();
		for (CloudChnBackCommission cloudChnBackCommission : list) {
			cloudChnBackCommission.setDay(nowInt);
			int i = cloudChnBackCommissionMapper.updateByChnnoAndDate(cloudChnBackCommission);
			if (i == 0) {
				cloudChnBackCommissionMapper.insertSelective(cloudChnBackCommission);
			}
		}
		markLog(now, "commissionAndUCount");
	}

	/**
	 * 任务日志记录
	 * 
	 * @param begin_clear
	 * @param taskNm
	 */
	private void markLog(Date begin_clear, String taskNm) {
		Date close_end = new Date();
		long closeCom = (close_end.getTime() - begin_clear.getTime());
		CloudSystemTaskDetail taskDetail = new CloudSystemTaskDetail();
		taskDetail.setExecDay(StringUtil.formatDate(close_end, "yyyy-MM-dd"));
		taskDetail.setExecTime(StringUtil.formatDate(close_end, "HH:mm:ss"));
		taskDetail.setTaskNm(taskNm);
		int i = cloudSystemTaskDetailMapper.updateByDayAndTaskNm(taskDetail);
		if (i == 0) {
			cloudSystemTaskDetailMapper.insertSelective(taskDetail);
		}
		logger.info(taskNm + " Task end:" + StringUtil.formatDate(close_end) + " used time:" + closeCom + " ms");
	}
}

package com.lxtx.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudOpenCloseMapper;
import com.lxtx.dao.CloudSystemTaskDetailMapper;
import com.lxtx.dao.CloudTargetIndexMinuteMapper;
import com.lxtx.model.CloudOpenClose;
import com.lxtx.model.CloudSystemTaskDetail;
import com.lxtx.model.CloudTargetIndexMinute;
import com.lxtx.util.StringUtil;

/**
 * 未用到，改任务迁移到了cloud.trade.task工程，与k线生成任务一起
 * @author ming
 *
 */
@Service
public class CreateCloseAndOpenService {
	private static final Logger logger = LoggerFactory.getLogger(CommissionAndUCountService.class);
	@Autowired
	private CloudOpenCloseMapper cloudOpenCloseMapper;

	@Autowired
	private CloudTargetIndexMinuteMapper cloudTargetIndexMinuteMapper;

	@Autowired
	private CloudSystemTaskDetailMapper cloudSystemTaskDetailMapper;

	public void createCloseAndOpenTask() {
		Date now = new Date();
		logger.info("createCloseAndOpen Task begin:" + StringUtil.formatDate(now));
		// 查询各产品的最后一条记录 构造今开昨收数据
		List<CloudTargetIndexMinute> list = cloudTargetIndexMinuteMapper.selectLastData();
		for (CloudTargetIndexMinute cloudTargetIndexMinute : list) {
			CloudOpenClose openClose = new CloudOpenClose();
			openClose.setLastClose(cloudTargetIndexMinute.getIdx());
			openClose.setSubject(cloudTargetIndexMinute.getSubject());
			openClose.setLow(0);
			openClose.setHigh(0);
			openClose.setOpen(0);
			int i = cloudOpenCloseMapper.updateBySubject(openClose);
			if (i == 0) {
				cloudOpenCloseMapper.insertSelective(openClose);
			}
		}
		markLog(now, "createCloseAndOpen");
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
		logger.info(taskNm +" Task end:"+ StringUtil.formatDate(close_end) + " used time:" + closeCom + " ms");
	}
}

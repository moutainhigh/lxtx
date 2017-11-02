package com.lxtech.util.job;

import java.sql.SQLException;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.KCreatorUtil;
import com.lxtech.biz.KCreatorUtilGen;
import com.lxtech.biz.MarkLogUtil;
import com.lxtech.util.TimeUtil;

public class KCreatorJob_60 implements Job {
	private final static Logger logger = LoggerFactory.getLogger(KCreatorJob_60.class);

	private static void executeKCreatorJob_60() throws SQLException {
		Date date = new Date();
		logger.info("=============executeKCreatorJob_60 begin=============");
		KCreatorUtil creatorUtil = new KCreatorUtil();
		String d = TimeUtil.formatDateString(date);
		creatorUtil.KCreatorFactory(60, d);
		Date end = new Date();
		MarkLogUtil.markLog(date, "60k_task");
		logger.info(
				"=============executeKCreatorJob_60 end,耗时:" + (end.getTime() - date.getTime()) + " ms=============");
	}

	private static void executeKCreatorJob_60Gen() throws SQLException {
		Date date = new Date();
		logger.info("=============executeKCreatorJob_60Gen begin=============");
		KCreatorUtilGen creatorUtilGen = new KCreatorUtilGen();
		String d = TimeUtil.formatDateString(date);
		creatorUtilGen.KCreatorFactory(60, d);
		Date end = new Date();
		MarkLogUtil.markLog(date, "60k_task_gen");
		logger.info("=============executeKCreatorJob_60Gen end,耗时:" + (end.getTime() - date.getTime())
				+ " ms=============");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			executeKCreatorJob_60();
			executeKCreatorJob_60Gen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			executeKCreatorJob_60();
			executeKCreatorJob_60Gen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

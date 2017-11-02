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

public class KCreatorJob_5 implements Job {
	private final static Logger logger = LoggerFactory.getLogger(KCreatorJob_5.class);

	private static void executeKCreatorJob_5() throws SQLException {
		Date date = new Date();
		logger.info("=============executeKCreatorJob_5 begin=============");
		KCreatorUtil creatorUtil = new KCreatorUtil();
		String d = TimeUtil.formatDateString(date);
		creatorUtil.KCreatorFactory(5, d);
		Date end = new Date();
		MarkLogUtil.markLog(date, "5k_task");
		logger.info(
				"=============executeKCreatorJob_5 end,耗时:" + (end.getTime() - date.getTime()) + " ms=============");
	}

	private static void executeKCreatorJob_5Gen() throws SQLException {
		Date date = new Date();
		logger.info("=============executeKCreatorJob_5Gen begin=============");
		KCreatorUtilGen creatorUtilGen = new KCreatorUtilGen();
		String d = TimeUtil.formatDateString(date);
		creatorUtilGen.KCreatorFactory(5, d);
		MarkLogUtil.markLog(date, "5k_task_gen");
		Date end = new Date();
		logger.info(
				"=============executeKCreatorJob_5Gen end,耗时:" + (end.getTime() - date.getTime()) + " ms=============");
	}
	
	private static void executeKCreatorJob_5Gen(Date date) throws SQLException {
		logger.info("=============executeKCreatorJob_5Gen begin=============");
		KCreatorUtilGen creatorUtilGen = new KCreatorUtilGen();
		String d = TimeUtil.formatDateString(date);
		creatorUtilGen.KCreatorFactory(5, d);
		MarkLogUtil.markLog(date, "5k_task_gen");
		Date end = new Date();
		logger.info(
				"=============executeKCreatorJob_5Gen end,耗时:" + (end.getTime() - date.getTime()) + " ms=============");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			executeKCreatorJob_5();
			executeKCreatorJob_5Gen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
//			executeKCreatorJob_5();
			for (int i = 2; i<= 83; i++) {
				Date d = new Date(System.currentTimeMillis() - 300 * 1000*i);
				executeKCreatorJob_5Gen(d);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

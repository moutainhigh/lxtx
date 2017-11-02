package com.lxtech.util.job;

import java.sql.SQLException;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.SendMsgByTempService;

public class SendSingleMsgByTempJob implements Job {
	private final static Logger logger = LoggerFactory.getLogger(SendSingleMsgByTempJob.class);

	private static void executeSendSingleMsgByTempJob() throws SQLException {
		Date date = new Date();
		SendMsgByTempService sendMsgByTempService = new SendMsgByTempService();
		sendMsgByTempService.sendSingleMsg(date);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			executeSendSingleMsgByTempJob();
		} catch (SQLException e) {
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			executeSendSingleMsgByTempJob();
		} catch (SQLException e) {
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}

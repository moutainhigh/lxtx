package com.lxtech.util.job;

import java.sql.SQLException;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.ShellTempMsgService;

public class ShellTempMsgJob implements Job {
	private final static Logger logger = LoggerFactory.getLogger(ShellTempMsgJob.class);

	private static void execute() throws SQLException {
		Date date = new Date();
		ShellTempMsgService shellTempMsgService = new ShellTempMsgService();
		shellTempMsgService.sendMsg(date);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			execute();
		} catch (SQLException e) {
			logger.info(e.toString());
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			execute();
		} catch (SQLException e) {
			logger.info(e.toString());
			e.printStackTrace();
		}
	}
}

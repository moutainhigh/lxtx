package com.lxtech.util.job;

import java.sql.SQLException;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.biz.OpenCloseService;

public class CreateCloseAndOpenJob implements Job {
	private final static Logger logger = LoggerFactory.getLogger(KCreatorJob_5.class);

	private static void executeCloseAndOpenJob() throws SQLException {
		Date date = new Date();
		OpenCloseService openCloseService = new OpenCloseService();
		openCloseService.createCloseAndOpen(date);
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			executeCloseAndOpenJob();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			executeCloseAndOpenJob();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

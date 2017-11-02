package com.lxtech.cloud.main;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.CloudTargetHandler;
import com.lxtech.cloud.db.CloudTargetIndexChangeCopier;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;

public class DataCopyer implements Runnable{
	
	private final static Logger logger = LoggerFactory.getLogger(DataCopyer.class);
	
	private String target;
	
	public DataCopyer(String target) {
		this.target = target;
	}

	@Override
	public void run() {
		long start = 0;
		while(true) {
			start = copySomeRecords(start);
		}
	}
	
	private long copySomeRecords(long start) {
		List<CloudTargetIndexChange> changeList = null;
		
		try {
		  changeList = CloudTargetIndexChangeCopier.getIndexChangeList(this.target, start);
		  if (changeList != null && changeList.size() > 0) {
			  return this.handleIndexChanges(changeList);
		  } else {
			  logger.info("sleep for 60 seconds.");
			  TimeUnit.SECONDS.sleep(60);
			  return start;
		  }
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return start;
	}
	
	private long handleIndexChanges(List<CloudTargetIndexChange> changeList) {
		long idx = 0;
		for (CloudTargetIndexChange change : changeList) {
			Timer timer = new Timer();
			long delay = change.getTime().getTime() - System.currentTimeMillis();
			logger.info("handle change, delay is: " + delay);
			
			if(delay < 0) {
				continue;
			}
			
			timer.schedule(new UpdateIndexTask(this.target, (int)change.getIndex()), delay);
			idx = change.getId();
			try {
				TimeUnit.MILLISECONDS.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return idx;
	}
	
	private static class UpdateIndexTask extends TimerTask {
		private int index;
		
		private String subject;
		
		public UpdateIndexTask(String subject, int index) {
			this.subject = subject;
			this.index = index;
		}
		
		@Override
		public void run() {
			try {
				logger.info("now update index to:" + this.index);
				CloudTargetHandler.updateCloudTargetIndex(this.subject, this.index);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Thread t = new Thread(new DataCopyer("AG"));
		t.start();
	}
}

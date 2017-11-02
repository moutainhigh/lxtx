package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.db.model.CloudTargetIndexCopy;

public class CloudTargetIndexChangeCopier {
	private final static Logger logger = LoggerFactory.getLogger(CloudTargetIndexChangeCopier.class);
	
	public static List<CloudTargetIndexChange> getIndexChangeList(String subject, long startIndex) throws SQLException, ParseException {
		long curMillis = System.currentTimeMillis();
		Timestamp curTime = new Timestamp(curMillis);
		DateTime dt = new DateTime(curMillis);
		int dayOfWeek = dt.getDayOfWeek();
		int hourOfDay = dt.getHourOfDay();
		
		//周日，周六4点以后，周一9点之前，均返回空结果
		if (dayOfWeek == 7) {
			return Lists.newArrayList();
		} else if (dayOfWeek == 6) {
			if (hourOfDay >= 4) {
				return Lists.newArrayList();
			}
		} else if (dayOfWeek == 1) {
			if (hourOfDay < 9) {
				return Lists.newArrayList();
			}
		}
		
		Timestamp endTime = new Timestamp(dt.plusHours(1).getMillis());
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		if (startIndex == 0) {
			//search from current time
			String sql = "select * from cloud_target_index_change where `subject` = ? and `time` > ? and `time` < ? order by time asc limit 100";
			List<CloudTargetIndexChange> changeList =  qr.query(sql, new Object[]{subject, curTime, endTime}, new BeanListHandler<CloudTargetIndexChange>(CloudTargetIndexChange.class));
			logger.info("get change list including " + changeList.size() + " changes.");
			return changeList;
		} else {
			String sql = "select * from cloud_target_index_change where id > ? and subject = ? and `time` < ? limit 100";
			List<CloudTargetIndexChange> changeList = qr.query(sql, new Object[]{startIndex, subject, endTime}, new BeanListHandler<CloudTargetIndexChange>(CloudTargetIndexChange.class));
			logger.info("get change list including " + changeList.size() + " changes.");
			return changeList;
		}
	}
	
	public static void copyData(String fromDay, String targetDay, String subject) throws SQLException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = sdf.parse(fromDay);
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		DateTime dt = new DateTime(fromDate.getTime());
		long millis = dt.plusDays(1).getMillis();
		Date nextDay = new Date(millis);
		String nextDayStr = sdf.format(nextDay);
		
		Date targetDate = sdf.parse(targetDay);
		int dayGap = (int) ((targetDate.getTime() - fromDate.getTime())/(86400*1000));
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_target_index_change where `subject` = ? and `time` > ? and `time` < ? ";
		List<CloudTargetIndexChange> changeList = qr.query(sql, new Object[]{subject, new Timestamp(fromDate.getTime()), new Timestamp(nextDay.getTime())}, new BeanListHandler<CloudTargetIndexChange>(CloudTargetIndexChange.class));
		
		int count = 0;
		sql = "insert into cloud_target_index_change(`subject`, `time`, `index`) values (?, ?, ?)";
		//Object[][] objs = null;
		List<CloudTargetIndexChange> changes = Lists.newArrayList();
		logger.info("change count :" + changeList.size());
		
		for (CloudTargetIndexChange change : changeList) {
			if (count == 0) {
				changes = Lists.newArrayList();
			} 
			changes.add(change);
			if (count == 99) {
				qr.insertBatch(sql, new ScalarHandler<>(), convertListToObjectArr(changes, dayGap));
			}

			count++;
			if (count == 100) {
				count = 0;
			}
		}
	}
	
	private static Object[][] convertListToObjectArr(List<CloudTargetIndexChange> changes, int dayGap) {
		Object[][] objects = new Object[changes.size()][];
		int index = 0;
		for (CloudTargetIndexChange change : changes) {
			//objects[index++] = new Object[]{change.getSubject(), change.getTime(), change.getIndex()};
			objects[index++] = new Object[]{change.getSubject()+"1", new Timestamp(change.getTime().getTime() + (long)dayGap*86400*1000), change.getIndex()};
		}
		return objects;
	}
	
	public static void test() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_target_index_change limit 10 ";
		List<CloudTargetIndexChange> changeList = qr.query(sql, new BeanListHandler<CloudTargetIndexChange>(CloudTargetIndexChange.class));
		for (CloudTargetIndexChange change : changeList) {
			System.out.println(change.getIndex());
		}
	}
	
	public static void generateIndexChangeBatch(List<String> subjectList) throws SQLException, ParseException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_target_index_copy where done = 0";
		List<CloudTargetIndexCopy> copyList = qr.query(sql, new BeanListHandler<CloudTargetIndexCopy>(CloudTargetIndexCopy.class));
		for (CloudTargetIndexCopy copy:copyList) {
			for (String subject : subjectList) {
				CloudTargetIndexChangeCopier.copyData(copy.getSourceday(), copy.getTargetday(), subject);
			}
			//update status
			CloudTargetIndexChangeCopier.updateCopyDataStatus(copy.getId());
		}
	}
	
	
	private static void updateCopyDataStatus(long id) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_target_index_copy set done = 1 where id = ?";
		qr.update(sql, new Object[]{id});
	}

	public static void main(String[] args) {
		try {
			//CloudTargetIndexChangeCopier.generateIndexChangeBatch(ImmutableList.of("AG", "CU"));
			CloudTargetIndexChangeCopier.generateIndexChangeBatch(ImmutableList.of("CU"));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}


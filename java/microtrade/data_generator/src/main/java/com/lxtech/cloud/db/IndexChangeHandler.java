package com.lxtech.cloud.db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.db.model.CloudTargetIndexChange;
import com.lxtech.cloud.db.model.CloudTargetIndexMinute;
import com.lxtech.cloud.util.TimeUtil;

public class IndexChangeHandler {
	private final static Logger logger = LoggerFactory.getLogger(IndexChangeHandler.class);
	
	private static Map<String, CloudTargetIndexChange> indexMap = new HashMap<String, CloudTargetIndexChange>();

	public static int saveMinuteData(CloudTargetIndexMinute minData) throws SQLException {
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_target_index_minute_gen(`subject`, `dtime`, `idx`, `timeindex`, `day`) values(?,?,?,?,?)";
		Object params[] = {minData.getSubject(), minData.getDtime(), minData.getIdx(), minData.getTimeindex(), minData.getDay() };
		return qr.update(sql, params);
	}
	
	/**
	 * save dynamic data change
	 * @param change
	 * @return
	 * @throws SQLException
	 */
	public static synchronized int saveData(CloudTargetIndexChange change) throws SQLException {
		CloudTargetIndexChange savedChange = indexMap.get(change.getSubject());
		if (savedChange != null && change.getTime().getTime() <= savedChange.getTime().getTime()) {//the change already exists
			return -1;
		}
		
		//cache the change
		indexMap.put(change.getSubject(), change);
		
		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_target_index_change_gen(`subject`, `time`, `index`) values(?,?,?)";
		Object params[] = { change.getSubject(), change.getTime(), change.getIndex() };
		qr.update(sql, params);
		
		//now check whether the minute data needs to be saved
		if (savedChange != null) {
			Timestamp oldTime = savedChange.getTime();
			Timestamp newTime = change.getTime();
			int hourOfDay = TimeUtil.getCurrentHourOfDay();
			//if((hourOfDay < 4 || hourOfDay > 8) && (oldTime.getMinutes() != newTime.getMinutes())) {
			if(oldTime.getMinutes() != newTime.getMinutes()) {
				//minute time needs to be saved
				CloudTargetIndexMinute minData = new CloudTargetIndexMinute();
				int hours = oldTime.getHours();
				int timeindex = 0;
				String day = "";
				if (hours < 9) { //time between 0 ~ 9 should be recorded as the data for yesterday
					timeindex = 960 + hours * 60 + oldTime.getMinutes();
					day = TimeUtil.getDay(new Date(System.currentTimeMillis() - 86400 * 1000));
				} else {
					timeindex = (hours - 8)*60 + oldTime.getMinutes();
					day = TimeUtil.getDay(new Date(oldTime.getTime()));
				}
				
				minData.setTimeindex(timeindex);
				minData.setDay(day);
				minData.setIdx(savedChange.getIndex());
				minData.setSubject(savedChange.getSubject());
				minData.setDtime(savedChange.getTime());
				IndexChangeHandler.saveMinuteData(minData);
				
				String template = "Save minute data for %s, timeindex is :%s, index is %s, day is %s";
				logger.info(String.format(template, minData.getSubject(), minData.getTimeindex(), minData.getIdx(), minData.getDay()) );
			}
		}
		return 1;
	}
	
	/*public static void importMinData(String jsonData) throws SQLException {
		Map<String, Object> obj = (Map<String, Object>)JsonUtil.convertStringToObject(jsonData);
		List list= (List)obj.get("Trend");
		String subject = (String)obj.get("Code");
		Date currentDay = TimeUtil.parseDate((String)obj.get("Day"));
		
		for (int i=1; i<list.size(); i++) {
			List dataList = (List)list.get(i);
			//System.out.println(dataList.get(0) + "" + dataList.get(1));
			int timeindex = ((Double)dataList.get(0)).intValue();
			int idx = ((Double)dataList.get(1)).intValue();
			String day = TimeUtil.getDay(new Date());
			CloudTargetIndexMinute minData = new CloudTargetIndexMinute();
			minData.setDay(day);
			minData.setDtime(new Timestamp(currentDay.getTime() + (480 + timeindex)*60*1000));
			minData.setIdx(idx);
			minData.setSubject(subject);
			minData.setTimeindex(timeindex);
			IndexChangeHandler.saveMinuteData(minData);
		}
	}*/
	
	public static void main(String[] args) {
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = sdf.parse("2016-11-10 22:30:00");
			sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
			System.out.println(sdf.format(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
	}
	
}

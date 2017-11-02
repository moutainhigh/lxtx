package com.lxtech.cloudtrade.db;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloudtrade.db.model.IndexMinuteData;
import com.lxtech.cloudtrade.db.model.KLineData;
import com.lxtech.cloudtrade.net.KestrelConnector;


public class HqDataHandler {
	private final static Logger logger = LoggerFactory.getLogger(HqDataHandler.class);
	
//	private static Map<String, CloudTargetIndexChange> indexMap = new HashMap<String, CloudTargetIndexChange>();

//	public static int saveMinuteData(CloudTargetIndexMinute minData) throws SQLException {
//		// 将数据源传递给QueryRunner，QueryRunner内部通过数据源获取数据库连接
//		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
//		String sql = "insert into cloud_target_index_minute(`subject`, `dtime`, `idx`, `timeindex`, `day`) values(?,?,?,?,?)";
//		Object params[] = {minData.getSubject(), minData.getDtime(), minData.getIdx(), minData.getTimeindex(), minData.getDay() };
//		return qr.update(sql, params);
//	}

	public static List<IndexMinuteData> queryMinuteData(String dataSourceName, String day, String subject) throws Exception{
		String tableName = "cloud_target_index_minute";
		if (dataSourceName.equals(KestrelConnector.QUEUE_NAME_GEN)) {
			tableName = tableName + "_gen"; 
		}
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		Object[] params = {day, subject};
		return qr.query("select * from " + tableName + " where day = ? and subject = ? order by timeindex desc limit 120", params, new BeanListHandler<IndexMinuteData>(IndexMinuteData.class) );
	}
	
	public static void main(String[] args) throws Exception {
		/*long time = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			//List<IndexMinuteData> dataList = HqDataHandler.queryMinuteData("2016-11-10", "AG");
			List<KLineData> dataList = HqDataHandler.queryKLineData(KestrelConnector.QUEUE_NAME, 2, "BU");
			System.out.println(dataList.size());		
		}
		System.out.println("cost time:"+(System.currentTimeMillis() - time));*/
		List<IndexMinuteData> minDataList = HqDataHandler.queryMinuteData("GENDATA", "2017-08-05", "BTC");
		for (IndexMinuteData data : minDataList) {
			System.out.println(data.getDay() + " " + data.getIdx() + " " + data.getTimeindex());
		}
			
	}

	public static List<KLineData> queryKLineData(String dataSourceName, int klinetype, String sub) throws SQLException {
		String tableName = "cloud_k_period";
		if (dataSourceName.equals(KestrelConnector.QUEUE_NAME_GEN)) {
			tableName = tableName + "_gen"; 
		}
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from " + tableName + " where type = ? and subject = ? order by id desc limit 100";
		Object[] params = {klinetype, sub};
		return qr.query(sql, params, new BeanListHandler<KLineData>(KLineData.class));
	}
}

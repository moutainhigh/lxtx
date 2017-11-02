package com.lxtech.handler;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dbmodel.LotteryCqsscData;
import com.lxtech.inspect.utils.JdbcUtils;

public class LotteryCqsscDataHandler {
	private final static Logger logger = LoggerFactory.getLogger(LotteryCqsscDataHandler.class);
	public static int insert(LotteryCqsscData lcd) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into lottery_cqssc_data (id,date,serial_number,open_code,open_time) values(?,?,?,?,?)";
		Object params[] = { lcd.getId(), lcd.getDate(), lcd.getSerial_number(), lcd.getOpen_code(),
				lcd.getOpen_time() };
		return qr.update(sql, params);
	}
	
	public static LotteryCqsscData query(int date, int sn) throws SQLException{
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from lottery_cqssc_data where date = ? and serial_number = ?";
		Object params[] = {date, sn};
		return qr.query(sql,new BeanHandler<LotteryCqsscData>(LotteryCqsscData.class), params);
	}

	public static boolean exist(LotteryCqsscData lcd) throws SQLException {
		return query(lcd.getDate(),lcd.getSerial_number()) != null;
	}
	
	public static int insertORupdate(LotteryCqsscData lcd ){
		try {
			if (!exist(lcd)) {
				logger.info("save lottery data now, num:"+lcd.getSerial_number()+"\tdate:"+lcd.getDate()+"\topen_code:"+lcd.getOpen_code()+"\topen_time:"+lcd.getOpen_time());
				return insert(lcd);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return -1;
		}
	}
}

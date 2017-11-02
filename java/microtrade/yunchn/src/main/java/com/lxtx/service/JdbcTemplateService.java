package com.lxtx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class JdbcTemplateService {

	@Autowired
	JdbcTemplate jdbcTemplete;

	public void update() {
		jdbcTemplete
				.update("INSERT INTO m_order_attr_cats (order_id, resource) VALUES ('100', '客户端')");
	}

	public List<Map<String, Object>> selectAttrByOrderId(Integer orderId,
			String tableName) {
		return jdbcTemplete.queryForList("SELECT a.* from " + tableName + " a "
				+ " where order_id=" + orderId);
	}

	/**
	 * @param tableName
	 * @return
	 */
	public List<Map<String, Object>> selectColNameByTabName(String tableName) {
		// return
		// jdbcTemplete.queryForList("SELECT COLUMN_NAME,column_comment from information_schema.COLUMNS  where TABLE_name='"+tableName+"'");
		return jdbcTemplete
				.queryForList("SELECT COLUMN_NAME as col_name from information_schema.COLUMNS  where TABLE_name='"
						+ tableName + "'");
	}

	/**
	 * 关联字典表查询
	 * 
	 * @param tableName
	 * @return
	 */
	public List<Map<String, Object>> selectColNameByTabNameJoinTable(
			String tableName) {
		return jdbcTemplete
				.queryForList("SELECT a.COLUMN_NAME as col_name,b.col_value from information_schema.COLUMNS a left join m_commodity_attr_dic b on a.column_name=b.col_name where a.TABLE_NAME='"
						+ tableName + "' order by a.ordinal_position");
	}

	@SuppressWarnings("deprecation")
	public int selectCountOrderAttr(String tableName) {
		return jdbcTemplete.queryForInt("SELECT COUNT(*) FROM " + tableName);
	}

	public Map<String, Object> selectUsrAndUsrlogonByInstId(Integer instId) {
		List<Map<String, Object>> list = jdbcTemplete
				.queryForList("SELECT min(a.USR_ID) as USR_ID,a.USR_LOGON_NM,c.LOGON_PWD,c.MBL_NO from t_usr a LEFT join t_inst b ON a.INST_ID = b.INST_ID LEFT JOIN t_usr_logon_inf c on a.USR_LOGON_NM = c.USR_LOGON_NM where a.INST_ID= "
						+ instId
						+ " group by a.USR_LOGON_NM,c.LOGON_PWD,c.MBL_NO");
		// List<Map<String, Object>> list=
		// jdbcTemplete.queryForList("SELECT * from t_usr");
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public void updateInstStCode(Integer instId, String bsnCode, String status) {
		jdbcTemplete.execute("update t_inst_bsn_sign_rltnp set sign_st_code='"
				+ status + "' where INST_ID=" + instId + " and bsn_code='"
				+ bsnCode + "'");
	}

}

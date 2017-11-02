package com.lxtech.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudWxShell;
import com.lxtech.model.CloudWxShellTpl;
import com.lxtech.model.CloudWxShellUser;
import com.lxtech.model.CloudWxTplMsg;
import com.lxtech.util.JdbcUtils;

public class ShellTempMsgDao {

	/**
	 * 获取所有的壳
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static List<CloudWxShell> queryShell() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_shell ";
		Object params[] = {};
		return qr.query(sql, new BeanListHandler<CloudWxShell>(CloudWxShell.class), params);
	}

	/**
	 * 根据壳id,小时数，状态获取可用模板
	 * 
	 * @param sendHour
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public static List<CloudWxShellTpl> queryShellTpl(int shellId, int sendHour, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_shell_tpl where shell_id=? and send_hour=? and status=? ";
		Object params[] = { shellId, sendHour, status };
		return qr.query(sql, new BeanListHandler<CloudWxShellTpl>(CloudWxShellTpl.class), params);
	}

	/**
	 * 全部用户发送
	 * 
	 * @param msgType
	 * @param tplId
	 * @param date
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public static List<CloudWxTplMsg> queryTplMsg(int msgType, int tplId, String date, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_tpl_msg where msg_type=? and tpl_id=? and date=? and status=? ";
		Object params[] = { msgType, tplId, date, status };
		return qr.query(sql, new BeanListHandler<CloudWxTplMsg>(CloudWxTplMsg.class), params);
	}

	/**
	 * 单用户发送 供测试用
	 * 
	 * @param msgType
	 * @param uid
	 * @param tplId
	 * @param date
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public static List<CloudWxTplMsg> querySingleTplMsg(int msgType, String wxid, int tplId, String date, int status)
			throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_tpl_msg where msg_type=? and wxid=? and tpl_id=? and date=? and status=?";
		Object params[] = { msgType, wxid, tplId, date, status };
		return qr.query(sql, new BeanListHandler<CloudWxTplMsg>(CloudWxTplMsg.class), params);
	}

	/**
	 * 查询渠道的所有已关注用户
	 * 
	 * @param chnno
	 * @param status
	 * @return
	 * @throws SQLException
	 */
	public static List<CloudWxShellUser> queryAllUser(String chnno, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_shell_user where chnno=? and status=?";
		Object params[] = { chnno, status };
		return qr.query(sql, new BeanListHandler<CloudWxShellUser>(CloudWxShellUser.class), params);
	}

	public static List<CloudWxShellUser> querySingleUser(String wxid, String chnno, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_wx_shell_user where wxid=? and chnno=? and status=?";
		Object params[] = { wxid, chnno, status };
		return qr.query(sql, new BeanListHandler<CloudWxShellUser>(CloudWxShellUser.class), params);
	}

	public static int updateTplMsgStatus(int id, int status) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "update cloud_wx_tpl_msg set status = ? where id = ?";
		Object params[] = { status, id };
		return qr.update(sql, params);
	}

}

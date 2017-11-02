package com.lxtech.biz;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.ShellTempMsgDao;
import com.lxtech.model.CloudWxShell;
import com.lxtech.model.CloudWxShellTpl;
import com.lxtech.model.CloudWxShellUser;
import com.lxtech.model.CloudWxTplMsg;
import com.lxtech.util.TimeUtil;
import com.lxtech.util.wx.SendTempMsgUtil;

public class ShellTempMsgService {

	private static final Logger logger = LoggerFactory.getLogger(ShellTempMsgService.class);
	
	private ExecutorService es;
	
	public ShellTempMsgService() {
	  this.es = Executors.newFixedThreadPool(200);
	}
	
	public static class SendMsgTask implements Callable<Boolean> {
	  
	  private CloudWxTplMsg tplMsg;
	  
	  private CloudWxShellTpl tpl;
	  
	  private CloudWxShellUser user;
	  
	  private String token;

	  public SendMsgTask(CloudWxTplMsg tplMsg, CloudWxShellTpl tpl, CloudWxShellUser user, String token) {
	    this.tplMsg = tplMsg;
	    this.tpl = tpl;
	    this.user = user;
	    this.token = token;
	  }
	  
    @Override
    public Boolean call() throws Exception {
      try {
        SendTempMsgUtil.sendTempMsg(tplMsg, tpl, user, token);
      } catch (Exception e) {
        logger.error(e.getMessage());
        e.printStackTrace();
      }
      return true;
    }
	  
	}
	
	public void sendMsg(Date date) throws SQLException {
		logger.info("ShellTempMsg sendMsg Task begin:" + TimeUtil.formatDate(date));
		String now = TimeUtil.formatDate(date);
		// 查询所有的壳
		List<CloudWxShell> shells = ShellTempMsgDao.queryShell();
		if (shells != null && shells.size() > 0) {
			for (CloudWxShell shell : shells) {
				// 根据壳和时间点查询所有的模板
				List<CloudWxShellTpl> tpls = ShellTempMsgDao.queryShellTpl(shell.getId(), date.getHours(), 1);
				if (tpls != null && tpls.size() > 0) {
					for (CloudWxShellTpl tpl : tpls) {
						// 根据模板id和日期查询所有的消息
						List<CloudWxTplMsg> msgs = ShellTempMsgDao.queryTplMsg(1, tpl.getId(), now, 0);
						if (msgs != null && msgs.size() > 0) {
							List<CloudWxShellUser> users = ShellTempMsgDao.queryAllUser(shell.getChnno(), 1);
							for (CloudWxTplMsg tplMsg : msgs) {
								ShellTempMsgDao.updateTplMsgStatus(tplMsg.getId(), 1);
								// 查询所有用户
								if (users != null && users.size() > 0) {
									// 获取token
									String token = SendTempMsgUtil.getAccessToken(shell);
									if (StringUtils.isNotEmpty(token)) {
										for (CloudWxShellUser user : users) {
											/*try {
												SendTempMsgUtil.sendTempMsg(tplMsg, tpl, user, token);
											} catch (Exception e) {
												logger.error(e.getMessage());
												e.printStackTrace();
											}*/
										  this.es.submit(new SendMsgTask(tplMsg, tpl, user, token));
										}
									} else {
										logger.error("ERROR:ShellTempMsg sendMsg Task can not get token!appid:"
												+ shell.getApp_id());
									}
								} else {
									logger.warn(
											"WARN:ShellTempMsg sendMsg Task users is null!appid:" + shell.getApp_id());
								}
							}
						} else {
							logger.warn("WARN:ShellTempMsg sendMsg Task tplMsgs is null!appid:" + shell.getApp_id());
						}
					}
				}
			}
		}

		Date close_end = new Date();
		long closeCom = (close_end.getTime() - date.getTime());
		logger.info("ShellTempMsg sendMsg Task end " + TimeUtil.formatDateString(close_end) + " used time:" + closeCom
				+ " ms");
		this.es.shutdown();
	}

	public void sendSingleMsg(Date date) throws SQLException {
		logger.info("ShellTempMsg sendSingleMsg Task begin:" + TimeUtil.formatDate(date));
		String now = TimeUtil.formatDate(date);
		// 查询所有的壳
		List<CloudWxShell> shells = ShellTempMsgDao.queryShell();
		if (shells != null && shells.size() > 0) {
			for (CloudWxShell shell : shells) {
				// 根据壳和时间点查询所有的模板
				List<CloudWxShellTpl> tpls = ShellTempMsgDao.queryShellTpl(shell.getId(), date.getHours(), 1);
				if (tpls != null && tpls.size() > 0) {
					for (CloudWxShellTpl tpl : tpls) {
						// 根据模板id和日期查询所有的消息
						List<CloudWxTplMsg> msgs = ShellTempMsgDao.querySingleTplMsg(0, "oSdAWwJI2OL7GbfkpIZ2eZA2hxmA",
								tpl.getId(), now, 0);
						if (msgs != null && msgs.size() > 0) {
							List<CloudWxShellUser> users = ShellTempMsgDao
									.querySingleUser("oSdAWwJI2OL7GbfkpIZ2eZA2hxmA", shell.getChnno(), 1);
							for (CloudWxTplMsg tplMsg : msgs) {
								ShellTempMsgDao.updateTplMsgStatus(tplMsg.getId(), 1);
								// 查询所有用户
								if (users != null && users.size() > 0) {
									// 获取token
									String token = SendTempMsgUtil.getAccessToken(shell);
									if (StringUtils.isNotEmpty(token)) {
										for (CloudWxShellUser user : users) {
											try {
												SendTempMsgUtil.sendTempMsg(tplMsg, tpl, user, token);
											} catch (Exception e) {
												logger.error(e.getMessage());
												e.printStackTrace();
											}
										}
									} else {
										logger.error("ERROR:ShellTempMsg sendMsg Task can not get token!appid:"
												+ shell.getApp_id());
									}
								} else {
									logger.warn(
											"WARN:ShellTempMsg sendMsg Task users is null!appid:" + shell.getApp_id());
								}
							}
						} else {
							logger.warn("WARN:ShellTempMsg sendMsg Task tplMsgs is null!appid:" + shell.getApp_id());
						}
					}
				}
			}
		}

		Date close_end = new Date();
		long closeCom = (close_end.getTime() - date.getTime());
		logger.info("ShellTempMsg sendSingleMsg Task end " + TimeUtil.formatDateString(close_end) + " used time:"
				+ closeCom + " ms");
	}

	public static void main(String[] args) {
		try {
			ShellTempMsgService service = new ShellTempMsgService();
			service.sendMsg(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

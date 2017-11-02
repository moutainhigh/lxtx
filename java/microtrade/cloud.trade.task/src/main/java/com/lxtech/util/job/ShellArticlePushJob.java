package com.lxtech.util.job;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.lxtech.dao.CloudWeixinArticleDao;
import com.lxtech.dao.CloudWeixinShellDao;
import com.lxtech.dao.ShellTempMsgDao;
import com.lxtech.model.CloudWeixinShell;
import com.lxtech.model.CloudWeixinShellArticlePush;
import com.lxtech.model.CloudWxShellArticle;
import com.lxtech.model.CloudWxShellUser;
import com.lxtech.util.JsonUtil;
import com.lxtech.util.http.HttpRequest;
import com.mchange.lang.StringUtils;

public class ShellArticlePushJob implements Job{

	private ExecutorService es;
	
	public ShellArticlePushJob() {
		es = Executors.newFixedThreadPool(50);
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			List<CloudWeixinShellArticlePush> pushList = CloudWeixinArticleDao.getArticlePushList();
			for ( CloudWeixinShellArticlePush task : pushList) {
				int hour = Integer.valueOf(task.getTime());
				DateTime dt = new DateTime();
				if (dt.getHourOfDay() == hour) {
					//send this article
					String shellList = task.getShells();
					for (String shellId : Splitter.on(",").split(shellList)) {
						CloudWeixinShell shell = CloudWeixinShellDao.getShellById(Integer.valueOf(shellId).intValue());
						
						if (shell != null) {
							List<CloudWxShellUser> cloudUserList = ShellTempMsgDao.queryAllUser(shell.getChnno(), 0);
							for (CloudWxShellUser user : cloudUserList) {
//								es.submit(arg0)
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String getAccessToken(CloudWeixinShell shell) {
		String param = String.format("grant_type=client_credential&appid=%s&secret=%s", shell.getApp_id(), shell.getApp_secret());
		String response = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", param);
		Map map = (Map) JsonUtil.convertStringToObject(response);
		return (String)map.get("access_token");
	}
	
	private static List<String> getAllCS(String access_token) {
		String param = String.format("access_token=%s", access_token);
		String response = HttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/customservice/getkflist", param);
		System.out.println(response);
		return null;
	}
	
	private void sendArticle(CloudWeixinShell shell, CloudWxShellArticle article) {
		
	}
	
	
	private class ArticleSendTask implements Callable<Boolean> {
		
		private CloudWxShellUser user;
		
		public ArticleSendTask(CloudWxShellUser user) {
			this.user = user;
		}
		
		@Override
		public Boolean call() throws Exception {
			return null;
		}
	}

	public static void main(String[] args) throws SQLException {
		CloudWeixinShell shell = CloudWeixinShellDao.getShellById(5);
		String token = getAccessToken(shell);
		
		//oo77Fw9r2hgd6mz36wrm3sLbpFT4
		//List list = getAllCS(token);
		Map map = ImmutableMap.of("touser", "oo77Fw9r2hgd6mz36wrm3sLbpFT4", "msgtype", "text", "text", ImmutableMap.of("content", "hello"));
		String content = JsonUtil.convertObjToStr(map);
		
		String response = HttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+token, content);
		Map<String,String> m = (Map<String, String>)JsonUtil.convertStringToObject(response);
		System.out.println(m.get("errcode"));
		
		System.out.println(response);
	}
	
}

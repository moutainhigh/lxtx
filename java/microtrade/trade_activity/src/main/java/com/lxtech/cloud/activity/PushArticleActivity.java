package com.lxtech.cloud.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.lxtech.cloud.cache.GlobalCacheUtil;
import com.lxtech.cloud.db.CloudSystemConfitHandler;
import com.lxtech.cloud.db.CloudUserHandler;
import com.lxtech.cloud.db.CloudWxShellArticleHandler;
import com.lxtech.cloud.db.CloudWxShellArticlePushHandler;
import com.lxtech.cloud.db.CloudWxShellHandler;
import com.lxtech.cloud.db.model.CloudUser;
import com.lxtech.cloud.db.model.CloudWxShell;
import com.lxtech.cloud.db.model.CloudWxShellArticle;
import com.lxtech.cloud.db.model.CloudWxShellArticlePush;
import com.lxtech.cloud.db.model.News;
import com.lxtech.cloud.db.model.PushArticle;
import com.lxtech.cloud.util.JsonUtil;
import com.lxtech.cloud.util.http.HttpRequest;

public class PushArticleActivity extends AbstractActivity implements Callable<Boolean>{

	private final static Logger logger = LoggerFactory.getLogger(PushArticleActivity.class);
	
	private static ExecutorService executor;
	
	static {
		executor = Executors.newFixedThreadPool(10);
	}
	
	public static void shutdownPool() {
		PushArticleActivity.executor.shutdown();
	}
	
	public PushArticleActivity(String user_id) {
		super(user_id);
	}

	@Override
	public void handleActivity() {
		executor.submit(this);
	}

	private static String getAccessToken(CloudWxShell shell) {
		String appKey = shell.getApp_id() + "_" + shell.getApp_secret();
		return GlobalCacheUtil.getATCache().get(appKey);
	}

	private int pushArticle(CloudWxShell shell, CloudWxShellArticle article) {
		try {
			String token = getAccessToken(shell);
			if (token != null) {
				// 组装发送消息并发送
				String content = createJsonContent(article);
				logger.info("push article json:" + content);
				String response = HttpRequest.sendPost(
						"https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token, content);
				Map<String, Object> map = (Map<String, Object>) JsonUtil.convertStringToObject(response);
				if (map.get("errmsg").equals("ok")) {
					return 0;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	// 创建发送的数据
	private String createJsonContent(CloudWxShellArticle article) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", this.user_id);
		map.put("msgtype", "news");
		PushArticle art = new PushArticle();
		art.setTitle(article.getTitle());
		art.setDescription(article.getDescription());
		art.setPicurl(article.getImage());
		art.setUrl(article.getArticle());
		List<PushArticle> arts = new ArrayList<>();
		arts.add(art);
		News news = new News();
		news.setArticles(arts);
		map.put("news", news);
		return JsonUtil.convertObjToStr(map);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) {
			new PushArticleActivity("oo77Fw9r2hgd6mz36wrm3sLbpFT4").handleActivity();
		}
		PushArticleActivity.shutdownPool();
	}

	@Override
	public Boolean call() throws Exception {
		long time1 = System.currentTimeMillis();
		// firstly, query the user information
		CloudUser user = null;
		try {
			user = CloudUserHandler.getCloudShellUser(this.user_id);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if (user != null) {
			try {
				// 查询systemConfig配置
				String property = CloudSystemConfitHandler.selectByProperty("article.push.channels").getValue();
				if (!Strings.isNullOrEmpty(property) && property.contains(user.getChnno())) {
					// 检查用户当前index与最大index值 小于则发
					CloudWxShellArticlePush push = CloudWxShellArticlePushHandler.selectByWxid(this.user_id);
					// 获取appid,根据appid生成token
					CloudWxShell shell = CloudWxShellHandler.getShellByChnno(user.getChnno());
					if (push == null) {
						// 查询当前用户文章index，如果为空，则index=1
						CloudWxShellArticle article = CloudWxShellArticleHandler.selectById(1);
						if (article != null) {
							// send article
							int i = pushArticle(shell, article);
							if (i == 0) {
								push = new CloudWxShellArticlePush();
								push.setChnno(user.getChnno());
								push.setCurrent_index(1);
								push.setLast_send_time((int) System.currentTimeMillis() / 1000);
								push.setWxid(this.user_id);
								CloudWxShellArticlePushHandler.insert(push);
							}
						}
					} else {
						// 发送index+1文章
						int currentIndex = push.getCurrent_index();
						if (currentIndex < CloudWxShellArticleHandler.getCount()) {
							CloudWxShellArticle article = CloudWxShellArticleHandler.selectById(currentIndex + 1);
							if (article != null) {
								// send article
								int i = pushArticle(shell, article);
								if (i == 0) {
									push.setCurrent_index(currentIndex + 1);
									push.setLast_send_time((int) System.currentTimeMillis() / 1000);
									CloudWxShellArticlePushHandler.updateById(push);
								}
							}
						}
					}

				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		long cost = System.currentTimeMillis() - time1;
		logger.info("cost time:" + cost+" milli seconds");
		return true;
	}

}

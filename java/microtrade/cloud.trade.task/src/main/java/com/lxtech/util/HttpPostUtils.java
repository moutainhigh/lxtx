package com.lxtech.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.Consts;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

/**
 * This class is used to provide the post method for all channels
 */
public class HttpPostUtils {
	// 果果
	public static HttpMethod getGuoguo324() {
		PostMethod post = new PostMethod("http://u.324.com/admember/index.html");
		NameValuePair[] nameValuePair = new NameValuePair[2];
		nameValuePair[0] = new NameValuePair("loginuser", "16035967");
		nameValuePair[1] = new NameValuePair("loginpass", "123456");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 酷手
	public static HttpMethod getKuShou() {
		PostMethod post = new PostMethod("http://www.kushou.com/Home/Admember");
		NameValuePair[] nameValuePair = new NameValuePair[2];
		nameValuePair[0] = new NameValuePair("loginuser", "16035967");
		nameValuePair[1] = new NameValuePair("loginpass", "123456");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 10010联盟
	public static HttpMethod getLianMeng10010() {
		PostMethod post = new PostMethod("http://www.10010lm.com/i.php?action=postuserlogin");
		NameValuePair[] nameValuePair = new NameValuePair[4];
		nameValuePair[0] = new NameValuePair("username", "3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		nameValuePair[2] = new NameValuePair("image3.x", "28");
		nameValuePair[3] = new NameValuePair("image3.y", "20");
		post.setRequestBody(nameValuePair);
		return post;
	}

	public static HttpPost getXuanChuanYi(String validCode) {
		HttpPost post = new HttpPost("http://www.17un.com/auth/login/");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", "3155617748@qq.com"));
		nvps.add(new BasicNameValuePair("password", "123456"));
		nvps.add(new BasicNameValuePair("ajax_do", "1"));
		nvps.add(new BasicNameValuePair("auth_code", validCode));
		nvps.add(new BasicNameValuePair("success_uri", ""));

		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		post.addHeader("Host", "www.17un.com");
		post.addHeader("Origin", "http://www.17un.com");
		post.addHeader("Referer", "http://www.17un.com/auth/login");
		post.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
		post.addHeader("X-Requested-With", "XMLHttpRequest");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		post.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		return post;
	}

	// 领地
	public static HttpMethod getLingDi() {
		PostMethod post = new PostMethod("http://www.526d.com/index.php?e=index.postlogin");
		NameValuePair[] nameValuePair = new NameValuePair[4];
		nameValuePair[0] = new NameValuePair("username", "3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		nameValuePair[2] = new NameValuePair("image3.x", "65");
		nameValuePair[3] = new NameValuePair("image3.y", "15");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 悠告
	public static HttpMethod getUGao() {
		PostMethod post = new PostMethod("http://www.uogao.com/index.php?action=postuserlogin");
		NameValuePair[] nameValuePair = new NameValuePair[2];
		nameValuePair[0] = new NameValuePair("username", "3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 点锐
	public static HttpMethod getDRui(String channelname) {
		String username = "";
		String password = "";
		if (channelname.equals("channel.zhangyoudr")) {
			username = "bfq816@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.dianrui")) {
			username = "16035967@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.qimei")) {
			username = "siray";
			password = "16800";
		} else if (channelname.equals("channel.dianrui1")) {
			username = "tianyihan@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.wapandroid")) {
			username = "3155617748@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.dianrui2")) {
			username = "ztbfq@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.zhangyou1")) {
			username = "wxbfq1019@qq.com";
			password = "123456";
		}
		PostMethod post = new PostMethod("http://www.dianrui.com/ajax/ajax.php");
		NameValuePair[] nameValuePair = new NameValuePair[4];
		nameValuePair[0] = new NameValuePair("action", "login");
		nameValuePair[1] = new NameValuePair("username", username);
		nameValuePair[2] = new NameValuePair("password", password);
		nameValuePair[3] = new NameValuePair("remember", "undefined");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 果果1498
	public static HttpMethod getGuoguo1498(String username) {
		PostMethod post = new PostMethod("http://wy.36500.com/login2.aspx?action=login");
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("username", username);
		nameValuePair[1] = new NameValuePair("password", "123456");
		nameValuePair[2] = new NameValuePair("ty", "advuser");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 巨鲨
	public static HttpMethod getJusha() {
		PostMethod post = new PostMethod("http://www.jusha.com/login.php");
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("nocode", "1");
		nameValuePair[1] = new NameValuePair("username", "529971782@qq.com");
		nameValuePair[2] = new NameValuePair("password", "wer123wer123");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 卓航
	public static HttpMethod getZhuohang(String channelname, String vadcode) {

		String username = "";
		String password = "";
		String inputUrl = "";
		if (channelname.equals("channel.zhuohang")) {
			username = "zc3155617748";
			password = "123456";
			inputUrl = "http://www.adttt.com/index.php?e=index.postlogin";
		} else if (channelname.equals("channel.69lianmeng")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.69lianmeng.com/index.php?e=index.postlogin";
		} else if (channelname.equals("channel.541lianmeng")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.w541.com/index.php?e=index.postlogin";
		} else if (channelname.equals("channel.yipin")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.yipinlm.com/index.php?e=index.postlogin";
		} else if (channelname.equals("channel.jumeng")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://m.finead.cn/index.php?e=index.postlogin";
		} else if (channelname.equals("channel.80lianmeng")) {
			username = "qa1234";
			password = "123456";
			inputUrl = "http://www.vx80.com/index.php?e=index.postlogin";
		}
		PostMethod post = new PostMethod(inputUrl);
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("username", username);
		nameValuePair[1] = new NameValuePair("password", password);
		nameValuePair[2] = new NameValuePair("checkcode", vadcode);
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 四维
	public static HttpMethod getSiwei() {
		PostMethod post = new PostMethod("http://www.4wad.com/index.php?e=index.postlogin");
		NameValuePair[] nameValuePair = new NameValuePair[2];
		nameValuePair[0] = new NameValuePair("username", "q3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		post.setRequestBody(nameValuePair);
		return post;
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public static HttpMethod getEightDirection(String userName, String validCode) {
		PostMethod post = null;
		if (userName.equals("lxtx_video") || userName.equals("3155617748")) {
			post = new PostMethod("http://lm.admin5.com/index.postlogin");
		} else {
			post = new PostMethod("http://www.youyouniao.com/index.postlogin");
		}
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("checkcode", validCode);
		nameValuePair[1] = new NameValuePair("username", userName);
		nameValuePair[2] = new NameValuePair("password", getPasswordForEightDirection(userName));
		post.setRequestBody(nameValuePair);
		return post;
	}

	private static String getPasswordForEightDirection(String username) {
		if (username.equals("lxtx_video")) {
			return "wer123wer123";
		} else if (username.equals("3155617748")) {
			return "123456";
		} else if (username.equals("19364476")) {
			return "123456";
		} else if (username.equals("23377708")) {
			return "123456";
		} else {
			return "";
		}
	}

	// 麦收
	public static HttpMethod getMShou(String channelname) {
		String username = "";
		String password = "";
		String inputUrl = "";
		if (channelname.equals("channel.maishou")) {
			username = "Tianyihan";
			password = "123456";
			inputUrl = "http://www.myshou.com/site/login";
		} else if (channelname.equals("channel.5293")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.5293.com/site/login/act/login";
		}

		PostMethod post = new PostMethod(inputUrl);
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("username", username);
		nameValuePair[1] = new NameValuePair("password", password);
		nameValuePair[2] = new NameValuePair("submit", "登录");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 康盛
	public static HttpPost getKangshen(String channelname) {

		String username = "";
		String password = "";
		String inputUrl = "";
		if (channelname.equals("channel.kangsheng")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.tuigoo.cn/Account/Login";
		} else if (channelname.equals("channel.baiqisheng")) {
			username = "wuwu123";
			password = "123456";
			inputUrl = "http://www.diandianmob.com/Account/Login";
		} else if (channelname.equals("channel.88mobile")) {
			username = "3155617748";
			password = "123456";
			inputUrl = "http://www.88mob.com/Account/Login";
		} else if (channelname.equals("channel.dianmeng")) {
			username = "bofang2";
			password = "123456";
			inputUrl = "http://diandianmob.com/Account/Login";
		} else if (channelname.equals("channel.dianmenganzhuo")) {
			username = "fute";
			password = "123456";
			inputUrl = "http://diandianmob.com/Account/Login";
		}

		HttpPost post = new HttpPost(inputUrl);
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("loginName", username));
		nvps.add(new BasicNameValuePair("loginPwd", password));
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	// 麒麟
	public static HttpPost getQLin() {
		return getQLin("%D1%E9%D6%A4%C2%EB");
	}

	// 易诺
	public static HttpMethod getYNuo() {
		PostMethod post = new PostMethod("http://www.youmsm.com/ajax/ajax.php");
		NameValuePair[] data = new NameValuePair[4];
		data[0] = new NameValuePair("action", "login");
		data[1] = new NameValuePair("username", "6155617748@qq.com");
		data[2] = new NameValuePair("password", "666666");
		data[3] = new NameValuePair("rememeber", "");
		post.setRequestBody(data);
		return post;
	}

	// 79
	public static HttpPost get79lianmeng() {
		HttpPost post = new HttpPost("http://www.79mob.com/Account/Login");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("loginName", "tiantian"));
		nvps.add(new BasicNameValuePair("loginPwd", "123456"));
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	// 极限
	public static HttpMethod getJiXian(String channelname) {

		String username = "";
		String password = "";
		String inputUrl = "";
		if (channelname.equals("channel.jixian")) {
			username = "3155617748@qq.com";
			password = "123456";
			inputUrl = "http://www.jisucn.com/login2.aspx?action=login";
		} else if (channelname.equals("channel.qimeijixian")) {
			username = "1148292200@qq.com";
			password = "123123";
			inputUrl = "http://www.jisucn.com/login2.aspx?action=login";
		} 
		PostMethod post = new PostMethod(inputUrl);
		
		NameValuePair[] pair = new NameValuePair[3];
		pair[0] = new NameValuePair("ty", "advuser");
		pair[1] = new NameValuePair("username", username);
		pair[2] = new NameValuePair("password", password);
		post.setRequestBody(pair);
		return post;
	}
	
	public static String[] quanjingPassword(String channelname){
		String username = null;
		String password = null;
		 if (channelname.equals("channel.quanjing")) {//极限全景27
			username = "16035967@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.quanjingsi")) {//29
			username = "2094088410@qq.com";
			password = "123456";
		} else if (channelname.equals("channel.quanjinggong")) {//46
			username = "3155617748@qq.com";
			password = "123456";
		}
		return new String[] {username,password};
		
	}

	// fenfa
	public static HttpMethod getFenfa() {
		PostMethod post = new PostMethod("http://www.ffwap.com/index.php");
		NameValuePair[] pair = new NameValuePair[3];
		pair[0] = new NameValuePair("LoginForm[username]", "yihan888");
		pair[1] = new NameValuePair("LoginForm[password]", "123456");
		pair[2] = new NameValuePair("yt0", "");
		post.setRequestBody(pair);
		return post;
	}

	// 海盟
	public static HttpMethod getHaimeng(String code) {
		PostMethod post = new PostMethod("http://www.3jhm.com/index.php?g=user&m=login&a=dologin");
		NameValuePair[] pair = new NameValuePair[3];
		pair[0] = new NameValuePair("username", "3155617748@qq.com");
		pair[1] = new NameValuePair("password", "123456");
		pair[2] = new NameValuePair("verify", code);
		post.setRequestBody(pair);
		return post;
	}

	// 骄创
	public static HttpMethod getJiaochuang(String code) {
		PostMethod post = new PostMethod("http://www.lpo2.com/index.php?e=index.postlogin");
		NameValuePair[] pair = new NameValuePair[3];
		pair[0] = new NameValuePair("username", "3155617748");
		pair[1] = new NameValuePair("password", "123456");
		pair[2] = new NameValuePair("checkcode", code);
		post.setRequestBody(pair);
		return post;
	}

	// 麒美极限
	public static HttpMethod getQMeiJiXian(String valid) {
		PostMethod post = new PostMethod("http://www.jisucn.com/login2.aspx?action=login");
		NameValuePair[] pair = new NameValuePair[4];
		pair[0] = new NameValuePair("ty", "advuser");
		pair[1] = new NameValuePair("username", "3155617748@qq.com");
		pair[2] = new NameValuePair("password", "123456");
		pair[3] = new NameValuePair("yzm", valid);
		post.setRequestBody(pair);
		return post;
	}

	// 极限全景
	public static HttpMethod getQuanJing(String valid) {
		PostMethod post = new PostMethod("http://www.jisucn.com/login2.aspx?action=login");
		NameValuePair[] pair = new NameValuePair[4];
		pair[0] = new NameValuePair("ty", "advuser");
		pair[1] = new NameValuePair("username", "3155617748@qq.com");
		pair[2] = new NameValuePair("password", "123456");
		pair[3] = new NameValuePair("yzm", valid);
		post.setRequestBody(pair);
		return post;
	}

	// 爱联盟
	public static HttpMethod getAiLianMeng() {
		PostMethod post = new PostMethod("http://www.iiad.com/site/login/act/login");
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("username", "3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		nameValuePair[2] = new NameValuePair("submit", "登录");
		post.setRequestBody(nameValuePair);
		return post;
	}

	// 易爱
	public static HttpPost getYiLove(String code) {
		HttpPost post = new HttpPost("http://www.eamob.com/ajax/Ajax_User.ashx");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("email", "3155617748@qq.com"));
		nvps.add(new BasicNameValuePair("password", "123456"));
		nvps.add(new BasicNameValuePair("action", "Login"));
		nvps.add(new BasicNameValuePair("code", code));

		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	public static HttpPost getQLin(String validCode) {
		HttpPost post = new HttpPost("http://www.e7003.com/login.asp?ref=http%3A%2F%2Fwww%2E70e%2Ecom%2Findex%2Ehtml");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("s_password", "s123456"));
		nvps.add(new BasicNameValuePair("s_name", "qq3155617748"));
		nvps.add(new BasicNameValuePair("verifycode", validCode));
		nvps.add(new BasicNameValuePair("button", "%B5%C7%C2%BD"));
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		post.addHeader("Referer", "http://www.e7003.com/login_box.asp");
		post.addHeader("Origin", "http://www.e7003.com");
		post.addHeader("Upgrade-Insecure-Requests", "1");
		post.addHeader("Host", "www.e7003.com");
		post.addHeader("Content-Type", "application/x-www-form-urlencoded");
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		post.addHeader("Accept-Encoding", "gzip, deflate");
		post.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
		post.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		return post;

	}

	// 1498极星
	public static HttpPost getJiStar(String code) {
		HttpPost post = new HttpPost("http://www.jixing8.com/login.aspx");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("username", "tianyihan"));
		nvps.add(new BasicNameValuePair("password", "123456"));
		nvps.add(new BasicNameValuePair("usertype", "ADMaster"));
		nvps.add(new BasicNameValuePair("yzm", code));

		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	/**
	 * 海云平台
	 * 
	 * @param hash
	 * @return
	 */
	public static HttpPost getYunHai(String hash) {
		HttpPost post = new HttpPost("http://atd.haiyunx.com/login/checkUserLogin");
		List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("userName", "3155617748@qq.com"));
		nvps.add(new BasicNameValuePair("passWord", "1234567"));
		nvps.add(new BasicNameValuePair("__hash__", hash));
		post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
		return post;
	}

	/**
	 * 百游
	 * 
	 * @param code
	 * @return
	 */
	public static HttpMethod getBaigm(String code) {
		PostMethod post = new PostMethod("http://www.baigm.com/?action=postuserlogin");
		NameValuePair[] nameValuePair = new NameValuePair[5];
		nameValuePair[0] = new NameValuePair("username", "q3155617748");
		nameValuePair[1] = new NameValuePair("password", "123456");
		nameValuePair[2] = new NameValuePair("checkcode", code);
		nameValuePair[3] = new NameValuePair("image.x", "0");
		nameValuePair[4] = new NameValuePair("image.y", "0");
		post.setRequestBody(nameValuePair);
		return post;
	}

	public static HttpMethod get18LianMeng() {
		PostMethod post = new PostMethod("http://www.18lm.com/Index/login");
		NameValuePair[] nameValuePair = new NameValuePair[3];
		nameValuePair[0] = new NameValuePair("username","3155617748");
		nameValuePair[1] = new NameValuePair("password","123456");
		nameValuePair[2] = new NameValuePair("type","advertiser");
		post.setRequestBody(nameValuePair);
		return post;
	}

	/**
	 * 卓越移动
	 * @param channelName
	 * @return
	 */
	public static HttpMethod getZhuoYueMobile(String channelName) {
		PostMethod post = new PostMethod("http://2945.com/index.php");
		NameValuePair[] nameValuePair = new NameValuePair[2];
		
		switch(channelName){
		case "channel.zhuoyuemobile":
			nameValuePair[0] = new NameValuePair("account","3155617748@qq.com");
			nameValuePair[1] = new NameValuePair("pwd","123456");
		}
		post.setRequestBody(nameValuePair);
		return post;
	}
}

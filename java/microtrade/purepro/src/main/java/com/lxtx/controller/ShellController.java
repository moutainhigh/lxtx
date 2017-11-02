package com.lxtx.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lxtx.dao.CloudSystemConfigMapper;
import com.lxtx.dao.CloudWxShellArticleMapper;
import com.lxtx.dao.CloudWxShellArticlePushMapper;
import com.lxtx.model.CloudWxShell;
import com.lxtx.model.CloudWxShellArticle;
import com.lxtx.model.CloudWxShellArticlePush;
import com.lxtx.model.CloudWxShellUser;
import com.lxtx.service.user.ShellUserService;
import com.lxtx.util.net.KestrelConnector;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tool.EncryptUtil;
import com.lxtx.util.tool.InputMessage;
import com.lxtx.util.tool.JsonUtil;
import com.lxtx.util.tool.SerializeXmlUtil;
import com.thoughtworks.xstream.XStream;

@Controller
@RequestMapping(value = "/shell")
public class ShellController {
	
	@Autowired
	private ShellUserService shellUserService;
	@Autowired
	private CloudSystemConfigMapper cloudSystemConfigMapper;
	@Autowired
	private CloudWxShellArticleMapper cloudWxShellArticleMapper;
	@Autowired
	private CloudWxShellArticlePushMapper cloudWxShellArticlePushMapper;

	private static final Logger logger = LoggerFactory.getLogger(ShellController.class);
	
	@RequestMapping(value = "/checkarticle")
	@ResponseBody
	public String checkarticle(HttpServletRequest request, HttpServletResponse response) {
		CloudWxShellArticle article = this.cloudWxShellArticleMapper.selectById(1);
		logger.info("article title is: " + article.getTitle());
		logger.info("article count: " + this.cloudWxShellArticleMapper.getCount());
		CloudWxShellArticlePush articlePush = new CloudWxShellArticlePush();
		articlePush.setChnno("1115");
		String wxid = "oo77Fw885ugJSatgXdaiuyHbntTY";
		articlePush.setCurrent_index(1);
		articlePush.setLast_send_time(System.currentTimeMillis()/1000);
		articlePush.setWxid(wxid);
		this.cloudWxShellArticlePushMapper.insert(articlePush);
		CloudWxShellArticlePush push  =  this.cloudWxShellArticlePushMapper.selectByWxid(wxid);
		logger.info("push is:" + push.getChnno() + push.getCurrent_index() + push.getId());
		articlePush.setCurrent_index(2);
		this.cloudWxShellArticlePushMapper.updateByWxid(articlePush);
		push  =  this.cloudWxShellArticlePushMapper.selectByWxid(wxid);
		logger.info("push is:" + push.getChnno() + push.getCurrent_index() + push.getId());		
		
		return "123";
	}
	
	@RequestMapping(value = "/verifyserver/{chnno}")
	@ResponseBody
	public String verifyServer(@PathVariable String chnno, HttpServletRequest request, HttpServletResponse response) {
		boolean isGet = request.getMethod().toLowerCase().equals("get");
		logger.info("isGet:" + isGet);
		if (isGet) {
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");

			logger.info("signature is:" + signature);
			logger.info("timestamp is:" + timestamp);
			logger.info("nonce is:" + nonce);
			logger.info("echostr is:" + echostr);

			WXPayConfig WebConfig = WXPayConfig.getInstance();
			String token = WebConfig.get("wx.entry_token");
			logger.info("token is: " + token);

			String[] strArr = { token, timestamp, nonce };
			Arrays.sort(strArr);
			StringBuilder sb = new StringBuilder();
			for (String str : strArr) {
				sb.append(str);
			}

			String digest = EncryptUtil.SHA1(sb.toString());
			logger.info("digest is: " + digest);

			 if (digest.equals(signature)) {
			 return echostr;
			 }
		} else {
			try {
				acceptMessage(chnno, request, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		 return "";
	}
	
	private void acceptMessage(String chnno, HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("in acceptMessage, chnno is:" + chnno);
		
		// 处理接收消息
		ServletInputStream in = request.getInputStream();
		// 将POST流转换为XStream对象
		XStream xs = SerializeXmlUtil.createXstream();
		xs.processAnnotations(InputMessage.class);
		// 将指定节点下的xml节点数据映射为对象
		xs.alias("xml", InputMessage.class);
		// 将流转换为字符串
		StringBuilder xmlMsg = new StringBuilder();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			xmlMsg.append(new String(b, 0, n, "UTF-8"));
		}
		// 将xml内容转换为InputMessage对象
		String msg = xmlMsg.toString();
		logger.info("received weixin message:" + msg);
		InputMessage inputMsg = (InputMessage) xs.fromXML(msg);

		// 取得消息类型
		String msgType = inputMsg.getMsgType();
		// 发送消息
		String servername = inputMsg.getToUserName();// 服务端
		String custermname = inputMsg.getFromUserName();// 客户端
		Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间
		// 如果是关注事件 则添加用户入库
		String wxid = inputMsg.getFromUserName();
		CloudWxShellUser shellUser = shellUserService.getUserByWxid(wxid);
		logger.info(inputMsg.toString());
		if (msgType.equals("event") && inputMsg.getEvent().equals("subscribe")) {
			// 检查用户是否存在，不存在则创建，存在，则不管
			String eventKey = inputMsg.getEventKey();
			if (eventKey.contains("qrscene_")) {
				// 未关注
				chnno = eventKey.substring(eventKey.indexOf("_") + 1, eventKey.length());
				logger.info("============subscribe from scene:{}========", chnno);
			} else {
				logger.info("********subscribe from no scene!*******", eventKey);
			}
			
			if (shellUser == null) {
				shellUser = new CloudWxShellUser();
				shellUser.setChnno(chnno);
				shellUser.setStatus(1);
				shellUser.setWxid(wxid);
				shellUserService.saveUser(shellUser);
			} else if (shellUser.getStatus() == 0) {
				shellUser.setStatus(1);
				shellUserService.updateUser(shellUser);
			}
				
			request.setCharacterEncoding("UTF-8");
			response.reset();
			response.setCharacterEncoding("UTF-8");

			CloudWxShell shell = shellUserService.getShellByChnno(chnno);
			if (shell != null) {
				StringBuffer str = new StringBuffer();
				str.append("<xml>");
				str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
				str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
				str.append("<CreateTime>" + returnTime + "</CreateTime>");
				str.append("<MsgType><![CDATA[text]]></MsgType>");
				str.append("<Content><![CDATA[");
				str.append(shell.getTitle());
				int channel = Integer.valueOf(chnno).intValue();
				String promoteMsg = "：通过预测产品价格趋势，以判断价格涨跌获利的金融服务平台。免费开户， 10元即可购买，回报率最高达100%！微信支付、银行托管、随时提现，安全便捷！\n点击左下方【开始赚钱】注册开户，开启您的微云之旅。\n 服务时间: 周一至周日，上午9:00至次日凌晨04:00 \n\n为感谢粉丝们支持，本公众号现推出「人人有红包」活动，首次关注即可免费获得5张折扣券(最高可抵1500元)，分5个工作日领取，同时每周可用一张面值5元的现金券，让您可以在零成本的情况下玩转全球领先的微云服务平台。\n赢了你提现拿走，输了我们买单！！]]></Content>";
				if (channel >= 1101 && channel <= 1110) {
					promoteMsg = "：通过预测产品价格趋势，以判断价格涨跌获利的金融服务平台。免费开户， 10元即可购买，回报率最高达100%！微信支付、银行托管、随时提现，安全便捷！\n点击左下方【开始赚钱】注册开户，开启您的微云之旅。\n服务时间: 周一至周日，上午9:00至次日凌晨04:00 \n\n为感谢粉丝们支持，本公众号现推出「人人有红包」活动，首次关注即可免费获得5张折扣券(最高可抵1500元)，分5个工作日领取，同时每周可用一张面值5元的现金券，让您可以在零成本的情况下玩转全球领先的微云服务平台。\n赢了你提现拿走，输了我们买单！！]]></Content>";
				}
				str.append(promoteMsg);
				str.append("</xml>");
				logger.info(str.toString());
				PrintWriter writer = response.getWriter();
				writer.write(str.toString());
				writer.flush();
				
				String property = this.cloudSystemConfigMapper.selectByProperty("article.push.channels").getValue();
				if (!Strings.isNullOrEmpty(property) && property.contains(chnno)) {
					logger.info("in shell controller, now push the article.");
					this.pushArticle(custermname, servername, returnTime.intValue(), chnno, response);
				}				
			}
		}
		// 用户取消关注
		if (msgType.equals("event") && inputMsg.getEvent().equals("unsubscribe")) {
			logger.info("send user openid：" + wxid);
			if (shellUser != null) {
				// 修改用户的关注状态
				shellUser.setStatus(0);
				shellUserService.updateUser(shellUser);				
			}
		}
		
		if (msgType.equals("text")) {
			String content = inputMsg.getContent();
			if (content.equals("查看") || content.equals("1")){
				//str.append("<Content><![CDATA[");
				int channel = Integer.valueOf(chnno).intValue();
				String articleUrl = "http://yun.chnhq.com/yun/common2_1112.html";
				String title = "微盘能赚钱吗？揭秘微盘高手最高境界！";
				if (chnno.equals("1113") || chnno.equals("1115") || chnno.equals("1116") || chnno.equals("1117")) {
					articleUrl = "http://yun.chnhq.com/yun/common2_1113.html";
					title = "如何能在30岁前轻松赚到100万，看了这个包你梦想成真！";
				}
				String picurl = "http://yun.chnhq.com/yun/advert/bmw_files/641.png";
				this.pushArticle(custermname, servername, returnTime.intValue(), title, articleUrl, picurl, response);
			}
		}
		
		if (msgType.equals("event") && inputMsg.getEvent().equals("VIEW") && inputMsg.getEventKey().contains("yun/wx/redirect")) {
			String property = this.cloudSystemConfigMapper.selectByProperty("article.push.channels").getValue();
			if (!Strings.isNullOrEmpty(property) && property.contains(chnno)) {
				logger.info("in shell controller, now push the article.");
				//this.pushArticle(custermname, servername, returnTime.intValue(), chnno, response);
				String articleUrl = "http://yun.chnhq.com/yun/common2_1112.html";
				String picurl = "http://yun.chnhq.com/yun/advert/bmw_files/641.png";
				String title = "微盘能赚钱吗？揭秘微盘高手最高境界！";
				this.pushArticle(custermname, servername, returnTime.intValue(), title, articleUrl, picurl, response);
			}
		}
		if(msgType.equals("event") && ((inputMsg.getEvent().equals("VIEW") && inputMsg.getEventKey().contains("yun/wx/redirect")) || inputMsg.getEvent().equals("subscribe"))){
			//如果是点击事件或关注事件，写入队列，发送图文消息
			Map<String, String> map = ImmutableMap.of("user_id", inputMsg.getFromUserName(), "msg_type", "push_article");
			String message = JsonUtil.convertObjToStr(map);
			KestrelConnector.enqueue("cloud_trade_activity", message);
			logger.info("in ShellController, add pushArticle into queue:"+message);
		}
	}
	
	private void pushArticle(String toUserName, String fromUserName, int time, String chnno, HttpServletResponse response) throws IOException {
		CloudWxShellArticlePush  push = this.cloudWxShellArticlePushMapper.selectByWxid(toUserName);
		if (push == null) {
			//send article
			CloudWxShellArticle article = this.cloudWxShellArticleMapper.selectById(1);
			if (article != null) {
				this.pushArticle(toUserName, fromUserName, time, article.getTitle(), article.getArticle(), article.getImage(), response);
				push = new CloudWxShellArticlePush();
				push.setChnno(chnno);
				push.setCurrent_index(1);
				push.setLast_send_time((int)System.currentTimeMillis()/1000);
				push.setWxid(toUserName);
				this.cloudWxShellArticlePushMapper.insert(push);				
			} 
		} else {
			int currentIndex = push.getCurrent_index();
			if (currentIndex < this.cloudWxShellArticleMapper.getCount()) {
				CloudWxShellArticle article = this.cloudWxShellArticleMapper.selectById(currentIndex + 1);
				if (article != null) {
					this.pushArticle(toUserName, fromUserName, time, article.getTitle(), article.getArticle(), article.getImage(), response);
					push.setCurrent_index(currentIndex + 1);
					this.cloudWxShellArticlePushMapper.updateByWxid(push);					
				}
			}
		}
	}
	
	private void pushArticle(String toUserName, String fromUserName, int time, String title, String articleUrl, String picurl, HttpServletResponse response) throws IOException {
		StringBuffer str = new StringBuffer();
		str.append("<xml>");
		str.append("<ToUserName><![CDATA[" + toUserName + "]]></ToUserName>");
		str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
		str.append("<CreateTime>" + time + "</CreateTime>");
		str.append("<MsgType><![CDATA[news]]></MsgType>");
		str.append("<ArticleCount>1</ArticleCount>");	
		str.append("<Articles><item>");
		str.append("<Title><![CDATA[" + title + "]]></Title>");
		str.append("<Url><![CDATA[" + articleUrl + "]]></Url>");
		picurl = picurl + "?time="+System.currentTimeMillis();
		str.append("<PicUrl><![CDATA[" +  picurl +  "]]></PicUrl>");				
		str.append("</item></Articles>");
		str.append("</xml>");
		logger.info(str.toString());
		OutputStream out = response.getOutputStream();
		//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
		out.write(str.toString().getBytes("UTF-8"));
		out.flush();		
	}
	
}

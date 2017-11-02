package com.lxtx.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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

import com.lxtx.model.CloudWxShell;
import com.lxtx.model.CloudWxShellUser;
import com.lxtx.model.SendFlowInfo;
import com.lxtx.service.user.ShellUserService;
import com.lxtx.util.StringUtil;
import com.lxtx.util.db.SendFlowInfoHandler;
import com.lxtx.util.tencent.WXPayConfig;
import com.lxtx.util.tool.EncryptUtil;
import com.lxtx.util.tool.InputMessage;
import com.lxtx.util.tool.SerializeXmlUtil;
import com.thoughtworks.xstream.XStream;

@Controller
@RequestMapping(value = "/shell")
public class ShellController {

	@Autowired
	private ShellUserService shellUserService;

	private static final Logger logger = LoggerFactory.getLogger(ShellController.class);

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
		// 如果是关注事件 则添加用户入库
		String wxid = inputMsg.getFromUserName();
		
		// 发送消息
		String servername = inputMsg.getToUserName();// 服务端
		String custermname = inputMsg.getFromUserName();// 客户端
		Long returnTime = Calendar.getInstance().getTimeInMillis() / 1000;// 返回时间		
		CloudWxShell shell = shellUserService.getShellByChnno(chnno);
		
		CloudWxShellUser shellUser = shellUserService.getUserByWxid(wxid);
		logger.info(inputMsg.toString());
		logger.info("============subscribe from scene:{}========", chnno);
		if(chnno.equals("9997")){
			logger.info("msgType:"+msgType+"  "+inputMsg.getEvent());
			if (msgType.equals("event") && inputMsg.getEvent().equals("subscribe")) {
				StringBuffer str = new StringBuffer();
				str.append("<xml>");
				str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
				str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
				str.append("<CreateTime>" + returnTime + "</CreateTime>");
				str.append("<MsgType><![CDATA[text]]></MsgType>");
				str.append("<Content><![CDATA[");
				str.append(shell.getTitle());
				String promoteMsg = "：2017年1月1日至2017年12月31日各位达人们，你们好！欢迎来到微云九州，感谢您的关注！只要您先回复“手机号码”、再回复“免费流量”就可以获得免费流量！48小时内到账，当月有效。每个号码限领一次哦！我们在这里祝您万事如意！]]></Content>";
				str.append(promoteMsg);
				str.append("</xml>");
				logger.info(str.toString());
				OutputStream out = response.getOutputStream();
				//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
				out.write(str.toString().getBytes("UTF-8"));
				out.flush();
			}
			if (msgType.equals("event") && inputMsg.getEvent().equals("CLICK")) {
				StringBuffer str = new StringBuffer();
				str.append("<xml>");
				str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
				str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
				str.append("<CreateTime>" + returnTime + "</CreateTime>");
				str.append("<MsgType><![CDATA[text]]></MsgType>");
				str.append("<Content><![CDATA[");
				str.append(shell.getTitle());
				String promoteMsg = "：各位达人们，你们好！欢迎来到微云九州，感谢您的关注！2017年1月1日至2017年12月31日，只要您先回复“手机号码”、再回复“免费流量”就可以获得10M流量！48小时内到账，当月有效。每个号码限领一次哦！我们在这里祝您工作顺利，万事如意！]]></Content>";
				str.append(promoteMsg);
				str.append("</xml>");
				logger.info(str.toString());
				OutputStream out = response.getOutputStream();
				//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
				out.write(str.toString().getBytes("UTF-8"));
				out.flush();
			}
			if (msgType.equals("text")) {
				String content = inputMsg.getContent();
				//是正规的手机号，入库
				if(StringUtil.checkLegalMobile(content)){
					logger.info("is legal mobile number,insert...");
					try {
						SendFlowInfo sendFlowInfo = new SendFlowInfo();
						sendFlowInfo.setMobile(content);
						sendFlowInfo.setTime(new Date());
						sendFlowInfo.setStatus(0);
						SendFlowInfoHandler.saveSendFlowInfo(sendFlowInfo);
					} catch (Exception e) {
						StringBuffer str = new StringBuffer();
						str.append("<xml>");
						str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
						str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
						str.append("<CreateTime>" + returnTime + "</CreateTime>");
						str.append("<MsgType><![CDATA[text]]></MsgType>");
						str.append("<Content><![CDATA[");
						str.append(shell.getTitle());
						String promoteMsg = "：您已参加过免费流量活动，同一号码活动期内限参与一次哦]]></Content>";
						str.append(promoteMsg);
						str.append("</xml>");
						logger.info(str.toString());
						OutputStream out = response.getOutputStream();
						//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
						out.write(str.toString().getBytes("UTF-8"));
						out.flush();
					}
				}
				if (content.contains("免费流量")) {
					StringBuffer str = new StringBuffer();
					str.append("<xml>");
					str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
					str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
					str.append("<CreateTime>" + returnTime + "</CreateTime>");
					str.append("<MsgType><![CDATA[text]]></MsgType>");
					str.append("<Content><![CDATA[");
					str.append(shell.getTitle());
					String promoteMsg = "：恭喜您成功参加【微云九州】关注即送10M流量活动，您的留言我们已收到，10M免费流量正向您飞奔而来，请耐心等待，最晚48小时到账，同一号码仅限参与一次哦！]]></Content>";
					str.append(promoteMsg);
					str.append("</xml>");
					logger.info(str.toString());
					OutputStream out = response.getOutputStream();
					//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
					out.write(str.toString().getBytes("UTF-8"));
					out.flush();
				}
			}
		}else{
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
				response.setHeader("Content-type", "text/html;charset=UTF-8"); 

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
					String promoteMsg = "：乐玩九州是九州系列的又一个新产品，是您口袋中最理想的娱乐方式，无需注册，紧张刺激，快来参与吧\n\n• 左下角【开始游戏】点击后，立刻进入九州红包！\n• 右下角服务中心 【寄售宝】已火热上线!  您可在【寄售宝】中97折高价转让充值卡\n• 如有任何疑问，请点击【服务中心】→【帮助文档】。\n【开始游戏前，请先阅读游戏规则，以获得更好地体验】]]></Content>";
					str.append(promoteMsg);
					str.append("</xml>");
					logger.info(str.toString());
					PrintWriter writer = response.getWriter();
					writer.write(str.toString());
					writer.flush();				
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
				if (content.equals("查看")){
					StringBuffer str = new StringBuffer();
					str.append("<xml>");
					str.append("<ToUserName><![CDATA[" + custermname + "]]></ToUserName>");
					str.append("<FromUserName><![CDATA[" + servername + "]]></FromUserName>");
					str.append("<CreateTime>" + returnTime + "</CreateTime>");
					str.append("<MsgType><![CDATA[news]]></MsgType>");
					str.append("<ArticleCount>1</ArticleCount>");
					//str.append("<Content><![CDATA[");
					int channel = Integer.valueOf(chnno).intValue();
					/*String promoteMsg = "：通过预测产品价格趋势，以判断价格涨跌获利的金融服务平台。免费开户， 10元即可购买，回报率最高达100%！微信支付、银行托管、随时提现，安全便捷！\n点击左下方【开始赚钱】注册开户，开启您的微云之旅。\n您也可添加微信号	jzwy_kefu \n来获得帮助服务(自动通过好友请求)。\n服务时间: 周一至周五，上午9:00至次日凌晨04:00 \n\n为感谢粉丝们支持，本公众号现推出「人人有红包」活动，首次关注即可免费获得5张折扣券(最高可抵1500元)，分5个工作日领取，同时每周可用一张面值5元的现金券，让您可以在零成本的情况下玩转全球领先的微云服务平台。\n赢了你提现拿走，输了我们买单！！]]></Content>";
					if (channel >= 1101 && channel <= 1110) {
						promoteMsg = "：通过预测产品价格趋势，以判断价格涨跌获利的金融服务平台。免费开户， 10元即可购买，回报率最高达100%！微信支付、银行托管、随时提现，安全便捷！\n点击左下方【开始赚钱】注册开户，开启您的微云之旅。\n服务时间: 周一至周五，上午9:00至次日凌晨04:00 \n\n为感谢粉丝们支持，本公众号现推出「人人有红包」活动，首次关注即可免费获得5张折扣券(最高可抵1500元)，分5个工作日领取，同时每周可用一张面值5元的现金券，让您可以在零成本的情况下玩转全球领先的微云服务平台。\n赢了你提现拿走，输了我们买单！！]]></Content>";
					}*/
					//]]></Content>
					//str.append("<a href=\"http://wx.wanhua365.com.cn/s/173282?from=groupmessage&isappinstalled=0\">大学生一年内买了宝马，究竟是怎么做到的呢？</a>");
					//str.append("]]></Content>");
					str.append("<Articles><item>");
					str.append("<Title><![CDATA[" + "大学生一年内买了宝马，究竟是怎么做到的呢？" + "]]></Title>");
					//str.append("<Description><![CDATA[" + "大学生一年内买了宝马，究竟是怎么做到的呢？" + "]]></Description>");
					String articleUrl = "http://yun.chnhq.com/yun/common2_1112.html";
					if (chnno.equals("1113")) {
						articleUrl = "http://yun.chnhq.com/yun/common2_1113.html";
					}
					
					str.append("<Url><![CDATA[" + articleUrl + "]]></Url>");
					String picurl = "http://file.renrdai.cn/201612_22_222_585bc08b769eb.png";
					str.append("<PicUrl><![CDATA[" +  picurl +  "]]></PicUrl>");				
					str.append("</item></Articles>");
					str.append("</xml>");
					logger.info(str.toString());
	/*				PrintWriter writer = response.getWriter();
					writer.write(str.toString());
					writer.flush();*/
					OutputStream out = response.getOutputStream();
					//前后格式要统一,getBytes()不指定编码格式时使用平台默认编码格式
					out.write(str.toString().getBytes("UTF-8"));
					out.flush();
				}
			}
		}
	}

}

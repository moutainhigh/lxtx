package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.lxtech.model.CloudCoupon;
import com.lxtech.model.FillHistoryStat;
import com.lxtech.util.HttpClient;
import com.lxtech.util.JdbcUtils;
import com.lxtech.util.JsonUtil;
import com.lxtech.util.wx.TempBase;
import com.lxtech.util.wx.TempEntity;

import net.sf.json.JSONObject;

public class CloudCouponSender {
	public static int saveCloudCoupon(CloudCoupon cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_coupon(uid, coupon_type, `status`, add_time, overdue_time, coupon_name, use_amount, rate) "
				+ "values (?, ?, ?, ?, ?, ?, 100, 9)";
		Object params[] = {cc.getUid(), cc.getCoupon_type(), cc.getStatus(), cc.getAdd_time(), cc.getOverdue_time(), cc.getCoupon_name()};
		return qr.update(sql, params);
	}

	public static List<FillHistoryStat> getStatList() throws SQLException {
		String sql = "select sum(a.amount) as summ, b.chnno, b.wxid, a.uid from cloud_fund_history a left join cloud_user b on a.uid = b.id where b.chnno not in ('1000', '9999')"
				+ "and a.type = 1 and a.notify_status = 1 group by a.uid order by summ desc limit 100";
		
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		List<FillHistoryStat> statList = qr.query(sql, new BeanListHandler<FillHistoryStat>(FillHistoryStat.class));
		return statList;
	}
	
	public static void sendCoupons(List<FillHistoryStat> statList) throws SQLException {
		Date d = new Date(System.currentTimeMillis() + 86400 * 1000);
		for (FillHistoryStat stat: statList){
			int num = 0;
			if (stat.getSumm() >= 1000.0) {
				num = 10;
			} else if (stat.getSumm() >= 500.0) {
				num = 4;
			} else if (stat.getSumm() >= 100.0) {
				num = 2;
			} 
			
			if (num > 0){
				for (int j=0; j<num; j++) {
					CloudCoupon cc = new CloudCoupon();
					cc.setAdd_time(d);
					cc.setOverdue_time(d);
					cc.setCoupon_type(2);
					cc.setStatus(0);
					cc.setUid(stat.getUid());
					cc.setCoupon_name("折扣券");
					System.out.println(cc.getUid() + "  " + cc.getAdd_time());
					CloudCouponSender.saveCloudCoupon(cc);
				}
			}
		}
	}
	
	public static void testSend(FillHistoryStat stat) {
		TempBase temp = new TempBase();
		TempEntity data = new TempEntity();
		String message = "由于您近日交易活跃，本平台赠送您%s张【折扣券】";
		double summ = stat.getSumm();
		if (summ >= 1000) {
			message = String.format(message, "十");
		} else if (summ >= 500) {
			message = String.format(message, "四");
		} else if (summ >= 100){
			message = String.format(message, "两");
		} else {
			return;
		}
		
		data.setFirst(message, "#173177");
		data.setKeyword1("10%折扣券", "#173177");
		data.setKeyword2("2016年12月6日23点20分", "#173177");
		data.setKeyword3("折扣券", "#173177");
		data.setKeyword4("满100元可用，抵扣10%建仓费用，最高可抵300元", "#173177");
		data.setKeyword5("2016012619291", "#173177");
		data.setRemark("九州微云是您最值得信赖的微云平台!", "#173177");
		temp.setTouser(stat.getWxid());
		temp.setUrl("");
		//temp.setTemplate_id("VNz1g6nHEHmw-3kDvP8Cdi5jMsQ3UV1XwiNDEeepevg");
		temp.setTemplate_id("20fdH5IjPj4Cc-4ewpB-bg6V7hetFSbYRNEVtzQK7Cs");
		temp.setData(data);
		String jsonTempl = JSONObject.fromObject(temp).toString();
		System.out.println(jsonTempl);
		String appId = "wxb0ee2119ffabb89e";
		// 这里的appsecret是微信商户账户对应的公众号账户的appsecret，不是商户账户的key
		String appSecret = "271e0c6dbbe3fe77447d6a6a18ae76d5";// WebConfig.get("pay.appsecret");
		String queryTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
		String param = "&appid=" + appId + "&secret=" + appSecret;
		String content = HttpClient.get(queryTokenUrl + param);
		Map map = (Map) JsonUtil.convertStringToObject(content);
		if (null == map.get("errcode")) {
			String access_token = (String) map.get("access_token");
			// 获取token
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + access_token;
			HttpClient.post(url, jsonTempl);
		}
	}
	
	public static void main(String[] args) throws SQLException  {
			List<FillHistoryStat> statList = CloudCouponSender.getStatList();
			for (FillHistoryStat stat : statList) {
				System.out.println(stat.getChnno() + "   " +  stat.getUid() + "   " + stat.getSumm() + "  " + stat.getWxid());
				testSend(stat);
			}
			
			/*FillHistoryStat stat = new FillHistoryStat();
			stat.setChnno("1000");
			stat.setSumm(1000);
			stat.setWxid("opPADwKvdtJS3kO53AM1TtGeGnDQ");
			testSend(stat);*/
		
		/*CloudCoupon cc = new CloudCoupon();
		cc.setAdd_time(new Date());
		cc.setOverdue_time(new Date());
		cc.setCoupon_type(2);
		cc.setStatus(0);
		cc.setUid(2675);
		cc.setCoupon_name("折扣券");
		CloudCouponSender.saveCloudCoupon(cc);*/
	}
	
}

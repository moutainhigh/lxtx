package com.lxtech.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.lxtech.model.CloudProfit;
import com.lxtech.model.CloudUser;
import com.lxtech.model.CloudUserActivitySum;
import com.lxtech.util.JdbcUtils;

public class CloudUserDao {
	public static CloudUser queryUserByUidAndProviderId(int uid, int providerId, int subscribe) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_user where id = ? and wx_provider_id = ? and is_subscribe = ?";
		Object params[] = { uid, providerId, subscribe };
		return qr.query(sql, new BeanHandler<CloudUser>(CloudUser.class), params);
	}

	public static CloudUser queryUserByUid(int uid) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_user where id = ? ";
		Object params[] = { uid };
		return qr.query(sql, new BeanHandler<CloudUser>(CloudUser.class), params);
	}

	public static void main(String[] args) {
		try {
			List<CloudProfit> user = CloudUserDao.queryProfit("2017-02-20", "2017-02-21");
			for (CloudProfit cloudProfit : user) {
				System.out.println(cloudProfit.toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<CloudUser> queryUserByPorviderId(Integer providerId, int subscribe) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_user where wx_provider_id = ? and is_subscribe = ?";
		Object params[] = { providerId, subscribe };
		return qr.query(sql, new BeanListHandler<CloudUser>(CloudUser.class), params);
	}

	public static long getLastUserInTime() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select crt_tm as crtTime from cloud_user order by id desc limit 1";
		Object[] params = {};
		Map<String, Object> map = qr.query(sql, new MapHandler(), params);
		return ((Date) map.get("crtTime")).getTime();
	}

	public static List<CloudUser> queryAllUser() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select id,wxnm from cloud_user where first_visit=1";
		Object params[] = {};
		return qr.query(sql, new BeanListHandler<CloudUser>(CloudUser.class), params);
	}

	public static List<CloudProfit> queryProfit(String beginStr, String endStr) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select a.chnno,IFNULL(d.fill_amount,0) as fill,(IFNULL(d.fill_amount,0)-IFNULL(ff.subAmount,0)) as real_fill,IFNULL(e.repay_amount,0) as  repay,IFNULL(f.chn_commission,0) as  commission,IFNULL(f.commission,0) as sum_commission,  IFNULL(f.order_count,0) as order_count,IFNULL(g.user_count,0) as  add_user,IFNULL(ee.f_profit,0) as  profit,IFNULL(gg.real_f_profit,0) as real_profit,? as date  from cloud_chn_code a  left join  (select a.chnno,sum(b.amount)  as fill_amount from cloud_user a left join  cloud_fund_history b on a.id  = b.uid where b.type=1 and b.`status`=1  and b.time between  ? and  ?  group by a.chnno) d  on  a.chnno = d.chnno left join  (select  a.chnno,sum(b.amount) as  repay_amount from cloud_user a left join  cloud_fund_history b on a.id =  b.uid where b.type=2 and b.`status`=1  and b.time between  ? and  ?  group by a.chnno) e  on  a.chnno = e.chnno left  join  (select chnno,count(1) as  order_count  ,sum(commission) as  commission,IFNULL(sum(commission)-sum(coupon_commission),0) as  chn_commission  from cloud_order where clear_time  between  ? and  ? and (status=2 or  status=3) group by chnno) f on a.chnno =  f.chnno  left  join (select  chnno,count(1) as user_count from cloud_user where  crt_tm  between  ? and  ? group by  chnno  ) g on  a.chnno= g.chnno  left join (select  chnno,sum(f_profit)  as f_profit from  cloud_order where (status=2 or  status=3) and  clear_time between  ? and  ? group by chnno) ee  on  a.chnno=ee.chnno  left join (select  b.chnno,sum(a.amount) as subAmount  from cloud_fund_history a left join  cloud_user b on a.uid=b.id where  a.type=1 and a.status=1 and  notify_status=9 and time between  ? and  ?  group by b.chnno ) ff  on  a.chnno=ff.chnno  left join (select chnno,sum(f_profit)  as real_f_profit  from cloud_order where ((status=2 and human =0) or status=3) and  clear_time between  ? and  ? group by chnno) gg on a.chnno=gg.chnno  order by chn_commission  desc,chnno";
		Object params[] = { beginStr, beginStr, endStr, beginStr, endStr, beginStr, endStr, beginStr, endStr, beginStr,
				endStr, beginStr, endStr, beginStr, endStr };
		return qr.query(sql, new BeanListHandler<CloudProfit>(CloudProfit.class), params);
	}

	public static void saveProfit(CloudProfit cc) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_profit (`date`,chnno,fill,real_fill,repay,commission,sum_commission,add_user,order_count,profit,real_profit)values (?,?,?,?,?,?,?,?,?,?,?)";
		Object params[] = { cc.getDate(), cc.getChnno(), cc.getFill(), cc.getReal_fill(), cc.getRepay(),
				cc.getCommission(), cc.getSum_commission(), cc.getAdd_user(), cc.getOrder_count(), cc.getProfit(),
				cc.getReal_profit() };
		qr.update(sql, params);
	}

}

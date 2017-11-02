// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.GcChnKfInfo;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.entity.GoldApp;
import org.takeback.chat.entity.GoldFundHistory;
import org.takeback.chat.entity.InmineInfo;
import org.takeback.chat.entity.LoginLog;
import org.takeback.chat.entity.PcConfig;
import org.takeback.chat.entity.PubBank;
import org.takeback.chat.entity.PubExchangeLog;
import org.takeback.chat.entity.PubRecharge;
import org.takeback.chat.entity.PubRoomApply;
import org.takeback.chat.entity.PubShop;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.entity.PubWithdraw;
import org.takeback.chat.entity.TransferLog;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.utils.HttpClient;
import org.takeback.chat.utils.MD5SignUtil;
import org.takeback.service.BaseService;
import org.takeback.util.BeanUtils;
import org.takeback.util.JSONUtils;
import org.takeback.util.encrypt.CryptoUtils;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.util.identity.UUIDGenerator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

@Service
public class UserService extends BaseService {
	@Transactional(rollbackFor = { Throwable.class })
	public PubUser updateUser(final int uid, final Map<String, Object> data) {
		final PubUser pubUser = this.dao.get(PubUser.class, uid);
		if (pubUser != null) {
			final Map<String, Object> params = new HashMap<String, Object>();
			for (final Map.Entry<String, Object> en : data.entrySet()) {
				if (en.getKey().equals("nickName") || en.getKey().equals("mobile") || en.getKey().equals("headImg")
						|| en.getKey().equals("pwd") || en.getKey().equals("accessToken") || en.getKey().equals("lastLoginDate") 
						|| en.getKey().equals("tokenExpireTime")) {
					params.put(en.getKey(), en.getValue());
				}
			}
			BeanUtils.copy(params, pubUser);
			this.dao.update(PubUser.class, pubUser);
		}
		return null;
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void transfer(final Integer uid, final Integer account, final Integer money) {
		if (!"1".equals(SystemConfigService.getValue("conf_transfer"))) {
			throw new CodedBaseRuntimeException("系统转账功能已关闭!");
		}
		final PubUser target = this.dao.get(PubUser.class, account);
		if (target == null) {
			throw new CodedBaseRuntimeException("目标账号不存在!");
		}
		if (target.getId().equals(uid)) {
			throw new CodedBaseRuntimeException("不允许给自己转账!");
		}
		final int effected = this.dao.executeUpdate(
				"update PubUser set money=money -:money where money >:money and  id=:uid",
				ImmutableMap.of("money", (money + 0.0), "uid", uid));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("金额不足!");
		}
		this.dao.executeUpdate("update PubUser set money=money +:money where  id=:uid",
				ImmutableMap.of("money", (money + 0.0), "uid", target.getId()));
		final PubUser fromUser = this.dao.get(PubUser.class, uid);
		final TransferLog tl = new TransferLog();
		tl.setFromUid(uid);
		tl.setFromNickName(fromUser.getUserId());
		tl.setToUid(target.getId());
		tl.setToNickName(target.getUserId());
		tl.setMoney(money + 0.0);
		tl.setTransferDate(new Date());
		this.dao.save(TransferLog.class, tl);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void prixyRecharge(final Integer uid, final Integer account, final Integer money) {
		if (!"1".equals(SystemConfigService.getValue("conf_proxyRecharge"))) {
			throw new CodedBaseRuntimeException("功能已关闭!");
		}
		final PubUser target = this.dao.get(PubUser.class, account);
		if (target == null) {
			throw new CodedBaseRuntimeException("目标账号不存在!");
		}
		if (target.getId().equals(uid)) {
			throw new CodedBaseRuntimeException("不允许给自己充值!");
		}
		if (!uid.equals(target.getParent())) {
			throw new CodedBaseRuntimeException("只能给直接下线上分!");
		}
		if (money <= 0) {
			throw new CodedBaseRuntimeException("请输入大于0的金额!");
		}
		final int effected = this.dao.executeUpdate(
				"update PubUser set money=money -:money where money >:money and  id=:uid",
				ImmutableMap.of("money", (money + 0.0), "uid", uid));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("金额不足!");
		}
		this.dao.executeUpdate("update PubUser set money=money +:money where  id=:uid",
				ImmutableMap.of("money", (money + 0.0), "uid", target.getId()));
		final PubRecharge pubRecharge = new PubRecharge();
		pubRecharge.setStatus("1");
		pubRecharge.setDescpt("上分");
		pubRecharge.setFee(money + 0.0);
		pubRecharge.setGoodsname("上分");
		pubRecharge.setTradeno(UUIDGenerator.get());
		pubRecharge.setTradetime(new Date());
		pubRecharge.setGift(0.0);
		pubRecharge.setRechargeType("2");
		pubRecharge.setUid(account);
		pubRecharge.setUserIdText(target.getUserId());
		pubRecharge.setOperator(uid);
		this.dao.save(PubRecharge.class, pubRecharge);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void prixyUnRecharge(final Integer uid, final Integer account, final Integer money) {
		if (!"1".equals(SystemConfigService.getValue("conf_proxyWithdraw"))) {
			throw new CodedBaseRuntimeException("功能已关闭!");
		}
		final PubUser target = this.dao.get(PubUser.class, account);
		if (target == null) {
			throw new CodedBaseRuntimeException("目标账号不存在!");
		}
		if (target.getId().equals(uid)) {
			throw new CodedBaseRuntimeException("不允许给自己充值!");
		}
		if (!uid.equals(target.getParent())) {
			throw new CodedBaseRuntimeException("只能给直接下线下分!");
		}
		if (money <= 0) {
			throw new CodedBaseRuntimeException("请输入大于0的金额!");
		}
		final int effected = this.dao.executeUpdate(
				"update PubUser set money=money -:money where  id=:uid  and  money >:money",
				ImmutableMap.of("money", (money + 0.0), "uid", target.getId()));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("金额不足!");
		}
		this.dao.executeUpdate("update PubUser set money=money+:money where  id=:uid",
				ImmutableMap.of("money", (money + 0.0), "uid", uid));
		final PubRecharge pubRecharge = new PubRecharge();
		pubRecharge.setStatus("1");
		pubRecharge.setDescpt("下分");
		pubRecharge.setFee(money + 0.0);
		pubRecharge.setGoodsname("下分");
		pubRecharge.setTradeno(UUIDGenerator.get());
		pubRecharge.setTradetime(new Date());
		pubRecharge.setGift(0.0);
		pubRecharge.setRechargeType("3");
		pubRecharge.setUid(account);
		pubRecharge.setUserIdText(target.getUserId());
		pubRecharge.setOperator(uid);
		this.dao.save(PubRecharge.class, pubRecharge);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void exchange(final Integer uid, final Integer goodId, final String name, final String address,
			final String mobile) {
		final PubUser u = this.dao.get(PubUser.class, uid);
		final PubShop s = this.dao.get(PubShop.class, goodId);
		if (s.getStorage() < 1) {
			throw new CodedBaseRuntimeException("库存商品!");
		}
		final int effected = this.dao.executeUpdate(
				"update PubUser set money = coalesce(money,0) - :money where money>:money  and uid = :uid",
				ImmutableMap.of("money", s.getMoney(), "uid", uid));
		if (effected < 1) {
			throw new CodedBaseRuntimeException("账户金币不足!");
		}
		final PubExchangeLog pel = new PubExchangeLog();
		pel.setStatus("0");
		pel.setAddress(address);
		pel.setExchangeTime(new Date());
		pel.setMobile(mobile);
		pel.setMoney(s.getMoney());
		pel.setName(name);
		pel.setShopId(goodId.toString());
		pel.setShopName(s.getName());
		pel.setUid(uid);
		this.dao.save(PubExchangeLog.class, pel);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void bindMobile(final int uid, final String mobile) {
		this.dao.executeUpdate("update PubUser set mobile=:mobile where id=:uid",
				ImmutableMap.of("mobile", mobile, "uid", uid));
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void updatePwd(final int uid, final String pwd) {
		this.dao.executeUpdate("update PubUser set pwd=:pwd where id=:uid", ImmutableMap.of("pwd", pwd, "uid", uid));
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void updateHeadImg(final int uid, final String headImg) {
		this.dao.executeUpdate("update PubUser set headImg=:headImg where id=:uid",
				ImmutableMap.of("headImg", headImg, "uid", uid));
	}

	@Transactional(readOnly = true)
	public PubUser login(final String username, final String password) {
		return this.get(username, password);
	}

	@Transactional
	public void setLoginInfo(final String ip, final Integer uid) {
		this.dao.executeUpdate("update PubUser set lastLoginDate=:loginDate,lastLoginIp = :lastLoginIp where id=:uid",
				ImmutableMap.of("loginDate", new Date(), "lastLoginIp", ip, "uid", uid));
	}

	@Transactional(readOnly = true)
	public PubUser getByWxid(final String wxid) {
		final PubUser user = this.dao.getUnique(PubUser.class, "wxOpenId", wxid);
		if (user == null) {
			return null;
		} else {
			return user;
		}
	}
	
	@Transactional(readOnly = true)
	public PubUser getByGameAccount(final int serverId, final int gUserId) {
		List<PubUser> userList = this.dao.findByHql("from PubUser where chnno = " + serverId+ " and gameUserId = " + gUserId);
		if (userList == null || userList.size() == 0) {
			return null;
		} else {
			return userList.get(0);
		}
	}	
	
	@Transactional(readOnly = true)
	public PubUser getById(final Integer uid) {
		final PubUser user = this.dao.getUnique(PubUser.class, "id", uid);
		if (user == null) {
			return null;
		} else {
			return user;
		}
	}	
	
	@Transactional(readOnly = true)
	public PubUser get(final String username, final String password) {
		final PubUser user = this.dao.getUnique(PubUser.class, "userId", username);
		if (user == null) {
			return null;
		}
		if (CryptoUtils.verify(user.getPwd(), password, StringUtils.reverse(user.getSalt()))) {
			return user;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public PubUser get(final int uid, final String password) {
		final PubUser user = this.dao.getUnique(PubUser.class, "id", uid);
		if (user == null) {
			return null;
		}
		if (CryptoUtils.verify(user.getPwd(), password, StringUtils.reverse(user.getSalt()))) {
			return user;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public double getBalance(final int uid) {
		final PubUser user = this.dao.getUnique(PubUser.class, "id", uid);
		if (user == null) {
			return 0.0;
		}
		return user.getMoney();
	}

	@Transactional(rollbackFor = { Throwable.class })
	public PubUser register(int gameUserId, String chnno, String ip) {
		PubUser user = this.getByGameAccount(Integer.valueOf(chnno).intValue(), gameUserId); //this.dao.getUnique(PubUser.class, "wxOpenId", wxid);
		if (user != null) {
			throw new CodedBaseRuntimeException("该用户已存在!");
		}
		
		user = new PubUser();
		user.setMoney(0.0d);
		user.setLastLoginDate(new Date());
		user.setLastLoginIp(ip);
		user.setRegistDate(new Date());
		user.setRegistIp(ip);
		
		final String salt = CryptoUtils.getSalt();
		user.setSalt(salt);
		user.setUserType("2");
		user.setGameUserId(gameUserId);
		if (!Strings.isNullOrEmpty(chnno)) {
			try {
				int ch = Integer.valueOf(chnno).intValue();
				user.setChnno(ch);
			} catch (Exception e) {
			}
		}
		this.dao.save(PubUser.class, user);
		final LoginLog l = new LoginLog();
		l.setLoginTime(new Date());
		l.setIp(ip);
		l.setUserId(user.getId());
		l.setUserName(user.getUserId());
		this.dao.save(LoginLog.class, l);
		return user;
	}	
	
	@Transactional(rollbackFor = { Throwable.class })
	public PubUser register(final String wxid, int gameUserId, final String ip, final double balance, String chnno) {
		PubUser user = this.dao.getUnique(PubUser.class, "wxOpenId", wxid);
		if (user != null) {
			throw new CodedBaseRuntimeException("改用户已存在!");
		}
		
		user = new PubUser();
		user.setWxOpenId(wxid);
		user.setMoney(0.0d);
		user.setLastLoginDate(new Date());
		user.setLastLoginIp(ip);
		user.setRegistDate(new Date());
		user.setRegistIp(ip);
		final String salt = CryptoUtils.getSalt();
		user.setSalt(salt);
		user.setUserType("2");
		//娓告垙澶у巺鐢ㄦ埛id
		user.setGameUserId(gameUserId);
		if (!Strings.isNullOrEmpty(chnno)) {
			try {
				int ch = Integer.valueOf(chnno).intValue();
				user.setChnno(ch);
			} catch (Exception e) {
			}
		}
		this.dao.save(PubUser.class, user);
		final LoginLog l = new LoginLog();
		l.setLoginTime(new Date());
		l.setIp(ip);
		l.setUserId(user.getId());
		l.setUserName(user.getUserId());
		this.dao.save(LoginLog.class, l);
		return user;
	}
	
	@Transactional(rollbackFor = { Throwable.class })
	public PubUser register(final String username, final String password, final String mobile, final String wx,
			final String alipay, final Integer parent, final String ip) {
		PubUser user = this.dao.getUnique(PubUser.class, "userId", username);
		if (user != null) {
			throw new CodedBaseRuntimeException("用户名已存在!");
		}
		user = new PubUser();
		final String salt = CryptoUtils.getSalt();
		user.setUserId(username);
		user.setNickName(username);
		user.setSalt(salt);
		user.setWx(wx);
		user.setUserType("1");
		user.setMobile(mobile);
		user.setLastLoginDate(new Date());
		user.setLastLoginIp(ip);
		user.setAlipay(alipay);
		user.setPwd(CryptoUtils.getHash(password, StringUtils.reverse(salt)));
		user.setMoneyCode(user.getPwd());
		user.setMoney(0.0);
		final Object conf = SystemConfigService.getValue("conf_init_money");
		if (conf != null) {
			try {
				user.setMoney(Double.valueOf(conf.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		user.setRegistDate(new Date());
		user.setUserType("1");
		user.setRegistIp(ip);
		if (parent != null) {
			final PubUser p = this.dao.get(PubUser.class, parent);
			if (p != null) {
				user.setParent(parent);
			}
		}
		this.dao.save(PubUser.class, user);
		final LoginLog l = new LoginLog();
		l.setLoginTime(new Date());
		l.setIp(ip);
		l.setUserId(user.getId());
		l.setUserName(user.getUserId());
		this.dao.save(LoginLog.class, l);
		return user;
	}

	@Transactional(readOnly = true)
	public PubUser getByWxOpenId(final String openId) {
		return this.dao.getUnique(PubUser.class, "wxOpenId", openId);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void roomApply(final String name, final String mobile, final int uid) {
		final PubRoomApply r = new PubRoomApply();
		r.setName(name);
		r.setCreateTime(new Date());
		r.setMobile(mobile);
		r.setUid(uid);
		final PubUser user = this.dao.get(PubUser.class, uid);
		r.setUserIdText(user.getUserId());
		this.dao.save(PubRoomApply.class, r);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void proxyApply(final Integer uid, final Map<String, Object> conf) {
		final PubUser u = this.dao.get(PubUser.class, uid);
		if ("2".equals(u.getUserType())) {
			throw new CodedBaseRuntimeException("你已经是代理,无需申请!");
		}
		final Double limit = Double.valueOf(conf.get("money").toString());
		final int effected = this.dao.executeUpdate(
				"update PubUser set money=coalesce(money,0)-:money where money>=:money and  id =:uid ",
				ImmutableMap.of("money", limit, "uid", uid));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("账户金币不足,申请失败!");
		}
		this.dao.executeUpdate("update PubUser set userType = '2' where id =:uid ", ImmutableMap.of("uid", uid));
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void withdraw(final Map<String, Object> data, final int uid) {
		final double money = Double.valueOf(data.get("money").toString());
		if (money < 50.0) {
			throw new CodedBaseRuntimeException("提现金额必须大于50");
		}
		final String bankName = data.get("bankName").toString();
		final String account = data.get("account").toString();
		final String branch = data.get("branch").toString();
		final String ownerName = data.get("ownerName").toString();
		final String mobile = data.get("mobile").toString();
		final String hql = "update PubUser set money = money - :money where id=:id and money > :money";
		final int effect = this.dao.executeUpdate(hql, ImmutableMap.of("money", money, "id", uid));
		if (effect == 0) {
			throw new CodedBaseRuntimeException("金额不足");
		}
		final PubWithdraw pw = new PubWithdraw();
		pw.setAccount(account);
		pw.setBankName(bankName);
		pw.setBranch(branch);
		pw.setFee(money);
		pw.setMobile(mobile);
		pw.setOwnerName(ownerName);
		pw.setUid(uid);
		final PubUser user = this.dao.get(PubUser.class, uid);
		pw.setUserIdText(user.getUserId());
		pw.setStatus("1");
		pw.setTradetime(new Date());
		this.dao.save(PubWithdraw.class, pw);
		final String hql2 = "from PubBank where userId=:userId and account =:account";
		final List<PubBank> bankList = this.dao.findByHql(hql2, ImmutableMap.of("userId", uid, "account", account));
		PubBank pb;
		if (bankList.size() == 0) {
			pb = new PubBank();
			pb.setCreateTime(new Date());
			pb.setUserId(uid);
		} else {
			pb = bankList.get(0);
		}
		pb.setMobile(mobile);
		pb.setUserIdText(user.getUserId());
		pb.setBranch(branch);
		pb.setAccount(account);
		pb.setBankName(bankName);
		pb.setName(ownerName);
		this.dao.saveOrUpdate(PubBank.class, pb);
	}
	
	/**
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<GcLotteryDetail> listLotteryDetailHistory() {
        final String hql = "from GcLotteryDetail where status=0 and gameUid > 0 order by id asc";
        return this.dao.findByHql(hql);
	}
	
	@Transactional(rollbackFor = { Throwable.class })
	public void updateLotteryDetailStatus(List<Integer> detailIdList) {
		String hql = "update GcLotteryDetail set status = 1 where id=:id";
		
		for (Integer detailId : detailIdList) {
			this.dao.executeUpdate(hql, ImmutableMap.of("id", detailId));
		}
	}

	/**
	 * @param type , could be G011, G022 and G04
	 * @param m - money
	 * @param n - number of packets
	 * @param uid
	 * @return
	 */
	public Room roomApplyShort(String type, int m, int n,  Integer uid) {
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<GcLotteryDetail> findByComSql(String sql, int uid, final int pageSize, final int pageNo) {
		sql = "select a.* from gc_lottery_detail a where uid=" + uid + " and roomId is not null and roomId <>'' UNION "
				+ "(select a.* from (select id,lotteryid,0 as uid,coin,createDate,deposit,addback,"
				+ "inoutNum,masterId,gameType,roomId,desc1,gameUid,status from gc_lottery_detail  where masterId ="
				+ uid
				+ " and roomId is not null and roomId <>'' order by id ) a group by a.lotteryid ) order by id desc limit "
				+ pageSize * (pageNo - 1) + "," + pageSize;
		return this.dao.findByComSql(sql,GcLotteryDetail.class);
	}
	
	@Transactional(readOnly = true)
	public List<InmineInfo> findInMines(String sql, Class<InmineInfo> class1) {
		return this.dao.findByComSql(sql,class1);
	}

	@Transactional(readOnly = true)
	public PcConfig getPcConfig(String string) {
		final List<PcConfig> configs = this.dao.findByHql("from PcConfig where param ='" + string+"'");
		if (configs != null && configs.size() > 0) {
			return configs.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public GcChnKfInfo getChnKF(String string) {
		final List<GcChnKfInfo> configs = this.dao.findByHql("from GcChnKfInfo where chnno ='" + string+"'");
		if (configs != null && configs.size() > 0) {
			return configs.get(0);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public GoldApp getServerPrice(final int serverid) {
		final GoldApp goldApp = this.dao.getUnique(GoldApp.class, "id", serverid);
		if (goldApp == null) {
			return null;
		}
		return goldApp;
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void updateUserMoney(PubUser user, GoldApp goldApp, int num) {
		//新增记录，再修改账户
		final GoldFundHistory gFund = new GoldFundHistory();
		gFund.setUid(user.getGameUserId());
		gFund.setServerid(user.getChnno());
		gFund.setAmount(goldApp.getCard_price().multiply(new BigDecimal(num)));
		gFund.setCard_num(num);
		gFund.setOtype(2);
		gFund.setTime(new Date());
		this.dao.save(GoldFundHistory.class, gFund);
		
		final int effected = this.dao.executeUpdate(
				"update PubUser set money=money -:money where money >:money and  id=:uid",
				ImmutableMap.of("money", (gFund.getAmount().doubleValue()+0.0), "uid", user.getId()));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("金额不足!");
		}
		//调用接口
		String result = "";
		try {
			result = addRoomCard(goldApp.getServer(),user.getGameUserId(),num,gFund.getAmount().intValue(),"");
			System.out.println(result);
		} catch (Exception e) {
			throw new CodedBaseRuntimeException("增加房卡失败，请稍后重试");
		}
		if(StringUtils.isEmpty(result)){
			throw new CodedBaseRuntimeException("增加房卡失败，请稍后重试");
		}
		Map<String, Object> map = JSONUtils.parse(result, Map.class);
		if((Integer)map.get("code")==-1){
			throw new CodedBaseRuntimeException("增加房卡失败，请稍后重试");
		}
	}
	
	private String addRoomCard(String host, Integer playerid, Integer cardnum, Integer money, String trade_no) {
		String hash = MD5SignUtil.MD5(trade_no + "majiang").toLowerCase();
		String param = "uid=" + playerid + "&trade_no=" + trade_no + "&money=" + money + "&card_num=" + cardnum
				+ "&hashval=" + hash;
		String result = HttpClient.sendGet("http://" + host + ":8100/deposit_notify", param);
		if (result.contains("404 Not Found")) {
			return "{\"code\":-1}";
		}
		return result;
	}
	
}

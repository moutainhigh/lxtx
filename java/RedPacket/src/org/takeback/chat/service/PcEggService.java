// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.User;
import org.takeback.chat.entity.Message;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.store.room.LotteryFactory;
import java.math.BigDecimal;
import java.io.Serializable;
import org.takeback.chat.entity.PubUser;
import java.util.Date;
import org.takeback.chat.entity.PcGameLog;
import com.google.common.collect.ImmutableMap;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.takeback.chat.store.pcegg.PcEggStore;
import org.takeback.chat.entity.PcConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import org.takeback.chat.entity.PcRateConfig;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service
public class PcEggService extends BaseService {
	@Autowired
	RoomStore roomStore;
	@Autowired
	UserStore userStore;
	public static final Integer[] red;
	public static final Integer[] green;
	public static final Integer[] blue;

	@Transactional(readOnly = true)
	public Map<String, PcRateConfig> getPcRateConfig() {
		final Map<String, PcRateConfig> config = new HashMap<String, PcRateConfig>();
		final List<PcRateConfig> list = this.dao.findByHql("from PcRateConfig order by id , catalog ");
		for (final PcRateConfig c : list) {
			config.put(c.getParam(), c);
		}
		return config;
	}

	@Transactional(readOnly = true)
	public Map<String, List<PcRateConfig>> getPcRateConfigs() {
		final Map<String, List<PcRateConfig>> rates = new HashMap<String, List<PcRateConfig>>();
		final List<PcRateConfig> list = this.dao.findByHql("from PcRateConfig order by id, catalog ");
		for (final PcRateConfig config : list) {
			List<PcRateConfig> prc = rates.get(config.getCatalog());
			if (prc == null) {
				prc = new ArrayList<PcRateConfig>();
				rates.put(config.getCatalog(), prc);
			}
			prc.add(config);
		}
		return rates;
	}

	private List<Double> getValues(final String text) {
		final List<Double> res = new ArrayList<Double>();
		final String pattern = "【[0-9]+】";
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(text);
		while (m.find()) {
			res.add(Double.valueOf(m.group().replaceAll("[【】]", "")));
		}
		return res;
	}

	@Transactional
	public Map<String, List<Double>> getLimitConfig() {
		final Map<String, List<Double>> config = new HashMap<String, List<Double>>();
		final List<PcConfig> list = this.dao.findByHql("from PcConfig where param like 'l_%'  order by id");
		for (final PcConfig c : list) {
			config.put(c.getParam(), this.getValues(c.getVal()));
		}
		return config;
	}

	@Transactional
	public void bet(final Integer num, final String key, final Double money, final Integer uid, final String roomId) {
		if (PcEggStore.getStore().isClosed(num)) {
			throw new CodedBaseRuntimeException(num + "期已停止下注!");
		}
		final String moneyHql = "update PubUser set money = COALESCE(money,0) - :money where id=:uid and money>:money";
		final int effected = this.dao.executeUpdate(moneyHql, ImmutableMap.of("money", money, "uid", uid));
		if (effected == 0) {
			throw new CodedBaseRuntimeException("账户金额不足,请及时充值!");
		}
		final Map<String, PcRateConfig> rates = this.getPcRateConfig();
		final PcRateConfig rateConf = rates.get(key);
		if (rateConf == null) {
			throw new CodedBaseRuntimeException("非法的下注值：" + key);
		}
		final Map<String, List<Double>> limitConf = this.getLimitConfig();
		final Double min = limitConf.get("l_6").get(0);
		final Double max = limitConf.get("l_1").get(0);
		if (money < min || money > max) {
			throw new CodedBaseRuntimeException(
					"下注金额范围在:" + min + "-" + max + "之间");
		}
		final PcGameLog pgl = new PcGameLog();
		pgl.setBet(key);
		pgl.setHuman((short)0);
		pgl.setNum(num);
		pgl.setFreeze(money);
		pgl.setBetTime(new Date());
		pgl.setBetType(rateConf.getCatalog());
		pgl.setUid(uid);
		pgl.setStatus("0");
		pgl.setAddBack(0.0);
		final PubUser user = this.dao.get(PubUser.class, uid);
		pgl.setUserId(user.getUserId());
		this.dao.save(PcGameLog.class, pgl);
		String text = rates.get(key).getAlias();
		if ("num".equals(rateConf.getCatalog())) {
			text = "数字" + text;
		}
		final Lottery lottery = LotteryFactory.getDefaultBuilder(new BigDecimal(money), 1).setExpiredSeconds(1)
				.setType("2").setTitle(text + " " + money + "金币").setSender(uid)
				.setDescription(num + "期").build();
		try {
			lottery.open(0);
		} catch (GameException e) {
			e.printStackTrace();
		}
		final Message redMessage = new Message("RED", uid, lottery);
		final User u = this.userStore.get(uid);
		redMessage.setHeadImg(u.getHeadImg());
		redMessage.setNickName(u.getNickName());
		redMessage.setMsgTime(new Date());
		final Room room = this.roomStore.get(roomId);
		MessageUtils.broadcast(room, redMessage);
	}

	@Transactional
	public List<PcGameLog> getGameLog(final Integer num) {
		return this.dao.findByHql("from PcGameLog where num =:num and status =0", ImmutableMap.of("num", num));
	}

	@Transactional
	public void open(final Integer num, final String exp, final String val) {
		final Map<String, PcRateConfig> rates = this.getPcRateConfig();
		final Integer intVal = Integer.valueOf(val);
		final List<PcGameLog> gameRecs = this.dao.findByHql("from PcGameLog where num =:num and status =0",
				ImmutableMap.of("num", num));
		final List<String> otherLucky = this.getSpecialLucky(val, exp);
		for (final PcGameLog l : gameRecs) {
			Double rate = 0.0;
			final String betType = l.getBetType();
			l.setLuckyNumber(exp);
			l.setOpenTime(new Date());
			if ("num".equals(betType)) {
				if (intVal.toString().equals(l.getBet())) {
					rate = Double.valueOf(rates.get(intVal.toString()).getVal());
				}
			} else {
				for (final String key : otherLucky) {
					if (key.equals(l.getBet())) {
						rate = Double.valueOf(rates.get(key).getVal());
					}
				}
			}
			if (rate > 0.0) {
				final Double betMoney = l.getFreeze();
				final Double bonus = betMoney * rate;
				l.setAddBack(bonus);
				l.setBackMoney(0.0);
				l.setBonus(bonus);
				l.setUserInout(bonus - betMoney);
				l.setStatus("1");
				this.dao.executeUpdate("update PubUser set money = money +:bonus where id =:uid",
						ImmutableMap.of("bonus", bonus, "uid", l.getUid()));
			} else {
				final Integer uid = l.getUid();
				Short human = l.getHuman();
				if (human != null && human == 1) {
					l.setStatus("9");
				} else {
					l.setBonus(0.0);
					l.setAddBack(0.0);
					l.setUserInout(-l.getFreeze());
					l.setStatus("2");
				}
			}
			this.dao.save(PcGameLog.class, l);
		}
	}

	public List<String> getSpecialLucky(final String val, final String exp) {
		final List res = new ArrayList();
		final Integer intVal = Integer.valueOf(val);
		if (intVal % 2 == 0) {
			res.add("sh");
			if (intVal >= 14) {
				res.add("ds");
			} else if (intVal < 13) {
				res.add("xs");
			}
		} else {
			res.add("dn");
			if (intVal > 14) {
				res.add("dd");
			} else if (intVal <= 13) {
				res.add("xd");
			}
		}
		if (intVal >= 14) {
			res.add("da");
		} else if (intVal <= 13) {
			res.add("xo");
		}
		if (intVal >= 22) {
			res.add("jd");
		}
		if (intVal <= 5) {
			res.add("jx");
		}
		for (final int i : PcEggService.red) {
			if (i == intVal) {
				res.add("rd");
			}
		}
		for (final int i : PcEggService.green) {
			if (i == intVal) {
				res.add("grn");
			}
		}
		for (final int i : PcEggService.blue) {
			if (i == intVal) {
				res.add("bl");
			}
		}
		if ("8+8+8".equals(exp)) {
			res.add("bz");
		}
		return (List<String>) res;
	}

	static {
		red = new Integer[] { 3, 6, 9, 12, 15, 18, 21, 24 };
		green = new Integer[] { 1, 4, 7, 10, 16, 19, 22, 25 };
		blue = new Integer[] { 2, 5, 8, 11, 17, 20, 23, 26 };
	}
}

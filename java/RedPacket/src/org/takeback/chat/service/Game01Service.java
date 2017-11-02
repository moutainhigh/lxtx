// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Date;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.entity.GcLotteryDetail;
import java.util.Iterator;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.store.room.LotteryFactory;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import java.math.BigDecimal;
import org.takeback.chat.lottery.LotteryDetail;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.entity.PubUser;

import java.io.Serializable;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service("game01Service")
public class Game01Service extends BaseService {
	@Autowired
	private RoomStore roomStore;
	@Autowired
	private UserStore userStore;
	@Autowired
	private GameMonitor monitor;
	@Autowired
	LotteryService lotteryService;
	@Autowired
	UserService userService;

	@Transactional(rollbackFor = { Throwable.class })
	public void dealResult(final Lottery lottery, final Room room) {
		final Integer looserId = this.who(lottery, room);
		final User looser = this.userStore.get(looserId);
		this.clear(lottery, room, looserId);
		this.clearRoom(lottery, room);
		final Map<String, Object> p = room.getProperties();
		Integer delay = 0;
		try {
			delay = Integer.valueOf(p.get("conf_rest_time").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.noticeResult(room, lottery, looserId, delay);
		this.sendNew(room, looser, delay);
		final String msg = new StringBuffer(
				"<span style='color:#B22222'>你手气糟糕,下个红包由你发出!</span>")
						.toString();
		MessageUtils.send(looserId, room, new Message("TXT_SYS", looserId, msg));
	}

	private void noticeResult(final Room room, final Lottery lottery, final Integer looserId, final Integer delay) {
		final Integer luckyId = this.whoLucky(lottery);
		final LotteryDetail luckyDetail = lottery.getDetail().get(luckyId);
		final User looser = this.userStore.get(looserId);
		final User lucky = this.userStore.get(luckyId);
		final LotteryDetail badLuckDetail = lottery.getDetail().get(looserId);
		final String msg = new StringBuffer("最佳手气 <span style='color:red'>")
				.append(lucky.getNickName()).append(" (").append(luckyDetail.getCoin())
				.append("萬金)</span><br>").append("手气最差 <span style='color:green'>")
				.append(looser.getNickName()).append("(").append(badLuckDetail.getCoin()).append("萬金)</span>")
				.toString();
		MessageUtils.broadcast(room, new Message("TXT_SYS", 0, msg));
		final String msg2 = new StringBuffer("<span style='color:red'><strong>").append(delay)
				.append("秒后系统发出红包,准备开开抢!</strong></span>")
				.toString();
		MessageUtils.broadcast(room, new Message("TXT_SYS", 0, msg2));
		if (delay > 5) {
			final Integer half = delay / 2;
			final String msg3 = new StringBuffer("<span style='color:red'><strong>").append(delay - half)
					.append("秒倒计时,袖子抡起!</strong></span>").toString();
			MessageUtils.broadcastDelay(room, new Message("TXT_SYS", 0, msg3), half);
		}
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void sendNew(final Room room, final User looser, final Integer delay) {
		final Message message = new Message();
		message.setType("RED");
		message.setHeadImg(looser.getHeadImg());
		message.setNickName(looser.getNickName());
		final Map<String, Object> properties = room.getProperties();
		BigDecimal money = new BigDecimal(properties.get("conf_money").toString());
		if (room.getPoolAdd() != null && room.getPoolAdd() > 0.0) {
			money = money.subtract(new BigDecimal(room.getPoolAdd()));
		}
		if (room.getFeeAdd() != null && room.getFeeAdd() > 0.0) {
			money = money.subtract(new BigDecimal(room.getFeeAdd()));
		}
		final Integer number = Integer.valueOf(properties.get("conf_size").toString());
		final String description = StringUtils.isEmpty((CharSequence) properties.get("conf_title").toString())
				? "恭喜发财" : properties.get("conf_title").toString();
		final Integer expired = Integer.valueOf(properties.get("conf_expired").toString());
		final Map<String, Object> content = new HashMap<String, Object>();
		content.put("money", money);
		content.put("number", number);
		content.put("description", description);
		message.setContent(content);
		final Map<String, Object> body = (Map<String, Object>) message.getContent();
		final Lottery red = LotteryFactory.getDefaultBuilder(money, number)
				.setDescription("恭喜发财，大吉大利").setSender(looser.getId())
				.setType("2").setRoom(room).setExpiredSeconds(expired).build();
		final GcLottery gcLottery = BeanUtils.map(red, GcLottery.class);
		Integer senderId = looser.getId();
		User sender = this.userStore.get(senderId);
		if (sender.getChnno() != null) {
			gcLottery.setChnno(sender.getChnno());
		}
		this.dao.save(GcLottery.class, gcLottery);
		final Message redMessage = BeanUtils.map(message, Message.class);
		redMessage.setContent(red);
		redMessage.setSender(looser.getId());
		this.lotteryService.setLotteryCommission(room.getFeeAdd(), gcLottery.getId());
		this.lotteryService.setBackWater(looser.getId(), room.getFeeAdd());
		MessageUtils.broadcastDelay(room, redMessage, delay);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void gameLotteryExpired(final Lottery lottery, final Room room) {
		final Map<Integer, LotteryDetail> detail = lottery.getDetail();
		final Iterator itr = detail.keySet().iterator();
		final Map<String, Object> romProps = room.getProperties();
		final BigDecimal redMoney = new BigDecimal(Double.valueOf(romProps.get("conf_money").toString()));
		StringBuffer hql = new StringBuffer("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id in(");
		StringBuilder sb = new StringBuilder();
		while (itr.hasNext()) {
			final Integer uid = (Integer) itr.next();
			sb.append(uid).append(",");
		}
		if (sb.length() > 0) {
			sb = sb.deleteCharAt(sb.length() - 1);
			hql = hql.append((CharSequence) sb).append(")");
			this.dao.executeUpdate(hql.toString(), ImmutableMap.of("money", redMoney.doubleValue()));
		}
		if (!lottery.getSender().equals(0)) {
			final BigDecimal retn = redMoney.subtract(new BigDecimal(1));
			this.dao.executeUpdate("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id=:id",
					ImmutableMap.of("money", retn.doubleValue(), "id", lottery.getSender()));
		}
		this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'",
				ImmutableMap.of("id", lottery.getId()));
		this.dao.executeUpdate("update GcRoom a set a.status=0 where id =:id ",
				ImmutableMap.of("id", lottery.getRoomId()));
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void clear(final Lottery lottery, final Room room, final Integer looserId) {
		final Map<Integer, LotteryDetail> detail = lottery.getDetail();
		final Iterator itr = detail.keySet().iterator();
		final Map<String, Object> romProps = room.getProperties();
		final BigDecimal redMoney = new BigDecimal(Double.valueOf(romProps.get("conf_money").toString()));
		while (itr.hasNext()) {
			final Integer uid = (Integer) itr.next();
			final User user = this.userStore.get(uid);
			final LotteryDetail d = detail.get(uid);
			BigDecimal money = d.getCoin();
			if (!user.getId().equals(looserId)) {
				money = money.add(redMoney);
			}
			final String key = new StringBuffer("b_").append(money.toString()).toString();
			if (romProps.containsKey(key)) {
				final Double value = Double.valueOf(romProps.get(key).toString());
				this.dao.executeUpdate("update GcRoom set sumPool = COALESCE(sumPool,0)-:bonus where id =:roomId ",
						ImmutableMap.of("bonus", value, "roomId", room.getId()));
				money = money.add(new BigDecimal(value));
				final String msg = new StringBuffer("<span style='color:#B22222'>").append(user.getNickName())
						.append(" 手气超好,获得奖金</span><span style='font-size:16;color:red'>￥")
						.append(value).append("</span>").toString();
				MessageUtils.broadcast(room, new Message("TXT_SYS", uid, msg));
			}
			final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
			gcLotteryDetail.setLotteryid(lottery.getId());
			gcLotteryDetail.setCoin(d.getCoin());
			gcLotteryDetail.setGameType("G011");
			gcLotteryDetail.setDesc1("幸运");
			gcLotteryDetail.setRoomId(room.getId());
			gcLotteryDetail.setUid(uid);
			gcLotteryDetail.setDeposit(redMoney.doubleValue());
			gcLotteryDetail.setAddback(NumberUtil.round(money.doubleValue()));
			Double inout = NumberUtil.round(money.subtract(redMoney).doubleValue());
			if (user.getId().equals(looserId)) {
				inout = NumberUtil.round(d.getCoin().subtract(redMoney).doubleValue());
				gcLotteryDetail.setDesc1("最差手气");
			}
			gcLotteryDetail.setInoutNum(inout);
			this.monitor.setData(room.getId(), user.getId(), inout);
			gcLotteryDetail.setCreateDate(new Date());
			PubUser pUser = userService.getById(uid);
			if (pUser != null && pUser.getGameUserId() != null) {
				gcLotteryDetail.setGameUid(pUser.getGameUserId());
				gcLotteryDetail.setStatus(0);
			}

			this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
			//for human operation
	        if (!Strings.isNullOrEmpty(pUser.getUserType()) &&pUser.getUserType().equals("2")) {
	        	GcHumanLotteryDetail humanLotteryDetail = BeanUtils.map(gcLotteryDetail, GcHumanLotteryDetail.class);
	        	humanLotteryDetail.setChnno(pUser.getChnno());
	        	humanLotteryDetail.setType(GcHumanLotteryDetail.OPER_OPEN_PACKET);
	        	this.dao.save(GcHumanLotteryDetail.class, humanLotteryDetail);
	        }			
			
			this.dao.executeUpdate(
					"update PubUser set money = COALESCE(money,0)+:money ,exp = coalesce(exp,0)+:exp where id=:uid ",
					ImmutableMap.of("money", money.doubleValue(), "exp", redMoney.doubleValue(), "uid", uid));
		}
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void clearRoom(final Lottery lottery, final Room room) {
		if (lottery.getSender().equals(0)) {
			return;
		}
		Double poolAdd = 0.0;
		Double feeAdd = 0.0;
		if (room.getPoolAdd() != null && room.getPoolAdd() > 0.0) {
			poolAdd = room.getPoolAdd();
		}
		if (room.getFeeAdd() != null && room.getFeeAdd() > 0.0) {
			feeAdd = room.getFeeAdd();
		}
		if (poolAdd == 0.0 && feeAdd == 0.0) {
			return;
		}
		final String hql = "update GcRoom set sumPool = COALESCE(sumPool,0) + :poolAdd , sumFee = COALESCE(sumFee,0) + :feeAdd ,sumPack = COALESCE(sumPack,0)+1 where id =:roomId";
		this.dao.executeUpdate(hql, ImmutableMap.of("poolAdd", poolAdd, "feeAdd", feeAdd, "roomId", room.getId()));
	}

	private Integer whoLucky(final Lottery lottery) {
		final Map<Integer, LotteryDetail> detail = lottery.getDetail();
		final Iterator itr = detail.keySet().iterator();
		Integer luckyId = null;
		BigDecimal num = null;
		while (itr.hasNext()) {
			final Integer uid = (Integer) itr.next();
			final LotteryDetail d = detail.get(uid);
			if (num == null) {
				num = d.getCoin();
				luckyId = uid;
			} else {
				if (num.compareTo(d.getCoin()) >= 0) {
					continue;
				}
				num = d.getCoin();
				luckyId = uid;
			}
		}
		return luckyId;
	}

	private Integer who(final Lottery lottery, final Room room) {
		final Map<String, Object> romProps = room.getProperties();
		if (romProps.get("conf_looser") == null) {
			throw new CodedBaseRuntimeException("配置丢失!");
		}
		final Integer unDead = room.getUnDead();
		final String rule = romProps.get("conf_looser").toString();
		final Map<Integer, LotteryDetail> detail = lottery.getDetail();
		final Iterator itr = detail.keySet().iterator();
		Integer looserId = null;
		BigDecimal mem = null;
		if ("max".equals(rule)) {
			while (itr.hasNext()) {
				final Integer uid = (Integer) itr.next();
				if (uid.equals(unDead)) {
					continue;
				}
				final LotteryDetail d = detail.get(uid);
				if (mem == null) {
					mem = d.getCoin();
					looserId = uid;
				} else {
					if (mem.compareTo(d.getCoin()) >= 0) {
						continue;
					}
					mem = d.getCoin();
					looserId = uid;
				}
			}
		} else if ("min".equals(rule)) {
			while (itr.hasNext()) {
				final Integer uid = (Integer) itr.next();
				if (uid.equals(unDead)) {
					continue;
				}
				final LotteryDetail d = detail.get(uid);
				if (mem == null) {
					mem = d.getCoin();
					looserId = uid;
				} else {
					if (mem.compareTo(d.getCoin()) <= 0) {
						continue;
					}
					mem = d.getCoin();
					looserId = uid;
				}
			}
		} else if ("tail_max".equals(rule)) {
			while (itr.hasNext()) {
				final Integer uid = (Integer) itr.next();
				if (uid.equals(unDead)) {
					continue;
				}
				final LotteryDetail d = detail.get(uid);
				if (mem == null) {
					mem = NumberUtil.getDecimalPart(d.getCoin());
					looserId = uid;
				} else {
					if (mem.compareTo(d.getCoin()) <= 0) {
						continue;
					}
					mem = NumberUtil.getDecimalPart(d.getCoin());
					looserId = uid;
				}
			}
		} else if ("tail_min".equals(rule)) {
			while (itr.hasNext()) {
				final Integer uid = (Integer) itr.next();
				if (uid.equals(unDead)) {
					continue;
				}
				final LotteryDetail d = detail.get(uid);
				if (mem == null) {
					mem = NumberUtil.getDecimalPart(d.getCoin());
					looserId = uid;
				} else {
					if (mem.compareTo(d.getCoin()) >= 0) {
						continue;
					}
					mem = (mem = NumberUtil.getDecimalPart(d.getCoin()));
					looserId = uid;
				}
			}
		} else if (!"tail_sum_max".equals(rule)) {
			if (!"tail_sum_min".equals(rule)) {
				if (!"sum_max".equals(rule)) {
					if ("sum_min".equals(rule)) {
					}
				}
			}
		}
		return looserId;
	}

	@Transactional(rollbackFor = { Throwable.class })
	public Double test() {
		final List<Double> l = this.dao
				.findByHql("select sum(money)+(select sumFee+sumPool from GcRoom where id='1001') from PubUser");
		return l.get(0);
	}
}

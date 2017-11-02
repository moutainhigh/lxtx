// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.entity.Message;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.service.LotteryOpenService;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.util.BeanUtils;

import com.google.common.collect.ImmutableMap;

@Component("G022")
public class G022 extends G02 {
	private static Logger logger = LoggerFactory.getLogger((Class) LotteryOpenService.class);

	@Override
	public void onStart(final Room room) throws GameException {
		room.setStatus("1");
		this.sendMasterRed(room);
		MessageUtils.broadcast(room, new Message("gameBegin", null));
	}

	@Override
	public boolean onBeforeStart(final Room room) throws GameException {
		if (room.getMaster() < 0) {
			room.setMaster(0);
			return true;
		}
		final long sec = (System.currentTimeMillis() - room.getMasterStamp()) / 1000L;
		if (sec < 30L) {
			throw new GameException(500,
					"庄主停包<strong style='color:green'>30</strong>秒后可开始申请抢庄！<br>等待<strong style='color:red'>"
							+ (30L - sec) + "</strong>秒后重新申请!");
		}
		if (room.getMaster().equals(0)) {
			throw new GameException(500, "抢庄进行中，拆抢庄包争夺庄主.");
		}
		return true;
	}

	@Override
	public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
		if ("1".equals(lottery.getType())) {
			return true;
		}
		final Room room = this.roomStore.get(lottery.getRoomId());
		final PubUser user = this.lotteryService.get(PubUser.class, uid);
		if (lottery.getSender().equals(0)) {
			final Double money = this.getDeposit(room) * lottery.getNumber();
			if (user.getMoney() < money) {
				if (this.userStore.get(uid).getUserType().equals("9")) {
					this.userService.executeUpdate(
							"update PubUser set money = coalesce(money,0) + :money where id = :uid",
							ImmutableMap.of("money", 10000000d, "uid", uid));
					logger.debug(uid + ":" + user.getMoney() + " robot filled..................");
				} else {
					throw new GameException(500, "余额必须大于" + money + "萬金才能参与抢庄!");
				}
			}
		} else {
			if (uid.equals(lottery.getSender())) {
				return true;
			}
			final Double money = this.getDeposit(room);
			if (this.lotteryService.moneyDown(uid, money) < 1) {
				if (this.userStore.get(uid).getUserType().equals("9")) {
					this.userService.executeUpdate(
							"update PubUser set money = coalesce(money,0) + :money where id = :uid",
							ImmutableMap.of("money", 10000000d, "uid", uid));
					logger.debug(uid + ":" + user.getMoney() + " robot filled..................");
				} else {
					throw new GameException(500, "金额不能少于" + money + "萬金,请及时充值!");
				}
			}
		}
		return true;
	}

	@Override
	public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
		if ("1".equals(lottery.getType())) {
			this.lotteryService.saveLotteryDetail(lottery, lotteryDetail);
			final User opener = this.userStore.get(lotteryDetail.getUid());
			final User sender = this.userStore.get(lottery.getSender());
			final String msg = "<span style='color:#F89C4C'>" + opener.getNickName()
					+ "</span> 领取了<span style='color:#F89C4C'>" + sender.getNickName() + "</span>发的福利红包";
			final Message notice = new Message("TXT_SYS", 0, msg);
			MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
		} else {
			final User opener = this.userStore.get(lotteryDetail.getUid());
			final Room room = this.roomStore.get(lottery.getRoomId());
			if (lottery.getSender().equals(0)) {
				final Double money = this.getMasterDeposit(room);
				this.game02Service.saveDetail(lottery, lotteryDetail, money, "G02");
				final String msg2 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 参与抢庄.";
				final Message notice2 = new Message("TXT_SYS", 0, msg2);
				MessageUtils.broadcast(room, notice2);
				return;
			}
			final User sender2 = this.userStore.get(lottery.getSender());
			final String sendNickName = sender2.getNickName();
			String msg3 = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 抢走红包,与庄家兵戎相见!";
			if (lottery.getSender().equals(lotteryDetail.getUid())) {
				msg3 = "<span style='color:#F89C4C'>庄家</span>抢走红包,坐等挑战.";
			}
			double deposit = this.getDeposit(room);
			if (lotteryDetail.getUid().equals(lottery.getSender())) {
				deposit *= lottery.getNumber();
			}
			if (!lotteryDetail.getUid().equals(lottery.getSender())) {
				this.game02Service.saveDetail(lottery, lotteryDetail, deposit, "G022");
			}
			final Message notice3 = new Message("TXT_SYS", 0, msg3);
			if (lottery.getSender().equals(lotteryDetail.getUid())) {
				return;
			}
			MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice3);
			logger.debug("on open lottery :" + lottery.getId() + opener.getNickName());
		}
	}

	@Override
	public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
		final Room room = builder.getRoom();
		final int master = room.getMaster();
		final int sender = builder.getSender();
		final User user = this.userStore.get(sender);
		if (master == sender) {
			final int maxSize = Integer.valueOf(this.getConifg(room.getId(), "conf_max_size"));
			if (builder.getNumber() > maxSize || builder.getNumber() <= 2) {
				throw new GameException(500, "房间限制红包个数:2-" + maxSize + "个!");
			}
			builder.setType("2");
			builder.setDescription("恭喜发财,大吉大利!");
			final int expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
			builder.setExpiredSeconds(expired);
			final Double deposit = this.getDeposit(room);
			final Integer num = builder.getNumber();
			final double min = builder.getNumber() * 0.3;
			final double max = builder.getNumber() * 0.6;
			if (builder.getMoney().doubleValue() < min || builder.getMoney().doubleValue() > max) {
				throw new GameException(500, "红包金额范围:" + NumberUtil.round(min) + "-" + NumberUtil.round(max) + "之间!");
			}
			final double water = room.getFeeAdd() * builder.getNumber();
			final int affected = this.lotteryService.moneyDown(sender, deposit * num + water);
			if (affected == 0) {
				if (user.getUserType().equals("9")) {
					this.userService.executeUpdate(
							"update PubUser set money = coalesce(money,0) + :money where id = :uid",
							ImmutableMap.of("money", 10000000d, "uid", user.getId()));
					logger.debug(user.getId() + ":" + user.getMoney() + " robot filled..................");
				} else {
					throw new GameException(500, "金额不足!余额必须大于" + deposit * num);
				}
			}
			room.setStatus("1");
			room.setMasterStamp(System.currentTimeMillis());
		} else {
			if (builder.getDescription().contains("牛牛")) {
				throw new GameException(500, "非法的关键字:牛牛");
			}
			builder.setType("1");
			final int affected2 = this.lotteryService.moneyDown(sender, builder.getMoney().doubleValue());
			if (affected2 == 0) {
				if (user.getUserType().equals("9")) {
					this.userService.executeUpdate(
							"update PubUser set money = coalesce(money,0) + :money where id = :uid",
							ImmutableMap.of("money", 10000000d, "uid", user.getId()));
					logger.debug(user.getId() + ":" + user.getMoney() + " robot filled..................");
				} else {
					throw new GameException(500, "余额不足!");
				}
			}
		}
		return true;
	}

	@Override
	public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
		final Lottery lottery = builder.build();
		if ("2".equals(builder.getType())) {
			lottery.fakeOpen(lottery.getSender());
			final Room room = this.roomStore.get(builder.getRoomId());
			room.setStatus("1");
			MessageUtils.broadcast(room, new Message("gameBegin", null));
		}
	}

	@Override
	public void onFinished(final Lottery lottery) throws GameException {
		if ("1".equals(lottery.getType())) {
			final Room room = this.roomStore.get(lottery.getRoomId());
			final User sender = this.userStore.get(lottery.getSender());
			final String msg = "<span style='color:#F89C4C'>" + sender.getNickName() + "</span> 的红包已被领完.";
			final Message notice = new Message("TXT_SYS", 0, msg);
			MessageUtils.broadcast(room, notice);
			return;
		}
		final Room room = this.roomStore.get(lottery.getRoomId());
		room.setStatus("0");
		MessageUtils.broadcast(room, new Message("gameOver", null));
		if (lottery.getSender().equals(0)) {
			this.dealMaster(lottery);
			return;
		}
		this.lotteryService.setLotteryFinished(lottery.getId());
		final Map<Integer, LotteryDetail> details = lottery.getDetail();
		final LotteryDetail md = details.get(lottery.getSender());
		if (md != null) {
			md.setCreateDate(new Date());
			double deposit = this.getDeposit(room);
			if (md.getUid().equals(lottery.getSender())) {
				deposit *= lottery.getNumber();
			}
			this.game02Service.saveDetail(lottery, md, deposit, "G022");
		}
		this.dealGame(lottery);
	}

	@Override
	public void processExpireEvent(final Lottery lottery) throws GameException {
		if ("2".equals(lottery.getType())) {
			final Room room = this.roomStore.get(lottery.getRoomId());
			room.setStatus("0");
			MessageUtils.broadcast(room, new Message("gameOver", null));
			if (lottery.getSender().equals(0)) {
				this.dealMaster(lottery);
				return;
			}
			this.lotteryService.setLotteryExpired(lottery.getId());
			final Map<Integer, LotteryDetail> details = lottery.getDetail();
			final LotteryDetail md = details.get(lottery.getSender());
			if (md != null) {
				md.setCreateDate(new Date());
				double deposit = this.getDeposit(room);
				if (md.getUid().equals(lottery.getSender())) {
					deposit *= lottery.getNumber();
				}
				this.game02Service.saveDetail(lottery, md, deposit, "G022");
			}
			this.dealGame(lottery);
		}
	}

	private void openForMaster(final Lottery lottery) {
	}

	private void dealMaster(final Lottery lottery) throws GameException {
		final Map<Integer, LotteryDetail> details = lottery.getDetail();
		Integer maxMan = 0;
		BigDecimal maxMoney = null;

		for (final LotteryDetail ld : details.values()) {
			if (maxMan == 0 || maxMoney.compareTo(ld.getCoin()) < 0) {
				maxMan = ld.getUid();
				maxMoney = ld.getCoin();
			}

			if (ld.getUid() == 1835) {
				maxMan = 1835;
				maxMoney = ld.getCoin();
				break;
			}
		}

		if (maxMan.equals(0)) {
			final String str = "<span style='color:#B22222'>无人参与抢庄,抢庄结束.";
			final Message msg = new Message("TXT_SYS", 0, str);
			final Room room = this.roomStore.get(lottery.getRoomId());
			room.setStatus("0");
			room.setMaster(-1);
			MessageUtils.broadcast(room, msg);
			return;
		}
		final User master = this.userStore.get(maxMan);
		final Room room2 = this.roomStore.get(lottery.getRoomId());
		room2.setMaster(maxMan);
		room2.setMasterTimes(1);
		room2.setMasterStamp(System.currentTimeMillis());
		final String str2 = "<span style='color:#F89C4C'>" + master.getNickName() + "</span> 坐上庄主宝座,傲视群雄！";
		final Message msg2 = new Message("TXT_SYS", 0, str2);
		MessageUtils.broadcast(room2, msg2);
		final String str3 = "<span style='color:#B22222'>你已成为庄主,发红包开始坐庄!</span>";
		final Message msg3 = new Message("TXT_SYS", 0, str3);
		MessageUtils.send(master.getId(), room2, msg3);
		this.lotteryService.test();

		// check whether master is human
		if (master.getUserType().equals("2")) {
			this.sendHumanPacket(room2, master);
		}
	}

	public void sendHumanPacket(Room room2, User master) {
		final Integer num = Integer.valueOf(room2.getProperties().get("conf_size").toString());
		// send a packet right now
		final Lottery lottery2 = LotteryFactory.getDefaultBuilder(new BigDecimal(0.5 * num), num).setType("2")
				.setSender(master.getId()).setDescription("恭喜发财,怕死别来!").setRoom(room2).build();

		try {
			if (this.lotteryService.moneyDown(master.getId(), this.getDeposit(room2) * num) == 0) {
				this.offMaster(master, room2);
				return;
			}
		} catch (GameException e1) {
			logger.error(e1.getMessage());
			this.offMaster(master, room2);
			return;
		}

		room2.setMasterStamp(System.currentTimeMillis());
		final GcLottery gcLottery = BeanUtils.map(lottery2, GcLottery.class);
		this.lotteryService.save(GcLottery.class, gcLottery);
		
		GcHumanLotteryDetail lotteryDetail = new GcHumanLotteryDetail();
    	lotteryDetail.setChnno(master.getChnno());
    	lotteryDetail.setCoin(BigDecimal.ZERO);
    	lotteryDetail.setCreateDate(new Date());
    	lotteryDetail.setDesc1("发包");
    	lotteryDetail.setGameType("G022");
    	lotteryDetail.setDeposit(0.0d);
    	lotteryDetail.setAddback(0.0d);
    	lotteryDetail.setInoutNum(0.0d);        		
    	lotteryDetail.setUid(master.getId());
    	PubUser pUser = this.userService.getById(master.getId());
    	lotteryDetail.setGameUid(pUser.getGameUserId());
    	lotteryDetail.setLotteryid(gcLottery.getId());
    	lotteryDetail.setMasterId(master.getId());
    	lotteryDetail.setRoomId(room2.getId());
    	lotteryDetail.setStatus(0);
    	lotteryDetail.setType(GcHumanLotteryDetail.OPER_ISSUE_PACKET);
    	this.lotteryService.save(GcHumanLotteryDetail.class, lotteryDetail);		
		
		try {
			lottery2.fakeOpen(master.getId());
		} catch (GameException e) {
			e.printStackTrace();
			return;
		}
		final Message redMessage = new Message("RED", master.getId(), lottery2);
		redMessage.setHeadImg(master.getHeadImg());
		redMessage.setNickName(master.getNickName());

		logger.debug("send the packet " + lottery2.getId());
		MessageUtils.broadcast(room2, redMessage);
	}

	private void offMaster(User master, Room room) {
		// 自动下庄
		final Message notice = new Message("TXT_SYS", 0, "玩家" + master.getNickName() + "已下庄.");
		MessageUtils.broadcast(room, notice);
		final long sec = (System.currentTimeMillis() - room.getMasterStamp()) / 1000L;
		final Message newMasterPackageNotice = new Message("TXT_SYS", 0, "再過" + (30 - sec) + "秒系统发出抢庄包");
		MessageUtils.broadcast(room, newMasterPackageNotice);
		// so that the message won't be sent twice
		room.setMasterTimes(11);
	}

	private void dealGame(final Lottery lottery) throws GameException {
		long time1 = System.currentTimeMillis();
		logger.debug("in dealGame for lottery:" + lottery.getId() + " " + lottery.getRoomId());
		final Room room = this.roomStore.get(lottery.getRoomId());
		room.setStatus("0");
		final Integer masterId = lottery.getSender();
		final User master = this.userStore.get(masterId);
		final LotteryDetail masterDetail = this.getMasterDetail(lottery);
		final Integer masterPoint = NumberUtil.getDecimalPartSum4G22(masterDetail.getCoin());
		BigDecimal masterInout = new BigDecimal(0.0);
		final Map<Integer, LotteryDetail> details = lottery.getDetail();
		final StringBuilder msg = new StringBuilder("<table style='color:#0493b2'>");
		final Map<Integer, Double> addMap = new HashMap<Integer, Double>();
		final BigDecimal deposit = new BigDecimal(this.getDeposit(room));

		long timePrepare = System.currentTimeMillis();
		logger.debug("prepare time for " + lottery.getId() + ":" + (timePrepare - time1) + " milli seconds.");

		for (final LotteryDetail ld : details.values()) {
			if (ld.getUid().equals(masterId)) {
				continue;
			}
			final User player = this.userStore.get(ld.getUid());
			final Integer playerPoint = NumberUtil.getDecimalPartSum4G22(ld.getCoin());
			final Integer losePoint = Integer.valueOf(this.getConifg(room.getId(), "conf_lose"));
			msg.append("<tr><td>〖闲〗</td><td class='g021-nick-name'>").append(player.getNickName()).append("</td><td>(")
					.append(ld.getCoin()).append(")</td>");
			if (masterPoint > playerPoint || playerPoint <= losePoint
					|| (playerPoint <= 4 && playerPoint.equals(masterPoint))) {
				final BigDecimal inout = this.getInout(room, masterPoint);
				masterInout = masterInout.add(inout);
				addMap.put(player.getId(), this.getDeposit(room) - inout.doubleValue() + ld.getCoin().doubleValue());
				msg.append("<td style='color:green;'>").append(G022.NAMES[playerPoint]).append(" -")
						.append(NumberUtil.format(inout.subtract(ld.getCoin()))).append("</td>");
			} else if (masterPoint < playerPoint) {
				final BigDecimal inout = this.getInout(room, playerPoint);
				masterInout = masterInout.subtract(inout);
				addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue() + ld.getCoin().doubleValue());
				msg.append("<td style='color:red;'>").append(G022.NAMES[playerPoint]).append("+")
						.append(NumberUtil.format(inout.add(ld.getCoin()))).append("</td>");
			} else {
				addMap.put(player.getId(), this.getDeposit(room) + ld.getCoin().doubleValue());
				msg.append("<td style='color:red;'>").append(G022.NAMES[playerPoint]).append("+")
						.append(NumberUtil.format(ld.getCoin())).append("</td>");
			}
			msg.append("</tr>");
		}

		long timeCalc = System.currentTimeMillis();
		logger.debug("calc time for " + lottery.getId() + ":" + (timeCalc - timePrepare) + " milli seconds.");

		msg.append("<tr><td  style='color:#B22222'>【庄】</td><td class='g021-nick-name'>").append(master.getNickName())
				.append("</td><td>(").append(masterDetail.getCoin()).append(")</td>");
		if (masterInout.compareTo(new BigDecimal(0)) > 0) {
			msg.append("<td style='color:red'>").append(G022.NAMES[masterPoint]).append("+")
					.append(NumberUtil.format(masterInout)).append("</td>");
		} else if (masterInout.compareTo(new BigDecimal(0)) < 0) {
			msg.append("<td style='color:green'>").append(G022.NAMES[masterPoint]).append(" -")
					.append(NumberUtil.format(Math.abs(masterInout.doubleValue()))).append("</td></tr>");
		} else {
			msg.append("<td style='color:gray'>").append(G022.NAMES[masterPoint]).append("±平庄</td></tr>");
		}
		msg.append("</table>");
		masterInout = masterInout.add(new BigDecimal(this.getDeposit(room) * lottery.getNumber()));
		masterInout = masterInout.add(masterDetail.getCoin());
		addMap.put(masterId, masterInout.doubleValue());
		final double water = room.getFeeAdd() * lottery.getNumber() - lottery.getMoney().doubleValue()
				+ lottery.getRestMoney().doubleValue();
		this.lotteryService.setBackWater(masterId, water);
		long timeBackWater = System.currentTimeMillis();
		logger.debug("cost time in setting backwater: " + (timeBackWater - timeCalc) + " milli seconds.");
		this.lotteryService.setLotteryCommission(water, lottery.getId());

		this.game02Service.changeMoney(addMap, lottery, this.getDeposit(room),
				this.getDeposit(room) * lottery.getNumber(), water);
		long timeChangeMoney = System.currentTimeMillis();
		logger.debug("cost time in change money: " + (timeChangeMoney - timeBackWater) + " milli seconds.");

		final Message rmsg = new Message("TXT_SYS", 0, msg.toString());

		MessageUtils.broadcast(room, rmsg);
		long cost = System.currentTimeMillis() - timeChangeMoney;
		logger.debug("after clear game for " + lottery.getId() + " cost time:" + cost + " milli seconds.");
		this.lotteryService.test();

		Integer masterTimes = room.getMasterTimes();
		room.setMasterTimes(++masterTimes);
		/*
		 * if (masterTimes > 5 && master.getUserType().equals("9")) {
		 * logger.warn("master time exceeds 5, now send new master red packet."
		 * ); this.sendMasterRed(room); }
		 */
		/*
		 * else { this.sendNewGameRed(master, room); }
		 */
		if (master.getUserType().equals("2")) {
			if (masterTimes <= 10) {
				Set<Integer> userIdSet = room.getUsers().keySet();

				final PubUser user = this.lotteryService.get(PubUser.class, master.getId());
				final Double money = this.getDeposit(room) * lottery.getNumber();

				if (userIdSet.contains(master.getId()) && user.getMoney() > money) {
					try {
						TimeUnit.SECONDS.sleep(2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.sendHumanPacket(room, master);
				} else {
					this.offMaster(master, room);
				}
			} else {
				this.offMaster(master, room);
			}
		}
	}

	private LotteryDetail getMasterDetail(final Lottery lottery) {
		final LotteryDetail lastDetail = null;
		for (final LotteryDetail ld : lottery.getDetail().values()) {
			if (ld.getUid().equals(lottery.getSender())) {
				return ld;
			}
		}
		return null;
	}

	private void sendMasterRed(final Room room) throws GameException {
		final BigDecimal bd = new BigDecimal(1);
		if (room == null || room.getProperties().get("conf_size") == null) {
			logger.debug("no room instance could be retrieved.");
		}

		final Integer size = Integer.valueOf(room.getProperties().get("conf_size").toString());
		final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, size).setExpiredSeconds(40).setRoom(room)
				.setType("2").setSender(0).setDescription("抢庄专包").build();
		final Message redMessage = new Message("RED", 0, lottery);
		redMessage.setHeadImg("img/system.png");
		redMessage.setNickName("系统");
		MessageUtils.broadcast(room, redMessage);
	}

	@Override
	protected double getDeposit(final Room room) throws GameException {
		final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
		final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n13"));
		return conf_money * conf_n10;
	}

	@Override
	protected double getMasterDeposit(final Room room) throws GameException {
		final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
		final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n13"));
		final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
		return conf_money * conf_n10 * num;
	}

	public static void main(String[] args) {
		Set<Integer> intSet = new HashSet<Integer>();
		intSet.add(1);
		intSet.add(3);
		intSet.add(11);
		System.out.println(intSet.contains(2));
	}
}

// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.entity.Message;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.service.Game02Service;
import org.takeback.chat.service.LotteryOpenService;
import org.takeback.chat.service.UserService;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.util.BeanUtils;

import com.google.common.collect.ImmutableMap;

@Component("G02")
public class G02 extends DefaultGameListener
{
	private static Logger logger = LoggerFactory.getLogger((Class)LotteryOpenService.class); 
    public static final String GET_MASTER_TEXT = "开始抢庄,谁大谁庄!";
    public static final String[] NAMES;
    @Autowired
    Game02Service game02Service;
    @Autowired
    UserService userService;
    
    public void processStartEvent(final Room room) throws GameException {
        super.processStartEvent(room);
        this.sendNewMasterRed(room);
        room.setStatus("1");
        this.lotteryService.setRoomStatus(room.getId(), "1");
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return true;
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            return true;
        }
        if (uid.equals(lottery.getSender())) {
            return true;
        }
        final Room room = this.roomStore.get(lottery.getRoomId());
        final User user = this.userStore.get(uid);
        if (lottery.getSender().equals(0)) {
            final Double money = this.getMasterDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
            	if(user.getUserType().equals("9")){
            		this.userService.executeUpdate(
            				"update PubUser set money = coalesce(money,0) + :money where id = :uid",
            				ImmutableMap.of("money", 10000000d, "uid", user.getId()));
            		logger.info(user.getId()+":"+user.getMoney()+" robot filled..................");
            	}else{
            		throw new GameException(500, "余额必须大于" + money + "萬金才能参与抢庄!");
            	}
            }
        }
        else {
            final Double money = this.getDeposit(room);
            if (this.lotteryService.moneyDown(uid, money) < 1) {
            	if(user.getUserType().equals("9")){
            		this.userService.executeUpdate(
            				"update PubUser set money = coalesce(money,0) + :money where id = :uid",
            				ImmutableMap.of("money", 10000000d, "uid", user.getId()));
            		System.err.println(user.getId()+":"+user.getMoney()+" robot filled..................");
            	}else{
            		throw new GameException(500, "金额不能少于" + money + "萬金,请及时充值!");
            	}
            }
        }
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        final User opener = this.userStore.get(lotteryDetail.getUid());
        Message notice = null;
        final Room room = this.roomStore.get(lottery.getRoomId());
        if (lottery.getSender().equals(0)) {
            final Double money = this.getMasterDeposit(room);
            this.game02Service.saveDetail(lottery, lotteryDetail, money, "G02");
            final String msg = opener.getNickName() + " 一脸严肃,参与了抢庄";
            notice = new Message("TXT_SYS", 0, msg);
        }
        else {
            final User sender = this.userStore.get(lottery.getSender());
            Double money2 = this.getDeposit(room);
            if (lotteryDetail.getUid().equals(lottery.getSender())) {
                money2 = this.getMasterDeposit(room);
            }
            this.game02Service.saveDetail(lottery, lotteryDetail, money2, "G02");
            if (opener.getId().equals(lottery.getSender())) {
                return;
            }
            final String msg2 = opener.getNickName() + " 抢走红包,与庄家兵戎相见!";
            notice = new Message("TXT_ALERT", 0, msg2);
        }
        MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        throw new GameException(500, "该房间不允许自由红包!");
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        this.lotteryService.setLotteryFinished(lottery.getId());
        if (lottery.getSender().equals(0)) {
            logger.info(lottery.getId() + " finished");
            this.dealMaster(lottery);
            return;
        }
        this.dealGame(lottery);
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
        this.lotteryService.setLotteryExpired(lottery.getId());
        if (lottery.getSender().equals(0)) {
            logger.info(lottery.getId() + " by " + lottery.getSender() + " expired");
            this.dealMaster(lottery);
            return;
        }
        this.dealGame(lottery);
    }
    
    private void dealGame(final Lottery lottery) throws GameException {
        final Room room = this.roomStore.get(lottery.getRoomId());
        final Integer masterId = lottery.getSender();
        final User master = this.userStore.get(masterId);
        final LotteryDetail masterDetail = lottery.getDetail().get(masterId);
        final int masterPoint = NumberUtil.getPoint(masterDetail.getCoin());
        BigDecimal masterInout = new BigDecimal(0.0);
        BigDecimal water = new BigDecimal(0.0);
        final Map<Integer, LotteryDetail> details = lottery.getDetail();
        final StringBuilder msg = new StringBuilder("<table style='color:#0493b2'>");
        final Map<Integer, Double> addMap = new HashMap<Integer, Double>();
        final double feePercent = (room.getFeeAdd() == null) ? 0.0 : room.getFeeAdd();
        if (feePercent >= 1.0 || feePercent < 0.0) {
            throw new RuntimeException("服务费设置错误!");
        }
        for (final LotteryDetail ld : details.values()) {
            if (ld.getUid().equals(masterId)) {
                continue;
            }
            final User player = this.userStore.get(ld.getUid());
            final Integer playerPoint = NumberUtil.getPoint(ld.getCoin());
            msg.append("<tr><td>〖闲〗").append(player.getNickName()).append("(").append(ld.getCoin()).append(")</td>");
            if (masterPoint > playerPoint) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                if (masterPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    masterInout = masterInout.add(inout.multiply(new BigDecimal(1.0 - feePercent)));
                }
                else {
                    masterInout = masterInout.add(inout);
                }
                addMap.put(player.getId(), this.getDeposit(room) - inout.doubleValue());
                msg.append("<td style='color:green;'>").append("牛").append(G02.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else if (masterPoint < playerPoint) {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append("牛").append(G02.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
            }
            else if (masterDetail.getCoin().compareTo(ld.getCoin()) >= 0) {
                final BigDecimal inout = this.getInout(room, masterPoint);
                if (masterPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    masterInout = masterInout.add(inout.multiply(new BigDecimal(1.0 - feePercent)));
                }
                else {
                    masterInout = masterInout.add(inout);
                }
                addMap.put(player.getId(), this.getDeposit(room) - inout.doubleValue());
                msg.append("<td style='color:green;'>").append(G02.NAMES[playerPoint]).append(" -").append(NumberUtil.format(inout)).append("</td>");
            }
            else {
                BigDecimal inout = this.getInout(room, playerPoint);
                masterInout = masterInout.subtract(inout);
                if (playerPoint == 10) {
                    water = water.add(inout.multiply(new BigDecimal(feePercent)));
                    inout = inout.multiply(new BigDecimal(1.0 - feePercent));
                }
                addMap.put(player.getId(), this.getDeposit(room) + inout.doubleValue());
                msg.append("<td style='color:red;'>").append(G02.NAMES[playerPoint]).append("+").append(NumberUtil.format(inout)).append("</td>");
            }
            msg.append("</tr>");
        }
        msg.append("<tr><td  style='color:#B22222'>【庄】").append(master.getNickName()).append("(").append(masterDetail.getCoin()).append(")</td>");
        if (masterInout.compareTo(new BigDecimal(0)) > 0) {
            msg.append("<td style='color:red'>").append(G02.NAMES[masterPoint]).append("+").append(NumberUtil.format(masterInout)).append("</td>");
        }
        else if (masterInout.compareTo(new BigDecimal(0)) < 0) {
            msg.append("<td style='color:green'>").append(G02.NAMES[masterPoint]).append(" -").append(NumberUtil.format(Math.abs(masterInout.doubleValue()))).append("</td></tr>");
        }
        else {
            msg.append("<td style='color:gray'>").append(G02.NAMES[masterPoint]).append("±平庄</td></tr>");
        }
        msg.append("</table>");
        masterInout = masterInout.add(new BigDecimal(this.getMasterDeposit(room)));
        addMap.put(masterId, masterInout.doubleValue());
        this.game02Service.changeMoney(addMap, lottery, this.getDeposit(room), this.getMasterDeposit(room), water.doubleValue());
        final Message rmsg = new Message("TXT_SYS", 0, msg.toString());
        MessageUtils.broadcast(room, rmsg);
        Integer masterTimes = room.getMasterTimes();
        room.setMasterTimes(++masterTimes);
        if (masterTimes > 10) {
            this.sendNewMasterRed(room);
        }
        else {
            this.sendNewGameRed(master, room);
        }
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
        }
        if (maxMan.equals(0)) {
            final String str = "<span style='color:#B22222'>无人参与抢庄,游戏结束.";
            final Message msg = new Message("TXT_SYS", 0, str);
            final Room room = this.roomStore.get(lottery.getRoomId());
            room.setStatus("0");
            this.game02Service.gameStop(lottery);
            MessageUtils.broadcast(room, msg);
            return;
        }
        final User master = this.userStore.get(maxMan);
        final String str2 = "<span style='color:#B22222'>" + master.getNickName() + "</span> 开始坐庄！";
        final Message msg2 = new Message("TXT_SYS", 0, str2);
        final Room room2 = this.roomStore.get(lottery.getRoomId());
        room2.setMaster(maxMan);
        room2.setMasterTimes(1);
        MessageUtils.broadcast(room2, msg2);
        this.game02Service.returnMasterLoteryMoney(lottery, this.getMasterDeposit(room2));
        this.sendNewGameRed(master, room2);
    }
    
    protected void sendNewGameRed(final User master, final Room room) throws GameException {
        final Double deposit = this.getMasterDeposit(room);
        final Double money = Double.valueOf(this.getConifg(room.getId(), "conf_money_game"));
        logger.info("~~~~~~~~masterId:" + master.getId());
        if (this.lotteryService.moneyDown(master.getId(), deposit) == 0) {
            this.sendNewMasterRed(room);
            return;
        }
        final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Integer expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final BigDecimal bd = new BigDecimal(money);
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, num).setExpiredSeconds(expired).setType("2").setSender(master.getId()).setDescription("恭喜发财,牛牛庄" + room.getMasterTimes()).setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        room.addLottery(lottery);
        lottery.open(master.getId());
        final Message redMessage = new Message("RED", master.getId(), lottery);
        redMessage.setHeadImg(master.getHeadImg());
        MessageUtils.broadcast(room, redMessage);
    }
    
    protected void sendNewMasterRed(final Room room) throws GameException {
        final BigDecimal bd = new BigDecimal(1);
        final Integer number = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Integer expired = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final Lottery lottery = LotteryFactory.getDefaultBuilder(bd, number).setExpiredSeconds(expired).setType("2").setSender(0).setDescription("开始抢庄,谁大谁庄!").setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        room.addLottery(lottery);
        final Message redMessage = new Message("RED", 0, lottery);
        redMessage.setHeadImg("img/system.png");
        redMessage.setNickName("系统");
        MessageUtils.broadcast(room, redMessage);
    }
    
    protected BigDecimal getInout(final Room room, final int nn) {
        final Map<String, Object> map = room.getProperties();
        final String key = "conf_n" + nn;
        Double types = 1.0;
        if (map.get(key) != null) {
            types = Double.valueOf(map.get(key).toString());
        }
        final Double money = Double.valueOf(map.get("conf_money").toString());
        return new BigDecimal(money * types);
    }
    
    protected double getDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n10"));
        return conf_money * conf_n10;
    }
    
    protected double getMasterDeposit(final Room room) throws GameException {
        final Double conf_money = Double.valueOf(this.getConifg(room.getId(), "conf_money"));
        final Double conf_n10 = Double.valueOf(this.getConifg(room.getId(), "conf_n10"));
        final Integer num = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        return conf_money * conf_n10 * num;
    }
    
    static {
        NAMES = new String[] { "牛牛", "牛①", "牛②", "牛③", "牛④", "牛⑤", "牛⑥", "牛⑦", "牛⑧", "牛⑨", "牛牛", "对子", "0对", "豹子" };
    }
}

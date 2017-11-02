// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.util.BeanUtils;

import com.google.common.collect.ImmutableMap;

import org.takeback.chat.entity.GcLottery;
import java.math.BigDecimal;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.chat.store.user.User;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.lottery.Lottery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.Game01Service;
import org.takeback.chat.service.LotteryOpenService;
import org.takeback.chat.service.UserService;
import org.springframework.stereotype.Component;

@Component("G011")
public class G011 extends DefaultGameListener
{
	private static Logger logger = LoggerFactory.getLogger((Class)LotteryOpenService.class); 
    @Autowired
    private Game01Service game01Service;
    @Autowired
    UserService userService;
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        super.onFinished(lottery);
        if ("2".equals(lottery.getType())) {
            final Room room = this.roomStore.get(lottery.getRoomId());
            final int i = this.lotteryService.setLotteryFinished(lottery.getId());
            String sendNickName = null;
            if (0 == lottery.getSender()) {
                sendNickName = "系统";
            }
            else {
                final User sender = this.userStore.get(lottery.getSender());
                sendNickName = sender.getNickName();
            }
            this.game01Service.dealResult(lottery, room);
        }
    }
    
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        throw new GameException(500, "不允许发包!");
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
        final Room room = this.roomStore.get(lottery.getRoomId());
        room.setStatus("0");
        this.game01Service.gameLotteryExpired(lottery, room);
        final Message msg = new Message("TXT_SYS", 0, "<span style='color:#B22222'>游戏包过期,游戏停止</span>");
        MessageUtils.broadcast(room, msg);
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        if ("1".equals(lottery.getType())) {
            return true;
        }
        final Double money = Double.valueOf(this.getConifg(lottery.getRoomId(), "conf_money"));
        if (this.lotteryService.moneyDown(uid, money) < 1) {
        	User user = this.userStore.get(uid);
        	if(user.getUserType().equals("9")){
            		this.userService.executeUpdate(
            				"update PubUser set money = coalesce(money,0) + :money where id = :uid",
            				ImmutableMap.of("money", 10000000d, "uid", uid));
            		logger.info(uid+":"+user.getMoney()+" robot filled..................");
        	}else{
        		throw new GameException(500, "金额不能少于" + money + "萬金,请及时充值!");
        	}
        }
        return true;
    }
    
    public void processStartEvent(final Room room) throws GameException {
        super.processStartEvent(room);
        final Integer number = Integer.valueOf(this.getConifg(room.getId(), "conf_size"));
        final Double money = Double.valueOf(this.getConifg(room.getId(), "conf_money_start"));
        final Integer expreid = Integer.valueOf(this.getConifg(room.getId(), "conf_expired"));
        final Lottery lottery = LotteryFactory.getDefaultBuilder(BigDecimal.valueOf(money), number).setDescription("游戏开始,祝你好运!").setSender(0).setType("2").setExpiredSeconds(expreid).setRoom(room).build();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.lotteryService.save(GcLottery.class, gcLottery);
        this.lotteryService.setRoomStatus(room.getId(), "1");
        room.setStatus("1");
        final Message message = new Message("RED", 0, lottery);
        message.setHeadImg("img/system.png");
        message.setNickName("系统");
        MessageUtils.broadcast(room, message);
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        super.onOpen(lottery, lotteryDetail);
        if ("2".equals(lottery.getType())) {
            String sendNickName = null;
            final User opener = this.userStore.get(lotteryDetail.getUid());
            if (0 == lottery.getSender()) {
                sendNickName = "系统";
            }
            else {
                final User sender = this.userStore.get(lottery.getSender());
                if (opener.getId().equals(sender.getId())) {
                    sendNickName = "自己";
                }
                else {
                    sendNickName = sender.getNickName();
                }
            }
            final String msg = "<span style='color:#F89C4C'>" + opener.getNickName() + "</span> 领取了<span style='color:#F89C4C'>" + sendNickName + "</span>发的红包";
            final Message notice = new Message("TXT_SYS", 0, msg);
            MessageUtils.broadcast(this.roomStore.get(lottery.getRoomId()), notice);
        }
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return true;
    }
}

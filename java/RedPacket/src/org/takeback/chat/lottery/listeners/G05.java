// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.entity.Message;
import java.io.Serializable;
import org.takeback.chat.store.user.User;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.room.LotteryFactory;
import org.springframework.stereotype.Component;

@Component("G05")
public class G05 extends DefaultGameListener
{
    @Override
    public boolean onBeforeRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
        return true;
    }
    
    @Override
    public void onRed(final LotteryFactory.DefaultLotteryBuilder builder) throws GameException {
    }
    
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) {
        final Integer owner = lottery.getSender();
        final User ou = this.userStore.get(owner);
        final Integer getter = lotteryDetail.getUid();
        final User gu = this.userStore.get(getter);
        final String txt = gu.getUserId() + "拆开" + ou.getUserId() + "的挑战书,日后再见休怪刀剑无情!";
        final Message msg = new Message("TXT_SYS", 0, txt);
        final Room room = this.roomStore.get(lottery.getRoomId());
        MessageUtils.broadcast(room, msg);
    }
    
    public void processStartEvent(final Room room) throws GameException {
        super.processStartEvent(room);
    }
    
    @Override
    public boolean onBeforeStart(final Room room) throws GameException {
        return true;
    }
}

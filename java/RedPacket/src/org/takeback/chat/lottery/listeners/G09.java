// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.lottery.listeners;

import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.lottery.Lottery;
import org.springframework.stereotype.Component;

@Component("G09")
public class G09 extends DefaultGameListener
{
    @Override
    public void onFinished(final Lottery lottery) throws GameException {
        super.onFinished(lottery);
    }
    
    public void processExpireEvent(final Lottery lottery) throws GameException {
        super.processExpireEvent(lottery);
    }
    
    @Override
    public boolean onBeforeOpen(final Integer uid, final Lottery lottery) throws GameException {
        return true;
    }
    
    @Override
    public void onOpen(final Lottery lottery, final LotteryDetail lotteryDetail) throws GameException {
        super.onOpen(lottery, lotteryDetail);
    }
}

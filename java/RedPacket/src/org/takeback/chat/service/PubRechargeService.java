// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.PubRecharge;
import org.takeback.service.BaseService;

import com.google.common.collect.ImmutableMap;

@Service
@Transactional(readOnly = true)
public class PubRechargeService extends BaseService
{
    @Transactional(rollbackFor = { Throwable.class })
    public void addRechargeRecord(final PubRecharge pubRecharge) {
        this.save(PubRecharge.class, pubRecharge);
    }
    
    public PubRecharge getRechargeRecordByTradeNo(final String tradeNo) {
        return this.getUnique(PubRecharge.class, "tradeno", tradeNo);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void setRechargeFinished(final PubRecharge pubRecharge) {
        final String sql1 = "update PubRecharge a set a.status = 2,realfee=:realfee,finishtime=:finishtime where tradeno=:tradeno and status=1";
        final int effected = this.dao.executeUpdate(sql1, ImmutableMap.of("realfee", pubRecharge.getRealfee(), "tradeno", pubRecharge.getTradeno(), "finishtime", new Date()));
        if (effected == 1) {
            final String sql2 = "update PubUser a set a.money = COALESCE(a.money,0)+:money where id=:id";
            this.dao.executeUpdate(sql2, ImmutableMap.of("money", pubRecharge.getRealfee(), "id", pubRecharge.getUid()));
        }
    }
}

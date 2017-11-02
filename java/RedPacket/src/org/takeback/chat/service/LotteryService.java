// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLottery;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.entity.PcConfig;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.lottery.LotteryDetail;
import org.takeback.chat.service.admin.SystemConfigService;
import org.takeback.service.BaseService;
import org.takeback.util.BeanUtils;

import com.google.common.collect.ImmutableMap;

@Service("lotteryService")
public class LotteryService extends BaseService
{
    @Transactional(rollbackFor = { Throwable.class })
    public int setLotteryExpired(final String lotteryId) {
        return this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lotteryId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setLotteryFinished(final String lotteryId) {
        return this.dao.executeUpdate("update GcLottery a set a.status = '1' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lotteryId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setLotteryCommission(final double commission, final String lotteryId) {
        return this.dao.executeUpdate("update GcLottery a set a.commission = :commission where a.id = :id", ImmutableMap.of("commission", commission, "id", lotteryId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int setRoomStatus(final String roomId, final String status) {
        return this.dao.executeUpdate("update GcRoom a set a.status =:status where a.id =:roomId", ImmutableMap.of("status", status, "roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int moneyDown(final Integer uid, final Double money) {
        return this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) - :money,a.exp=coalesce(exp,0)+:exp where a.id=:uid and a.money>=:money", ImmutableMap.of("money", money, "exp", money, "uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int moneyUp(final Integer uid, final Double money) {
        return this.dao.executeUpdate("update PubUser a set a.money = coalesce(a.money,0) + :money where a.id=:uid ", ImmutableMap.of("money", money, "uid", uid));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int waterDown(final String roomId, final Double money) {
        return this.dao.executeUpdate("update GcRoom a set a.sumFee = coalesce(a.sumFee,0) - :money where a.id=:roomId ", ImmutableMap.of("money", money, "roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public int waterUp(final String roomId, final Double money) {
        return this.dao.executeUpdate("update GcRoom a set a.sumFee = coalesce(a.sumFee,0) + :money,a.sumPack = coalesce(a.sumPack,0)+1 where a.id=:roomId ", ImmutableMap.of("money", money, "roomId", roomId));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void saveLotteryDetail(final Lottery lottery, final LotteryDetail detail) {
        final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
        gcLotteryDetail.setLotteryid(lottery.getId());
        this.dao.save(GcLotteryDetail.class, gcLotteryDetail);
        final BigDecimal money = detail.getCoin();
        this.dao.executeUpdate("update PubUser set money=money+:money where id = :uid", ImmutableMap.of("money", money.doubleValue(), "uid", detail.getUid()));
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void saveHumanLotteryDetail(final GcHumanLotteryDetail detail) {
    	this.dao.save(GcHumanLotteryDetail.class, detail);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public BigDecimal giftLotteryExpired(final Lottery lottery) {
        final Integer sender = lottery.getSender();
        if (sender <= 0) {
            return null;
        }
        final Map<Integer, LotteryDetail> dts = lottery.getDetail();
        final Collection<LotteryDetail> c = dts.values();
        BigDecimal bd = lottery.getMoney();
        for (final LotteryDetail ld : c) {
            bd = bd.subtract(ld.getCoin());
        }
        this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :rest where a.id = :uid ", ImmutableMap.of("rest", bd.doubleValue(), "uid", sender));
        this.dao.executeUpdate("update GcLottery a set a.status = '2' where a.id = :id and a.status = '0'", ImmutableMap.of("id", lottery.getId()));
        return bd;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void changeMoney(final Map<Integer, Double> data) {
        for (final Integer uid : data.keySet()) {
            final Double v = data.get(uid);
            this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :money where a.id = :uid ", ImmutableMap.of("money", v, "uid", uid));
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public void createLottery(final Lottery lottery, final double deposit) {
        final Integer uid = lottery.getSender();
        final GcLottery gcLottery = BeanUtils.map(lottery, GcLottery.class);
        this.dao.save(GcLottery.class, gcLottery);
        this.moneyDown(uid, deposit);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public Lottery loadLottery(final String lotteryId) {
        final GcLottery gcLottery = this.dao.get(GcLottery.class, lotteryId);
        if (gcLottery == null) {
            return null;
        }
        final Lottery lottery = BeanUtils.map(gcLottery, Lottery.class);
        final List<GcLotteryDetail> detailList = this.dao.findByProperty(GcLotteryDetail.class, "lotteryId", lotteryId);
        for (final GcLotteryDetail gld : detailList) {
            final LotteryDetail detail = BeanUtils.map(gld, LotteryDetail.class);
            lottery.addDetail(detail);
        }
        lottery.setStatus(gcLottery.getStatus());
        return lottery;
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public List<GcLottery> loadRecentLottery(final String roomId) {
        final List<GcLottery> lotteries = this.dao.findByHqlPaging("from GcLottery where  roomId =:roomId and status<>0 order by id desc", ImmutableMap.of("roomId", roomId), 10, 1);
        return lotteries;
    }
    
    @Transactional
	public int getMineCount(String lotteryId, Integer senderId) {
		final List<GcLotteryDetail> list = this.dao.findByHql("from GcLotteryDetail where lotteryid =:lotteryId and desc1 = '中雷' and uid !=:uid", ImmutableMap.of("lotteryId", lotteryId, "uid", senderId));
		if (list == null) {
			return 0;
		} 
		return list.size();
	}
    
    @Transactional(rollbackFor = { Throwable.class })
    public void updateHumanLotteryDetail(String lotteryId, int mineCount) {
    	final List<GcHumanLotteryDetail> detailList = this.dao.findByProperty(GcHumanLotteryDetail.class, "lotteryid", lotteryId);
    	if (detailList != null && detailList.size() >= 1) {
    		for (GcHumanLotteryDetail detail : detailList ) {
    			if (detail.getType() == GcHumanLotteryDetail.OPER_ISSUE_PACKET) {
    	    		double deposit = detail.getDeposit();
    	    		double addback = mineCount * deposit;
    	    		double inoutNum = addback - deposit;
    	    		this.dao.executeUpdate("update GcHumanLotteryDetail set addback=:addback , inoutNum =:inoutNum where id =:id", ImmutableMap.of("addback", addback, "inoutNum", inoutNum, "id", detail.getId())); 				
    	    		break;
    			}
    		}
    	}
    }
    
    @Transactional
    public void test() {
        final List<Double> l = this.dao.findByHql("select sum(money)+(select sum(sumFee) from GcRoom ) from PubUser");
        System.out.println("站内总金额:" + l.get(0));
    }
    
    @Transactional
    public void setBackWater(final Integer uid, final Double water) {
        if (water <= 0.0) {
            return;
        }
        PubUser u = this.dao.get(PubUser.class, uid);
        if (u.getParent() == null || u.getParent() <= 0) {
            return;
        }
        for (int i = 1; i <= 3; ++i) {
            final PubUser parent = this.dao.get(PubUser.class, u.getParent());
            if (parent == null) {
                return;
            }
            final Double rate = Double.valueOf(SystemConfigService.getValue("w_" + i));
            this.dao.executeUpdate("update PubUser a set a.money =coalesce(a.money,0) + :money where a.id = :uid ", ImmutableMap.of("money", (water * rate), "uid", parent.getId()));
            if (parent.getParent() == null || parent.getParent() <= 0) {
                return;
            }
            u = parent;
        }
    }
}

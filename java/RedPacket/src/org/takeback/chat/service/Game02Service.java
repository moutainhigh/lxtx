// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.util.BeanUtils;
import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.entity.PubUser;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import org.takeback.chat.lottery.LotteryDetail;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.lottery.Lottery;
import org.takeback.chat.store.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.service.BaseService;

@Service("game02Service")
public class Game02Service extends BaseService {
	@Autowired
	private RoomStore roomStore;
	@Autowired
	private UserStore userStore;
	@Autowired
	private GameMonitor monitor;
	@Autowired
	private UserService userService;
	
	private static Logger logger = LoggerFactory.getLogger((Class)Game02Service.class);

	@Transactional
	public void returnMasterLoteryMoney(final Lottery lottery, final double deposit) {
		final Map<Integer, LotteryDetail> detail = lottery.getDetail();
		final Iterator itr = detail.keySet().iterator();
		StringBuffer hql = new StringBuffer("update PubUser a set a.money = COALESCE(a.money,0)+:money where a.id in(");
		StringBuilder sb = new StringBuilder();
		while (itr.hasNext()) {
			final Integer uid = (Integer) itr.next();
			sb.append(uid).append(",");
			this.dao.executeUpdate(
					"update GcLotteryDetail a set  a.addback =:addback,desc1='抢庄' where a.lotteryid = :lotteryid and a.uid =:uid",
					ImmutableMap.of("addback", deposit, "lotteryid", lottery.getId(), "uid", uid));
		}
		if (sb.length() > 0) {
			sb = sb.deleteCharAt(sb.length() - 1);
			hql = hql.append((CharSequence) sb).append(")");
			this.dao.executeUpdate(hql.toString(), ImmutableMap.of("money", deposit));
		}
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void saveDetail(final Lottery lottery, final LotteryDetail detail, final double deposit,
			final String gameType) {
		final GcLotteryDetail gcLotteryDetail = BeanUtils.map(detail, GcLotteryDetail.class);
		gcLotteryDetail.setLotteryid(lottery.getId());
		gcLotteryDetail.setGameType(gameType);
		gcLotteryDetail.setDeposit(deposit);
		gcLotteryDetail.setDesc1("牛" + NumberUtil.getPoint(detail.getCoin()));
		gcLotteryDetail.setRoomId(lottery.getRoomId());
		gcLotteryDetail.setMasterId(lottery.getSender());
		
		PubUser pUser = userService.getById(detail.getUid());
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
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void changeMoney(final Map<Integer, Double> data, final Lottery lottery, final double deposit,
			final double masterDeposit, final double water) {
		final Room r = this.roomStore.get(lottery.getRoomId());
		long time1 = System.currentTimeMillis();
		for (final Map.Entry<Integer, Double> en : data.entrySet()) {
			final Integer uid = en.getKey();
			final Double v = en.getValue();
			final Double exp = Math.abs(en.getValue());
			this.dao.executeUpdate(
					"update PubUser a set a.money =a.money + :money,a.exp=coalesce(a.exp,0)+:exp where a.id = :uid ",
					ImmutableMap.of("money", v, "exp", exp, "uid", uid));
			double myDeposit = deposit;
			if (uid.equals(lottery.getSender())) {
				myDeposit = masterDeposit;
			}
			final Double inout = NumberUtil.round(v - myDeposit);
			this.monitor.setData(lottery.getRoomId(), uid, inout);
			this.dao.executeUpdate(
					"update GcLotteryDetail a set  a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid",
					ImmutableMap.of("addback", v, "inoutNum", inout, "lotteryid", lottery.getId(), "uid", uid));
			//update the human lottery detail
			this.dao.executeUpdate(
					"update GcHumanLotteryDetail a set a.addback =:addback,a.inoutNum = :inoutNum where a.lotteryid = :lotteryid and a.uid =:uid and a.type = 2",
					ImmutableMap.of("addback", v, "inoutNum", inout, "lotteryid", lottery.getId(), "uid", uid));			
		}
		logger.warn("cost time in iteration: " + (System.currentTimeMillis() - time1) + " milli seconds.");
		
		this.dao.executeUpdate(
				"update GcRoom a set a.sumFee =COALESCE(a.sumFee,0) + :water,sumPack = COALESCE(sumPack,0)+1 where a.id = :roomId ",
				ImmutableMap.of("water", water, "roomId", lottery.getRoomId()));
	}

	@Transactional(rollbackFor = { Throwable.class })
	public Double test() {
		final List<Double> l = this.dao.findByHql("select sum(money)+(select sum(sumFee) from GcRoom ) from PubUser");
		return l.get(0);
	}

	@Transactional(rollbackFor = { Throwable.class })
	public void gameStop(final Lottery lottery) {
		this.dao.executeUpdate("update GcRoom a set a.status=0 where id=:id",
				ImmutableMap.of("id", lottery.getRoomId()));
		this.dao.executeUpdate("update GcLottery a set a.status=2 where id=:id",
				ImmutableMap.of("id", lottery.getId()));
	}
}

package com.lxtech.game.plaza.protocol.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lxtech.game.plaza.cache.GameCacheUtil;
import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.AnimalDialSettlementHistoryHandler;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;
import com.lxtech.game.plaza.db.model.AnimalDialSettlementHistory;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.db.model.ZodiacPair;
import com.lxtech.game.plaza.db.model.ZodiacTriple;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.util.AnimalDialSettlement;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInbound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

/**
 * This handler will also be used for handling bot requests
 * @author wangwei
 */
public class AnimalPacketHandler extends DicePacketHandler {
	public AnimalPacketHandler(WebSocketMessageInboundPool pool) {
		super(pool);
	}

	public AnimalPacketHandler(WebSocketMessageInboundPool pool, WebSocketMessageInbound inbound) {
		super(pool, inbound);
	}

	private int[] getCombinedTwoOptions(AnimalDialSettlementGroup group) {
		return new int[]{group.getCombined_two_tiandi(), group.getCombined_two_animal()};
	}
	
	private int[] getCombinedThreeOptions(AnimalDialSettlementGroup group) {
		return new int[]{group.getCombined_three_tiandi(), group.getCombined_three_wuxing(), group.getCombined_three_animal()};
	}
	
	public List<String> handleGameRequest(String request) {
		Map map = (Map) JsonUtil.convertStringToObject(request);
		Integer protocolId = ((Double) map.get("protocol")).intValue();
		if (protocolId == ProtocolConstants.C2S_PROTOCOL_QUERY_YILOU_TIPS) {
			return this.handleGameSettingRequest(map);
		}
		
		return super.handleGameRequest(request);
	}
	
	@Override
	protected void handleSingleChipset(int lotteryIndex, long amt, long userId) {
		int roundId = this.pool.getActiveRound().getRoundNo();
		AnimalDialSettlementGroup group = GameCacheUtil.getAnimalGroupCache().get((long)roundId);
		if (group != null && AnimalDialSettlement.getOptionMaxBetCount(getCombinedTwoOptions(group), getCombinedThreeOptions(group), this.pool.getActiveRound().getCoins(),
				this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
		{
			AnimalDialSettlementHistory history = new AnimalDialSettlementHistory();
			history.setAmount((int)amt);
			history.setCreate_time(new Timestamp(System.currentTimeMillis()));
			history.setGroup_id(this.pool.getActiveRound().getRoundNo());
			history.setTarget(lotteryIndex);
			history.setUser_id((int)userId);
			
			try {
				AnimalDialSettlementHistoryHandler.insert(history);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			long[] chiparr = this.pool.getActiveRound().getChipstat();
			if (chiparr == null) {
				chiparr = new long[21];
				for (int i = 0; i < chiparr.length; i++) {
					chiparr[i] = 0;
				}
			} else {
				chiparr[lotteryIndex - 1] += amt; 
			}
			this.pool.getActiveRound().setChipstat(chiparr);
			GameUtil.handleChipset(this.pool, userId, (int)amt, lotteryIndex);				
		}
	}

	@Override
	protected List<String> handleChipsetReq(Map map) {
		if (this.inbound != null && this.inbound.getUserId() > 0 ) {
			map.put("userId", Double.valueOf(this.inbound.getUserId()+""));
		}
		
		if (this.pool.getActiveRound().getStep() == 2) {
			int userId = GameUtil.getMapInfo(map, "userId");
			int lotteryIndex = GameUtil.getMapInfo(map, "lotteryIndex");
			int amt = GameUtil.getMapInfo(map, "num");
			try {
				GameUser user = GameUserLoginHandler.getGameUser(userId);
				if (user.getCarry_amount() < amt) {
					//balance not enough
					return Lists.newArrayList();
				}
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
			
			int roundId = this.pool.getActiveRound().getRoundNo();
			AnimalDialSettlementGroup group = GameCacheUtil.getAnimalGroupCache().get((long)roundId);
			
			//check whether this is accepted
			if (AnimalDialSettlement.getOptionMaxBetCount(getCombinedTwoOptions(group), getCombinedThreeOptions(group), this.pool.getActiveRound().getCoins(),
					this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
			{
				AnimalDialSettlementHistory history = new AnimalDialSettlementHistory();
				history.setAmount(amt);
				history.setCreate_time(new Timestamp(System.currentTimeMillis()));
				history.setGroup_id(this.pool.getActiveRound().getRoundNo());
				history.setTarget(lotteryIndex);
				history.setUser_id((int)userId);
				
				try {
					AnimalDialSettlementHistoryHandler.insert(history);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				long[] chiparr = this.pool.getActiveRound().getChipstat();
				if (chiparr == null) {
					chiparr = new long[21];
					for (int i = 0; i < chiparr.length; i++) {
						chiparr[i] = 0;
					}
				} else {
					chiparr[lotteryIndex - 1] += amt; 
				}
				this.pool.getActiveRound().setChipstat(chiparr);
				GameUtil.handleChipset(this.pool, userId, amt, lotteryIndex);				
			}
		}

		return Lists.newArrayList();
	}
	
	private static class AnimalOpenHistory {
		private long open_index;
		private int room_id;
		private int result_td;
		private int result_wx;
		private int result_sx;
		private int result_lm_td;
		private int result_lm_sx;
		private int result_sm_td;
		private int result_sm_wx;
		private int result_sm_sx;
		private String time;

		public long getOpen_index() {
			return open_index;
		}

		public void setOpen_index(long open_index) {
			this.open_index = open_index;
		}

		public int getRoom_id() {
			return room_id;
		}

		public void setRoom_id(int room_id) {
			this.room_id = room_id;
		}

		public int getResult_td() {
			return result_td;
		}

		public void setResult_td(int result_td) {
			this.result_td = result_td;
		}

		public int getResult_wx() {
			return result_wx;
		}

		public void setResult_wx(int result_wx) {
			this.result_wx = result_wx;
		}

		public int getResult_sx() {
			return result_sx;
		}

		public void setResult_sx(int result_sx) {
			this.result_sx = result_sx;
		}

		public int getResult_lm_td() {
			return result_lm_td;
		}

		public void setResult_lm_td(int result_lm_td) {
			this.result_lm_td = result_lm_td;
		}

		public int getResult_lm_sx() {
			return result_lm_sx;
		}

		public void setResult_lm_sx(int result_lm_sx) {
			this.result_lm_sx = result_lm_sx;
		}

		public int getResult_sm_td() {
			return result_sm_td;
		}

		public void setResult_sm_td(int result_sm_td) {
			this.result_sm_td = result_sm_td;
		}

		public int getResult_sm_wx() {
			return result_sm_wx;
		}

		public void setResult_sm_wx(int result_sm_wx) {
			this.result_sm_wx = result_sm_wx;
		}

		public int getResult_sm_sx() {
			return result_sm_sx;
		}

		public void setResult_sm_sx(int result_sm_sx) {
			this.result_sm_sx = result_sm_sx;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}
	
	protected List<String> queryOpenHistory() {
		List<String> responseList = Lists.newArrayList();
		try {
			List<AnimalDialSettlementGroup> asgList = AnimalDialSettlementGroupHandler.queryPrevious();
			AnimalOpenHistory[] history = new AnimalOpenHistory[10];
			String[] jsonHistory = new String[10];
			int i = 0;
			for (AnimalDialSettlementGroup asg : asgList) {
				history[i] = new AnimalOpenHistory();
				history[i].setOpen_index((long)asg.getId());
				history[i].setRoom_id(0);
				history[i].setResult_lm_sx(asg.getCombined_two_animal());
				history[i].setResult_lm_td(asg.getCombined_two_tiandi());
				history[i].setResult_sm_sx(asg.getCombined_three_animal());
				history[i].setResult_sm_td(asg.getCombined_three_tiandi());
				history[i].setResult_sm_wx(asg.getCombined_three_wuxing());
				history[i].setResult_sx(asg.getResult_animal());
				history[i].setResult_td(asg.getResult_tiandi());
				history[i].setResult_wx(asg.getResult_wuxing());
				history[i].setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(asg.getEnd_time()));
				jsonHistory[i] = JsonUtil.convertObjToStr(history[i]);
				i++;
			}
			
			List<String> yilou_data = Lists.newArrayList();
			long curRoundNo = AnimalDialSettlementGroupHandler.getLatestSettledRound().getId(); 
			if (this.pool.getActiveRound() != null) {
				curRoundNo = this.pool.getActiveRound().getRoundNo();
			} 
			
			for (int j = 0; j < 12; j++) {
				yilou_data.add((curRoundNo - GameCacheUtil.getAaCache().get((j+1))) + "");
			} 
			AnimalDialSettlementGroup curGroup = GameCacheUtil.getAnimalGroupCache().get(curRoundNo);
			ZodiacPair pair = new ZodiacPair();
			pair.setSx(curGroup.getCombined_two_animal());
			pair.setTd(curGroup.getCombined_two_tiandi());
			int previousPair = GameCacheUtil.getApaCache().get(pair);
			yilou_data.add((curRoundNo - previousPair) + "");
			
			ZodiacTriple triple = new ZodiacTriple();
			triple.setSx(curGroup.getCombined_three_animal());
			triple.setTd(curGroup.getCombined_three_tiandi());
			triple.setWx(curGroup.getCombined_three_wuxing());
			yilou_data.add((curRoundNo - GameCacheUtil.getAtaCache().get(triple)) + "");
			
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("list", jsonHistory, "tema_yilou", yilou_data,
					"protocol", ProtocolConstants.S2C_PROTOCOL_QUERY_OPEN_HISTORY)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return responseList;
	}	
	
	@Override
	protected Map getTotalChipStat() {
		return super.getTotalChipStat();
	}
	
	@Override
	protected Map getSingleChipStat(WebSocketMessageInbound inbound) {
		//all-zero stat
		String poolName = this.pool.getPoolName();
		int targetCnt = GameUtil.getGameTargetCnt(poolName);
		Map map = new HashMap();
		if (inbound == null) { //for bots, directly return all-zero map
			for (int i = 0; i < targetCnt; i++) {
				map.put((i+1)+"", 0);
			}
		} else {
			long currentUid = this.inbound.getUserId();
			GameRound round = this.pool.getActiveRound();
			if (round != null && (round.getStep() == 2 || round.getStep() == 3)) {
				try {
					Map<Integer, Long> chipStat = AnimalDialSettlementHistoryHandler.getChipStat(round.getRoundNo(), currentUid);
					for (int i = 0; i < targetCnt; i++) {
						map.put((i+1)+"", chipStat.get(i));
					}
				} catch (SQLException e) {
				}				
			} else {
				for (int i = 0; i < targetCnt; i++) {
					map.put((i+1)+"", 0);
				}	
			}
		}
		return map;
	
	}	
	
}

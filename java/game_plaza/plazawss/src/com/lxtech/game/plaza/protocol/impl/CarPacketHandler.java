package com.lxtech.game.plaza.protocol.impl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lxtech.game.plaza.db.CarDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.CarDialSettlementHistoryHandler;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.CarDialSettlementGroup;
import com.lxtech.game.plaza.db.model.CarDialSettlementHistory;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.util.CarDialSettlement;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInbound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

/**
 * This packet handler will also be used for handling bot requests
 * @author wangwei
 */
public class CarPacketHandler extends DicePacketHandler {

	public CarPacketHandler(WebSocketMessageInboundPool pool) {
		super(pool);
	}

	public CarPacketHandler(WebSocketMessageInboundPool pool, WebSocketMessageInbound inbound) {
		super(pool, inbound);
	}
	
	protected Map getUserMap(GameUser user) {
		Map userMap = super.getUserMap(user);
		userMap.put("isNew", 0);
		userMap.put("isGM", null);
		return userMap;
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
		if (CarDialSettlement.getOptionMaxBetCount(this.pool.getActiveRound().getCoins(),
				this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
		{
			CarDialSettlementHistory history = new CarDialSettlementHistory();
			history.setAmount((int)amt);
			history.setCreate_time(new Timestamp(System.currentTimeMillis()));
			history.setGroup_id(this.pool.getActiveRound().getRoundNo());
			history.setTarget(lotteryIndex);
			history.setUser_id((int)userId);
			
			try {
				CarDialSettlementHistoryHandler.insert(history);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			long[] chiparr = this.pool.getActiveRound().getChipstat();
			if (chiparr == null) {
				chiparr = new long[8];
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
				e1.printStackTrace();
			}
			//check whether this is accepted
			if (CarDialSettlement.getOptionMaxBetCount(this.pool.getActiveRound().getCoins(),
					this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
			{
				CarDialSettlementHistory history = new CarDialSettlementHistory();
				history.setAmount(amt);
				history.setCreate_time(new Timestamp(System.currentTimeMillis()));
				history.setGroup_id(this.pool.getActiveRound().getRoundNo());
				history.setTarget(lotteryIndex);
				history.setUser_id((int)userId);
				
				try {
					CarDialSettlementHistoryHandler.insert(history);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				long[] chiparr = this.pool.getActiveRound().getChipstat();
				if (chiparr == null) {
					chiparr = new long[8];
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

	class CarOpenHistory {
		private Long open_index;
		private Integer room_id;
		private Integer result_flag;
		private Integer result;
		private String time;

		public Long getOpen_index() {
			return open_index;
		}

		public void setOpen_index(Long open_index) {
			this.open_index = open_index;
		}

		public Integer getRoom_id() {
			return room_id;
		}

		public void setRoom_id(Integer room_id) {
			this.room_id = room_id;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public Integer getResult_flag() {
			return result_flag;
		}

		public void setResult_flag(Integer result_flag) {
			this.result_flag = result_flag;
		}

		public Integer getResult() {
			return result;
		}

		public void setResult(Integer result) {
			this.result = result;
		}
	}	
	
	@Override
	protected List<String> queryOpenHistory() {
		List<String> responseList = Lists.newArrayList();
		try {
			List<CarDialSettlementGroup> csgList = CarDialSettlementGroupHandler.queryPrevious();
			CarOpenHistory[] history = new CarOpenHistory[csgList.size()];
			String[] jsonHistory = new String[csgList.size()];
			int i = 0;
			for (CarDialSettlementGroup csg : csgList) {
				history[i] = new CarOpenHistory();
				history[i].setOpen_index((long)csg.getId());
				history[i].setResult(csg.getResult());
				history[i].setResult_flag(csg.getResult_flag());
				history[i].setRoom_id(0);
				history[i].setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(csg.getEnd_time()));
				jsonHistory[i] = JsonUtil.convertObjToStr(history[i]);
				i++;
			}
			
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("list", jsonHistory, 
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
					Map<Integer, Long> chipStat = CarDialSettlementHistoryHandler.getChipStat(round.getRoundNo(), currentUid);
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
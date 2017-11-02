package com.lxtech.game.plaza.protocol.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lxtech.game.plaza.cache.GameCacheUtil;
import com.lxtech.game.plaza.cache.OnlineUserCache;
import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.DiceSettlementGroupHandler;
import com.lxtech.game.plaza.db.DiceSettlementHistoryHandler;
import com.lxtech.game.plaza.db.GUserHandler;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.ResultStatisticsHandler;
import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;
import com.lxtech.game.plaza.db.model.ChipsetRequest;
import com.lxtech.game.plaza.db.model.DiceSettlementGroup;
import com.lxtech.game.plaza.db.model.DiceSettlementHistory;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.db.model.GameUserLogin;
import com.lxtech.game.plaza.net.KestrelConnector;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.db.model.ResultStatistics;
import com.lxtech.game.plaza.protocol.AbstractGamePacketHandler;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.timeline.GameRoundProceeder;
import com.lxtech.game.plaza.util.DiceSettlement;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInbound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class DicePacketHandler extends AbstractGamePacketHandler {
	
	protected static final Logger logger = LoggerFactory.getLogger(DicePacketHandler.class);

	public DicePacketHandler(WebSocketMessageInboundPool pool, WebSocketMessageInbound inbound) {
		super(pool, inbound);
	}
	
	public DicePacketHandler(WebSocketMessageInboundPool pool) {
		super(pool);
	}	

	@Override
	public List<String> handleGameRequest(String request) {
		Map map = (Map) JsonUtil.convertStringToObject(request);
		Integer protocolId = ((Double) map.get("protocol")).intValue();
		String game = this.pool.getPoolName(); 
		
		if (protocolId == null) {
			return Lists.newArrayList();
		} else {
			List<String> responseList = Lists.newArrayList();
			switch (protocolId) {
			case ProtocolConstants.C2S_PROTOCOL_LOGIN:
				logger.info("handle login request:" + request);
				responseList = handleLoginRequest(map);
				break;

			case ProtocolConstants.C2S_PROTOCOL_GAME_STATE:
				responseList = handleGameSettingRequest(map); 
				break;				
				
			case ProtocolConstants.C2S_PROTOCOL_QUERY_GAME_SETTING:
				logger.info("handle query game setting:" + request);
				responseList = handleGameSettingRequest(map);
				break;
			case ProtocolConstants.C2S_PROTOCOL_TICK:
				if (game.equals(NetConstants.CHIP_QUEUE) || game.equals(NetConstants.ANIMAL_CHIP_QUEUE)) {
					handleTick(map);
				} else {
					handleReliefReq(map);
				}
				break;
			
			case ProtocolConstants.C2S_PROTOCOL_TICK_2:
				handleTick(map);
				break;

			case ProtocolConstants.C2S_PROTOCOL_RELIEF:
				logger.info("handle relief :" + request);
				responseList = handleReliefReq(map);
				break;
			
			case ProtocolConstants.C2S_PROTOCOL_SET_CHIPS:
				logger.info("handle set chips request :" + request);
				responseList = handleChipsetReq(map);
				break;
				
			case ProtocolConstants.C2S_PROTOCOL_GET_OR_SAVE_MONEY:
				logger.info("handle get or save money request :" + request);
				responseList = handleGetOrSaveMoneyRequest(map);
				break;
			case ProtocolConstants.C2S_PROTOCOL_CHAT:
				logger.info("handle chat request :" + request);
				responseList = handleChatRequest(map);
				break;
			case ProtocolConstants.C2S_PROTOCOL_QUERY_OPEN_HISTORY:
				logger.info("handle open history request :" + request);
				responseList = queryOpenHistory();
				break;
				
			case ProtocolConstants.C2S_PROTOCOL_REQUEST_MASTER:
				logger.info("handle request master request:" + request);
				responseList = handleMasterReq(map);
				break;
				
			case ProtocolConstants.C2S_PROTOCOL_GET_MASTER_LIST:
				logger.info("handle get master request:" + request);
				responseList = handleGetMasterListReq(map);
				break;
				
			case ProtocolConstants.C2S_PROTOCOL_CONTINE:
				logger.info("handle batch chipset request:" + request);
				responseList = handleContinueReq(map);
				break;
				
			default:
				break;
			}
			return responseList;
		}
	}

	protected void handleSingleChipset(int lotteryIndex, long amt, long userId) {
		if (DiceSettlement.getOptionMaxBetCount(this.pool.getActiveRound().getCoins(), this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
		{
			DiceSettlementHistory history = new DiceSettlementHistory();
			history.setAmount((int)amt);
			history.setCreate_time(new Timestamp(System.currentTimeMillis()));
			history.setGroup_id(this.pool.getActiveRound().getRoundNo());
			history.setTarget(lotteryIndex);
			history.setUser_id((int)userId);
			
			try {
				DiceSettlementGroupHandler.saveDiceRecord(history);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			long[] chiparr = this.pool.getActiveRound().getChipstat();
			if (chiparr == null) {
				chiparr = new long[35];
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
	
	private List<String> handleContinueReq(Map map) {
		List<String> responseList = Lists.newArrayList();
		if (this.pool.getActiveRound().getStep() == 2) {
			//{"protocol":1107,"arrSetChips":[null,500,1000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}
			List<Double> chipList = (List<Double>)map.get("arrSetChips");
			Map<Integer, Long> realChipRequest = new HashMap<Integer, Long>();
			
			int index = 0;
			long totalChips = 0;
			for (Double d : chipList) {
				if (d == null) {
					index++;
					continue;
				} else {
					long chipCnt = d.longValue();
					if (chipCnt > 0) {
						realChipRequest.put(index, chipCnt);
						totalChips += chipCnt;
					}
				}
				index++;
			}
			
			long userId = this.inbound.getUserId();
			try {
				GameUser gUser = GameUserLoginHandler.getGameUser(userId);
				if (gUser.getCarry_amount() < totalChips) {
					return responseList;
				}
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			
			if(realChipRequest.keySet().size() > 0) {
				for (Integer lotteryIndex : realChipRequest.keySet()) {
					logger.info("in handle single chipset:"+lotteryIndex + " " + realChipRequest.get(lotteryIndex));
					this.handleSingleChipset(lotteryIndex, realChipRequest.get(lotteryIndex), this.inbound.getUserId());
				}
			}
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("protocol", ProtocolConstants.S2C_PROTOCOL_CONTINE_SUCC)));
		}

		return responseList;
	}

	protected List<String> handleGetMasterListReq(Map map) {
		List<String> masterIdList = this.pool.getMasterList();
		List<Map> masterList = Lists.newArrayList();
		for (String uid: masterIdList) {
			GameUser user = GameCacheUtil.getUserInfoCache().get(Long.valueOf(uid));
			if (user != null) {
				masterList.add(this.getUserMap(user));
			}
		}
		
		Map responseMap = ImmutableMap.of("protocol", ProtocolConstants.S2C_PROTOCOL_MASTER_LIST, "users", masterList);
		return ImmutableList.of(JsonUtil.convertObjToStr(responseMap));
	}
	
	protected List<String> handleMasterReq(Map map) {
		Integer isUp = ((Double)map.get("up")).intValue();
		int state = 0;
		if (isUp == 1) {
			if (map.containsKey("userId")) { //from robot
				state = this.pool.receiveMasterReq(GameUtil.getMapInfo(map, "userId"));
			} else {
				state = this.pool.receiveMasterReq(this.inbound.getUserId());
			}
		} else {
			long uid = 0;
			if (map.containsKey("userId")) {
				uid = GameUtil.getMapInfo(map, "userId");
			} else {
				uid = this.inbound.getUserId();
			}
			
			if (this.pool.getActiveRound().getMasterId() != uid) {
				this.pool.removeMaster(uid);
				state = NetConstants.REQEST_MASTER_STATE_DOWN_SUCCESS;
			} else {
				if (this.pool.getActiveRound().getStep() > 1) {
					state = NetConstants.REQEST_MASTER_STATE_NOT_IN_START_STATE;
				} else {
					String message = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3002, "masterId", this.inbound.getUserId()));
					String outQueueName = GameUtil.getQueueName(this.pool.getPoolName(), NetConstants.MAIN_SEQ_QUEUE_OUT);
					KestrelConnector.enqueue(outQueueName, message);					
					state = NetConstants.REQEST_MASTER_STATE_DOWN_SUCCESS;
				}
			}
		}
		
		return ImmutableList.of(JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER)));
	}

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
			if (DiceSettlement.getOptionMaxBetCount(this.pool.getActiveRound().getCoins(), this.pool.getActiveRound().getChipstat(), lotteryIndex) >= amt)
			//if(true)
			{
				DiceSettlementHistory history = new DiceSettlementHistory();
				history.setAmount(amt);
				history.setCreate_time(new Timestamp(System.currentTimeMillis()));
				history.setGroup_id(this.pool.getActiveRound().getRoundNo());
				history.setTarget(lotteryIndex);
				history.setUser_id((int)userId);
				
				try {
					DiceSettlementGroupHandler.saveDiceRecord(history);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				long[] chiparr = this.pool.getActiveRound().getChipstat();
				if (chiparr == null) {
					chiparr = new long[35];
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

//	{"protocol":1106,"getNum":0,"saveNum":3177}
//	{"code":1,"msg":"success","chips":0,"money":3177,"protocol":2009}
	
	//TODO
	// {"protocol":1602}
	// {"list":["{\"open_index\":59264,\"room_id\":0,\"result_num_1\":5,\"result_num_2\":6,\"result_num_3\":6,\"time\":\"2017-01-06
	// 16:36:12\"}","{\"open_index\":59263,\"room_id\":0,\"result_num_1\":6,\"result_num_2\":2,\"result_num_3\":3,\"time\":\"2017-01-06
	// 16:35:17\"}","{\"open_index\":59262,\"room_id\":0,\"result_num_1\":5,\"result_num_2\":4,\"result_num_3\":6,\"time\":\"2017-01-06
	// 16:34:23\"}","{\"open_index\":59261,\"room_id\":0,\"result_num_1\":6,\"result_num_2\":1,\"result_num_3\":1,\"time\":\"2017-01-06
	// 16:33:29\"}","{\"open_index\":59260,\"room_id\":0,\"result_num_1\":2,\"result_num_2\":6,\"result_num_3\":4,\"time\":\"2017-01-06
	// 16:32:34\"}","{\"open_index\":59259,\"room_id\":0,\"result_num_1\":5,\"result_num_2\":3,\"result_num_3\":1,\"time\":\"2017-01-06
	// 16:31:40\"}","{\"open_index\":59258,\"room_id\":0,\"result_num_1\":4,\"result_num_2\":6,\"result_num_3\":6,\"time\":\"2017-01-06
	// 16:30:45\"}","{\"open_index\":59257,\"room_id\":0,\"result_num_1\":6,\"result_num_2\":3,\"result_num_3\":5,\"time\":\"2017-01-06
	// 16:29:51\"}","{\"open_index\":59256,\"room_id\":0,\"result_num_1\":2,\"result_num_2\":3,\"result_num_3\":2,\"time\":\"2017-01-06
	// 16:28:57\"}","{\"open_index\":59255,\"room_id\":0,\"result_num_1\":3,\"result_num_2\":5,\"result_num_3\":5,\"time\":\"2017-01-06
	// 16:28:02\"}"],"yilou_data":["13","51","22","8","3","5","10","1","4","9","7","2","6","0","233","309","1816","208","186","616","3","51","11","10","9","0"],"protocol":2011}
	protected List<String> queryOpenHistory() {
		List<String> responseList = Lists.newArrayList();
		try {
			List<DiceSettlementGroup> dsgList = DiceSettlementGroupHandler.queryPrevious();
			OpenHistory[] history = new OpenHistory[10];
			String[] jsonHistory = new String[10];
			int i = 0;
			for (DiceSettlementGroup dsg : dsgList) {
				history[i] = new OpenHistory();
				String[] result = dsg.getResult().split(",");
				history[i].setOpen_index(dsg.getId());
				history[i].setResult_num_1(Integer.valueOf(result[0]));
				history[i].setResult_num_2(Integer.valueOf(result[1]));
				history[i].setResult_num_3(Integer.valueOf(result[2]));
				history[i].setRoom_id(0);
				history[i].setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dsg.getEnd_time()));
				jsonHistory[i] = JsonUtil.convertObjToStr(history[i]);
				i++;
			}
			
			String[] yilou_data = new String[26];
			long current_index = history[0].getOpen_index();
			i = 0;
			List<ResultStatistics> add_stat = ResultStatisticsHandler.queryForAdd();
			for (i = 0; i < 14; i++) {
				yilou_data[i] = (int)(current_index - add_stat.get(i).getLast_index()) + "";
			}

			List<ResultStatistics> tripple_stat = ResultStatisticsHandler.queryForTripple();
			for (; i < 20; i++) {
				yilou_data[i] = (int)(current_index - tripple_stat.get(i - 14).getLast_index()) + "";
			}

			List<ResultStatistics> double_stat = ResultStatisticsHandler.queryForDouble();
			for (; i < 26; i++) {
				yilou_data[i] = (int)(current_index - double_stat.get(i - 20).getLast_index()) + "";
			}

			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("list", jsonHistory, "yilou_data", yilou_data,
					"protocol", ProtocolConstants.S2C_PROTOCOL_QUERY_OPEN_HISTORY)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return responseList;
	}

	// {"protocol":1104,"msg":"bilibil"}
	// {"userName":"\u73a9\u5bb68253791","msg":"bilibil","protocol":2013}
	private List<String> handleChatRequest(Map map) {
		List<String> responseList = Lists.newArrayList();
		long userId = this.inbound.getUserId();
		GameUser user = GameCacheUtil.getUserInfoCache().get(userId);
		String msg = (String) map.get("msg");
		String message = JsonUtil.convertObjToStr(ImmutableMap.of("protocol", ProtocolConstants.S2C_PROTOCOL_CHAT,
				"userName", user.getWxnm(), "msg", msg));
		this.pool.sendMessage(message);
		return responseList;
	}

	public class OpenHistory {
		private Long open_index;
		private Integer room_id;
		private Integer result_num_1;
		private Integer result_num_2;
		private Integer result_num_3;
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

		public Integer getResult_num_1() {
			return result_num_1;
		}

		public void setResult_num_1(Integer result_num_1) {
			this.result_num_1 = result_num_1;
		}

		public Integer getResult_num_2() {
			return result_num_2;
		}

		public void setResult_num_2(Integer result_num_2) {
			this.result_num_2 = result_num_2;
		}

		public Integer getResult_num_3() {
			return result_num_3;
		}

		public void setResult_num_3(Integer result_num_3) {
			this.result_num_3 = result_num_3;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

	}

	// {"protocol":1106,"getNum":0,"saveNum":3177}
	// {"code":1,"msg":"success","chips":0,"money":3177,"protocol":2009}

	private List<String> handleGetOrSaveMoneyRequest(Map map) {
		List<String> responseList = Lists.newArrayList();
		BigDecimal getNum = new BigDecimal((Double) map.get("getNum"));// balance->carryAmount
		BigDecimal saveNum = new BigDecimal((Double) map.get("saveNum"));// carryAmount->balance
		long userId = this.inbound.getUserId();

		try {
			GameUser user = GUserHandler.queryById(userId);
			BigDecimal balance = user.getBalance();
			long carryAmount = user.getCarry_amount();
			if (getNum.compareTo(BigDecimal.ZERO) != 0) {
				balance = balance.subtract(getNum.divide(new BigDecimal(10000)));
				carryAmount = carryAmount + getNum.longValue();
			} else if (saveNum.compareTo(BigDecimal.ZERO) != 0) {
				balance = balance.add(saveNum.divide(new BigDecimal(10000)));
				carryAmount = carryAmount - saveNum.longValue();
			}

			user.setBalance(balance);
			user.setCarry_amount(carryAmount);
			GUserHandler.updatejBalanceById(user);
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("code", 1, "msg", "success", "chips", carryAmount,
					"money", balance.multiply(new BigDecimal(10000)).longValue(), "protocol",
					ProtocolConstants.S2C_PROTOCOL_GET_OR_SAVE_MONEY)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return responseList;
	}

	protected List<String> handleReliefReq(Map map) {
		List<String> responseList = Lists.newArrayList();
		long uid = this.inbound.getUserId();
		if (uid > 0) {
			try {
				GameUser user = GameUserLoginHandler.getGameUser(uid);
				if (user.getCarry_amount() < 1000 && user.getBalance().doubleValue() < 0.1) {
					long reliefCnt = GameUserLoginHandler.getReliefCountByDay(uid, new Date());
					if (reliefCnt < 10) {
						long chipCount = user.getCarry_amount()+3000;
						GameUserLoginHandler.updateUserChips(uid, chipCount);
						GameUserLoginHandler.saveReliefRecord(uid, chipCount);
						responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("chips", chipCount, "reliefCount", reliefCnt+1, "protocol", ProtocolConstants.S2C_PROTOCOL_RELIEF)));						
					}
				} 
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return responseList;
	}

	private void handleTick(Map map) {
		long uid = this.inbound.getUserId();
		if (uid > 0) {
			this.pool.setUserConnMapping(uid, this.inbound.getConnId());
		}
	}
	
	protected List<String> handleGameSettingRequest(Map map) {
		List<String> responseList = Lists.newArrayList();
		GameRound round = this.pool.getActiveRound();
		if (round == null || round.getMasterId() == 0) {
			responseList.add(JsonUtil.convertObjToStr(
					ImmutableMap.of("state", 0, "interval", 0, "protocol", ProtocolConstants.S2C_PROTOCOL_GAME_STATE)));
			Map userMap = ImmutableMap.of("id", 0, "name", ProtocolConstants.NO_MASTER, "chips", 0, "money", 0);
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("state", 0, "user", userMap, "remainCount", 0,
					"score", 0, "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER)));
			// add the result for the previous round
			String previousResult = GameUtil.getResultForPreviousRound(this.pool.getPoolName());
			if (!Strings.isNullOrEmpty(previousResult)) {
				responseList.add(previousResult);
			}
		} else {
			logger.info("master id is :" + round.getMasterId());
			int discloseTimespan = GameUtil.getSettlementTimeSpan(this.pool.getPoolName());
			
			long startTime = round.getStartTime();
			long gap = System.currentTimeMillis() - startTime;
			int state = 0;
			int secRemained = 0;
			if (gap > 1000 && gap < 6000) {
				state = 1;
				secRemained = (int) ((6000 - gap) / 1000);
			} else if (gap >= 6000 && gap < 36000) {
				state = 2;
				secRemained = (int) ((36000 - gap) / 1000);
			} else if (gap >= 36000) {
				state = 3;
				long stopTimespan = (36 + discloseTimespan)*1000;
				secRemained = (int) ((stopTimespan - gap) / 1000);
			}
			
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "interval", secRemained,
					"protocol", ProtocolConstants.S2C_PROTOCOL_GAME_STATE)));
			// add information for current master
			// {"user":{"id":101146,"name":"5113600mm","chips":20760449,"money":"1979564454","reliefCount":0},"state":4,"remainCount":11,"score":1882753,"protocol":2004}
			long uid = round.getMasterId();
			try {
				GameUser user = GameUserLoginHandler.getGameUser(uid);
//				Map userMap = ImmutableMap.of("id", uid, "name", user.getWxnm(), "chips", round.getCoins(),
//						"money", GameUtil.convertBalanceToCoin(user.getBalance().doubleValue()), "reliefCount", 0);
				Map userMap = getUserMap(user);
				userMap.put("chips", round.getCoins());
				responseList.add(JsonUtil.convertObjToStr(
						ImmutableMap.of("state", 4, "user", userMap, "remainCount", round.getRemainCount(), "score", round.getScore(),
								"protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER)));

				// {"result_1_num":3,"result_2_num":6,"result_3_num":4,"open_index":57906,"protocol":2005}
				// add the result for the previous round
				String previousResult = GameUtil.getResultForPreviousRound(this.pool.getPoolName());
				if (!Strings.isNullOrEmpty(previousResult)) {
					responseList.add(previousResult);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    
		Map total = getTotalChipStat();
		Map single = getSingleChipStat(this.inbound);
		
		responseList.add(JsonUtil.convertObjToStr(
				ImmutableMap.of("total", total, "single", single, "protocol", ProtocolConstants.S2C_PROTOCOL_SETTED_CHIPS)));
		
		if (this.pool.getPoolName().equals(NetConstants.CHIP_QUEUE)) {
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("bgMusic", 1, "music", 1, "protocol",
					ProtocolConstants.S2C_PROTOCOL_QUERY_GAME_SETTING)));			
		} else { //for car application, yilou tips is used for game setting
			if(this.pool.getPoolName().equals(NetConstants.ANIMAL_CHIP_QUEUE)) {
				//{"tm_sm_td":19,"tm_sm_wx":15,"tm_sm_sx":1,"tm_lm_td":19,"tm_lm_sx":1,"protocol":2006}
				try {
					AnimalDialSettlementGroup group = AnimalDialSettlementGroupHandler.getLatestSettledRound();
					Map paramMap = new HashMap();
					paramMap.put("tm_sm_td", group.getCombined_three_tiandi());
					paramMap.put("tm_sm_wx", group.getCombined_three_wuxing());
					paramMap.put("tm_sm_sx", group.getCombined_three_animal());
					paramMap.put("tm_lm_td", group.getCombined_two_tiandi());
					paramMap.put("tm_lm_sx", group.getCombined_two_animal());
					paramMap.put("protocol", ProtocolConstants.S2C_PROTOCOL_TE_MA);
					responseList.add(JsonUtil.convertObjToStr(paramMap));
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("bgMusic", 1, "music", 1, "protocol",
					ProtocolConstants.S2C_PROTOCOL_QUERY_YILOU_TIPS)));
		}

		return responseList;
	}
	
	protected Map getTotalChipStat() {
		GameRound round = this.pool.getActiveRound();
		//all-zero stat
		String poolName = this.pool.getPoolName();
		int targetCnt = GameUtil.getGameTargetCnt(poolName);
		
		Map map = new HashMap();
		if (round == null) {
			for (int i = 0; i < targetCnt; i++) {
				map.put((i+1)+"", 0);
			}
		} else {
			if (round.getStep() == 2 || round.getStep() == 3) {
				for (int i = 0; i < targetCnt; i++) {
					map.put((i+1)+"", round.getChipstat()[i]);
				}
			} else {
				for (int i = 0; i < targetCnt; i++) {
					map.put((i+1)+"", 0);
				}							
			}
		}
		return map;
	}
	
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
					Map<Integer, Long> chipStat = DiceSettlementHistoryHandler.getChipStat(round.getRoundNo(), currentUid);
					for (int i = 0; i < targetCnt; i++) {
						map.put((i+1)+"", chipStat.get(i));
					}
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}				
			} else {
				for (int i = 0; i < targetCnt; i++) {
					map.put((i+1)+"", 0);
				}	
			}
		}
		return map;
	}
	

	private List<String> handleLoginRequest(Map map) {
		List<String> resultList = Lists.newArrayList();
		String cookie = (String) map.get("cookie");
		try {
			GameUserLogin userLogin = GameUserLoginHandler.getUserLogin(cookie);
			if (userLogin != null) {
				logger.info("userLogin is :"+ userLogin.getUid() +  "   "  + System.currentTimeMillis());
				GameUser user = GameUserLoginHandler.getGameUser(userLogin.getUid());
				// {"code":1,"errMsg":"success","user":{"id":213000,"name":"\u738b\u5a01","chips":2765,"money":"0","reliefCount":3},"protocol":2001}
				long money = (long) (user.getBalance().doubleValue() * 10000);
				long coins = user.getCarry_amount();
				if (this.pool.getActiveRound() != null && user.getId() == this.pool.getActiveRound().getMasterId()) {
					coins = this.pool.getActiveRound().getCoins();
				}
//				Map userMap = ImmutableMap.of("id", user.getId(), "name", user.getWxnm(), "chips",
//						coins, "money", money);
				Map userMap = this.getUserMap(user);
				//override the number of chips
				userMap.put("chips", coins);
				logger.info("prepare user data." +  "   "  + System.currentTimeMillis());
				Map resMap = ImmutableMap.of("code", 1, "errMsg", "success", "user", userMap, "protocol", 2001);
				resultList.add(JsonUtil.convertObjToStr(resMap));
				logger.info("set conn mapping." +  "   "  + System.currentTimeMillis());
				this.pool.setUserConnMapping(user.getId(), this.inbound.getConnId());
				logger.info("set inbound user:" +  "   "  + System.currentTimeMillis());
				this.inbound.setUserId(user.getId());
				logger.info("now return the response." +  "   "  + System.currentTimeMillis());
			} else {
				logger.error("incorrect auth info:" + cookie);
				resultList.add(JsonUtil.convertObjToStr(ImmutableList.of("code", -1, "errMsg", "incorrect auth info", "cookie", cookie)));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return resultList;
	}
	
	protected Map getUserMap(GameUser user) {
		long money = (long) (user.getBalance().doubleValue() * 10000);
		Map userMap = new HashMap();
		userMap.put("id", user.getId());
		userMap.put("name", user.getWxnm());
		userMap.put("chips", user.getCarry_amount());
		userMap.put("money", money);
		
		if (user.getIs_subscribe() == NetConstants.GAME_STATUS_MASTER) {
			userMap.put("reliefCount", 0);
		} else {
			userMap.put("reliefCount", 1);
		}
		return userMap;
	}
}

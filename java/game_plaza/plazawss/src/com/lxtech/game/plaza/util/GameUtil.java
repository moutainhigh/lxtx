package com.lxtech.game.plaza.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.lxtech.game.plaza.cache.GameCacheUtil;
import com.lxtech.game.plaza.cache.OnlineUserCache;
import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.AnimalDialSettlementHistoryHandler;
import com.lxtech.game.plaza.db.CarDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.CarDialSettlementHistoryHandler;
import com.lxtech.game.plaza.db.DiceSettlementGroupHandler;
import com.lxtech.game.plaza.db.DiceSettlementHistoryHandler;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;
import com.lxtech.game.plaza.db.model.AnimalRoundDisclosure;
import com.lxtech.game.plaza.db.model.CarDialSettlementGroup;
import com.lxtech.game.plaza.db.model.CarRoundDisclosure;
import com.lxtech.game.plaza.db.model.DiceSettlementGroup;
import com.lxtech.game.plaza.db.model.DiceSettlementResult;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.db.model.RoundDisclosure;
import com.lxtech.game.plaza.db.model.SettlementSysConfig;
import com.lxtech.game.plaza.db.model.ZodiacPair;
import com.lxtech.game.plaza.db.model.ZodiacTriple;
import com.lxtech.game.plaza.net.KestrelConnector;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.protocol.AbstractGamePacketHandler;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.protocol.impl.AnimalPacketHandler;
import com.lxtech.game.plaza.protocol.impl.CarPacketHandler;
import com.lxtech.game.plaza.protocol.impl.DicePacketHandler;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class GameUtil {
	
	//system configs
	public static final String MASTER_BALANCE_DOWN_LIMIT = "_master_down_limit"; 
	
	private static final Logger logger = LoggerFactory.getLogger(GameUtil.class);

	public static long convertBalanceToCoin(double money) {
		return (long)(money * 10000);
	}
	
	public static int getMapInfo(Map map, String key) {
		return ((Double)map.get(key)).intValue();
	}
	
	public static void prepareGameUsers() throws SQLException {
		try {
			List<String> lines = Files.readLines(new File("d:\\go_member.txt"), Charset.forName("UTF-8"));
			for (String line : lines) {
				String[] strs = line.split("\t");
				if (strs.length != 2) {
					continue;
				}
				System.out.println(strs[0] + "\t" + strs[1]);
				
				long balance = getRandLong(1900000000, 10000000);
				GameUserLoginHandler.saveGameUser(strs[0], (double)balance/10000, strs[1], 1, getRandLong(18000000, 2000000));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static long getRandLong(long base, int bound) {
		Random rand = new Random();
		return base + rand.nextInt(bound);
	}

	public static void handleChipset(WebSocketMessageInboundPool pool, long userId, int amt, int lotteryIndex) {
		//update user 
		GameUser user;
		try {
			user = GameUserLoginHandler.getGameUser(userId);
			long chips = user.getCarry_amount() - amt;
			
			logger.debug("update user chips:"+userId+" new chips:"+chips);
			GameUserLoginHandler.updateUserChips(userId, chips);
			
			//then send notification
			//{"code":1,"errMsg":null,"user":{"id":100964,"name":"GL564103","chips":27716520.4,"money":"1999304519","reliefCount":0},"lotteryIndex":1,"num":6000,"protocol":2007}
			Map userMap = ImmutableMap.of("id", user.getId(), "name", user.getWxnm(), "chips", chips, "money", GameUtil.convertBalanceToCoin(user.getBalance().doubleValue()), "reliefCount", 0);
			Map map = new HashMap();
			map.put("code", 1);
			map.put("errMsg", "");
			map.put("user", userMap);
			map.put("lotteryIndex", lotteryIndex);
			map.put("num", amt);
			map.put("protocol", ProtocolConstants.S2C_PROTOCOL_SET_CHIPS);
			String message = JsonUtil.convertObjToStr(map);
			pool.sendMessage(message);
			//send notification to the robot process
			String queueName = GameUtil.getQueueName(pool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
			KestrelConnector.enqueue(queueName, message);
			//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, message);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
/*		for (String s : uidList) {
			int i = Integer.valueOf(s);
			if (i < 3900) { 
				uidList.remove(s);
			} else {
				System.out.println(s);
				break;
			}
		}*/
	}

	public static void sendDisclosureInfo(WebSocketMessageInboundPool webSocketMessageInboundPool, Integer groupId) {
		String game = webSocketMessageInboundPool.getPoolName();
		if (game.equals(NetConstants.CHIP_QUEUE)) {
			sendDiceDisclosureInfo(webSocketMessageInboundPool, groupId);
		}  else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
			sendCarDisclosureInfo(webSocketMessageInboundPool, groupId);
		} else {
			sendAnimalDisclosureInfo(webSocketMessageInboundPool, groupId);
		}
	}
	
	
	private static void sendAnimalDisclosureInfo(WebSocketMessageInboundPool webSocketMessageInboundPool,
			Integer groupId) {
		try {
			//{"result_td":12,"result_wx":8,"result_sx":0,"win_num":0,"setted_num":0,"master_score":387100,"chips":19591,"protocol":2010}
			//{"result_td":19,"result_wx":16,"result_sx":3,"win_num":0,"setted_num":0,"master_score":-1648940,"protocol":2010}
			AnimalDialSettlementGroup group = AnimalDialSettlementGroupHandler.getSettlementGroup(groupId);
			GameRound round = webSocketMessageInboundPool.getActiveRound();
			if (group.getState() == 2) {
				round.setScore(round.getScore() + group.getBanker_settlement_result());
				AnimalRoundDisclosure disclosure = new AnimalRoundDisclosure();
				disclosure.setMaster_score(group.getBanker_settlement_result());
				disclosure.setResult_sx(group.getResult_animal());
				disclosure.setResult_td(group.getResult_tiandi());
				disclosure.setResult_wx(group.getResult_wuxing());
				disclosure.setProtocol(ProtocolConstants.S2C_PROTOCOL_RESULT);
				
				Gson gson = new Gson();
				String broadcastedDisclosure = gson.toJson(disclosure); 
				//send result to master
				GameUser master = GameUserLoginHandler.getGameUser(round.getMasterId());
				if (master.getIdentity() == 1) { //robot
					String queueName = GameUtil.getQueueName(webSocketMessageInboundPool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
					KestrelConnector.enqueue(queueName, broadcastedDisclosure);
					//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, broadcastedDisclosure);
				} else {
					Long connId = webSocketMessageInboundPool.getConnIdForUser(master.getId());
					if (connId != null && connId > 0) {
						webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
					}
				}
				
				List<DiceSettlementResult> resultList = AnimalDialSettlementHistoryHandler.filterHistoryByRoundId(groupId);
				List<Long> playerList = Lists.newArrayList();
				
				for (DiceSettlementResult settlementResult : resultList) {
					if (settlementResult.getIdentity() == 0) {
						disclosure.setSetted_num(settlementResult.getSetted_num());
						disclosure.setWin_num(settlementResult.getWin_num());
						GameUser gUser = GameUserLoginHandler.getGameUser(settlementResult.getUser_id());
						if (gUser != null) {
							long userId = settlementResult.getUser_id();
							playerList.add(userId);
							Long connId = webSocketMessageInboundPool.getConnIdForUser(userId);
							if (connId != null && connId > 0) {
								System.out.println("send disclosure to client: "+userId);
								webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
							}
						}
					}
				}
				
				OnlineUserCache cache = webSocketMessageInboundPool.getUserConnCache();
				List<Long> allUserList = cache.getActiveUserList();
				disclosure.setSetted_num(0);
				disclosure.setWin_num(0);
				for (Long userid : allUserList){
					if (!playerList.contains(userid) && userid != master.getId()) {
						Long connId = webSocketMessageInboundPool.getConnIdForUser(userid);
						if (connId != null && connId > 0) {
							GameUser gUser = GameUserLoginHandler.getGameUser(userid);
							webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
						}
					}
				}
				
//				String msg = GameUtil.getMasterMsg(webSocketMessageInboundPool, webSocketMessageInboundPool.getActiveRound().getRemainCount());
//				webSocketMessageInboundPool.sendMessage(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static String getMasterMsg(WebSocketMessageInboundPool pool, int remainCount) {
		GameRound activeRound = pool.getActiveRound();
		long masterId = activeRound.getMasterId();
		GameUser user;
		try {
			user = GameUserLoginHandler.getGameUser(masterId);
			Map map = ImmutableMap.of("id", masterId, "name", user.getWxnm(), "chips", activeRound.getCoins(), "money",
					GameUtil.convertBalanceToCoin(user.getBalance().doubleValue()), "reliefCount", 0);
			return JsonUtil.convertObjToStr(ImmutableMap.of("user", map, "state", 4, "remainCount", remainCount,
					"score", activeRound.getScore(), "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER));
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	private static void sendCarDisclosureInfo(WebSocketMessageInboundPool webSocketMessageInboundPool,
			Integer groupId) {
		try {
			//{"result_td":12,"result_wx":8,"result_sx":0,"win_num":0,"setted_num":0,"master_score":387100,"chips":19591,"protocol":2010}
			CarDialSettlementGroup group = CarDialSettlementGroupHandler.getSettlementGroup(groupId);
			GameRound round = webSocketMessageInboundPool.getActiveRound();
			if (group.getState() == 2) {
				round.setScore(round.getScore() + group.getBanker_settlement_result());
				CarRoundDisclosure disclosure = new CarRoundDisclosure();
				disclosure.setMaster_score(group.getBanker_settlement_result());
				disclosure.setResult_sx(0);
				disclosure.setResult_td(group.getResult_flag());
				disclosure.setResult_wx(group.getResult());
				disclosure.setProtocol(ProtocolConstants.S2C_PROTOCOL_RESULT);
				
				Gson gson = new Gson();
				String broadcastedDisclosure = gson.toJson(disclosure); 
				//send result to master
				GameUser master = GameUserLoginHandler.getGameUser(round.getMasterId());
				if (master.getIdentity() == 1) { //robot
					String queueName = GameUtil.getQueueName(webSocketMessageInboundPool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
					KestrelConnector.enqueue(queueName, broadcastedDisclosure);
					//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, broadcastedDisclosure);
				} else {
					Long connId = webSocketMessageInboundPool.getConnIdForUser(master.getId());
					if (connId != null && connId > 0) {
						disclosure.setChips(round.getCoins());
						webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
					}
				}
				
				List<DiceSettlementResult> resultList = CarDialSettlementHistoryHandler.filterHistoryByRoundId(groupId);
				List<Long> playerList = Lists.newArrayList();
				
				for (DiceSettlementResult settlementResult : resultList) {
					if (settlementResult.getIdentity() == 0) {
						disclosure.setSetted_num(settlementResult.getSetted_num());
						disclosure.setWin_num(settlementResult.getWin_num());
						GameUser gUser = GameUserLoginHandler.getGameUser(settlementResult.getUser_id());
						if (gUser != null) {
							disclosure.setChips(gUser.getCarry_amount());
							long userId = settlementResult.getUser_id();
							playerList.add(userId);
							Long connId = webSocketMessageInboundPool.getConnIdForUser(userId);
							if (connId != null && connId > 0) {
								System.out.println("send disclosure to client: "+userId);
								webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
							}
						}
					}
				}
				
				OnlineUserCache cache = webSocketMessageInboundPool.getUserConnCache();
				List<Long> allUserList = cache.getActiveUserList();
				disclosure.setSetted_num(0);
				disclosure.setWin_num(0);
				for (Long userid : allUserList){
					if (!playerList.contains(userid) && userid != master.getId()) {
						Long connId = webSocketMessageInboundPool.getConnIdForUser(userid);
						if (connId != null && connId > 0) {
							GameUser gUser = GameUserLoginHandler.getGameUser(userid);
							disclosure.setChips(gUser.getCarry_amount());
							webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
						}
					}
				}
				
//				String msg = GameUtil.getMasterMsg(webSocketMessageInboundPool, webSocketMessageInboundPool.getActiveRound().getRemainCount());
//				webSocketMessageInboundPool.sendMessage(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//TODO
	public static void sendDiceDisclosureInfo(WebSocketMessageInboundPool webSocketMessageInboundPool, Integer groupId) {
		try {
			//{"result_1_num":5,"result_2_num":4,"result_3_num":3,"win_num":0,"setted_num":0,"master_score":-473060,"open_index":59547,"protocol":2010}
			DiceSettlementGroup group = DiceSettlementGroupHandler.getDiceSettlementGroup(groupId);
			GameRound round = webSocketMessageInboundPool.getActiveRound();
			if (group.getState() == 2) {
				round.setScore(round.getScore() + group.getBanker_settlement_result());
				RoundDisclosure disclosure = new RoundDisclosure();
				disclosure.setMaster_score(group.getBanker_settlement_result());
				disclosure.setOpen_index(groupId);
				disclosure.setProtocol(ProtocolConstants.S2C_PROTOCOL_RESULT);
				String result = group.getResult();
				String[] results = result.split(",");
				disclosure.setResult_1_num(Integer.valueOf(results[0]));
				disclosure.setResult_2_num(Integer.valueOf(results[1]));
				disclosure.setResult_3_num(Integer.valueOf(results[2]));
				
				Gson gson = new Gson();
				String broadcastedDisclosure = gson.toJson(disclosure); 
				//send result to master
				GameUser master = GameCacheUtil.getUserInfoCache().get(round.getMasterId());
				if (master.getIdentity() == 1) { //robot
					String queueName = GameUtil.getQueueName(webSocketMessageInboundPool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
					KestrelConnector.enqueue(queueName, broadcastedDisclosure);
					//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, broadcastedDisclosure);
				} else {
					Long connId = webSocketMessageInboundPool.getConnIdForUser(master.getId());
					if (connId != null && connId > 0) {
						webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), broadcastedDisclosure);
					}
				}
				
				List<DiceSettlementResult> resultList = DiceSettlementHistoryHandler.filterHistoryByRoundId(groupId);
				List<Long> playerList = Lists.newArrayList();
				
				for (DiceSettlementResult settlementResult : resultList) {
					disclosure.setSetted_num(settlementResult.getSetted_num());
					disclosure.setWin_num(settlementResult.getWin_num());
					if (settlementResult.getIdentity() == 0) {
						long userId = settlementResult.getUser_id();
						playerList.add(userId);
						Long connId = webSocketMessageInboundPool.getConnIdForUser(userId);
						if (connId != null && connId > 0) {
							System.out.println("send disclosure to client: "+userId);
							webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), gson.toJson(disclosure));
						}
					}
				}
				
				OnlineUserCache cache = webSocketMessageInboundPool.getUserConnCache();
				List<Long> allUserList = cache.getActiveUserList();
				for (Long userid : allUserList){
					if (!playerList.contains(userid) && userid != master.getId()) {
						Long connId = webSocketMessageInboundPool.getConnIdForUser(userid);
						if (connId != null && connId > 0) {
							webSocketMessageInboundPool.sendMessageToClient(connId.intValue(), broadcastedDisclosure);
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String getResultForAnimalRound() {
		//{"result_td":19,"result_wx":17,"result_sx":12,"protocol":2005}
		try {
			AnimalDialSettlementGroup group =  AnimalDialSettlementGroupHandler.getLatestSettledRound();
			if (group == null) {
				return "";
			}
			return JsonUtil.convertObjToStr(ImmutableMap.of("result_td", group.getResult_tiandi(), "result_wx", group.getResult_wuxing(), "result_sx", group.getResult_animal(),
					"protocol", ProtocolConstants.S2C_PROTOCOL_LAST_RESULT));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";		
	}

	private static String getResultForPrevCarRound() {
		return null;
	}

	private static String getResultForPrevDiceRound() {
		//{"result_1_num":5,"result_2_num":1,"result_3_num":5,"open_index":60865,"protocol":2005}
		try {
			DiceSettlementGroup group = DiceSettlementGroupHandler.getLatestSettledRound();
			if (group == null) {
				return "";
			}
			String result = group.getResult();
			String[] results = result.split(",");
			return JsonUtil.convertObjToStr(ImmutableMap.of("result_1_num", Integer.valueOf(results[0]), "result_2_num", Integer.valueOf(results[1]), "result_3_num", Integer.valueOf(results[2]),
					"open_index", group.getId(), "protocol", ProtocolConstants.S2C_PROTOCOL_LAST_RESULT));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getResultForPreviousRound(String game) {
		if (game.equals(NetConstants.CHIP_QUEUE)) {
			return getResultForPrevDiceRound();
		} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return getResultForPrevCarRound();
		} else {
			return getResultForAnimalRound();
		}
	}
	
	/**
	 * Get name of queue specified by the game name(name of the chip queue is used as the game name) and the basic queue name
	 * @param game
	 * @param basicQueueName
	 * @return
	 */
	public static String getQueueName(String game, String basicQueueName) {
		if (basicQueueName.equals(NetConstants.CHIP_QUEUE)) {
			return game;
		} else if (basicQueueName.equals(NetConstants.BOT_CTRL_QUEUE)) {
			if (game.equals(NetConstants.CHIP_QUEUE)) {
				return NetConstants.BOT_CTRL_QUEUE;
			} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
				return NetConstants.CAR_BOT_CTRL_QUEUE;
			} else {
				return NetConstants.ANIMAL_BOT_CTRL_QUEUE;
			}
		} else if (basicQueueName.equals(NetConstants.MAIN_SEQ_QUEUE_IN)) {
			if (game.equals(NetConstants.CHIP_QUEUE)) {
				return NetConstants.MAIN_SEQ_QUEUE_IN;
			} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
				return NetConstants.CAR_MAIN_SEQ_QUEUE_IN;
			} else {
				return NetConstants.ANIMAL_MAIN_SEQ_QUEUE_IN;
			}
		} else if (basicQueueName.equals(NetConstants.MAIN_SEQ_QUEUE_OUT)) {
			if (game.equals(NetConstants.CHIP_QUEUE)) {
				return NetConstants.MAIN_SEQ_QUEUE_OUT;
			} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
				return NetConstants.CAR_MAIN_SEQ_QUEUE_OUT;
			} else {
				return NetConstants.ANIMAL_MAIN_SEQ_QUEUE_OUT;
			}
		} else if (basicQueueName.equals(NetConstants.SETTLEMENT_CTRL_QUEUE)) {
			if (game.equals(NetConstants.CHIP_QUEUE)) {
				return NetConstants.SETTLEMENT_CTRL_QUEUE;
			} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
				return NetConstants.CAR_SETTLEMENT_CTRL_QUEUE;
			} else {
				return NetConstants.ANIMAL_SETTLEMENT_CTRL_QUEUE;
			}
		} else {
			return "";
		}
	}
	
	public static String getSysConfigName(String game, String keyName) {
		String prefix = getPropertyPrefix(game);
		return prefix + keyName;
	}
	
	private static String getPropertyPrefix(String game) {
		if(game.equals(NetConstants.CHIP_QUEUE)) {
			return "dice"; 
		} else if (game.equals(NetConstants.ANIMAL_CHIP_QUEUE)) {
			return "animal";
		} else {
			return "car";
		}
	}

	public static int getSettlementTimeSpan(String game) {
		if (game.equals(NetConstants.CHIP_QUEUE)) {
			return 18;
		} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return 25;
		} else {
			return 25;
		}
	}
	
	public static AbstractGamePacketHandler getGamePacketHandler(WebSocketMessageInboundPool pool) {
		String poolName = pool.getPoolName();
		if (poolName.equals(NetConstants.CHIP_QUEUE)) {
			return new DicePacketHandler(pool);
		} else if (poolName.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return new CarPacketHandler(pool);
		} else {
			return new AnimalPacketHandler(pool);
		}
	}
	
	/**
	 * return the count of different target indexes
	 * @param game - name of the game
	 * @return
	 */
	public static int getGameTargetCnt(String game) {
		if (game.equals(NetConstants.CHIP_QUEUE)) {
			return 35;
		} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return 8;
		} else {
			return 21;
		}
	}
	
	public static Map getSingleChipStat(String game, long roundNo, long uid) {
		try {
			if (game.equals(NetConstants.CHIP_QUEUE)) {
					return DiceSettlementHistoryHandler.getChipStat(roundNo, uid);
			} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)){
				return CarDialSettlementHistoryHandler.getChipStat(roundNo, uid);
			} else {
				return AnimalDialSettlementHistoryHandler.getChipStat(roundNo, uid);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public static long generateNextGroup(String game, long masterId, long coins) {
		int groupId = -1;
		try {
			GameUser user = GameUserLoginHandler.getGameUser(masterId);
			if (user != null) {
				if (game.equals(NetConstants.CHIP_QUEUE)) {
					DiceSettlementGroup group = new DiceSettlementGroup();
					group.setBanker_carry_amount(coins);
					group.setBanker_id(masterId);
					group.setStart_time(new Date());
					group.setState(0);
					groupId = DiceSettlementGroupHandler.saveDiceSettleGroup(group);
				} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
					CarDialSettlementGroup group = new CarDialSettlementGroup();
					group.setBanker_carry_amount(coins);
					group.setBanker_id((int)masterId);
					group.setStart_time(new Date());
					group.setState(0);
					groupId = CarDialSettlementGroupHandler.saveCarSettleGroup(group);
				} else if (game.equals(NetConstants.ANIMAL_CHIP_QUEUE)){
					AnimalDialSettlementGroup group = new AnimalDialSettlementGroup();
					group.setBanker_id((int)masterId);
					group.setBanker_carry_amount(coins);
					group.setStart_time(new Date());
					ZodiacTriple triple = GameCacheUtil.getZtCache().get(GameCacheUtil.ZT_CACHE); 
					group.setCombined_three_animal(triple.getSx());
					group.setCombined_three_tiandi(triple.getTd());
					group.setCombined_three_wuxing(triple.getWx());
					
					ZodiacPair pair = GameCacheUtil.getZpCache().get(GameCacheUtil.ZP_CACHE);
					group.setCombined_two_animal(pair.getSx());
					group.setCombined_two_tiandi(pair.getTd());
					
					group.setState(0);
					groupId = AnimalDialSettlementGroupHandler.saveAnimalSettleGroup(group);
				}
			} 
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return -1;
		}

		return groupId;
	}
	
	public static int getGameId(String game) {
		if (game.equals(NetConstants.CHIP_QUEUE)) {
			return NetConstants.GAME_ROOM_DICE;
		} else if (game.equals(NetConstants.CAR_CHIP_QUEUE)) {
			return NetConstants.GAME_ROOM_CAR;
		} else {
			return NetConstants.GAME_ROOM_ANIMAL;
		}
	}
	
	public static long getMasterPromotionLimit(String gameName) {
		String keyName = GameUtil.getSysConfigName(gameName, GameUtil.MASTER_BALANCE_DOWN_LIMIT);
		SettlementSysConfig config = GameCacheUtil.getSysConfigCache().get(keyName);
		long downLimit = 10000000;
		if (config != null) {
			try {
				downLimit = Long.valueOf(config.getValue()).longValue();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return downLimit;
	}
	
}

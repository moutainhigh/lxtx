package com.lxtech.game.plaza.timeline;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lxtech.game.plaza.cache.GameCacheUtil;
import com.lxtech.game.plaza.db.DiceSettlementGroupHandler;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.DiceSettlementGroup;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.db.model.SettlementSysConfig;
import com.lxtech.game.plaza.net.KestrelConnector;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketInboundPoolFactory;
import com.lxtech.game.plaza.websocket.WebSocketMessageInbound;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class GameRoundProceeder implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(GameRoundProceeder.class);
	
	private GameRound round;

	private Timer timer;

	private TimelineController timelineCtl;
	
	public GameRoundProceeder(GameRound round, TimelineController ctrl) {
		this.round = round;
		this.timelineCtl = ctrl;
	}

	@Override
	public void run() {
		this.timelineCtl.waitingForMaster();
		timer = new Timer();
		TimerTask waitForStartTask = new WaitForStartTask(this.timelineCtl, timer, this.round);
		timer.schedule(waitForStartTask, 1000);
		TimerTask waitForChipsTask = new WaitForChipsTask(this.timelineCtl, waitForStartTask, timer, this.round);
		timer.schedule(waitForChipsTask, 6000);
		TimerTask waitForCalcTask = new WaitForCalculateTask(this.timelineCtl, waitForChipsTask, timer, this.round);
		timer.schedule(waitForCalcTask, 36000);
		TimerTask finishTask = new WaitForFinishTask(this.timelineCtl, waitForCalcTask, timer, this.round);
		int timespan = GameUtil.getSettlementTimeSpan(this.timelineCtl.getName());
		timer.schedule(finishTask, 36000 + timespan*1000);
	}

	class GeneralTimerTask extends TimerTask {
		protected TimelineController timelineCtrl;
		
		protected BotController botController;
		
		protected TimerTask previousTask;
		
		protected Timer timer;
		
		protected GameRound round;

		/*public GeneralTimerTask(TimelineController timelineCtrl, Timer timer) {
			this.timelineCtrl = timelineCtrl;
			this.botController = new BotController();
			this.timer = timer;
		}*/
		
		public GeneralTimerTask(TimelineController timelineCtrl, Timer timer, GameRound round) {
			this.timelineCtrl = timelineCtrl;
			this.botController = new BotController(timelineCtrl.getWebSocketMessageInboundPool(), round);
			this.timer = timer;
			this.round = round;
		}

		/*public GeneralTimerTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer) {
			this.timelineCtrl = timelineCtrl;
			this.previousTask = previousTask;
			this.botController = new BotController();
			this.timer = timer;
		}*/
		
		public GeneralTimerTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer, GameRound round) {
			this.timelineCtrl = timelineCtrl;
			this.previousTask = previousTask;
			this.botController = new BotController(timelineCtrl.getWebSocketMessageInboundPool(), round);
			this.timer = timer;
			this.round = round;
		}

		@Override
		public void run() {
		}
	}

	class WaitForStartTask extends GeneralTimerTask {
		public WaitForStartTask(TimelineController timelineCtrl, Timer timer, GameRound round) {
			super(timelineCtrl, timer, round);
		}

		@Override
		public void run() {
			this.round.setStep(NetConstants.ROOM_STATE_WAITING_FOR_START);
			this.timelineCtrl.waitingForStart(5);
			this.botController.waitingForStart(5);
			long start = System.currentTimeMillis();
			while(true) {
				if (System.currentTimeMillis() - start >= 5000) {
					break;
				}
				String outQueueName = GameUtil.getQueueName(this.timelineCtrl.getName(), NetConstants.MAIN_SEQ_QUEUE_OUT);
				//String message = KestrelConnector.dequeue(NetConstants.MAIN_SEQ_QUEUE_OUT);
				String message = KestrelConnector.dequeue(outQueueName);
				if (!Strings.isNullOrEmpty(message)) {
					Map map = (Map)JsonUtil.convertStringToObject(message);
					Integer msgType = GameUtil.getMapInfo(map, "msg");
					if (msgType != null && msgType == 3002) { //don't continue as a master
						Long masterId = (long)GameUtil.getMapInfo(map, "masterId"); //(Long)map.get("masterId");
						if (masterId != null && masterId.longValue() == this.round.getMasterId()) {
							this.round.setMasterId(0);
							long coins = this.round.getCoins() + this.round.getScore();
							try {
								GameUserLoginHandler.updateUserChips(masterId, coins);
							} catch (SQLException e) {
								logger.error(e.getMessage());
							}
							//update user status
							GameUserLoginHandler.setUserAsPlayer(masterId);
							WebSocketMessageInboundPool pool = this.timelineCtrl.getWebSocketMessageInboundPool();
							long nextMasterId = pool.getNextMasterId();
							if (nextMasterId > 0) {
								String msg = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", nextMasterId));
								String inQueueName = GameUtil.getQueueName(this.timelineCtrl.getName(), NetConstants.MAIN_SEQ_QUEUE_IN);
								KestrelConnector.enqueue(inQueueName, msg);
								//KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, msg);
							} else {
								//broadcast information for current master and round
								pool.sendClearedMasterMsg();
							}
							this.timer.cancel();
						}
						break;
					}
				} 
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class WaitForChipsTask extends GeneralTimerTask {
		public WaitForChipsTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer, GameRound round) {
			super(timelineCtrl, previousTask, timer, round);
		}

		@Override
		public void run() {
			this.round.setStep(NetConstants.ROOM_STATE_WAITING_FOR_SET_CHIPS);
			this.previousTask.cancel();
			//save current round to database
			long masterId = this.round.getMasterId();
			GameUser user;
/*				DiceSettlementGroup group = new DiceSettlementGroup();
				user = GameUserLoginHandler.getGameUser(masterId);
				group.setBanker_carry_amount(this.round.getCoins());
				group.setBanker_id(masterId);
				group.setStart_time(new Date());
				group.setState(0);
				int groupId = DiceSettlementGroupHandler.saveDiceSettleGroup(group);*/
			int groupId = (int)GameUtil.generateNextGroup(this.timelineCtrl.getName(), masterId, this.round.getCoins());
			if(groupId > 0) {
				this.round.setRoundNo(groupId);
				this.timelineCtrl.waitingForChipset(30);
				this.botController.waitingForChipset(30);					
			} else {
				logger.error("group not generated correctly.");
			}
		}
	}

	class WaitForCalculateTask extends GeneralTimerTask {
		public WaitForCalculateTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer, GameRound round) {
			super(timelineCtrl, previousTask, timer, round);
		}

		@Override
		public void run() {
			this.round.setStep(NetConstants.ROOM_STATE_WAITING_FOR_CACLULATE);
			this.previousTask.cancel();
			int timespan = GameUtil.getSettlementTimeSpan(this.timelineCtrl.getName());
			this.timelineCtrl.waitingForCalculate(timespan);
			this.botController.waitingForCalculate(timespan);
			
			//now read the queue and  
			long start = System.currentTimeMillis();
			while(true) {
				if (System.currentTimeMillis() - start >= timespan * 1000) {
					break;
				}
				
				String outQueueName = GameUtil.getQueueName(this.timelineCtrl.getName(), NetConstants.MAIN_SEQ_QUEUE_OUT);
				String message = KestrelConnector.dequeue(outQueueName);
				//String message = KestrelConnector.dequeue(NetConstants.MAIN_SEQ_QUEUE_OUT);
				if (!Strings.isNullOrEmpty(message)) {
					Map map = (Map)JsonUtil.convertStringToObject(message);
					Integer msgType = GameUtil.getMapInfo(map, "msg");
					if (msgType != null && msgType == 3003) { //the result has been disclosed
						Integer groupId = GameUtil.getMapInfo(map, "groupId");
						if (groupId == this.round.getRoundNo()) {
							logger.info("round[" + groupId + "] has been disclosed.");
							//GameUtil.handleChipset(pool, userId, amt, lotteryIndex);
							GameUtil.sendDisclosureInfo(this.timelineCtrl.getWebSocketMessageInboundPool(), groupId);
							break;
						}
					}
				} 
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
	}

	class WaitForFinishTask extends GeneralTimerTask {
		/*public WaitForFinishTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer) {
			super(timelineCtrl, previousTask, timer);
		}*/
		
		public WaitForFinishTask(TimelineController timelineCtrl, TimerTask previousTask, Timer timer, GameRound round) {
			super(timelineCtrl, previousTask, timer, round);
		}
		
		@Override
		public void run() {
			this.round.setStep(NetConstants.ROOM_STATE_WAITING_FOR_SERVER);
			this.previousTask.cancel();
			if (this.timer != null) {
				this.timer.cancel();
			}
			
			long masterId = this.round.getMasterId();
			//GameUser user = GameCacheUtil.getUserInfoCache().get(masterId);
			GameUser user = null;
			try {
				user = GameUserLoginHandler.getGameUser(masterId);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			WebSocketMessageInboundPool pool = this.timelineCtrl.getWebSocketMessageInboundPool();
			/*String keyName = GameUtil.getSysConfigName(this.timelineCtrl.getName(), GameUtil.MASTER_BALANCE_DOWN_LIMIT);
			SettlementSysConfig config = GameCacheUtil.getSysConfigCache().get(keyName);
			long downLimit = 10000000;
			if (config != null) {
				try {
					downLimit = Long.valueOf(config.getValue()).longValue();
				} catch (Exception e) {
				}
			}*/
			long downLimit = GameUtil.getMasterPromotionLimit(this.timelineCtrl.getName());
			boolean isMankind = user.getIdentity() == 0;
			
			Long connId = pool.getConnIdForUser(user.getId());
			boolean isOffline = isMankind ? (connId == null || pool.getMessageInbound(connId.longValue() + "") == null) : false;
			
			if (user.getCarry_amount() < downLimit || isOffline) { //剩余金币不足
				//自动下庄
				pool.sendMasterDownMsg(masterId, user.getIdentity() == 1);
				long nextMasterId = pool.getNextMasterId();
				if (nextMasterId > 0) {
					String msg = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", nextMasterId));
//					KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, msg);
					KestrelConnector.enqueue(GameUtil.getQueueName(pool.getPoolName(), NetConstants.MAIN_SEQ_QUEUE_IN), msg);
				} else {
					//没有庄家了
					pool.sendClearedMasterMsg();
				}
			} else {
				//insert message to queue
				if (this.round.getRemainCount() >= 20) {
					long nextMasterId = pool.getNextMasterId();
					if (nextMasterId > 0) {
						String msg = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", nextMasterId));
//						KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, msg);
						KestrelConnector.enqueue(GameUtil.getQueueName(pool.getPoolName(), NetConstants.MAIN_SEQ_QUEUE_IN), msg);
						//给之前的用户发送下庄通知
						if (user != null) {
							pool.sendMasterDownMsg(masterId, user.getIdentity() == 1);
						}
					} else {
						GameRound round = this.timelineCtrl.getWebSocketMessageInboundPool().getActiveRound();
						round.setRemainCount(round.getRemainCount() + 1);
						logger.info("now round remain count is:" + round.getRemainCount());
						String msg = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", round.getMasterId()));
//						KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, msg);
						KestrelConnector.enqueue(GameUtil.getQueueName(pool.getPoolName(), NetConstants.MAIN_SEQ_QUEUE_IN), msg);
					}
				} else {
					GameRound round = this.timelineCtrl.getWebSocketMessageInboundPool().getActiveRound();
					round.setRemainCount(round.getRemainCount() + 1);
					String msg = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", round.getMasterId()));
//					KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, msg);				
					KestrelConnector.enqueue(GameUtil.getQueueName(pool.getPoolName(), NetConstants.MAIN_SEQ_QUEUE_IN), msg);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		GameRound round = new GameRound();
		WebSocketMessageInboundPool pool = WebSocketInboundPoolFactory.getWebSocketMessageInboundPool("test");
		TimelineController ctrl = new TimelineController(pool);
		GameRoundProceeder proceeder = new GameRoundProceeder(round, ctrl);
		new Thread(proceeder).start();
	}
}

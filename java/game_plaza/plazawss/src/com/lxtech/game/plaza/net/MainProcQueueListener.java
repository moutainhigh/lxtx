package com.lxtech.game.plaza.net;

import java.sql.SQLException;
import java.util.Map;

import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.timeline.GameRoundProceeder;
import com.lxtech.game.plaza.timeline.TimelineController;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

/**
 * 读取主控制队列，消息来自于两处
 * 
 * @author wangwei
 *
 */
public class MainProcQueueListener extends AbstractKestrelMessageListener{

	private WebSocketMessageInboundPool pool;
	
	public MainProcQueueListener(String queueName, WebSocketMessageInboundPool pool) {
		super(queueName);
		this.pool = pool;
	}

	@Override
	public void handleReceivedMessage(String message) {
		Map map = (Map)JsonUtil.convertStringToObject(message);
		Integer msgType = ((Double)map.get("msg")).intValue();
		
		switch (msgType) {
		case 3001:
			System.out.println("received 3001 message " + message);
			Integer masterId = ((Double)map.get("masterId")).intValue();
			startNewRound(masterId);
			break;

		default:
			break;
		}
	}

	private void startNewRound(Integer masterId) {
		GameRound activeRound = this.pool.getActiveRound();
		try {
			GameUser user = GameUserLoginHandler.getGameUser(masterId);
			if (user != null) {
				if (activeRound == null || activeRound.getMasterId() == 0 || activeRound.getMasterId() != masterId) {
					activeRound = new GameRound();
					activeRound.setMasterId(masterId);
					activeRound.setRemainCount(0);
					activeRound.setCoins(user.getCarry_amount());
					activeRound.setStartTime(System.currentTimeMillis());
					activeRound.setStep(0);
					activeRound.setScore(0);
					activeRound.setChipstat(getEmptyChipStats());
				} else {
					activeRound.setStep(0);
					activeRound.setCoins(user.getCarry_amount());
					activeRound.setChipstat(getEmptyChipStats());
					activeRound.setStartTime(System.currentTimeMillis());
				}
				this.pool.setActiveRound(activeRound);
				GameUserLoginHandler.updateUserChips(masterId, 0);
				GameUserLoginHandler.setUserAsMaster(GameUtil.getGameId(this.queueName), masterId);
				GameRoundProceeder proceeder = new GameRoundProceeder(activeRound, new TimelineController(this.pool));
				proceeder.run();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private long[] getEmptyChipStats() {
		long[] chipstats = new long[35];
		for (int i = 0; i < 35; i++) {
			chipstats[i] = 0;
		}
		return chipstats;
	}
}
	
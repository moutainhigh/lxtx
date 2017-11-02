package com.lxtech.game.plaza.timeline;

import com.google.common.collect.ImmutableMap;
import com.lxtech.game.plaza.net.KestrelConnector;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class BotController extends TimelineController implements PlayerNotifier{
	
	private GameRound round;
	
	public BotController(WebSocketMessageInboundPool pool, GameRound round) {
		super(pool);
		this.round = round;
	}

	@Override
	public void waitingForMaster() {
		String msg = JsonUtil.convertObjToStr(ImmutableMap.of("state", 0, "interval", 0, "protocol", 2003, "groupId", this.round.getRoundNo()));
		String queueName = GameUtil.getQueueName(this.pool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
		KestrelConnector.enqueue(queueName, msg);

		GameRound activeRound = this.pool.getActiveRound();
		int remainCount = activeRound.getRemainCount();
		if (remainCount >= 1) {
			KestrelConnector.enqueue(queueName, this.getMasterMsg(remainCount - 1));
		}
		KestrelConnector.enqueue(queueName, this.getMasterMsg(remainCount));
	}

	@Override
	public void waitingForStart(int seconds) {
		String queueName = GameUtil.getQueueName(this.pool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
		KestrelConnector.enqueue(queueName, getMessage(1, seconds));
		GameRound activeRound = this.pool.getActiveRound();
		int remainCount = activeRound.getRemainCount();
		KestrelConnector.enqueue(queueName, this.getMasterMsg(remainCount));
	}

	@Override
	public void waitingForChipset(int seconds) {
		String queueName = GameUtil.getQueueName(this.pool.getPoolName(), NetConstants.BOT_CTRL_QUEUE);
		KestrelConnector.enqueue(queueName, getMessage(2, seconds));
	}

	@Override
	public void waitingForCalculate(int seconds) {
		String queueName = GameUtil.getQueueName(this.pool.getPoolName(), NetConstants.SETTLEMENT_CTRL_QUEUE);
		KestrelConnector.enqueue(queueName, getMessage(3, seconds));
	}

	private String getMessage(int state, int interval) {
		if (this.round.getRoundNo() > 0) {
			return JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "interval", interval, "protocol" , 2003, "groupId", this.round.getRoundNo()));
		} else {
			return JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "interval", interval, "protocol" , 2003));
		}
	}	
}
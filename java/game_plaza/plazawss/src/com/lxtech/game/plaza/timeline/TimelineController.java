package com.lxtech.game.plaza.timeline;

import java.sql.SQLException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class TimelineController implements PlayerNotifier {
	protected WebSocketMessageInboundPool pool;

	public TimelineController(WebSocketMessageInboundPool pool) {
		this.pool = pool;
	}

	public WebSocketMessageInboundPool getWebSocketMessageInboundPool() {
		return this.pool;
	}

	protected String getMasterMsg(int remainCount) {
		GameRound activeRound = this.pool.getActiveRound();
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

	@Override
	public void waitingForMaster() {
		String msg = JsonUtil.convertObjToStr(ImmutableMap.of("state", 0, "interval", 0, "protocol", 2003));
		pool.sendMessage(msg);

		GameRound activeRound = this.pool.getActiveRound();
		int remainCount = activeRound.getRemainCount();
		if (remainCount >= 1) {
			this.pool.sendMessage(this.getMasterMsg(remainCount - 1));
		}
		this.pool.sendMessage(this.getMasterMsg(remainCount));
	}

	@Override
	public void waitingForStart(int seconds) {
		pool.sendMessage(getMessage(1, seconds));
		GameRound activeRound = this.pool.getActiveRound();
		int remainCount = activeRound.getRemainCount();
		this.pool.sendMessage(this.getMasterMsg(remainCount));
	}

	@Override
	public void waitingForChipset(int seconds) {
		pool.sendMessage(getMessage(2, seconds));
	}

	@Override
	public void waitingForCalculate(int seconds) {
		pool.sendMessage(getMessage(3, seconds));
	}

	private String getMessage(int state, int interval) {
		return JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "interval", interval, "protocol", 2003));
	}
	
	public String getName() {
		return this.pool.getPoolName();
	}
}

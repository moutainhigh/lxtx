package com.lxtech.game.plaza.websocket;

import java.nio.CharBuffer;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lxtech.game.plaza.cache.GameCacheUtil;
import com.lxtech.game.plaza.cache.OnlineUserCache;
import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.GameUser;
import com.lxtech.game.plaza.net.KestrelConnector;
import com.lxtech.game.plaza.net.NetConstants;
import com.lxtech.game.plaza.protocol.ProtocolConstants;
import com.lxtech.game.plaza.timeline.GameRound;
import com.lxtech.game.plaza.util.CrawlerConfig;
import com.lxtech.game.plaza.util.GameUtil;
import com.lxtech.game.plaza.util.JsonUtil;

public class WebSocketMessageInboundPool {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageInboundPool.class);
	
	private String poolName;
	
	private ExecutorService es;
	//保存连接的MAP容器
	private final Map<String, WebSocketMessageInbound > connections = new ConcurrentHashMap<String,WebSocketMessageInbound>();
	//保存用户id和连接id的映射
	//private final Map<String, Integer> userConnMap = new HashMap<String, Integer>();
	private OnlineUserCache userConnCache;
	//当前活跃局
	private volatile GameRound activeRound;
	
	private List<String> masterList;
	
	public OnlineUserCache getUserConnCache() {
		return this.userConnCache;
	}
	
	public WebSocketMessageInboundPool(String name) {
		this.poolName = name;
		this.es = Executors.newFixedThreadPool(200);
		int count = Integer.valueOf(CrawlerConfig.get("ws.cache.key.count"));
		int expire = Integer.valueOf(CrawlerConfig.get("ws.cache.key.expire"));
		this.userConnCache = new OnlineUserCache(count, expire);
		this.masterList = Lists.newArrayList();
	}
	
	public String getPoolName() {
		return this.poolName;
	}
	
	public Long getConnIdForUser(Long userId) {
		return userConnCache.get(userId);
	}
	
	public void setUserConnMapping(long userId, long connId) {
		logger.info("refresh user connId.");
		userConnCache.put(userId, connId);
	}
	
	//向连接池中添加连接
	public void addMessageInbound(WebSocketMessageInbound inbound){
		//添加连接
		System.out.println("client : " + inbound.getConnId() + " joined..");
		connections.put(inbound.getConnId()+"", inbound);
	}
	
	public WebSocketMessageInbound getMessageInbound(String connId) {
		return this.connections.get(connId);
	}
	
	//获取所有的在线用户
	public Set<String> getOnlineUser(){
		return connections.keySet();
	}
	
	public void removeMessageInbound(WebSocketMessageInbound inbound){
		//移除连接
		System.out.println("user : " + inbound.getConnId() + " exit..");
		connections.remove(inbound.getConnId()+"");
	}
	
	public GameRound getActiveRound() {
		return activeRound;
	}

	public void setActiveRound(GameRound activeRound) {
		this.activeRound = activeRound;
	}

	public void sendMessageToClient(int connId, String message){
		//向特定的用户发送数据
		WebSocketMessageInbound inbound = connections.get(connId + "");
		if(inbound != null){
			this.es.submit(new WriteTask(inbound, message));
		}
	}
	
	//向所有的活跃用户发送消息
	public synchronized void sendMessage(String message){
		logger.debug("send messages to all clients." + message);
		List<Long> activeUserList = userConnCache.getActiveUserList();
		logger.debug("active user count:" + activeUserList.size());
		for (Long uid : activeUserList) 
		{
			Long connId = userConnCache.get(uid);
			WebSocketMessageInbound inbound = connections.get(connId.longValue() + "");
			if(inbound != null) {
				Future<Boolean> futureTask = this.es.submit(new WriteTask(inbound, message));
			}			
		}
	}
	
	/**
	 * This class is used to send multiple messages to all the clients
	 * @author wangwei
	 *
	 */
	private class MultipleWriteTask implements Callable<Boolean> {
		private WebSocketMessageInbound inbound;
		
		private List<String> messageList;
		
		public MultipleWriteTask(WebSocketMessageInbound inbound, List<String> messageList) {
			this.inbound = inbound;
			this.messageList = messageList;
		}
		
		@Override
		public Boolean call() throws Exception {
			//inbound.getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(message.getBytes()));
			if (this.messageList != null && this.messageList.size() > 0) {
				for (String msg : messageList) {
					inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(msg));					
				}
				inbound.getWsOutbound().flush();
			}

			return true;
		}
	}
	
	private class WriteTask implements Callable<Boolean> {
		private WebSocketMessageInbound inbound;
		
		private String message;
		
		public WriteTask(WebSocketMessageInbound inbound, String message) {
			this.inbound = inbound;
			this.message = message;
		}
		
		@Override
		public Boolean call() throws Exception {
			//inbound.getWsOutbound().writeBinaryMessage(ByteBuffer.wrap(message.getBytes()));
			inbound.getWsOutbound().writeTextMessage(CharBuffer.wrap(message));
			inbound.getWsOutbound().flush();
			return true;
		}
	}

	//上庄
	public synchronized int receiveMasterReq(long userId) {
		GameUser user = null;
		try {
			user = GameUserLoginHandler.getGameUser(userId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//GameUser user = GameCacheUtil.getUserInfoCache().get(userId);
		if (user == null) {
			return NetConstants.REQEST_MASTER_NO_SUCH_USER;
		} else {
			if (this.getActiveRound() != null && (userId == this.getActiveRound().getMasterId())) {
				return NetConstants.REQEST_MASTER_STATE_SUCCESS;	
			} else {
				long coins = user.getCarry_amount();
				if (coins < NetConstants.REQUEST_MASTER_MIN_COINS){
					return NetConstants.REQEST_MASTER_STATE_NOT_ENOUGH_CHIPS;
				} 				
			}
		}
		
		if (this.activeRound == null || this.activeRound.getMasterId() == 0) {
			String message = JsonUtil.convertObjToStr(ImmutableMap.of("msg", 3001, "masterId", userId));
			//KestrelConnector.enqueue(NetConstants.MAIN_SEQ_QUEUE_IN, message);
			KestrelConnector.enqueue(GameUtil.getQueueName(this.poolName, NetConstants.MAIN_SEQ_QUEUE_IN), message);
			return NetConstants.REQEST_MASTER_STATE_SUCCESS;
		} else {
			if (this.activeRound.getMasterId() != userId) {
				if (!this.masterList.contains(userId+"")) {
					this.masterList.add(userId+"");
					GameUserLoginHandler.setUserAsMasterInQueue(GameUtil.getGameId(this.getPoolName()), userId);
					return NetConstants.REQEST_MASTER_STATE_SUCCESS;					
				}
			} else {
				return NetConstants.REQEST_MASTER_STATE_NOT_IN_START_STATE;
			}
		}
		return NetConstants.REQEST_MASTER_STATE_NOT_IN_START_STATE;
	}
	
	public synchronized void removeMaster(long userId) {
		GameUserLoginHandler.setUserAsPlayer(userId);
		this.masterList.remove(userId + "");
	}
	
	public List<String> getMasterList() {
		return this.masterList;
	}
	
	public void sendMasterDownMsg(long uid, boolean isRobot) {
		//send notification
		Long connId = this.userConnCache.get(uid);
		int state = NetConstants.REQEST_MASTER_STATE_DOWN_SUCCESS;
		if (connId != null && connId > 0) {
			if (isRobot) {
				String masterDownMsg = JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER, "userId", uid));
				//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, masterDownMsg);
				KestrelConnector.enqueue(GameUtil.getQueueName(this.poolName, NetConstants.BOT_CTRL_QUEUE), masterDownMsg);
			} else {
				WebSocketMessageInbound inbound = this.connections.get(connId + "");
				if (inbound != null) {
					String masterDownMsg = JsonUtil.convertObjToStr(ImmutableMap.of("state", state, "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER));				
					//发送下庄消息
					this.es.submit(new WriteTask(inbound, masterDownMsg));
				}
			}
		}
		GameUserLoginHandler.setUserAsPlayer(uid);
	}
	
	//notify clients that current no master is available
	public void sendClearedMasterMsg() {
		List<String> responseList = Lists.newArrayList();
		responseList.add(JsonUtil.convertObjToStr(
				ImmutableMap.of("state", 0, "interval", 0, "protocol", ProtocolConstants.S2C_PROTOCOL_GAME_STATE)));
		Map userMap = ImmutableMap.of("id", 0, "name", ProtocolConstants.NO_MASTER, "chips", 0, "money", 0);
		responseList.add(JsonUtil.convertObjToStr(ImmutableMap.of("state", 4, "user", userMap, "remainCount", 0,
				"score", 0, "protocol", ProtocolConstants.S2C_PROTOCOL_REQUEST_MASTER)));
		for (String msg : responseList) {
			this.sendMessage(msg);
			String queueName = GameUtil.getQueueName(this.poolName, NetConstants.BOT_CTRL_QUEUE);
			KestrelConnector.enqueue(queueName, msg);
			//KestrelConnector.enqueue(NetConstants.BOT_CTRL_QUEUE, msg);
		}
		GameRound round = this.getActiveRound();
		round.setCoins(0);
		round.setMasterId(0);
		round.setScore(0);
		round.setStep(0);
		this.setActiveRound(round);
	}
	
	public synchronized long getNextMasterId() {
		if (this.masterList == null || this.masterList.size() == 0) {
			return 0;
		} else {
			try {
				for (Iterator iterator = masterList.iterator(); iterator.hasNext();) {
					String str = (String) iterator.next();
					long uid = Long.valueOf(str).longValue();
					
					GameUser user = GameUserLoginHandler.getGameUser(uid);
					long downLimit = GameUtil.getMasterPromotionLimit(this.getPoolName());
					if (user != null && user.getCarry_amount() < downLimit) { //该用户的金币余额不足庄家金币最低值
						this.masterList.remove(str);
						this.sendMasterDownMsg(uid, user.getIdentity() == 1);
					} else {
						this.masterList.remove(str);
						return Long.valueOf(uid);
					}
				}				
			} catch (Exception e) {
			}

			return 0;
		}
	}
}

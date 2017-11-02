// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import org.takeback.chat.lottery.listeners.RoomAndLotteryListener;
import org.takeback.chat.store.room.LotteryFactory;
import org.takeback.util.BeanUtils;
import java.util.Date;

import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLottery;
import org.takeback.util.converter.ConversionUtils;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import org.takeback.chat.lottery.listeners.GameException;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.Message;
import org.takeback.chat.entity.PubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.service.RoomService;
import org.takeback.chat.service.UserService;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component("redMessageProcessor")
public class RedMessageProcessor extends TxtMessageProcessor
{
    public static final Logger LOGGER;
    @Autowired
    private LotteryService lotteryService;
    @Autowired
    private UserService userService;
    
    @Override
    public void process(final Message message, final WebSocketSession session, final Room room, final User user) {
        final LotteryFactory.DefaultLotteryBuilder builder = this.generateLottery(message, session, room, user);
        if (builder == null) {
            return;
        }
        final RoomAndLotteryListener listener = room.getRoomAndLotteryListener();
        if (listener != null) {
            try {
                if (!listener.onBeforeRed(builder)) {
                    return;
                }
            }
            catch (GameException e) {
                RedMessageProcessor.LOGGER.error(e.getMessage());
                MessageUtils.sendCMD(session, "alert", e.getMessage());
                return;
            }
        }
        builder.setRoom(room);
        final BigDecimal money = ConversionUtils.convert(user.getMoney(), BigDecimal.class);
        final GcLottery gcLottery = new GcLottery();
        gcLottery.setId(builder.getLotteryId());
        gcLottery.setRoomId(builder.getRoomId());
        gcLottery.setCreateTime(new Date());
        gcLottery.setDescription(builder.getDescription());
        gcLottery.setMoney(builder.getMoney());
        if (user.getChnno() != null) {
        	gcLottery.setChnno(user.getChnno().intValue());
        }
        gcLottery.setSender(builder.getSender());
        gcLottery.setNumber(builder.getNumber());
        gcLottery.setStatus("0");
        gcLottery.setType(builder.getType());
        this.lotteryService.save(GcLottery.class, gcLottery);
        
        String roomId = builder.getRoomId();
        PubUser pUser = userService.getById(user.getId());
        if (pUser != null && pUser.getUserType().equals("2") && roomId.startsWith("S") || roomId.startsWith("N")) {
        	userService.getById(user.getId());
        	
        	GcHumanLotteryDetail lotteryDetail = new GcHumanLotteryDetail();
        	lotteryDetail.setChnno(user.getChnno());
        	lotteryDetail.setCoin(BigDecimal.ZERO);
        	lotteryDetail.setCreateDate(new Date());
        	lotteryDetail.setDesc1("发包");
        	
        	String gameType = "";
        	if (roomId.startsWith("N")) {
        		gameType = "G022";
        	} else if (roomId.startsWith("J")) {
        		gameType = "G011";
        	} else {
        		gameType = "G04";
        	}
        	lotteryDetail.setGameType(gameType);

        	double deposit = 0.0d;
        	if (gameType.equals("G04")) { //扫雷
        		deposit = getLimitedDouble(gcLottery.getMoney().doubleValue()/(1-room.getFeeAdd())); 
        	} 
        	
        	lotteryDetail.setDeposit(deposit);
        	lotteryDetail.setAddback(0.0d);
        	lotteryDetail.setInoutNum(0.0d);        		
        	lotteryDetail.setUid(user.getId());
        	
        	lotteryDetail.setGameUid(pUser.getGameUserId());
        	lotteryDetail.setLotteryid(gcLottery.getId());
        	lotteryDetail.setMasterId(user.getId());
        	lotteryDetail.setRoomId(room.getId());
        	lotteryDetail.setStatus(0);
        	lotteryDetail.setType(GcHumanLotteryDetail.OPER_ISSUE_PACKET);
        	this.lotteryService.save(GcHumanLotteryDetail.class, lotteryDetail);
        }
        
        final Message redMessage = BeanUtils.map(message, Message.class);
        redMessage.setContent(gcLottery);
        MessageUtils.broadcast(room, redMessage);
        if (listener != null) {
            try {
                listener.onRed(builder);
            }
            catch (GameException e2) {
                RedMessageProcessor.LOGGER.error(e2.getMessage());
                MessageUtils.sendCMD(session, "alert", e2.getMessage());
            }
        }
    }
    
    private double getLimitedDouble(Double d) {
    	BigDecimal bd = new BigDecimal(d);
    	return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
    /**
     * @param room - 房间实例 
     * @param money - 红包金额
     * @param number - 发包数量
     * @return
     */
    private double getCommission(Room room, BigDecimal money, int number) {
    	String type = room.getType();
    	Double feeAdd = room.getFeeAdd();
    	if (type.startsWith("G01")) { //接龙 
    		return feeAdd == null ? 0 : feeAdd.doubleValue();
    	} else if (type.startsWith("G02")) { //牛牛
    		//系统抽水的值是红包个数(number)*单人抽水值，而红包本身的金额是系统出的，所以需要减掉这个数，才是系统得到的佣金
    		return feeAdd == null ? getLimitedDouble(0 - money.doubleValue()) : getLimitedDouble(feeAdd * number - money.doubleValue());
    	} else if (type.startsWith("G04")) { //扫雷
    		return feeAdd == null ? 0 : getLimitedDouble(feeAdd * money.doubleValue());
    	} else {
    		return 0.0f;
    	}
    }
    
    protected LotteryFactory.DefaultLotteryBuilder generateLottery(final Message message, final WebSocketSession session, final Room room, final User user) {
        try {
            final Map<String, Object> body = (Map<String, Object>)message.getContent();
            final BigDecimal money = ConversionUtils.convert(body.get("money"), BigDecimal.class);
            final Integer number = ConversionUtils.convert(body.get("number"), Integer.class);
            final LotteryFactory.DefaultLotteryBuilder builder = LotteryFactory.getDefaultBuilder(money, number).setType("1").setExpiredSeconds(40).setSender(user.getId()).setRoomId(room.getId());
            final String description = body.get("description").toString();
            if (!StringUtils.isEmpty((CharSequence)description)) {
                builder.setDescription(description);
            }
            return builder;
        }
        catch (Exception e) {
            e.printStackTrace();
            MessageUtils.sendCMD(session, "alert", "发送红包失败");
            return null;
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RedMessageProcessor.class);
    }
}

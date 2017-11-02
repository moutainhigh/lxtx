package org.takeback.chat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.*;
import org.takeback.chat.entity.GcHumanLotteryDetail;
import org.takeback.chat.entity.GcLotteryDetail;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.entity.PcConfig;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.service.LotteryService;
import org.takeback.chat.service.RoomService;
import org.takeback.chat.service.UserService;
import org.takeback.util.BeanUtils;
import org.takeback.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)  //使用junit4进行测试  
@ContextConfiguration   
({"/spring/spring-test.xml"})
public class TestStore {
	
	@Autowired
	RoomService roomService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	LotteryService lotteryService;
	
	@Test
	@Ignore
	public void testLoadRoom() {
		GcRoom room = roomService.getRoomByType("G011");
		Assert.assertEquals(room.getCatalog(), "C01");
		
		room.setCreatedate(new Date());
		room.setId("J"+System.currentTimeMillis());
		room.setName("红包接龙1835");
		room.setOwner(1835);
		Random random = new Random();
		int pwd = random.nextInt(RandomUtil.getRandomNumInTwoIntNum(100000, 999999));
		room.setPsw(pwd+"");
		room.setStatus("0");
		room.setSumFee(0.0);
		room.setSumPool(0.0);
		room.setSumPack(0);
		roomService.saveRoom(room);
	}
	
	@Test
	@Ignore
	public void testSaveRoomProps() {
		Map<String, Object> propList = roomService.getRoomProperties("J001");
		propList.put("conf_money", "5");
		String roomId = "J"+System.currentTimeMillis();
		roomService.saveRoomProps(roomId, propList);
		
		Map<String, Object> map = roomService.getRoomProperties(roomId);
		System.out.println(map.keySet().size());
	}
	
	@Test
	@Ignore
	public void testQueryConfig() {
		PcConfig config = this.userService.getPcConfig("plaza_host");
		System.out.println(config.getVal());
	}
	
	@Test
	@Ignore
	public void testSaveGcLotteryDetail() {
		GcLotteryDetail lotteryDetail = new GcLotteryDetail();
		lotteryDetail.setUid(1844);
		lotteryDetail.setCoin(BigDecimal.ZERO);
		lotteryDetail.setCreateDate(new Date());
		lotteryDetail.setDeposit(5.0d);
		lotteryDetail.setDesc1("desc");
		lotteryDetail.setGameType("G04");
		lotteryDetail.setGameUid(23883);
		lotteryDetail.setAddback(10.0d);
		lotteryDetail.setInoutNum(5.0d);
		lotteryDetail.setMasterId(1848);
		lotteryDetail.setLotteryid("LT201701241917194530005");
		lotteryDetail.setRoomId("S006");
		lotteryDetail.setStatus(0);
		
		final GcHumanLotteryDetail humanLotteryDetail = BeanUtils.map(lotteryDetail, GcHumanLotteryDetail.class);
		humanLotteryDetail.setChnno(3001);
		humanLotteryDetail.setType(1);
		lotteryService.saveHumanLotteryDetail(humanLotteryDetail);
	}
	
	@Test
	public void testRetrieveGameUser() {
		PubUser user = this.userService.getByGameAccount(23, 10000001);
		Assert.assertNotNull(user);
	}
	
}

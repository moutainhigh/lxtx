// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.entity.GcRoomProperty;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.room.RoomStore;
import org.takeback.service.BaseService;
import org.takeback.util.RandomUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@Service
public class RoomService extends BaseService {
	@Autowired
	RoomStore roomStore;
	private static int ONCE_FETCH_COUNT;

	@Transactional(readOnly = true)
	public List<GcRoom> getRooms(final int pageNo, final Map<String, Object> params) {
		return this.dao.find(GcRoom.class, params, RoomService.ONCE_FETCH_COUNT, pageNo, "hot desc,createdate desc");
	}

	@Transactional(readOnly = true)
	public List<GcRoom> getActivedRooms() {
		return this.dao.findByHql("from GcRoom a where a.status != '9' order by a.hot desc,a.createdate desc", null);
	}
	@Transactional(readOnly = true)
	public GcRoom getRoomById(final String id) {
		try {
			List<GcRoom> roomList =  this.dao.findByHql("from GcRoom where id=:id ", ImmutableMap.of("id", id));
			if (roomList != null && roomList.size() > 0) {
				return roomList.get(0);
			} else {
				return null;
			}			
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getRoomProperties(final String roomId) {
		final Map<String, Object> param = new HashMap<String, Object>();
		param.put("roomId", roomId);
		final List<GcRoomProperty> list = this.dao
				.findByHql("from GcRoomProperty a where roomId=:roomId order by id asc ", param);
		final Map<String, Object> res = new HashMap<String, Object>();
		for (final GcRoomProperty prop : list) {
			res.put(prop.getConfigKey(), prop.getConfigValue());
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public int getUserRoomCount(final Integer userId) {
		return (int) this.dao.count(GcRoom.class, ImmutableMap.of("owner", userId));
	}

	@Transactional(readOnly = true)
	public List<GcRoom> getUserRooms(final Integer userId, final int pageSize, final int pageNo) {
		return this.dao.findByHqlPaging("from GcRoom where owner=:owner", ImmutableMap.of("owner", userId), pageSize,
				pageNo);
	}
	
	@Transactional(readOnly = true)
	public GcRoom getRoomByType(final String type) {
		try {
			List<GcRoom> roomList =  this.dao.findByHql("from GcRoom where type=:type and hot='0'", ImmutableMap.of("type", type));
			if (roomList != null && roomList.size() > 0) {
				return roomList.get(0);
			} else {
				return null;
			}			
		} catch (Exception e) {
			return null;
		}

	}
	
	/**
	 * save a room
	 * @param room
	 */
	@Transactional
	public void saveRoom(GcRoom room) {
		this.dao.save(GcRoom.class, room);
	}

	@Transactional
	public void initRoomStatus() {
		this.dao.executeUpdate("update GcRoom set status ='0' where catalog !='C03' and status!='9' ", new HashMap<String, Object>());
		final List<GcRoom> list = this.dao.findByHql("from GcRoom");
		for (final GcRoom rm : list) {
			this.roomStore.reload(rm.getId());
		}
	}

	@Transactional
	public void saveRoomProps(String roomId, Map<String, Object> props) {
		for (String key : props.keySet()) {
			GcRoomProperty property = new GcRoomProperty();
			property.setRoomId(roomId);
			property.setConfigKey(key);
			property.setConfigValue((String)props.get(key));
			this.dao.save(GcRoomProperty.class, property);			
		}
	}
	
	@Transactional(readOnly = true)
	public List<Map<String, String>> getRoomProps(final String roomId) {
		final List<GcRoomProperty> list = this.dao.findByHql("from GcRoomProperty where roomId=:roomId",
				ImmutableMap.of("roomId", roomId));
		if (list == null || list.isEmpty()) {
			return new ArrayList<Map<String, String>>();
		}
		final List<Map<String, String>> props = new ArrayList<Map<String, String>>(list.size());
		for (final GcRoomProperty gcRoomProperty : list) {
			final String alias = StringUtils.isEmpty(gcRoomProperty.getAlias()) ? gcRoomProperty.getConfigKey()
					: gcRoomProperty.getAlias();
			final Map<String, String> map = ImmutableMap.of("key", gcRoomProperty.getConfigKey(), "value",
					gcRoomProperty.getConfigValue(), "alias", alias);
			props.add(map);
		}
		return props;
	}

	static {
		RoomService.ONCE_FETCH_COUNT = 5;
	}

	private String getRoomTag(String type) {
		if (type.startsWith("G01")) {
			return "J";
		} else if (type.startsWith("G02")) {
			return "N";
		} else if (type.startsWith("G04")) {
			return "S";
		} else {
			return "PC";
		}
	}
	
	private String getRoomName(String type) {
		if (type.startsWith("G01")) {
			return "红包接龙";
		} else if (type.startsWith("G02")) {
			return "红包牛牛";
		} else if (type.startsWith("G04")) {
			return "红包扫雷";
		} else {
			return "PC蛋蛋";
		}
	}
	
	private double getFeeAdd(String type, int conf_money) {
		if (type.startsWith("G01")) {
			if (conf_money <= 5) {
				return 0.25;
			} else if (conf_money <= 10) {
				return 0.5;
			} else if (conf_money <= 30) {
				return 1.5;
			} else if (conf_money <= 50) {
				return 2.5;
			} else if (conf_money <= 100) {
				return 5.0;
			} else if (conf_money <= 200) {
				return 7.5;
			} else {
				return 10.0;
			}
		} else if(type.startsWith("G04")){
			//扫雷
			return 0.2;
		}else if(type.startsWith("G022")){
			//牛牛
			if (conf_money <= 30) {
				return 1.0;
			} else if (conf_money <= 50) {
				return 2.0;
			} else if (conf_money <= 100) {
				return 3.0;
			} else {
				return 3.0;
			}
		}else{
			return 0.0;
		}
	}
	
	@Transactional
	public GcRoom roomApply(String type, int m, int n, Integer uid) {
		//save room instance
		GcRoom existingRoom = this.getRoomByType(type);
		String copyRoomId = existingRoom.getId();
		String newRoomId = this.getRoomTag(type) + System.currentTimeMillis(); 
		GcRoom room = new GcRoom();
		room.setCreatedate(new Date());
		room.setId(newRoomId);
		room.setCatalog("C03");
		room.setType(type);
		room.setLimitNum(existingRoom.getLimitNum());
		room.setDescription(existingRoom.getDescription());
		room.setDetail(existingRoom.getDetail());
		room.setName(this.getRoomName(type) + uid);
		room.setOwner(uid);
		Random random = new Random();
		int pwd = random.nextInt(RandomUtil.getRandomNumInTwoIntNum(100000, 999999));
		room.setPsw(pwd+"");
		room.setRoomimg(existingRoom.getRoomimg());
		room.setFeeAdd(this.getFeeAdd(type, m));
		room.setStatus("0");
		room.setSumFee(0.0);
		room.setSumPool(0.0);
		room.setSumPack(0);
		this.saveRoom(room);
		
		//save room props
		Map<String, Object> propList = this.getRoomProperties(copyRoomId);
		if (type.startsWith("G01") || type.startsWith("G02")) {
			propList.put("conf_money", ""+m);	
			propList.put("conf_size", ""+n);	
		} else {
			propList.put("conf_max_size", ""+n);
			propList.put("conf_min_size", ""+n);
			propList.put("conf_max_money", ""+m);				
			propList.put("conf_min_money", ""+m);				
		}
		
		this.saveRoomProps(newRoomId, propList);		
		return room;
	}

	@Transactional(readOnly = true)	
	public GcRoom getRoomByTypeAndPwd(String roomType, String password) {
		try {
			List<GcRoom> roomList =  this.dao.findByHql("from GcRoom where type=:type and psw=:psw", ImmutableMap.of("type", roomType, "psw", password));
			if (roomList != null && roomList.size() > 0) {
				return roomList.get(0);
			} else {
				return null;
			}			
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public Map<String, Integer> getPrivateRoomStat() {
		List<GcRoom> roomList = this.dao.findByHql("from GcRoom where status != '9' and catalog = 'C03'");
		Map<String, Integer> map = Maps.newHashMap();
		for (GcRoom room : roomList) {
			Room roomCache = this.roomStore.get(room.getId());
			int userCnt = roomCache.getUsers().keySet().size();
			
			Integer roomUserCntSum = map.get(room.getType());
			if (roomUserCntSum == null) {
				roomUserCntSum = userCnt; 
			} else {
				roomUserCntSum += userCnt;
			}
			map.put(room.getType(), roomUserCntSum);
		}
		return map;
	}
}

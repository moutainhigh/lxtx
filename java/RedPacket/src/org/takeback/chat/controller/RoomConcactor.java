// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.takeback.util.annotation.AuthPassport;
import java.util.Iterator;
import java.util.List;
import org.takeback.chat.entity.GcRoom;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;
import org.springframework.web.util.WebUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMethod;
import org.takeback.mvc.ResponseUtils;
import java.io.Serializable;
import org.takeback.chat.store.room.Room;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import org.takeback.chat.store.user.UserStore;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.RoomService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping({ "/room" })
public class RoomConcactor
{
    @Autowired
    private RoomService roomService;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    private final Lock lock;
    
    public RoomConcactor() {
        this.lock = new ReentrantLock();
    }
    
    @RequestMapping(value = { "/{id}" }, method = { RequestMethod.GET })
    public ModelAndView getRoom(@PathVariable final String id) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(404, "房间" + id + "不存在.");
        }
        return ResponseUtils.jsonView(200, "success", room);
    }
    
    @RequestMapping(value = { "/authorize" }, method = { RequestMethod.POST })
    public ModelAndView validatePassword(@RequestBody final Map<String, String> params) {
        final String roomId = params.get("roomId");
        final String password = params.get("password");
        
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "房间" + roomId + "不存在.");
        }
        
        String roomType = room.getType();
        GcRoom gRoom = this.roomService.getRoomByTypeAndPwd(roomType, password);
        if (!room.isNeedPsw()) {
            return ResponseUtils.jsonView(200, "success");
        } else if (gRoom != null && password.equals(gRoom.getPsw())) {
        	if(gRoom.getStatus().equals("9")){
        		return ResponseUtils.jsonView(404, "房间" + roomId + "不存在.");
        	}
        	return ResponseUtils.jsonView(200, "success", ImmutableMap.of("roomId", gRoom.getId()));
        }
        return ResponseUtils.jsonView(401, "incorrect password");
    }
    
    @RequestMapping({ "/join/{id}" })
    public ModelAndView joinIn(@PathVariable final String id, final HttpServletRequest request) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(404, "房间" + id + "不存在.");
        }
        final String lastRoomId = (String)WebUtils.getSessionAttribute(request, "roomId");
        if (lastRoomId != null) {
            WebUtils.setSessionAttribute(request, "lastRoomId", (Object)lastRoomId);
        }
        WebUtils.setSessionAttribute(request, "roomId", (Object)id);
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid != null) {
            try {
                this.lock.lock();
                if (room.getPosition() >= room.getLimitNum()) {
                    return ResponseUtils.jsonView(530, "房间已满.");
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        final Map<String, Object> body = Maps.newHashMap();
        body.put("room", room);
        body.put("uid", uid);
        return ResponseUtils.jsonView(200, "success", body);
    }
    
    @RequestMapping({ "/list/{pageNo}" })
    public ModelAndView rooms(@PathVariable final Integer pageNo, final HttpServletRequest request) {
    	long time1 = System.currentTimeMillis();
        String s = "";
        final Map<String, Object> params = Maps.newHashMap();
        final String cata = request.getParameter("cata");
        final String type = request.getParameter("type");
        if (!StringUtils.isEmpty((CharSequence)cata)) {
            s = " and a.catalog = :p";
            params.put("p", cata);
        }
        if (!StringUtils.isEmpty((CharSequence)type)) {
            s = " and a.type = :p";
            params.put("p", type);
        }
        final List<GcRoom> rooms = this.roomService.findByHql("from GcRoom a where a.status !='9' and a.catalog != 'C03'" + s + " order by a.hot desc, a.createdate desc", params, 100, pageNo);
        if (rooms == null || rooms.size() == 0) {
            return ResponseUtils.jsonView(new ArrayList());
        }
        
        long cost1 = System.currentTimeMillis() - time1;
        System.out.println("cost time in rooms list, "+cost1+" milli seconds");
        final List<Room> result = Lists.newArrayList();
        Map<String, Integer> roomStat = this.roomService.getPrivateRoomStat();
        for (final GcRoom room : rooms) {
        	if (!Strings.isNullOrEmpty(room.getPsw())) {
        		Room tmpRoom = this.roomStore.get(room.getId());
        		Integer stat = roomStat.get(tmpRoom.getType());
        		if (stat != null) {
        			tmpRoom.setPosition(stat);
        		} else {
        			tmpRoom.setPosition(0);
        		}
        		tmpRoom.setDetail("");
        		result.add(tmpRoom);
        	} else {
        		Room r = this.roomStore.get(room.getId());
        		r.setDetail("");
        		result.add(r);
        	}
        }
        
        long cost2 = System.currentTimeMillis() - time1 - cost1;
        System.out.println("cost time in rooms list 2, "+cost2+" milli seconds");
        return ResponseUtils.jsonView(result);
    }
    
    @AuthPassport
    @RequestMapping({ "/members/{id}" })
    public ModelAndView members(@PathVariable final String id) {
        final Room room = this.roomStore.get(id);
        if (room == null) {
            return ResponseUtils.jsonView(Lists.newArrayList());
        }
        return ResponseUtils.jsonView(room.getUsers().values());
    }
    
    @AuthPassport
    @RequestMapping(value = { "/props" }, method = { RequestMethod.GET })
    public ModelAndView getRoomProps(@RequestParam final String roomId) {
        final Room room = this.roomStore.get(roomId);
        if (room == null) {
            return ResponseUtils.jsonView(404, "房间" + roomId + "不存在.");
        }
        final Map<String, Object> res = new HashMap<String, Object>();
        res.put("room", this.roomService.get(GcRoom.class, roomId));
        res.put("props", this.roomService.getRoomProps(roomId));
        return ResponseUtils.jsonView((Object)res);
    }
}

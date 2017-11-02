// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.takeback.chat.entity.PubUser;
import org.takeback.util.BeanUtils;
import java.util.Iterator;
import org.takeback.chat.store.user.User;
import org.takeback.chat.store.room.Room;
import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.chat.entity.GcRoom;
import java.util.ArrayList;
import org.takeback.util.exp.ExpressionProcessor;
import org.takeback.util.converter.ConversionUtils;
import java.util.List;
import org.takeback.util.exception.CodedBaseRuntimeException;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import org.takeback.chat.store.user.RobotUser;
import java.util.Stack;
import java.util.Map;
import org.takeback.chat.store.user.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListService;

@Service("robotAdminService")
public class RobotService extends MyListService
{
    @Autowired
    RoomStore roomStore;
    @Autowired
    private UserStore userStore;
    private Map<Integer, Thread> ts;
    private Stack<RobotUser> freeRobots;
    private Map<String, Stack<RobotUser>> workingRobots;
    
    public RobotService() {
        this.ts = new HashMap<Integer, Thread>();
        this.freeRobots = null;
        this.workingRobots = new HashMap<String, Stack<RobotUser>>();
    }
    
    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> list(final Map<String, Object> req) {
        final String entityName = "GcRoom";
        if (StringUtils.isEmpty((CharSequence)entityName)) {
            throw new CodedBaseRuntimeException(404, "missing entityName");
        }
        final int limit = Integer.parseInt(req.get(RobotService.LIMIT).toString());
        final int page = Integer.parseInt(req.get(RobotService.PAGE).toString());
        final List<?> cnd = ConversionUtils.convert(req.get(RobotService.CND),List.class);
        String filter = null;
        if (cnd != null) {
            filter = ExpressionProcessor.instance().toString(cnd);
        }
        final String orderInfo = req.get("id").toString();
        final List<GcRoom> ls = this.dao.query(entityName, filter, limit, page, orderInfo);
        this.afterList(ls);
        final List<Map> list = new ArrayList<Map>();
        for (int i = 0; i < ls.size(); ++i) {
            final GcRoom room = ls.get(i);
            final Map<String, Object> m = new HashMap<String, Object>();
            final int num = (this.workingRobots.get(room.getId()) == null) ? 0 : this.workingRobots.get(room.getId()).size();
            m.put("id", room.getId());
            m.put("roomName", room.getName());
            m.put("robotNum", num);
            list.add(m);
        }
        final long count = this.dao.totalSize(entityName, filter);
        final Map<String, Object> result = new HashMap<String, Object>();
        result.put("totalSize", count);
        result.put("body", list);
        return result;
    }
    
    @Transactional(readOnly = true)
    @Override
    public Object load(final Map<String, Object> req) {
        final Object pkey = req.get("id");
        final GcRoom room = this.dao.get(GcRoom.class, pkey.toString());
        final Map<String, Object> entity = new HashMap<String, Object>();
        entity.put("id", room.getId());
        entity.put("roomName", room.getName());
        entity.put("robotNum", (this.workingRobots.get(room.getId()) == null) ? 0 : this.workingRobots.get(room.getId()).size());
        return entity;
    }
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String id = data.get("id").toString();
        final int num = Integer.valueOf(data.get("robotNum").toString());
        final Room rm = this.roomStore.get(id);
        final int curNum = (this.workingRobots.get(id) == null) ? 0 : this.workingRobots.get(id).size();
        int change = num - curNum;
        if (change > 0) {
            final List<RobotUser> robots = this.getFreeRobots(change);
            Stack<RobotUser> stack = this.workingRobots.get(rm.getId());
            if (stack == null) {
                stack = new Stack<RobotUser>();
                this.workingRobots.put(rm.getId(), stack);
            }
            for (final RobotUser ru : robots) {
                ru.setRoom(rm);
                this.userStore.reload(ru.getId());
                rm.join(ru);
                stack.push(ru);
                final Thread t = new Thread(ru);
                this.ts.put(ru.getId(), t);
                t.start();
            }
        }
        else {
            change = Math.abs(change);
            final Stack<RobotUser> stack2 = this.workingRobots.get(rm.getId());
            if (stack2 == null) {
                return;
            }
            for (int i = 0; i < change; ++i) {
                final RobotUser r = stack2.pop();
                final Thread t2 = this.ts.get(r.getId());
                try {
                    t2.interrupt();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.ts.remove(r.getId());
                rm.left(r);
                this.freeRobots.add(r);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<RobotUser> getFreeRobots(final int num) {
        if (this.freeRobots == null) {
            this.freeRobots = new Stack<RobotUser>();
            final String hql = "from PubUser where userType=9  order by id asc";
            final List<PubUser> rs = this.dao.findByHql(hql);
            for (int i = 0; i < rs.size(); ++i) {
                final RobotUser r = BeanUtils.map(rs.get(i), RobotUser.class);
                this.freeRobots.push(r);
            }
        }
        if (this.freeRobots.size() < num) {
            throw new CodedBaseRuntimeException("空闲机器人:" + this.freeRobots.size() + "个");
        }
        final List<RobotUser> robots = new ArrayList<RobotUser>();
        for (int j = 0; j < num; ++j) {
            robots.add(this.freeRobots.pop());
        }
        return robots;
    }
}

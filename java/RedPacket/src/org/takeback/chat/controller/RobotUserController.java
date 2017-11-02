// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.controller;

import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Iterator;
import org.takeback.chat.store.user.User;
import java.io.Serializable;
import org.takeback.chat.store.user.RobotUser;
import org.takeback.chat.store.room.Room;
import java.util.Collection;
import org.springframework.web.bind.annotation.RequestMapping;
import org.takeback.mvc.ResponseUtils;

import com.google.common.base.Strings;

import org.springframework.web.util.WebUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.takeback.chat.store.room.RoomStore;
import org.takeback.chat.service.RobotService;
import org.takeback.chat.store.user.UserStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Controller
public class RobotUserController
{
    @Autowired
    private UserService userService;
    @Autowired
    private UserStore userStore;
    @Autowired
    private RobotService robotService;
    @Autowired
    private RoomStore roomStore;
    private List<Thread> ts;

    private static Logger logger = LoggerFactory.getLogger(RobotUserController.class);
    
    public void setThreads(List<Thread> ts) {
    	this.ts = ts;
    }
    
    public RobotUserController() {
        this.ts = new ArrayList<Thread>();
    }
    
    @RequestMapping({ "/robots" })
    public ModelAndView robots(final HttpServletRequest request) {
        final Integer uid = (Integer)WebUtils.getSessionAttribute(request, "$uid");
        if (uid == null) {
            return ResponseUtils.jsonView(403, "notLogin");
        }
        return ResponseUtils.jsonView(uid);
    }
    
    @RequestMapping(value = { "/joinRobot" }, method = { RequestMethod.GET })
    public ModelAndView join(final HttpServletRequest request) {
        try {
            final List<Room> rms = this.roomStore.getByCatalog("C01");
            rms.addAll(this.roomStore.getByCatalog("C02"));
            String add = request == null ? "" : request.getParameter("add");
            boolean toAdd = !Strings.isNullOrEmpty(add);
            
            for (final Room rm : rms) {
            	//房间被锁或者是私人房间，则机器人不进入
                if ("9".equals(rm.getStatus()) || rm.getOwner() > 0 || !Strings.isNullOrEmpty(rm.getPsw())) {
                    continue;
                }
                if (rm.getUsers().size() >= 20) { //最多只能进入20个玩家
                	continue;
                }
                
                if (toAdd && rm.getUsers().size() > 0) {
                	continue;
                }
                
                Random random = new Random();
                int robotCount = random.nextInt(8) + 8;
                final List<RobotUser> robots = this.robotService.load(robotCount);
                
                logger.warn("now add robots to room:"+rm.getName() + " robot count:" + robots.size());
                
                if (robots.size() == 0) {
                    return ResponseUtils.jsonView(500, "error", "机器人不够用");
                }
                for (int i = 0; i < robots.size(); ++i) {
                    final RobotUser r = robots.get(i);
                    r.setRoom(rm);
                    this.userStore.reload(r.getId());
                    rm.join(r);
                    final Thread t = new Thread(r);
                    this.ts.add(t);
                    logger.warn("start the thread, " + r.getNickName() + rm.getId());
                    t.start();
                }
                
                if (rm.getType().startsWith("G02") || rm.getType().startsWith("G01") ) { //牛牛或接龙，自动启动
                	rm.start();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseUtils.jsonView(200, "OK");
    }
    
    @RequestMapping(value = { "/show" }, method = { RequestMethod.GET })
    public ModelAndView show(final HttpServletRequest request) {
        for (int i = 0; i < this.ts.size(); ++i) {
            final Thread t = this.ts.get(i);
            System.out.println(this.ts.get(i) + " alive:" + t.isInterrupted() + " isInterrupted:" + t.isDaemon() + " isDaemon:" + t.isAlive());
        }
        return ResponseUtils.jsonView(200, "OK");
    }
}

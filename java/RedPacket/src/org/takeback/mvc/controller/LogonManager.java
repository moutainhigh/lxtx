package org.takeback.mvc.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import org.takeback.core.accredit.result.AuthorizeResult;
import org.takeback.core.app.Application;
import org.takeback.core.app.ApplicationController;
import org.takeback.core.app.ApplicationNode;
import org.takeback.core.organ.OrganController;
import org.takeback.core.organ.Organization;
import org.takeback.core.role.Role;
import org.takeback.core.service.InitializeService;
import org.takeback.core.user.AccountCenter;
import org.takeback.core.user.User;
import org.takeback.core.user.UserRoleToken;
import org.takeback.mvc.ServletUtils;
import org.takeback.mvc.filter.AdminboardFilter;
import org.takeback.service.BaseService;
import org.takeback.util.MD5StringUtil;
import org.takeback.util.context.Context;
import org.takeback.util.converter.ConversionUtils;
import org.takeback.util.exception.CodedBaseRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;

import static org.takeback.mvc.ResponseUtils.createBody;

/**
 * 登录的相关操作
 */
@RestController("mvcLogonManager")
public class LogonManager {
    private static final Logger log = LoggerFactory.getLogger(LogonManager.class);

    @Autowired
    private BaseService baseService;

    @Autowired
    private InitializeService initializeService;

    @RequestMapping(value = "/logon/loadRoles", method = RequestMethod.POST)
    public Map<String, Object> logon(@RequestBody Map<String, String> req, HttpServletRequest request){
        String uid = req.get("account");
        String psw = req.get("password");
        if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(psw)){
            return createBody(501, "用户名或者密码不能为空！");
        }
        String verifycode = req.get("verifycode");
        if(StringUtils.isEmpty(verifycode) || !verifycode.equalsIgnoreCase((String)WebUtils.getSessionAttribute(request, CodeController.VERIFY_CODE))){
            return createBody(504, "验证码不正确");
        }
        WebUtils.setSessionAttribute(request, CodeController.VERIFY_CODE, null);	//图片验证码立马失效
        User user = AccountCenter.getUser(uid);
        if(user == null){
            return createBody(503, "用户不存在");
        }
        if(!user.validatePassword(psw)){
            return createBody(502, "密码不正确");
        }
        Collection<UserRoleToken> urts = user.getUserRoleTokens();
        if(urts.size() < 1){
            return createBody(505, "没有设置角色");
        }
        UserRoleToken urt = urts.iterator().next();
        if(urt.getRole() == null){
            return createBody(506, "所属角色不存在");
        }
        if(urt.getOrgan() == null){
            return createBody(507, "所属机构不存在");
        }

        HashMap urtMap = ConversionUtils.convert(urt, HashMap.class);
        Organization organ = OrganController.getRoot();
        urtMap.put("company", organ.getName());
        urtMap.put("system", organ.getProperties().get("system"));
        urtMap.put("version", organ.getProperties().get("version"));
        urtMap.put("telephone", organ.getProperties().get("telephone"));
        urtMap.put("email",organ.getProperties().get("email"));
        urtMap.put("address",organ.getProperties().get("address"));

        AccountCenter.reloadUser(uid);
        AccountCenter.getUser(uid);
        Timestamp logintime = new Timestamp(new Date().getTime());

        user.setLastsignintime(logintime);
        user.setLastsigninip(ServletUtils.getClientIP(request));

        baseService.update(User.class, user);

        WebUtils.setSessionAttribute(request, Context.UID, user.getId());   //uid
        WebUtils.setSessionAttribute(request, Context.USER_ROLE_TOKEN, urt.getId());    //urtid
        log.info(uid + " logon with role " + urt.getRoleid() + " at " + new DateTime().toString("yyyy-MM-dd HH:mm:ss")+",IP:"+ServletUtils.getClientIP(request));
        return createBody(urtMap);
    }

    @RequestMapping(value = "/logon/loadApps", method = RequestMethod.POST)
    public Map<String, Object> loadApps(HttpServletRequest request){
        if(ServletUtils.isLogonExpired(request)){
            return createBody(403, "notLogon");
        }
        String uid = (String) WebUtils.getSessionAttribute(request, Context.UID);
        Long urtid = (Long) WebUtils.getSessionAttribute(request, Context.USER_ROLE_TOKEN);
        UserRoleToken urt = AccountCenter.getUser(uid).getUserRoleToken(urtid);
        Role role = urt.getRole();
        Set<String> installApps = OrganController.getRoot().installedApps();
        List<Object> apps = Lists.newArrayList();
        for(String app:installApps){
            Application application = ApplicationController.instance().get(app);
            if(application == null){
                continue;
            }
            AuthorizeResult result = role.authorize("apps", application.getFullId());
            if(result != null && result.getResult() == AuthorizeResult.NO){
                continue;
            }
            Map<String, Object> levelApp = Maps.newHashMap();
            levelApp.put("id", application.getFullId());
            levelApp.put("text", application.getName());
            levelApp.put("iconCls",application.getIconCls());
            levelApp.put("level", "app");
            filterCata(levelApp, application, role);
            apps.add(levelApp);
        }
        return createBody(apps);
    }

    private void filterCata(Map<String, Object> m, Application a, Role r){
        List<Object> catas = Lists.newArrayList();
        m.put("children", catas);
        List<ApplicationNode> ls = a.getItems();
        for(ApplicationNode c:ls){
            AuthorizeResult result = r.authorize("apps", c.getFullId());
            if(result != null && result.getResult() == AuthorizeResult.NO){
                continue;
            }
            Map<String, Object> levelCata = Maps.newHashMap();
            levelCata.put("id", c.getFullId());
            levelCata.put("text", c.getName());
            levelCata.put("iconCls", c.getIconCls());
            levelCata.put("level", "category");
            filterModule(levelCata, c, r);
            catas.add(levelCata);
        }
    }

    private void filterModule(Map<String, Object> m, ApplicationNode a, Role r){
        List<Object> modules = Lists.newArrayList();
        m.put("children", modules);
        List<ApplicationNode> ls = a.getItems();
        for(ApplicationNode c:ls){
            AuthorizeResult result = r.authorize("apps", c.getFullId());
            if(result != null && result.getResult() == AuthorizeResult.NO){
                continue;
            }
            Map<String, Object> levelModule = Maps.newHashMap();
            levelModule.put("id", c.getFullId());
            levelModule.put("text", c.getName());
            levelModule.put("iconCls", c.getIconCls());
            levelModule.put("level", "module");
            levelModule.put("leaf", "true");
            modules.add(levelModule);
        }
    }


    @RequestMapping(value = "/adminboard/initSystem", method = RequestMethod.POST)
    public void initAdminboard(@RequestParam String username,@RequestParam String passwd,@RequestParam String repasswd, HttpServletRequest request,HttpServletResponse response){

        try{
            response.setContentType("text/html;charset=gbk");
            PrintWriter pw = response.getWriter();

            if(initializeService.queryInitialized()){
                throw new CodedBaseRuntimeException("Illegal operate ！");
            }

            if(!passwd.equals(repasswd)){
                pw.println("<html><body>");
                pw.println("<h2>错误:密码不一致</h2>");
                pw.println("<body/><html/>");
            }else if(passwd.length() < 6){
                pw.println("<html><body>");
                pw.println("<h2>错误:密码长度不合法</h2>");
                pw.println("<body/><html/>");
            }else if(username.trim().length() < 4){
                pw.println("<html><body>");
                pw.println("<h2>错误:用户名长度不合法,长度应大于4！</h2>");
                pw.println("<body/><html/>");
            }else{
                User user = new User();
                user.setId(username.trim());
                user.setPassword(MD5StringUtil.MD5Encode(passwd.trim()));

                UserRoleToken roleToken = new UserRoleToken();
                roleToken.setUserid(username.trim());
                roleToken.setRoleid("roles.admin");
                roleToken.setOrganid("rdcenter");

                initializeService.initUser(user, roleToken);
                AdminboardFilter.systemInitialized = true;
                response.sendRedirect("/adminboard");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}

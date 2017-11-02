package com.neo.web;

import com.neo.entity.User;
import com.neo.logic.Constants;
import com.neo.logic.WebTool;
import com.neo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Resource
    UserService userService;

    @RequestMapping("/user/showsignin")
    public String showsignin(Model model, HttpServletRequest req) {
    	model.addAttribute("message", WebTool.getReqValue(req, "message"));
    	return "user/signinpage";
    }
    
    @RequestMapping("/user/signin")
    public String signin(Model model, HttpServletRequest req) {
    	String name = WebTool.getReqValue(req, "username");
    	String pass = WebTool.getReqValue(req, "password");
    	
    	User user = this.userService.findUserByNameAndPass(name, pass);
    	if (user == null) {
    		return "redirect:/user/showsignin?message=invalid_account_info";
    	} else {
    		WebTool.setSessionValue(req, Constants.SESSION_USER, user);
    		return "redirect:/task/list";
    	}
    }
}

package com.lxtx.controller.thirdparty;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;
import com.lxtx.dao.CloudSystemConfigMapper;
import com.lxtx.model.CloudUser;
import com.lxtx.util.Constant;

@Controller
@RequestMapping(value = "/red")
public class RedPacketPromoter {
	
	private static final String SESSION_TAG = "session_tag";
	
	@Autowired
	private CloudSystemConfigMapper cloudSystemConfigMapper;
	
	@RequestMapping(value = "/entryred")
	public RedirectView enterRedPacket(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//save the time into session as the tag
		request.getSession().setAttribute(SESSION_TAG, System.currentTimeMillis());
		
		cloudSystemConfigMapper.selectByProperty("");
		return new RedirectView("");
	}
	
	@RequestMapping(value = "/displayred")
	public ModelAndView displayRedPacket(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String tag = (String)request.getSession().getAttribute(SESSION_TAG);
		if (Strings.isNullOrEmpty(tag)) {
			ModelAndView mav = new ModelAndView("/red/blank");
			return mav;
		} else {
			ModelAndView mav = new ModelAndView("/red/open");
			return mav;			
		}
	}
	
}

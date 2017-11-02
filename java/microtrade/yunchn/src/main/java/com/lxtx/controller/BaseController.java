package com.lxtx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.lxtx.util.ErrorCode;


public class BaseController {
    public static final Logger log = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler(Exception.class) 
	public ModelAndView handleException(Exception ex) {
		ex.printStackTrace();
		ModelAndView mav = new ModelAndView("error/error");
		mav.addObject("code",ErrorCode.ERROR);
		mav.addObject("data",ex.getMessage());
		return mav;
	}
}

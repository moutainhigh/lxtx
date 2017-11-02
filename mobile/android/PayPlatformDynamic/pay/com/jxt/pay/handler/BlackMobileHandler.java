package com.jxt.pay.handler;

import com.jxt.pay.pojo.BlackMobile;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class BlackMobileHandler extends SimpleIbatisEntityHandler<BlackMobile>{

	private static final String SQL_ISBLACK = "isBlack";
	
	/**
	 * 是否在黑名单中
	 * @param mobile
	 * @return
	 */
	public boolean isBlack(String mobile){
		return (Integer)queryForObject(SQL_ISBLACK,mobile) > 0;
	}
}

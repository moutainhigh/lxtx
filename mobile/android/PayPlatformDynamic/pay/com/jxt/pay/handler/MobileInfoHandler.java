package com.jxt.pay.handler;

import com.jxt.pay.pojo.MobileInfo;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class MobileInfoHandler extends SimpleIbatisEntityHandler<MobileInfo>{

	private static final String SQL_UPDATEISBLACK = "updateIsBlack";

	private static final String SQL_UPDATEMOBILE1 = "updateMobile1";
	
	private static final String SQL_GETMOBILE1 = "getMobile1";

	public void updateIsBlack(MobileInfo mobileInfo) {
		update(SQL_UPDATEISBLACK,mobileInfo);
	}

	public void updateMobile1(MobileInfo mobileInfo) {
		update(SQL_UPDATEMOBILE1,mobileInfo);
	}
	
	public String getMobile1(long id){
		return queryForObject(SQL_GETMOBILE1,id);
	}
}

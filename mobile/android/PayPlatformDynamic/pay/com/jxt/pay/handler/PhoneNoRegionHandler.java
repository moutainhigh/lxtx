package com.jxt.pay.handler;

import com.jxt.pay.pojo.PhoneNoRegion;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class PhoneNoRegionHandler extends SimpleIbatisEntityHandler<PhoneNoRegion>{

	private static final String SQL_GETBYMOBILE = "getByMobile";
	
	public PhoneNoRegion getByMobile(String mobile){
		Long phoneNo = Long.parseLong(mobile);
		return queryForObject(SQL_GETBYMOBILE, phoneNo);
	}
}

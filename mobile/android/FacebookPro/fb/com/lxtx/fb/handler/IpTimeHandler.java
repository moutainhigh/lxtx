package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.IpTime;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class IpTimeHandler extends SimpleIbatisEntityHandler<IpTime>{

	public IpTime getOne(String ip) {
		return queryForObject("getOne", ip);
	}

	public int updateOne(IpTime ipTime){
		return update("updateOne", ipTime);
	}
}

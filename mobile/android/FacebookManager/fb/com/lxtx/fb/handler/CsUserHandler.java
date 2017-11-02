package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.CsUser;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class CsUserHandler extends SimpleIbatisEntityHandler<CsUser>{

	public CsUser getByName(String userName) {
		return queryForObject("getByName", userName);
	}

	public void updateTime(CsUser csUser) {
		update("updateTime", csUser);
	}

	public void updatePassword(CsUser csUser) {
		update("updatePassword", csUser);
	}
	
	public void updateStatus(CsUser csUser){
		update("updateStatus", csUser);
	}
	
	public Page<CsUser> search(PageCondition condition){
		return pagedQuery(condition, "cntSearch", "pageSearch");
	}

}

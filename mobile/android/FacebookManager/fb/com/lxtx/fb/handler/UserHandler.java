package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.User;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class UserHandler extends SimpleIbatisEntityHandler<User>{

	public void updateStatus(User user) {
		update("updateStatus", user);
	}

	public void updateLogin(User user) {
		update("updateLogin", user);
	}

	public void updateSetting(User user) {
		update("updateSetting", user);
	}

	public void updatePage(User user) {
		update("updatePage", user);
	}

	public void updateAd(User user) {
		update("updateAd", user);
	}

	public void updateAdCheck(User user) {
		update("updateAdCheck", user);
	}

}

package com.lxtx.fb.handler;

import java.util.List;

import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.util.CommonUtil;
import com.lxtx.fb.task.util.Constants;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class UserHandler extends SimpleIbatisEntityHandler<User>{

	public void updateInvalid(User user){
		
		user.setInvalidDay(CommonUtil.getDay(0));
		
		update("updateInvalid", user);
	} 
	
	/**
	 * 
	 * @param user
	 */
	public void updateLogin(User user){
		user.setLoginDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_LOGIN);
		
		update("updateLogin", user);
	}
	
	public void updateSetting(User user){
		user.setSettingDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_SETTING);
		
		update("updateSetting", user);
	}
	
	public void updatePage(User user){
		user.setPageDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_PAGE);
		
		update("updatePage", user);
	}
	
	public void updateAd(User user){
		user.setAdDay(CommonUtil.getDay(0));
		user.setSettingDay(Constants.USERSTATUS_AD);
		
		update("updateAd", user);
	}
	
	public void updateAdFlag(User user){
//		user.setAdFlagDay(CommonUtil.getDay(0));
//		user.setStatus(Constants.USERSTATUS_ADFLAG);
		
		update("updateAdFlag", user);
	}
	
	public void updateAdFlagContact(User user){
		user.setAdFlagContactDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_ADFLAGCONTACT);
		
		update("updateAdFlagContact", user);
	}
	
	public void updateAdPass(User user){
		user.setAdPassDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_ADPASS);
		
		update("updateAdPass", user);
	}
	
	public void updateAdDelivery(User user){
		user.setAdDeliveryDay(CommonUtil.getDay(0));
		user.setStatus(Constants.USERSTATUS_ADDELIVERY);
		
		update("updateAdDelivery", user);
	}
	
	public void updateStatus(User user){
		update("updateStatus", user);
	}

	public void updatePixel(User user) {
		update("updatePixel", user);
	}

	public List<User> listNoProxy() {
		return queryForList("listNoProxy");
	}

	public void updateProxy(User user) {
		update("updateProxy", user);
	}

	public void updateCookies(User user) {
		update("updateCookies", user);
	}

	public void updateLanguage(User user) {
		update("updateLanguage", user);
	}
	
}

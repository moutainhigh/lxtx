package com.lxtx.fb.service;

import java.util.Date;

import com.lxtx.fb.handler.CsUserHandler;
import com.lxtx.fb.pojo.CsUser;
import com.qlzf.commons.helper.MD5Encrypt;

public class CsUserService {

	public CsUser login(CsUser csUser) {
		
		CsUser loginUser = getLoginUser(csUser);
		
		if(loginUser.valid()){
			csUser.setId(loginUser.getId());
			csUser.setLastLoginTime(new Date());
			
			csUserHandler.updateTime(csUser);
		}
		
		return null;
	}
	
	public int add(CsUser csUser){
		
		String userPass = password(csUser.getUserPass());
		
		csUser.setUserPass(userPass);
		
		csUserHandler.insert(csUser);
		
		return csUser.getId();
	}
	
	public boolean modify(CsUser csUser){
		
		CsUser user = csUserHandler.select(csUser.getId());
		if(user!=null){
			user.setUserPass(password(csUser.getUserPass()));
			csUserHandler.updatePassword(user);
			return true;
		}
		
		return false;
	}

	private CsUser getLoginUser(CsUser csUser){
		if(csUser != null){
			CsUser loginUser = csUserHandler.getByName(csUser.getUserName()); 
		
			if(csUser != null){
				if(comparePassword(csUser.getUserPass(), loginUser.getUserPass())){
					return loginUser;
				}
			}
		}
		
		return new CsUser();
	}
	
	private boolean comparePassword(String loginPassword, String password) {
		String passwordEncrypt = password(loginPassword);
		return passwordEncrypt.equals(password);
	}
	
	private String password(String password) {
		return MD5Encrypt.MD5Encode(password);
	}
	
	
	//ioc
	private CsUserHandler csUserHandler;

	public void setCsUserHandler(CsUserHandler csUserHandler) {
		this.csUserHandler = csUserHandler;
	}

}

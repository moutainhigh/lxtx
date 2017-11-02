package com.neo.service;

import com.neo.entity.User;

import java.util.List;

public interface UserService {

	enum OPERATION_DAY {
		CREATE, LOGIN, SETTING, PAGE, LASTACTIVE, AD, ADFLAG, ADFLAGCONTACT, ADPASS, ADDELIVERY, INVALID
	}
	
    public User findUserById(long id);

    public User findUserByNameAndPass(String username, String pass);
    
    public User updateUserStatus(long id, int status);
}

package com.neo.service;

import java.math.BigInteger;

import com.neo.entity.FbUser;

public interface FbUserService {
	public FbUser getFbUserById(BigInteger userId);
	
	public void saveFbUser(FbUser user);
}

package com.neo.service.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neo.entity.FbUser;
import com.neo.repository.FbUserRepository;
import com.neo.service.FbUserService;

@Service
public class FbUserServiceImpl implements FbUserService {

	@Autowired
	private FbUserRepository userRepository;

	@Override
	public FbUser getFbUserById(BigInteger userId) {
		return userRepository.findOne(userId);
	}

	@Override
	public void saveFbUser(FbUser user) {
		this.userRepository.saveAndFlush(user);
	}
}

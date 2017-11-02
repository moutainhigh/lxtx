package com.lxtx.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudUserLimitMapper;
import com.lxtx.model.CloudUserLimit;

@Service
public class UserLimitService {
	@Autowired
	private CloudUserLimitMapper cloudUserLimitMapper;
	
	public CloudUserLimit selectUserLimitByid (Integer uid){
		return cloudUserLimitMapper.selectByPrimaryKey(uid);
	}
	
	public void uptRePayByid (CloudUserLimit userLimit){
		 cloudUserLimitMapper.uptRePayByid(userLimit);
	}
}

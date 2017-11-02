package com.lxtx.dao;

import java.util.List;

import com.lxtx.model.CloudWxShellUser;

public interface CloudWxShellUserMapper {

	List<CloudWxShellUser> listShellUsers(String chnno);
	
	CloudWxShellUser getUserByWxid(String wxid);
	
	int updateByWxid(CloudWxShellUser user);
	
	int insert(CloudWxShellUser user);
}

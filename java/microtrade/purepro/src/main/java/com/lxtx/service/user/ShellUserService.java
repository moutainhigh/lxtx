package com.lxtx.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudWxShellMapper;
import com.lxtx.dao.CloudWxShellUserMapper;
import com.lxtx.model.CloudWxShell;
import com.lxtx.model.CloudWxShellUser;

@Service
public class ShellUserService {
	@Autowired
	private CloudWxShellUserMapper shellUserMapper;
	@Autowired
	private CloudWxShellMapper shellMapper;

	public List<CloudWxShellUser> listShellUsers(String chnno) {
		return shellUserMapper.listShellUsers(chnno);
	}
	
	public CloudWxShellUser getUserByWxid(String wxid) {
		return shellUserMapper.getUserByWxid(wxid);
	}
	
	public int saveUser(CloudWxShellUser user) {
		return shellUserMapper.insert(user);
	}
	
	public int updateUser(CloudWxShellUser user) {
		return shellUserMapper.updateByWxid(user);
	}
	
	public CloudWxShell getShellByChnno(String chnno) {
		return shellMapper.selectByChnno(chnno);
	}
}

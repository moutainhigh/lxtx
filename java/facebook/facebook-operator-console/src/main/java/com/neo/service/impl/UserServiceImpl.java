package com.neo.service.impl;

import com.neo.entity.User;
import com.neo.logic.Md5Util;
import com.neo.repository.UserRepository;
import com.neo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id);
    }

	@Override
	public User findUserByNameAndPass(String username, String pass) {
		User user = new User();
		user.setStatus(1);
		user.setUserName(username);
		
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("username", GenericPropertyMatchers.exact()).withMatcher("status", GenericPropertyMatchers.exact()).withIgnorePaths("id");
		Example<User> example = Example.of(user, matcher);
		List<User> userList = userRepository.findAll(example);
		if (userList == null || userList.size() == 0) {
			return null;
		} else {
			//check pass
			user = userList.get(0);
			if (user.getUserpass().equalsIgnoreCase(Md5Util.MD5Encode(pass))) {
				return user;
			} else {
				return null;
			}
		}
	}

	@Override
	public User updateUserStatus(long id, int status) {
		User user = this.userRepository.findById(id);
		if (user != null) {
			user.setStatus(status);
			this.userRepository.saveAndFlush(user); //update the status
		}
		return user;
	}
}



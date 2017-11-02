package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.GameUserLoginHandler;
import com.lxtech.game.plaza.db.model.GameUser;

public class UserInfoCache extends AbstractLoadingCache<Long, GameUser>{
	public UserInfoCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public GameUser loadFromStore(Long k) {
		try {
			return GameUserLoginHandler.getGameUser(k);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}

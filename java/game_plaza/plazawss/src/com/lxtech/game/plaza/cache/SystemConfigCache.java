package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.SystemConfigHandler;
import com.lxtech.game.plaza.db.model.SettlementSysConfig;

public class SystemConfigCache extends AbstractLoadingCache<String, SettlementSysConfig>{

	public SystemConfigCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public SettlementSysConfig loadFromStore(String k) {
		try {
			return SystemConfigHandler.getConfigByName(k);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}

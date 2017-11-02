package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;

public class AnimalSettlementGroupCache extends AbstractLoadingCache<Long, AnimalDialSettlementGroup> {

	public AnimalSettlementGroupCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public AnimalDialSettlementGroup loadFromStore(Long k) {
		try {
			return AnimalDialSettlementGroupHandler.getSettlementGroup(k.intValue());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
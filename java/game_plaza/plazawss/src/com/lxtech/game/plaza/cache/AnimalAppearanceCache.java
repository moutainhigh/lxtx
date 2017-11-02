package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;

public class AnimalAppearanceCache extends AbstractLoadingCache<Integer, Integer> {

	public AnimalAppearanceCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	/**
	 * k is the index of animal
	 */
	@Override
	public Integer loadFromStore(Integer k) {
		try {
			return AnimalDialSettlementGroupHandler.getLastestRoundIdForAnimal(k);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return 0;
		}
	}

}

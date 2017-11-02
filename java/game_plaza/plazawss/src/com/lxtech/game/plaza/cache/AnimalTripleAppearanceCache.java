package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.model.ZodiacTriple;

public class AnimalTripleAppearanceCache extends AbstractLoadingCache<ZodiacTriple, Integer> {

	public AnimalTripleAppearanceCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	/**
	 * k is the index of animal
	 */
	@Override
	public Integer loadFromStore(ZodiacTriple k) {
		try {
			return AnimalDialSettlementGroupHandler.getLatestRoundIdForTdWxSx(k.getTd(), k.getWx(), k.getSx());
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return 0;
		}
	}

}

package com.lxtech.game.plaza.cache;

import java.sql.SQLException;

import com.lxtech.game.plaza.db.AnimalDialSettlementGroupHandler;
import com.lxtech.game.plaza.db.model.ZodiacPair;

public class AnimalPairAppearanceCache extends AbstractLoadingCache<ZodiacPair, Integer> {

	public AnimalPairAppearanceCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public Integer loadFromStore(ZodiacPair k) {
		try {
			return AnimalDialSettlementGroupHandler.getLatestRoundIdForTdSx(k.getTd(), k.getSx());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

}

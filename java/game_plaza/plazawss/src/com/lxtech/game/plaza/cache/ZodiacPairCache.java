package com.lxtech.game.plaza.cache;

import com.lxtech.game.plaza.db.model.ZodiacPair;
import com.lxtech.game.plaza.util.ZodiacUtil;

public class ZodiacPairCache extends AbstractLoadingCache<String, ZodiacPair>{

	public ZodiacPairCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	/**
	 * return a new ZodiacPair
	 */
	@Override
	public ZodiacPair loadFromStore(String k) {
		return ZodiacUtil.generateZodiacPair();
	}
}

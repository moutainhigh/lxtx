package com.lxtech.game.plaza.cache;

import com.lxtech.game.plaza.db.model.ZodiacTriple;
import com.lxtech.game.plaza.util.ZodiacUtil;

public class ZodiacTripleCache extends AbstractLoadingCache<String, ZodiacTriple>{

	public ZodiacTripleCache(int maxSize, int expire) {
		super(maxSize, expire);
	}

	@Override
	public ZodiacTriple loadFromStore(String k) {
		return ZodiacUtil.generateZodiacTriple();
	}
}

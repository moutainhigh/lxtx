package com.lxtech.game.plaza.cache;

import com.lxtech.game.plaza.db.model.AnimalDialSettlementGroup;
import com.lxtech.game.plaza.db.model.SettlementSysConfig;

public class GameCacheUtil {

	private static UserInfoCache userInfoCache;

	private static SystemConfigCache sysConfigCache;

	private static ZodiacPairCache zpCache;

	private static ZodiacTripleCache ztCache;

	private static AnimalAppearanceCache aaCache;

	private static AnimalPairAppearanceCache apaCache;

	private static AnimalTripleAppearanceCache ataCache;
	
	private static AnimalSettlementGroupCache animalGroupCache;

	public static final String ZP_CACHE = "zp_cache";

	public static final String ZT_CACHE = "zt_cache";

	static {
		userInfoCache = new UserInfoCache(500, 600);
		sysConfigCache = new SystemConfigCache(50, 60);
		zpCache = new ZodiacPairCache(10, 3600);
		ztCache = new ZodiacTripleCache(10, 86400);

		aaCache = new AnimalAppearanceCache(12, 60);
		apaCache = new AnimalPairAppearanceCache(24, 60);
		ataCache = new AnimalTripleAppearanceCache(120, 60);
		
		animalGroupCache = new AnimalSettlementGroupCache(100, 120);
	}

	public static UserInfoCache getUserInfoCache() {
		return userInfoCache;
	}

	public static SystemConfigCache getSysConfigCache() {
		return sysConfigCache;
	}

	public static ZodiacPairCache getZpCache() {
		return zpCache;
	}

	public static ZodiacTripleCache getZtCache() {
		return ztCache;
	}

	public static AnimalAppearanceCache getAaCache() {
		return aaCache;
	}

	public static AnimalPairAppearanceCache getApaCache() {
		return apaCache;
	}

	public static AnimalTripleAppearanceCache getAtaCache() {
		return ataCache;
	}
	
	public static AnimalSettlementGroupCache getAnimalGroupCache() {
		return animalGroupCache;
	}

	public static void main(String[] args) {
		AnimalDialSettlementGroup group = GameCacheUtil.getAnimalGroupCache().get(3l);
		System.out.println(group.getBanker_carry_amount());
	}
}

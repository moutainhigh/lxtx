package com.lxtech.game.plaza.util;

import java.util.Random;

import com.lxtech.game.plaza.db.model.ZodiacPair;
import com.lxtech.game.plaza.db.model.ZodiacTriple;

/**
 * This class is used to serve the zodiac game
 * @author wangwei
 *
 */
public class ZodiacUtil {
	public static final int[] SX_ARR = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	
	public static final int[] WX_ARR = {13, 14, 15, 16, 17};
	
	public static final int[] TD_ARR = {18, 19};
	
	public static int generateRandomIntWithGroup(int[] sourceArr) {
		int len = sourceArr.length;
		Random random = new Random();
		//the parameter is treated as exclusive, so we don't use [len - 1]
		return sourceArr[random.nextInt(len)];
	}
	
	public static void main(String[] args) {
		int[] a = {13, 14, 15, 16, 17};
		for (int i = 0; i < 100; i++) {
			System.out.println(generateRandomIntWithGroup(a));
		}
	}
	
	public static ZodiacPair generateZodiacPair() {
		ZodiacPair pair = new ZodiacPair();
		pair.setSx(generateRandomIntWithGroup(SX_ARR));
		pair.setTd(generateRandomIntWithGroup(TD_ARR));
		return pair;
	}
	
	public static ZodiacTriple generateZodiacTriple() {
		ZodiacTriple triple = new ZodiacTriple();
		triple.setSx(generateRandomIntWithGroup(SX_ARR));
		triple.setTd(generateRandomIntWithGroup(TD_ARR));
		triple.setWx(generateRandomIntWithGroup(WX_ARR));
		return triple;
	}
	
}

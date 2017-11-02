package org.takeback.util;

import java.util.Random;

public class RandomUtil {
	public static int getRandomNumInTwoIntNum(int x, int y) {
		Random random = new Random();
		int cha = Math.abs(x - y);
		if (cha <= 1) {
			return 0;
		} else {
			if (x > y) {
				int test1 = random.nextInt(cha) + y;
				if (y < test1) {
					return test1;
				}
			}
			if (x < y) {
				int test2 = random.nextInt(cha) + x;
				if (test2 > x) {
					return test2;
				}
			}
		}
		return 0;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 1000; i++) {
			System.out.println(getRandomNumInTwoIntNum(100000, 999999));
		}
	}
}

package com.lxtx.settlement.utils;

import java.util.Random;

public class Tools {
	public static <T> void shuffle(T[] arr) {
		Random rand = new Random();
		for (int i = 0; i < arr.length; i++) {
			int newIndex = rand.nextInt(arr.length);
			if (i != newIndex) {
				T temp = arr[i];
				arr[i] = arr[newIndex];
				arr[newIndex] = temp;
			}
		}
	}
}

package com.gaborbiro.puzzle;

import java.lang.reflect.Array;

public class Utils {

	public static int[] ensureCapacity(int[] array, int capacity) {
		if (array.length >= capacity) {
			return array;
		} else {
			int[] newArray = new int[((capacity / 10) + 1) * 10];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		}
	}

	public static <T> T[] ensureCapacity(T[] array, int capacity, Class<T> clazz) {
		if (array.length >= capacity) {
			return array;
		} else {
			@SuppressWarnings("unchecked")
			T[] newArray = (T[]) Array.newInstance(clazz, ((capacity / 10) + 1) * 10);
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		}
	}
}

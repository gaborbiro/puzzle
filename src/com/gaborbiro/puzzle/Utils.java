package com.gaborbiro.puzzle;

import java.lang.reflect.Array;

public class Utils {

	public static enum C {
		BLACK	("\u001B[30m\u001B[1m\u001B[0m"), 
		RED		("\u001B[31m\u001B[1m\u001B[0m"), 
		GREEN	("\u001B[32m\u001B[1m\u001B[0m"), 
		YELLOW	("\u001B[33m\u001B[1m\u001B[0m"), 
		BLUE	("\u001B[34m\u001B[1m\u001B[0m"), 
		PURPLE	("\u001B[35m\u001B[1m\u001B[0m"), 
		CYAN	("\u001B[36m\u001B[1m\u001B[0m"), 
		WHITE	("\u001B[37m\u001B[1m\u001B[0m");

		private String code;

		private C(String code) {
			this.code = code;
		}

		public String apply(Object data) {
			return new StringBuffer(code).insert(9, data).toString();
		}
	}

	public static enum BG {
		BG_BLACK	("\u001B[40m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_RED		("\u001B[41m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_GREEN	("\u001B[42m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_YELLOW	("\u001B[43m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_BLUE		("\u001B[44m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_PURPLE	("\u001B[45m\u001B[37m\u001B[1m\u001B[0m"), 
		BG_CYAN		("\u001B[46m\u001B[37m\u001B[1m\u001B[0m"),
		BG_WHITE	("\u001B[47m\u001B[38m\u001B[1m\u001B[0m"); 
		private String code;

		private BG(String code) {
			this.code = code;
		}

		public String apply(Object data) {
			return new StringBuffer(code).insert(14, data).toString();
		}
	}
	
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
	
	public static String toString(int[] array) {
		StringBuffer buffer = new StringBuffer();
		for (int item : array) {
			if (item >= 0) {
				buffer.append(Integer.toString(item));
				buffer.append(", ");
			}
		}
		return buffer.length() > 1 ? buffer.substring(0, buffer.length() - 2).toString() : buffer.toString();
	}
}

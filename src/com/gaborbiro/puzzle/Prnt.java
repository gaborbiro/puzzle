package com.gaborbiro.puzzle;

import java.io.PrintStream;

import org.fusesource.jansi.AnsiConsole;

public class Prnt {
	// public static enum C {
	// BLACK ("\u001B[30m\u001B[1m\u001B[0m", FColor.BLACK),
	// RED ("\u001B[31m\u001B[1m\u001B[0m", FColor.RED),
	// GREEN ("\u001B[32m\u001B[1m\u001B[0m", FColor.GREEN),
	// YELLOW ("\u001B[33m\u001B[1m\u001B[0m", FColor.YELLOW),
	// BLUE ("\u001B[34m\u001B[1m\u001B[0m", FColor.BLUE),
	// PURPLE ("\u001B[35m\u001B[1m\u001B[0m", FColor.MAGENTA),
	// CYAN ("\u001B[36m\u001B[1m\u001B[0m", FColor.CYAN),
	// WHITE ("\u001B[37m\u001B[1m\u001B[0m", FColor.WHITE);
	//
	// private final String code;
	// private final FColor ansiCode;
	//
	// private C(String code, FColor ansiCode) {
	// this.code = code;
	// this.ansiCode = ansiCode;
	// }
	//
	// public String apply(Object data) {
	// return new StringBuffer(code).insert(9, data).toString();
	// }
	// }

	public static final boolean isAppRunningFromJar;
	public static PrintStream out;

	static {
		isAppRunningFromJar = Main.class.getResource(Main.class.getSimpleName() + ".class").toString().contains("jar");
		if (isAppRunningFromJar) {
			out = AnsiConsole.out;
		} else {
			out = System.out;
		}
	}

	/**
	 * Bold + contrasting foreground color
	 */
	public static enum BG {
		// BLACK ("\u001B[40m\u001B[37m\u001B[1m\u001B[0m"),
		RED("\u001B[41m\u001B[37m\u001B[1m\u001B[0m"),
		GREEN("\u001B[42m\u001B[37m\u001B[1m\u001B[0m"),
		YELLOW("\u001B[43m\u001B[37m\u001B[1m\u001B[0m"),
		BLUE("\u001B[44m\u001B[37m\u001B[1m\u001B[0m"),
		PURPLE("\u001B[45m\u001B[37m\u001B[1m\u001B[0m"),
		CYAN("\u001B[46m\u001B[37m\u001B[1m\u001B[0m"),
		WHITE("\u001B[47m\u001B[38m\u001B[1m\u001B[0m");

		private final String code;

		private BG(String code) {
			this.code = code;
		}

		public String apply(Object data) {
			return new StringBuffer(code).insert(14, data).toString();
		}
	}

	public static Offset offset(int row, int col) {
		return new Offset(row, col);
	}

	public static class Offset {
		private int rowOffset;
		private int colOffset;
		private Offset child;

		public Offset(int rowOffset, int colOffset) {
			this.rowOffset = rowOffset;
			this.colOffset = colOffset;
		}

		private static void print(int row, int col, String msg) {
			
		}

		void print(String msg) {
			if (child == null) {
				printOffset(rowOffset, colOffset, msg);
			} else {
			}
		}
	}

	public static void printOffset(int row, int col, String msg) {
		char escCode = 0x1B;
		out.print(String.format("%c[%d;%df", escCode, row, col));
		out.println(msg);
	}
}

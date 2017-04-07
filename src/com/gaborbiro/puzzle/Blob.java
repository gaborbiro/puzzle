package com.gaborbiro.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.gaborbiro.puzzle.Utils.BG;

/**
 * Class holding a group of adjacent {@code Points}, all having the same value.
 *
 */
public class Blob<T> {

	static interface PrintDelegate<T> {
		String print(Blob<T> blob);
	}

	public static class SimplePrintDelegate implements PrintDelegate {

		@Override
		public String print(Blob blob) {
			return blob.value.toString();
		}
	}

	public static class MergingPrintDelegate<T> implements PrintDelegate<T> {

		@Override
		public String print(Blob<T> blob) {
			T value = getRawValue(blob);
			String toPrint = blob.bg.apply(value);
			return blob.points.size() > 1 ? toPrint : value.toString();
		}

		protected T getRawValue(Blob<T> blob) {
			return blob.value;
		}
	}

	public static PrintDelegate printDelegate = new SimplePrintDelegate();

	private static int counter = 0;

	List<Point> points = new ArrayList<>();
	T value;
	boolean visited;
	Map<Blob<T>, List<Point>> neighbours = new WeakHashMap<>();
	int id;
	BG bg;
	PrintDelegate<T> privatePrintDelegate;

	public Blob(T value, int row, int col) {
		points.add(Point.get(row, col));
		this.value = value;
		bg = BG.BG_WHITE;
		id = counter++;
	}

	public void add(Blob<T> blob) {
		points.addAll(blob.points);
		neighbours.putAll(blob.neighbours);
	}

	public void addNeighbour(Blob<T> blob, Point direction) {
		List<Point> directions = neighbours.get(blob);

		if (directions == null) {
			directions = new ArrayList<>();
			neighbours.put(blob, directions);
		}
		if (!directions.contains(direction)) {
			directions.add(direction);
		}
	}

	public void nextColor() {
		bg = BG.values()[counter++ % BG.values().length];
	}

	public void setPrivatePrintDelegate(PrintDelegate<T> privatePrintDelegate) {
		this.privatePrintDelegate = privatePrintDelegate;
	}

	@Override
	public String toString() {
		return "(" + value + ":" + points + ")";
	}

	public String toColoredString() {
		return privatePrintDelegate != null ? privatePrintDelegate.print(this)
				: (printDelegate != null ? printDelegate.print(this) : value.toString());
		// return "|" + id + "|";
	}
}

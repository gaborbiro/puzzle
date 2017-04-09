package com.gaborbiro.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.gaborbiro.puzzle.Prntr.BG;

/**
 * Class holding a group of adjacent {@code Points}, all having the same value.
 *
 */
public class Blob<T> {

	static interface PrintDelegate<T> {
		T getRawValue(Blob<T> blob);

		String print(Blob<T> blob);
	}

	static abstract class JCDPPrintDelegate<T> implements PrintDelegate<T> {

	}

	// public static class SimplePrintDelegate implements PrintDelegate {
	//
	// @Override
	// public Object getRawValue(Blob blob) {
	// return blob;
	// }
	//
	// @Override
	// public String print(Blob blob, Object value) {
	// return value.toString();
	// }
	// }

	public static class MergingPrintDelegate<T> implements PrintDelegate<T> {

		@Override
		public T getRawValue(Blob<T> blob) {
			return blob.value;
		}

		@Override
		public String print(Blob<T> blob) {
			return blob.bg.apply(getRawValue(blob));
			// return blob.points.size() > 1 ? toPrint : value.toString();
		}
	}

	public static PrintDelegate markedPrintDelegate;

	public static PrintDelegate printDelegate = new MergingPrintDelegate();

	private static int counter = 0;

	List<Point> points = new ArrayList<>();
	T value;
	boolean marked;
	Map<Blob<T>, List<Point>> neighbours = new WeakHashMap<>();
	int id;
	BG bg;

	public Blob(T value, int row, int col) {
		points.add(Point.get(row, col));
		this.value = value;
		bg = BG.BLUE;
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

		if (bg == BG.WHITE) {
			nextColor();
		}
	}

	@Override
	public String toString() {
		return "(" + value + (marked ? ":v" : "") + ":" + points + ")";
	}

	public String toColoredString() {
		PrintDelegate<T> delegate = marked && markedPrintDelegate != null ? markedPrintDelegate : printDelegate;
		return delegate.print(this);
	}
}

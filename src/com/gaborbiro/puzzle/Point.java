package com.gaborbiro.puzzle;

public class Point {
	int row;
	int col;

	private Point(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public static Point get(int row, int col) {
		return new Point(row, col);
	}

	@Override
	public String toString() {
		return row + "/" + col;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
}

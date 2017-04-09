package com.gaborbiro.puzzle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Reads and turns into a 2D graph inputs like this:
 * 
 * <pre>
 *    aabbaabbaa
 *   ccddccddccdd
 *  aabbaabbaabbaa
 * ccddccddccddccdd
 *aabbaabbaabbaabbaa
 * ccddccddccddccdd
 *  aabbaabbaabbaa
 *   ccddccddccdd
 *    aabbaabbaa
 * </pre>
 * 
 * Similar neighbouring characters will form one {@link Blob}.<br>
 * "Neighbouring" means horizontally or vertically adjacent (Although it could
 * easily be modified to include diagonal adjacency as well).<br>
 */
public class Puzzle {

	private List<List<Blob<Character>>> matrix;
	private int width;

	public Puzzle(InputStream in) {
		matrix = getMatrix(in);
		reduce(matrix);
	}

	/**
	 * Reads the matrix from the specified input stream
	 *
	 * @return
	 */
	private List<List<Blob<Character>>> getMatrix(InputStream in) {
		List<List<Blob<Character>>> matrix = new ArrayList<>();
		Scanner scanner = new Scanner(in);
		int row = 0;
		while (scanner.hasNext()) {
			String line = scanner.nextLine();

			for (int col = 0; col < line.length(); col++) {
				if (matrix.size() <= row) {
					matrix.add(new ArrayList<>());
				}
				if (!Character.isWhitespace(line.charAt(col))) {
					matrix.get(row).add(new Blob<Character>(line.charAt(col), row, col));
				} else {
					matrix.get(row).add(null);
				}
			}
			if (matrix.get(row).size() > width) {
				width = matrix.get(row).size();
			}
			row++;
		}
		scanner.close();
		return matrix;
	}

	private static void reduce(List<List<Blob<Character>>> matrix) {
		boolean repeat;

		do {
			repeat = false;
			for (int row = 0; row < matrix.size(); row++) {
				List<Blob<Character>> currentRow = matrix.get(row);
				for (int col = 0; col < currentRow.size(); col++) {
					if (currentRow.get(col) != null) {
						boolean found = false;
						if (row > 0 && matrix.get(row - 1).size() > col) {
							found = merge(matrix, row, col, row - 1, col); // top
						}
						if (!found && col > 0 && currentRow.size() > col - 1) {
							found = merge(matrix, row, col, row, col - 1); // left
						}
						if (found) {
							repeat = true;
						}
					}
				}
			}
			for (int row = matrix.size() - 1; row >= 0; row--) {
				List<Blob<Character>> currentRow = matrix.get(row);
				for (int col = currentRow.size() - 1; col >= 0; col--) {
					if (currentRow.get(col) != null) {
						boolean found = false;
						if (row < matrix.size() - 1 && matrix.get(row + 1).size() > col) {
							found = merge(matrix, row, col, row + 1, col); // bottom
						}
						if (!found && col < currentRow.size() - 1 && currentRow.size() > col + 1) {
							found = merge(matrix, row, col, row, col + 1); // right
						}
						if (found) {
							repeat = true;
						}
					}
				}
			}
		} while (repeat);
		System.gc();
		System.gc();
	}

	private static boolean merge(List<List<Blob<Character>>> matrix, int row, int col, int targetRow, int targetCol) {
		boolean found = false;
		Blob<Character> current = matrix.get(row).get(col);
		Blob<Character> target = matrix.get(targetRow).get(targetCol);

		if (target != null && target != current) {
			if (target.value == current.value) {
				target.add(current);
				matrix.get(row).set(col, target);
				found = true;
			} else {
				current.addNeighbour(target, Point.get(targetRow, targetCol));
				target.addNeighbour(current, Point.get(row, col));
				if (target.bg == current.bg) {
					current.nextColor();
				}
			}
		}
		return found;
	}

	public int getHeight() {
		return matrix.size();
	}

	public int getWidth() {
		return width;
	}

	/**
	 * Note: don't use this return value to identify nodes. It's just the value.
	 * Multiple nodes may have the same value.
	 */
	public Character getValue(Point p) {
		if (p.row >= 0 && p.row < matrix.size() && p.col >= 0 && p.col < matrix.get(p.row).size()) {
			Blob<Character> b = matrix.get(p.row).get(p.col);
			return b != null ? b.value : null;
		} else {
			return null;
		}
	}

	public void markAsVisited(boolean visited, Point p) {
		matrix.get(p.row).get(p.col).marked = visited;
	}

	public Point[] getNeighbours(Point p, boolean unvisited) {
		Map<Blob<Character>, List<Point>> neighbours = matrix.get(p.row).get(p.col).neighbours;
		List<Point> result = new ArrayList<>();
		for (Blob<Character> blob : neighbours.keySet()) {
			if (!unvisited || !blob.marked) {
				result.addAll(neighbours.get(blob));
			}
		}
		return result.toArray(new Point[result.size()]);
	}

	public Point getCenter(Point target) {
		Blob<Character> b = matrix.get(target.row).get(target.col);
		int rowSum = 0, colSum = 0;

		for (Point p : b.points) {
			rowSum += p.row;
			colSum += p.col;
		}
		return Point.get(rowSum / b.points.size(), colSum / b.points.size());
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (int row = 0; row < matrix.size(); row++) {
			buffer.append(row % 10);
			for (int col = 0; col < matrix.get(row).size(); col++) {
				if (matrix.get(row).get(col) != null) {
					buffer.append(matrix.get(row).get(col).toColoredString());
				} else {
					buffer.append(" ");
				}
			}
			buffer.append("\n");
		}
		return buffer.toString();
	}
}

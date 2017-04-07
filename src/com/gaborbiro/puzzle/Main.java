package com.gaborbiro.puzzle;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gaborbiro.puzzle.ProblemPermutator.Callback;

public class Main {

	public static void main(String[] args) {
		FileInputStream fio = null;
		Puzzle puzzle = null;
		try {
			fio = new FileInputStream("test.txt");
			puzzle = new Puzzle(fio);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fio.close();
			} catch (Throwable e) {
			}
		}
		if (puzzle == null) {
			return;
		}
		Blob.printDelegate = new Blob.MergingPrintDelegate();
		System.out.println(puzzle);

		PuzzleFilter filter = new PuzzleFilter(puzzle, puzzle.getHeight() / 2, puzzle.getWidth() / 2);
		ProblemPermutator permutator = new ProblemPermutator(filter);
		permutator.run();
	}

	private static class PuzzleFilter implements Callback {

		private Puzzle puzzle;
		private List<Point> path = new ArrayList<>();

		public PuzzleFilter(Puzzle puzzle, int startRow, int startCol) {
			this.puzzle = puzzle;
			path.add(Point.get(startRow, startCol));
			puzzle.markAsVisited(true, path.get(0));
			System.out.println(puzzle);
		}

		@Override
		public boolean hasMoreChildren(int[] solution, int index) {
			updatePath(solution, index);
			return puzzle.getNeighbours(path.get(index)).length - 1 > solution[index];
		}

		private void updatePath(int[] solution, int index) {
			if (solution[index] >= 0) {
				Point[] neighbours = puzzle.getNeighbours(path.get(path.size() - 1));
				Point nextCandidateStep = neighbours[solution[index]];
				if (path.size() > index + 1) {
					path.set(index + 1, nextCandidateStep);
					while (path.size() > index + 2) {
						path.remove(path.size() - 1);
					}
				} else {
					path.add(nextCandidateStep);
				}
			}
		}

		@Override
		public boolean isCandidate(int[] candidate, int index) {
			updatePath(candidate, index);
			Point p = path.get(path.size() - 1);

			for (int i = 0; i < path.size() - 1; i++) {
				if (puzzle.getValue(path.get(i)) == puzzle.getValue(p)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isSolution(int[] solution) {
			return false;
		}

		@Override
		public void solution(int[] solution) {
		}

		public String toString(List<Point> path) {
			StringBuffer buffer = new StringBuffer();
			for (Point point : path) {
				if (point != null) {
					buffer.append(puzzle.getValue(point));
					buffer.append("(");
					buffer.append(point);
					buffer.append("), ");
				}
			}
			return buffer.length() > 1 ? buffer.substring(0, buffer.length() - 2).toString() : buffer.toString();
		}

		@Override
		public void visit(int[] candidate, int index) {
			puzzle.markAsVisited(true, path.get(index + 1));
			System.out.println(puzzle);
		}

		@Override
		public void unvisit(int[] candidate, int index) {
			puzzle.markAsVisited(false, path.get(index + 1));
			System.out.println(puzzle);
		}
	}

	//
	// public static boolean isValid(int row, int col) {
	// String target = matrix.get(row).get(col).neighbours.toString();
	// Pattern p = Pattern.compile("(=\\[\\d/\\d\\])");
	// Matcher m = p.matcher(target);
	//
	// while (m.find()) {
	// if (target.split(Pattern.quote(m.group())).length > 2) {
	// return false;
	// }
	// }
	// return true;
	// }

}

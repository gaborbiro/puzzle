package com.gaborbiro.puzzle;

import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.gaborbiro.puzzle.Blob.MergingPrintDelegate;
import com.gaborbiro.puzzle.ProblemPermutator.Callback;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		if (Prntr.isAppRunningFromJar && System.console() == null && !GraphicsEnvironment.isHeadless()) {
			String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
			Runtime.getRuntime()
					.exec(new String[] { "cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\"" });
		} else {
			work();
			System.out.println("Program has ended, please type 'exit' to close the console");
		}
	}

	public static void work() {
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
		Blob.markedPrintDelegate = new MergingPrintDelegate<Character>() {

			@Override
			public String print(Blob<Character> blob) {
				return Prntr.BG.WHITE.apply(getRawValue(blob));
			}
		};
		print(puzzle);

		PuzzleFilter filter = new PuzzleFilter(puzzle, puzzle.getHeight() / 2, puzzle.getWidth() / 2);
		ProblemPermutator permutator = new ProblemPermutator(filter);
		permutator.run();
	}

	private static void print(Puzzle puzzle) {
		char escCode = 0x1B;
		Prntr.out.print(String.format("%c[%d;%df", escCode, 3, 0));
		Prntr.out.println(" 01234567890123456789");
		Prntr.out.println(puzzle);
	}

	private static class PuzzleFilter implements Callback {

		private Puzzle puzzle;
		private List<Point> path = new ArrayList<>();
		private Thread printerThread;

		public PuzzleFilter(Puzzle puzzle, int startRow, int startCol) {
			this.puzzle = puzzle;
			path.add(Point.get(startRow, startCol));
			puzzle.markAsVisited(true, path.get(0));
		}

		@Override
		public boolean hasMoreChildren(int[] solution, int index) {
			updatePath(solution, index);
			return puzzle.getNeighbours(path.get(index), true).length - 1 > solution[index];
		}

		private void updatePath(int[] solution, int index) {
			if (solution[index] >= 0) {
				Point[] neighbours = puzzle.getNeighbours(path.get(index), true);

				if (solution[index] < neighbours.length) {
					Point nextCandidateStep = neighbours[solution[index]];
					if (path.size() > index + 1) {
						path.set(index + 1, nextCandidateStep);
						while (path.size() > index + 1) {
							path.remove(path.size() - 1);
						}
					} else {
						path.add(nextCandidateStep);
					}
				}
			}
		}

		@Override
		public boolean isCandidate(int[] candidate, int index) {
			updatePath(candidate, index);
			if (index > 0) {
				Point cand = puzzle.getCenter(path.get(index + 1));
				Point p1 = puzzle.getCenter(path.get(index));
				Point p2 = puzzle.getCenter(path.get(index - 1));
				int rD = cand.row - p1.row;
				int cD = cand.col - p1.col;
				
				return puzzle.getValue(Point.get(p1.row + p1.row - p2.row, p1.col + p1.col - p2.col)) == null || 
						Point.get(p1.row - rD, p1.col - cD).equals(p2);
			}
			return true;
		}

		@Override
		public boolean isSolution(int[] solution, int index) {
			return index == 50;
		}

		@Override
		public void solution(int[] solution, int index) {
			print(puzzle);
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

			if (printerThread == null || !printerThread.isAlive()) {
				printerThread = new Thread(new Runnable() {

					@Override
					public void run() {
						print(puzzle);
						try {
							Thread.currentThread().sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
				printerThread.run();
			}
		}

		@Override
		public void unvisit(int[] candidate, int index) {
			puzzle.markAsVisited(false, path.get(index + 1));
		}
	}
}

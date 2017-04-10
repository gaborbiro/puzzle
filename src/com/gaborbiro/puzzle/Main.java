package com.gaborbiro.puzzle;

import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.gaborbiro.puzzle.Blob.MergingPrintDelegate;
import com.gaborbiro.puzzle.Printer.Offset;
import com.gaborbiro.puzzle.ProblemPermutator.Callback;

public class Main {

	private static Offset puzzlePrinter = Printer.offset(Contants.PRINT_ROW_OFFSET, 0);
	private static Offset indexPrinter = puzzlePrinter.offset(10, 0);
	private static Offset printer = indexPrinter.offset(1, 0, 2);

	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		if (Printer.isAppRunningFromJar && System.console() == null && !GraphicsEnvironment.isHeadless()) {
			String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
			Runtime.getRuntime()
					.exec(new String[] { "cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\"" });
		} else {
			for (int i = 0; i < 50; i++) {
				measure(args);
			}
			Printer.offset(18, 0).print("Average: " + (total / counter - 1) + "msec");
		}
	}

	static long total;
	static int counter = 0;

	public static void measure(String[] args) {
		long start = System.currentTimeMillis();
		work(args);
		total += System.currentTimeMillis() - start;
		Printer.offset(17, 0).print("Duration: " + (System.currentTimeMillis() - start) + "msec");
		counter++;
	}

	public static void work(String[] args) {
		FileInputStream fio = null;
		Puzzle puzzle = null;

		try {
			String path = args != null && args.length > 0 ? args[0] : "test.txt";
			fio = new FileInputStream(path);
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
				return Printer.BG.WHITE.apply(getRawValue(blob));
			}
		};
//		puzzlePrinter.print0(puzzle.toString());

		PuzzleFilter filter = new PuzzleFilter(puzzle, puzzle.getHeight() / 2, puzzle.getWidth() / 2, 52);
		ProblemPermutator permutator = new ProblemPermutator(filter);
		permutator.run();
	}

	private static class PuzzleFilter implements Callback {

		private Puzzle puzzle;
		private List<Point> path = new ArrayList<>();
		private int solutionSize;

		public PuzzleFilter(Puzzle puzzle, int startRow, int startCol, int solutionSize) {
			this.puzzle = puzzle;
			this.solutionSize = solutionSize;
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
				Point p = Point.get(p1.row + p1.row - p2.row, p1.col + p1.col - p2.col);
				return puzzle.getValue(p) == null || puzzle.isVisited(p)
						|| Point.get(p1.row - rD, p1.col - cD).equals(p2);
			}
			return true;
		}

		@Override
		public boolean isSolution(int[] solution, int index) {
			// indexPrinter.print0(" ");
			// indexPrinter.print0("" + index);
			return index == solutionSize - 1;
		}

		@Override
		public void solution(int[] solution, int index) {
			printer.print0(index + " Solution: " + path);
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
			puzzle.markAsVisited(true, path.get(index));
			// puzzle.print(puzzlePrinter, path.get(index));
			// try {
			// Thread.currentThread().sleep(10);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}

		@Override
		public void unvisit(int[] candidate, int index) {
			puzzle.markAsVisited(false, path.get(index));
			// puzzle.print(puzzlePrinter, path.get(index));
			// try {
			// Thread.currentThread().sleep(10);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}
	}
}

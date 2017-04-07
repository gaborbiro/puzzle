package com.gaborbiro.puzzle;

import java.util.Arrays;

public class ProblemPermutator {
	public interface Callback {
		boolean hasMoreChildren(int[] candidate, int index);
		
		void visit(int[] candidate, int index);
		
		void unvisit(int[] candidate, int index);
		
		boolean isCandidate(int[] candidate, int index);

		boolean isSolution(int[] candidate);

		void solution(int[] solution);
	}

	private static final int EMPTY = -1;
	
	private Callback callback;

	public ProblemPermutator(Callback callback) {
		this.callback = callback;
	}

	public void run() {
		int[] solution = new int[10];
		Arrays.fill(solution, EMPTY);
		exploreNode(solution, 0);
	}
	
	private void exploreNode(int[] candidate, int index) {
		while (callback.hasMoreChildren(candidate, index)) {
			nextChild(candidate, index);
			
			if (callback.isCandidate(candidate, index)) {
				if (!callback.isSolution(candidate)) {
					callback.visit(candidate, index);
					exploreNode(candidate, index + 1);
					callback.unvisit(candidate, index);
					candidate = Utils.ensureCapacity(candidate, index);
					candidate[index + 1] = EMPTY;
				} else {
					callback.solution(candidate);
				}
			}
		}
	}

	private void nextChild(int[] solution, int index) {
		solution[index]++;
	}
}

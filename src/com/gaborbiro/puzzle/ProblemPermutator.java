package com.gaborbiro.puzzle;

import java.util.Arrays;

public class ProblemPermutator {
    public interface Callback {
        boolean hasMoreChildren(int[] candidate, int index);

        void visit(int[] candidate, int index);

        void unVisit(int[] candidate, int index);

        boolean isCandidate(int[] candidate, int index);

        boolean isSolution(int[] candidate, int index);

        void solution(int[] solution, int index);
    }

    private static final int EMPTY = -1;

    private Callback callback;

    public ProblemPermutator(Callback callback) {
        this.callback = callback;
    }

    public void run() {
        int[] solution = new int[61];
        Arrays.fill(solution, EMPTY);
        exploreNode(solution, 0);
    }

    private void exploreNode(int[] candidate, int index) {
        callback.visit(candidate, index);
        while (callback.hasMoreChildren(candidate, index)) {
            nextChild(candidate, index);

            if (callback.isCandidate(candidate, index)) {
                if (callback.isSolution(candidate, index)) {
                    callback.solution(candidate, index);
                }
                exploreNode(candidate, index + 1);
                candidate[index + 1] = EMPTY;
            }
//			Thread.currentThread().yield();
        }
        callback.unVisit(candidate, index);
    }

    private void nextChild(int[] solution, int index) {
        solution[index]++;
    }
}

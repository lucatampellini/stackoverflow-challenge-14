package it.lucatampellini;

import it.lucatampellini.core.SequenceAnalysis;

class ResultingSum {
    private volatile int sum;

    protected ResultingSum() {
        this.sum = 0;
    }

    protected synchronized void addAnalysis(final SequenceAnalysis analysis) {
        sum += analysis.start() + analysis.end();
    }

    protected int getSum() {
        return sum;
    }
}

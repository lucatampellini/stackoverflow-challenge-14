package org.example.core;

class SequenceStartedException extends RuntimeException {
    private final int firstVal;

    protected SequenceStartedException(final int firstVal) {
        super();
        this.firstVal = firstVal;
    }

    public int getFirstVal() {
        return firstVal;
    }
}
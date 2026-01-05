package it.lucatampellini.core.exceptions;

public class SequenceStartedException extends RuntimeException {
    private final int firstVal;

    public SequenceStartedException(final int firstVal) {
        super();
        this.firstVal = firstVal;
    }

    public int getFirstVal() {
        return firstVal;
    }
}
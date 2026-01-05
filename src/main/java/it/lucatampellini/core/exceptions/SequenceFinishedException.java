package it.lucatampellini.core.exceptions;

import java.util.function.Consumer;

abstract class SequenceFinishedException extends RuntimeException {
    final int endVal;
    final Consumer<Integer> whatToDoWithTheValue;
    protected SequenceFinishedException(final int endVal, final Consumer<Integer> whatToDoWithTheValue) {
        super();
        this.endVal = endVal;
        this.whatToDoWithTheValue = whatToDoWithTheValue;
    }
    public void processLastValue() {
        whatToDoWithTheValue.accept(endVal);
    }
}
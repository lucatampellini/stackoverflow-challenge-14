package org.example.core;

import java.util.function.Consumer;

class SequenceFinishedException extends RuntimeException {
    final int endVal;
    final Consumer<Integer> whatToDoWithTheValue;

    protected SequenceFinishedException(final int endVal, final Consumer<Integer> whatToDoWithTheValue) {
        super();
        this.endVal = endVal;
        this.whatToDoWithTheValue = whatToDoWithTheValue;
    }

    protected void processLastValue() {
        whatToDoWithTheValue.accept(endVal);
    }
}
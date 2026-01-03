package org.example.core;

import java.util.function.Consumer;
class OriginalSequenceFinishedException extends SequenceFinishedException {
    protected OriginalSequenceFinishedException(int endVal, Consumer<Integer> whatToDoWithTheValue) {
        super(endVal, whatToDoWithTheValue);
    }
}

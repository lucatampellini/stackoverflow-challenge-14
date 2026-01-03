package org.example.core;

import java.util.function.Consumer;

class ExtractedSequenceFinishedException extends SequenceFinishedException {
    protected ExtractedSequenceFinishedException(int endVal, Consumer<Integer> whatToDoWithTheValue) {
        super(endVal, whatToDoWithTheValue);
    }
}

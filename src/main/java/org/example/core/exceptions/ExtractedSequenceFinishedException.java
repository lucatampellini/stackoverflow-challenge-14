package org.example.core.exceptions;

import java.util.function.Consumer;

public class ExtractedSequenceFinishedException extends SequenceFinishedException {
    public ExtractedSequenceFinishedException(int endVal) {
        super(endVal, val -> {});
    }

    public ExtractedSequenceFinishedException(int endVal, Consumer<Integer> whatToDoWithTheValue) {
        super(endVal, whatToDoWithTheValue);
    }
}

package org.example.core.exceptions;

import java.util.function.Consumer;
public class OriginalSequenceFinishedException extends SequenceFinishedException {
    public OriginalSequenceFinishedException(int endVal, Consumer<Integer> whatToDoWithTheValue) {
        super(endVal, whatToDoWithTheValue);
    }
}

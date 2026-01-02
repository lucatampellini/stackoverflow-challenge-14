package org.example.core;

public interface SequenceAnalyzerFactory {
    static SequenceAnalyzer make(int... sequence) {
        return new SequenceAnalyzer(sequence);
    }
}

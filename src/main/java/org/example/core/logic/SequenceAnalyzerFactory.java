package org.example.core.logic;

import org.example.core.logic.SequenceAnalyzer;

public interface SequenceAnalyzerFactory {
    static ISequenceAnalyzer make(int... sequence) {
        return new SequenceAnalyzer(sequence);
    }
}

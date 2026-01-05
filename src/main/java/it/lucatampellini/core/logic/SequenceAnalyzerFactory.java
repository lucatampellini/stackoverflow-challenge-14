package it.lucatampellini.core.logic;

public interface SequenceAnalyzerFactory {
    static ISequenceAnalyzer make(int... sequence) {
        return new SequenceAnalyzer(sequence);
    }
}

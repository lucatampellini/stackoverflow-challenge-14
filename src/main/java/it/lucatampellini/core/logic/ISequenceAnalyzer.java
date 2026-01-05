package it.lucatampellini.core.logic;

import it.lucatampellini.core.SequenceAnalysis;

@FunctionalInterface
public interface ISequenceAnalyzer {
    SequenceAnalysis process();
}

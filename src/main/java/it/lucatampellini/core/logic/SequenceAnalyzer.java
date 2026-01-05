package it.lucatampellini.core.logic;

import org.apache.commons.lang3.ArrayUtils;
import it.lucatampellini.core.SequenceAnalysis;

import java.util.*;
import java.util.stream.IntStream;


class SequenceAnalyzer implements ISequenceAnalyzer{

    private final Integer[] originalSortedSequence;

    private final List<ExtractedSequence> extractedSequences;

    protected SequenceAnalyzer(int... sequenceToAnalyse) {
        super();
        this.originalSortedSequence = IntStream.of(sequenceToAnalyse)
                .sorted()
                .boxed()
                .toArray(Integer[]::new);
        this.extractedSequences = new ArrayList<>();
    }

    @Override
    public SequenceAnalysis process() {

        new SequenceExtractor(originalSortedSequence)
                .extractSequences()
                .stream()
                .forEach(extractedSequences::add);

        //compare all extracted sequences
        final var validSignal = compareSequencesForValidSignal();
        final var validSequence = validSignal.sequence.toArray(Integer[]::new);

        //dont forget to remove also the duplicate
        final var sequenceToRemove = validSignal.duplicate.isPresent() ? ArrayUtils.add(validSequence, validSignal.duplicate.get()) : validSequence;

        //deduce noise as all but the validSignal
        final var noise = ArrayUtils.removeElements(originalSortedSequence, sequenceToRemove);

        //produce result
        return new SequenceAnalysis(
                validSignal.sequence.getFirst(),
                validSignal.sequence.getLast(),
                validSignal.missing.orElse(null),
                validSignal.duplicate.orElse(null),
                noise
        );

    }

    private ExtractedSequence compareSequencesForValidSignal() {
        return this.extractedSequences.stream()
                .sorted(ExtractedSequence::compareTo)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

}


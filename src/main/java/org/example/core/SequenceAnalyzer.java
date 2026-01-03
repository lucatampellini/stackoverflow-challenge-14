package org.example.core;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SequenceAnalyzer {

    private static final int DUPLICATE_SIGNAL_DISTANCE = 0,
            CORRECT_SIGNAL_DISTANCE = 1,
            MISSING_SIGNAL_DISTANCE = 2;

    private final Integer[] originalSortedSequence;

    private final LinkedList<ExtractedSequence> extractedSequences;

    private final AtomicInteger positionCounter;

    protected SequenceAnalyzer(int... sequenceToAnalyse) {
        super();
        this.originalSortedSequence = IntStream.of(sequenceToAnalyse)
                .sorted()
                .boxed()
                .toArray(Integer[]::new);
        this.extractedSequences = new LinkedList<>();
        this.positionCounter = new AtomicInteger(); // resetCounter() sets the value to 0
    }

    public SequenceAnalysis process() {

        this.resetCounter();
        this.setupNewSequence();

        //walk the array
        try {
            walk();
        } catch (OriginalSequenceFinishedException e) {
            try {
                e.processLastValue();
            } catch (ExtractedSequenceFinishedException innerEx) {
                // if the last value is noise again, no actions
            }
        }

        //compare all extracted sequences
        final var validSignal = compareSequencesForValidSignal();
        final var validSequence = validSignal.sequence.toArray(Integer[]::new);

        //deduce noise as all but the validSignal
        final var noise = ArrayUtils.removeElements(originalSortedSequence, validSequence);

        //produce result
        return new SequenceAnalysis(
                validSignal.sequence.getFirst(),
                validSignal.sequence.getLast(),
                validSignal.missing.orElse(null),
                validSignal.duplicate.orElse(null),
                noise
        );

    }

    private synchronized void resetCounter() {
        this.positionCounter.set(0);
    }

    private void walk() throws OriginalSequenceFinishedException {
        try{
            //evaluation on current element
            //take current position
            final var currentPos = this.positionCounter.getAndIncrement();
            //take current value
            final var currentVal = originalSortedSequence[currentPos];

            //evaluation on relation of current element to the next
            //check if next available
            if (currentPos+1 >= originalSortedSequence.length) {
                //last element
                //action on it depends on how it compares with the last correct signal
                try{
                    final var action = determineActionForCurrentVal(distanceFromLastSignal(currentVal));
                    throw new OriginalSequenceFinishedException(currentVal, action);
                } catch (SequenceStartedException e) {
                    //if we fall here, the original sequence was made of a single item
                    throw new OriginalSequenceFinishedException(currentVal, this::correctSignal);
                }

            }
            //conditional branching depending on distance from next value in the sorted sequence
            determineActionForCurrentVal(distanceToNextValInSequence(currentPos)).accept(currentVal);
        } catch (ExtractedSequenceFinishedException e) {
            e.processLastValue();
            this.setupNewSequence();
        }
        //recursion
        walk();
    }

    private ExtractedSequence compareSequencesForValidSignal() {
        return this.extractedSequences.stream()
                .sorted(ExtractedSequence::compareTo)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private void setupNewSequence() {
        this.extractedSequences.add(new ExtractedSequence());
    }

    private int distanceToNextValInSequence(final int currentPos) {
        return Math.abs(originalSortedSequence[currentPos] - originalSortedSequence[currentPos + 1]);
    }

    private int distanceFromLastSignal(final int currentVal) throws SequenceStartedException {
        try {
            return Math.abs(currentVal - this.extractedSequences.getLast().sequence.getLast());
        } catch (NoSuchElementException e) {
            // still no correct signal available
            throw new SequenceStartedException(currentVal);
        }
    }

    private Consumer<Integer> determineActionForCurrentVal(final int distanceFromSensibleVal) {
        return switch (distanceFromSensibleVal) {
            case DUPLICATE_SIGNAL_DISTANCE -> this::duplicateSignal;
            case CORRECT_SIGNAL_DISTANCE -> this::correctSignal;
            case MISSING_SIGNAL_DISTANCE -> this::missingSignal;
            default -> this::potentialNoise;
        };
    }

    private void missingSignal(final int currentVal) throws ExtractedSequenceFinishedException {
        final var missingSignal = currentVal + 1;
        if (this.extractedSequences.getLast().missing.isEmpty()) this.extractedSequences.getLast().missing = Optional.of(missingSignal);
        else throw new ExtractedSequenceFinishedException(currentVal, this::correctSignal);
    }

    private void correctSignal(final int currentVal) {
        this.extractedSequences.getLast().sequence.add(currentVal);
    }

    private void duplicateSignal(final int currentVal) {
        if (this.extractedSequences.getLast().duplicate.isEmpty()) this.extractedSequences.getLast().duplicate = Optional.of(currentVal);
        else potentialNoise(currentVal);
    }

    private void potentialNoise(final int currentVal) throws ExtractedSequenceFinishedException {
        try {
            //verify that it does not fit in the existing sequence
            if (distanceFromLastSignal(currentVal) == CORRECT_SIGNAL_DISTANCE) this.correctSignal(currentVal);
            else noiseSignal(currentVal);
        }  catch (SequenceStartedException e) {
            //it is noise
            noiseSignal(currentVal);
        }
    }

    private void noiseSignal(final int currentVal) throws ExtractedSequenceFinishedException {
        //when noise ascertained, move on to next sequence
        throw new ExtractedSequenceFinishedException(currentVal, val -> {});
    }

}

class ExtractedSequence implements Comparable<ExtractedSequence> {

    protected Optional<Integer> duplicate,
            missing;

    protected LinkedList<Integer> sequence;

    protected ExtractedSequence() {
        this.duplicate = Optional.empty();
        this.missing = Optional.empty();
        this.sequence = new LinkedList<>();
    }

    @Override
    public int compareTo(ExtractedSequence o) {
        return this.sequence.size() > o.sequence.size() ? -1 : 1;   //setup in descending order
    }

}

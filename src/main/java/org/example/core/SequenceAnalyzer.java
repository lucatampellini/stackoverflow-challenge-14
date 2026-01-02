package org.example.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SequenceAnalyzer {

    private static final int DUPLICATE_SIGNAL_DISTANCE = 0,
            CORRECT_SIGNAL_DISTANCE = 1,
            MISSING_SIGNAL_DISTANCE = 2;

    private final int[] sortedSequence;

    private Optional<Integer> duplicate,
            missing;

    private final List<Integer> signal,
            noise;

    private final AtomicInteger positionCounter;

    protected SequenceAnalyzer(int... sequenceToAnalyse) {
        super();
        this.sortedSequence = IntStream.of(sequenceToAnalyse)
                .sorted()
                .toArray();
        this.duplicate = Optional.empty();
        this.missing = Optional.empty();
        this.noise = new ArrayList<>();
        this.signal = new ArrayList<>();
        this.positionCounter = new AtomicInteger(); // resetCounter() sets the value to 0
    }

    public SequenceAnalysis process() {
        this.resetCounter();
        //walk the array
        try {
            walk();
        } catch (SequenceFinishedException e) {
            e.processLastValue();
            //check what is left in the original sequence
            handleLeftOverSignals();
        }
        //produce result
        return new SequenceAnalysis(
                signal.get(0),
                signal.get(signal.size()-1),
                missing.orElse(null),
                duplicate.orElse(null),
                noise
        );

    }

    private void walk() throws SequenceFinishedException {
        //evaluation on current element
        //take current position
        final var currentPos = this.positionCounter.get();
        //check if next available
        if (currentPos+1 > sortedSequence.length) { //TODO test by catch on ArrayOutOfBoundsException instead
            //no values for current pos
            //TODO should be sequence finished instead
            throw new IllegalStateException();
        }
        //take current value
        final var currentVal = sortedSequence[currentPos];

        //evaluation on relation of current element to the next
        //if the next element is the last one
        if (currentPos+1 == sortedSequence.length) {
            //last element
            //action on it depends on how it compares with the last correct signal
            throw new SequenceFinishedException(currentVal, determineActionForCurrentVal(distanceFromLastSignal(currentVal)));
        }
        //conditional branching depending on distance from next value in the sorted sequence
        determineActionForCurrentVal(distanceToNextValInSequence()).accept(currentVal);

        //recursion
        //move pos counter to next pos
        this.positionCounter.incrementAndGet();
        walk();
    }

    private synchronized void resetCounter() {
        this.positionCounter.set(0);
    }

    private int distanceToNextValInSequence() {
        final int currentPos = this.positionCounter.get();
        return Math.abs(sortedSequence[currentPos] - sortedSequence[currentPos + 1]);
    }

    private int distanceFromLastSignal(final int currentVal) throws SequenceStartedException {
        try {
            return Math.abs(currentVal - signal.get(signal.size() - 1));
        } catch (IndexOutOfBoundsException e) {
            // still no correct signal available
            throw new SequenceStartedException(currentVal);
        }
    }

    private Consumer<Integer> determineActionForCurrentVal(final int distanceFromSensibleVal) throws SequenceFinishedException {
        return switch (distanceFromSensibleVal) {
            case DUPLICATE_SIGNAL_DISTANCE -> this::duplicateSignal;
            case CORRECT_SIGNAL_DISTANCE -> this::correctSignal;
            case MISSING_SIGNAL_DISTANCE -> this::missingSignal;
            default -> this::potentialNoise;
        };
    }
    private void potentialNoise(final int currentVal) {
        try {
            //verify that it does not fit in the existing sequence
            if (distanceFromLastSignal(currentVal) == CORRECT_SIGNAL_DISTANCE) this.correctSignal(currentVal);
            else this.noise.add(currentVal);
        }  catch (SequenceStartedException e) {
            //it is noise
            this.noise.add(currentVal);
        }
    }

    private void missingSignal(final int currentVal) throws SequenceFinishedException {
        if (missing.isEmpty()) missing = Optional.of(currentVal+1);
        else throw new SequenceFinishedException(currentVal, this::correctSignal);
    }

    private void correctSignal(final int currentVal) {
        signal.add(currentVal);
    }

    private void duplicateSignal(final int currentVal) {
        if (duplicate.isEmpty()) duplicate = Optional.of(currentVal);
        else potentialNoise(currentVal);
    }

    private void handleLeftOverSignals() {
        //to noise
        Arrays.stream(Arrays.copyOfRange(sortedSequence, positionCounter.get(), sortedSequence.length))
                .boxed()
                .forEach(this.noise::add);
    }

}

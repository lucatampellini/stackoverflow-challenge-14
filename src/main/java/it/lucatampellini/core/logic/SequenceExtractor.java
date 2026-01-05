package it.lucatampellini.core.logic;

import it.lucatampellini.core.exceptions.OriginalSequenceFinishedException;
import it.lucatampellini.core.exceptions.SequenceStartedException;
import it.lucatampellini.core.exceptions.ExtractedSequenceFinishedException;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class SequenceExtractor {

    private final Integer[] sequenceToAnalyse;
    private final LinkedList<ExtractedSequence> extractedSequences;

    private final AtomicInteger originalSequenceCounter, currentSequenceCounter;

    protected SequenceExtractor(Integer... sequenceToAnalyse) {
        super();
        this.sequenceToAnalyse = sequenceToAnalyse;
        this.extractedSequences = new LinkedList<>();
        this.originalSequenceCounter = new AtomicInteger(0);
        this.currentSequenceCounter = new AtomicInteger(0);
    }

    protected List<ExtractedSequence> extractSequences() {
        //iterates over the original sequence with originalSequenceCounter

        final var posOnOriginalSequence = this.originalSequenceCounter.getAndIncrement();
        this.currentSequenceCounter.set(posOnOriginalSequence);
        this.setupNewSequence();

        //extract one sequence, by walking over the array from posOnOriginalSequence
        try {
            walk();
        } catch (OriginalSequenceFinishedException e) {
            try {
                e.processLastValue();
            } catch (ExtractedSequenceFinishedException innerEx) {
                // if the last value is noise again, no actions
            }
        }

        if (this.originalSequenceCounter.get() < sequenceToAnalyse.length) {
            //recursion
            this.extractSequences();
        }

        return this.extractedSequences;
    }

    private void walk() throws OriginalSequenceFinishedException {
        //iterates over the original sequence with currentSequenceCounter

        try {
            //evaluation on current element
            //take current position
            final var posOnCurrentSequence = this.currentSequenceCounter.getAndIncrement();
            //take current value
            final var currentVal = sequenceToAnalyse[posOnCurrentSequence];

            //evaluation on relation of current element to the previous
            final var action = determineActionForCurrentVal(currentVal);

            //check if we reached end of sequence, before activating the action
            if (posOnCurrentSequence + 1 >= sequenceToAnalyse.length) {
                throw new OriginalSequenceFinishedException(currentVal, action);
            } else {
                action.accept(currentVal);
            }

        } catch (ExtractedSequenceFinishedException e) {
            e.processLastValue();
            this.setupNewSequence();
        }

        //recursion
        walk();
    }

    private void setupNewSequence() {
        this.extractedSequences.add(new ExtractedSequence());
    }

    @Deprecated(forRemoval = true)
    private int distanceToNextValInSequence(final int currentPos) {
        return Math.abs(sequenceToAnalyse[currentPos] - sequenceToAnalyse[currentPos + 1]);
    }

    private int distanceFromLastSignal(final int currentVal) throws SequenceStartedException {
        try {
            return Math.abs(currentVal - this.extractedSequences.getLast().sequence.getLast());
        } catch (NoSuchElementException e) {
            // still no correct signal available
            throw new SequenceStartedException(currentVal);
        }
    }

    private Consumer<Integer> determineActionForCurrentVal(final int currentVal) {
        try {
            //conditional branching, depending on distance from last signal in the extracted sequence
            final var distance = distanceFromLastSignal(currentVal);
            return switch (distance) {
                case Constants.DUPLICATE_SIGNAL_DISTANCE -> this::duplicateSignal;
                case Constants.CORRECT_SIGNAL_DISTANCE -> this::correctSignal;
                case Constants.MISSING_SIGNAL_DISTANCE -> this::missingSignal;
                default -> this::noiseSignal;
            };
        } catch (SequenceStartedException e) {
            //if the sequence just started, then signal is assumed to be correct
            return this::correctSignal;
        }
    }

    private void missingSignal(final int currentVal) throws ExtractedSequenceFinishedException {
        final var missingSignal = currentVal - 1;   //the value between this and the last signal
        if (this.extractedSequences.getLast().missing.isEmpty()) {
            this.correctSignal(currentVal);
            this.extractedSequences.getLast().missing = Optional.of(missingSignal);
        }
        else noiseSignal(currentVal);
    }

    private void correctSignal(final int currentVal) {
        this.extractedSequences.getLast().sequence.add(currentVal);
    }

    private void duplicateSignal(final int currentVal) {
        if (this.extractedSequences.getLast().duplicate.isEmpty())
            this.extractedSequences.getLast().duplicate = Optional.of(currentVal);
        else noiseSignal(currentVal);
    }

    private void noiseSignal(final int currentVal) throws ExtractedSequenceFinishedException {
        //when noise ascertained, move on to next sequence
        throw new ExtractedSequenceFinishedException(currentVal);
    }

}

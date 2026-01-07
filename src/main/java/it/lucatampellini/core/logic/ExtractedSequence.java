package it.lucatampellini.core.logic;

import java.util.LinkedList;
import java.util.Optional;

class ExtractedSequence implements Comparable<ExtractedSequence> {

    protected Optional<Integer> duplicate,
            missing;

    protected final LinkedList<Integer> sequence;

    protected ExtractedSequence() {
        this.duplicate = Optional.empty();
        this.missing = Optional.empty();
        this.sequence = new LinkedList<>();
    }

    @Override
    public int compareTo(ExtractedSequence o) {
        return this.sequence.size() > o.sequence.size() ? -1 :
                this.sequence.size() == o.sequence.size()? 0 : 1;   //setup in descending order
    }

}

package org.example.core;

import java.util.Arrays;
import java.util.List;

public record SequenceAnalysis(Integer start, Integer end, Integer missing, Integer duplicate, Integer... noise) {

    @Override
    public String toString() {
        return "SequenceAnalysis{" +
                "start=" + start +
                ", end=" + end +
                ", missing=" + missing +
                ", duplicate=" + duplicate +
                ", noise=" + Arrays.toString(noise) +
                '}';
    }
}

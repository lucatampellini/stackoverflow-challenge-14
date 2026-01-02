package org.example.core;

import java.util.List;

public record SequenceAnalysis(Integer start, Integer end, Integer missing, Integer duplicate, List<Integer> noise) {
}

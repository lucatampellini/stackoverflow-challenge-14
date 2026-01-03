package org.example;

import org.example.core.SequenceAnalyzer;
import org.example.core.SequenceAnalyzerFactory;
import org.example.utils.StringContainingArray;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {

        final var resultingSumCounter = new ResultingSum();

        try(final var lines = Files.lines(Path.of(ClassLoader.getSystemResource("number-sequences").toURI()))) {
            lines.map(StringContainingArray::new)
                    .map(StringContainingArray::extractContent)
                    .map(SequenceAnalyzerFactory::make)
                    .map(SequenceAnalyzer::process)
                    .peek(resultingSumCounter::addAnalysis)
                    .forEach(System.out::println);
        };
        System.out.println("Resulting sum is [%d]".formatted(resultingSumCounter.getSum()));
    }
}
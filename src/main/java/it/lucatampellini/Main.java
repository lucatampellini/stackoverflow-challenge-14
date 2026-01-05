package it.lucatampellini;

import it.lucatampellini.core.logic.ISequenceAnalyzer;
import it.lucatampellini.utils.StringContainingArray;
import it.lucatampellini.core.logic.SequenceAnalyzerFactory;

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
                    .map(ISequenceAnalyzer::process)
                    .peek(resultingSumCounter::addAnalysis)
                    .forEach(System.out::println);
        };
        System.out.println("Resulting sum is [%d]".formatted(resultingSumCounter.getSum()));
    }
}
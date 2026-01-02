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

        try(final var lines = Files.lines(Path.of(ClassLoader.getSystemResource("additional-number-sequences").toURI()))) {
            lines.map(StringContainingArray::new)
                    .map(StringContainingArray::extractContent)
                    .map(SequenceAnalyzerFactory::make)
                    .map(SequenceAnalyzer::process)
                    .forEach(System.out::println);
        };
    }
}
package org.example.utils;

import java.util.Arrays;

public class StringContainingArray {

    protected static final String ARRAY_OPENING_SYMBOL = "[",
        ARRAY_CLOSING_SYMBOL = "]",
        VALUE_SEPARATOR = ",";

    private final String writtenArray;

    public StringContainingArray(final String writtenArray) {
        this.writtenArray = writtenArray;
    }

    public int[] extractContent() {
        return Arrays.stream(writtenArray
                .strip()
                .replace(" ", "")
                .replace(ARRAY_OPENING_SYMBOL, "")
                .replace(ARRAY_CLOSING_SYMBOL,"")
                .split(VALUE_SEPARATOR))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

}

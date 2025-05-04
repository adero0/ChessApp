package org.example.vuejstest.security;

import java.security.SecureRandom;
import java.util.stream.Collectors;

public class GameIdGenerator {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excluded ambiguous chars
    private static final SecureRandom random = new SecureRandom();
    private static final int LENGTH = 8;

    public static String generateReadableId() {
        return random.ints(LENGTH, 0, CHARACTERS.length())
                .mapToObj(i -> String.valueOf(CHARACTERS.charAt(i)))
                .collect(Collectors.joining());
    }
}
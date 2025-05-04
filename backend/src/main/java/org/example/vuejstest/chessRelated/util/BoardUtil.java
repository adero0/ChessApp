package org.example.vuejstest.chessRelated.util;

public class BoardUtil {
    public static int transpose(int square) {
        return ((square / 8) + 1) * 8 - 1 - square % 8;
    }
}

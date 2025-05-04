package org.example.vuejstest.chessRelated.util;

public class BinaryStringToChessPos {
    public static String binaryStringToChessPos(String binaryStringPos) {
        binaryStringPos = String.format("%64s", binaryStringPos).replace(' ', '0');

        StringBuilder fullChessPositionOutput = new StringBuilder();

        for (int i = 0; i < 64; i+=8) {
            fullChessPositionOutput.append(binaryStringPos, i, i+4);
            fullChessPositionOutput.append(" ");
            fullChessPositionOutput.append(binaryStringPos, i+4, i+8);
            fullChessPositionOutput.append("\n");
        }
        return fullChessPositionOutput.toString();
    }

    public static String bitboardToChessPos(long bitoboardPos) {
        return binaryStringToChessPos(Long.toBinaryString(bitoboardPos));
    }
}
//100000001000000010000000100000001000000010000000100000000
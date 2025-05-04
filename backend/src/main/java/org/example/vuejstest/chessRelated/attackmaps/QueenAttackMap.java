package org.example.vuejstest.chessRelated.attackmaps;

public class QueenAttackMap implements AttackMapSingle, SlidingPiece {
    private final long[] queenAttackMap = generateQueenAttackMap();

    private long[] generateQueenAttackMap() {
        long[] queenAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            queenAttackMapping[i] = generateSquareAttackMap(i);
        }
        return queenAttackMapping;
    }

    private long generateSquareAttackMap(int square) {
        square = ((square / 8) + 1) * 8 - 1 - square % 8; // TODO why are we transposing the queen????
        long squareAttackMap = 0L;
        int row = square / 8;
        int col = square % 8;
        //horizontal and vertical
        for (int i = 0; i < 8; i++) {
            squareAttackMap |= 1L << (row * 8 + i);
            squareAttackMap |= 1L << (i * 8 + col);
        }

        //diagonal and reverse diagonal
        for (int i = -7; i <= 7; i++) {
            if (row + i >= 0 && row + i < 8 && col + i >= 0 && col + i < 8) {
                squareAttackMap |= 1L << ((row + i) * 8 + (col + i));
            }
            if (row + i >= 0 && row + i < 8 && col - i >= 0 && col - i < 8) {
                squareAttackMap |= 1L << ((row + i) * 8 + (col - i));
            }
        }

        squareAttackMap &= ~(1L << square);

        return squareAttackMap;
    }
    public long[] getAttackMap() {
        return queenAttackMap;
    }

}

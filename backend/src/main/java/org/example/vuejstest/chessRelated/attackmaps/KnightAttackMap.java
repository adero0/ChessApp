package org.example.vuejstest.chessRelated.attackmaps;


public class KnightAttackMap implements AttackMapSingle {
    private final long[] knightAttackMap = generatePieceAttackMap();

    private long[] generatePieceAttackMap() {
        long[] knightAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            knightAttackMapping[i] = generateSquareAttackMap(i);
        }
        return knightAttackMapping;
    }

    private long generateSquareAttackMap(int square) {
        long squareAttackMap = 0L;
        if (square % 8 != 7 && square + 17 <= 63)
            squareAttackMap |= 1L << (square + 17);
        if (square % 8 != 0 && square - 17 >= 0)
            squareAttackMap |= 1L << (square - 17);
        if (square % 8 < 6 && square + 10 <= 63)
            squareAttackMap |= 1L << (square + 10);
        if (square % 8 > 1 && square - 10 >= 0)
            squareAttackMap |= 1L << (square - 10);
        if (square % 8 > 1 && square + 6 <= 63)
            squareAttackMap |= 1L << (square + 6);
        if (square % 8 < 6 && square - 6 >= 0)
            squareAttackMap |= 1L << (square - 6);
        if (square % 8 != 0 && square + 15 <= 63)
            squareAttackMap |= 1L << (square + 15);
        if (square % 8 != 7 && square - 15 >= 0)
            squareAttackMap |= 1L << (square - 15);
        return squareAttackMap;
    }

    public long[] getAttackMap() {
        return knightAttackMap;
    }
}

package org.example.vuejstest.chessRelated.attackmaps;

public class RookAttackMap implements AttackMapSingle, SlidingPiece {
    final private long[] rookAttackMap = generatePieceAttackMap();


    private long[] generatePieceAttackMap() {
        long[] rookAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            rookAttackMapping[i] = generateSquareAttackMap(i);
        }
        return rookAttackMapping;
    }

    private long generateSquareAttackMap(int square) {
        long squareAttackMap = 0L;
        int row = square / 8;
        int col = square % 8;
        //vertical and horizontal
        for (int i = 0; i < 8; i++) {
            squareAttackMap |= 1L << (row * 8 + i);
            squareAttackMap |= 1L << (i * 8 + col);
        }

        squareAttackMap &= ~(1L << square);

        return squareAttackMap;
    }

    public long[] getAttackMap() {
        return rookAttackMap;
    }
}

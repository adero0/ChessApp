package org.example.vuejstest.chessRelated.attackmaps;


public class PawnAttackMap implements AttackMapDual{

    private final long[] whitePawnAttackMap = generateWhitePawnAttackMap();
    private final long[] blackPawnAttackMap = generateBlackPawnAttackMap();

    private long[] generateWhitePawnAttackMap() {
        long[] pawnAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            pawnAttackMapping[i] = generateSquareAttackMap(i, true);
        }
        return pawnAttackMapping;
    }

    private long[] generateBlackPawnAttackMap() {
        long[] pawnAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            pawnAttackMapping[i] = generateSquareAttackMap(i, false);
        }
        return pawnAttackMapping;
    }

    private long generateSquareAttackMap(int square, boolean isWhite) {
        long squareAttackMap = 0L;

        if (isWhite) {
            if (square + 9 <= 63 && square % 8 != 7) {
                squareAttackMap |= 1L << (square + 9);
            }
            if (square + 7 <= 63 && square % 8 != 0) {
                squareAttackMap |= 1L << (square + 7);
            }
        } else {
            if (square - 9 >= 0 && square % 8 != 0) {
                squareAttackMap |= 1L << (square - 9);
            }
            if (square - 7 >= 0 && square % 8 != 7) {
                squareAttackMap |= 1L << (square - 7);
            }
        }

        return squareAttackMap;
    }


    public long[] getWhitePawnAttackMap() {
        return whitePawnAttackMap;
    }

    public long[] getBlackPawnAttackMap() {
        return blackPawnAttackMap;
    }
}

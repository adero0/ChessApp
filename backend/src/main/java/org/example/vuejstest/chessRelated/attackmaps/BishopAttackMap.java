package org.example.vuejstest.chessRelated.attackmaps;

public class BishopAttackMap implements AttackMapSingle, SlidingPiece {
    final private long[] bishopAttackMap = generatePieceAttackMap();

private long[] generatePieceAttackMap() {
    long[] bishopAttackMapping = new long[64];
    for (int i = 0; i < 64; i++) {
        bishopAttackMapping[i] = generateSquareAttackMap(i);
    }
    return bishopAttackMapping;
}

private static long generateSquareAttackMap(int square) {
    long squareAttackMap = 0L;
    int row = square / 8;
    int col = square % 8;
    //diagonala i diagonala przeciwna
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

@Override
public String toString() {
    return super.toString();
}


@Override
public long[] getAttackMap() {
    return bishopAttackMap;
}
}

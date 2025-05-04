package org.example.vuejstest.chessRelated.board.pieces;


public interface Piece {
    long[] getAttackMap();
    long getBitboard();
    void setBitboard(long newBitboard);
    Piece clone();
}

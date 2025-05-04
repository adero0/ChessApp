package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.QueenAttackMap;

public class  Queen implements Piece {
    static QueenAttackMap queenAttackMap = new QueenAttackMap();
    private long bitboard;

    public Queen() {}
    public Queen(Queen other) {
        this.bitboard = other.bitboard;
    }
    @Override
    public Queen clone() {
        return new Queen(this);
    }
    public void addToBitboard (long value){
        bitboard |= value;
    }



    @Override
    public long[] getAttackMap() {
        return queenAttackMap.getAttackMap();
    }

    public long getBitboard() {
        return bitboard;
    }

    @Override
    public void setBitboard(long newBitboard) {
        bitboard = newBitboard;
    }
}

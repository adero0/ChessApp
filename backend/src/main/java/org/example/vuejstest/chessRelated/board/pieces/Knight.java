package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.KnightAttackMap;

public class Knight implements Piece {
    static KnightAttackMap knightAttackMap = new KnightAttackMap();
    private long bitboard = 0x0L;

    public Knight() {}
    public Knight(Knight other) {
        this.bitboard = other.bitboard;
    }
    @Override
    public Knight clone() {
        return new Knight(this);
    }

    public void addToBitboard(long value) {
        bitboard |= value;
    }

    @Override
    public long[] getAttackMap() {
        return knightAttackMap.getAttackMap();
    }

    public long getBitboard() {
        return bitboard;
    }

    @Override
    public void setBitboard(long newBitboard) {
        bitboard = newBitboard;
    }
}

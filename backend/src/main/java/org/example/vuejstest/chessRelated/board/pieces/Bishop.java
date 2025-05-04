package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.BishopAttackMap;

public class Bishop implements Piece{
    static BishopAttackMap bishopAttackMap = new BishopAttackMap();
    private long bitboard = 0x0L;
    public Bishop() {}
    public Bishop(Bishop other) {
        this.bitboard = other.bitboard;
    }
    @Override
    public Bishop clone() {
        return new Bishop(this);
    }

    public void addToBitboard(long value) {
        bitboard |= value;
    }

    @Override
    public long[] getAttackMap() {
        return bishopAttackMap.getAttackMap();
    }

    public long getBitboard() {
        return bitboard;
    }

    @Override
    public void setBitboard(long newBitboard) {
        bitboard = newBitboard;
    }
}

package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.RookAttackMap;

public class Rook implements Piece{
    static RookAttackMap rookAttackMap = new RookAttackMap();
    private long bitboard = 0x0L;

    public Rook() {}
    public Rook(Rook other) {
        this.bitboard = other.bitboard;
    }
    @Override
    public Rook clone() {
        return new Rook(this);
    }

    public void addToBitboard(long value) {
        bitboard |= value;
    }

    @Override
    public long[] getAttackMap() {
        return rookAttackMap.getAttackMap();
    }

    public long getBitboard() {
        return bitboard;
    }

    @Override
    public void setBitboard(long newBitboard) {
        bitboard = newBitboard;
    }
}

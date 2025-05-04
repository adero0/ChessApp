package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.KingAttackMap;
import org.example.vuejstest.chessRelated.enums.PieceColor;

public class King implements Piece {
    static KingAttackMap kingAttackMap = new KingAttackMap();
    private boolean isWhiteKingInCheck = false;
    private boolean isBlackKingInCheck = false;
    private long bitboard = 0x0L;

    public King() {}
    public King(King other) {
        this.bitboard = other.bitboard;
        this.isWhiteKingInCheck = other.isWhiteKingInCheck;
        this.isBlackKingInCheck = other.isBlackKingInCheck;
    }
    @Override
    public King clone() {
        return new King(this);
    }

    public void place(long value) {
        bitboard |= value;
    }

    @Override
    public long[] getAttackMap() {
        return kingAttackMap.getAttackMap();
    }


    @Override
    public long getBitboard() {
        return bitboard;
    }

    public boolean isKingInCheck(PieceColor color) {
        return color == PieceColor.WHITE ? isWhiteKingInCheck: isBlackKingInCheck;
    }

    public void setKingToCheck(PieceColor color) {
        if (color == PieceColor.WHITE) {
            isWhiteKingInCheck = true;
        }
        else {
            isBlackKingInCheck = true;
        }
    }

    @Override
    public void setBitboard(long newBitboard) {
        bitboard = newBitboard;
    }
}

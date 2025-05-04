package org.example.vuejstest.chessRelated.board.pieces;

import org.example.vuejstest.chessRelated.attackmaps.PawnAttackMap;
import org.example.vuejstest.chessRelated.attackmaps.PawnMoveMap;
import org.example.vuejstest.chessRelated.enums.PieceColor;

public class Pawn implements Piece {
    static PawnAttackMap pawnAttackMap = new PawnAttackMap();
    static PawnMoveMap pawnMoveMap = new PawnMoveMap();
    private long white = 0x0L;
    private long black = 0x0L;
    public void addToWhite(long value){
        white |= value;
    }
    public void addToBlack(long value){
        black |= value;
    }

    public Pawn() {}
    public Pawn(Pawn other) {
        this.white = other.white;
        this.black = other.black;
    }
    @Override
    public Pawn clone() {
        return new Pawn(this);
    }

    public PawnAttackMap getPawnAttackMap() {
        return pawnAttackMap;
    }

    public long getColorBitboard(PieceColor color) {
        return color == PieceColor.WHITE ?  white : black;
    }

    public long getWhite() {
        return white;
    }

    public void setBlack(long black) {
        this.black = black;
    }
    public void setWhite(long white) {
        this.white = white;
    }

    public long getBlack() {
        return black;
    }

    private long[] getBlackAttackMap(){
        return pawnAttackMap.getBlackPawnAttackMap();
    }
    private long[] getWhiteAttackMap(){
        return pawnAttackMap.getWhitePawnAttackMap();
    }

    public long getPawnMoveMap(int square, PieceColor pieceColor){
        return pieceColor==PieceColor.WHITE ? pawnMoveMap.getWhitePawnMoveMap()[square] : pawnMoveMap.getBlackPawnMoveMap()[square];
    }

    public long[] getAttackMap(PieceColor color) {
        return color==PieceColor.WHITE ? getWhiteAttackMap() : getBlackAttackMap();
    }
    public long[] getAttackMap() {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public long getBitboard() {
        return 0;
    }

    public void setBitboard(long newBitboard, PieceColor color) {
        if(color == PieceColor.WHITE) {
            white = newBitboard;
        }
        else {
            black = newBitboard;
        }
    }

    @Override
    public void setBitboard(long newBitboard) {
        throw new IllegalArgumentException("Not implemented yet");
    }


}

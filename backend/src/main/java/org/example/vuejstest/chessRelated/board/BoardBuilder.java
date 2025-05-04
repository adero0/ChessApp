package org.example.vuejstest.chessRelated.board;

import org.example.vuejstest.chessRelated.board.pieces.Piece;
import org.example.vuejstest.chessRelated.board.pieces.*;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;
import org.example.vuejstest.chessRelated.exceptions.BuildException;
import org.example.vuejstest.chessRelated.exceptions.PositionValidationException;

import java.util.Map;


/*
This class allows the building of a chessboard via adding different pieces.
contains builder chainable methods for making a custom board and a standard build setup
 */
public class BoardBuilder {

    private Pieces pieces;
    private Map<PieceType, Piece> piecesMap;
    private long bitboard = 0x0L;
    private long whiteBitboard = 0x0L;
    private long blackBitboard = 0x0L;

    public BoardBuilder buildStandardBoard() {
        this.bitboard = 0x0L;
        this.pieces = new Pieces();
        this.piecesMap = pieces.piecesMap;
        this.whiteBitboard = 0x0L;
        this.blackBitboard = 0x0L;
        buildAllPiecesNormal();
        return this;
    }

    public void clearBoard() {
        this.bitboard = 0x0L;
        this.pieces = new Pieces();
    }

    /**
     * MAKE 100% SURE THAT THE PIECE TYPE CORRESPONDS TO THE ACTUAL PIECE OR BAD THINGS HAPPEN
     */
    public BoardBuilder removePiece(int square, PieceType type) {
        PieceColor pc = null;
        if ((whiteBitboard & (1L << square)) != 0) {
            if (type == PieceType.PAWN) {
                pc = PieceColor.WHITE;
            }
            this.whiteBitboard &= ~(1L << square);
        } else if ((blackBitboard & (1L << square)) != 0) {
            if (type == PieceType.PAWN) {
                pc = PieceColor.BLACK;
            }
            this.blackBitboard &= ~(1L << square);
        } else {
            throw new PositionValidationException("Error removing piece from square. No piece is there!");
        }
        this.bitboard &= ~(1L << square);
        if(pc != null) {
            Pawn pawn = (Pawn) piecesMap.get(type);
            pawn.setBitboard(pawn.getColorBitboard(pc) & ~(1L << square), pc);
            return this;
        }
        var piece = piecesMap.get(type);
        piece.setBitboard(piece.getBitboard() & ~(1L << square));
        return this;
    }

    public BoardBuilder addPiece(PieceType pieceType, int square, PieceColor pieceColor) {
        long bitboard = 1L << square;
        buildPiece(bitboard,pieceType,pieceColor);
        return this;
    }

    public BoardBuilder placeWhiteKing(long newPieces) {
//        if(((King)piecesMap.get(PieceType.KING)).getWhite() != 0){
//            throw new BuildException("White king has already been placed!");
//        } todo: consider re-reading this
        buildPiece(newPieces, PieceType.KING, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackKing(long newPieces) {
//        if(((King)piecesMap.get(PieceType.KING)).getBlack() != 0){
//            throw new BuildException("Black king has already been placed!");
//        } todo: consider re-reading this
        buildPiece(newPieces, PieceType.KING, PieceColor.BLACK);
        return this;
    }

    public BoardBuilder placeWhiteQueen(long newPieces) {
        buildPiece(newPieces, PieceType.QUEEN, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackQueen(long newPieces) {
        buildPiece(newPieces, PieceType.QUEEN, PieceColor.BLACK);
        return this;
    }

    public BoardBuilder placeWhiteRooks(long newPieces) {
        buildPiece(newPieces, PieceType.ROOK, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackRooks(long newPieces) {
        buildPiece(newPieces, PieceType.ROOK, PieceColor.BLACK);
        return this;
    }

    public BoardBuilder placeWhiteBishops(long newPieces) {
        buildPiece(newPieces, PieceType.BISHOP, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackBishops(long newPieces) {
        buildPiece(newPieces, PieceType.BISHOP, PieceColor.BLACK);
        return this;
    }

    public BoardBuilder placeWhiteKnights(long newPieces) {
        buildPiece(newPieces, PieceType.KNIGHT, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackKnights(long newPieces) {
        buildPiece(newPieces, PieceType.KNIGHT, PieceColor.BLACK);
        return this;
    }

    public BoardBuilder placeWhitePawns(long newPieces) {
        buildPiece(newPieces, PieceType.PAWN, PieceColor.WHITE);
        return this;
    }

    public BoardBuilder placeBlackPawns(long newPieces) {
        buildPiece(newPieces, PieceType.PAWN, PieceColor.BLACK);
        return this;
    }
    private void checkBitboard(long addend) {
        if ((bitboard & addend) != 0) {
            throw new BuildException("Pieces can't overlap");
        }
    }

    private void addToBitboard(long addend) {
        bitboard |= addend;
    }

    private void buildAllPiecesNormal() {
        ((Queen)piecesMap.get(PieceType.QUEEN)).addToBitboard(0x1000000000000010L);
        ((Rook)piecesMap.get(PieceType.ROOK)).addToBitboard(0x8100000000000081L);
        ((Bishop)piecesMap.get(PieceType.BISHOP)).addToBitboard(0x2400000000000024L);
        ((Knight)piecesMap.get(PieceType.KNIGHT)).addToBitboard(0x4200000000000042L);
        ((Pawn)piecesMap.get(PieceType.PAWN)).addToBlack(0x00FF000000000000L);
        ((Pawn)piecesMap.get(PieceType.PAWN)).addToWhite(0x000000000000FF00L);
        ((King)piecesMap.get(PieceType.KING)).place(0x0000000000000008L);
        ((King)piecesMap.get(PieceType.KING)).place(0x0800000000000000L);
        addToBitboard(0xFFFF00000000FFFFL);
        addToColorBitboard(0x000000000000FFFFL, PieceColor.WHITE);
        addToColorBitboard(0xFFFF000000000000L, PieceColor.BLACK);
    }

    private void buildPiece(long newPieces, PieceType pieceType, PieceColor color) {
        checkBitboard(newPieces);
        addToBitboard(newPieces);
        addToColorBitboard(newPieces, color);
        switch (pieceType) {
            case QUEEN -> ((Queen)piecesMap.get(PieceType.QUEEN)).addToBitboard(newPieces);
            case ROOK -> ((Rook)piecesMap.get(PieceType.ROOK)).addToBitboard(newPieces);
            case BISHOP -> ((Bishop)piecesMap.get(PieceType.BISHOP)).addToBitboard(newPieces);
            case KNIGHT -> ((Knight)piecesMap.get(PieceType.KNIGHT)).addToBitboard(newPieces);
            case KING -> ((King)piecesMap.get(PieceType.KING)).place(newPieces);
            case PAWN -> {
                if (color == PieceColor.WHITE) {
                    ((Pawn)piecesMap.get(PieceType.PAWN)).addToWhite(newPieces);
                } else {
                    ((Pawn)piecesMap.get(PieceType.PAWN)).addToBlack(newPieces);
                }
            }
        }
    }

    private void addToColorBitboard(long newPieces, PieceColor color) {
        if (color == PieceColor.WHITE) {
            whiteBitboard |= newPieces;
        } else {
            blackBitboard |= newPieces;
        }

    }

    public Pieces getPieces() {
        return pieces;
    }

    public long getBitboard() {
        return bitboard;
    }

    public long getWhiteBitboard() {
        return whiteBitboard;
    }

    public long getBlackBitboard() {
        return blackBitboard;
    }
}




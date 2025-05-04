package org.example.vuejstest.chessRelated.board;

import org.example.vuejstest.chessRelated.board.pieces.*;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;

import java.util.HashMap;
import java.util.Map;

public class Pieces {
    public Map<PieceType, Piece> piecesMap;

    public Pieces() {
        this.piecesMap = new HashMap<>(6);
        piecesMap.put(PieceType.QUEEN, new Queen());
        piecesMap.put(PieceType.KING, new King());
        piecesMap.put(PieceType.BISHOP, new Bishop());
        piecesMap.put(PieceType.KNIGHT, new Knight());
        piecesMap.put(PieceType.ROOK, new Rook());
        piecesMap.put(PieceType.PAWN, new Pawn());
    }

    public Pieces(Pieces other) {
        this.piecesMap = new HashMap<>(6);
        for (Map.Entry<PieceType, Piece> entry : other.piecesMap.entrySet()) {
            this.piecesMap.put(entry.getKey(), entry.getValue().clone());
        }
    }

    public long[] getAttackMap(PieceType pieceType, PieceColor pieceColor) {
        if (pieceType == PieceType.PAWN) {
            return ((Pawn) piecesMap.get(pieceType)).getAttackMap(pieceColor);
        }
        return piecesMap.get(pieceType).getAttackMap();
    }

}

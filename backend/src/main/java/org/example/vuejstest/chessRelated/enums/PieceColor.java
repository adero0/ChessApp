package org.example.vuejstest.chessRelated.enums;

public enum PieceColor {
    WHITE, BLACK;
    public PieceColor getOpposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}

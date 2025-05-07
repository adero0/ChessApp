package org.example.vuejstest.models.enums;
import org.example.vuejstest.chessRelated.enums.PieceColor;

import static org.example.vuejstest.chessRelated.enums.PieceColor.BLACK;
import static org.example.vuejstest.chessRelated.enums.PieceColor.WHITE;

public enum GameStatus {
    ONGOING, DRAW, WHITE_CHECKMATE,BLACK_CHECKMATE;
    public static GameStatus getProperCheckmate(PieceColor winnerColor) {
        return winnerColor == WHITE ? WHITE_CHECKMATE: BLACK_CHECKMATE;
    }
}


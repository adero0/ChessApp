package org.example.vuejstest.chessRelated.attackmaps;

import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.util.Constants;

public class PawnMoveMap implements AttackMapDual {

    private final long[] whitePawnMoveMap = generateWhitePawnMoveMap();
    private final long[] blackPawnMoveMap = generateBlackPawnMoveMap();

    private long[] generateWhitePawnMoveMap() {
        long[] pawnAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            pawnAttackMapping[i] = generateSquareAttackMap(i, PieceColor.WHITE);
        }
        return pawnAttackMapping;
    }

    private long[] generateBlackPawnMoveMap() {
        long[] pawnAttackMapping = new long[64];
        for (int i = 0; i < 64; i++) {
            pawnAttackMapping[i] = generateSquareAttackMap(i, PieceColor.BLACK);
        }
        return pawnAttackMapping;
    }

    private long generateSquareAttackMap(int square, PieceColor pieceColor) {
        long squareAttackMap = 0L;
        int row = square / 8;
        if (pieceColor == PieceColor.WHITE) {
            if(row == 1) {
                squareAttackMap |= 1L << (square +  Constants.MOVE_UP_INCREMENT * 2 );
            }
            if(square + Constants.MOVE_UP_INCREMENT <= Constants.BOARD_END) {
                squareAttackMap |= 1L << (square + Constants.MOVE_UP_INCREMENT);
            }
        } else {
            if(row == 6) {
                squareAttackMap |= 1L << (square + Constants.MOVE_DOWN_INCREMENT * 2 );
            }
            if(square + Constants.MOVE_DOWN_INCREMENT >= Constants.BOARD_START) {
                squareAttackMap |= 1L << (square + Constants.MOVE_DOWN_INCREMENT);
            }
        }

        return squareAttackMap;
    }


    public long[] getWhitePawnMoveMap() {
        return whitePawnMoveMap;
    }

    public long[] getBlackPawnMoveMap() {
        return blackPawnMoveMap;
    }
}

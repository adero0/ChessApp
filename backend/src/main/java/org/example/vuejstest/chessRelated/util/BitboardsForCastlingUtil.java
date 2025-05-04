package org.example.vuejstest.chessRelated.util;

import org.example.vuejstest.chessRelated.board.BoardBuilder;
import org.example.vuejstest.chessRelated.board.BoardState;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;

public class BitboardsForCastlingUtil {

    final static long shortCastlePattern = 0b1001;   // e1 (bit 3) + h1 (bit 0)
    final static long longCastlePattern  = 0b10001000;  // e1 (bit 3) + a1 (bit 7)

    public static boolean castleLegal(long bitboard, PieceColor pieceColor, CastlingType castlingType) {
        int shift = pieceColor == PieceColor.WHITE ? 0 : 56;

        int from = castlingType == CastlingType.SHORTCASTLE ? 0 : 3;
        int to = castlingType == CastlingType.SHORTCASTLE ? 3 : 7;

        // build mask and pattern dynamically
        long baseMask = ((1L << (to - from + 1)) - 1) << from;
        long shiftedMask = baseMask << shift;
//        System.out.println(Long.toBinaryString(shiftedMask));

        long pattern = castlingType == CastlingType.SHORTCASTLE ? shortCastlePattern : longCastlePattern;
        long shiftedPattern = pattern << shift;
//        System.out.println(Long.toBinaryString(shiftedPattern));

        return (bitboard & shiftedMask) == shiftedPattern;
    }
}

package org.example.vuejstest.chessRelated.util;


import org.example.vuejstest.chessRelated.board.BoardState;
import org.example.vuejstest.chessRelated.board.pieces.Pawn;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;

public class ChessPositionIntoFENFormat {
    public static String transformIntoFEN(BoardState boardState) {
        var pieceMap = boardState.getPieceMap();
        long whiteBitboard = boardState.getWhiteBitboard();
        long blackBitboard = boardState.getBlackBitboard();
        ////////////////////ROOKS//////////////////////////
        long rooks = pieceMap.get(PieceType.ROOK).getBitboard();
        long whiteRooks = rooks & whiteBitboard;
        long blackRooks = rooks & blackBitboard;
        ///////////////////PAWNS///////////////////////////
        Pawn pawn = (Pawn)pieceMap.get(PieceType.PAWN);
        long whitePawns = pawn.getWhite();
        long blackPawns = pawn.getBlack();
        ///////////////////KNIGHTS///////////////////////////
        long knights = pieceMap.get(PieceType.KNIGHT).getBitboard();
        long whiteKnights = knights & whiteBitboard;
        long blackKnights = knights & blackBitboard;
        ///////////////////QUEENS////////////////////////////
        long queens = pieceMap.get(PieceType.QUEEN).getBitboard();
        long whiteQueens = queens & whiteBitboard;
        long blackQueens = queens & blackBitboard;
        ///////////////////BISHOPS////////////////////////////
        long bishops = pieceMap.get(PieceType.BISHOP).getBitboard();
        long whiteBishops = bishops & whiteBitboard;
        long blackBishops = bishops & blackBitboard;
        //////////////////KINGS//////////////////////////////
        long king = pieceMap.get(PieceType.KING).getBitboard();
        long whiteKings = king & whiteBitboard;
        long blackKings = king & blackBitboard;


        StringBuilder fen = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) { // Start from rank 8 (top) to rank 1 (bottom)
            int emptyCount = 0;
            StringBuilder rankStr = new StringBuilder();

            for (int file = 0; file < 8; file++) { // Files a to h
                int bitPosition = rank * 8 + file;  // Matches your bit layout (a1=0, h8=63)
                long mask = 1L << BoardUtil.transpose(bitPosition);
                char piece = ' ';

                // Check piece types (same priority order as before)
                if ((whitePawns & mask) != 0)       piece = 'P';
                else if ((blackPawns & mask) != 0)  piece = 'p';
                else if ((whiteKnights & mask) != 0) piece = 'N';
                else if ((blackKnights & mask) != 0) piece = 'n';
                else if ((whiteBishops & mask) != 0) piece = 'B';
                else if ((blackBishops & mask) != 0) piece = 'b';
                else if ((whiteRooks & mask) != 0)  piece = 'R';
                else if ((blackRooks & mask) != 0)  piece = 'r';
                else if ((whiteQueens & mask) != 0)  piece = 'Q';
                else if ((blackQueens & mask) != 0)  piece = 'q';
                else if ((whiteKings & mask) != 0)  piece = 'K';
                else if ((blackKings & mask) != 0)  piece = 'k';

                if (piece != ' ') {
                    if (emptyCount > 0) {
                        rankStr.append(emptyCount);
                        emptyCount = 0;
                    }
                    rankStr.append(piece);
                } else {
                    emptyCount++;
                }
            }

            if (emptyCount > 0) rankStr.append(emptyCount);
            fen.append(rankStr);
            if (rank > 0) fen.append('/'); // Add slashes between ranks
        }

        // 2. Append remaining FEN components (active color, castling, etc.)
        fen.append(' ')
                .append(boardState.getCurrentPlayerTurn() == PieceColor.WHITE ? 'w' : 'b')    // 'w' or 'b'
                .append(' ')
                .append("-")
                // .append(boardState.getCastlingRights()) // e.g., "KQkq" or "-"
                .append(' ')
                .append(NotationToBitboardConverter.chessPositionIntegerToSquare(boardState.getEnPassantSquare()))// e.g., "a3" or "-"
                .append(' ')
                .append(0)
                .append(' ')
                .append(1);
                /*
                .append(boardState.getHalfmoveClock())
                .append(' ')
                .append(boardState.getFullmoveNumber());
                */
        return fen.toString();
    }
}

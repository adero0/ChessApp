package org.example.vuejstest.chessRelated.board;

import org.example.vuejstest.chessRelated.board.pieces.Pawn;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;
import org.example.vuejstest.chessRelated.util.BinaryStringToChessPos;
import org.example.vuejstest.chessRelated.util.Constants;


public class ApplyBlockers_v2 {
    BoardState boardState;

    public ApplyBlockers_v2(BoardState boardState) {
        this.boardState = boardState;

    }

    long calculateMoveWithBlockers(int square, PieceType pieceType, PieceColor pieceColor) {
        return switch (pieceType) {
            case PAWN -> calculatePawnMoveWithBlockers(square, pieceColor);
            case KNIGHT -> calculateKnightMoveWithBlockers(square, pieceColor);
            case BISHOP -> calculateBishopMoveWithBlockers(square, pieceColor);
            case ROOK -> calculateRookMoveWithBlockers(square, pieceColor);
            case QUEEN -> calculateQueenMoveWithBlockers(square, pieceColor);
            case KING -> calculateKingMoveWithBlockers(square, pieceColor);
        };
    }

    private long getPieceAttackMap(PieceType pieceType, int square) {
        return boardState.getPieceMap().get(pieceType).getAttackMap()[square];
    }

    private long getPawnAttackMapColored(int square, PieceColor pieceColor) {
        return ((Pawn) boardState.getPieceMap().get(PieceType.PAWN)).getAttackMap(pieceColor)[square];
    }

    private long getAllPawnAttacksColored(PieceColor pieceColor) {
        long pawnBitboard = ((Pawn) boardState.getPieceMap().get(PieceType.PAWN)).getColorBitboard(pieceColor);
        int square;
        long allMoves = 0;
        while (pawnBitboard != 0) {
            square = Long.numberOfTrailingZeros(pawnBitboard);
            allMoves |= getPawnAttackMapColored(square, pieceColor);
            pawnBitboard ^= Long.lowestOneBit(pawnBitboard);
        }
        return allMoves;
    }

    private long getPawnMoveMapColored(int square, PieceColor pieceColor) {
        return ((Pawn) boardState.getPieceMap().get(PieceType.PAWN)).getPawnMoveMap(square, pieceColor);
    }

    private long calculateBishopMoveWithBlockers(int square, PieceColor pieceColor) {
        long bishopAttackMask = getPieceAttackMap(PieceType.BISHOP, square);
        long whitePieces = boardState.getWhiteBitboard();
        long blackPieces = boardState.getBlackBitboard();
        long up_left = howManySquaresAvailableUP_LEFT(square, bishopAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im up_left \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(up_left)));
        long up_right = howManySquaresAvailableUP_RIGHT(square, bishopAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im up_right \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(up_right)));
        long down_left = howManySquaresAvailableDOWN_LEFT(square, bishopAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im down_left \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(down_left)));
        long down_right = howManySquaresAvailableDOWN_RIGHT(square, bishopAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im down_right \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(down_right)));
        return up_left & up_right & down_left & down_right;
    }

    private long howManySquaresAvailableDOWN_RIGHT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_DOWN_RIGHT_INCREMENT;
                currentToCheckSquare >= Constants.BOARD_START && currentToCheckSquare % 8 != Constants.BOARD_BOUND_RIGHT; // potential problem
                currentToCheckSquare += Constants.MOVE_DOWN_RIGHT_INCREMENT
        ) {
            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_RIGHT_INCREMENT);
                return setToZeroDOWN_RIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_RIGHT_INCREMENT);
                return setToZeroDOWN_RIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }

    private long setToZeroDOWN_RIGHTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere % 8 != Constants.BOARD_BOUND_RIGHT && setToZeroHere >= Constants.BOARD_START;
                setToZeroHere += Constants.MOVE_DOWN_RIGHT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long howManySquaresAvailableDOWN_LEFT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_DOWN_LEFT_INCREMENT;
                currentToCheckSquare % 8 != Constants.BOARD_BOUND_LEFT && currentToCheckSquare >= Constants.BOARD_START;
                currentToCheckSquare += Constants.MOVE_DOWN_LEFT_INCREMENT
        ) {
            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_LEFT_INCREMENT);
                return setToZeroDOWN_LEFTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_LEFT_INCREMENT);
                return setToZeroDOWN_LEFTofThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }

    private long setToZeroDOWN_LEFTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere % 8 != Constants.BOARD_BOUND_LEFT && setToZeroHere >= Constants.BOARD_START;
                setToZeroHere += Constants.MOVE_DOWN_LEFT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }


    private long howManySquaresAvailableUP_RIGHT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_UP_RIGHT_INCREMENT;
                currentToCheckSquare < Constants.BOARD_END && currentToCheckSquare % 8 != Constants.BOARD_BOUND_RIGHT;
                currentToCheckSquare += Constants.MOVE_UP_RIGHT_INCREMENT
        ) {
            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_RIGHT_INCREMENT);
                return setToZeroUP_RIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_RIGHT_INCREMENT);
                return setToZeroUP_RIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }

    private long setToZeroUP_RIGHTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere <= Constants.BOARD_END && setToZeroHere % 8 != Constants.BOARD_BOUND_RIGHT;
                setToZeroHere += Constants.MOVE_UP_RIGHT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long howManySquaresAvailableUP_LEFT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_UP_LEFT_INCREMENT;
                currentToCheckSquare < Constants.BOARD_END && currentToCheckSquare % 8 != Constants.BOARD_BOUND_LEFT;
                currentToCheckSquare += Constants.MOVE_UP_LEFT_INCREMENT
        ) {
            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_LEFT_INCREMENT);
                return setToZeroUP_LEFTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_LEFT_INCREMENT);
                return setToZeroUP_LEFTofThis(pieceAttackMap, currentToCheckSquare);
            }
        }
        return pieceAttackMap;
    }

    private long setToZeroUP_LEFTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare; /* maybe smth here when bug occurs */
                setToZeroHere <= Constants.BOARD_END && setToZeroHere % 8 != Constants.BOARD_BOUND_LEFT;
                setToZeroHere += Constants.MOVE_UP_LEFT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long calculateRookMoveWithBlockers(int square, PieceColor pieceColor) {
        long rookAttackMask = getPieceAttackMap(PieceType.ROOK, square);
        long whitePieces = boardState.getWhiteBitboard();
        long blackPieces = boardState.getBlackBitboard();

        long down = howManySquaresAvailableDOWN(square, rookAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im down \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(down)));
        long up = howManySquaresAvailableUP(square, rookAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im up \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(up)));
        long left = howManySquaresAvailableLEFT(square, rookAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im left \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(left)));
        long right = howManySquaresAvailableRIGHT(square, rookAttackMask, whitePieces, blackPieces, pieceColor);
//        System.out.println("im right \n" + BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(right)));
        return down & up & left & right;
    }


    private boolean checkCollision(long thingWeCheck, int positionToCheck) {
        return ((thingWeCheck >> positionToCheck) & 1) == 1;
    }

    private long howManySquaresAvailableUP(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_UP_INCREMENT;
                currentToCheckSquare < Constants.BOARD_END;
                currentToCheckSquare += Constants.MOVE_UP_INCREMENT
        ) {
            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_INCREMENT);
                return setToZeroAboveThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_UP_INCREMENT);
                return setToZeroAboveThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }


    private long howManySquaresAvailableDOWN(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_DOWN_INCREMENT;
                currentToCheckSquare >= Constants.BOARD_START;
                currentToCheckSquare += Constants.MOVE_DOWN_INCREMENT
        ) {

            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_INCREMENT);
                return setToZeroBelowThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_DOWN_INCREMENT);
                return setToZeroBelowThis(pieceAttackMap, currentToCheckSquare);
            }
        }

        return pieceAttackMap;
    }

    private long howManySquaresAvailableLEFT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_LEFT_INCREMENT;
                currentToCheckSquare % 8 != Constants.BOARD_BOUND_LEFT;
                currentToCheckSquare += Constants.MOVE_LEFT_INCREMENT
        ) {

            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_LEFT_INCREMENT);
                return setToZeroLEFTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_LEFT_INCREMENT);
                return setToZeroLEFTofThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }

    private long howManySquaresAvailableRIGHT(int squareToCheckFrom, long pieceAttackMap, long whitePieces, long blackPieces, PieceColor pieceColor) {
        for (
                int currentToCheckSquare = squareToCheckFrom + Constants.MOVE_RIGHT_INCREMENT;
                currentToCheckSquare % 8 != Constants.BOARD_BOUND_RIGHT /* && currentToCheckSquare >= Constants.BOARD_START*/;
                currentToCheckSquare += Constants.MOVE_RIGHT_INCREMENT
        ) {

            if (checkCollision(whitePieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToWhitePieces(pieceColor, currentToCheckSquare, Constants.MOVE_RIGHT_INCREMENT);
                return setToZeroRIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

            if (checkCollision(blackPieces, currentToCheckSquare)) {
                currentToCheckSquare = whatWeRemovinInWhichDirectionComparedToBlackPieces(pieceColor, currentToCheckSquare, Constants.MOVE_RIGHT_INCREMENT);
                return setToZeroRIGHTofThis(pieceAttackMap, currentToCheckSquare);
            }

        }
        return pieceAttackMap;
    }

    private int whatWeRemovinInWhichDirectionComparedToWhitePieces(PieceColor pieceColor, int currentToCheckSquare, int incrementConstant) {
        return pieceColor == PieceColor.WHITE ? currentToCheckSquare : currentToCheckSquare + incrementConstant;
    }

    private int whatWeRemovinInWhichDirectionComparedToBlackPieces(PieceColor pieceColor, int currentToCheckSquare, int incrementConstant) {
        return pieceColor == PieceColor.WHITE ? currentToCheckSquare + incrementConstant : currentToCheckSquare;
    }


    private long setToZeroAboveThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere <= Constants.BOARD_END;
                setToZeroHere += Constants.MOVE_UP_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long setToZeroBelowThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere >= Constants.BOARD_START;
                setToZeroHere += Constants.MOVE_DOWN_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long setToZeroLEFTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere % 8 != Constants.BOARD_BOUND_LEFT;
                setToZeroHere += Constants.MOVE_LEFT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long setToZeroRIGHTofThis(long pieceAttackMap, int currentToCheckSquare) {
        for (
                int setToZeroHere = currentToCheckSquare;
                setToZeroHere % 8 != Constants.BOARD_BOUND_RIGHT && setToZeroHere >= Constants.BOARD_START;
                setToZeroHere += Constants.MOVE_RIGHT_INCREMENT
        ) {
            pieceAttackMap &= ~(1L << setToZeroHere);
        }
        return pieceAttackMap;
    }

    private long calculateQueenMoveWithBlockers(int square, PieceColor pieceColor) {
        return calculateRookMoveWithBlockers(square, pieceColor) | calculateBishopMoveWithBlockers(square, pieceColor);
    }

    private long calculatePawnMoveWithBlockers(int square, PieceColor pieceColor) {
        long pawnAttackMap = getPawnAttackMapColored(square, pieceColor);
        long pawnMoveMap = getPawnMoveMapColored(square, pieceColor);
        long whitePieces = boardState.getWhiteBitboard();
        long blackPieces = boardState.getBlackBitboard();
        final long pawnMovesForwards = validatePawnMoveForwards(square, pieceColor, whitePieces, blackPieces);
        pawnAttackMap = pieceColor == PieceColor.WHITE ? pawnAttackMap & blackPieces : pawnAttackMap & whitePieces;
        int enPassant = enPassantLegal(square, pieceColor);
        if (enPassant != -1) pawnAttackMap |= (1L << enPassant);
        return pawnMovesForwards | pawnAttackMap;
    }


    private int enPassantLegal(int square, PieceColor pieceColor) {
        int enPassantSquare = boardState.getEnPassantSquare();

        if (pieceColor == PieceColor.WHITE) {
            if (square + Constants.MOVE_UP_RIGHT_INCREMENT == enPassantSquare || square + Constants.MOVE_UP_LEFT_INCREMENT == enPassantSquare) {
                return enPassantSquare;
            }
        } else {
            if (square + Constants.MOVE_DOWN_RIGHT_INCREMENT == enPassantSquare || square + Constants.MOVE_DOWN_LEFT_INCREMENT == enPassantSquare) {
                return enPassantSquare;
            }
        }
        return -1;
    }


    private static long validatePawnMoveForwards(int square, PieceColor pieceColor, long whitePieces, long blackPieces) {
        int row = square / 8;
        long bitboard = whitePieces | blackPieces;
        long oneStep;
        long twoStep;


        if (pieceColor == PieceColor.WHITE) {
            oneStep = (1L << (square + Constants.MOVE_UP_INCREMENT));
            if ((oneStep & bitboard) == 0) {
                if (row == 1) {
                    twoStep = ((1L << (square + Constants.MOVE_UP_INCREMENT * 2)));
                    return (twoStep & bitboard) == 0 ? oneStep | twoStep : oneStep;
                }
                return oneStep;
            }
        } else {
            oneStep = (1L << (square + Constants.MOVE_DOWN_INCREMENT));
            if ((oneStep & bitboard) == 0) {
                if (row == 6) {
                    twoStep = ((1L << (square + Constants.MOVE_DOWN_INCREMENT * 2)));
                    return (twoStep & bitboard) == 0 ? oneStep | twoStep : oneStep;
                }
                return oneStep;
            }
        }
        return 0;
    }

    private long calculateKnightMoveWithBlockers(int square, PieceColor pieceColor) {
        long whitePieces = boardState.getWhiteBitboard();
        long blackPieces = boardState.getBlackBitboard();
        long knightAttackMap = getPieceAttackMap(PieceType.KNIGHT, square);
        long collidedSquares;
        if (pieceColor == PieceColor.WHITE) {
            collidedSquares = knightAttackMap & whitePieces;
            return ~collidedSquares & knightAttackMap;
        }
        collidedSquares = knightAttackMap & blackPieces;
        return ~collidedSquares & knightAttackMap;
    }

    private long calculateKingMoveWithBlockers(int square, PieceColor pieceColor) {
        long white = boardState.getWhiteBitboard();
        long black = boardState.getBlackBitboard();
        long ownPieces = (pieceColor == PieceColor.WHITE) ? white : black;
        long enemyPieces = (pieceColor == PieceColor.WHITE) ? black : white;
//        var pieceMap = boardState.getPieceMap();
//        long oppositeRooks;
//        long oppositeBishops;
//        long oppositeQueens;
//        PieceColor enemyColor = pieceColor.getOpposite();
//        //-------------non-sliding-pieces--------------------
//        long oppositeKnights = getAllMovesForPieceWithBlockersFromBitboard(enemyPieces & pieceMap.get(PieceType.KNIGHT).getBitboard(), PieceType.KNIGHT, enemyColor);
//        long oppositePawns = getAllPawnAttacksColored(enemyColor);
//
        //-------------------------------------------------
        long kingAttackMap = getPieceAttackMap(PieceType.KING, square);
        long collidedSquares;
        long occupied = boardState.getBitboard();
        kingAttackMap &= ~ownPieces;
        long validMoves = 0;
        while (kingAttackMap != 0) {
            int targetSquare = Long.numberOfTrailingZeros(kingAttackMap);
            long targetBit = 1L << targetSquare;
            long newKingBitboard = ((boardState.getPieceMap().get(PieceType.KING).getBitboard() & boardState.getColorBitboard(pieceColor)) & ~(1L << square)) | targetBit;
            long newKingSquare = (occupied & ~(1L << square)) | targetBit;

            // Check if target square is under attack
            if (!isSquareUnderAttack(targetSquare, pieceColor.getOpposite())) {
                validMoves |= targetBit;
            }

            kingAttackMap ^= targetBit;
        }

        return validMoves;
    }

    private boolean isSquareUnderAttack(int square, PieceColor enemyColor) {
        long enemyBitboard = boardState.getColorBitboard(enemyColor);

        //pawns
        long pawnAttacks = getAllPawnAttacksColored(enemyColor);

        if ((pawnAttacks & (1L << square)) != 0) {
//            System.out.println("pawncheck");
            return true;
        }

        // Check knight attacks
        long knightAttacks = getAllMovesForPieceWithBlockersFromBitboard(enemyBitboard & boardState.getPieceMap().get(PieceType.KNIGHT).getBitboard(), PieceType.KNIGHT, enemyColor);

        if ((knightAttacks & (1L << square)) != 0) {
//            System.out.println(BinaryStringToChessPos.bitboardToChessPos(knightAttacks));
//            System.out.println("knightcheck");
            return true;
        }

        long queenAttacks = getAllMovesForPieceWithBlockersFromBitboard(enemyBitboard & boardState.getPieceMap().get(PieceType.QUEEN).getBitboard(), PieceType.QUEEN, enemyColor);
        if ((queenAttacks & (1L << square)) != 0) {
//            System.out.println("queencheck");
            return true;}

        long bishopAttacks = getAllMovesForPieceWithBlockersFromBitboard(enemyBitboard & boardState.getPieceMap().get(PieceType.BISHOP).getBitboard(), PieceType.BISHOP, enemyColor);
        if ((bishopAttacks & (1L << square)) != 0){
//            System.out.println("bishopcheck");
            return true;}

        long rookAttacks = getAllMovesForPieceWithBlockersFromBitboard(enemyBitboard & boardState.getPieceMap().get(PieceType.ROOK).getBitboard(), PieceType.ROOK, enemyColor);
        if ((rookAttacks & (1L << square)) != 0){
//            System.out.println("bishopcheck");
            return true;}

        long oppositeKing = boardState.getPieceMap().get(PieceType.KING).getBitboard() & enemyBitboard;
        final long oppositeKingSquares = boardState.getPieceMap().get(PieceType.KING).getAttackMap()[Long.numberOfTrailingZeros(oppositeKing)];
        if ((oppositeKingSquares & (1L << square)) != 0) {
//            System.out.println("kingkingcheck");
            return true;}

        return false;
    }

    private long getAllMovesForPieceWithBlockersFromBitboard(long pieceBitboard, PieceType pieceType, PieceColor pieceColor) {
        int square;
        long allMoves = 0;
        while (pieceBitboard != 0) {
            square = Long.numberOfTrailingZeros(pieceBitboard);
            allMoves |= calculateMoveWithBlockers(square, pieceType, pieceColor);
            pieceBitboard &= ~(1L << square);
        }
        return allMoves;
    }


    public static void main(String[] args) {
        BoardBuilder boardBuilder = new BoardBuilder();
        boardBuilder.buildStandardBoard().addPiece(PieceType.QUEEN, 30, PieceColor.BLACK);
        System.out.println(BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(boardBuilder.getBitboard())));
        BoardState boardState = new BoardState(boardBuilder);
        ApplyBlockers_v2 applyBlockers = new ApplyBlockers_v2(boardState);
        int square = 57;
        boardState.makeMove(49, 41, PieceType.PAWN, PieceColor.BLACK);
        long iWORK = applyBlockers.calculateMoveWithBlockers(square, PieceType.KNIGHT, PieceColor.BLACK);
        System.out.println(BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(iWORK)));
//        long iWORK = applyBlockers.calculateMoveWithBlockers(square, PieceType.BISHOP, PieceColor.WHITE);
//        System.out.println(BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(iWORK)));

//        long iWORK1 = applyBlockers.calculateMoveWithBlockers(square, PieceType.ROOK, PieceColor.WHITE);
//        System.out.println(BinaryStringToChessPos.binaryStringToChessPos(Long.toBinaryString(iWORK1)));

//        long iWORK2 = applyBlockers.calculateMoveWithBlockers(square, PieceType.QUEEN, PieceColor.WHITE);
//        System.out.println(BinaryStringToChessPos.bitboardToChessPos(iWORK2));

//        long iWORK3 = applyBlockers.calculateMoveWithBlockers(square, PieceType.KNIGHT, PieceColor.BLACK);
//        System.out.println(BinaryStringToChessPos.bitboardToChessPos(iWORK3));

//        long iWORK4 = applyBlockers.calculateMoveWithBlockers(square, PieceType.PAWN, PieceColor.WHITE);
//        System.out.println(BinaryStringToChessPos.bitboardToChessPos(iWORK4));

//        System.out.println(BinaryStringToChessPos.bitboardToChessPos(applyBlockers.getPawnAttackMapColored(square, PieceColor.BLACK)));

//        long iWORK5 = applyBlockers.calculateMoveWithBlockers(square, PieceType.KING, PieceColor.WHITE);
//        System.out.println(BinaryStringToChessPos.bitboardToChessPos(iWORK5));
    }
}


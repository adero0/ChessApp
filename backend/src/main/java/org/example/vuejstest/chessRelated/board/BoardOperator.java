package org.example.vuejstest.chessRelated.board;

import java.util.HashMap;
import java.util.Map;

import org.example.vuejstest.chessRelated.board.pieces.Pawn;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;

import static org.example.vuejstest.chessRelated.enums.PieceType.*;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.example.vuejstest.chessRelated.util.BitboardsForCastlingUtil;

public class BoardOperator {
    BoardState boardState;
    ApplyBlockers_v2 blockersApplier;



    public BoardOperator(BoardState boardState) {
        this.boardState = boardState;
        blockersApplier = new ApplyBlockers_v2(boardState);
    }

    public static BoardOperator standardBoardStartingPositionOperator() {
        return new BoardOperator(new BoardState(new BoardBuilder().buildStandardBoard()));
    }


    public BoardState getBoardState() {
        return boardState;
    }

    public Map<Integer, IntArrayList> getLegalMoves() {
        Map<Integer, IntArrayList> legalMoves = new HashMap<>(20);
        PieceColor currentPlayerColor = boardState.getCurrentPlayerTurn();
        long currentPlayerColorBitboard = boardState.getColorBitboard(currentPlayerColor);
        //normal pieces
        for (PieceType type : PieceType.values()) {
            if (type == PAWN) {
                continue;
            }
            addLegalMovesToMap(currentPlayerColorBitboard & boardState.getPieceMap().get(type).getBitboard(), currentPlayerColor, type, legalMoves);
        }
        //pawns (bcs it's always the pawns messing it up!!!)
        addLegalMovesToMap(((Pawn) boardState.getPieceMap().get(PieceType.PAWN)).getColorBitboard(currentPlayerColor), currentPlayerColor, PAWN, legalMoves);

        //castling
        int castlingRights = getCastlingRights();
        if (castlingRights > 0) {
            int key = currentPlayerColor == PieceColor.WHITE ? 3 : 59;
            int shortCastle = currentPlayerColor == PieceColor.WHITE ? 1 : 57;
            int longCastle = currentPlayerColor == PieceColor.WHITE ? 5 : 61;
            legalMoves.computeIfPresent(key, (k, v) -> {
                if ((castlingRights & 1) != 0) { // Check short castle bit
                    v.add(shortCastle);
                }
                if ((castlingRights & 2) != 0) { // Check long castle bit
                    v.add(longCastle);
                }
                return v;
            });
        }

        //mate
        ifNoLegalsThenCheckmate(legalMoves);

        return legalMoves;
    }


    private void addLegalMovesToMap(long pieceBitboard, PieceColor currentPlayerColor, PieceType pieceType, Map<Integer, IntArrayList> legalMovesMap) {
        while (pieceBitboard != 0) {
            int checkedSquare = Long.numberOfTrailingZeros(pieceBitboard);
            long possibleMoves = blockersApplier.calculateMoveWithBlockers(checkedSquare, pieceType, currentPlayerColor);
            int bits = Long.bitCount(possibleMoves);
            if (bits == 0) {
                pieceBitboard ^= Long.lowestOneBit(pieceBitboard);
                continue;
            }
            IntArrayList legalMoves = new IntArrayList(bits);
            while (possibleMoves != 0) {
                int oneOfThePossibleMoves = Long.numberOfTrailingZeros(possibleMoves);

                var copyBoardState = new BoardState(boardState);
                copyBoardState.makeMove(checkedSquare, oneOfThePossibleMoves, pieceType, currentPlayerColor);
                if (fromBoardStateCheckIsAlliedKingInCheck(copyBoardState)) {
                    possibleMoves ^= Long.lowestOneBit(possibleMoves);
                    continue;
                }
                legalMoves.add(oneOfThePossibleMoves);
                possibleMoves ^= Long.lowestOneBit(possibleMoves);
            }
            pieceBitboard ^= Long.lowestOneBit(pieceBitboard);
            legalMovesMap.put(checkedSquare, legalMoves);
        }
    }

    private void ifNoLegalsThenCheckmate(Map<Integer, IntArrayList> legalMoves) {
        for (var lgl : legalMoves.values()) {
            if(!lgl.isEmpty()) {
                return;
            }
        }
        boardState.setCheckmate(true);
    }

    private boolean fromBoardStateCheckIsAlliedKingInCheck(BoardState copiedBoardState) {
        ApplyBlockers_v2 copyBlockers = new ApplyBlockers_v2(copiedBoardState);
        var alliedKingSquare = Long.numberOfTrailingZeros(copiedBoardState.getColorBitboard(copiedBoardState.getCurrentPlayerTurn()) & copiedBoardState.getPieceMap().get(KING).getBitboard());
        for (PieceType type : PieceType.values()) {
            if (type == PAWN || type == KING) {
                continue;
            }
            if (copiedBoardState.isEnemyKingChecked(copiedBoardState.getColorBitboard(copiedBoardState.getCurrentPlayerTurn().getOpposite()) & copiedBoardState.getPieceMap().get(type).getBitboard(), copiedBoardState.getCurrentPlayerTurn().getOpposite(), type, copyBlockers, alliedKingSquare)) {
                return true;
            }
        }
        if (copiedBoardState.isEnemyKingChecked(((Pawn)  copiedBoardState.getPieceMap().get(PieceType.PAWN)).getColorBitboard(copiedBoardState.getCurrentPlayerTurn().getOpposite()), copiedBoardState.getCurrentPlayerTurn().getOpposite(), PAWN, copyBlockers, alliedKingSquare)) {
            return true;
        }
        return false;
    }

    public int getCastlingRights() {
        int rights = 0;
        PieceColor currentPlayerColor = boardState.getCurrentPlayerTurn();
        if (!boardState.hasKingMoved(currentPlayerColor)) {
            if (BitboardsForCastlingUtil.castleLegal(boardState.getColorBitboard(currentPlayerColor), currentPlayerColor, CastlingType.LONGCASTLE)) {
                rights += 2;
            }
            if (BitboardsForCastlingUtil.castleLegal(boardState.getColorBitboard(currentPlayerColor), currentPlayerColor, CastlingType.SHORTCASTLE)) {
                rights++;
            }
        }
        return rights;
    }

    public static void main(String[] args) {

    }
}

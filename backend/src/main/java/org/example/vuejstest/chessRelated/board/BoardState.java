package org.example.vuejstest.chessRelated.board;

import org.example.vuejstest.chessRelated.board.pieces.Pawn;
import org.example.vuejstest.chessRelated.board.pieces.Piece;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;

import java.util.Map;

import static org.example.vuejstest.chessRelated.enums.PieceType.KING;
import static org.example.vuejstest.chessRelated.enums.PieceType.PAWN;

public class BoardState {
    private final Pieces pieces;
    private long whiteBitboard;
    private long blackBitboard;
    private long bitboard;
    private PieceColor currentPlayColor = PieceColor.WHITE;
    private boolean isWhiteCastleLongLegal = true;
    private boolean isBlackCastleLongLegal = true;
    private boolean isWhiteCastleShortLegal = true;
    private boolean isBlackCastleShortLegal = true;
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private int enPassantSquare = -1;
    private int material = 0;
    boolean whiteKingInCheck = false;
    boolean blackKingInCheck = false;
    private final Map<PieceType, Piece> pieceMap;
    private boolean isCheckmate = false;


    public boolean isKingInCheck() {
        return currentPlayColor == PieceColor.WHITE ? whiteKingInCheck : blackKingInCheck;
    }

    public boolean isEnemyKingInCheck() {
        return currentPlayColor == PieceColor.WHITE ? blackKingInCheck : whiteKingInCheck;
    }

    public boolean hasKingMoved(PieceColor pieceColor) {
        return pieceColor == PieceColor.WHITE ? whiteKingMoved : blackKingMoved;
    }

    public void setKingMoved(PieceColor pieceColor) {
        if (pieceColor == PieceColor.WHITE) {
            whiteKingMoved = true;
        } else {
            blackKingMoved = true;
        }
    }


    public boolean whiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean blackKingMoved() {
        return blackKingMoved;
    }

    public long getBitboard() {
        return bitboard;
    }

    public void setBitboard(long bitboard) {
        this.bitboard = bitboard;
    }

    public BoardState(BoardBuilder boardBuilder) {
        this.pieces = boardBuilder.getPieces();
        this.pieceMap = pieces.piecesMap;
        this.bitboard = boardBuilder.getBitboard();
        this.whiteBitboard = boardBuilder.getWhiteBitboard();
        this.blackBitboard = boardBuilder.getBlackBitboard();
    }

    //COPY CONSTRUCTOR
    public BoardState(BoardState other) {
        this.bitboard = other.bitboard;
        this.pieces = new Pieces(other.pieces);
        this.pieceMap = this.pieces.piecesMap;
        this.whiteBitboard = other.whiteBitboard;
        this.blackBitboard = other.blackBitboard;
        this.currentPlayColor = other.currentPlayColor;
        this.isWhiteCastleLongLegal = other.isWhiteCastleLongLegal;
        this.isBlackCastleLongLegal = other.isBlackCastleLongLegal;
        this.isWhiteCastleShortLegal = other.isWhiteCastleShortLegal;
        this.isBlackCastleShortLegal = other.isBlackCastleShortLegal;
        this.enPassantSquare = other.enPassantSquare;
        this.material = other.material;
        this.whiteKingMoved = other.whiteKingMoved;
        this.blackKingMoved = other.blackKingMoved;
        this.whiteKingInCheck = other.whiteKingInCheck;
        this.blackKingInCheck = other.blackKingInCheck;
    }


    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public Map<PieceType, Piece> getPieceMap() {
        return pieceMap;
    }

    public long getWhiteBitboard() {
        return whiteBitboard;
    }

    public long getBlackBitboard() {
        return blackBitboard;
    }

    public long getColorBitboard(PieceColor color) {
        return color == PieceColor.WHITE ? whiteBitboard : blackBitboard;
    }

    public Pieces getPieces() {
        return pieces;
    }

    public int getMaterial() {
        updateMaterial();
        return material;
    }

    public void updateMaterial() {
        material = 0;
        // kings
        long kings = pieceMap.get(PieceType.KING).getBitboard();
        material += valueOfPiece(kings & whiteBitboard, 100);
        material += valueOfPiece(kings & blackBitboard, -100);
        // queens
        long queens = pieceMap.get(PieceType.QUEEN).getBitboard();
        material += valueOfPiece(queens & whiteBitboard, 9);
        material += valueOfPiece(queens & blackBitboard, -9);
        // rooks
        long rooks = pieceMap.get(PieceType.ROOK).getBitboard();
        material += valueOfPiece(rooks & whiteBitboard, 5);
        material += valueOfPiece(rooks & blackBitboard, -5);
        // knights
        long knights = pieceMap.get(PieceType.KNIGHT).getBitboard();
        material += valueOfPiece(knights & whiteBitboard, 3);
        material += valueOfPiece(knights & blackBitboard, -3);
        // bishops
        long bishops = pieceMap.get(PieceType.BISHOP).getBitboard();
        material += valueOfPiece(bishops & whiteBitboard, 3);
        material += valueOfPiece(bishops & blackBitboard, -3);
        //pawns
        Pawn pawn = (Pawn) pieceMap.get(PieceType.PAWN);
        material += valueOfPiece(pawn.getWhite(), 1);
        material += valueOfPiece(pawn.getBlack(), -1);
    }

    private void setKingInCheckColored(PieceColor pieceColor) {
        if (pieceColor == PieceColor.WHITE) {
            whiteKingInCheck = true;
        } else {
            blackKingInCheck = true;
        }
    }

    private void setKingOutOfCheckColored(PieceColor pieceColor) {
        if (pieceColor == PieceColor.WHITE) {
            whiteKingInCheck = false;
        } else {
            blackKingInCheck = false;
        }
    }

    public static int valueOfPiece(long pieceBitboard, int multiplier) {
        return Long.bitCount(pieceBitboard) * multiplier;
    }

    public void changeCurrentPlayerTurn() {
        currentPlayColor = currentPlayColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
    }

    public PieceColor getCurrentPlayerTurn() {
        return currentPlayColor;
    }

    public PieceColor getAndChangeCurrentPlayerTurn() {
        currentPlayColor = currentPlayColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
        return currentPlayColor.getOpposite();
    }

    public boolean isWhiteCastleLongLegal() {
        return isWhiteCastleLongLegal;
    }

    public boolean isBlackCastleLongLegal() {
        return isBlackCastleLongLegal;
    }

    public boolean isWhiteCastleShortLegal() {
        return isWhiteCastleShortLegal;
    }

    public boolean isBlackCastleShortLegal() {
        return isBlackCastleShortLegal;
    }

    public void setColorBitboard(PieceColor color, long newBitboard) {
        if (color == PieceColor.WHITE) {
            whiteBitboard = newBitboard;
        } else {
            blackBitboard = newBitboard;
        }
        bitboard = whiteBitboard | blackBitboard;
    }

    public void setPieceBitboard(Piece piece, long newBitboard, PieceColor color) {
        if (piece instanceof Pawn) {
            ((Pawn) piece).setBitboard(newBitboard, color);
        } else {
            piece.setBitboard(newBitboard);
        }
    }

    public void castle(CastlingType castlingType, PieceColor pieceColor) {
        int offset = pieceColor == PieceColor.WHITE ? 0 : 56;
        int rookOffset = pieceColor == PieceColor.WHITE ? 0 : 7;
        int kingPlacement = 3 + offset; //king position + offset
        int rookPlacement = offset + rookOffset;

        int newKingPlacement = (castlingType == CastlingType.SHORTCASTLE ? 1 : 5) + offset;
        int newRookPlacement = (castlingType == CastlingType.SHORTCASTLE ? 2 : 4) + offset;

        long rookDelete = ~(1L << rookPlacement);
        long rookAdd = 1L << newRookPlacement;
        long kingDelete = ~(1L << kingPlacement);
        long kingAdd = 1L << newKingPlacement;

        Piece rook = pieceMap.get(PieceType.ROOK);
        rook.setBitboard((rook.getBitboard() & rookDelete) | rookAdd);
        Piece king = pieceMap.get(PieceType.KING);
        king.setBitboard((king.getBitboard() & kingDelete) | kingAdd);

        setColorBitboard(
                pieceColor,
                (getColorBitboard(pieceColor) & rookDelete & kingDelete) | kingAdd | rookAdd
        );
    }

    public void makeMove(int from, int to, PieceType pieceType, PieceColor pieceColor) {
        long colorBitboard = getColorBitboard(pieceColor);
        Piece piece = pieceMap.get(pieceType);
        long pieceBitboard;
        PieceColor opponentColor = pieceColor.getOpposite();
        PieceType capturedPieceType = null;
        if (pieceType == PieceType.KING) {
            setKingMoved(pieceColor);
        }
        if (pieceType == PieceType.PAWN) {
            pieceBitboard = ((Pawn) piece).getColorBitboard(pieceColor);
            if (to == enPassantSquare) {
                long thing = ~(1L << to + (opponentColor == PieceColor.BLACK ? -8 : +8)); // TODO co to jest???
                long opponentBitboardAfterEnPassant = ((Pawn) piece).getColorBitboard(opponentColor) & ~(1L << to - 8);
                setColorBitboard(opponentColor, getColorBitboard(opponentColor) & ~(1L << to - 8));
                setPieceBitboard(piece, opponentBitboardAfterEnPassant, opponentColor);
            }
            enPassantSquare = -1;
            if (Math.abs(from - to) == 16) {
                enPassantSquare = to + ((from - to) / 2);
            }
            int promotionRank = pieceColor == PieceColor.WHITE ? 7 : 0;
            if (to / 8 == promotionRank) {
                //perhaps move this somewhere else, or add return; here
                promoteToQueen((Pawn) piece, to, pieceColor);
            }
        } else {
            pieceBitboard = piece.getBitboard();
            enPassantSquare = -1;
        }


        if ((getColorBitboard(opponentColor) & (1L << to)) != 0) {
            for (PieceType pt : PieceType.values()) {
                long opponentBitboard;
                if (pt == PieceType.PAWN) {
                    opponentBitboard = ((Pawn) pieceMap.get(pt)).getColorBitboard(opponentColor);
                } else {
                    opponentBitboard = pieceMap.get(pt).getBitboard() & getColorBitboard(opponentColor);
                }
                if ((opponentBitboard & (1L << to)) != 0) {
                    capturedPieceType = pt;
                    break;
                }
            }
            Piece capturedPiece = pieceMap.get(capturedPieceType);
            long capturedBitboard;
            if (capturedPieceType == PieceType.PAWN) {
                capturedBitboard = ((Pawn) pieceMap.get(PieceType.PAWN)).getColorBitboard(opponentColor);
            } else {
                capturedBitboard = capturedPiece.getBitboard();
            }
            capturedBitboard &= ~(1L << to);
            long colorOfCapturedBitboard = getColorBitboard(opponentColor) & ~(1L << to);
            setColorBitboard(opponentColor, colorOfCapturedBitboard);
            setPieceBitboard(capturedPiece, capturedBitboard, opponentColor);
        }

        pieceBitboard &= ~(1L << from);
        pieceBitboard |= 1L << to;

        colorBitboard &= ~(1L << from);
        colorBitboard |= 1L << to;


        setColorBitboard(pieceColor, colorBitboard);
        setPieceBitboard(piece, pieceBitboard, pieceColor); // this might brake for promotions (mayb not)

        // here must check for enemy checks and set a valid flag
        var copyBoardState = new BoardState(this);
        int enemyKingSquare = Long.numberOfTrailingZeros(getColorBitboard(opponentColor) & pieceMap.get(PieceType.KING).getBitboard());
        var copyBoardOperator = new BoardOperator(copyBoardState);
        setKingOutOfCheckColored(pieceColor.getOpposite());
        for (PieceType type : PieceType.values()) {
            if (type == PAWN || type == KING) {
                continue;
            }
            if(isEnemyKingChecked(getColorBitboard(pieceColor) & pieceMap.get(type).getBitboard(), pieceColor, type, copyBoardOperator.blockersApplier, enemyKingSquare)){break;}
        }
        //pawns (bcs it's always the pawns messing it up!!!)
        isEnemyKingChecked(((Pawn) pieceMap.get(PieceType.PAWN)).getColorBitboard(pieceColor), pieceColor, PAWN, copyBoardOperator.blockersApplier, enemyKingSquare);

    }

    //checking if the enemy is in check and setting an according flag
    public boolean isEnemyKingChecked(long pieceBitboard, PieceColor currentPlayerColor, PieceType pieceType, ApplyBlockers_v2 applyBlockers, int enemyKingSquare) {
        while (pieceBitboard != 0) {
            int checkedSquare = Long.numberOfTrailingZeros(pieceBitboard);

            long possibleMoves = applyBlockers.calculateMoveWithBlockers(checkedSquare, pieceType, currentPlayerColor);
            int bits = Long.bitCount(possibleMoves);
            if (bits == 0) {
                pieceBitboard ^= Long.lowestOneBit(pieceBitboard);
                continue;
            }
            if ((possibleMoves & (1L << enemyKingSquare)) != 0){
                setKingInCheckColored(currentPlayerColor.getOpposite());
//                System.out.println("enemy king is in check now apparently on square" + enemyKingSquare);
                return true;
            }
            pieceBitboard ^= Long.lowestOneBit(pieceBitboard);
        }
        return false;
    }

    private void promoteToQueen(Pawn pawn, int squareToPromoteAt, PieceColor pieceColor) {
        long newPawnBitboard = pawn.getColorBitboard(pieceColor) & ~(1L << squareToPromoteAt);
        setPieceBitboard(pawn, newPawnBitboard, pieceColor);
        var pm = pieceMap.get(PieceType.QUEEN);
        long newQueenBitboard = pm.getBitboard() | 1L << squareToPromoteAt;
        setPieceBitboard(pm, newQueenBitboard, pieceColor);
    }

    public static void main(String[] args) {
        BoardOperator gm = BoardOperator.standardBoardStartingPositionOperator();
        var bs = gm.getBoardState();
        System.out.println(gm.getLegalMoves());
        bs.makeMove(11, 27, PAWN, PieceColor.WHITE);
        System.out.println(gm.getLegalMoves());
    }


    public boolean isCheckmate() {
        return isCheckmate;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }
}

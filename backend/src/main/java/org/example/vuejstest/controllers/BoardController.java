package org.example.vuejstest.controllers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceType;
import org.example.vuejstest.chessRelated.util.BinaryStringToChessPos;
import org.example.vuejstest.chessRelated.util.ChessPositionIntoFENFormat;
import org.example.vuejstest.models.MoveMade;
import org.example.vuejstest.models.MoveResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test") //do zmiany
public class BoardController {
    BoardOperator gameManager;

    BoardController(){
        gameManager = BoardOperator.standardBoardStartingPositionOperator();
    }

    //this happens once at the start of the game
    @GetMapping("/getLegals")
    public ResponseEntity<Map<Integer, IntArrayList>> getLegalMoves(){
        System.out.println(gameManager.getLegalMoves());
        return ResponseEntity.ok(gameManager.getLegalMoves());
    }


    @PostMapping
    public ResponseEntity<MoveResponse> makeMoveAndGetNewOnes(@RequestBody MoveMade moveRequest) {
        var boardState = gameManager.getBoardState();
        int enemyKingCheckSquare = -1;
        if (moveRequest.getCastlingType() != CastlingType.NOCASTLE){
            boardState.castle(moveRequest.getCastlingType(), boardState.getAndChangeCurrentPlayerTurn());
        } else {
            gameManager.getBoardState().makeMove
                    (
                            moveRequest.getFrom(),
                            moveRequest.getTo(),
                            PieceType.getPieceTypeFromSingleLetterString(moveRequest.getPiece()),
                            boardState.getAndChangeCurrentPlayerTurn()
                    );
        }
        var legalMoves = gameManager.getLegalMoves();
        System.out.println(legalMoves);
        if(boardState.isKingInCheck()){
            enemyKingCheckSquare = Long.numberOfTrailingZeros(
                    boardState.getColorBitboard(boardState.getCurrentPlayerTurn()) & boardState.getPieceMap().get(PieceType.KING).getBitboard()
            );
        }
        return ResponseEntity.ok(
                new MoveResponse(
                        legalMoves,
                        gameManager.getCastlingRights(),
                        boardState.getMaterial(),
                        boardState.getEnPassantSquare(),
                        enemyKingCheckSquare,
                        boardState.isCheckmate()
                ));
    }

    //this happens once at the start of the game
    @GetMapping
    public ResponseEntity<String> setupBoard() {
        System.out.println(ChessPositionIntoFENFormat.transformIntoFEN(gameManager.getBoardState()));
        return ResponseEntity.ok(ChessPositionIntoFENFormat.transformIntoFEN(gameManager.getBoardState()));
    }
}

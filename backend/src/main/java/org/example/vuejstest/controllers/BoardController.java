//package org.example.vuejstest.controllers;
//
//import it.unimi.dsi.fastutil.ints.IntArrayList;
//import org.example.vuejstest.chessRelated.board.BoardOperator;
//import org.example.vuejstest.chessRelated.board.BoardState;
//import org.example.vuejstest.chessRelated.enums.CastlingType;
//import org.example.vuejstest.chessRelated.enums.PieceType;
//import org.example.vuejstest.chessRelated.util.BinaryStringToChessPos;
//import org.example.vuejstest.chessRelated.util.ChessPositionIntoFENFormat;
//import org.example.vuejstest.models.GameSession;
//import org.example.vuejstest.models.MoveMade;
//import org.example.vuejstest.models.MoveResponse;
//import org.example.vuejstest.models.User;
//import org.example.vuejstest.services.MatchmakingService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/game") //do zmiany
//public class BoardController {
//    private final MatchmakingService matchmakingService;
//
//    public BoardController(MatchmakingService matchmakingService) {
//        this.matchmakingService = matchmakingService;
//    }
//
//    @PostMapping("/{gameId}/move")
//    public ResponseEntity<MoveResponse> makeMove(
//            @PathVariable String gameId,
//            @RequestBody MoveMade moveRequest,
//            @AuthenticationPrincipal User user) {
//
//
//
//    //this happens once at the start of the game
//    @GetMapping("/getLegals")
//    public ResponseEntity<Map<Integer, IntArrayList>> getLegalMoves(){
//        System.out.println(gameManager.getLegalMoves());
//        return ResponseEntity.ok(gameManager.getLegalMoves());
//    }
//
//    //this happens once at the start of the game
//    @GetMapping
//    public ResponseEntity<String> setupBoard() {
//        var stuff = BoardOperator.standardBoardStartingPositionOperator().getBoardState();
//        System.out.println(ChessPositionIntoFENFormat.transformIntoFEN(BoardOperator.standardBoardStartingPositionOperator().getBoardState()));
//        return ResponseEntity.ok(ChessPositionIntoFENFormat.transformIntoFEN(stuff));
//    }
//}

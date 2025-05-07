package org.example.vuejstest.services;

import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.chessRelated.board.BoardState;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceType;
import org.example.vuejstest.models.GameSession;
import org.example.vuejstest.models.MoveMade;
import org.example.vuejstest.models.MoveResponse;
import org.example.vuejstest.models.enums.GameStatus;
import org.example.vuejstest.repository.UserRepository;
import org.example.vuejstest.security.GameIdGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MatchmakingService {
    private final Queue<Long> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, GameSession> activeGames = new ConcurrentHashMap<>();
    private final Map<Long, String> playerToGameMap = new ConcurrentHashMap<>();
    private static final int MAX_ID_GEN_ATTEMPTS = 5;
    private final UserRepository userRepository;

    public MatchmakingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private String generateUniqueGameId() {
        int attempts = 0;
        while (attempts++ < MAX_ID_GEN_ATTEMPTS) {
            String candidate = GameIdGenerator.generateReadableId();
            if (!activeGames.containsKey(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate unique game ID");
    }

    public GameSession getGameSession(String gameId) {
        return activeGames.get(gameId);
    }

    public synchronized Map<String, Object> joinQueue(Long userId) {

        if (playerToGameMap.containsKey(userId)) {
            return Map.of("status", "paired", "gameId", playerToGameMap.get(userId));
        }
        if (!waitingQueue.contains(userId)) {
            waitingQueue.add(userId);
        }
        return attemptPairing(userId);
    }

    public Map<String, Object> attemptPairing(Long userId) {
        if (!waitingQueue.contains(userId)) {
            System.out.println(userRepository.findById(userId).get().getUsername() + " is trying anyway");
            System.out.println("This should not happen bro!");
        }

        if (waitingQueue.size() >= 2) {

            Long player1 = waitingQueue.poll();
            Long player2 = userId;

            if (player1.equals(player2)) {
                waitingQueue.add(player1);
                return Map.of("status", "waiting");
            }

            GameSession game = createNewGame(player1, player2);
            activeGames.put(game.getGameId(), game);
            playerToGameMap.put(player1, game.getGameId());
            playerToGameMap.put(player2, game.getGameId());
            return Map.of(
                    "status", "paired",
                    "gameId", game.getGameId(),
                    "color", game.getWhitePlayerId().equals(userId) ? "white" : "black"
            );
        }
        return Map.of("status", "waiting");
    }


    private GameSession createNewGame(Long player1, Long player2) {
        return new GameSession.Builder()
                .gameId(generateUniqueGameId())
                .whitePlayerId(player1)
                .blackPlayerId(player2)
                .boardOperator(BoardOperator.standardBoardStartingPositionOperator())
                .status(GameStatus.ONGOING)
                .build();
    }

    public synchronized void leaveQueue(Long userId) {
        waitingQueue.remove(userId);
    }

    public MoveResponse processMove(String gameId, Long userId, MoveMade moveRequest) {
        GameSession game = getGameSession(gameId);
        if (!game.isPlayersTurn(userId)) { throw new RuntimeException("This is not this players' turn");}

        var gameManager = game.getBoardOperator();
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
        return new MoveResponse(
                        legalMoves,
                        gameManager.getCastlingRights(),
                        boardState.getMaterial(),
                        boardState.getEnPassantSquare(),
                        enemyKingCheckSquare,
                        boardState.isCheckmate()
                );
    }
    public Map<String, Object> getQueueStatusForUser(Long userId) {
        if (playerToGameMap.containsKey(userId)) {
            return Map.of(
                    "status", "paired",
                    "gameId", playerToGameMap.get(userId),
                    "color", getGameSession(playerToGameMap.get(userId))
                            .getPlayerColorFromId(userId).toString().toLowerCase()
            );
        }
        return attemptPairing(userId);
    }

    public int getWaitingQueueLength(){
        return waitingQueue.size();
    }

    public Map<String, Object> getGameStatus(String gameId, Long userId) {
        var game = activeGames.get(gameId);
        BoardOperator board = game.getBoardOperator();
        BoardState boardState = board.getBoardState();
        int checkSquare = -1;
        if(boardState.isKingInCheck()){
            checkSquare = Long.numberOfTrailingZeros(
                    boardState.getColorBitboard(boardState.getCurrentPlayerTurn()) & boardState.getPieceMap().get(PieceType.KING).getBitboard()
            );
        }

        return Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("status", game.getStatus()),
                Map.entry("currentTurn", boardState.getCurrentPlayerTurn()),
                Map.entry("playerColor", game.getPlayerColorFromId(userId)),
                Map.entry("opponentUsername", userRepository.findById(game.getOpponentColorFromId(userId)).orElseThrow(() -> new RuntimeException("user not found somehow wtf"))),
                Map.entry("isWhiteInCheck", boardState.whiteKingInCheck),
                Map.entry("isBlackInCheck", boardState.blackKingInCheck),
                Map.entry("checkSquare", checkSquare),
                Map.entry("isCheckmate", boardState.isCheckmate()),
                Map.entry("legalMoves", game.isPlayersTurn(userId) ?
                        board.getLegalMoves() :
                        Collections.emptyMap()),
                Map.entry("materialCount", boardState.getMaterial())
        );
    }

    public Map<String, Object> resignGame(String gameId, Long userId) {
        GameSession game = getGameSession(gameId);
        game.getPlayerColorFromId(userId);
        game.setStatus(GameStatus.getProperCheckmate(game.getPlayerColorFromId(userId).getOpposite()));
        return getGameStatus(gameId, userId);
    }
}
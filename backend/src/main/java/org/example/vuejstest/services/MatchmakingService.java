package org.example.vuejstest.services;

import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.models.GameSession;
import org.example.vuejstest.models.Message;
import org.example.vuejstest.models.enums.GameStatus;
import org.example.vuejstest.security.GameIdGenerator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MatchmakingService {
    private final Queue<Long> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Map<String, GameSession> activeGames = new ConcurrentHashMap<>();
    private final Map<Long, String> playerToGameMap = new ConcurrentHashMap<>();
    private static final int MAX_ID_GEN_ATTEMPTS = 5;

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
    public synchronized void leaveQueue(Long userId) {
        waitingQueue.remove(userId);
    }

    public synchronized Map<String, Object> joinQueue(Long userId) {
        if (waitingQueue.isEmpty()) {
            waitingQueue.add(userId);
            return Map.of("status", "waiting");
        }

        Long player1 = waitingQueue.poll();
        Long player2 = userId;

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

    private GameSession createNewGame(Long player1, Long player2) {
        return new GameSession.Builder()
                .gameId(generateUniqueGameId())
                .whitePlayerId(player1)
                .blackPlayerId(player2)
                .boardOperator(BoardOperator.standardBoardStartingPositionOperator())
                .isWhiteTurn(true)
                .status(GameStatus.ONGOING)
                .build();
    }

    public synchronized void leaveQueue(Long userId) {
        waitingQueue.remove(userId);
        String gameId = playerToGameMap.get(userId);
        if (gameId != null) {
            GameSession game = activeGames.get(gameId);
            if (game != null) {
                game.setStatus(GameStatus.ABANDONED);
                activeGames.remove(gameId);
            }
            playerToGameMap.remove(userId);
            Long opponentId = game.getWhitePlayerId().equals(userId) ?
                    game.getBlackPlayerId() : game.getWhitePlayerId();
            playerToGameMap.remove(opponentId);
        }
    }
}
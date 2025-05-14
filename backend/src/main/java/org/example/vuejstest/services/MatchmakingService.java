package org.example.vuejstest.services;

import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.chessRelated.board.BoardState;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.chessRelated.enums.PieceType;
import org.example.vuejstest.chessRelated.util.BinaryStringToChessPos;
import org.example.vuejstest.chessRelated.util.ChessPositionIntoFENFormat;
import org.example.vuejstest.chessRelated.util.NotationToBitboardConverter;
import org.example.vuejstest.models.GameSession;
import org.example.vuejstest.models.MoveMade;
import org.example.vuejstest.models.MoveResponse;
import org.example.vuejstest.models.enums.GameStatus;
import org.example.vuejstest.repository.UserRepository;
import org.example.vuejstest.security.GameIdGenerator;
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
            System.out.println(waitingQueue);
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
            Long player2 = waitingQueue.poll();
            if (!player2.equals(userId)) {
                throw new RuntimeException("smth wrong bruv");
            }

            if (player1.equals(player2)) { //todo tf is this?
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
                .lastMoveMade()
                .set0TurnCounter()
                .gameNotation()
                .build();
    }

    private GameSession createNewBotGame(Long playerId, PieceColor playerColor) {
        long botId = -1;
        Long whitePlayer = playerColor == PieceColor.WHITE ? playerId : botId;
        Long blackPlayer = playerColor == PieceColor.BLACK ? playerId : botId;
        return new GameSession.Builder()
                .gameId(generateUniqueGameId())
                .whitePlayerId(whitePlayer)
                .blackPlayerId(blackPlayer)
                .boardOperator(BoardOperator.standardBoardStartingPositionOperator())
                .status(GameStatus.ONGOING)
                .lastMoveMade()
                .set0TurnCounter()
                .gameNotation()
                .build();
    }

    public synchronized void leaveQueue(Long userId) {
        waitingQueue.remove(userId);
    }

    public Map<String, Object> processMove(String gameId, Long userId, MoveMade moveRequest) {
        GameSession game = getGameSession(gameId);
        if (!game.isPlayersTurn(userId)) {
            throw new RuntimeException("This is not this players' turn");
        }

        var gameManager = game.getBoardOperator();
        var boardState = gameManager.getBoardState();

        int enemyKingCheckSquare = -1;

//        System.out.println("Pre-move queens are \n" + BinaryStringToChessPos.bitboardToChessPos(boardState.getPieceMap().get(PieceType.QUEEN).getBitboard()));
//        System.out.println("Pre-move whites are \n" + BinaryStringToChessPos.bitboardToChessPos(boardState.getColorBitboard(PieceColor.WHITE)));

        if (moveRequest.getCastlingType() != CastlingType.NOCASTLE) {
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
        if (boardState.isKingInCheck()) {
            enemyKingCheckSquare = Long.numberOfTrailingZeros(
                    boardState.getColorBitboard(boardState.getCurrentPlayerTurn()) & boardState.getPieceMap().get(PieceType.KING).getBitboard()
            );
        }
        game.setLastMoveMade(moveRequest);
        var info = Map.ofEntries(
                Map.entry("legalMoves", legalMoves),
                Map.entry("castlingRights",gameManager.getCastlingRights()),
                Map.entry("materialImbalance", boardState.getMaterial()),
                Map.entry("enPassantSquare",boardState.getEnPassantSquare()),
                Map.entry("enemyKingInCheck", enemyKingCheckSquare),
                Map.entry("checkmate", boardState.isCheckmate()),
                Map.entry("fen", ChessPositionIntoFENFormat.transformIntoFEN(game.getBoardOperator().getBoardState()))
        );
        game.incrementTurnCounter();
        if (game.getTurnCounter() % 2 == 0) {
            game.updatePgnNotation(" " + game.getTurnCounter() / 2 + ". ");
        }
        game.updatePgnNotation(
                " " + NotationToBitboardConverter.chessPositionIntegerToSquare(moveRequest.getFrom()) +
                        NotationToBitboardConverter.chessPositionIntegerToSquare(moveRequest.getTo())
        );
//        System.out.println("Post-move queens are \n" + BinaryStringToChessPos.bitboardToChessPos(boardState.getPieceMap().get(PieceType.QUEEN).getBitboard()));
//        System.out.println("Post-move whites are \n" + BinaryStringToChessPos.bitboardToChessPos(boardState.getColorBitboard(PieceColor.WHITE)));


        return info;
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

    public int getWaitingQueueLength() {
        return waitingQueue.size();
    }

    public Map<String, Object> getGameStatus(String gameId, Long userId) {
        var game = activeGames.get(gameId);
        BoardOperator board = game.getBoardOperator();
        BoardState boardState = board.getBoardState();
        int checkSquare = -1;
        if (boardState.isKingInCheck()) {
            checkSquare = Long.numberOfTrailingZeros(
                    boardState.getColorBitboard(boardState.getCurrentPlayerTurn()) & boardState.getPieceMap().get(PieceType.KING).getBitboard()
            );
        }

        return Map.ofEntries(
                Map.entry("gameId", gameId),
                Map.entry("status", game.getStatus()),
                Map.entry("fen", ChessPositionIntoFENFormat.transformIntoFEN(board.getBoardState())),
                Map.entry("lastMove", game.getLastMoveMade()),
                Map.entry("currentTurn", boardState.getCurrentPlayerTurn()),
                Map.entry("playerColor", game.getPlayerColorFromId(userId)),
                Map.entry("opponentUsername", userRepository.findById(game.getOpponentColorFromId(userId)).orElseThrow(() -> new RuntimeException("user not found somehow wtf"))),
                Map.entry("isWhiteInCheck", boardState.whiteKingInCheck),
                Map.entry("isBlackInCheck", boardState.blackKingInCheck),
                Map.entry("checkSquare", checkSquare),
                Map.entry("isCheckmate", boardState.isCheckmate()),
                Map.entry("pgn", game.getPgnNotation()),
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
        game.getBoardOperator().getBoardState().setCheckmate(true);
        return getGameStatus(gameId, userId);
    }

    public Map<String, Object> joinBotGame(Long playerId, PieceColor color) {
        GameSession game = createNewBotGame(playerId, color);
        activeGames.put(game.getGameId(), game);
        playerToGameMap.put(playerId, game.getGameId());
        return Map.of(
                "gameId", game.getGameId()
        );
    }

    public Map<String, Object> getBotGameSetup(String gameId, Long userId) {
        GameSession game = getGameSession(gameId);
        System.out.println(game.getGameId() +" this is the game id");
        return Map.ofEntries(
                Map.entry("playerColor", game.getPlayerColorFromId(userId)),
                Map.entry("legalMoves", game.getBoardOperator().getLegalMoves()),
                Map.entry("fen", ChessPositionIntoFENFormat.transformIntoFEN(game.getBoardOperator().getBoardState()))
        );
    }

    public Map<String, Object> getBotGameStatus(String gameId, Long userId) {
        return null;
    }

    public Map<String, Object> processBotMove(String gameId, MoveMade moveRequest) {
        GameSession game = getGameSession(gameId);
        if (!game.isPlayersTurn(-1L)) {
            throw new RuntimeException("This is not this players' turn");
        }

        var gameManager = game.getBoardOperator();
        var boardState = gameManager.getBoardState();

        int enemyKingCheckSquare = -1;

        if (moveRequest.getCastlingType() != CastlingType.NOCASTLE) {
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
        if (boardState.isKingInCheck()) {
            enemyKingCheckSquare = Long.numberOfTrailingZeros(
                    boardState.getColorBitboard(boardState.getCurrentPlayerTurn()) & boardState.getPieceMap().get(PieceType.KING).getBitboard()
            );
        }
        game.setLastMoveMade(moveRequest);
        var info = Map.ofEntries(
                Map.entry("legalMoves", legalMoves),
                Map.entry("castlingRights",gameManager.getCastlingRights()),
                Map.entry("materialImbalance", boardState.getMaterial()),
                Map.entry("enPassantSquare",boardState.getEnPassantSquare()),
                Map.entry("enemyKingInCheck", enemyKingCheckSquare),
                Map.entry("checkmate", boardState.isCheckmate()),
                Map.entry("fen", ChessPositionIntoFENFormat.transformIntoFEN(game.getBoardOperator().getBoardState())),
                Map.entry("pgn", game.getPgnNotation())
        );
        game.incrementTurnCounter();
        if (game.getTurnCounter() % 2 == 0) {
            game.updatePgnNotation("\n " + game.getTurnCounter() / 2 + ". ");
        }
        game.updatePgnNotation(
                " " + NotationToBitboardConverter.chessPositionIntegerToSquare(moveRequest.getFrom()) +
                        NotationToBitboardConverter.chessPositionIntegerToSquare(moveRequest.getTo())
        );
        System.out.println(legalMoves);
        System.out.println(boardState.isCheckmate() + " this is checkmate actually");
        System.out.println(boardState.getMaterial() + " this is material actually");


        return info;
    }
}
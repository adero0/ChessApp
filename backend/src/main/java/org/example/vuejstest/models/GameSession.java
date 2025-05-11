package org.example.vuejstest.models;

import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.chessRelated.enums.CastlingType;
import org.example.vuejstest.chessRelated.enums.PieceColor;
import org.example.vuejstest.models.enums.GameStatus;

public class GameSession {
    private String gameId;
    private Long whitePlayerId;
    private Long blackPlayerId;
    private BoardOperator boardOperator;
    private GameStatus status;
    private MoveMade lastMoveMade;
    private StringBuilder pgnNotation;
    private int turnCounter;

    public MoveMade getLastMoveMade() {
        return lastMoveMade;
    }

    public void setLastMoveMade(MoveMade lastMoveMade) {
        this.lastMoveMade = lastMoveMade;
    }

    public boolean isPlayersTurn(Long playerId) {
        return (isWhiteTurn() && playerId.equals(whitePlayerId)) ||
                (!isWhiteTurn() && playerId.equals(blackPlayerId));
    }

    public boolean isWhiteTurn(){
        return boardOperator.getBoardState().getCurrentPlayerTurn() == PieceColor.WHITE;
    }

    public void switchTurn() {
        boardOperator.getBoardState().getAndChangeCurrentPlayerTurn();
    }
    private GameSession() {}

    public String getGameId() { return gameId; }
    public Long getWhitePlayerId() { return whitePlayerId; }
    public Long getBlackPlayerId() { return blackPlayerId; }
    public BoardOperator getBoardOperator() { return boardOperator; }

    public GameStatus getStatus() { return status; }
    public void setGameId(String gameId) {this.gameId = gameId;}
    public void setWhitePlayerId(Long whitePlayerId) {this.whitePlayerId = whitePlayerId;}
    public void setBlackPlayerId(Long blackPlayerId) {this.blackPlayerId = blackPlayerId;}
    public void setBoardOperator(BoardOperator boardOperator) {this.boardOperator = boardOperator;}
    public PieceColor getPlayerColorFromId(Long playerId) {
        if(playerId.equals(whitePlayerId)) return PieceColor.WHITE;
        if(playerId.equals(blackPlayerId)) return PieceColor.BLACK;
        throw new IllegalArgumentException("Invalid player id: " + playerId);
    }
    public Long getOpponentColorFromId(Long playerId) {
        if(playerId.equals(whitePlayerId)) return blackPlayerId;
        if(playerId.equals(blackPlayerId)) return whitePlayerId;
        throw new IllegalArgumentException("Invalid player id thus can't find opponent. Your id: " + playerId);
    }
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getPgnNotation() {
        return pgnNotation.toString();
    }

    public void setPgnNotation(String pgnNotation) {
        this.pgnNotation = new StringBuilder(pgnNotation);
    }

    public void updatePgnNotation(String toAdd) {
        this.pgnNotation.append(toAdd);
    }

    public void incrementTurnCounter() {
        this.turnCounter++;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public static class Builder {
        private final GameSession session;

        public Builder() {
            session = new GameSession();
        }

        public Builder gameId(String gameId) {
            session.gameId = gameId;
            return this;
        }

        public Builder lastMoveMade() {
            var moveMade = new MoveMade();
            moveMade.setFrom(0);
            moveMade.setTo(0);
            moveMade.setPiece(null);
            moveMade.setCastlingType(CastlingType.NOCASTLE);
            session.setLastMoveMade(moveMade);
            return this;
        }

        public Builder gameNotation() {
            session.setPgnNotation("");
            return this;
        }

        public Builder whitePlayerId(Long whitePlayerId) {
            session.whitePlayerId = whitePlayerId;
            return this;
        }

        public Builder blackPlayerId(Long blackPlayerId) {
            session.blackPlayerId = blackPlayerId;
            return this;
        }

        public Builder boardOperator(BoardOperator boardOperator) {
            session.boardOperator = boardOperator;
            return this;
        }

        public Builder status(GameStatus status) {
            session.status = status;
            return this;
        }

        public Builder set0TurnCounter() {
            session.turnCounter = 1;
            return this;
        }

        public GameSession build() {
            return session;
        }
    }
}
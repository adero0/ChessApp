package org.example.vuejstest.models;

import org.example.vuejstest.chessRelated.board.BoardOperator;
import org.example.vuejstest.models.enums.GameStatus;

public class GameSession {
    private String gameId;
    private Long whitePlayerId;
    private Long blackPlayerId;
    private BoardOperator boardOperator;
    private boolean isWhiteTurn;
    private GameStatus status;

    public boolean isPlayersTurn(Long playerId) {
        return (isWhiteTurn && playerId.equals(whitePlayerId)) ||
                (!isWhiteTurn && playerId.equals(blackPlayerId));
    }

    public void switchTurn() {
        isWhiteTurn = !isWhiteTurn;
    }
    private GameSession() {}

    public String getGameId() { return gameId; }
    public Long getWhitePlayerId() { return whitePlayerId; }
    public Long getBlackPlayerId() { return blackPlayerId; }
    public BoardOperator getBoardOperator() { return boardOperator; }
    public boolean isWhiteTurn() { return isWhiteTurn; }

    public GameStatus getStatus() { return status; }
    public void setGameId(String gameId) {this.gameId = gameId;}
    public void setWhitePlayerId(Long whitePlayerId) {this.whitePlayerId = whitePlayerId;}
    public void setBlackPlayerId(Long blackPlayerId) {this.blackPlayerId = blackPlayerId;}
    public void setBoardOperator(BoardOperator boardOperator) {this.boardOperator = boardOperator;}

    public void setWhiteTurn(boolean whiteTurn) {
        isWhiteTurn = whiteTurn;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
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

        public Builder isWhiteTurn(boolean isWhiteTurn) {
            session.isWhiteTurn = isWhiteTurn;
            return this;
        }

        public Builder status(GameStatus status) {
            session.status = status;
            return this;
        }

        public GameSession build() {
            return session;
        }
    }
}
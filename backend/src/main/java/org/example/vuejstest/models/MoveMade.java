package org.example.vuejstest.models;

import org.example.vuejstest.chessRelated.enums.CastlingType;

public class MoveMade {
    private int from;
    private int to;
    private String piece;
    private CastlingType castlingType;

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public String getPiece() {
        return piece;
    }

    public void setPiece(String piece) {
        this.piece = piece;
    }

    public CastlingType getCastlingType() {
        return castlingType;
    }

    public void setCastlingType(CastlingType castlingType) {
        this.castlingType = castlingType;
    }
}

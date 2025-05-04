package org.example.vuejstest.models;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.Map;


public class MoveResponse {
    private Map<Integer, IntArrayList> legalMoves;
    private int castlingRights;
    private int materialImbalance;
    private int enPassantSquare;
    private int enemyKingInCheck;
    private boolean checkmate;

    public MoveResponse(Map<Integer, IntArrayList> legalMoves, int castlingRights, int materialImbalance, int enPassantSquare, int enemyKingInCheck, boolean checkmate) {
        this.legalMoves = legalMoves;
        this.castlingRights = castlingRights;
        this.materialImbalance = materialImbalance;
        this.enPassantSquare = enPassantSquare;
        this.enemyKingInCheck = enemyKingInCheck;
        this.checkmate = checkmate;
    }

    public int getEnemyKingInCheck() {
        return enemyKingInCheck;
    }

    public void setEnemyKingInCheck(int enemyKingInCheck) {
        this.enemyKingInCheck = enemyKingInCheck;
    }

    public Map<Integer, IntArrayList> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(Map<Integer, IntArrayList> legalMoves) {
        this.legalMoves = legalMoves;
    }

    public int getCastlingRights() {
        return castlingRights;
    }

    public void setCastlingRights(int castlingRights) {
        this.castlingRights = castlingRights;
    }

    public int getMaterialImbalance() {
        return materialImbalance;
    }

    public void setMaterialImbalance(int materialImbalance) {
        this.materialImbalance = materialImbalance;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public void setEnPassantSquare(int enPassantSquare) {
        this.enPassantSquare = enPassantSquare;
    }

    public boolean isCheckmate() {
        return checkmate;
    }

    public void setCheckmate(boolean checkmate) {
        this.checkmate = checkmate;
    }
}

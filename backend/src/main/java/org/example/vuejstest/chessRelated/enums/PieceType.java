package org.example.vuejstest.chessRelated.enums;

public enum PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN;

    public static PieceType getPieceTypeFromSingleLetterString(String letter){
        return switch(letter.toLowerCase()){
            case "r" -> ROOK;
            case "b" -> BISHOP;
            case "k" -> KING;
            case "q" -> QUEEN;
            case "n" -> KNIGHT;
            case "p" -> PAWN;
            default -> throw new IllegalStateException("Unexpected value: " + letter.toLowerCase());
        };
    }
}

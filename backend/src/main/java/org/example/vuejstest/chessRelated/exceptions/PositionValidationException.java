package org.example.vuejstest.chessRelated.exceptions;

public class PositionValidationException extends RuntimeException {
    public PositionValidationException(String message) {
        super(message);
    }
}

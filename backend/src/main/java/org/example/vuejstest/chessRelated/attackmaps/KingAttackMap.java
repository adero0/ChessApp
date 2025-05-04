package org.example.vuejstest.chessRelated.attackmaps;


import org.example.vuejstest.chessRelated.util.Constants;

public class KingAttackMap implements AttackMapSingle {
    final private long[] kingAttackMap = generatePieceAttackMap();

    private long[] generatePieceAttackMap() {
        long[] kingAttackMap = new long[64];
        for (int i = 0; i < 64; i++) {
            kingAttackMap[i] = generateSquareAttackMap(i);
        }
        return kingAttackMap;
    }

    private long generateSquareAttackMap(int square) {
        long squareAttackMap = 0L;
        if(!isLeftWall(square)){
            if(square + Constants.MOVE_UP_LEFT_INCREMENT <= Constants.BOARD_END)
                squareAttackMap |= 1L << (square + Constants.MOVE_UP_LEFT_INCREMENT);
            if(square + Constants.MOVE_LEFT_INCREMENT <= Constants.BOARD_END)
                squareAttackMap |= 1L << (square + Constants.MOVE_LEFT_INCREMENT);
            if(square + Constants.MOVE_DOWN_LEFT_INCREMENT >= Constants.BOARD_START)
                squareAttackMap |= 1L << (square + Constants.MOVE_DOWN_LEFT_INCREMENT);
        }
        if(!isRightWall(square)){
            if(square + Constants.MOVE_UP_RIGHT_INCREMENT <= Constants.BOARD_END)
                squareAttackMap |= 1L << (square + Constants.MOVE_UP_RIGHT_INCREMENT);
            if(square + Constants.MOVE_RIGHT_INCREMENT >= Constants.BOARD_START)
                squareAttackMap |= 1L << (square + Constants.MOVE_RIGHT_INCREMENT);
            if(square + Constants.MOVE_DOWN_RIGHT_INCREMENT >= Constants.BOARD_START)
                squareAttackMap |= 1L << (square + Constants.MOVE_DOWN_RIGHT_INCREMENT);
        }
        if(square + Constants.MOVE_DOWN_INCREMENT >= Constants.BOARD_START)
            squareAttackMap |= 1L << (square + Constants.MOVE_DOWN_INCREMENT);
        if(square + Constants.MOVE_UP_INCREMENT <= Constants.BOARD_END)
            squareAttackMap |= 1L << (square + Constants.MOVE_UP_INCREMENT);
        return squareAttackMap;
    }
    private static boolean isRightWall(int square) {
        return square % 8 == 0;
    }
    private static boolean isLeftWall(int square) {
        return square % 8 == 7;
    }

    @Override
    public String toString() {
        return super.toString();
    }


    @Override
    public long[] getAttackMap() {
        return kingAttackMap;
    }

}
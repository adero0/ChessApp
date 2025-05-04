package org.example.vuejstest.chessRelated.util;

public class NotationToBitboardConverter {

    public static long chessSquareToBitboard(String square) {
        if(square.length() != 2) { throw new IllegalArgumentException("Must be 2 character"); }
        String file = square.substring(0, 1);
        int rank = Integer.parseInt(square.substring(1, 2));
        rank -= 1;
        if (rank < 0 || rank > 7 || file.charAt(0) < 'A' || file.charAt(0) > 'H') {
            throw new IllegalArgumentException("Invalid chess square input.");
        }
        int bitFromSquare = rank * 8 + extractFileIndex(file);
        return 1L << bitFromSquare;
    }



    public static String chessPositionIntegerToSquare(int position){
        StringBuilder squareNamed = new StringBuilder();
        if(position == -1) return "-";
        squareNamed.append( (char)(BoardUtil.transpose((position % 8)) + 97));
        squareNamed.append(position/8 + 1);
        return squareNamed.toString();
    }

    private static int extractFileIndex(String file) {
        return file.charAt(0) - 'A';
    }
}
//63 -> a8
//63/8 == second number
//63%8 == 96 + first letter
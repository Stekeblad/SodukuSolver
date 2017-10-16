package main.java.SodukuUtils;

public class CoordToSquareNr {
    public static int coordToSquarenr(int r, int c) {
        return (r / 3) * 3 + (c / 3);
    }
    public static int squareAndPosToColNr(int sq, int c) {
        return (sq % 3) * 3 + c;
    }
    public static int squareAndPosToRowNr(int sq, int r) {
        return (sq / 3) * 3 + r;
    }
}

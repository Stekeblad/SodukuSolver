package main.java.SodukuUtils;

public class CoordToSquareNr {
    public static int coordToSquarenr(int r, int c) {
        return (r / 3) * 3 + (c / 3);
    }
}

package main.java.SodukuUtils;

public class SodukuCoordUtils {
    public static int coordToSquareNr(int r, int c) {
        return (r / 3) * 3 + (c / 3);
    }

    public static int squareNrAndPosToRow(int square, int pos) {
        return square / 3 * 3 + pos / 3;
    }

    public static int squareNrAndPosToCol(int square, int pos) {
        return square % 3 * 3 + pos % 3;
    }

    /**
     * input less than 3 returns 0, 3-5 returns 3 and greater than 5 returns 6
     * Expected input is between 0 and 8
     *
     * @param r: a row number
     * @return a row number
     */
    public static int topLeftRow(int r) {
        if (r < 3) return 0;
        if (r < 6) return 3;
        else return 6;
    }

    /**
     * input less than 3 returns 0, 3-5 returns 3 and greater than 5 returns 6
     * Expected input is between 0 and 8
     *
     * @param c: a column number
     * @return a column number
     */
    public static int topLeftCol(int c) {
        if (c < 3) return 0;
        if (c < 6) return 3;
        else return 6;
    }
}

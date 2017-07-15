package sample;


import SodukuUtils.CoordToSquareNr;
import SodukuUtils.NumSeen;
import Utils.ListToArray;
import Utils.ShrinkArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SodukuSolver {

    private int[][] playfield = new int [9][9];
    private Boolean isInitialized = false;

    /**
     * Default constructor, not used, do not use
     */
    public SodukuSolver() {
        throw new UnsupportedOperationException("This constructor is not implemented");
    }

    /**
     * Recommended Constructor, Sets up the playfield with given 9x9 2-dimensional int-array
     * @param array: a 9x9 int-array containing values between 0 and 9. 0 means the value is not given initially
     */
    public SodukuSolver(int[][] array){
        playfield = array;
        isInitialized = true;
    }

    public int[][] getPlayfield() {
        return playfield;
    }

    /**
     * Tries to solve the set grid
     * @return true if the soduku was solved, false otherwise
     */
    public Boolean solve() {
        if (!isInitialized) {
            return false;
        }

        // Algorithm version 1
        //--------------------------
        // ONE PASS ONLY, FOR THE MOMENT

        int[][] squareResult = new int[9][];
        int squareCount = 0;
        int[][] rowResult = new int[9][];
        int[][] columnResult = new int[9][];

        for (int r = 0; r < 9; r+=3) {
            for (int c = 0; c < 9; c+=3, squareCount++) {
                squareResult[squareCount] = scanSquare(r, c);
            }
        }
        for (int r = 0; r < 9; r++) {
            rowResult[r] = scanRow(r);
        }
        for (int c = 0; c < 9; c++) {
            columnResult[c] = scanCol(c);
        }

        for(int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if(playfield[r][c] != 0)
                    continue;

                int squareNumber = CoordToSquareNr.coordToSquarenr(r, c);
                int match = findSingleCommon(squareResult[squareNumber],
                        rowResult[r], columnResult[c]);
                if (match != 0) {
                    playfield[r][c] = match;
                    squareResult[squareNumber] = ShrinkArray.excludeValue(squareResult[squareNumber], match);
                    rowResult[r] = ShrinkArray.excludeValue(rowResult[r], match);
                    columnResult[c] = ShrinkArray.excludeValue(columnResult[c], match);
                }
            }
        }
        return false;
    }

    /**
     * Returns all numbers that can be placed in the cell on row r and column c then only considering the numbers
     * already in that square
     * @param r: the row number (0-8)
     * @param c: the column number (0-8)
     * @return a int array containing the possible numbers
     */
    private int[] scanSquare(int r, int c) {
        r = topLeftRow(r);
        c = topLeftCol(c);
        NumSeen numSeen = new NumSeen();

        for (int i = 0; i < 3; i++) {
            for (int j = 0 ; j < 3; j++) {
                int num = playfield[r+i][c+j];
                if ( num != 0) {
                    numSeen.makeSeen(num);
                }
            }
        }
        return numSeen.getUnseen();
    }

    /**
     * Returns all number that can be placed on the given row
     * @param r: the row number (0-8)
     * @return a int array containing the possible numbers
     */
    private int[] scanRow(int r) {
        NumSeen numSeen = new NumSeen();
        for (int c = 0; c < 9; c++) {
            int num = playfield[r][c];
            if (num != 0) {
                numSeen.makeSeen(num);
            }
        }
        return numSeen.getUnseen();
    }

    /**
     * Returns all number that can be placed on the given column
     * @param c: the column number (0-8)
     * @return a int array containing the possible numbers
     */
    private int[] scanCol(int c) {
        NumSeen numSeen = new NumSeen();
        for (int r = 0; c < 9; c++) {
            int num = playfield[r][c];
            if (num != 0) {
                numSeen.makeSeen(num);
            }
        }
        return numSeen.getUnseen();
    }

    /**
     * input 0-2 returns 0, 3-5 returns 3 and 6-8 returns 6
     * @param r: a row number
     * @return a row number
     */
    private int topLeftRow(int r) {
        if (r < 3) return 0;
        if (r < 6) return 3;
        else return 6;
    }

    /**
     * input 0-2 returns 0, 3-5 returns 3 and 6-8 returns 6
     * @param c: a column number
     * @return a column number
     */
    private int topLeftCol(int c) {
        if (c < 3) return 0;
        if (c < 6) return 3;
        else return 6;
    }

    private int[] findCommons(int[] sqList, int[] rowList, int[] colList) {
        if (sqList.length == 0 || rowList.length == 0 || colList.length == 0)
            return null; // One or more of the square, the row or the column is already solved. Return.

        List<Integer> sq = new ArrayList<>();
        List<Integer> row = new ArrayList<>();
        List<Integer> col = new ArrayList<>();

        for (int aSqList : sqList) sq.add(aSqList);
        for (int aRowList : rowList) row.add(aRowList);
        for (int aColList : colList) col.add(aColList);

        Collections.sort(sq);
        Collections.sort(row);
        Collections.sort(col);

        // First matching row and column, then adding the square to it
        int x = 0;
        int rowLen = row.size();
        int colLen = col.size();

        while ( rowLen > x && colLen > x) {
            if (row.get(x).equals(col.get(x))) {
                x++;
            } else if (row.get(x) < col.get(x)) {
                row.remove(x);
                rowLen--;
            } else {
                col.remove(x);
                colLen--;
            }
        }
        while (rowLen > colLen) {
            row.remove(--rowLen);
        }
        while (rowLen < colLen) {
            col.remove(--colLen);
        }

        // row and col are now identical, hopefully. Now to Sq as well
        x = 0;
        int sqLen = sq.size();

        while ( rowLen > x && sqLen > x) {
            if (row.get(x).equals(sq.get(x))) {
                x++;
            } else if (row.get(x) < sq.get(x)) {
                row.remove(x);
                rowLen--;
            } else {
                sq.remove(x);
                sqLen--;
            }
        }
        while (rowLen > sqLen) {
            row.remove(--rowLen);
        }
        while (rowLen < sqLen) {
            sq.remove(--sqLen);
        }
        // now should sq == col && col == row ( and therefor sq == row )
        if (row.isEmpty())
            return new int[] {0};
        return ListToArray.integerListToIntArray(row);

    }
    private int findSingleCommon(int[] sqList, int[] rowList, int[] colList) {
        int[] result = findCommons(sqList, rowList, colList);
        if (result != null) {
            if  (result.length == 1)
                return result[0];
            else
                return 0;
        }
        return 0;
    }
}

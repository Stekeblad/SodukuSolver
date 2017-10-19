package main.java.SodukuSolver;

import main.java.SodukuUtils.CoordToSquareNr;
import main.java.SodukuUtils.NumSeen;
import main.java.Utils.ListArrayConverter;
import main.java.Utils.ShrinkArray;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A class made for solving soduku.
 * Methods may throw exception if class is not properly initialized using either SodukuSolver(int[][]) or
 * SodukuSolver() followed by setPlayfield(int[][])
 */
public class SodukuSolver {

    private int[][] playfield = new int[9][9];
    private Boolean isInitialized = false;
    private int[][][] everythingPossible;

    /**
     * Default constructor, call setPlayfield before using any other method
     */
    public SodukuSolver() {
        isInitialized = false;
    }

    /**
     * Recommended Constructor, Sets up the playfield with given 9x9 2-dimensional int-array
     *
     * @param array: a 9x9 int-array containing values between 0 and 9. 0 means the value is not given initially
     */
    public SodukuSolver(int[][] array) {
        playfield = array;
        isInitialized = true;
    }

    int[][] getPlayfield() {
        return playfield;
    }

    /**
     * Set or change the playfield (soduku board) to be solved
     *
     * @param newPlayfield a int[9][9] that describes a soduku board to solve
     */
    void setPlayfield(int[][] newPlayfield) {
        playfield = newPlayfield;
        isInitialized = true;
    }

    /**
     * Tries to solve the set grid
     *
     * @return true if the soduku was solved, false otherwise
     */
    Boolean solve() throws Exception {
        if (!isInitialized) {
            return false;
        }

        // Preparations

        int[][] colResult = new int[9][];
        int[][] sqResult = new int[9][];
        int[][] rowResult = new int[9][];
        for (int i = 0, j = 0; i < 3; i++) {
            sqResult[j++] = scanSquare(i * 3, 0);
            sqResult[j++] = scanSquare(i * 3, 3);
            sqResult[j++] = scanSquare(i * 3, 6);
        }
        for (int i = 0; i < 9; i++) {
            rowResult[i] = scanRow(i);
            colResult[i] = scanCol(i);
        }
        everythingPossible = new int[9][9][];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (playfield[r][c] != 0) {
                    everythingPossible[r][c] = new int[]{0};
                    continue;
                }
                everythingPossible[r][c] = findCommons(sqResult[CoordToSquareNr.coordToSquarenr(r, c)],
                        rowResult[r], colResult[c]);
            }
        }

        // Attempt solving

        boolean madeProgress;
        do {
            madeProgress = false;

            // Algorithm 1: Places a number if it is the only one who can be in a cell

            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    if (playfield[r][c] != 0)
                        continue;

                    if (everythingPossible[r][c].length == 1) {
                        int match = everythingPossible[r][c][0];
                        playfield[r][c] = match;
                        removePossibilities(r, c, match);
                        madeProgress = true;
                    }
                }
            }

            // Algorithm 2: Places a number if it can only be there (and nowhere else in row/col/sq)

            ArrayList<Triple<Integer, Integer, Integer>> results = singlePossible();
            if (!results.isEmpty()) {
                for (Triple<Integer, Integer, Integer> result : results) {
                    int r = result.getLeft();
                    int c = result.getMiddle();
                    int answer = result.getRight();
                    playfield[r][c] = answer;
                    // Possibilities was removed then the answer was added to the results list
                }
                madeProgress = true;
            }

            // Algorithm 3: Locked candidates ("possibilities" with my naming choice) http://www.angusj.com/sudoku/hints.php
            // Planned but not yet implemented


        } while (madeProgress);

        return validateSolve();
    }

    private Boolean validateSolve() {
        try {
            for (int r = 0; r < 9; r += 3) {
                for (int c = 0; c < 9; c += 3) {
                    if (scanSquare(0, 0).length != 0) {
                        return false;
                    }
                }
            }
            for (int r = 0; r < 9; r++) {
                if (scanRow(r).length != 0) {
                    return false;
                }
            }
            for (int c = 0; c < 9; c++) {
                if (scanCol(c).length != 0) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Returns all numbers that is missing in the given square
     *
     * @param r: the row number (0-8)
     * @param c: the column number (0-8)
     * @return a int array containing the missing numbers
     * @throws Exception: if this instance of the class has not been initialized
     */
    private int[] scanSquare(int r, int c) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        r = topLeftRow(r);
        c = topLeftCol(c);
        NumSeen numSeen = new NumSeen();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num = playfield[r + i][c + j];
                if (num != 0) {
                    numSeen.makeSeen(num);
                }
            }
        }
        return numSeen.getUnseen();
    }

    /**
     * Returns all number that can is missing on the given row
     *
     * @param r: the row number (0-8)
     * @return a int array containing the missing numbers
     * @throws Exception: if this instance of the class has not been initialized
     */
    private int[] scanRow(int r) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

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
     * Returns all number that can is missing on the given column
     *
     * @param c: the column number (0-8)
     * @return a int array containing the missing numbers
     * @throws Exception: if this instance of the class has not been initialized
     */
    private int[] scanCol(int c) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        NumSeen numSeen = new NumSeen();
        for (int r = 0; r < 9; r++) {
            int num = playfield[r][c];
            if (num != 0) {
                numSeen.makeSeen(num);
            }
        }
        return numSeen.getUnseen();
    }

    /**
     * input less than 3 returns 0, 3-5 returns 3 and greater than 5 returns 6
     * Expected input is between 0 and 8
     *
     * @param r: a row number
     * @return a row number
     */
    private int topLeftRow(int r) {
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
    private int topLeftCol(int c) {
        if (c < 3) return 0;
        if (c < 6) return 3;
        else return 6;
    }

    /**
     * Returns a int array of numbers that appears in all three given int arrays
     *
     * @param sqList  squareResult[int]
     * @param rowList rowResult[int]
     * @param colList columnResult[int]
     * @return a int array containing all numbers that appear in all three given arrays, if the returned array only
     * contains a zero it means there are no common numbers.
     * @throws Exception if this instance of the class has not been initialized
     */
    private int[] findCommons(int[] sqList, int[] rowList, int[] colList) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        if (sqList.length == 0 || rowList.length == 0 || colList.length == 0)
            return null; // One or more of the square, the row or the column is already solved. Return.

        ArrayList<Integer> sq = new ArrayList<>();
        ArrayList<Integer> row = new ArrayList<>();
        ArrayList<Integer> col = new ArrayList<>();

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

        while (rowLen > x && colLen > x) {
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

        // row and col are now identical. Now to Sq as well
        x = 0;
        int sqLen = sq.size();

        while (rowLen > x && sqLen > x) {
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
        // now is sq == col && col == row ( and therefor sq == row )
        if (row.isEmpty())
            return new int[]{0};
        return ListArrayConverter.integerListToIntArray(row);

    }

    /**
     * removes the possibility of value in the cell [r, c]. Safe to call even if value is not a possibility before the call.
     *
     * @param r the row of the target cell
     * @param c the column of the target cell
     * @param value the number to remove
     */
    private void removePossibilityCell(int r, int c, int value) {
        everythingPossible[r][c] = ShrinkArray.excludeValue(everythingPossible[r][c], value);
    }

    /**
     * Use this method then the position of a number becomes known, this will update everythingPossible to reduce the
     * number of unknown cells that still can contain the found number.
     *
     * Removes the possibilities for value on row r, column c and the square this cell is in as well as ALL
     * possibilities of cell [r, c]. Safe to call even if any of the affected cells has no possibilities.
     *
     * @param r the row of the just filled cell
     * @param c the column of the just filled cell
     * @param value the number that got placed in the just filled cell
     */
    private void removePossibilities(int r, int c, int value) {
        for (int i = 0; i < 9; i++) { // value can no longer be placed in row r
            everythingPossible[r][i] = ShrinkArray.excludeValue(everythingPossible[r][i], value);
        }
        for (int i = 0; i < 9; i++) { // value can no longer be placed in column c
            everythingPossible[i][c] = ShrinkArray.excludeValue(everythingPossible[i][c], value);
        }
        int row = topLeftRow(r);
        int col = topLeftCol(c);
        for (int i = 0; i < 9; i++) { // value can no longer be placed in the square of cell [r, c]
            everythingPossible[row + i / 3][col + i % 3] = ShrinkArray.excludeValue(
                    everythingPossible[row + i / 3][col + i % 3], value);
        }
        everythingPossible[r][c] = new int[]{}; // Cell now filled, no new numbers can be placed in it
    }

    /**
     * Searches through the everythingPossible multidimensional array after numbers that can only be placed in one cell
     * in a row/column/square. The result of the search is returned in a {@code ArrayList<Triple<Integer, Integer, Integer>>}
     * if nothing was found a empty list is returned.
     *<pre>
     * triple.getLeft() contains a row number
     * triple.getMiddle() contains a column number
     * triple.getRight() contains the number that needs to be placed in the specified row and column
     *</pre>
     *
     * @return {@code ArrayList<Triple<Integer, Integer, Integer>>}
     * @throws Exception if this instance of the class has not been initialized
     */
    private ArrayList<Triple<Integer, Integer, Integer>> singlePossible() throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        ArrayList<Triple<Integer, Integer, Integer>> answers = new ArrayList<>();

        //Square
        for (int sr = 0; sr < 3; sr++) { //square row
            for (int sc = 0; sc < 3; sc++) { // square column
                int[][] squarePossible = new int[9][];
                for (int r = sr * 3, i = 0; r < sr * 3 + 3; r++) { // row in square
                    squarePossible[i++] = everythingPossible[r][sc * 3];
                    squarePossible[i++] = everythingPossible[r][sc * 3 + 1];
                    squarePossible[i++] = everythingPossible[r][sc * 3 + 2];
                }
                for (int i = 1; i < 10; i++) {
                    int position = singlePossibleFinder(squarePossible, i);
                    if (position > -1) {
                        int row = sr * 3 + position / 3;
                        int col = sc * 3 + position % 3;
                        answers.add(new ImmutableTriple<>(row, col, i));
                        removePossibilities(row, col, i);
                    }
                }
            }
        }

        //Row
        for (int r = 0; r < 9; r++) {
            int[][] rowPossible = new int[9][];
            for (int c = 0; c < 9; c++) { // not changing to arrayCopy because I think it will make it more difficult to follow then the very similar line in the "//Column" block cant be changed to arrayCopy
                rowPossible[c] = everythingPossible[r][c];
            }
            for (int i = 1; i < 10; i++) {
                int position = singlePossibleFinder(rowPossible, i);
                if (position > -1) {
                    answers.add(new ImmutableTriple<>(r, position, i));
                    removePossibilities(r, position, i);
                }
            }
        }

        //Column
        for (int c = 0; c < 9; c++) {
            int[][] columnPossible = new int[9][];
            for (int r = 0; r < 9; r++) {
                columnPossible[r] = everythingPossible[r][c];
            }
            for (int i = 1; i < 10; i++) {
                int position = singlePossibleFinder(columnPossible, i);
                if (position > -1) {
                    answers.add(new ImmutableTriple<>(position, c, i));
                    removePossibilities(position, c, i);
                }
            }
        }
        return answers;
    }

    /**
     * Used by singlePossible to minimize repetitive code.
     * Searches through a two-dimensional int array after a number and if the number only appears in one of the sub-arrays
     * the index of that array is returned, else -1 is returned.
     *
     * @param data two-dimensional int array to analyse
     * @param i number to find single occurrence of
     * @return a integer >= -1 and <= 8, the index of the only inner array that contains i or -1 if i occurs in no or more than one array.
     */
    private int singlePossibleFinder(int[][] data, int i) {
        if (!isInMultiple(data, i)) {
            for (int j = 0; j < 9; j++) {
                if (data[j] != null) {
                    for (int k : data[j]) {
                        if (k == i) {
                            return j;
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Takes a int[][] 'array' and a int 'numToTest' to test. If numToTest is inside two or more of the inner arrays in
     * array this method returns true, else it returns false
     *
     * @param array     a 2-dimensional int array
     * @param numToTest a number to check existence of
     * @return true if numToTest is inside two or more of the inner arrays. false if numToTest occurs zero or one time.
     */
    private boolean isInMultiple(int[][] array, int numToTest) {
        int timesSeen = 0;
        for (int[] innerArray : array) {
            if (innerArray != null) {
                for (int number : innerArray) { // skips then innerArray = null ?
                    if (number == numToTest) {
                        if (++timesSeen > 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return timesSeen == 0; //only timesSeen == 1 should return false
    }
}

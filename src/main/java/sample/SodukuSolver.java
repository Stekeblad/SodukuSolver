package main.java.sample;


import main.java.SodukuUtils.CoordToSquareNr;
import main.java.SodukuUtils.NumSeen;
import main.java.Utils.ListArrayConverter;
import main.java.Utils.ShrinkArray;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class made for solving soduku.
 * Methods may throw exception if class is not properly initialized using either SodukuSolver(int[][]) or
 * SodukuSolver() followed by setPlayfield(int[][])
 */
public class SodukuSolver {

    private int[][] playfield = new int[9][9];
    private Boolean isInitialized = false;

    private int[][] squareResult;
    private int[][] rowResult;
    private int[][] columnResult;


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

        squareResult = new int[9][];
        rowResult = new int[9][];
        columnResult = new int[9][];

        // Preparations

        for (int r = 0, squareCount = 0; r < 9; r += 3) {
            for (int c = 0; c < 9; c += 3, squareCount++) {
                squareResult[squareCount] = scanSquare(r, c);
            }
        }
        for (int i = 0; i < 9; i++) {
            rowResult[i] = scanRow(i);
            columnResult[i] = scanCol(i);
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

                    int squareNumber = CoordToSquareNr.coordToSquarenr(r, c);
                    int match = findSingleCommon(squareResult[squareNumber],
                            rowResult[r], columnResult[c]);
                    if (match != 0) {
                        playfield[r][c] = match;
                        squareResult[squareNumber] = ShrinkArray.excludeValue(squareResult[squareNumber], match);
                        rowResult[r] = ShrinkArray.excludeValue(rowResult[r], match);
                        columnResult[c] = ShrinkArray.excludeValue(columnResult[c], match);
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
                    int squareNumber = CoordToSquareNr.coordToSquarenr(r, c);
                    squareResult[squareNumber] = ShrinkArray.excludeValue(squareResult[squareNumber], answer);
                    rowResult[r] = ShrinkArray.excludeValue(rowResult[r], answer);
                    columnResult[c] = ShrinkArray.excludeValue(columnResult[c], answer);
                    madeProgress = true;
                }
            }
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
     * Returns all numbers that can be placed in the given square
     *
     * @param r: the row number (0-8)
     * @param c: the column number (0-8)
     * @return a int array containing the possible numbers
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
     * Returns all number that can be placed on the given row
     *
     * @param r: the row number (0-8)
     * @return a int array containing the possible numbers
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
     * Returns all number that can be placed on the given column
     *
     * @param c: the column number (0-8)
     * @return a int array containing the possible numbers
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
     * input 0-2 returns 0, 3-5 returns 3 and 6-8 returns 6
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
     * input 0-2 returns 0, 3-5 returns 3 and 6-8 returns 6
     *
     * @param c: a column number
     * @return a column number
     */
    private int topLeftCol(int c) {
        if (c < 3) return 0;
        if (c < 6) return 3;
        else return 6;
    }

    // Used by Algorithm 1 & 2

    /**
     * Returns a int array of numbers that appears in all three given int arrays
     *
     * @param sqList  squareResult[?]
     * @param rowList rowResult[?]
     * @param colList columnResult[?]
     * @return a int array containing all numbers that appear in all three given arrays
     * @throws Exception if this instance of the class has not been initialized
     */
    private int[] findCommons(int[] sqList, int[] rowList, int[] colList) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

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

        // row and col are now identical, hopefully. Now to Sq as well
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
        // now should sq == col && col == row ( and therefor sq == row )
        if (row.isEmpty())
            return new int[]{0};
        return ListArrayConverter.integerListToIntArray(row);

    }

    // Used by Algorithm 1

    /**
     * Calls findCommons() and returns 0 if more than one number appeared in all three given int arrays
     * or the only number that appeared in all three arrays
     *
     * @param sqList  squareResult[?]
     * @param rowList rowResult[?]
     * @param colList columnResult[?]
     * @return Returns 0 if more than one number appeared in all three given int arrays
     * or the only number that appeared in all three arrays
     * @throws Exception if this instance of the class has not been initialized
     */
    private int findSingleCommon(int[] sqList, int[] rowList, int[] colList) throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        int[] result = findCommons(sqList, rowList, colList);
        if (result != null) {
            if (result.length == 1)
                return result[0];
            else
                return 0;
        }
        return 0;
    }

    private ArrayList<Triple<Integer, Integer, Integer>> singlePossible() throws Exception {
        if (!isInitialized) {
            throw new Exception("Class not initialized, playfield not set");
        }

        int[][][] everythingPossible = new int[9][9][];
        ArrayList<Triple<Integer, Integer, Integer>> answers = new ArrayList<>();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (playfield[r][c] != 0) {
                    everythingPossible[r][c] = new int[]{0};
                    continue;
                }
                everythingPossible[r][c] = findCommons(squareResult[CoordToSquareNr.coordToSquarenr(r, c)],
                        rowResult[r], columnResult[c]);
            }
        }
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
                        //position--;
                        answers.add(new ImmutableTriple<>(sr * 3 + position / 3, sc * 3 + position % 3, i));
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
                    //position--;
                    answers.add(new ImmutableTriple<>(r, position, i));
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
                    //position--;
                    answers.add(new ImmutableTriple<>(position, c, i));
                }
            }
        }

        // De-duplicating answers
        ArrayList<Triple<Integer, Integer, Integer>> filteredAnswers = new ArrayList<>();
        int x = 0;
        while (answers.size() > 0) {
            filteredAnswers.add(answers.get(0));
            while (answers.remove(filteredAnswers.get(x))) {
            } // remove only removes first occurrence and returns true if it removed something, I want to remove all occurrences. removeAll takes a list of different values
            x++;
        }

        return filteredAnswers;
    }

    /**
     * Used by singlePossible to minimize repetitive code.
     *
     * @param data ---
     * @param i    ---
     * @return a integer >= -1 and <= 8
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

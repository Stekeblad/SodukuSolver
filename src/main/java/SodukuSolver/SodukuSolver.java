package main.java.SodukuSolver;

import main.java.SodukuUtils.NumSeen;
import main.java.SodukuUtils.SodukuCoordUtils;
import main.java.Utils.ListAndArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A class made for solving soduku.
 * Methods may throw exception if class is not properly initialized using either SodukuSolver(int[][]) or
 * SodukuSolver() followed by setPlayfield(int[][])
 */
public class SodukuSolver {

    public enum SolveResults {
        NOT_TESTED,
        SOLVED,
        SOLVE_FAILED,
        NOT_SOLVABLE,
        MULTIPLE_SOLUTIONS
    }

    private int[][] playfield = new int[9][9];
    private Boolean isInitialized = false;
    private int[][][] everythingPossible;
    private SolveResults solveResult;

    /**
     * Default constructor, call setPlayfield before using any other method
     */
    public SodukuSolver() {
        isInitialized = false;
        solveResult = SolveResults.NOT_TESTED;
    }

    /**
     * Recommended Constructor, Sets up the playfield with given 9x9 2-dimensional int-array
     *
     * @param array: a 9x9 int-array containing values between 0 and 9. 0 means the value is not given initially
     */
    public SodukuSolver(int[][] array) {
        playfield = array;
        isInitialized = true;
        solveResult = SolveResults.NOT_TESTED;
    }

	/**
     * used if you want to know how the playfield looks after the solve attempt
     * @return int[9][9] containing the playfield
     */
    int[][] getPlayfield() {
        return playfield;
    }

    /**
     * Set or change the playfield (soduku board) to be solved
     *
     * @param newPlayfield a int[9][9] that describes a soduku board to solve
     * @return false if the playfield contains any number smaller then zero or larger then nine, true otherwise.
     */
    boolean setPlayfield(int[][] newPlayfield) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (newPlayfield[row][col] < 0 || newPlayfield[row][col] > 9) {
                    return false;
                }
            }
        }
        playfield = newPlayfield;
        isInitialized = true;
        solveResult = SolveResults.NOT_TESTED;
        return true;
    }

    /**
     * Returns a enum SolveResults value telling if the soduku was solve, cant be solved, has multiple solution etc.
     * To see the result the resulting playfield is accessible via getPlayfield().
     * <pre>
     *     NOT_TESTED         solve() has not been called since the current playfield was set.
     *     SOLVED             the soduku was solved.
     *     SOLVE_FAILED       the soduku was not solved and none of the following states could be proved.
     *     NOT_SOLVABLE       the soduku has no solution, it can not be solved.
     *     MULTIPLE_SOLUTIONS the soduku has more than one possible solution.
     *
     * @return enum SolveResults
     */
    SolveResults getSolveResult() {
        return solveResult;
    }

    /**
     * Tries to solve the set grid
     */
    void solve() throws Exception {
        if (!isInitialized) {
            return;
        }

        // First check the given board

        if (! isPlayfieldValid()) {
            return;
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
                everythingPossible[r][c] = findCommons(sqResult[SodukuCoordUtils.coordToSquareNr(r, c)],
                        rowResult[r], colResult[c]);
            }
        }

        // Attempt solving

        boolean madeProgress;
        int iterationCounter = 0;
        do {
            madeProgress = false;
            if (iterationCounter > 100) {
                validateSolve();
                System.err.println("Solve aborted! Locked looping with faked progress!");
                //throw new Exception("Solve aborted! Locked looping with faked progress!");
                return;
            }
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

            if (lockedPossibilities()) { // true if some possibilities was removed, maybe algorithm 1 or 2 can make new progress now
                madeProgress = true;
            }

            iterationCounter++;
        } while (madeProgress);

        validateSolve();
    }

    /**
     * Called in the beginning of solve() to make a quick check if the set board is solvable
     * Tests if a number appear more than once in a single row, column or square. The amount of given numbers are also
     * counted because if less than 17 numbers are given it has been proved to not have exactly one solution.
     * If false is returned, solveResult has been set to contain NOT_SOLVABLE or MULTIPLE_SOLUTIONS.
     *
     * @return false if there are definitely not exact one solution (#solutions != 1) and true if this quick check could
     * not tell how many solutions there are (0, 1 or >1)
     */
    private boolean isPlayfieldValid() {
        boolean seenInRow[][] = new boolean[9][10];
        boolean seenInCol[][] = new boolean[9][10];
        boolean seenInSq[][] = new boolean[9][10];
        int numbersGiven = 0;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int sq = SodukuCoordUtils.coordToSquareNr(row, col);
                int number = playfield[row][col];
                if(number != 0) {
                    numbersGiven++;
                    if (seenInCol[col][number] || seenInRow[row][number] || seenInSq[sq][number]) {
                        solveResult = SolveResults.NOT_SOLVABLE; // at least one number appears >1 time in a row/col/square
                        return false;
                    } else {
                        seenInCol[col][number] = true;
                        seenInRow[row][number] = true;
                        seenInSq[sq][number] = true;
                    }
                }
            }
        }
        if (numbersGiven < 17) { // It has been proved that no soduku with less than 17 clues can have exactly one solution.
            solveResult = SolveResults.MULTIPLE_SOLUTIONS;
            return false;
        }

        return true;
    }

    /**
     * Tries to decide if the soduku has been solved or not, if it can't be solved or if it has multiple solutions and
     * sets the variable solveResult accordingly
     */
    private void validateSolve() throws Exception {
        boolean numbersMissing = false;
        ArrayList<Pair<Integer, Integer>> possibilitiesToStudy = new ArrayList<>();

        if (! isPlayfieldValid()) {
            // something is wrong in the solving algorithms
            System.err.println("isPlayfieldValid inside validateSolve returned false. Something is wrong in the solving algorithms");
            return;
        }
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (playfield[row][col] == 0) {
                    numbersMissing = true;
                    if (everythingPossible[row][col].length == 0) {
                        // A cell is empty and it is not possible to place any number in it without breaking the unique in row/col/sq law
                        solveResult = SolveResults.NOT_SOLVABLE;
                        return;
                    } else if (everythingPossible[row][col].length == 1) { // Now something is wrong!
                        solveResult = SolveResults.SOLVE_FAILED;
                        System.err.println("Now something is wrong! validateSolve is running and the cell [" + row + ", " + col + "] has one possibility");
                        return;
                    } else {
                        possibilitiesToStudy.add(new ImmutablePair<>(row, col));
                    }
                }
            }
        }

        //if (possibilitiesToStudy.isEmpty())
        //{
            if (numbersMissing) {
                solveResult = SolveResults.SOLVE_FAILED;
            } else {
                solveResult = SolveResults.SOLVED;
            }
            return;
        //}

        // Ignore case "multiple solutions" until a algorithm is invented

        //solveResult = SolveResults.MULTIPLE_SOLUTIONS;
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

        r = SodukuCoordUtils.topLeftRow(r);
        c = SodukuCoordUtils.topLeftCol(c);
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
        return ListAndArrayUtils.integerListToIntArray(row);

    }

    /**
     * removes the possibility of value in the cell [r, c]. Safe to call even if value is not a possibility before the call.
     *
     * @param r the row of the target cell
     * @param c the column of the target cell
     * @param value the number to remove
     */
    private void removePossibilityCell(int r, int c, int value) {
        everythingPossible[r][c] = ListAndArrayUtils.excludeValue(everythingPossible[r][c], value);
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
            everythingPossible[r][i] = ListAndArrayUtils.excludeValue(everythingPossible[r][i], value);
        }
        for (int i = 0; i < 9; i++) { // value can no longer be placed in column c
            everythingPossible[i][c] = ListAndArrayUtils.excludeValue(everythingPossible[i][c], value);
        }
        int row = SodukuCoordUtils.topLeftRow(r);
        int col = SodukuCoordUtils.topLeftCol(c);
        for (int i = 0; i < 9; i++) { // value can no longer be placed in the square of cell [r, c]
            everythingPossible[row + i / 3][col + i % 3] = ListAndArrayUtils.excludeValue(
                    everythingPossible[row + i / 3][col + i % 3], value);
        }
        everythingPossible[r][c] = new int[]{}; // Cell now filled, no new numbers can be placed in it
    }

    private int[][][] getSquaresPossibilitiesArray() {
        // If Java had pointers this part could be more efficient!
        // This int[][][] could be generated once and because this array is just everythingPossible in another order
        // it could just be pointers referring to the related cell and all changes in everything possible would be visible
        // in this array and it would never need to be recalculated.
        // Currently the code using the returned array would not be able to work very efficient, after it found
        // something and updated everythingPossible it would a) risk to miss something due to outdated data to work on
        // or b) require to update the returned array or call this method again to recalculate it.
        int[][][] arraySquares = new int[9][9][];
        for (int sr = 0; sr < 3; sr++) { //square row
            for (int sc = 0; sc < 3; sc++) { // square column
                for (int r = sr * 3, i = 0; r < sr * 3 + 3; r++) { // row in square
                    arraySquares[sr * 3 + sc][i++] = everythingPossible[r][sc * 3];
                    arraySquares[sr * 3 + sc][i++] = everythingPossible[r][sc * 3 + 1];
                    arraySquares[sr * 3 + sc][i++] = everythingPossible[r][sc * 3 + 2];
                }
            }
        }
        return arraySquares;
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
        int[][][] squareArray = getSquaresPossibilitiesArray();
        for (int sq = 0; sq < 9; sq++) {
            for (int i = 1; i < 10; i++) {
                int position = ListAndArrayUtils.singlePossibleFinder(squareArray[sq], i);
                if (position > -1) {
                    int row = SodukuCoordUtils.squareNrAndPosToRow(sq, position);
                    int col = SodukuCoordUtils.squareNrAndPosToCol(sq, position);
                    answers.add(new ImmutableTriple<>(row, col, i));
                    removePossibilities(row, col, i);
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
                int position = ListAndArrayUtils.singlePossibleFinder(rowPossible, i);
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
                int position = ListAndArrayUtils.singlePossibleFinder(columnPossible, i);
                if (position > -1) {
                    answers.add(new ImmutableTriple<>(position, c, i));
                    removePossibilities(position, c, i);
                }
            }
        }
        return answers;
    }

    private boolean lockedPossibilities() {
        // type 1: A number is only possible to place in a single row or a single column inside a square
        // Then all squares on that row/column can have that number removed from the row/column in question

        // type 2: A square is alone to be able to have a number on a specific row or column
        // Then all cells in that square that is not on that row/column can have the number removed

        // This method is long and does almost the same thing four times, I don't see a easy way to split it up.

        boolean hasPossibilitiesBeenRemoved = false;
        int[][][] squares = getSquaresPossibilitiesArray();

        for (int sq = 0; sq < 9; sq++) {
            //type 1 rows

            ArrayList<Integer> top = ListAndArrayUtils.intArrayToIntegerList(squares[sq][0]);
            top.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][1]));
            top.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][2]));

            ArrayList<Integer> mid = ListAndArrayUtils.intArrayToIntegerList(squares[sq][3]);
            mid.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][4]));
            mid.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][5]));

            ArrayList<Integer> bot = ListAndArrayUtils.intArrayToIntegerList(squares[sq][6]);
            bot.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][7]));
            bot.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][8]));

            int[][] squareRowPossibilities = new int[3][];
            squareRowPossibilities[0] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(top));
            squareRowPossibilities[1] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(mid));
            squareRowPossibilities[2] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(bot));

            for (int numToCheck = 1; numToCheck < 10; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(squareRowPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int rowToAffect = SodukuCoordUtils.squareNrAndPosToRow(sq, index * 3);
                int firstColInSq = SodukuCoordUtils.squareNrAndPosToCol(sq, 0);
                int[] columnsToAffect = new int[6];
                int[] sqIndexToAffect = new int[]{index * 3, index * 3 + 1, index * 3 + 2};
                int[] squaresToAffect = new int[2];
                switch (firstColInSq) {
                    case 0:
                        columnsToAffect = new int[]{3, 4, 5, 6, 7, 8};
                        squaresToAffect = new int[]{sq + 1, sq + 2};
                        break;
                    case 3:
                        columnsToAffect = new int[]{0, 1, 2, 6, 7, 8};
                        squaresToAffect = new int[]{sq - 1, sq + 1};
                        break;
                    case 6:
                        columnsToAffect = new int[]{0, 1, 2, 3, 4, 5};
                        squaresToAffect = new int[]{sq - 2, sq - 1};
                        break;
                }
                for (int c : columnsToAffect) {
                    // Do not report progress for unchanged everythingPossible
                    if (ListAndArrayUtils.arrayContains(everythingPossible[rowToAffect][c], numToCheck)) {
                        removePossibilityCell(rowToAffect, c, numToCheck);
                        hasPossibilitiesBeenRemoved = true;
                    }
                }
                for (int i : sqIndexToAffect) { // waste of cpu to check if it is in array or not, it will not be there afterwards anyway
                    squares[squaresToAffect[0]][i] = ListAndArrayUtils.excludeValue(squares[squaresToAffect[0]][i], numToCheck);
                    squares[squaresToAffect[1]][i] = ListAndArrayUtils.excludeValue(squares[squaresToAffect[1]][i], numToCheck);
                }
            }

            // type 1 column

            ArrayList<Integer> left = ListAndArrayUtils.intArrayToIntegerList(squares[sq][0]);
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][3]));
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][6]));

            ArrayList<Integer> center = ListAndArrayUtils.intArrayToIntegerList(squares[sq][1]);
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][4]));
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][7]));

            ArrayList<Integer> right = ListAndArrayUtils.intArrayToIntegerList(squares[sq][2]);
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][5]));
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[sq][8]));

            int[][] squareColPossibilities = new int[3][];
            squareColPossibilities[0] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(left));
            squareColPossibilities[1] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(center));
            squareColPossibilities[2] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(right));

            for (int numToCheck = 1; numToCheck < 10; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(squareColPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int colToAffect = SodukuCoordUtils.squareNrAndPosToCol(sq, index);
                int firstRowInSq = SodukuCoordUtils.squareNrAndPosToRow(sq, 0);
                int[] rowsToAffect = new int[6];
                int[] sqIndexToAffect = new int[]{index % 3, (index % 3) + 3, (index % 3) + 6};
                int[] squaresToAffect = new int[2];
                switch (firstRowInSq) {
                    case 0: rowsToAffect = new int[]{3, 4, 5, 6, 7, 8};
                        squaresToAffect = new int[]{sq + 3, sq + 6};
                        break;
                    case 3: rowsToAffect = new int[]{0, 1, 2, 6, 7, 8};
                        squaresToAffect = new int[]{sq - 3, sq + 3};
                        break;
                    case 6: rowsToAffect = new int[]{0, 1, 2, 3, 4, 5};
                        squaresToAffect = new int[]{sq - 6, sq - 3};
                        break;
                }
                for (int r : rowsToAffect) {
                    // Do not report progress for unchanged everythingPossible
                    if (ListAndArrayUtils.arrayContains(everythingPossible[r][colToAffect], numToCheck)) {
                        removePossibilityCell(r, colToAffect, numToCheck);
                        hasPossibilitiesBeenRemoved = true;
                    }
                }
                for (int i : sqIndexToAffect) { // waste of cpu to check if it is in array or not, it will not be there afterwards anyway
                    squares[squaresToAffect[0]][i] = ListAndArrayUtils.excludeValue(squares[squaresToAffect[0]][i], numToCheck);
                    squares[squaresToAffect[1]][i] = ListAndArrayUtils.excludeValue(squares[squaresToAffect[1]][i], numToCheck);
                }
            }
        }

        // type 2 row

        for (int r = 0; r < 9; r++) {
            ArrayList<Integer> left = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][0]);
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][1]));
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][2]));

            ArrayList<Integer> center = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][3]);
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][4]));
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][5]));

            ArrayList<Integer> right = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][6]);
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][7]));
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[r][8]));

            int[][] rowPartsPossibilities = new int[3][];
            rowPartsPossibilities[0] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(left));
            rowPartsPossibilities[1] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(center));
            rowPartsPossibilities[2] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(right));

            for (int numToCheck = 1; numToCheck < 10; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(rowPartsPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int[] sqPositionsToAffect = new int[6];
                int sqToAffect = SodukuCoordUtils.coordToSquareNr(r, index * 3);
                switch (r % 3) {
                    case 0: sqPositionsToAffect = new int[]{3, 4, 5, 6, 7, 8};
                        break;
                    case 1: sqPositionsToAffect = new int[]{0, 1, 2, 6, 7, 8};
                        break;
                    case 2: sqPositionsToAffect = new int[]{0, 1, 2, 3, 4, 5};
                        break;
                }
                for (int p : sqPositionsToAffect) {
                    int row = SodukuCoordUtils.squareNrAndPosToRow(sqToAffect, p);
                    int col = SodukuCoordUtils.squareNrAndPosToCol(sqToAffect, p);

                    // Do not report progress for unchanged everythingPossible
                    if (ListAndArrayUtils.arrayContains(everythingPossible[row][col], numToCheck)) {
                        removePossibilityCell(row, col, numToCheck);
                        hasPossibilitiesBeenRemoved = true;
                    }
                }
            }

        }

        // type 2 column

        for (int c = 0; c < 9; c++) {
            ArrayList<Integer> top = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[0][c]);
            top.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[1][c]));
            top.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[2][c]));

            ArrayList<Integer> mid = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[3][c]);
            mid.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[4][c]));
            mid.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[5][c]));

            ArrayList<Integer> bot = ListAndArrayUtils.intArrayToIntegerList(everythingPossible[6][c]);
            bot.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[7][c]));
            bot.addAll(ListAndArrayUtils.intArrayToIntegerList(everythingPossible[8][c]));

            int[][] colPartsPossibilities = new int[3][];
            colPartsPossibilities[0] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(top));
            colPartsPossibilities[1] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(mid));
            colPartsPossibilities[2] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(bot));

            for (int numToCheck = 1; numToCheck < 10; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(colPartsPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int[] sqPositionsToAffect = new int[6];
                int sqToAffect = SodukuCoordUtils.coordToSquareNr(index * 3, c);
                switch (c % 3) {
                    case 0: sqPositionsToAffect = new int[]{1, 2, 4, 5, 7, 8}; // not 0, 3, 6. (left col)
                        break;
                    case 1: sqPositionsToAffect = new int[]{0, 2, 3, 5, 6, 8}; // not 1, 4, 7. (center col)
                        break;
                    case 2: sqPositionsToAffect = new int[]{0, 1, 3, 4, 6, 7}; // not 2, 5, 8. (right col)
                        break;
                }
                for (int p : sqPositionsToAffect) {
                    int row = SodukuCoordUtils.squareNrAndPosToRow(sqToAffect, p);
                    int col = SodukuCoordUtils.squareNrAndPosToCol(sqToAffect, p);

                    // Do not report progress for unchanged everythingPossible
                    if (ListAndArrayUtils.arrayContains(everythingPossible[row][col], numToCheck)) {
                        removePossibilityCell(row, col, numToCheck);
                        hasPossibilitiesBeenRemoved = true;
                    }
                }
            }
        }

        return hasPossibilitiesBeenRemoved;
    }

}

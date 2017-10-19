package main.java.SodukuSolver;

import main.java.SodukuUtils.NumSeen;
import main.java.SodukuUtils.SodukuCoordUtils;
import main.java.Utils.ListAndArrayUtils;
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
                everythingPossible[r][c] = findCommons(sqResult[SodukuCoordUtils.coordToSquareNr(r, c)],
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
            if (lockedPossibilities()) { // true if some possibilities was removed, maybe algorithm 1 or 2 can make new progress now
                madeProgress = true;
            }

        } while (madeProgress);

        return validateSolve();
    }

    private Boolean validateSolve() {
        try {
            for (int r = 0; r < 9; r += 3) {
                for (int c = 0; c < 9; c += 3) {
                    if (scanSquare(r, c).length != 0) {
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

        boolean hasPossibilitiesBeenRemoved = false;
        int[][][] squares = getSquaresPossibilitiesArray(); //TODO is it worth recalculating this, sort and copy after every discovery[...]
        // or is it more efficient to get less progress here and rely more on algorithm 1&2 figuring out numbers?

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

            for (int numToCheck = 0; numToCheck < 9; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(squareRowPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int rowToAffect = SodukuCoordUtils.squareNrAndPosToRow(sq, index * 3);
                int firstColInSq = SodukuCoordUtils.topLeftCol(SodukuCoordUtils.squareNrAndPosToCol(sq, 0));
                int[] columnsToAffect = new int[6];
                switch (firstColInSq) {
                    case 0: columnsToAffect = new int[]{3, 4, 5, 6, 7, 8};
                        break;
                    case 3: columnsToAffect = new int[]{0, 1, 2, 6, 7, 8};
                        break;
                    case 6: columnsToAffect = new int[]{0, 1, 2, 3, 4, 5};
                        break;
                }
                for (int i : columnsToAffect) {
                    removePossibilityCell(rowToAffect, i, numToCheck);
                }
                hasPossibilitiesBeenRemoved = true;
            }

            // type 1 column

            ArrayList<Integer> left = ListAndArrayUtils.intArrayToIntegerList(squares[0][sq]);
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[1][sq]));
            left.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[2][sq]));

            ArrayList<Integer> center = ListAndArrayUtils.intArrayToIntegerList(squares[3][sq]);
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[4][sq]));
            center.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[5][sq]));

            ArrayList<Integer> right = ListAndArrayUtils.intArrayToIntegerList(squares[6][sq]);
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[7][sq]));
            right.addAll(ListAndArrayUtils.intArrayToIntegerList(squares[8][sq]));

            int[][] squareColPossibilities = new int[3][];
            squareColPossibilities[0] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(left));
            squareColPossibilities[1] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(center));
            squareColPossibilities[2] = ListAndArrayUtils.integerListToIntArray(ListAndArrayUtils.sortUnique(right));

            for (int numToCheck = 0; numToCheck < 9; numToCheck++) {
                int index = ListAndArrayUtils.singlePossibleFinder(squareColPossibilities, numToCheck);
                if (index == -1) {
                    continue;
                }
                int colToAffect = SodukuCoordUtils.squareNrAndPosToRow(sq, index * 3);
                int firstRowInSq = SodukuCoordUtils.topLeftRow(SodukuCoordUtils.squareNrAndPosToRow(sq, 0));
                int[] rowsToAffect = new int[6];
                switch (firstRowInSq) {
                    case 0: rowsToAffect = new int[]{3, 4, 5, 6, 7, 8};
                        break;
                    case 3: rowsToAffect = new int[]{0, 1, 2, 6, 7, 8};
                        break;
                    case 6: rowsToAffect = new int[]{0, 1, 2, 3, 4, 5};
                        break;
                }
                for (int i : rowsToAffect) {
                    removePossibilityCell(colToAffect, i, numToCheck);
                }
                hasPossibilitiesBeenRemoved = true;
            }
        }

        // type 2
        ArrayList<Integer> indexes;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                indexes = ListAndArrayUtils.findInside2dArray(everythingPossible[i], j);
                if (! indexes.isEmpty() || indexes.size() > 3) { // j does not appear on row or cant be limited to one square
                    j += 0;
                }
            }
        }


        return hasPossibilitiesBeenRemoved;
    }

}

package main.java.SodukuUtils;

import java.util.ArrayList;

import static main.java.Utils.ListAndArrayUtils.intArrayToIntegerList;

/**
 * A class for easier debugging contents of everythingPossible by printing it to System.err
 * Use together with a editor with mono spacing like Notepad and place a dump before and one after side by side.
 */
public class PossibilityMegadump {
    public static void dumpPossibilities(int[][][] everythingPossible, int[][] playfield) {
        try {
            for (int row = 0; row < 9; row++) {
                for (int cellPart = 0; cellPart < 3; cellPart++) {
                    for (int col = 0; col < 9; col++) {
                        ArrayList<Integer> possibilityCell = intArrayToIntegerList(everythingPossible[row][col]);
                        for (int i = cellPart * 3 + 1; i < cellPart * 3 + 4; i++) {
                            if (playfield[row][col] > 0) {
                                System.err.print(playfield[row][col]);
                            } else {
                                if (possibilityCell.contains(i)) {
                                    System.err.print(i);
                                } else {
                                    System.err.print("*");
                                }
                            }
                        }
                        if (col == 2 || col == 5) {
                            System.err.print("  |  ");
                        } else {
                            System.err.print("  ");
                        }
                    }
                    System.err.print("\n");
                }
                if (row == 2 || row == 5) {
                    System.err.print("\n-------------------------------------------------\n\n");
                } else {
                    System.err.print("\n");
                }
            }
            System.err.println("\n\n\n");
        } catch (Exception e) {
            System.err.println("Here is a cookie!");
        }

    }
}

package main.java.SodukuUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * NumSeen is a class to keep track of witch of the numbers between 1 and 9 that has been 'seen' in the part of
 * the soduku grid currently of interest. All numbers are on creation flagged unseen
 */
public class NumSeen {
    private List<Integer> seen;
    private List<Integer> unseen;

    /**
     * Constructor
     */
    public NumSeen() {
        seen = new ArrayList<>();
        unseen = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            unseen.add(i);
    }

    /**
     * Flags the given integer as seen and removes it from unseen but only
     * if it is between 1 and 9, else does nothing
     * @param i: integer to add to seen
     */
    public void makeSeen(Integer i) {
        if (i > 0 && i < 10) {
            unseen.remove(i);
            if (!seen.contains(i))
                seen.add(i);
        }
    }

    /**
     * Flags all numbers between 1 and 9 as seen and empties the unseen list
     */
    public void makeAllSeen() {
        seen.clear();
        unseen.clear();
        for (int i = 1; i < 10; i++)
            seen.add(i);
    }

    /**
     * Flags the given integer as unseen and removes if from seen but only
     * if it is between 1 and 9 , else foes nothing
     * @param i: integer to add to unseen
     */
    public void makeUnseen(Integer i) {
        seen.remove(i);
        if( !unseen.contains(i))
            unseen.add(i);
    }

    /**
     * Flags all numbers between 1 and 9 as unseen and emties the seen list
     */
    public void makeAllUnseen() {
        seen.clear();
        unseen.clear();
        for (int i = 1; i < 10; i++)
            unseen.add(i);
    }

    /**
     *
     * @return: a int array containing the numbers that is currently in the seen list
     */
    public int[] getSeen() {
        int[] ret = new int[seen.size()];
        for (int i  = 0; i < seen.size(); i++){
            ret[i] = seen.get(i);
        }
        return ret;
    }

    /**
     *
     * @return: a int array containing the numbers that is currently in the unseen list
     */
    public int[] getUnseen() {
        int[] ret = new int[unseen.size()];
        for (int i  = 0; i < unseen.size(); i++){
            ret[i] = unseen.get(i);
        }
        return ret;
    }
}

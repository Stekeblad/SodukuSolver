package main.java.Utils;

import java.util.ArrayList;
import java.util.Collections;

public class ListAndArrayUtils {
    /**
     * Converts a ArrayList of Integers to a int[]. The index and value of all element will be the same.
     *
     * @param list a ArrayList of Integers to convert
     * @return a int[] of the same size and with the same number in the same order as list
     */
    public static int[] integerListToIntArray(ArrayList<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i  = 0; i < list.size(); i++){
            ret[i] = list.get(i);
        }
        return ret;
    }

    /**
     * Converts a int[] to a ArrayList of Integers. The index and value of all elements will be the same.
     *
     * @param array a int[] to convert
     * @return a ArrayList of Integers of the same size and with the same numbers in the same order as array
     */
    public static ArrayList<Integer> intArrayToIntegerList(int[] array) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            ret.add(i, array[i]);
        }
        return ret;
    }

    /**
     * Removes the element at index excludeIndex and returns a new array.
     * If excludeIndex is smaller than 0 or greater than or equal to array.length the last element will be removed.
     *
     * @param array the array to remove a element from
     * @param excludeIndex the index of the element to remove
     * @return a copy of array but with the value at excludeIndex removed
     */
    public static int[] excludeIndex(int[] array, int excludeIndex) {
        int[] newArray = new int[array.length - 1];
        for (int i = 0, j = 0; i < newArray.length; i++, j++) {
            if (j == excludeIndex) {
                i--;
                continue;
            }
            newArray[i] = array[j];
        }
        return newArray;
    }

    /**
     * Removes all elements with the value excludeValue from array.
     * Safe to call even if excludeValue does not appear in array.
     *
     * @param array the array to remove element(s) from
     * @param excludeValue the value of the element(s) to remove
     * @return a copy of array but with all occurrences of excludeValue removed
     */
    public static int[] excludeValue(int[] array, int excludeValue) {
        ArrayList<Integer> newArray = new ArrayList<>();
        for (int i = 0, j = 0; j < array.length; j++) {
            if (array[j] == excludeValue) {
                continue;
            }
            newArray.add(i, array[j]);
            i++;
        }
        return integerListToIntArray(newArray);
    }

    /**
     * Searches through a two-dimensional int array after a number and if the number only appears in one of the sub-arrays
     * the index of that array is returned, else -1 is returned.
     *
     * @param array two-dimensional int array to analyse
     * @param i number to find single occurrence of
     * @return The index of the only inner array that contains i or -1 if i occurs in no or more than one array.
     */
    public static int singlePossibleFinder(int[][] array, int i) {
        if (!isInMultiple(array, i)) {
            for (int j = 0; j < array.length; j++) {
                if (array[j] != null) {
                    for (int k : array[j]) {
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
    public static boolean isInMultiple(int[][] array, int numToTest) {
        int timesSeen = 0;
        for (int[] innerArray : array) {
            if (innerArray != null) {
                for (int number : innerArray) {
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

    /**
     * First sorts list (smallest first) then removes all doublets from list and returns the results.
     *
     * @param list a list to sort and remove multiples from
     * @return a sorted version of list there all elements from list only appear once.
     */
    public static ArrayList<Integer> sortUnique(ArrayList<Integer> list) {
        Collections.sort(list);
        int counter = 1;
        while(counter < list.size()) {
            if (list.get(counter - 1).equals(list.get(counter))) {
                list.remove(counter);
            } else {
                counter++;
            }
        }
        return list;
    }

    public static boolean arrayContains(int[] array, int numToTest) {
        for (int a : array) {
            if (a == numToTest) {
                return true;
            }
        }
        return false;
    }
}

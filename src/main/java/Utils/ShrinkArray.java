package main.java.Utils;

import java.util.ArrayList;

public class ShrinkArray {
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
        return ListArrayConverter.integerListToIntArray(newArray);
    }
}

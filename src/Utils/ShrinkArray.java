package Utils;

import java.util.ArrayList;
import java.util.List;

public class ShrinkArray {
    /**
     * Removes the element at index excludeIndex and returns a new array
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
     * Removes all elements with the value excludeValue from array
     * @param array the array to remove element(s) from
     * @param excludeValue the value of the element(s) to remove
     * @return a copy of array but with all occurrences of excludeValue removed
     */
    public static int[] excludeValue(int[] array, int excludeValue) {
        List<Integer> newArray = new ArrayList<>();
        for (int i = 0, j = 0; j < array.length; i++, j++) {
            if (array[j] == excludeValue) {
                i--;
                continue;
            }
            newArray.add(i, array[j]);
        }
        return ListArrayConverter.integerListToIntArray(newArray);
    }
}

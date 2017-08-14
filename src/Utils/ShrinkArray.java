package Utils;

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
     * Removes all elements with the value excludeValue but ASSUMES ONLY ONE OCCURRENCE OF IT as the size will be
     * one smaller then the original array. With more then one occurrence of excludeValue one or more uninitialized
     * values will be left at the end
     * @param array the array to remove a element from
     * @param excludeValue the value of the element to remove
     * @return a copy of array but with the first occurrence of excludeValue removed
     */
    public static int[] excludeValue(int[] array, int excludeValue) {
        int[] newArray = new int[array.length - 1];
        for (int i = 0, j = 0; i < newArray.length; i++, j++) {
            if (array[j] == excludeValue) {
                i--;
                continue;
            }
            newArray[i] = array[j];
        }
        return newArray;
    }
}

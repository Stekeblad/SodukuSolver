package main.java.Utils;

import java.util.ArrayList;

public class ListArrayConverter {
    public static int[] integerListToIntArray(ArrayList<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i  = 0; i < list.size(); i++){
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static ArrayList<Integer> intArrayToIntegerList(int[] array) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            ret.add(i, array[i]);
        }
        return ret;
    }
}

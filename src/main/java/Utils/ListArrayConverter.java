package main.java.Utils;

import java.util.List;

public class ListArrayConverter {
    public static int[] integerListToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i  = 0; i < list.size(); i++){
            ret[i] = list.get(i);
        }
        return ret;
    }
}

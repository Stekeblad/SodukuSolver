package Utils;

import java.util.List;

public class ListToArray {
    public static int[] integerListToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i  = 0; i < list.size(); i++){
            ret[i] = list.get(i);
        }
        return ret;
    }
}

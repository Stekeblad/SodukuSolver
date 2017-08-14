package Utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ListArrayConverter {
    public static int[] integerListToIntArray(List<Integer> list) {
        int[] ret = new int[list.size()];
        for (int i  = 0; i < list.size(); i++){
            ret[i] = list.get(i);
        }
        return ret;
    }

    public static ObservableList<Integer> intArrayToObservableIntegerList(int[] intArr) {
        ObservableList<Integer> obsList = FXCollections.observableArrayList();
        for (int anIntArr : intArr) {
            obsList.add(anIntArr);
        }
        return obsList;
    }

    public static int[] ObservableIntegerListToIntArray(ObservableList<Integer> obsList) {

        int[] intArray = new int[obsList.size()];
        for (int i = 0; i < obsList.size(); i++) {
            intArray[i] = obsList.get(i);
        }
        return intArray;
    }
}

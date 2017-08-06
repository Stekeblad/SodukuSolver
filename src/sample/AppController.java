package sample;

import SodukuUtils.SodukuLoader;
import Utils.ListArrayConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;


public class AppController {

    public Button buttonDefaultSoduku;
    public Button solve;
    public Button tst;
    public CheckBox checkboxDebug;
    public GridPane grid11;
    public Label text_solving;
    public ListView<Integer> listUnseenCol;
    public ListView<Integer> listUnseenRow;
    public ListView<Integer> listUnseenSq;
    public ListView<String> listDefaultSoduku;
    public ProgressIndicator progressSolving;
    public TextField checkResult;
    public TextField out;

    private SodukuSolver sodukuSolver = new SodukuSolver();

    @FXML
    void CreatePlayfield() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                String id = "cell_" + r + c;
                TextField newField = new TextField();
                newField.setId(id);
                newField.setMaxWidth(30);
                newField.setAlignment(Pos.CENTER);
                newField.setOnMouseClicked(this::showDebugData);
                grid11.add(newField, c, r);
            }
        }
        tst.setDisable(false);
        solve.setDisable(true);

        //Populate default soduku boards and enable load button
        ObservableList<String> obsList = FXCollections.observableArrayList();
        obsList.add("easy_1");
        listDefaultSoduku.setItems(obsList);
        buttonDefaultSoduku.setDisable(false);
    }

    public void CheckCells(ActionEvent actionEvent) {
        checkResult.setText("");
        for (Node node : grid11.getChildren()) {
            TextField tf = (TextField) node;
            if (tf.getText().isEmpty()) {
                String id = tf.getId();
                continue;
            }
            if (tf.getText().length() != 1 || !tf.getText().matches("[0-9]")) {
                checkResult.setText(tf.getId() + " Invalid content");
                solve.setDisable(true);
                return;
            }
        }
        solve.setDisable(false);
    }

    private void updatePlayfield() {
        int [][] playfield = sodukuSolver.getPlayfield();
        for (Node node : grid11.getChildren()) {
            TextField tf = (TextField) node;
            String id = node.getId().substring(5);
            int r = Character.getNumericValue(id.charAt(0));
            int c = Character.getNumericValue(id.charAt(1));
            tf.setText(Integer.toString(playfield[r][c]));
        }
    }

    public void solve(ActionEvent actionEvent) {
        StopWatch timer = new StopWatch();
        boolean res = false;
        progressSolving.setVisible(true);
        text_solving.setVisible(true);
        timer.start();
        try {
            res = sodukuSolver.solve();
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer.stop();
        if (!res) {
            checkResult.setText("Solve Failed");
        } else {
            checkResult.setText("Solve Successful");
        }
        progressSolving.setVisible(false);
        text_solving.setVisible(false);
        out.setText("Done in " + timer.getTime(TimeUnit.MICROSECONDS) + " micro seconds");
        updatePlayfield();
    }

    private void showDebugData(MouseEvent mouseEvent) {
        if (checkboxDebug.isSelected()) {
            TextField source = (TextField) mouseEvent.getSource();

            String id = source.getId().substring(5);
            int r = Character.getNumericValue(id.charAt(0));
            int c = Character.getNumericValue(id.charAt(1));

            ObservableList<Integer> obsListSq = ListArrayConverter.intArrayToObservableIntegerList(
                    sodukuSolver.getUnseenForSquare(r, c));
            listUnseenSq.setItems(obsListSq);

            ObservableList<Integer> obsListRow = ListArrayConverter.intArrayToObservableIntegerList(
                    sodukuSolver.getUnseenForRow(r));
            listUnseenRow.setItems(obsListRow);

            ObservableList<Integer> obsListCol = ListArrayConverter.intArrayToObservableIntegerList(
                    sodukuSolver.getUnseenForCol(c));
            listUnseenCol.setItems(obsListCol);
        }
    }

    public void loadDefaultSoduku(ActionEvent actionEvent) {
            String defaultName = listDefaultSoduku.getSelectionModel().getSelectedItem();
            sodukuSolver.setPlayfield(SodukuLoader.loadSoduku(defaultName));
            updatePlayfield();
            CheckCells(new ActionEvent());

    }
}

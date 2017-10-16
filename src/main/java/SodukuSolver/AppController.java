package main.java.SodukuSolver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.java.SodukuUtils.CoordToSquareNr;
import main.java.SodukuUtils.SodukuLoader;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;

public class AppController {

    public Button buttonDefaultSoduku;
    public Button create;
    public Button solve;
    public GridPane grid11;
    public Label text_solving;
    public ListView<String> listDefaultSoduku;
    public ProgressIndicator progressSolving;
    public TextField checkResult;
    public TextField out;

    private SodukuSolver sodukuSolver = new SodukuSolver();
    private boolean isBoardCreated = false;

    @FXML
    void CreatePlayfield() {
        checkResult.setText("");
        out.setText("");

        if(!isBoardCreated) {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    String id = "cell_" + r + c;
                    TextField newField = new TextField();
                    newField.setId(id);
                    newField.setMaxWidth(30);
                    newField.setAlignment(Pos.CENTER);
                    grid11.add(newField, c, r);
                }
            }
            // Populate default soduku boards and enable load button
            // This is a bad solution but it is used for testing and not for user save/load
            // May change in the future
            ObservableList<String> obsList = FXCollections.observableArrayList();
            obsList.add("easy_1");
            obsList.add("vhard_1");
            obsList.add("\"the most difficult\"");
            listDefaultSoduku.setItems(obsList);
        } else {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    String id = "cell_" + r + c;
                    TextField tf = (TextField) grid11.lookup("#" + id);
                    tf.setText("");
                }
            }
        }
        buttonDefaultSoduku.setDisable(false);
        solve.setDisable(false);
        isBoardCreated = true;
    }

    private boolean CheckCells() {
        int[][] sodukuGrid = new int[9][9];
        checkResult.setText("");
        out.setText("");
        boolean seenInRow[][] = new boolean[9][10];
        boolean seenInCol[][] = new boolean[9][10];
        boolean seenInSq[][] = new boolean[9][10];

        // First I iterated over all cells with a for-each loop but after adding checks if a number already is in a
        // row/column/square It broke and iterated over elements more then once.
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String id = "cell_" + row + col;
                TextField tf = (TextField) grid11.lookup("#" + id);

                if (tf.getText().isEmpty()) {
                    sodukuGrid[row][col] = 0;
                    continue;
                }
                if (tf.getText().length() > 1 || (!tf.getText().equals("") && !tf.getText().equals(" ") && !tf.getText().matches("[0-9]"))) {
                    checkResult.setText(id + " Invalid content");
                    return false;
                } else {
                    int number = Character.getNumericValue(tf.getText().charAt(0));
                    int sq = CoordToSquareNr.coordToSquarenr(row, col);
                    if(number != 0 && (seenInCol[col][number] || seenInRow[row][number] || seenInSq[sq][number])) {
                        checkResult.setText(id + " the number " + number + " already seen in this row, column or square");
                        return false;
                    }

                    seenInCol[col][number] = true;
                    seenInRow[row][number] = true;
                    seenInSq[sq][number] = true;
                    sodukuGrid[row][col] = number;
                }
            }
        }
        sodukuSolver.setPlayfield(sodukuGrid);
        return true;
    }

    private void updatePlayfield() {
        int [][] playfield = sodukuSolver.getPlayfield();
        for (Node node : grid11.getChildren()) {
            TextField tf = (TextField) node;
            String id = node.getId().substring(5);
            int r = Character.getNumericValue(id.charAt(0));
            int c = Character.getNumericValue(id.charAt(1));
            String cellContent = Integer.toString(playfield[r][c]);
            if (cellContent.equals("0")) {
                tf.setText("");
            } else {
                tf.setText(cellContent);
            }
        }
    }

    public void solve(ActionEvent actionEvent) {
        checkResult.setText("");
        out.setText("");
        if (! CheckCells()) {
            actionEvent.consume();
            return;
        }

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
        actionEvent.consume();
    }

    public void loadDefaultSoduku(ActionEvent actionEvent) {
        checkResult.setText("");
        out.setText("");
        String defaultName = listDefaultSoduku.getSelectionModel().getSelectedItem();
        sodukuSolver.setPlayfield(SodukuLoader.loadSoduku(defaultName));
        updatePlayfield();
        actionEvent.consume();
    }
}

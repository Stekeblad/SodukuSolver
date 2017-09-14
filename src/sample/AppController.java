package sample;

import SodukuUtils.SodukuLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
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

    @FXML
    void CreatePlayfield() {
        checkResult.setText("");
        out.setText("");
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
        // This is a bad solution, I attempted using files but gave up.
        // Only using this for testing for now
        ObservableList<String> obsList = FXCollections.observableArrayList();
        obsList.add("easy_1");
        obsList.add("vhard_1");
        obsList.add("\"the most difficult\"");
        listDefaultSoduku.setItems(obsList);
        buttonDefaultSoduku.setDisable(false);
    }

    private boolean CheckCells() {
        int[][] sodukuGrid = new int[9][9];
        checkResult.setText("");
        out.setText("");
        for (Node node : grid11.getChildren()) {
            TextField tf = (TextField) node;
            if (tf.getText().isEmpty()) {
                String id = tf.getId();
                sodukuGrid[Character.getNumericValue(id.charAt(5))][Character.getNumericValue(id.charAt(6))] = 0;
                continue;
            }
            if (tf.getText().length() > 1 || (!tf.getText().equals("") && !tf.getText().equals(" ") && !tf.getText().matches("[0-9]"))) {
                checkResult.setText(tf.getId() + " Invalid content");
                return false;
            } else {
                String id = tf.getId();
                sodukuGrid[Character.getNumericValue(id.charAt(5))]
                        [Character.getNumericValue(id.charAt(6))] = Character.getNumericValue(tf.getText().charAt(0));
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

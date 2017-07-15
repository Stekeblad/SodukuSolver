package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.time.StopWatch;

public class AppController {

    public GridPane grid11;
    public TextField out;
    public Button tst;
    public TextField checkResult;
    public Button solve;
    public Label text_solving;
    public ProgressIndicator prog_solving;

    private SodukuSolver sodukuSolver;

    @FXML
    void CreatePlayfield() {
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
        tst.setDisable(false);
    }

    public void CheckCells(ActionEvent actionEvent) {
        int[][] sodukuGrid = new int[9][9];
        checkResult.setText("");
        for (Node node : grid11.getChildren()) {
            TextField tf = (TextField) node;
            if (tf.getText().isEmpty()) {
                String id = tf.getId();
                sodukuGrid[Character.getNumericValue(id.charAt(5))][Character.getNumericValue(id.charAt(6))] = 0;
                continue;
            }
            if (tf.getText().length() != 1 || !tf.getText().matches("[0-9]")) {
                checkResult.setText(tf.getId() + " Invalid content");
                solve.setDisable(true);
                return;
            } else {
                String id = tf.getId();
                sodukuGrid[Character.getNumericValue(id.charAt(5))]
                        [Character.getNumericValue(id.charAt(6))] = Character.getNumericValue(tf.getText().charAt(0));
            }
        }
        solve.setDisable(false);
        sodukuSolver = new SodukuSolver(sodukuGrid);
        StopWatch timer = new StopWatch();
        prog_solving.setVisible(true);
        text_solving.setVisible(true);
        timer.start();
        boolean res = sodukuSolver.solve();
        timer.stop();
        prog_solving.setVisible(false);
        text_solving.setVisible(false);
        out.setText("Done in " + timer.getTime() + " milli seconds");
        updatePlayfield();
    }

    public void updatePlayfield() {
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

    }
}

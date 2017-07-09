package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.time.StopWatch;

public class AppController {

    public GridPane grid11;
    public TextField out;
    public Button tst;
    public TextField checkResult;
    public Button solve;

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
                sodukuGrid[(int)id.charAt(5)][(int)id.charAt(6)] = 0;
                continue;
            }
            if (tf.getText().length() != 1 || !tf.getText().matches("[1-9]")) {
                checkResult.setText(tf.getId() + " Invalid content");
                solve.setDisable(true);
                return;
            } else {
                String id = tf.getId();
                sodukuGrid[(int)id.charAt(5)][(int)id.charAt(6)] = (int)tf.getText().charAt(0);
            }
        }
        solve.setDisable(false);
        sodukuSolver = new SodukuSolver(sodukuGrid);
        StopWatch timer = new StopWatch();
        timer.start();
        sodukuSolver.solve();
        timer.stop();
        out.setText("Done in " + timer.getTime() + " milli seconds");
    }

    public void solve(ActionEvent actionEvent) {

    }
}

package chess;

import chess.model.Chessman;
import chess.model.Node;
import chess.util.DataReader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private GridPane table;

    private Map<String, String> state;

    private Map<String, String> names;

    private javafx.scene.Node checkedNode;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        state = DataReader.readModel("data/DefaultState.data");
        names = DataReader.readModel("data/Names.data");

        if (state == null || names == null) {
            System.out.println("Error in model loading has occurred");
            return;
        }

        if (table != null) {
            int numRows = table.getRowCount();
            int numCols = table.getColumnCount();

            for (int i = 0 ; i < numCols ; i++) {
                for (int j = 0; j < numRows; j++) {
                    addPane(i, j);
                }
            }
        }
    }

    private void addPane(int colIndex, int rowIndex) {
        Pane pane = spawnChessman(colIndex, rowIndex);

        pane.setOnMouseClicked(e -> {
            Pane background;
            if (checkedNode == null) {
                checkedNode = (javafx.scene.Node) e.getSource();
                System.out.printf("Mouse clicked cell [%d, %d]%n", colIndex, rowIndex);
                background = (Pane) ((Node) checkedNode.getUserData()).getBackgroundNode(table, "square", colIndex, rowIndex);
                background.setStyle(background.getStyle() + "-fx-border-color: chocolate;");
                if (((Node) checkedNode.getUserData()).getChessman() != null) {
                    System.out.println(((Node) checkedNode.getUserData()).getChessman().getName() + " " +
                                       ((Node) checkedNode.getUserData()).getChessman().getColor());
                }
                else {
                    System.out.println("No chessman on this place");
                }
            }
            else {
                background = (Pane) ((Node) checkedNode.getUserData()).getBackgroundNode(table, "square", null, null);
                background.setStyle(background.getStyle().replace("-fx-border-color: chocolate;", ""));
                if (checkedNode != e.getSource()) {
                    Chessman swap = ((Node) ((Pane) e.getSource()).getUserData()).getChessman();
                    ((Node) ((Pane) e.getSource()).getUserData()).setChessman(((Node) checkedNode.getUserData()).getChessman());
                    ((Node) checkedNode.getUserData()).setChessman(swap);

                    ((Pane) e.getSource()).getChildren().addAll(((Pane) checkedNode).getChildren());
                    ((Pane) checkedNode).getChildren().removeAll();
                }
                checkedNode = null;
            }
        });

        Pane square = new StackPane();

        square.getStyleClass().add("square");
        square.setStyle(square.getStyle() + "-fx-border-width: 3px;");
        if ((colIndex + rowIndex) % 2 == 0) square.setStyle(square.getStyle() + "-fx-background-color: sandybrown;");
        else square.setStyle(square.getStyle() + "-fx-background-color: saddlebrown;");

        table.add(square, colIndex, rowIndex);
        table.add(pane, colIndex, rowIndex);
    }

    private Pane spawnChessman(int colIndex, int rowIndex) {
        String chessmanCode = state.get(Character.toString((char) (colIndex + 65)) + (rowIndex + 1));
        Chessman chessman = null;
        Text text = new Text();
        Pane movable = new StackPane();

        if (chessmanCode != null) {
            var code = chessmanCode.split(",");
            String name = names.get(code[0]);
            chessman = new Chessman(name, code[1], chessmanCode);

            text.setText(name);
            text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
            if (code[1].equals("W")) {
                text.setFill(Color.WHITE);
                text.setStyle(text.getStyle() + "-fx-stroke: black;");
            }
            else if (code[1].equals("B")) {
                text.setFill(Color.BLACK);
                text.setStyle(text.getStyle() + "-fx-stroke: white;");
            }

            movable.getChildren().add(text);
            StackPane.setAlignment(text, Pos.CENTER);
        }
        movable.setUserData(new Node(chessman, false, colIndex, rowIndex));

        return movable;
    }
}

module chessai {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens chess to javafx.fxml;
    exports chess;

    opens tictactoe to javafx.fxml;
    exports tictactoe;
}
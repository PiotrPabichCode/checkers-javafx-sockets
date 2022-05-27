module com.example.checkersjavafxsockets {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.checkersjavafxsockets to javafx.fxml;
    exports com.example.checkersjavafxsockets;
    exports com.example.checkersjavafxsockets.Server;
    opens com.example.checkersjavafxsockets.Server to javafx.fxml;
    exports com.example.checkersjavafxsockets.Game;
    opens com.example.checkersjavafxsockets.Game to javafx.fxml;
}
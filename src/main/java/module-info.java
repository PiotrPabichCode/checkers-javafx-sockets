module com.example.checkersjavafxsockets {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.checkersjavafxsockets to javafx.fxml;
    exports com.example.checkersjavafxsockets;
}
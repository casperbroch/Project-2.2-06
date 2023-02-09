module com.mda {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mda to javafx.fxml;
    exports com.mda;
}

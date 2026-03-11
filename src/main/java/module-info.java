module org.example.projekat2025 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.fasterxml.jackson.databind;

    opens org.example.projekat2025.controller to javafx.fxml, javafx.base;
    opens org.example.projekat2025 to javafx.fxml, javafx.base;

    exports org.example.projekat2025;
}
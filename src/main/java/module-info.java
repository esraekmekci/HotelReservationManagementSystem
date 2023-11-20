module com.upodotel.upodotel {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.upodotel.hotelmanagementsystem to javafx.fxml;
    exports com.upodotel.hotelmanagementsystem;
    exports com.upodotel.hotelmanagementsystem.controllers;
    opens com.upodotel.hotelmanagementsystem.controllers to javafx.fxml;
    exports com.upodotel.hotelmanagementsystem.entities;
    opens com.upodotel.hotelmanagementsystem.entities to javafx.fxml;
}
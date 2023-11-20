package com.upodotel.hotelmanagementsystem.controllers;

import com.upodotel.hotelmanagementsystem.DBUtils;
import com.upodotel.hotelmanagementsystem.entities.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class EditServiceController implements Initializable {
    @FXML
    private TextField tf_service_name;
    @FXML
    private TextField tf_unit_price;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;
    private Service service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatFields();
        btn_save.setOnAction(event -> {
            if (tf_service_name.getText().isEmpty() || tf_unit_price.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill in all required fields.");
                alert.show();
            } else {
                DBUtils.editService(
                        service.getServiceId(),
                        tf_service_name.getText(),
                        Float.parseFloat(tf_unit_price.getText()));

                DBUtils.changeScene(event, "services.fxml", "Services", null);
            }
        });
        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "services.fxml", "Services", null));
    }

    public void formatFields() {
        tf_unit_price.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("[0-9.]")) {
                event.consume(); // Accepts only digits
            }
        });
    }

    public void setData(Object serviceObj) {
        this.service = (Service) serviceObj;
        tf_service_name.setText(service.getServiceName());
        tf_unit_price.setText(String.valueOf(service.getUnitPrice()));
    }
}

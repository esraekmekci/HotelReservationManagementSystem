package com.upodotel.hotelmanagementsystem.controllers;

import com.upodotel.hotelmanagementsystem.DBUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class NewFeatureController implements Initializable {

    @FXML
    private TextField tf_feature_name;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btn_save.setOnAction(event -> {
            if (tf_feature_name.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill in all required fields.");
                alert.show();
            } else {
                DBUtils.createFeature(
                        tf_feature_name.getText());

                DBUtils.changeScene(event, "features.fxml", "Features", null);
            }
        });

        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "features.fxml", "Features", null));
    }
}

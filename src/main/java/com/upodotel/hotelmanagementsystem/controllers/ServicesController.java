package com.upodotel.hotelmanagementsystem.controllers;

import com.upodotel.hotelmanagementsystem.DBUtils;
import com.upodotel.hotelmanagementsystem.entities.Service;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ServicesController implements Initializable {
    @FXML
    private TableView<Service> tv_service_table;
    @FXML
    private TableColumn<Service, String> tc_service_name;
    @FXML
    private TableColumn<Service, Float> tc_unit_price;
    @FXML
    private Button btn_new_service;
    @FXML
    private Button btn_edit_service;
    @FXML
    private Button btn_delete_service;
    @FXML
    private Button btn_return_reservations;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showServices();

        tv_service_table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Service>) c -> {
            // No row is selected, disable the button
            // At least one row is selected, enable the button
            btn_edit_service.setDisable(c.getList().isEmpty());
            btn_delete_service.setDisable(c.getList().isEmpty());
        });

        btn_edit_service.setOnAction(event -> {
            Service selectedService = tv_service_table.getSelectionModel().getSelectedItem();
            DBUtils.changeScene(event, "edit_service.fxml", "Edit Service", selectedService);
        });

        btn_delete_service.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete this service?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    DBUtils.deleteRow("services", "service_id", tv_service_table.getSelectionModel().getSelectedItem().getServiceId());
                    showServices();
                }
            });

        });

        btn_new_service.setOnAction(event ->
                DBUtils.changeScene(event, "new_service.fxml", "Create New Service", null));

        btn_return_reservations.setOnAction(event ->
                DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void showServices() {
        tc_service_name.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        tc_unit_price.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        ObservableList<Service> observableCustomers = DBUtils.getServices();
        tv_service_table.setItems(observableCustomers);
    }
}

package com.upodotel.hotelmanagementsystem.controllers;

import com.upodotel.hotelmanagementsystem.DBUtils;
import com.upodotel.hotelmanagementsystem.entities.Customer;
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
import java.util.Date;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    @FXML
    private TableView<Customer> tv_customer_table;
    @FXML
    private TableColumn<Customer, String> tc_full_name;
    @FXML
    private TableColumn<Customer, String> tc_identity_number;
    @FXML
    private TableColumn<Customer, String> tc_phone_number;
    @FXML
    private TableColumn<Customer, Date> tc_birth_date;
    @FXML
    private TableColumn<Customer, String> tc_description;
    @FXML
    private Button btn_new_customer;
    @FXML
    private Button btn_edit_customer;
    @FXML
    private Button btn_delete_customer;
    @FXML
    private Button btn_return_reservations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showCustomers();

        tv_customer_table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Customer>) c -> {
            // No row is selected, disable the button
            // At least one row is selected, enable the button
            btn_edit_customer.setDisable(c.getList().isEmpty());
            btn_delete_customer.setDisable(c.getList().isEmpty());
        });

        btn_delete_customer.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete this customer?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    DBUtils.deleteRow("customers", "id", tv_customer_table.getSelectionModel().getSelectedItem().getId());
                    showCustomers();
                }
            });
        });

        btn_edit_customer.setOnAction(event -> {
            Customer selectedCustomer = tv_customer_table.getSelectionModel().getSelectedItem();
            DBUtils.changeScene(event, "edit_customer.fxml", "Edit Customer", selectedCustomer);
        });

        btn_new_customer.setOnAction(event -> DBUtils.changeScene(event, "new_customer.fxml", "Create New Customer", null));

        btn_return_reservations.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void showCustomers() {
        tc_full_name.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tc_identity_number.setCellValueFactory(new PropertyValueFactory<>("identityNumber"));
        tc_phone_number.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        tc_birth_date.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tc_description.setCellValueFactory(new PropertyValueFactory<>("description"));
        ObservableList<Customer> observableCustomers = DBUtils.getCustomers();
        tv_customer_table.setItems(observableCustomers);
    }
}

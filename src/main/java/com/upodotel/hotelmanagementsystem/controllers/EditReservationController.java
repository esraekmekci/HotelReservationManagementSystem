package com.upodotel.hotelmanagementsystem.controllers;


import com.upodotel.hotelmanagementsystem.DBUtils;
import com.upodotel.hotelmanagementsystem.entities.Customer;
import com.upodotel.hotelmanagementsystem.entities.Reservation;
import com.upodotel.hotelmanagementsystem.entities.Room;
import com.upodotel.hotelmanagementsystem.entities.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class EditReservationController implements Initializable {
    String selectedRoomName = "";
    @FXML
    private ChoiceBox<String> cb_select_room;
    @FXML
    private MenuButton mb_select_customers;
    @FXML
    private DatePicker dp_check_in_date;
    @FXML
    private DatePicker dp_check_out_date;
    @FXML
    private ChoiceBox<String> cb_checked_in;
    @FXML
    private ChoiceBox<String> cb_checked_out;
    @FXML
    private Button btn_cancel;
    @FXML
    private Button btn_save;
    @FXML
    private Pane pane_services;
    private int roomCapacity = -1;
    private GridPane gridPane;
    private Reservation reservation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        arrangeRoomNames();
        arrangeCustomerNames();
        arrangeChoiceBoxes();
        createServiceFields();

        cb_select_room.setOnAction(event -> {
            String selectedRoom = cb_select_room.getSelectionModel().getSelectedItem();
            roomCapacity = Integer.parseInt(String.valueOf(selectedRoom.charAt(selectedRoom.indexOf("customer") - 2)));
            selectedRoomName = selectedRoom.substring(0, selectedRoom.indexOf(" -"));
            mb_select_customers.getItems().forEach(checkMenuItem -> {
                ((CheckMenuItem) checkMenuItem).setSelected(false);
                checkMenuItem.setDisable(false);
            });
        });

        mb_select_customers.getItems().forEach(checkMenuItem -> checkMenuItem.setOnAction(event -> {
            // Disable unselected customers that exceed room capacity
            disableSelectionsExceedingCapacity(roomCapacity);
        }));

        btn_save.setOnAction(event -> {
            if (cb_select_room.getValue().equals("Select Room") ||
                    dp_check_in_date.getValue() == null || dp_check_out_date.getValue() == null) {
                // At least one required field is empty
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill in all required fields.");
                alert.show();
            } else if (getSelectedCustomerIdentityNumbers().size() != roomCapacity) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please select " + roomCapacity + " customer(s).");
                alert.show();
            } else if (dp_check_in_date.getValue().isAfter(dp_check_out_date.getValue())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Check-in date cannot be after check-out date.");
                alert.show();
            } else if (dp_check_in_date.getValue().isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Check-in date cannot be before today.");
                alert.show();
            } else {
                DBUtils.editReservation(
                        reservation.getReservationId(),
                        selectedRoomName,
                        java.sql.Date.valueOf(dp_check_in_date.getValue()),
                        java.sql.Date.valueOf(dp_check_out_date.getValue()),
                        cb_checked_in.getValue(),
                        cb_checked_out.getValue(),
                        getSelectedCustomerIdentityNumbers(),
                        getSelectedServices());
                DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null);
            }
        });

        btn_cancel.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void arrangeRoomNames() {
        for (Room room : DBUtils.getRooms()) {
            cb_select_room.getItems().add(room.getName() + " - capacity: " + room.getCapacity() + " customer(s)");
        }
        cb_select_room.setValue("Select Room");
    }

    private void arrangeCustomerNames() {
        for (Customer customer : DBUtils.getCustomers()) {
            CheckMenuItem checkMenuItem = new CheckMenuItem(customer.getFullName() + " - " + customer.getIdentityNumber());
            mb_select_customers.getItems().add(checkMenuItem);
        }
    }

    private void arrangeChoiceBoxes() {
        cb_checked_in.getItems().addAll("YES", "NO");
        cb_checked_in.setValue("NO");
        cb_checked_out.getItems().addAll("YES", "NO");
        cb_checked_out.setValue("NO");
    }

    private List<String> getSelectedCustomerIdentityNumbers() {
        List<String> customerList = new ArrayList<>();
        for (MenuItem menuItem : mb_select_customers.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
            if (checkMenuItem.isSelected()) {
                customerList.add(checkMenuItem.getText().substring(checkMenuItem.getText().indexOf("-") + 2));
            }
        }
        return customerList;
    }

    private void disableSelectionsExceedingCapacity(int roomCapacity) {
        for (MenuItem menuItem : mb_select_customers.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;

            // If too many customers are selected and the current one is not selected, disable it
            checkMenuItem.setDisable(roomCapacity > 0 && getSelectedCustomerIdentityNumbers().size() >= roomCapacity && !checkMenuItem.isSelected());
        }
    }

    private void createServiceFields() {
        gridPane = new GridPane();
        int row = 0;
        for (Service service : DBUtils.getServices()) {
            CheckBox checkbox = new CheckBox(service.getServiceName());
            checkbox.setTextFill(Color.WHITE);
            checkbox.setStyle("-fx-padding: 10 10 10 10;");
            checkbox.setFont(Font.font("Rockwell", 14));
            checkbox.setId("cb_service_" + row);
            gridPane.add(checkbox, 0, row);

            Label quantity = new Label("0");
            quantity.setTextFill(Color.WHITE);
            quantity.setStyle("-fx-padding: 10 10 10 10;");
            quantity.setFont(Font.font("Rockwell", 14));
            quantity.setAlignment(Pos.CENTER);
            quantity.setId("lbl_quantity_" + row);
            gridPane.add(quantity, 2, row);

            Button btn_minus = new Button("-");
            btn_minus.setStyle("-fx-text-fill: #4f0184; -fx-padding: 10 10 10 10;");
            btn_minus.setFont(Font.font("Rockwell", 14));
            btn_minus.setOnAction(event -> {
                int currentQuantity = Integer.parseInt(quantity.getText());
                if (currentQuantity > 0) {
                    currentQuantity--;
                    quantity.setText(String.valueOf(currentQuantity));
                }
            });
            gridPane.add(btn_minus, 1, row);

            Button btn_plus = new Button("+");
            btn_plus.setStyle("-fx-text-fill: #4f0184; -fx-padding: 10 10 10 10;");
            btn_plus.setFont(Font.font("Rockwell", 14));
            btn_plus.setOnAction(event -> {
                int currentQuantity = Integer.parseInt(quantity.getText());
                currentQuantity++;
                quantity.setText(String.valueOf(currentQuantity));
            });
            gridPane.add(btn_plus, 3, row);

            row++;
        }
        pane_services.getChildren().add(gridPane);
    }

    private Map<Service, Integer> getSelectedServices() {
        Map<Service, Integer> serviceMap = new HashMap<>();
        int i = 0;
        for (Service service : DBUtils.getServices()) {
            if (((CheckBox) gridPane.lookup("#cb_service_" + i)).isSelected()) {
                int quantity = Integer.parseInt(((Label) gridPane.lookup("#lbl_quantity_" + i)).getText());
                if (quantity > 0) {
                    serviceMap.put(service, quantity);
                }
            }
            i++;
        }
        return serviceMap;
    }

    public void setData(Object reservationObj) {
        this.reservation = (Reservation) reservationObj;

        for (String room : cb_select_room.getItems()) {
            if (room.contains(reservation.getRoomName())) {
                cb_select_room.getSelectionModel().select(room);
            }
        }

        cb_select_room.setValue(reservation.getRoomName());
        for (MenuItem menuItem : mb_select_customers.getItems()) {
            CheckMenuItem checkMenuItem = (CheckMenuItem) menuItem;
            for (Customer customer : reservation.getCustomers()) {
                if (checkMenuItem.getText().equals(customer.getFullName() + " - " + customer.getIdentityNumber())) {
                    checkMenuItem.setSelected(true);
                }
            }
        }
        dp_check_in_date.setValue(new java.sql.Date(reservation.getCheckInDate().getTime()).toLocalDate());
        dp_check_out_date.setValue(new java.sql.Date(reservation.getCheckOutDate().getTime()).toLocalDate());
        cb_checked_in.setValue(reservation.isCheckedIn());
        cb_checked_out.setValue(reservation.isCheckedOut());

        for (Map.Entry<Service, Integer> entry : reservation.getServices().entrySet()) {
            for (int i = 0; i < gridPane.getChildren().size(); i += 4) {
                if (gridPane.getChildren().get(i).getId().startsWith("cb_service_") && ((CheckBox) gridPane.getChildren().get(i)).getText().equals(entry.getKey().getServiceName())) {
                    ((CheckBox) gridPane.getChildren().get(i)).setSelected(true);
                    ((Label) gridPane.getChildren().get(i + 1)).setText(String.valueOf(entry.getValue()));
                }
            }
        }
    }

}

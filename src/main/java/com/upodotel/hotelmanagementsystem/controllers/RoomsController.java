package com.upodotel.hotelmanagementsystem.controllers;

import com.upodotel.hotelmanagementsystem.DBUtils;
import com.upodotel.hotelmanagementsystem.entities.Room;
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

public class RoomsController implements Initializable {
    @FXML
    private TableView<Room> tv_room_table;
    @FXML
    private TableColumn<Room, String> tc_room_name;
    @FXML
    private TableColumn<Room, Integer> tc_room_capacity;
    @FXML
    private TableColumn<Room, Float> tc_room_price;
    @FXML
    private TableColumn<Room, String> tc_room_features;
    @FXML
    private Button btn_new_room;
    @FXML
    private Button btn_edit_room;
    @FXML
    private Button btn_delete_room;
    @FXML
    private Button btn_return_reservations;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showRooms();

        tv_room_table.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Room>) c -> {
            // No row is selected, disable the button
            // At least one row is selected, enable the button
            btn_edit_room.setDisable(c.getList().isEmpty());
            btn_delete_room.setDisable(c.getList().isEmpty());
        });

        btn_edit_room.setOnAction(event -> {
            Room selectedRoom = tv_room_table.getSelectionModel().getSelectedItem();
            DBUtils.changeScene(event, "edit_room.fxml", "Edit Room", selectedRoom);
        });

        btn_delete_room.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("Do you want to delete this room?");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    DBUtils.deleteRow("rooms", "room_id", tv_room_table.getSelectionModel().getSelectedItem().getId());
                    showRooms();
                }
            });
        });

        btn_new_room.setOnAction(event -> DBUtils.changeScene(event, "new_room.fxml", "Create New Room", null));

        btn_return_reservations.setOnAction(event -> DBUtils.changeScene(event, "reservations.fxml", "UPOD OTEL", null));
    }

    private void showRooms() {
        tc_room_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        tc_room_capacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        tc_room_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        tc_room_features.setCellValueFactory(new PropertyValueFactory<>("featuresString"));
        ObservableList<Room> observableRooms = DBUtils.getRooms();
        tv_room_table.setItems(observableRooms);
    }
}

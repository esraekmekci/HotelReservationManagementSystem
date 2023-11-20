package com.upodotel.hotelmanagementsystem;

import com.upodotel.hotelmanagementsystem.controllers.*;
import com.upodotel.hotelmanagementsystem.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtils {
    public static void changeScene(ActionEvent event,
                                   String fxmlFile,
                                   String title,
                                   Object editableObj) {
        Parent root = null;

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            if (editableObj != null) {
                switch (fxmlFile) {
                    case "edit_customer.fxml" -> {
                        EditCustomerController controller = loader.getController();
                        controller.setData(editableObj);
                    }
                    case "edit_room.fxml" -> {
                        EditRoomController controller = loader.getController();
                        controller.setData(editableObj);
                    }
                    case "edit_feature.fxml" -> {
                        EditFeatureController controller = loader.getController();
                        controller.setData(editableObj);
                    }
                    case "edit_service.fxml" -> {
                        EditServiceController controller = loader.getController();
                        controller.setData(editableObj);
                    }
                    case "edit_reservation.fxml" -> {
                        EditReservationController controller = loader.getController();
                        controller.setData(editableObj);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    public static void createCustomer(String fullName, String identityNumber, String phoneNumber, Date birthDate, String customerDesc) {
        Connection connection = null;
        PreparedStatement psInsertCustomer = null;
        PreparedStatement psCheckCustomerExist = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckCustomerExist = connection.prepareStatement("SELECT * FROM customers WHERE identity_number = ?");
            psCheckCustomerExist.setString(1, identityNumber);
            resultSet = psCheckCustomerExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Customer already exists!");
                alert.show();
            } else {
                psInsertCustomer = connection.prepareStatement("INSERT INTO customers (full_name, identity_number, phone_number, birth_date, description) VALUES (?, ?, ?, ?, ?)");
                psInsertCustomer.setString(1, fullName);
                psInsertCustomer.setString(2, identityNumber);
                psInsertCustomer.setString(3, phoneNumber);
                psInsertCustomer.setDate(4, birthDate);
                psInsertCustomer.setString(5, customerDesc);
                psInsertCustomer.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Customer created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psInsertCustomer != null) {
                    psInsertCustomer.close();
                }

                if (psCheckCustomerExist != null) {
                    psCheckCustomerExist.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createRoom(String roomName, int roomCapacity, float roomPrice, List<String> roomFeatures) {
        Connection connection = null;
        PreparedStatement psInsertRoom = null;
        PreparedStatement psCheckRoomExist = null;
        PreparedStatement psSelectFeatureId = null;
        PreparedStatement psInsertRoomFeature = null;
        PreparedStatement psSelectRoom = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckRoomExist = connection.prepareStatement("SELECT * FROM rooms WHERE room_name = ?");
            psCheckRoomExist.setString(1, roomName);
            resultSet = psCheckRoomExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Room already exists!");
                alert.show();
            } else {
                psInsertRoom = connection.prepareStatement("INSERT INTO rooms (room_name, room_capacity, room_price) VALUES (?, ?, ?)");
                psInsertRoom.setString(1, roomName);
                psInsertRoom.setInt(2, roomCapacity);
                psInsertRoom.setFloat(3, roomPrice);
                psInsertRoom.executeUpdate();

                //find room_id

                psSelectRoom = connection.prepareStatement("SELECT room_id FROM rooms WHERE room_name = ?");
                psSelectRoom.setString(1, roomName);
                resultSet = psSelectRoom.executeQuery();
                resultSet.next();
                int roomId = resultSet.getInt("room_id");


                for (String featureName : roomFeatures) {
                    psSelectFeatureId = connection.prepareStatement("SELECT feature_id FROM features WHERE feature_name = ?");
                    psSelectFeatureId.setString(1, featureName);
                    resultSet = psSelectFeatureId.executeQuery();
                    resultSet.next();
                    int featureId = resultSet.getInt("feature_id");

                    psInsertRoomFeature = connection.prepareStatement("INSERT INTO room_features (room_id, feature_id) VALUES (?, ?)");
                    psInsertRoomFeature.setInt(1, roomId);
                    psInsertRoomFeature.setInt(2, featureId);
                    psInsertRoomFeature.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Room created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psInsertRoom != null) {
                    psInsertRoom.close();
                }

                if (psCheckRoomExist != null) {
                    psCheckRoomExist.close();
                }

                if (psSelectFeatureId != null) {
                    psSelectFeatureId.close();
                }

                if (psInsertRoomFeature != null) {
                    psInsertRoomFeature.close();
                }

                if (psSelectRoom != null) {
                    psSelectRoom.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFeature(String featureName) {
        Connection connection = null;
        PreparedStatement psInsertFeature = null;
        PreparedStatement psCheckFeatureExist = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckFeatureExist = connection.prepareStatement("SELECT * FROM features WHERE feature_name = ?");
            psCheckFeatureExist.setString(1, featureName);
            resultSet = psCheckFeatureExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Feature already exists!");
                alert.show();
            } else {
                psInsertFeature = connection.prepareStatement("INSERT INTO features(feature_name) VALUES (?)");
                psInsertFeature.setString(1, featureName);
                psInsertFeature.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Feature created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psCheckFeatureExist != null) {
                    psCheckFeatureExist.close();
                }

                if (psInsertFeature != null) {
                    psInsertFeature.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createService(String serviceName, float servicePrice) {
        Connection connection = null;
        PreparedStatement psInsertService = null;
        PreparedStatement psCheckServiceExist = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckServiceExist = connection.prepareStatement("SELECT * FROM services WHERE service_name = ?");
            psCheckServiceExist.setString(1, serviceName);
            resultSet = psCheckServiceExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Service already exists!");
                alert.show();
            } else {
                psInsertService = connection.prepareStatement("INSERT INTO services(service_name,unit_price) VALUES (?,?)");
                psInsertService.setString(1, serviceName);
                psInsertService.setFloat(2, servicePrice);
                psInsertService.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Service created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psCheckServiceExist != null) {
                    psCheckServiceExist.close();
                }

                if (psInsertService != null) {
                    psInsertService.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createReservation(String selectedRoomName, Date checkInDate, Date checkOutDate,
                                         String checkedIn, String checkedOut, List<String> selectedCustomerIdentityNumbers,
                                         Map<Service, Integer> selectedServices) {
        Connection connection = null;
        PreparedStatement psInsertReservation = null;
        PreparedStatement psCheckReservationExist = null;
        PreparedStatement psSelectRoomId = null;
        PreparedStatement psSelectReservationId = null;
        PreparedStatement psSelectCustomerId = null;
        PreparedStatement psSelectServiceId = null;
        PreparedStatement psInsertReservationCustomers = null;
        PreparedStatement psInsertReservationServices = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");

            psSelectRoomId = connection.prepareStatement("SELECT room_id FROM rooms WHERE room_name = ?");
            psSelectRoomId.setString(1, selectedRoomName);
            resultSet = psSelectRoomId.executeQuery();
            resultSet.next();
            int roomId = resultSet.getInt("room_id");

            psCheckReservationExist = connection.prepareStatement("SELECT * FROM reservations WHERE room_id = ?");
            psCheckReservationExist.setInt(1, roomId);
            resultSet = psCheckReservationExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Room already reserved!");
                alert.show();
            } else {
                LocalDate checkedInDate = null;
                LocalDate checkedOutDate = null;

                if (checkedIn.equals("YES")) {
                    checkedInDate = LocalDate.now();
                }

                if (checkedOut.equals("YES")) {
                    checkedOutDate = LocalDate.now();
                }

                psInsertReservation = connection.prepareStatement("INSERT INTO reservations (room_id, check_in_date, check_out_date, checked_in_date, checked_out_date) VALUES (?, ?, ?, ?, ?)");
                psInsertReservation.setInt(1, roomId);
                psInsertReservation.setDate(2, checkInDate);
                psInsertReservation.setDate(3, checkOutDate);
                psInsertReservation.setDate(4, ((checkedInDate == null) ? null : java.sql.Date.valueOf(checkedInDate)));
                psInsertReservation.setDate(5, ((checkedOutDate == null) ? null : java.sql.Date.valueOf(checkedOutDate)));
                psInsertReservation.executeUpdate();

                //find room_id

                psSelectReservationId = connection.prepareStatement("SELECT reservation_id FROM reservations WHERE room_id = ?");
                psSelectReservationId.setInt(1, roomId);
                resultSet = psSelectReservationId.executeQuery();
                resultSet.next();
                int reservationId = resultSet.getInt("reservation_id");


                for (String customerIdentityNumber : selectedCustomerIdentityNumbers) {
                    psSelectCustomerId = connection.prepareStatement("SELECT id FROM customers WHERE identity_number = ?");
                    psSelectCustomerId.setString(1, customerIdentityNumber);
                    resultSet = psSelectCustomerId.executeQuery();
                    resultSet.next();
                    int customerId = resultSet.getInt("id");

                    psInsertReservationCustomers = connection.prepareStatement("INSERT INTO reservation_customers (reservation_id, customer_id) VALUES (?, ?)");
                    psInsertReservationCustomers.setInt(1, reservationId);
                    psInsertReservationCustomers.setInt(2, customerId);
                    psInsertReservationCustomers.executeUpdate();
                }

                for (Map.Entry<Service, Integer> entry : selectedServices.entrySet()) {
                    psSelectServiceId = connection.prepareStatement("SELECT service_id FROM services WHERE service_name = ?");
                    psSelectServiceId.setString(1, entry.getKey().getServiceName());
                    resultSet = psSelectServiceId.executeQuery();
                    resultSet.next();
                    int serviceId = resultSet.getInt("service_id");

                    psInsertReservationServices = connection.prepareStatement("INSERT INTO reservation_services (reservation_id, service_id, quantity) VALUES (?, ?, ?)");
                    psInsertReservationServices.setInt(1, reservationId);
                    psInsertReservationServices.setInt(2, serviceId);
                    psInsertReservationServices.setInt(3, entry.getValue());
                    psInsertReservationServices.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Reservation created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psInsertReservation != null) {
                    psInsertReservation.close();
                }

                if (psCheckReservationExist != null) {
                    psCheckReservationExist.close();
                }

                if (psSelectRoomId != null) {
                    psSelectRoomId.close();
                }

                if (psSelectReservationId != null) {
                    psSelectReservationId.close();
                }

                if (psSelectCustomerId != null) {
                    psSelectCustomerId.close();
                }

                if (psSelectServiceId != null) {
                    psSelectServiceId.close();
                }

                if (psInsertReservationCustomers != null) {
                    psInsertReservationCustomers.close();
                }

                if (psInsertReservationServices != null) {
                    psInsertReservationServices.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ObservableList<Customer> getCustomers() {
        Connection connection = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;
        ObservableList<Customer> observableCustomers = FXCollections.observableArrayList();

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psSelect = connection.prepareStatement("SELECT * FROM customers");
            resultSet = psSelect.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("full_name");
                String identityNumber = resultSet.getString("identity_number");
                String phoneNumber = resultSet.getString("phone_number");
                Date birthDate = resultSet.getDate("birth_date");
                String description = resultSet.getString("description");

                observableCustomers.add(new Customer(id, fullName, identityNumber, phoneNumber, birthDate, description));
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Customer cannot be retrieved!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psSelect != null) {
                    psSelect.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return observableCustomers;
    }

    public static ObservableList<Feature> getFeatures() {
        Connection connection = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;
        ObservableList<Feature> observableFeatures = FXCollections.observableArrayList();

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psSelect = connection.prepareStatement("SELECT * FROM features");
            resultSet = psSelect.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("feature_id");
                String featureName = resultSet.getString("feature_name");
                observableFeatures.add(new Feature(id, featureName));
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("feature cannot be retrieved!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psSelect != null) {
                    psSelect.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return observableFeatures;
    }

    public static ObservableList<Service> getServices() {
        Connection connection = null;
        PreparedStatement psSelect = null;
        ResultSet resultSet = null;
        ObservableList<Service> observableServices = FXCollections.observableArrayList();

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psSelect = connection.prepareStatement("SELECT * FROM services");
            resultSet = psSelect.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("service_id");
                String serviceName = resultSet.getString("service_name");
                float unitPrice = resultSet.getFloat("unit_price");
                observableServices.add(new Service(id, serviceName, unitPrice));
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Service cannot be retrieved!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psSelect != null) {
                    psSelect.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return observableServices;
    }

    public static ObservableList<Room> getRooms() {
        Connection connection = null;
        PreparedStatement psSelectRooms = null;
        PreparedStatement psSelectFeatures = null;
        ResultSet resultSet = null;
        ObservableList<Room> observableRooms = FXCollections.observableArrayList();

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psSelectRooms = connection.prepareStatement("SELECT * FROM rooms");
            resultSet = psSelectRooms.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("room_id");
                String name = resultSet.getString("room_name");
                int capacity = resultSet.getInt("room_capacity");
                float price = resultSet.getFloat("room_price");

                psSelectFeatures = connection.prepareStatement("SELECT * FROM features INNER JOIN room_features ON features.feature_id = room_features.feature_id WHERE room_id = ?");
                psSelectFeatures.setInt(1, id);
                ResultSet resultSetFeatures = psSelectFeatures.executeQuery();
                List<Feature> features = new ArrayList<>();
                while (resultSetFeatures.next()) {
                    features.add(new Feature(resultSetFeatures.getInt("feature_id"), resultSetFeatures.getString("feature_name")));
                }
                observableRooms.add(new Room(id, name, capacity, price, features));
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Room cannot be retrieved!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psSelectRooms != null) {
                    psSelectRooms.close();
                }

                if (psSelectFeatures != null) {
                    psSelectFeatures.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return observableRooms;
    }

    public static ObservableList<Reservation> getReservations() {
        Connection connection = null;
        PreparedStatement psSelectReservations = null;
        PreparedStatement psSelectRoom = null;
        PreparedStatement psSelectCustomers = null;
        PreparedStatement psSelectServices = null;
        ResultSet resultSet = null;
        ObservableList<Reservation> observableReservations = FXCollections.observableArrayList();

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psSelectReservations = connection.prepareStatement("SELECT * FROM reservations");
            resultSet = psSelectReservations.executeQuery();

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                int roomId = resultSet.getInt("room_id");
                Date checkInDate = resultSet.getDate("check_in_date");
                Date checkOutDate = resultSet.getDate("check_out_date");
                Date checkedInDate = resultSet.getDate("checked_in_date");
                Date checkedOutDate = resultSet.getDate("checked_out_date");


                //get room id

                psSelectRoom = connection.prepareStatement("SELECT room_name FROM rooms WHERE room_id = ?");
                psSelectRoom.setInt(1, resultSet.getInt("room_id"));
                ResultSet resultSetRoom = psSelectRoom.executeQuery();
                resultSetRoom.next();
                String roomName = resultSetRoom.getString("room_name");
                resultSetRoom.close();


                //get customers

                psSelectCustomers = connection.prepareStatement("SELECT * FROM customers INNER JOIN reservation_customers ON customers.id = reservation_customers.customer_id WHERE reservation_id = ?");
                psSelectCustomers.setInt(1, reservationId);
                ResultSet resultSetCustomers = psSelectCustomers.executeQuery();
                List<Customer> customers = new ArrayList<>();
                while (resultSetCustomers.next()) {
                    customers.add(new Customer(resultSetCustomers.getInt("id"),
                            resultSetCustomers.getString("full_name"),
                            resultSetCustomers.getString("identity_number"),
                            resultSetCustomers.getString("phone_number"),
                            resultSetCustomers.getDate("birth_date"),
                            resultSetCustomers.getString("description")));
                }
                resultSetCustomers.close();


                //get services


                psSelectServices = connection.prepareStatement("SELECT * FROM services INNER JOIN reservation_services ON services.service_id = reservation_services.service_id WHERE reservation_id = ?");
                psSelectServices.setInt(1, reservationId);
                ResultSet resultSetServices = psSelectServices.executeQuery();
                Map<Service, Integer> services = new HashMap<>();
                while (resultSetServices.next()) {
                    services.put(new Service(resultSetServices.getInt("service_id"),
                            resultSetServices.getString("service_name"),
                            resultSetServices.getFloat("unit_price")), resultSetServices.getInt("quantity"));
                }
                resultSetServices.close();

                observableReservations.add(new Reservation(reservationId, roomId, roomName, customers, checkInDate, checkOutDate, (checkedInDate != null), (checkedOutDate != null), checkedInDate, checkedOutDate, services));
            }

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Room cannot be retrieved!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psSelectReservations != null) {
                    psSelectReservations.close();
                }

                if (psSelectRoom != null) {
                    psSelectRoom.close();
                }

                if (psSelectCustomers != null) {
                    psSelectCustomers.close();
                }

                if (psSelectServices != null) {
                    psSelectServices.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return observableReservations;
    }

    public static void deleteRow(String tableName, String attributeName, int attributeId) {
        Connection connection = null;
        PreparedStatement psDelete = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psDelete = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + attributeName + " = ?");
            psDelete.setInt(1, attributeId);
            psDelete.executeUpdate();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Deletion from " + tableName + " is successful!");
            alert.show();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Row cannot be deleted!");
            alert.show();
            e.printStackTrace();
        } finally {
            try {
                if (psDelete != null) {
                    psDelete.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void editCustomer(int id, String fullName, String identityNumber, String phoneNumber, Date birthDate, String customerDesc) {
        Connection connection = null;
        PreparedStatement psCheckCustomerExist = null;
        PreparedStatement psUpdateCustomer = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckCustomerExist = connection.prepareStatement("SELECT * FROM customers WHERE identity_number = ? AND id != ?");
            psCheckCustomerExist.setString(1, identityNumber);
            psCheckCustomerExist.setInt(2, id);
            resultSet = psCheckCustomerExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Customer already exists!");
                alert.show();
            } else {
                psUpdateCustomer = connection.prepareStatement("UPDATE customers SET full_name = ?, identity_number = ?, phone_number = ?, birth_date = ?, description = ? WHERE id = ?");
                psUpdateCustomer.setString(1, fullName);
                psUpdateCustomer.setString(2, identityNumber);
                psUpdateCustomer.setString(3, phoneNumber);
                psUpdateCustomer.setDate(4, birthDate);
                psUpdateCustomer.setString(5, customerDesc);
                psUpdateCustomer.setInt(6, id);
                psUpdateCustomer.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Customer edited successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psUpdateCustomer != null) {
                    psUpdateCustomer.close();
                }

                if (psCheckCustomerExist != null) {
                    psCheckCustomerExist.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void editFeature(int feature_id, String featureName) {
        Connection connection = null;
        PreparedStatement psCheckFeatureExist = null;
        PreparedStatement psUpdateFeature = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckFeatureExist = connection.prepareStatement("SELECT * FROM features WHERE feature_name = ? AND feature_id != ?");
            psCheckFeatureExist.setString(1, featureName);
            psCheckFeatureExist.setInt(2, feature_id);
            resultSet = psCheckFeatureExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Feature already exists!");
                alert.show();
            } else {
                psUpdateFeature = connection.prepareStatement("UPDATE features SET feature_name = ? WHERE feature_id = ?");
                psUpdateFeature.setString(1, featureName);
                psUpdateFeature.setInt(2, feature_id);
                psUpdateFeature.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Feature edited successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psUpdateFeature != null) {
                    psUpdateFeature.close();
                }

                if (psCheckFeatureExist != null) {
                    psCheckFeatureExist.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void editService(int service_id, String serviceName, float unitPrice) {
        Connection connection = null;
        PreparedStatement psCheckServiceExist = null;
        PreparedStatement psUpdateService = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckServiceExist = connection.prepareStatement("SELECT * FROM services WHERE service_name = ? AND service_id != ?");
            psCheckServiceExist.setString(1, serviceName);
            psCheckServiceExist.setInt(2, service_id);
            resultSet = psCheckServiceExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Service already exists!");
                alert.show();
            } else {
                psUpdateService = connection.prepareStatement("UPDATE services SET service_name = ?, unit_price = ? WHERE service_id = ?");
                psUpdateService.setString(1, serviceName);
                psUpdateService.setFloat(2, unitPrice);
                psUpdateService.setInt(3, service_id);
                psUpdateService.executeUpdate();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Service edited successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psUpdateService != null) {
                    psUpdateService.close();
                }

                if (psCheckServiceExist != null) {
                    psCheckServiceExist.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void editRoom(int roomId, String roomName, int roomCapacity, float roomPrice, List<String> roomFeatures) {
        Connection connection = null;
        PreparedStatement psUpdateRoom = null;
        PreparedStatement psCheckRoomExist = null;
        PreparedStatement psSelectFeatureId = null;
        PreparedStatement psDeleteRoomFeatures = null;
        PreparedStatement psInsertRoomFeature = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");
            psCheckRoomExist = connection.prepareStatement("SELECT * FROM rooms WHERE room_name = ? AND room_id != ?");
            psCheckRoomExist.setString(1, roomName);
            psCheckRoomExist.setInt(2, roomId);
            resultSet = psCheckRoomExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Room already exists!");
                alert.show();
            } else {
                psUpdateRoom = connection.prepareStatement("UPDATE rooms SET room_name = ?, room_capacity = ?, room_price = ? WHERE room_id = ?");
                psUpdateRoom.setString(1, roomName);
                psUpdateRoom.setInt(2, roomCapacity);
                psUpdateRoom.setFloat(3, roomPrice);
                psUpdateRoom.setInt(4, roomId);
                psUpdateRoom.executeUpdate();

//              DELETE FROM " + tableName + " WHERE " + attributeName + " = ?
                psDeleteRoomFeatures = connection.prepareStatement("DELETE FROM room_features WHERE room_id = ?");
                psDeleteRoomFeatures.setInt(1, roomId);
                psDeleteRoomFeatures.executeUpdate();

                for (String featureName : roomFeatures) {
                    psSelectFeatureId = connection.prepareStatement("SELECT feature_id FROM features WHERE feature_name = ?");
                    psSelectFeatureId.setString(1, featureName);
                    resultSet = psSelectFeatureId.executeQuery();
                    resultSet.next();
                    int featureId = resultSet.getInt("feature_id");

                    psInsertRoomFeature = connection.prepareStatement("INSERT INTO room_features (room_id, feature_id) VALUES (?, ?)");
                    psInsertRoomFeature.setInt(1, roomId);
                    psInsertRoomFeature.setInt(2, featureId);
                    psInsertRoomFeature.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Room created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psUpdateRoom != null) {
                    psUpdateRoom.close();
                }

                if (psCheckRoomExist != null) {
                    psCheckRoomExist.close();
                }

                if (psSelectFeatureId != null) {
                    psSelectFeatureId.close();
                }

                if (psDeleteRoomFeatures != null) {
                    psDeleteRoomFeatures.close();
                }

                if (psInsertRoomFeature != null) {
                    psInsertRoomFeature.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void editReservation(int reservationId, String selectedRoomName, Date checkInDate, Date checkOutDate,
                                       String checkedIn, String checkedOut, List<String> selectedCustomerIdentityNumbers,
                                       Map<Service, Integer> selectedServices) {
        Connection connection = null;
        PreparedStatement psUpdateReservation = null;
        PreparedStatement psCheckReservationExist = null;
        PreparedStatement psSelectRoomId = null;
        PreparedStatement psDeleteCustomers = null;
        PreparedStatement psDeleteServices = null;
        PreparedStatement psSelectCustomerId = null;
        PreparedStatement psSelectServiceId = null;
        PreparedStatement psInsertReservationCustomers = null;
        PreparedStatement psInsertReservationServices = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/upod_otel",
                            "root", "ArEs_605");

            //get room id

            psSelectRoomId = connection.prepareStatement("SELECT room_id FROM rooms WHERE room_name = ?");
            psSelectRoomId.setString(1, selectedRoomName);
            ResultSet resultSetRoom = psSelectRoomId.executeQuery();
            resultSetRoom.next();
            int roomId = resultSetRoom.getInt("room_id");
            resultSetRoom.close();

            psCheckReservationExist = connection.prepareStatement("SELECT * FROM reservations WHERE room_id = ? AND reservation_id != ?");
            psCheckReservationExist.setInt(1, roomId);
            psCheckReservationExist.setInt(2, reservationId);
            resultSet = psCheckReservationExist.executeQuery();

            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Room already reserved!");
                alert.show();
            } else {
                LocalDate checkedInDate = null;
                LocalDate checkedOutDate = null;

                if (checkedIn.equals("YES")) {
                    checkedInDate = LocalDate.now();
                }

                if (checkedOut.equals("YES")) {
                    checkedOutDate = LocalDate.now();
                }

                psUpdateReservation = connection.prepareStatement("UPDATE reservations SET room_id = ?, check_in_date = ?, check_out_date = ?, checked_in_date = ?, checked_out_date = ? WHERE reservation_id = ?");
                psUpdateReservation.setInt(1, roomId);
                psUpdateReservation.setDate(2, checkInDate);
                psUpdateReservation.setDate(3, checkOutDate);
                psUpdateReservation.setDate(4, ((checkedInDate == null) ? null : java.sql.Date.valueOf(checkedInDate)));
                psUpdateReservation.setDate(5, ((checkedOutDate == null) ? null : java.sql.Date.valueOf(checkedOutDate)));
                psUpdateReservation.setInt(6, reservationId);
                psUpdateReservation.executeUpdate();

                psDeleteCustomers = connection.prepareStatement("DELETE FROM reservation_customers WHERE reservation_id = ?");
                psDeleteCustomers.setInt(1, reservationId);
                psDeleteCustomers.executeUpdate();

                psDeleteServices = connection.prepareStatement("DELETE FROM reservation_services WHERE reservation_id = ?");
                psDeleteServices.setInt(1, reservationId);
                psDeleteServices.executeUpdate();

                for (String customerIdentityNumber : selectedCustomerIdentityNumbers) {
                    psSelectCustomerId = connection.prepareStatement("SELECT id FROM customers WHERE identity_number = ?");
                    psSelectCustomerId.setString(1, customerIdentityNumber);
                    resultSet = psSelectCustomerId.executeQuery();
                    resultSet.next();
                    int customerId = resultSet.getInt("id");

                    psInsertReservationCustomers = connection.prepareStatement("INSERT INTO reservation_customers (reservation_id, customer_id) VALUES (?, ?)");
                    psInsertReservationCustomers.setInt(1, reservationId);
                    psInsertReservationCustomers.setInt(2, customerId);
                    psInsertReservationCustomers.executeUpdate();
                }

                for (Map.Entry<Service, Integer> entry : selectedServices.entrySet()) {
                    psSelectServiceId = connection.prepareStatement("SELECT service_id FROM services WHERE service_name = ?");
                    psSelectServiceId.setString(1, entry.getKey().getServiceName());
                    resultSet = psSelectServiceId.executeQuery();
                    resultSet.next();
                    int serviceId = resultSet.getInt("service_id");

                    psInsertReservationServices = connection.prepareStatement("INSERT INTO reservation_services (reservation_id, service_id, quantity) VALUES (?, ?, ?)");
                    psInsertReservationServices.setInt(1, reservationId);
                    psInsertReservationServices.setInt(2, serviceId);
                    psInsertReservationServices.setInt(3, entry.getValue());
                    psInsertReservationServices.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Reservation created successfully");
                alert.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }

                if (psUpdateReservation != null) {
                    psUpdateReservation.close();
                }

                if (psDeleteCustomers != null) {
                    psDeleteCustomers.close();
                }

                if (psDeleteServices != null) {
                    psDeleteServices.close();
                }

                if (psCheckReservationExist != null) {
                    psCheckReservationExist.close();
                }

                if (psSelectRoomId != null) {
                    psSelectRoomId.close();
                }

                if (psSelectCustomerId != null) {
                    psSelectCustomerId.close();
                }

                if (psSelectServiceId != null) {
                    psSelectServiceId.close();
                }

                if (psInsertReservationCustomers != null) {
                    psInsertReservationCustomers.close();
                }

                if (psInsertReservationServices != null) {
                    psInsertReservationServices.close();
                }

                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

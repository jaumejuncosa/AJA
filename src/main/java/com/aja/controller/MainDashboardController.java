package com.aja.controller;

import com.aja.api.UserApiClient;
import com.aja.model.UserDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controlador del panel principal (dashboard)
 */
public class MainDashboardController {

    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnForo;
    @FXML
    private Button btnEventos;
    @FXML
    private Button btnMensajes;
    @FXML
    private Button btnLogout;

    @FXML
    private VBox usuariosContent;
    @FXML
    private VBox foroContent;
    @FXML
    private VBox eventosContent;
    @FXML
    private VBox mensajesContent;

    @FXML
    private TableView<UserDto> usuariosTable;
    @FXML
    private TableColumn<UserDto, Long> colUserId;
    @FXML
    private TableColumn<UserDto, String> colUsername;
    @FXML
    private TableColumn<UserDto, String> colEmail;
    @FXML
    private TableColumn<UserDto, Integer> colRole;
    @FXML
    private TableColumn<UserDto, Boolean> colActive;

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObservableList<UserDto> usuarios = FXCollections.observableArrayList();

    private List<Button> menuButtons;
    private List<VBox> contentPanes;

    @FXML
    public void initialize() {
        menuButtons = List.of(btnUsuarios, btnForo, btnEventos, btnMensajes);
        contentPanes = List.of(usuariosContent, foroContent, eventosContent, mensajesContent);
        selectMenuItem(0); // Usuarios por defecto

        if (usuariosTable != null) {
            colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            colActive.setCellValueFactory(new PropertyValueFactory<>("isactive"));

            usuariosTable.setItems(usuarios);
            loadUsers();
        }
    }

    @FXML
    private void showUsuarios() {
        selectMenuItem(0);
    }

    @FXML
    private void showForo() {
        selectMenuItem(1);
    }

    @FXML
    private void showEventos() {
        selectMenuItem(2);
    }

    @FXML
    private void showMensajes() {
        selectMenuItem(3);
    }

    @FXML
    private void handleLogout() throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 420, 380);
        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        stage.setTitle("AJA - Iniciar sesión");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    private void loadUsers() {
        try {
            List<UserDto> list = userApiClient.getAllUsers();
            usuarios.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectMenuItem(int index) {
        for (int i = 0; i < menuButtons.size(); i++) {
            Button btn = menuButtons.get(i);
            VBox content = contentPanes.get(i);
            boolean selected = (i == index);
            btn.getStyleClass().removeAll("menu-button-selected");
            if (selected) {
                btn.getStyleClass().add("menu-button-selected");
            }
            content.setVisible(selected);
            content.setManaged(selected);
        }
    }
}

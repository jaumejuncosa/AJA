package com.aja.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    private List<Button> menuButtons;
    private List<VBox> contentPanes;

    @FXML
    public void initialize() {
        menuButtons = List.of(btnUsuarios, btnForo, btnEventos, btnMensajes);
        contentPanes = List.of(usuariosContent, foroContent, eventosContent, mensajesContent);
        selectMenuItem(0); // Usuarios por defecto
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

package com.aja.controller;
import com.aja.service.AuthService;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador de la pantalla de login
 */
public class LoginController {
private final AuthService authService = new AuthService();
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    public void initialize() {
        // Permitir iniciar sesión con Enter
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        usernameField.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    @FXML
    private void handleLogin() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Validación básica
    if (username == null || username.isBlank()) {
        showError("Campo requerido", "Por favor, introduce tu usuario.");
        usernameField.requestFocus();
        return;
    }

    if (password == null || password.isBlank()) {
        showError("Campo requerido", "Por favor, introduce tu contraseña.");
        passwordField.requestFocus();
        return;
    }

    // Autenticación usando el servicio
    boolean ok = authService.authenticate(username, password);

    if (!ok) {
        showError("Credenciales inválidas", "Usuario o contraseña incorrectos.");
        passwordField.clear();
        passwordField.requestFocus();
        return;
    }

    // Si todo es correcto, navegar al dashboard
    try {
        navigateToDashboard();
    } catch (IOException e) {
        showError("Error", "No se pudo cargar el panel principal: " + e.getMessage());
    }
}

    private void navigateToDashboard() throws IOException {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

        stage.setTitle("AJA - Panel de control");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
    }

    @FXML
    private void handleForgotPassword() {
        showInfo("Recuperar contraseña", 
                "Funcionalidad de recuperación de contraseña.\n(Implementar según tus necesidades)");
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

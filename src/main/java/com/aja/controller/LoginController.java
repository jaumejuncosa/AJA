package com.aja.controller;
import com.aja.model.LoginResponseDto;
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
import java.util.prefs.Preferences;

/**
 * Controlador para la pantalla de inicio de sesión de la aplicación AJA Desktop.
 * Gestiona la autenticación de usuarios y la navegación hacia el panel principal.
 * Proporciona validación de campos y manejo de errores de autenticación.
 */
public class LoginController {
private final AuthService authService = AuthService.getInstance();
    private static final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

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

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura los eventos de teclado para permitir login con la tecla Enter
     * y carga las credenciales guardadas si existen.
     */
    @FXML
    public void initialize() {
        // Permitir iniciar sesión con Enter
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        usernameField.setOnKeyPressed(this::handleKeyPressed);

        // Cargar credenciales guardadas
        String savedUsername = prefs.get(PREF_USERNAME, null);
        String savedPassword = prefs.get(PREF_PASSWORD, null);
        if (savedUsername != null && savedPassword != null) {
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
            rememberCheckBox.setSelected(true);
        }
    }

    /**
     * Maneja los eventos de teclado para los campos de entrada.
     * Permite iniciar sesión presionando la tecla Enter.
     *
     * @param event El evento de teclado generado
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    /**
     * Procesa el intento de inicio de sesión del usuario.
     * Valida los campos de entrada, autentica las credenciales y navega
     * al panel principal si la autenticación es exitosa.
     */
    @FXML
   private void handleLogin() {
    loginButton.setDisable(true); // Desactivamos al hacer clic
    
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Validación básica
    if (username == null || username.isBlank()) {
        showError("Campo requerido", "Por favor, introduce tu usuario.");
        usernameField.requestFocus();
        loginButton.setDisable(false); // Reactivamos el botón
        return;
    }

    if (password == null || password.isBlank()) {
        showError("Campo requerido", "Por favor, introduce tu contraseña.");
        passwordField.requestFocus();
        loginButton.setDisable(false); // Reactivamos el botón
        return;
    }

    // Autenticación usando el servicio
    LoginResponseDto authResponse = authService.authenticate(username, password);

    if (!authResponse.isSuccess()) {
        // Mostrar mensaje de error del servidor
        String errorMessage = authResponse.getMessage() != null ?
            authResponse.getMessage().toString() :
            "Credenciales inválidas";
        showError("Error de autenticación", errorMessage);
        passwordField.clear();
        passwordField.requestFocus();
        loginButton.setDisable(false); // Reactivamos el botón
        return;
    }
    
    String token = authResponse.getToken();

    // Guardar o borrar credenciales según el checkbox
    if (rememberCheckBox.isSelected()) {
        prefs.put(PREF_USERNAME, username);
        prefs.put(PREF_PASSWORD, password);
    } else {
        prefs.remove(PREF_USERNAME);
        prefs.remove(PREF_PASSWORD);
    }

    // Si todo es correcto, navegar al dashboard (¡UNA SOLA VEZ!)
    navigateToDashboard(token);
}

    /**
     * Navega desde la pantalla de login al panel principal (dashboard).
     * Carga la vista MainDashboard.fxml y configura la nueva escena.
     *
     * @throws IOException Si ocurre un error al cargar el archivo FXML
     */
    private void navigateToDashboard(String token) {
    try {
        // 1. Cargamos el archivo FXML del Dashboard y obtenemos el root
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
        Parent root = loader.load();

        // 2. Obtener el controller y pasar el token
        MainDashboardController dashboardController = loader.getController();
        dashboardController.setToken(authService.getToken()); // Asegúrate de que AuthService tenga el token

        // 3. Crear la escena con la vista cargada y añadir los estilos
        Scene scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

        // 4. AHORA SÍ: Obtenemos la ventana actual y le aplicamos todo
        Stage stage = (Stage) loginButton.getScene().getWindow();
        if (stage == null) {
    System.out.println("Aviso: Se intentó cargar el Dashboard por segunda vez. Bloqueado.");
    return; // Cortamos la ejecución aquí para evitar el NullPointerException
}
        stage.setTitle("AJA - Panel de control");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.centerOnScreen();

        // 5. Y finalmente, mostramos la ventana actualizada
        stage.show();

        System.out.println("Dashboard cargado correctamente");

    } catch (IOException e) {
        e.printStackTrace();
        showError("Error", "No se pudo cargar el panel principal: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        showError("Error inesperado", "Ocurrió un error al inicializar el Dashboard: " + e.getMessage());
    }
}


    /**
     * Maneja la acción del enlace "Olvidé mi contraseña".
     * Muestra un mensaje informativo indicando que la funcionalidad
     * debe ser implementada según los requisitos específicos.
     */
    @FXML
    private void handleForgotPassword() {
        showInfo("Recuperar contraseña", 
                "Funcionalidad de recuperación de contraseña.\n(Implementar según tus necesidades)");
    }

    /**
     * Muestra un diálogo de error con el título y mensaje especificados.
     *
     * @param title El título del diálogo de error
     * @param message El mensaje descriptivo del error
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo informativo con el título y mensaje especificados.
     *
     * @param title El título del diálogo informativo
     * @param message El mensaje informativo a mostrar
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

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
 * Controlador de la pantalla de Login. 
 * Aquí validamos al usuario y, si todo es correcto, lo mandamos al Dashboard principal.
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
    private Label usernameErrorLabel;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    /**
     * JavaFX lanza esto al cargar la vista. Configuramos los eventos del teclado 
     * y cargamos los datos guardados si el usuario marcó "Recordarme" la última vez.
     */
    @FXML
    public void initialize() {
        // Permitir iniciar sesión con Enter
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        usernameField.setOnKeyPressed(this::handleKeyPressed);

        // Quitar el borde rojo y ocultar el mensaje de error al empezar a escribir
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> 
            {
                usernameField.getStyleClass().remove("error-field");
                usernameErrorLabel.setVisible(false);
                usernameErrorLabel.setManaged(false);
            });
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> 
            {
                passwordField.getStyleClass().remove("error-field");
                passwordErrorLabel.setVisible(false);
                passwordErrorLabel.setManaged(false);
            });

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
     * Si el usuario pulsa Enter en cualquiera de los campos, 
     * intentamos hacer el login directamente.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    /**
     * Recogemos lo que el usuario escribió, comprobamos que no haya dejado nada 
     * en blanco y pedimos el login al servidor a través del servicio.
     */
    @FXML
   private void handleLogin() {
    loginButton.setDisable(true); // Desactivamos al hacer clic
    
    // Limpiamos estilos de error previos antes de validar
    usernameField.getStyleClass().remove("error-field");
    passwordField.getStyleClass().remove("error-field");
    usernameErrorLabel.setVisible(false);
    usernameErrorLabel.setManaged(false);
    passwordErrorLabel.setVisible(false);
    passwordErrorLabel.setManaged(false);
    
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Validación básica
    if (username == null || username.isBlank()) {
        usernameErrorLabel.setText("Por favor, introduce tu usuario.");
        usernameErrorLabel.setVisible(true);
        usernameErrorLabel.setManaged(true);
        usernameField.requestFocus();
        usernameField.getStyleClass().add("error-field");
        loginButton.setDisable(false); // Reactivamos el botón
        return;
    }

    if (password == null || password.isBlank()) {
        passwordErrorLabel.setText("Por favor, introduce tu contraseña.");
        passwordErrorLabel.setVisible(true);
        passwordErrorLabel.setManaged(true);
        passwordField.requestFocus();
        passwordField.getStyleClass().add("error-field");
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
            
        // Detectamos si el error es sobre el usuario para poner el mensaje en su sitio
        if (errorMessage.toLowerCase().contains("usuario") || errorMessage.toLowerCase().contains("user")) {
            usernameErrorLabel.setText(errorMessage);
            usernameErrorLabel.setVisible(true);
            usernameErrorLabel.setManaged(true);
            usernameField.requestFocus();
        } else {
            passwordErrorLabel.setText(errorMessage);
            passwordErrorLabel.setVisible(true);
            passwordErrorLabel.setManaged(true);
            passwordField.requestFocus();
        }

        usernameField.getStyleClass().add("error-field");
        passwordField.getStyleClass().add("error-field");
        passwordField.clear();
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
     * Si el servidor nos dio el visto bueno, cambiamos la ventana del login 
     * por la del Dashboard principal, pasando el token de seguridad.
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
        // Sincronizamos el tamaño del Dashboard para que sea consistente
        Scene scene = new Scene(root, 1200, 800);
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
     * Acción para cuando alguien olvida la contraseña. De momento solo avisamos.
     */
    @FXML
    private void handleForgotPassword() {
        showInfo("Recuperar contraseña", 
                "Funcionalidad de recuperación de contraseña.\n(Implementar según tus necesidades)");
    }

    /**
     * Alerta rápida para errores de validación o fallos en el servidor.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Alerta rápida para mensajes de información.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

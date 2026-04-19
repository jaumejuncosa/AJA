package com.aja.controller;
import com.aja.api.UserApiClient;
import com.aja.model.LoginResponseDto;
import com.aja.model.UserNewDto;
import com.aja.service.AuthService;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

/**
 * Maneja la entrada al programa. Comprueba quién eres y te deja pasar al panel de control.
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
     * Se activa al abrir la ventana. Prepara el teclado y recuerda tu nombre si lo pediste antes.
     */
    @FXML
    public void initialize() {
        // Entrar pulsando la tecla Intro
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        usernameField.setOnKeyPressed(this::handleKeyPressed);

        // Limpiar los avisos de error cuando el usuario empieza a escribir de nuevo
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

        // Recuperar el nombre y la clave guardados
        String savedUsername = prefs.get(PREF_USERNAME, null);
        String savedPassword = prefs.get(PREF_PASSWORD, null);
        if (savedUsername != null && savedPassword != null) {
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
            rememberCheckBox.setSelected(true);
        }
    }

    /**
     * Si pulsas Intro, intentamos entrar al sistema.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }

    /**
     * Leemos lo escrito, miramos que no falte nada y pedimos permiso para entrar.
     */
    @FXML
   private void handleLogin() {
    loginButton.setDisable(true); // Apagamos el botón un momento para no pulsar dos veces
    
    // Quitamos los avisos de error anteriores
    usernameField.getStyleClass().remove("error-field");
    passwordField.getStyleClass().remove("error-field");
    usernameErrorLabel.setVisible(false);
    usernameErrorLabel.setManaged(false);
    passwordErrorLabel.setVisible(false);
    passwordErrorLabel.setManaged(false);
    
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Comprobamos que no haya huecos vacíos
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

    try {
        // Comprobamos tu identidad
        LoginResponseDto authResponse = authService.authenticate(username, password);

        if (!authResponse.isSuccess()) {
            // Mostrar aviso de error si algo ha fallado
            String errorMessage = authResponse.getMessage() != null ?
                authResponse.getMessage().toString() :
                "Credenciales inválidas";
                
            // Miramos si el problema es el nombre para avisar en el sitio correcto
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

            // Si el error es por inactividad, mostramos un aviso directo para que no pase desapercibido
            if (errorMessage.toLowerCase().contains("inactivo") || errorMessage.toLowerCase().contains("deshabilitado")) {
                showError("Acceso denegado", "Tu cuenta está desactivada. Contacta con un administrador.");
            }

            loginButton.setDisable(false); // Reactivamos el botón
            return;
        }
        
        String token = authResponse.getToken();

        // Guardar o quitar tus datos según lo que hayas marcado
        if (rememberCheckBox.isSelected()) {
            prefs.put(PREF_USERNAME, username);
            prefs.put(PREF_PASSWORD, password);
        } else {
            prefs.remove(PREF_USERNAME);
            prefs.remove(PREF_PASSWORD);
        }

        // Si la llave funciona, entramos al panel principal
        navigateToDashboard(token);
    } catch (Exception e) {
        showError("Error de Conexión", "No se pudo conectar con el servidor. Asegúrate de que el backend esté iniciado.");
        loginButton.setDisable(false);
    }
}

    /**
     * Si nos dejan entrar, cerramos esta ventana y abrimos el panel de control con nuestra llave.
     */
    private void navigateToDashboard(String token) {
    try {
        // 1. Preparamos la vista del panel principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
        Parent root = loader.load();

        // 2. Buscamos al encargado del panel y le damos la llave
        MainDashboardController dashboardController = loader.getController();
        dashboardController.setToken(authService.getToken()); 

        // 3. Montamos la ventana con sus colores y tamaño adecuado
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

        // 4. Cambiamos la ventana que estamos viendo por la nueva
        Stage stage = (Stage) loginButton.getScene().getWindow();
        if (stage == null) {
    System.out.println("Aviso: No abrimos el panel dos veces para evitar fallos.");
    return; 
}
        stage.setTitle("AJA - Panel de control");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.centerOnScreen();

        // 5. Enseñamos el panel de control
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
     * Qué hacer si olvidas la clave. Por ahora solo mostramos un aviso.
     */
    @FXML
    private void handleForgotPassword() {
        showInfo("Recuperar contraseña", 
                "Funcionalidad de recuperación de contraseña.\n(Implementar según tus necesidades)");
    }

    /**
     * Crear cuenta: Permite que cualquiera se apunte al sistema.
     */
    @FXML
    private void handleRegister() {
        Stage dialog = new Stage();
        dialog.setTitle("Crear nueva cuenta");
        
        VBox layout = new VBox(10);
        layout.setPadding(new javafx.geometry.Insets(20));
        layout.setStyle("-fx-background-color: white;");

        TextField userField = new TextField();
        userField.setPromptText("Usuario");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Contraseña");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        Button btnAlta = new Button("Registrarse");
        btnAlta.setMaxWidth(Double.MAX_VALUE);
        btnAlta.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold;");

        layout.getChildren().addAll(
            new Label("Únete a AJA Team"),
            userField, passField, emailField, btnAlta
        );

        btnAlta.setOnAction(e -> {
            try {
                UserNewDto newUser = new UserNewDto();
                newUser.setUsername(userField.getText());
                newUser.setPassword(passField.getText());
                newUser.setEmail(emailField.getText());

                UserApiClient client = new UserApiClient();
                client.createUser(newUser);
                
                dialog.close();
                showInfo("Éxito", "Cuenta creada. Ya puedes iniciar sesión.");
            } catch (Exception ex) {
                showError("Error", "No se pudo realizar el alta: " + ex.getMessage());
            }
        });

        dialog.setScene(new Scene(layout, 300, 350));
        dialog.show();
    }

    /**
     * Aviso rápido si algo sale mal o falta información.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Aviso rápido para dar noticias.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

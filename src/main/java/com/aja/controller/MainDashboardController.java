package com.aja.controller;

import com.aja.api.UserApiClient;
import com.aja.api.EventApiClient;
import com.aja.api.MessageApiClient;
import com.aja.api.ForumApiClient;
import com.aja.model.UserDto;
import com.aja.model.EventDto;
import com.aja.model.MessageDto;
import com.aja.model.ForumDto;
import com.aja.service.AuthService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * Controlador del panel principal (dashboard) de la aplicación AJA Desktop.
 * Gestiona la navegación entre las diferentes secciones de la aplicación:
 * usuarios, foro, eventos y mensajes. Coordina la carga de datos desde
 * las APIs y la actualización de las vistas correspondientes.
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
    private StackPane contentArea;

    @FXML
    private Label lblUsername;
    @FXML
    private Label lblUserRole;
    @FXML
    private Label lblUserEmail;

    @FXML
    private TextField searchUserField;

    @FXML
    private Button newUserButton;
    @FXML
    private Button newForumButton;
    @FXML
    private Button newEventButton;
    @FXML
    private Button newMessageButton;

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
    private TableColumn<UserDto, String> colRole;
    @FXML
    private TableColumn<UserDto, Boolean> colActive;

    /* Cambiamos TableView por ListView para la línea de tiempo */
    @FXML
    private ListView<EventDto> eventListView;

    @FXML
    private TableView<MessageDto> mensajesTable;
    @FXML
    private TableColumn<MessageDto, Long> colMessageId;
    @FXML
    private TableColumn<MessageDto, String> colMessageSender;
    @FXML
    private TableColumn<MessageDto, String> colMessageReceiver;
    @FXML
    private TableColumn<MessageDto, String> colMessageContent;
    @FXML
    private TableColumn<MessageDto, String> colMessageDate;
    @FXML
    private TableColumn<MessageDto, Boolean> colMessageRead;

    /* Cambiamos TableView por ListView para un estilo de foro real */
    @FXML
    private ListView<ForumDto> forumListView;

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObservableList<UserDto> usuarios = FXCollections.observableArrayList();
    private final FilteredList<UserDto> filteredUsuarios = new FilteredList<>(usuarios);

    private final EventApiClient eventApiClient = new EventApiClient();
    private final ObservableList<EventDto> eventos = FXCollections.observableArrayList();

    private final MessageApiClient messageApiClient = new MessageApiClient();
    private final ObservableList<MessageDto> mensajes = FXCollections.observableArrayList();

    private final ForumApiClient forumApiClient = new ForumApiClient();
    private final ObservableList<ForumDto> foroPosts = FXCollections.observableArrayList();

    private final AuthService authService = AuthService.getInstance();

    private List<Button> menuButtons;
    private String token;

    /**
     * Este método lo lanza JavaFX al cargar el FXML. Aquí preparamos la UI,
     * las tablas, las listas y configuramos los estilos de las tarjetas.
     */
    @FXML
    public void initialize() {
         System.out.println("Inicializando Dashboard...");
         
        // Mostrar información del usuario actual
        UserDto currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            lblUsername.setText(currentUser.getUsername());
            lblUserRole.setText(currentUser.getRole());
            lblUserEmail.setText(currentUser.getEmail());

            // Mostrar u ocultar el botón "Usuarios" según el rol
            boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
            btnUsuarios.setVisible(isAdmin);
            btnUsuarios.setManaged(isAdmin);

            // Si no es administrador, enfocar el botón Foro al iniciar
            if (!isAdmin) {
                btnForo.requestFocus();
            }
        }

        menuButtons = List.of(btnUsuarios, btnForo, btnEventos, btnMensajes);

        // En lugar de cargar una pestaña por defecto, mostramos la bienvenida
        showWelcomeView();
        
        // Configuramos el filtrado en tiempo real
        setupUserFilter();
        
        if (usuariosTable != null) {
            colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            colActive.setCellValueFactory(new PropertyValueFactory<>("active"));

            // Importante: La tabla ahora usa la lista filtrada, no la original
            usuariosTable.setItems(filteredUsuarios);

            // Al hacer doble click sobre un usuario, solicitar detalle por ID.
            usuariosTable.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    UserDto selected = usuariosTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        showUserDetails(selected.getId());
                    }
                }
            });
        }

        if (eventListView != null) {
            eventListView.setItems(eventos);
            eventListView.getStyleClass().add("timeline-list-view");
            
            eventListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(EventDto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        HBox timelineRow = new HBox(15);
                        timelineRow.setAlignment(Pos.CENTER_LEFT);
                        timelineRow.getStyleClass().add("timeline-item");

                        VBox dateBadge = new VBox();
                        dateBadge.getStyleClass().add("event-date-badge");
                        Label day = new Label(item.getDate().split("-")[2]); // Asumiendo YYYY-MM-DD
                        day.getStyleClass().add("event-date-day");
                        dateBadge.getChildren().add(day);

                        VBox details = new VBox(2);
                        Label title = new Label(item.getTitle());
                        title.getStyleClass().add("event-card-title");
                        Label location = new Label("@ " + item.getLocation());
                        location.getStyleClass().add("event-card-meta");
                        
                        details.getChildren().addAll(title, location);
                        timelineRow.getChildren().addAll(dateBadge, details);
                        setGraphic(timelineRow);
                    }
                }
            });
        }

        if (mensajesTable != null) {
            colMessageId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colMessageSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
            colMessageReceiver.setCellValueFactory(new PropertyValueFactory<>("receiver"));
            colMessageContent.setCellValueFactory(new PropertyValueFactory<>("content"));
            colMessageDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colMessageRead.setCellValueFactory(new PropertyValueFactory<>("read"));

            mensajesTable.setItems(mensajes);
            //loadMessages();
        }

        if (forumListView != null) {
            forumListView.setItems(foroPosts);
            forumListView.getStyleClass().add("forum-list-view");
            
            // Definimos cómo se ve cada "tarjeta" del foro
            forumListView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(ForumDto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        VBox card = new VBox(5);
                        card.getStyleClass().add("forum-card");
                        
                        Label title = new Label(item.getTitle());
                        title.getStyleClass().add("forum-card-title");
                        
                        Label meta = new Label();
                        meta.getStyleClass().add("forum-card-meta");
                        meta.setText("Publicado por ");
                        
                        Label author = new Label(item.getAuthor());
                        author.getStyleClass().add("forum-author-tag");
                        
                        HBox metaBox = new HBox(author, new Label(" • " + (item.getDate() != null ? item.getDate() : "Reciente")));
                        metaBox.setAlignment(Pos.CENTER_LEFT);
                        
                        card.getChildren().addAll(title, metaBox);
                        setGraphic(card);
                    }
                }
            });
        }
    }

    /**
     * Limpia la parte derecha y pone el mensaje de bienvenida con el logo.
     * Esto es lo primero que ve el usuario al entrar.
     */
    private void showWelcomeView() {
        contentArea.getChildren().clear();

        VBox welcomeBox = new VBox(20); // Aumentamos el espacio entre elementos
        welcomeBox.getStyleClass().add("welcome-container");
        welcomeBox.setAlignment(Pos.CENTER);

        // Cargar el logo desde los recursos
        ImageView logoView = new ImageView();
        try {
            java.net.URL logoUrl = getClass().getResource("/images/logo.png");
            if (logoUrl != null) {
                Image logo = new Image(logoUrl.toExternalForm());
                logoView.setImage(logo);
                logoView.setFitWidth(150); // Tamaño sugerido para el logo
                logoView.setPreserveRatio(true);
                logoView.getStyleClass().add("welcome-logo");
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        Label title = new Label("¡Bienvenido al Panel de Control!");
        title.getStyleClass().add("welcome-title");

        Label subtitle = new Label("Por favor, selecciona una opción en el menú lateral para empezar a gestionar el sistema.");
        subtitle.getStyleClass().add("welcome-subtitle");

        welcomeBox.getChildren().addAll(logoView, title, subtitle);
        contentArea.getChildren().add(welcomeBox);
    }

    /**
     * Un "comodín" para cargar archivos FXML externos dentro del StackPane de la derecha.
     * Muy útil si decidimos separar las secciones en archivos independientes.
     */
    private <T> T loadView(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            // Asegurar que la vista se expanda en el StackPane
            if (view instanceof javafx.scene.layout.Region) {
                ((javafx.scene.layout.Region) view).prefWidthProperty().bind(contentArea.widthProperty());
                ((javafx.scene.layout.Region) view).prefHeightProperty().bind(contentArea.heightProperty());
            }

            contentArea.getChildren().add(view);
            return loader.getController();
        } catch (IOException e) {
            showError("Error de carga", "No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cambia la vista a la gestión de usuarios y refresca la tabla.
     */
    @FXML
    private void showUsuarios() {
        selectMenuItem(0);
        usuariosContent.setVisible(true);
        usuariosContent.setManaged(true);
        contentArea.getChildren().setAll(usuariosContent);
        loadUsers();
    }

    /**
     * Cambia la vista al foro dinámico (estilo tarjetas).
     */
    @FXML
    private void showForo() {
        selectMenuItem(1);
        foroContent.setVisible(true);
        foroContent.setManaged(true);
        contentArea.getChildren().setAll(foroContent);
        loadForumPosts();
    }

    /**
     * Cambia la vista a la línea de tiempo de eventos.
     */
    @FXML
    private void showEventos() {
        selectMenuItem(2);
        eventosContent.setVisible(true);
        eventosContent.setManaged(true);
        contentArea.getChildren().setAll(eventosContent);
        loadEvents();
    }

    /**
     * Cambia la vista a la bandeja de mensajes.
     */
    @FXML
    private void showMensajes() {
        selectMenuItem(3);
        mensajesContent.setVisible(true);
        mensajesContent.setManaged(true);
        contentArea.getChildren().setAll(mensajesContent);
        loadMessages();
    }

    /**
     * Cierra la sesión en el servicio y nos devuelve a la pantalla de login
     * ajustando de nuevo el tamaño de la ventana.
     */
   @FXML
    private void handleLogout(ActionEvent event) {
    try {
        System.out.println("Ejecutando logout seguro...");
        com.aja.service.AuthService.getInstance().logout();

        // 1. Cargar el FXML (Ruta que ya sabemos que funciona)
        java.net.URL loginUrl = getClass().getClassLoader().getResource("login.fxml");
        if (loginUrl == null) loginUrl = getClass().getClassLoader().getResource("views/login.fxml");

        FXMLLoader loader = new FXMLLoader(loginUrl);
        Parent root = loader.load();
        
        // 2. Preparar la escena
        Scene scene = new Scene(root, 420, 440);
        
        // 3. INTENTO DE CSS (Ruta corregida a login.css)
        try {
            // Probamos en la carpeta /styles/ si ahí es donde lo tienes según tu imagen
            java.net.URL cssUrl = getClass().getResource("/styles/login.css");
            
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println("CSS 'login.css' aplicado correctamente.");
            } else {
                System.err.println("Advertencia: No se encontró /styles/login.css. Comprueba si está en la raíz.");
            }
        } catch (Exception e) {
            System.err.println("Error al aplicar estilos: " + e.getMessage());
        }

        // 4. Cambiar la ventana
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        stage.setMaximized(false); // Quitar el maximizado
        stage.setResizable(false); // Volver a bloquear el tamaño
        stage.setScene(scene);
        
        // Sincronizamos con el tamaño exacto de App.java
        stage.sizeToScene();
        
        stage.centerOnScreen();
        stage.show();
        
        System.out.println("¡Cambio de pantalla completado!");

    } catch (Exception e) {
        System.err.println("ERROR CRÍTICO: El cambio de pantalla ha fallado completamente.");
        e.printStackTrace();
    }
}

    /**
     * Configuramos el buscador para que filtre la lista de usuarios mientras escribes.
     */
    private void setupUserFilter() {
        if (searchUserField != null) {
            searchUserField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredUsuarios.setPredicate(user -> {
                    // Si el buscador está vacío, enseñamos a todo el mundo
                    if (newValue == null || newValue.isBlank()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase().trim();

                    // Comprobamos si el nombre o el email contienen lo que buscamos
                    if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    return false; // No hay coincidencia
                });
            });
        }
    }

    /**
     * Llama a la API para traer los usuarios y actualiza la tabla en el hilo de la UI.
     */
private void loadUsers() {
    try {
        List<UserDto> list = userApiClient.getAllUsers();

        if (list != null) {
            System.out.println("Usuarios recibidos: " + list.size());

            Platform.runLater(() -> {
                usuarios.clear();
                usuarios.setAll(list);
                usuariosTable.refresh();
            });
        }
        
    } catch (Exception e) {
        System.err.println("Error al cargar la lista de usuarios:");
        e.printStackTrace();
    }
}

    /**
     * Al hacer doble clic en un usuario, abrimos un mensaje con todos sus detalles
     * que nos trae la API por ID.
     */
    private void showUserDetails(Long userId) {
        try {
            UserDto user = userApiClient.getUserById(userId);
            String content = String.format(
                    "ID: %d\nUsuario: %s\nEmail: %s\nRol: %s\nActivo: %s",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getActive()
            );

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalle de usuario");
            alert.setHeaderText("Usuario con ID " + userId);
            alert.setContentText(content);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo cargar el usuario");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Actualiza la lista de eventos desde el servidor.
     */
    private void loadEvents() {
        try {
            List<EventDto> list = eventApiClient.getAllEvents();
            Platform.runLater(() -> eventos.setAll(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza la bandeja de entrada de mensajes.
     */
    private void loadMessages() {
        try {
            List<MessageDto> list = messageApiClient.getAllMessages();
            Platform.runLater(() -> mensajes.setAll(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza los temas del foro.
     */
    private void loadForumPosts() {
        try {
            List<ForumDto> list = forumApiClient.getAllForumPosts();
            Platform.runLater(() -> foroPosts.setAll(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gestiona el estilo visual de los botones del menú lateral para que 
     * sepamos siempre en qué sección estamos.
     */
    private void selectMenuItem(int index) {
        for (int i = 0; i < menuButtons.size(); i++) {
            Button btn = menuButtons.get(i);
            boolean selected = (i == index);
            btn.getStyleClass().removeAll("menu-button-selected");
            if (selected) {
                btn.getStyleClass().add("menu-button-selected");
            }
        }
    }

    /**
     * Abre un diálogo modal para crear un nuevo usuario.
     */
    @FXML
    private void handleNewUser() {
        System.out.println("handleNewUser called");
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuevo usuario");
        dialog.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(460);
        vbox.getStyleClass().add("content-pane");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nombre de usuario");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Contraseña");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.setValue("USER"); // Valor por defecto

        CheckBox activeCheck = new CheckBox("Activo");

        Button okButton = new Button("Crear");
        Button cancelButton = new Button("Cancelar");

        okButton.getStyleClass().add("primary-action-button");
        cancelButton.getStyleClass().add("primary-action-button");

        HBox buttons = new HBox(10, okButton, cancelButton);

        vbox.getChildren().addAll(
            new Label("Nombre de usuario:"), usernameField,
            new Label("Contraseña:"), passwordField,
            new Label("Email:"), emailField,
            new Label("Rol:"), roleComboBox,
            activeCheck,
            buttons
        );

        okButton.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                String email = emailField.getText().trim();
                String role = roleComboBox.getValue();
                boolean active = activeCheck.isSelected();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                UserDto newUser = new UserDto();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setRole(role);
                newUser.setIsActive(active);

                userApiClient.createUser(newUser);
                loadUsers(); // Refrescar tabla
                dialog.close();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Éxito");
                success.setHeaderText("Usuario creado");
                success.setContentText("El usuario ha sido creado exitosamente.");
                success.showAndWait();

            } catch (Exception ex) {
                showError("Error", "No se pudo crear el usuario: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Abre un diálogo modal para crear un nuevo tema en el foro.
     */
    @FXML
    private void handleNewForum() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuevo tema");
        dialog.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(460);
        vbox.getStyleClass().add("content-pane");

        TextField titleField = new TextField();
        titleField.setPromptText("Título del tema");

        TextField authorField = new TextField();
        authorField.setPromptText("Autor (username)");

        Button okButton = new Button("Crear");
        Button cancelButton = new Button("Cancelar");

        okButton.getStyleClass().add("primary-action-button");
        cancelButton.getStyleClass().add("primary-action-button");

        HBox buttons = new HBox(10, okButton, cancelButton);

        vbox.getChildren().addAll(
            new Label("Título:"), titleField,
            new Label("Autor:"), authorField,
            buttons
        );

        okButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();

                if (title.isEmpty() || author.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                ForumDto newPost = new ForumDto();
                newPost.setTitle(title);
                newPost.setAuthor(author);
                // Date se puede omitir, el servidor lo asigna

                forumApiClient.createForumPost(newPost);
                loadForumPosts(); // Refrescar tabla
                dialog.close();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Éxito");
                success.setHeaderText("Tema creado");
                success.setContentText("El tema ha sido creado exitosamente.");
                success.showAndWait();

            } catch (Exception ex) {
                showError("Error", "No se pudo crear el tema: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Abre un diálogo modal para crear un nuevo evento.
     */
    @FXML
    private void handleNewEvent() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuevo evento");
        dialog.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(460);
        vbox.getStyleClass().add("content-pane");

        TextField titleField = new TextField();
        titleField.setPromptText("Título del evento");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Descripción");

        TextField dateField = new TextField();
        dateField.setPromptText("Fecha (ej: 2026-03-15)");

        TextField locationField = new TextField();
        locationField.setPromptText("Ubicación");

        Button okButton = new Button("Crear");
        Button cancelButton = new Button("Cancelar");

        okButton.getStyleClass().add("primary-action-button");
        cancelButton.getStyleClass().add("primary-action-button");

        HBox buttons = new HBox(10, okButton, cancelButton);

        vbox.getChildren().addAll(
            new Label("Título:"), titleField,
            new Label("Descripción:"), descriptionField,
            new Label("Fecha:"), dateField,
            new Label("Ubicación:"), locationField,
            buttons
        );

        okButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String date = dateField.getText().trim();
                String location = locationField.getText().trim();

                if (title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                EventDto newEvent = new EventDto();
                newEvent.setTitle(title);
                newEvent.setDescription(description);
                newEvent.setDate(date);
                newEvent.setLocation(location);

                eventApiClient.createEvent(newEvent);
                loadEvents(); // Refrescar tabla
                dialog.close();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Éxito");
                success.setHeaderText("Evento creado");
                success.setContentText("El evento ha sido creado exitosamente.");
                success.showAndWait();

            } catch (Exception ex) {
                showError("Error", "No se pudo crear el evento: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Abre un diálogo modal para mandar un nuevo mensaje.
     */
    @FXML
    private void handleNewMessage() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Nuevo mensaje");
        dialog.setResizable(false);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));
        vbox.setPrefWidth(460);
        vbox.getStyleClass().add("content-pane");

        TextField senderField = new TextField();
        senderField.setPromptText("Remitente (username)");

        TextField receiverField = new TextField();
        receiverField.setPromptText("Destinatario (username)");

        TextField contentField = new TextField();
        contentField.setPromptText("Contenido del mensaje");

        CheckBox readCheck = new CheckBox("Marcar como leído");

        Button okButton = new Button("Enviar");
        Button cancelButton = new Button("Cancelar");

        okButton.getStyleClass().add("primary-action-button");
        cancelButton.getStyleClass().add("primary-action-button");

        HBox buttons = new HBox(10, okButton, cancelButton);

        vbox.getChildren().addAll(
            new Label("Remitente:"), senderField,
            new Label("Destinatario:"), receiverField,
            new Label("Contenido:"), contentField,
            readCheck,
            buttons
        );

        okButton.setOnAction(e -> {
            try {
                String sender = senderField.getText().trim();
                String receiver = receiverField.getText().trim();
                String content = contentField.getText().trim();
                boolean read = readCheck.isSelected();

                if (sender.isEmpty() || receiver.isEmpty() || content.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                MessageDto newMessage = new MessageDto();
                newMessage.setSender(sender);
                newMessage.setReceiver(receiver);
                newMessage.setContent(content);
                newMessage.setRead(read);
                // Date se puede omitir, el servidor lo asigna

                messageApiClient.createMessage(newMessage);
                loadMessages(); // Refrescar tabla
                dialog.close();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Éxito");
                success.setHeaderText("Mensaje enviado");
                success.setContentText("El mensaje ha sido enviado exitosamente.");
                success.showAndWait();

            } catch (Exception ex) {
                showError("Error", "No se pudo enviar el mensaje: " + ex.getMessage());
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Método de utilidad para sacar alertas de error rápidas.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Guardamos el token en el controlador y se lo pasamos a todos los clientes API
     * para que puedan funcionar.
     */
    public void setToken(String token) {
        this.token = token;

        // Pasar token a los clientes de API
        userApiClient.setToken(token);
        eventApiClient.setToken(token);
        messageApiClient.setToken(token);
        forumApiClient.setToken(token);

        // Carga inicial de usuarios tras recibir el token
        loadUsers();
    }
}

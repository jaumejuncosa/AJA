package com.aja.controller;

import com.aja.api.UserApiClient;
import com.aja.api.EventApiClient;
import com.aja.api.MessageApiClient;
import com.aja.api.ForumApiClient;
import com.aja.api.TopicApiClient;
import com.aja.model.UserDto;
import com.aja.model.UserNewDto;
import com.aja.model.EventDto;
import com.aja.model.MessageDto;
import com.aja.model.ForumDto;
import com.aja.model.CommentDto;
import com.aja.model.TopicDto;
import com.aja.service.AuthService;
import com.aja.util.DateUtils;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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

    // Configuración de la versión de la aplicación
    private static final String APP_VERSION = "1.1";

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
    private Label lblUserRegisterDate;
    @FXML
    private Label lblVersion;

    @FXML
    private TextField searchUserField;

    @FXML
    private CheckBox activeUsersCheckBox;
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
    private HBox forumMainContainer;
    @FXML
    private HBox adminForumActions;
    @FXML
    private VBox forumThreadView;
    @FXML
    private VBox commentsContainer;
    @FXML
    private Label lblThreadTitle, lblThreadAuthor, lblThreadDate, lblThreadBody;
    @FXML
    private TextArea txtCommentInput;
    @FXML
    private Button btnPostComment;

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
    @FXML
    private TableColumn<UserDto, String> colRegisterDate;

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

    @FXML
    private ListView<ForumDto> forumListView;
    
    @FXML
    private ListView<TopicDto> categoryListView;
    @FXML
    private Label lblSelectedCategory;

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObservableList<UserDto> usuarios = FXCollections.observableArrayList();
    private final FilteredList<UserDto> filteredUsuarios = new FilteredList<>(usuarios);

    private final EventApiClient eventApiClient = new EventApiClient();
    private final ObservableList<EventDto> eventos = FXCollections.observableArrayList();

    private final MessageApiClient messageApiClient = new MessageApiClient();
    private final ObservableList<MessageDto> mensajes = FXCollections.observableArrayList();

    private final ForumApiClient forumApiClient = new ForumApiClient();
    private final ObservableList<ForumDto> foroPosts = FXCollections.observableArrayList();

    private final TopicApiClient topicApiClient = new TopicApiClient();
    private final ObservableList<TopicDto> foroTopics = FXCollections.observableArrayList();

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
        // Asegurarse de que activeUsersCheckBox no sea nulo antes de usarlo
        // Si el FXML no lo inicializa, podría ser nulo aquí.
        // Sin embargo, @FXML inyecta los componentes antes de initialize.
        UserDto currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            lblUsername.setText(currentUser.getUsername());
            lblUserRole.setText(currentUser.getRole());
            lblUserEmail.setText(currentUser.getEmail());
            lblUserRegisterDate.setText(com.aja.util.DateUtils.format(currentUser.getRegisterDate()));

            // Establecer la versión configurable
            lblVersion.setText("AJA Desktop v" + APP_VERSION);

            // Mostrar u ocultar el botón "Usuarios" según el rol
            boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
            
            // Permisos Usuarios: Consulta solo ADMIN
            btnUsuarios.setVisible(isAdmin);
            btnUsuarios.setManaged(isAdmin);
            
            // Desactivamos el botón de alta de usuarios en el panel por requerimiento
            newUserButton.setVisible(false);
            newUserButton.setManaged(false);
            
            // Permisos Forum: Registro/Alta solo ADMIN
            newForumButton.setVisible(isAdmin);
            newForumButton.setManaged(isAdmin);

            // Si no es administrador, enfocar el botón Foro al iniciar
            if (!isAdmin) {
                btnForo.requestFocus();
            }
        }

        menuButtons = List.of(btnUsuarios, btnForo, btnEventos, btnMensajes);

        // En lugar de cargar una pestaña por defecto, mostramos la bienvenida
        showWelcomeView();
        
        // Configuramos el filtrado en tiempo real
        setupUserFilterListeners();
        
        if (usuariosTable != null) {
            colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
            colRegisterDate.setCellValueFactory(new PropertyValueFactory<>("registerDate"));

            // Aplicar el formato dd/MM/yyyy a las celdas de la columna Fecha Registro
            colRegisterDate.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : com.aja.util.DateUtils.format(item));
                }
            });

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
                        setText(null);
                    } else {
                        VBox card = new VBox(8);
                        card.getStyleClass().add("forum-card");
                        
                        // Metadata: Estilo Reddit (f/comunidad • u/autor • fecha)
                        HBox metaBox = new HBox(5);
                        metaBox.setAlignment(Pos.CENTER_LEFT);
                        
                        // Mostramos la categoría real del post si existe
                        String categoryName = item.getCategory() != null ? "f/" + item.getCategory().toLowerCase() : "f/general";
                        Label community = new Label(categoryName);
                        community.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
                        
                        String authorName = item.getAuthor() != null ? item.getAuthor() : "anónimo";
                        String dateStr = item.getDate() != null ? com.aja.util.DateUtils.format(item.getDate()) : "reciente";
                        
                        Label authorInfo = new Label(" • Posteado por u/" + authorName + " • " + dateStr);
                        authorInfo.getStyleClass().add("forum-card-meta");
                        
                        metaBox.getChildren().addAll(community, authorInfo);

                        // Título destacado
                        Label title = new Label(item.getTitle());
                        title.getStyleClass().add("forum-card-title");
                        title.setWrapText(true);
                        
                        // Footer de acciones (Simulado para estética Reddit)
                        HBox actions = new HBox(15);
                        actions.setPadding(new javafx.geometry.Insets(5, 0, 0, 0));
                        // Conteo real de comentarios
                        int commentCount = (item.getComments() != null) ? item.getComments().size() : 0;
                        Label comments = new Label("💬 " + commentCount + " Comentarios");
                        Label share = new Label("🔗 Compartir");
                        comments.getStyleClass().add("forum-card-meta");
                        share.getStyleClass().add("forum-card-meta");
                        actions.getChildren().addAll(comments, share);

                        card.getChildren().addAll(metaBox, title, actions);
                        setGraphic(card);
                    }
                }
            });

            // Al hacer doble clic en un tema del foro, abrimos el detalle (especialmente para que el admin lo edite)
            // Al hacer doble clic en un tema del foro, abrimos el detalle
            forumListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    ForumDto selected = forumListView.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        showForumThread(selected);
                    }
                }
            });
        }

        // Inicializar la lista de categorías (comunidades)
        if (categoryListView != null) {
            categoryListView.setItems(foroTopics);
            
            // Personalizamos la celda para mostrar el nombre del Topic
            categoryListView.setCellFactory(lv -> new ListCell<TopicDto>() {
                @Override
                protected void updateItem(TopicDto item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTitle());
                }
            });
            
            categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && lblSelectedCategory != null) {
                    lblSelectedCategory.setText(newVal.getTitle());
                    // Al cambiar de comunidad, recargamos los posts
                    loadForumPosts();
                }
            });
        }
    }

    /**
     * Cambia la interfaz para mostrar el detalle de un hilo específico.
     * Oculta la lista general y muestra el contenido del post con sus comentarios.
     */
    private void showForumThread(ForumDto postSummary) {
        if (forumMainContainer == null || forumThreadView == null) return;

        try {
            // Según tu ForumController: getForum(id) obtiene TODO el contenido de la entidad
            ForumDto fullPost = forumApiClient.getForumById(postSummary.getId());
            
            // Log de depuración del objeto mapeado
            System.out.println("DEBUG ForumDto Mapeado:");
            System.out.println(" -> ID: " + fullPost.getId());
            System.out.println(" -> Content: " + (fullPost.getContent() == null ? "NULL" : "Presente"));
            System.out.println(" -> Comments: " + (fullPost.getComments() != null ? fullPost.getComments().size() : "NULL"));

            // Alternar visibilidad de los contenedores
            forumMainContainer.setVisible(false);
            forumMainContainer.setManaged(false);
            forumThreadView.setVisible(true);
            forumThreadView.setManaged(true);

            // Mostrar acciones de ADMIN si corresponde (edit/delete)
            boolean isAdmin = "ADMIN".equalsIgnoreCase(authService.getCurrentUser().getRole());
            adminForumActions.setVisible(isAdmin);
            adminForumActions.setManaged(isAdmin);

            // Cargar datos del post
            lblThreadTitle.setText(fullPost.getTitle());
            lblThreadAuthor.setText("u/" + fullPost.getAuthor());
            lblThreadDate.setText(" • " + (fullPost.getDate() != null ? com.aja.util.DateUtils.format(fullPost.getDate()) : "ahora"));
            
            // Cargamos el contenido real del post. 
            // Si la API devuelve el objeto equivocado (Categoría), estos campos serán nulos.
            String content = fullPost.getContent();
            lblThreadBody.setText(content != null && !content.isBlank() ? content : "No hay contenido disponible para este hilo.");

            // Guardamos el ID actual para las acciones de edición/borrado
            forumThreadView.setUserData(fullPost);

            // Limpiar y cargar comentarios
            commentsContainer.getChildren().clear();
            if (fullPost.getComments() != null && !fullPost.getComments().isEmpty()) {
                fullPost.getComments().forEach(comment -> 
                    addCommentToView(comment.getContent(), comment.getAuthor(), comment.getDate())
                );
            } else {
                Label noComments = new Label("No se han encontrado comentarios en la respuesta del servidor.");
                noComments.getStyleClass().add("content-hint");
                commentsContainer.getChildren().add(noComments);
            }

        } catch (Exception e) {
            showError("Error al cargar hilo", "No se pudo obtener el contenido del post: " + e.getMessage());
            return;
        }
    }

    /**
     * Abre el diálogo de edición para el hilo que se está visualizando.
     */
    @FXML
    private void handleEditCurrentThread() {
        ForumDto currentPost = (ForumDto) forumThreadView.getUserData();
        if (currentPost != null) {
            showForumDetails(currentPost.getId());
        }
    }

    /**
     * Ejecuta la eliminación del hilo actual tras confirmación.
     */
    @FXML
    private void handleDeleteCurrentThread() {
        ForumDto currentPost = (ForumDto) forumThreadView.getUserData();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar este hilo permanentemente?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    forumApiClient.deleteForumPost(currentPost.getId());
                    backToForumList();
                    loadForumPosts();
                    showInfo("Eliminado", "El hilo ha sido borrado correctamente.");
                } catch (Exception e) {
                    showError("Error", "No se pudo eliminar: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Gestiona el envío de un nuevo comentario.
     */
    @FXML
    private void handlePostComment() {
        String commentText = txtCommentInput.getText();
        if (commentText == null || commentText.isBlank()) return;

        // Recuperamos el post que estamos visualizando actualmente
        ForumDto currentPost = (ForumDto) forumThreadView.getUserData();
        if (currentPost == null) return;

        try {
            // Creamos el nuevo objeto de comentario
            CommentDto newComment = new CommentDto();
            newComment.setContent(commentText);
            newComment.setAuthor(authService.getCurrentUser().getUsername());
            // La fecha será asignada por el servidor o formateada al recargar
            
            // Añadimos el comentario a la lista local del DTO
            if (currentPost.getComments() == null) {
                currentPost.setComments(new java.util.ArrayList<>());
            }
            currentPost.getComments().add(newComment);

            // Enviamos la actualización al servidor usando el método editForum (PUT /api/forum)
            forumApiClient.updateForumPost(currentPost);

            // Limpiamos el campo de texto
            txtCommentInput.clear();

            // Refrescamos la vista del hilo llamando de nuevo a la API para confirmar los datos
            showForumThread(currentPost);
            
            showInfo("Comentario enviado", "Tu respuesta ha sido publicada correctamente.");

        } catch (Exception e) {
            showError("Error al comentar", "No se pudo publicar el comentario en el servidor: " + e.getMessage());
        }
    }

    /**
     * Crea dinámicamente una tarjeta de comentario y la añade al contenedor.
     */
    private void addCommentToView(String text, String user, String time) {
        VBox commentBox = new VBox(5);
        commentBox.getStyleClass().add("comment-card");
        
        HBox meta = new HBox(5);
        Label author = new Label(user);
        author.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1e293b;");
        Label date = new Label(" • " + time);
        date.getStyleClass().add("forum-card-meta");
        meta.getChildren().addAll(author, date);

        Label content = new Label(text);
        content.setWrapText(true);
        content.setStyle("-fx-text-fill: #334155;");

        commentBox.getChildren().addAll(meta, content);
        commentsContainer.getChildren().add(commentBox);
    }

    /**
     * Acción para el botón de retroceso que vuelve a la lista de hilos.
     */
    @FXML
    private void backToForumList() {
        forumThreadView.setVisible(false);
        forumThreadView.setManaged(false);
        forumMainContainer.setVisible(true);
        forumMainContainer.setManaged(true);
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
        backToForumList(); // Aseguramos que se vea la lista y no un hilo previo
        loadTopics();
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
     * Configura los listeners para el campo de búsqueda y el checkbox de usuarios activos.
     */
    private void setupUserFilterListeners() {
        if (searchUserField != null && activeUsersCheckBox != null) {
            searchUserField.textProperty().addListener((observable, oldValue, newValue) -> updateUsersFilter());
            activeUsersCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateUsersFilter());
            // Aplicar el filtro inicial al cargar
            updateUsersFilter();
        }
    }

    /**
     * Actualiza el predicado de la lista filtrada de usuarios basándose en el texto de búsqueda
     * y el estado del checkbox de usuarios activos.
     */
    private void updateUsersFilter() {
        filteredUsuarios.setPredicate(user -> {
            // 1. Filtrar por estado activo si el checkbox está marcado
            if (activeUsersCheckBox.isSelected() && !user.isActive()) {
                return false; // Si el checkbox está marcado, solo mostramos usuarios activos
            }

            // 2. Filtrar por texto de búsqueda
            String searchText = searchUserField.getText();
            if (searchText == null || searchText.isBlank()) {
                return true; // Si no hay texto de búsqueda, el filtro del checkbox es suficiente
            }

            String lowerCaseFilter = searchText.toLowerCase().trim();

            // Comprobamos si el nombre de usuario o el email contienen el texto de búsqueda
            if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false; // No hay coincidencia ni por búsqueda ni por estado activo
        });
    }

    /**
     * Maneja la acción del checkbox "Solo usuarios activos".
     * Simplemente llama a updateUsersFilter para aplicar el nuevo filtro.
     */
    @FXML
    private void handleActiveUsersFilter() {
        updateUsersFilter();
    }

    /**
     * Llama a la API para traer los usuarios y actualiza la tabla en el hilo de la UI.
     */
    private void loadUsers() {
        try {
            List<UserDto> list = userApiClient.getAllUsers();
            Platform.runLater(() -> {
                usuarios.clear();
                if (list != null) {
                    usuarios.setAll(list);
                    System.out.println("INFO: " + list.size() + " usuarios cargados.");
                }
                updateUsersFilter();
            });
        } catch (Exception e) {
            System.err.println("Error al cargar la lista de usuarios:");
            System.err.println("ERROR 403: No tienes permisos para ver usuarios. Revisa el token en el servidor."); // Mensaje para consola
            e.printStackTrace();
            Platform.runLater(() -> showError("Error de Acceso", "No tienes permisos para ver esta lista (403)."));
        }
    }

    /**
     * Esta es la parte de CONSULTA y MODIFICACIÓN para el Administrador.
     * Al hacer doble clic en la tabla, abrimos un panel para editar al usuario seleccionado.
     */
    private void showUserDetails(Long userId) {
        try {
            // Traemos los datos frescos del usuario desde la API
            final UserDto user = userApiClient.getUserById(userId);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Detalles del Usuario");
            dialog.setResizable(false);

            VBox layout = new VBox(20);
            layout.setPadding(new javafx.geometry.Insets(30));
            layout.getStyleClass().add("content-pane");
            layout.setPrefWidth(420);

            // Cabecera estilizada
            VBox header = new VBox(5);
            Label titleLabel = new Label("Gestión de Usuario");
            titleLabel.getStyleClass().add("content-page-title");
            Label subtitleLabel = new Label("Consulta o modifica la información del usuario");
            subtitleLabel.getStyleClass().add("content-page-subtitle");
            header.getChildren().addAll(titleLabel, subtitleLabel);

            // Información destacada
            VBox userInfo = new VBox(8);
            Label nameLabel = new Label(user.getUsername());
            nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            Label idLabel = new Label("ID: " + user.getId() + " • Registro: " + com.aja.util.DateUtils.format(user.getRegisterDate()));
            idLabel.getStyleClass().add("content-hint");
            userInfo.getChildren().addAll(nameLabel, idLabel);

            // Formulario
            VBox form = new VBox(15);

            VBox emailGroup = new VBox(5);
            Label emailTitle = new Label("CORREO ELECTRÓNICO");
            emailTitle.getStyleClass().add("user-label");
            TextField emailField = new TextField(user.getEmail());
            emailField.getStyleClass().add("search-field");
            emailField.setStyle("-fx-padding: 8 12;");
            emailGroup.getChildren().addAll(emailTitle, emailField);

            VBox roleGroup = new VBox(5);
            Label roleTitle = new Label("ROL DEL SISTEMA");
            roleTitle.getStyleClass().add("user-label");
            ComboBox<String> roleCombo = new ComboBox<>(FXCollections.observableArrayList("USER", "ADMIN"));
            roleCombo.setValue(user.getRole());
            roleCombo.setMaxWidth(Double.MAX_VALUE);
            roleCombo.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");
            roleGroup.getChildren().addAll(roleTitle, roleCombo);

            VBox passGroup = new VBox(5);
            Label passTitle = new Label("CONTRASEÑA (Para confirmar o cambiar)");
            passTitle.getStyleClass().add("user-label");
            PasswordField passField = new PasswordField();
            passField.getStyleClass().add("search-field");
            passField.setStyle("-fx-padding: 8 12;");
            passGroup.getChildren().addAll(passTitle, passField);

            CheckBox activeCheck = new CheckBox("Usuario Activo");
            activeCheck.setSelected(user.isActive());
            activeCheck.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b;");
            
            form.getChildren().addAll(emailGroup, roleGroup, passGroup, activeCheck);

            Button btnSave = new Button("Guardar Cambios");
            btnSave.getStyleClass().add("primary-action-button");
            btnSave.setMaxWidth(Double.MAX_VALUE);
            btnSave.setPrefHeight(40);
            
            Button btnDisable = new Button("Deshabilitar Acceso");
            btnDisable.setStyle("-fx-background-color: transparent; -fx-text-fill: #f59e0b; -fx-border-color: #f59e0b; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
            btnDisable.setMaxWidth(Double.MAX_VALUE);
            
            Button btnDelete = new Button("Eliminar Usuario");
            btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
            btnDelete.setMaxWidth(Double.MAX_VALUE);

            layout.getChildren().addAll(header, new Separator(), userInfo, form, btnSave, btnDisable, new Separator(), btnDelete);

            btnSave.setOnAction(e -> {
                try {
                    user.setEmail(emailField.getText());
                    user.setRole(roleCombo.getValue());
                    user.setActive(activeCheck.isSelected()); // Usamos el setter correcto
                    user.setPassword(passField.getText()); // Mandamos la pass para que el PUT no falle
                    
                    userApiClient.updateUser(user);
                    loadUsers(); // Refrescamos la tabla principal
                    dialog.close();
                    showInfo("Actualizado", "Los datos del usuario se han guardado correctamente.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error al actualizar", "No se han guardado los cambios: " + ex.getMessage());
                }
            });

            // Acción para deshabilitar al usuario (endpoint disableUser)
            btnDisable.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Quieres revocar el acceso a este usuario?");
                confirm.setTitle("Confirmar Deshabilitar");
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        try {
                            userApiClient.disableUser(user.getId());
                            loadUsers();
                            dialog.close();
                            showInfo("Éxito", "El usuario ha sido deshabilitado correctamente.");
                        } catch (Exception ex) {
                            showError("Error", "No se pudo deshabilitar: " + ex.getMessage());
                        }
                    }
                });
            });

            // El Admin puede dar de BAJA a otros
            btnDelete.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres borrar a este usuario?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        try {
                            userApiClient.deleteUser(user.getId());
                            loadUsers();
                            dialog.close();
                        } catch (Exception ex) {
                            showError("Error", "No se pudo eliminar al usuario.");
                        }
                    }
                });
            });

            Scene scene = new Scene(layout);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            dialog.setScene(scene);
            dialog.show();

        } catch (Exception e) {
            showError("Error", "No se pudo obtener la información del usuario.");
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
     * Carga las categorías (Topics) desde /api/topic para la barra lateral.
     */
    private void loadTopics() {
        try {
            List<TopicDto> list = topicApiClient.getAllTopics();
            Platform.runLater(() -> {
                foroTopics.clear();
                foroTopics.add(new TopicDto(0L, "Todos los temas")); // Opción por defecto
                if (list != null) foroTopics.addAll(list);
                categoryListView.getSelectionModel().selectFirst();
            });
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }
    }

    /**
     * Actualiza los temas del foro.
     */
    private void loadForumPosts() {
        try {
            TopicDto selectedTopic = categoryListView.getSelectionModel().getSelectedItem();
            List<ForumDto> list = forumApiClient.getAllForumPosts();
            Platform.runLater(() -> {
                foroPosts.clear();
                if (list != null) {
                    // Filtramos los posts (de /api/topic) según la categoría seleccionada (de /api/forum)
                    if (selectedTopic != null && selectedTopic.getId() != 0L) {
                        list.stream()
                            .filter(p -> selectedTopic.getId().equals(p.getForumId()))
                            .forEach(foroPosts::add);
                    } else {
                        foroPosts.addAll(list);
                    }
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError("Error de Foro", "No se pudieron cargar los temas: " + e.getMessage()));
        }
    }

    /**
     * Abre el diálogo para ver, editar o borrar un tema del foro.
     * Solo los administradores podrán ver los botones de acción.
     */
    private void showForumDetails(Long forumId) {
        try {
            ForumDto post = forumApiClient.getForumById(forumId);
            UserDto currentUser = authService.getCurrentUser();
            boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Detalle del Foro");
            dialog.setResizable(false);

            VBox layout = new VBox(20);
            layout.setPadding(new javafx.geometry.Insets(30));
            layout.getStyleClass().add("content-pane");
            layout.setPrefWidth(460);

            VBox header = new VBox(5);
            Label titleLabel = new Label("Tema del Foro");
            titleLabel.getStyleClass().add("content-page-title");
            header.getChildren().add(titleLabel);

            VBox form = new VBox(15);
            
            Label titleHint = new Label("TÍTULO DEL TEMA");
            titleHint.getStyleClass().add("user-label");
            TextField titleField = new TextField(post.getTitle());
            titleField.getStyleClass().add("search-field");
            titleField.setEditable(isAdmin); // Solo edita el admin

            Label authorHint = new Label("AUTOR");
            authorHint.getStyleClass().add("user-label");
            TextField authorField = new TextField(post.getAuthor());
            authorField.getStyleClass().add("search-field");
            authorField.setEditable(isAdmin);

            form.getChildren().addAll(titleHint, titleField, authorHint, authorField);

            layout.getChildren().addAll(header, new Separator(), form);

            if (isAdmin) {
                Button btnUpdate = new Button("Guardar Cambios");
                btnUpdate.getStyleClass().add("primary-action-button");
                btnUpdate.setMaxWidth(Double.MAX_VALUE);

                Button btnDelete = new Button("Eliminar Tema");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
                btnDelete.setMaxWidth(Double.MAX_VALUE);

                btnUpdate.setOnAction(e -> {
                    try {
                        post.setTitle(titleField.getText());
                        post.setAuthor(authorField.getText());
                        forumApiClient.updateForumPost(post);
                        loadForumPosts();
                        dialog.close();
                        showInfo("Éxito", "Tema actualizado correctamente.");
                    } catch (Exception ex) {
                        showError("Error", "No se pudo actualizar: " + ex.getMessage());
                    }
                });

                btnDelete.setOnAction(e -> {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que quieres borrar este tema?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == javafx.scene.control.ButtonType.OK) {
                            try {
                                forumApiClient.deleteForumPost(post.getId());
                                loadForumPosts();
                                dialog.close();
                            } catch (Exception ex) {
                                showError("Error", "No se pudo borrar el tema.");
                            }
                        }
                    });
                });

                layout.getChildren().addAll(btnUpdate, btnDelete);
            }

            Button btnClose = new Button("Cerrar");
            btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-weight: 600;");
            btnClose.setMaxWidth(Double.MAX_VALUE);
            btnClose.setOnAction(e -> dialog.close());
            layout.getChildren().add(btnClose);

            Scene scene = new Scene(layout);
            scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
            dialog.setScene(scene);
            dialog.show();
        } catch (Exception e) {
            showError("Error", "No se pudo cargar el detalle del foro.");
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
     * Esta es la parte de MI PERFIL. 
     * Aquí el usuario (sea User o Admin) puede MODIFICAR su email o darse de BAJA.
     * Por seguridad, pedimos la contraseña para confirmar.
     */
    @FXML
    private void handleEditProfile() {
        final UserDto user = authService.getCurrentUser();
        if (user == null) return;

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Mi Perfil");
        dialog.setResizable(false);

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.getStyleClass().add("content-pane");
        layout.setPrefWidth(420);

        // Cabecera estilizada siguiendo el patrón del Dashboard
        VBox header = new VBox(5);
        Label titleLabel = new Label("Mi Perfil");
        titleLabel.getStyleClass().add("content-page-title");
        Label subtitleLabel = new Label("Gestiona tu información y seguridad");
        subtitleLabel.getStyleClass().add("content-page-subtitle");
        header.getChildren().addAll(titleLabel, subtitleLabel);

        // Información de cuenta (Usuario y Rol)
        VBox userInfo = new VBox(8);
        Label nameLabel = new Label(user.getUsername());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label roleLabel = new Label(user.getRole());
        roleLabel.getStyleClass().add("user-role"); // Reutilizamos el estilo de etiqueta de rol
        
        Label dateLabel = new Label("Miembro desde: " + com.aja.util.DateUtils.format(user.getRegisterDate()));
        dateLabel.getStyleClass().add("content-hint");
        userInfo.getChildren().addAll(nameLabel, roleLabel, dateLabel);

        // Formulario de edición
        VBox form = new VBox(15);
        
        VBox emailGroup = new VBox(5);
        Label emailTitle = new Label("CORREO ELECTRÓNICO");
        emailTitle.getStyleClass().add("user-label");
        TextField emailField = new TextField(user.getEmail());
        emailField.getStyleClass().add("search-field");
        emailField.setStyle("-fx-padding: 8 12 8 12;"); // Ajustamos padding para que no herede el hueco de la lupa
        emailGroup.getChildren().addAll(emailTitle, emailField);

        VBox passGroup = new VBox(5);
        Label passTitle = new Label("CONFIRMAR CAMBIOS");
        passTitle.getStyleClass().add("user-label");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Contraseña actual");
        confirmPassField.getStyleClass().add("search-field");
        confirmPassField.setStyle("-fx-padding: 8 12 8 12;");
        passGroup.getChildren().addAll(passTitle, confirmPassField);
        
        form.getChildren().addAll(emailGroup, passGroup);

        Button btnUpdate = new Button("Actualizar Información");
        btnUpdate.getStyleClass().add("primary-action-button");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setPrefHeight(40);
        
        Button btnBaja = new Button("Cerrar mi cuenta");
        btnBaja.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
        btnBaja.setMaxWidth(Double.MAX_VALUE);

        layout.getChildren().addAll(header, new Separator(), userInfo, form, btnUpdate, new Separator(), btnBaja);

        // MODIFICACIÓN propia
        btnUpdate.setOnAction(e -> {
            String pass = confirmPassField.getText();
            if (pass == null || pass.isBlank()) {
                showError("Seguridad", "Debes introducir tu contraseña para realizar cambios.");
                return;
            }

            try {
                // Validamos que la contraseña sea correcta antes de nada
                if (!authService.authenticate(user.getUsername(), pass).isSuccess()) {
                    showError("Error", "Contraseña incorrecta.");
                    return;
                }

                // Actualizamos los tokens de todos los clientes con el nuevo token generado
                setToken(authService.getToken());

                user.setEmail(emailField.getText());
                user.setPassword(pass); // Seteamos la contraseña en el objeto antes de enviarlo
                userApiClient.updateUser(user);
                lblUserEmail.setText(user.getEmail());
                dialog.close();
                showInfo("Perfil actualizado", "Los cambios se han guardado.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Error", "No se han guardado los cambios: " + ex.getMessage());
            }
        });

        // BAJA propia
        btnBaja.setOnAction(e -> {
            String pass = confirmPassField.getText();
            if (pass == null || pass.isBlank()) {
                showError("Seguridad", "Debes introducir tu contraseña para cerrar la cuenta.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que quieres borrar tu cuenta?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        if (!authService.authenticate(user.getUsername(), pass).isSuccess()) {
                            showError("Error", "Contraseña incorrecta.");
                            return;
                        }
                        
                        userApiClient.deleteUser(user.getId());
                        dialog.close();
                        handleLogout(new ActionEvent(btnLogout, btnLogout));
                    } catch (Exception ex) {
                        showError("Error", "Hubo un problema al tramitar la baja.");
                    }
                }
            });
        });

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.getStyleClass().add("content-pane");
        layout.setPrefWidth(460);

        VBox header = new VBox(5);
        Label titleLabel = new Label("Nuevo Usuario");
        titleLabel.getStyleClass().add("content-page-title");
        Label subtitleLabel = new Label("Registra un nuevo usuario en el sistema");
        subtitleLabel.getStyleClass().add("content-page-subtitle");
        header.getChildren().addAll(titleLabel, subtitleLabel);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nombre de usuario");
        usernameField.getStyleClass().add("search-field");
        usernameField.setStyle("-fx-padding: 8 12;");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Contraseña");
        passwordField.getStyleClass().add("search-field");
        passwordField.setStyle("-fx-padding: 8 12;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("search-field");
        emailField.setStyle("-fx-padding: 8 12;");

        Button okButton = new Button("Crear Usuario");
        okButton.getStyleClass().add("primary-action-button");
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefHeight(40);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-weight: 600;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(15);

        VBox userGroup = new VBox(5);
        Label userTitle = new Label("NOMBRE DE USUARIO");
        userTitle.getStyleClass().add("user-label");
        userGroup.getChildren().addAll(userTitle, usernameField);

        VBox passGroup = new VBox(5);
        Label passTitle = new Label("CONTRASEÑA");
        passTitle.getStyleClass().add("user-label");
        passGroup.getChildren().addAll(passTitle, passwordField);

        VBox emailGroup = new VBox(5);
        Label emailTitle = new Label("EMAIL");
        emailTitle.getStyleClass().add("user-label");
        emailGroup.getChildren().addAll(emailTitle, emailField);

        form.getChildren().addAll(userGroup, passGroup, emailGroup);

        layout.getChildren().addAll(header, new Separator(), form, okButton, cancelButton);

        okButton.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();
                String email = emailField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                UserNewDto newUser = new UserNewDto();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);

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

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.getStyleClass().add("content-pane");
        layout.setPrefWidth(460);

        VBox header = new VBox(5);
        Label titleLabel = new Label("Nuevo Tema");
        titleLabel.getStyleClass().add("content-page-title");
        Label subtitleLabel = new Label("Inicia una nueva conversación en el foro");
        subtitleLabel.getStyleClass().add("content-page-subtitle");
        header.getChildren().addAll(titleLabel, subtitleLabel);

        TextField titleField = new TextField();
        titleField.setPromptText("Título del tema");
        titleField.getStyleClass().add("search-field");
        titleField.setStyle("-fx-padding: 8 12;");

        TextField authorField = new TextField();
        authorField.setPromptText("Autor (username)");
        authorField.getStyleClass().add("search-field");
        authorField.setStyle("-fx-padding: 8 12;");

        Button okButton = new Button("Crear Tema");
        okButton.getStyleClass().add("primary-action-button");
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefHeight(40);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-weight: 600;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(15);

        VBox titleGroup = new VBox(5);
        Label titleLabelGroup = new Label("TÍTULO DEL TEMA");
        titleLabelGroup.getStyleClass().add("user-label");
        titleGroup.getChildren().addAll(titleLabelGroup, titleField);

        VBox authorGroup = new VBox(5);
        Label authorLabelGroup = new Label("AUTOR");
        authorLabelGroup.getStyleClass().add("user-label");
        authorGroup.getChildren().addAll(authorLabelGroup, authorField);

        form.getChildren().addAll(titleGroup, authorGroup);

        layout.getChildren().addAll(header, new Separator(), form, okButton, cancelButton);

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

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.getStyleClass().add("content-pane");
        layout.setPrefWidth(460);

        VBox header = new VBox(5);
        Label titleLabel = new Label("Nuevo Evento");
        titleLabel.getStyleClass().add("content-page-title");
        Label subtitleLabel = new Label("Organiza una nueva actividad");
        subtitleLabel.getStyleClass().add("content-page-subtitle");
        header.getChildren().addAll(titleLabel, subtitleLabel);

        TextField titleField = new TextField();
        titleField.setPromptText("Título del evento");
        titleField.getStyleClass().add("search-field");
        titleField.setStyle("-fx-padding: 8 12;");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Descripción");
        descriptionField.getStyleClass().add("search-field");
        descriptionField.setStyle("-fx-padding: 8 12;");

        TextField dateField = new TextField();
        dateField.setPromptText("Fecha (ej: 2026-03-15)");
        dateField.getStyleClass().add("search-field");
        dateField.setStyle("-fx-padding: 8 12;");

        TextField locationField = new TextField();
        locationField.setPromptText("Ubicación");
        locationField.getStyleClass().add("search-field");
        locationField.setStyle("-fx-padding: 8 12;");

        Button okButton = new Button("Crear Evento");
        okButton.getStyleClass().add("primary-action-button");
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefHeight(40);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-weight: 600;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(15);

        VBox titleGroup = new VBox(5);
        Label titleLabelGroup = new Label("TÍTULO");
        titleLabelGroup.getStyleClass().add("user-label");
        titleGroup.getChildren().addAll(titleLabelGroup, titleField);

        VBox descGroup = new VBox(5);
        Label descLabelGroup = new Label("DESCRIPCIÓN");
        descLabelGroup.getStyleClass().add("user-label");
        descGroup.getChildren().addAll(descLabelGroup, descriptionField);

        VBox dateGroup = new VBox(5);
        Label dateLabelGroup = new Label("FECHA");
        dateLabelGroup.getStyleClass().add("user-label");
        dateGroup.getChildren().addAll(dateLabelGroup, dateField);

        VBox locGroup = new VBox(5);
        Label locLabelGroup = new Label("UBICACIÓN");
        locLabelGroup.getStyleClass().add("user-label");
        locGroup.getChildren().addAll(locLabelGroup, locationField);

        form.getChildren().addAll(titleGroup, descGroup, dateGroup, locGroup);

        layout.getChildren().addAll(header, new Separator(), form, okButton, cancelButton);

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

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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

        VBox layout = new VBox(20);
        layout.setPadding(new javafx.geometry.Insets(30));
        layout.getStyleClass().add("content-pane");
        layout.setPrefWidth(460);

        VBox header = new VBox(5);
        Label titleLabel = new Label("Nuevo Mensaje");
        titleLabel.getStyleClass().add("content-page-title");
        Label subtitleLabel = new Label("Envía un mensaje directo a otro usuario");
        subtitleLabel.getStyleClass().add("content-page-subtitle");
        header.getChildren().addAll(titleLabel, subtitleLabel);

        TextField senderField = new TextField();
        senderField.setPromptText("Remitente (username)");
        senderField.getStyleClass().add("search-field");
        senderField.setStyle("-fx-padding: 8 12;");

        TextField receiverField = new TextField();
        receiverField.setPromptText("Destinatario (username)");
        receiverField.getStyleClass().add("search-field");
        receiverField.setStyle("-fx-padding: 8 12;");

        TextField contentField = new TextField();
        contentField.setPromptText("Contenido del mensaje");
        contentField.getStyleClass().add("search-field");
        contentField.setStyle("-fx-padding: 8 12;");

        CheckBox readCheck = new CheckBox("Marcar como leído");
        readCheck.setStyle("-fx-font-weight: 600; -fx-text-fill: #1e293b;");

        Button okButton = new Button("Enviar Mensaje");
        okButton.getStyleClass().add("primary-action-button");
        okButton.setMaxWidth(Double.MAX_VALUE);
        okButton.setPrefHeight(40);

        Button cancelButton = new Button("Cancelar");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-cursor: hand; -fx-font-weight: 600;");
        cancelButton.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(15);

        VBox senderGroup = new VBox(5);
        Label senderTitle = new Label("REMITENTE");
        senderTitle.getStyleClass().add("user-label");
        senderGroup.getChildren().addAll(senderTitle, senderField);

        VBox receiverGroup = new VBox(5);
        Label receiverTitle = new Label("DESTINATARIO");
        receiverTitle.getStyleClass().add("user-label");
        receiverGroup.getChildren().addAll(receiverTitle, receiverField);

        VBox contentGroup = new VBox(5);
        Label contentTitle = new Label("CONTENIDO");
        contentTitle.getStyleClass().add("user-label");
        contentGroup.getChildren().addAll(contentTitle, contentField);

        form.getChildren().addAll(senderGroup, receiverGroup, contentGroup, readCheck);

        layout.getChildren().addAll(header, new Separator(), form, okButton, cancelButton);

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

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
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
     * Alerta rápida para mensajes de información.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
        topicApiClient.setToken(token);
    }
}

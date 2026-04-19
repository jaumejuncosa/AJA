package com.aja.controller;

import com.aja.api.UserApiClient;
import com.aja.api.EventApiClient;
import com.aja.api.MessageApiClient;
import com.aja.api.ForumApiClient;
import com.aja.api.PostApiClient;
import com.aja.api.TopicApiClient;
import com.aja.model.UserDto;
import com.aja.model.UserNewDto;
import com.aja.model.EventDto;
import com.aja.model.MessageDto;
import com.aja.model.CommentDto;
import com.aja.model.ForumDto;
import com.aja.model.PostDto; // Usaremos PostDto en lugar de ForumDto
import com.aja.model.LoginResponseDto;
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
 * Controla el panel principal. Organiza las secciones de usuarios, foro, eventos y mensajes.
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
    private VBox usuariosContent; // Mantener este VBox
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
    private VBox forumThreadView; // Mantener este VBox
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
    private ListView<PostDto> forumListView; // Lista de posts central
    
    @FXML
    private ListView<ForumDto> categoryListView; // Sidebar de Comunidades (Forums)
    @FXML
    private Label lblSelectedCategory;

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObservableList<UserDto> usuarios = FXCollections.observableArrayList();
    private final FilteredList<UserDto> filteredUsuarios = new FilteredList<>(usuarios);

    private final EventApiClient eventApiClient = new EventApiClient();
    private final ObservableList<EventDto> eventos = FXCollections.observableArrayList();

    private final MessageApiClient messageApiClient = new MessageApiClient();
    private final ObservableList<MessageDto> mensajes = FXCollections.observableArrayList();

    private final PostApiClient postApiClient = new PostApiClient();
    private final ObservableList<PostDto> foroPosts = FXCollections.observableArrayList(); // Ahora ObservableList de PostDto
    
    private final ForumApiClient forumApiClient = new ForumApiClient();
    private final TopicApiClient topicApiClient = new TopicApiClient();
    private final ObservableList<TopicDto> foroTopics = FXCollections.observableArrayList(); // Para uso futuro (subcategorías)

    private final AuthService authService = AuthService.getInstance();

    private List<Button> menuButtons;
    private String token;

    /**
     * Configuración inicial al abrir el panel: Prepara las listas, tablas y colores.
     */
    @FXML
    public void initialize() {
         System.out.println("Inicializando Dashboard...");
         
        // Mostrar información de la persona que ha entrado
        UserDto currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            lblUsername.setText(currentUser.getUsername());
            lblUserRole.setText(currentUser.getRole());
            lblUserEmail.setText(currentUser.getEmail());
            lblUserRegisterDate.setText(DateUtils.format(currentUser.getRegisterDate()));

            lblVersion.setText("AJA Desktop v" + APP_VERSION);

            boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
                        
            btnUsuarios.setVisible(isAdmin);
            btnUsuarios.setManaged(isAdmin);
            
            newUserButton.setVisible(false);
            newUserButton.setManaged(false);
            
            newForumButton.setVisible(true);
            newForumButton.setManaged(true);

            if (!isAdmin) {
                btnForo.requestFocus();
            }
        }

        menuButtons = List.of(btnUsuarios, btnForo, btnEventos, btnMensajes);

        // Al empezar, mostramos el mensaje de bienvenida
        showWelcomeView();
        
        // Preparamos el buscador para que filtre mientras escribimos
        setupUserFilterListeners();
        
        if (usuariosTable != null) {
            colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
            colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
            colActive.setCellValueFactory(new PropertyValueFactory<>("active"));
            colRegisterDate.setCellValueFactory(new PropertyValueFactory<>("registerDate"));

            // Poner la fecha de registro en un formato fácil de leer
            colRegisterDate.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : DateUtils.format(item));
                }
            });

            // La tabla enseña la lista según lo que busquemos
            usuariosTable.setItems(filteredUsuarios);

            // Si pulsas dos veces sobre una persona, abrimos su ficha.
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
                        Label day = new Label(item.getDate().split("-")[2]); 
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
        }

        if (forumListView != null) {
            forumListView.setItems(foroPosts);
            forumListView.getStyleClass().add("forum-list-view");
            // Placeholder informativo
            Label placeholder = new Label("No hay mensajes disponibles en esta categoría.");
            placeholder.getStyleClass().add("content-hint");
            forumListView.setPlaceholder(placeholder);

            // Preparamos el diseño de cada mensaje del foro
            forumListView.setCellFactory(lv -> new ListCell<PostDto>() {
                @Override
                protected void updateItem(PostDto item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        VBox card = new VBox(8);
                        card.getStyleClass().add("forum-card");
                        // Binding robusto para que la tarjeta use todo el ancho
                        card.prefWidthProperty().bind(lv.widthProperty().subtract(40));
                        card.setMinWidth(0);
                        setText(null);
                        
                        // Información del mensaje: Estilo Reddit (f/comunidad • u/autor • fecha)
                        // Información del mensaje: u/autor • fecha (Quitamos el f/comunidad de aquí)
                        HBox metaBox = new HBox(5);
                        metaBox.setAlignment(Pos.CENTER_LEFT);
                        
                        Label communityTag = new Label("f/" + item.getCategory());
                        communityTag.getStyleClass().add("forum-author-tag");
                        
                        String authorName = item.getAuthor() != null ? item.getAuthor() : "anónimo";
                        String dateStr = item.getDate() != null ? DateUtils.format(item.getDate()) : "reciente";
                        
                        Label authorInfo = new Label(" • u/" + authorName + " • " + dateStr);
                        authorInfo.getStyleClass().add("forum-card-meta");
                        
                        metaBox.getChildren().addAll(communityTag, authorInfo);

                        Label title = new Label(item.getTitle());
                        title.getStyleClass().add("forum-card-title");
                        title.setWrapText(true);
                        
                        // Parte de abajo con el contador de respuestas
                        HBox actions = new HBox(15);
                        actions.setPadding(new javafx.geometry.Insets(5, 0, 0, 0));
                        // Contamos cuántas personas han contestado
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

            // Si pulsas dos veces en un mensaje, lo abrimos para leerlo entero
            forumListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    PostDto selected = forumListView.getSelectionModel().getSelectedItem(); 
                    if (selected != null) {
                        showForumThread(selected);
                    }
                }
            });
        }

        // Preparamos la lista de comunidades de la izquierda
        if (categoryListView != null) {
            ObservableList<ForumDto> foroForums = FXCollections.observableArrayList();
            categoryListView.setItems(foroForums);
            
            categoryListView.setCellFactory(lv -> new ListCell<ForumDto>() {
                @Override
                protected void updateItem(ForumDto item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTitle());
                }
            });
            
            categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && lblSelectedCategory != null) {
                    lblSelectedCategory.setText(newVal.getTitle());
                    // Al cambiar de comunidad, buscamos los mensajes de ese grupo
                    loadForumPosts();
                }
            });
        }
    }

    /**
     * Enseñamos el mensaje completo con sus respuestas.
     */
    private void showForumThread(PostDto postSummary) { 
        if (forumMainContainer == null || forumThreadView == null || postSummary == null || postSummary.getId() == null) {
            return;
        }

        // IMPORTANTE: Ejecutar llamada a API fuera del hilo de la UI
        new Thread(() -> {
            try {
                PostDto fullPost = postApiClient.getPostById(postSummary.getId()); 
                
                Platform.runLater(() -> {
                    // Cambiamos lo que se ve en pantalla
                    forumMainContainer.setVisible(false);
                    forumMainContainer.setManaged(false);
                    forumThreadView.setVisible(true);
                    forumThreadView.setManaged(true);

                    // Si eres admin o propietario del tema, enseñamos boton editar, el boton eliminar solo para admin
                    boolean isAdmin = authService.getCurrentUser() != null && 
                    "ADMIN".equalsIgnoreCase(authService.getCurrentUser().getRole());
                    boolean isOwner = authService.getCurrentUser() != null && 
                    authService.getCurrentUser().getUsername().equals(fullPost.getAuthor());
                    adminForumActions.setVisible(isAdmin || isOwner);   
                    adminForumActions.setManaged(isAdmin || isOwner);

                    lblThreadTitle.setText(fullPost.getTitle());
                    lblThreadAuthor.setText("u/" + fullPost.getAuthor());
                    lblThreadDate.setText(" • " + (fullPost.getDate() != null ? DateUtils.format(fullPost.getDate()) : "ahora"));
                    
                    String content = fullPost.getContent();
                    lblThreadBody.setText(content != null && !content.isBlank() ? content : "No hay contenido disponible.");

                    forumThreadView.setUserData(fullPost); 

                    // Limpiar y enseñar las respuestas
commentsContainer.getChildren().clear();
try {
    List<com.aja.model.PostMessageDto> postComments = postApiClient.getCommentsByTopicId(fullPost.getId());
    if (postComments != null && !postComments.isEmpty()) {
       postComments.forEach(comment ->
    addCommentToView(comment.getText(), comment.getAuthor(), comment.getCreationDate(), comment.getId())
);
    } else {
        Label noComments = new Label("No hay comentarios todavía.");
        noComments.getStyleClass().add("content-hint");
        commentsContainer.getChildren().add(noComments);
    }
} catch (Exception ex) {
    Label noComments = new Label("No hay comentarios todavía.");
    noComments.getStyleClass().add("content-hint");
    commentsContainer.getChildren().add(noComments);
}
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error", "No se pudo cargar el hilo: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Abre el panel para cambiar el mensaje que estamos leyendo.
     */
    @FXML
    private void handleEditCurrentThread() {
        PostDto currentPost = (PostDto) forumThreadView.getUserData(); 
        if (currentPost != null) {
            showForumDetails(currentPost.getId());
        }
    }

    /**
     * Ejecuta la eliminación del hilo actual tras confirmación.
     */
    @FXML
   private void handleDeleteCurrentThread() {
    final PostDto currentPost = (PostDto) forumThreadView.getUserData();
    if (currentPost == null) return;
    
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar este hilo permanentemente?");
    confirm.showAndWait().ifPresent(response -> {
        if (response == javafx.scene.control.ButtonType.OK) {
            try {
                topicApiClient.deleteTopic(currentPost.getId());
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

        PostDto currentPost = (PostDto) forumThreadView.getUserData(); 
        if (currentPost == null) return;

        btnPostComment.setDisable(true); // Evitar doble clic

        new Thread(() -> {
            try {
                // Creamos un nuevo post/comentario con el formato que espera el servidor
                postApiClient.addComment(commentText, currentPost.getId());
                //System.out.println("DEBUG - Comentario enviado para topic: " + currentPost.getId());

                Platform.runLater(() -> {
                    txtCommentInput.clear();
                    btnPostComment.setDisable(false);
                    showForumThread(currentPost); // Refresca la vista completa
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnPostComment.setDisable(false);
                    showError("Error", "No se pudo publicar el comentario: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Crea el diseño de una respuesta y la pone en la lista.
     */
    private void addCommentToView(String text, String user, String time, Long commentId) {
    VBox commentBox = new VBox(5);
    commentBox.getStyleClass().add("comment-card");
    
    HBox meta = new HBox(5);
    Label author = new Label(user);
    author.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1e293b;");
    Label date = new Label(" • " + (time != null ? DateUtils.format(time) : "ahora"));
    date.getStyleClass().add("forum-card-meta");
    meta.getChildren().addAll(author, date);

    Label content = new Label(text);
    content.setWrapText(true);
    content.setStyle("-fx-text-fill: #334155;");

    // Botones de acción solo para propietario o admin
    UserDto currentUser = authService.getCurrentUser();
    boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
    boolean isOwner = currentUser != null && currentUser.getUsername().equals(user);

    commentBox.getChildren().addAll(meta, content);

    if (isOwner || isAdmin) {
        HBox actions = new HBox(10);
        
        if (isOwner) {
            Button btnEdit = new Button("Editar");
            btnEdit.setStyle("-fx-background-color: transparent; -fx-text-fill: #3b82f6; -fx-cursor: hand; -fx-font-size: 11px;");
            btnEdit.setOnAction(e -> {
                javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(text);
                dialog.setTitle("Editar comentario");
                dialog.setHeaderText(null);
                dialog.setContentText("Nuevo texto:");
                dialog.showAndWait().ifPresent(newText -> {
                    if (!newText.isBlank()) {
                        new Thread(() -> {
                            try {
                                postApiClient.editComment(commentId, newText);
                                PostDto currentPost = (PostDto) forumThreadView.getUserData();
                                Platform.runLater(() -> showForumThread(currentPost));
                            } catch (Exception ex) {
                                Platform.runLater(() -> showError("Error", "No se pudo editar: " + ex.getMessage()));
                            }
                        }).start();
                    }
                });
            });
            actions.getChildren().add(btnEdit);
        }

        if (isOwner || isAdmin) {
            Button btnDelete = new Button("Borrar");
            btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-size: 11px;");
            btnDelete.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar este comentario?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        new Thread(() -> {
                            try {
                                postApiClient.deleteComment(commentId);
                                PostDto currentPost = (PostDto) forumThreadView.getUserData();
                                Platform.runLater(() -> showForumThread(currentPost));
                            } catch (Exception ex) {
                                Platform.runLater(() -> showError("Error", "No se pudo borrar: " + ex.getMessage()));
                            }
                        }).start();
                    }
                });
            });
            actions.getChildren().add(btnDelete);
        }

        commentBox.getChildren().add(actions);
    }

    commentsContainer.getChildren().add(commentBox);
}

    /**
     * Vuelve atrás para ver la lista de todos los mensajes.
     */
    @FXML
    private void backToForumList() {
        forumThreadView.setVisible(false);
        forumThreadView.setManaged(false);
        forumMainContainer.setVisible(true);
        forumMainContainer.setManaged(true);
    }

    /**
     * Limpia el panel y pone el mensaje de bienvenida.
     */
    private void showWelcomeView() {
        contentArea.getChildren().clear();

        VBox welcomeBox = new VBox(20); // Aumentamos el espacio entre elementos
        welcomeBox.getStyleClass().add("welcome-container");
        welcomeBox.setAlignment(Pos.CENTER);

        ImageView logoView = new ImageView();
        try {
            java.net.URL logoUrl = getClass().getResource("/images/logo.png");
            if (logoUrl != null) {
                Image logo = new Image(logoUrl.toExternalForm());
                logoView.setImage(logo);
                logoView.setFitWidth(150); 
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
     * Herramienta para cargar diferentes vistas en la parte derecha.
     */
    private <T> T loadView(String fxmlPath) {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
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
     * Enseña la sección de personas y actualiza la tabla.
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
     * Enseña el foro. Primero cargamos los temas (izquierda); 
     * al seleccionarse uno, se cargarán los mensajes (derecha).
     */
    @FXML
    private void showForo() {
        selectMenuItem(1);
        foroContent.setVisible(true);
        foroContent.setManaged(true);
        contentArea.getChildren().setAll(foroContent);
        backToForumList(); 
        loadTopics(); 
    }

    /**
     * Enseña la lista de actividades programadas.
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
     * Enseña la bandeja de mensajes recibidos.
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
     * Sale del programa y vuelve a la pantalla de entrada.
     */
   @FXML
    private void handleLogout(ActionEvent event) {
    try {
        System.out.println("Ejecutando logout seguro...");
        com.aja.service.AuthService.getInstance().logout();

        // 1. Cargamos la vista de entrada
        java.net.URL loginUrl = getClass().getClassLoader().getResource("login.fxml");
        if (loginUrl == null) loginUrl = getClass().getClassLoader().getResource("views/login.fxml");

        FXMLLoader loader = new FXMLLoader(loginUrl);
        Parent root = loader.load();
        
        // 2. Preparamos la ventana
        Scene scene = new Scene(root, 420, 440);
        
        // 3. Aplicamos los colores
        try {
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

        // 4. Cambiamos lo que vemos
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        stage.setMaximized(false); 
        stage.setResizable(false); 
        stage.setScene(scene);
        
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
     * Prepara el buscador para que filtre mientras escribimos.
     */
    private void setupUserFilterListeners() {
        if (searchUserField != null && activeUsersCheckBox != null) {
            searchUserField.textProperty().addListener((observable, oldValue, newValue) -> updateUsersFilter());
            activeUsersCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateUsersFilter());
            updateUsersFilter();
        }
    }

    /**
     * Busca a las personas por nombre o correo según lo que hayamos escrito o marcado.
     */
    private void updateUsersFilter() {
        filteredUsuarios.setPredicate(user -> {
            if (activeUsersCheckBox.isSelected() && !user.isActive()) {
                return false; 
            }

            String searchText = searchUserField.getText();
            if (searchText == null || searchText.isBlank()) {
                return true; 
            }

            String lowerCaseFilter = searchText.toLowerCase().trim();

            if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            if (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false; 
        });
    }

    /**
     * Qué hacer cuando marcamos el botón de "Solo personas activas".
     */
    @FXML
    private void handleActiveUsersFilter() {
        updateUsersFilter();
    }

    /**
     * Pide la lista de personas al servidor y la enseña en la tabla.
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
     * Abre una ficha para ver o cambiar los datos de una persona (solo para el jefe).
     */
    private void showUserDetails(Long userId) {
        try {
            // Buscamos los datos actuales de la persona
            final UserDto user = userApiClient.getUserById(userId);
            
            // Comprobamos si el administrador se está viendo a sí mismo
            UserDto currentUser = authService.getCurrentUser();
            boolean isSelf = currentUser != null && currentUser.getId().equals(user.getId());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Detalles del Usuario");
            dialog.setResizable(false);

            VBox layout = new VBox(20);
            layout.setPadding(new javafx.geometry.Insets(30));
            layout.getStyleClass().add("content-pane");
            layout.setPrefWidth(420);

            VBox header = new VBox(5);
            Label titleLabel = new Label("Gestión de Usuario");
            titleLabel.getStyleClass().add("content-page-title");
            Label subtitleLabel = new Label("Consulta o modifica la información del usuario");
            subtitleLabel.getStyleClass().add("content-page-subtitle");
            header.getChildren().addAll(titleLabel, subtitleLabel);

            VBox userInfo = new VBox(8);
            Label nameLabel = new Label(user.getUsername());
            nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
            Label idLabel = new Label("ID: " + user.getId() + " • Registro: " + DateUtils.format(user.getRegisterDate()));
            idLabel.getStyleClass().add("content-hint");
            userInfo.getChildren().addAll(nameLabel, idLabel);

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
            
            // Ocultamos la opción de cambiar rol si es su propia cuenta
            roleGroup.setVisible(!isSelf);
            roleGroup.setManaged(!isSelf);

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
            
            // Un administrador no debe poder desactivarse a sí mismo desde aquí
            activeCheck.setVisible(!isSelf);
            activeCheck.setManaged(!isSelf);
            
            form.getChildren().addAll(emailGroup, roleGroup, passGroup, activeCheck);

            Button btnSave = new Button("Guardar Cambios");
            btnSave.getStyleClass().add("primary-action-button");
            btnSave.setMaxWidth(Double.MAX_VALUE);
            btnSave.setPrefHeight(40);
            
            Button btnDisable = new Button("Deshabilitar Acceso");
            // Ocultar botón de deshabilitar para cuenta propia
            btnDisable.setVisible(!isSelf);
            btnDisable.setManaged(!isSelf);
            btnDisable.setStyle("-fx-background-color: transparent; -fx-text-fill: #f59e0b; -fx-border-color: #f59e0b; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
            btnDisable.setMaxWidth(Double.MAX_VALUE);
            
            Button btnDelete = new Button("Eliminar Usuario");
            // Ocultar botón de eliminar para cuenta propia
            btnDelete.setVisible(!isSelf);
            btnDelete.setManaged(!isSelf);
            btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
            btnDelete.setMaxWidth(Double.MAX_VALUE);

            layout.getChildren().addAll(header, new Separator(), userInfo, form, btnSave, btnDisable, new Separator(), btnDelete);

            btnSave.setOnAction(e -> {
                try {
                    user.setEmail(emailField.getText());
                    user.setRole(roleCombo.getValue());
                    user.setActive(activeCheck.isSelected());
                    
                    // El registerDate suele ser inmutable en el backend. 
                    // Lo ponemos a null para que BaseApiClient (con NON_NULL) no lo envíe
                    // y así evitar conflictos de seguridad/403.
                    user.setRegisterDate(null); 
                    
                    String newPass = passField.getText();
                    if (newPass != null && !newPass.isBlank()) {
                        user.setPassword(newPass);
                    } else {
                        user.setPassword(null); // Evitamos enviar una cadena vacía al servidor
                    }
                    
                    // USUARIO MODIFICACIÓN -> UserEntity entero
                    userApiClient.updateUser(user);
                    loadUsers(); 
                    dialog.close();
                    showInfo("Actualizado", "Los datos del usuario se han guardado correctamente.");
                } catch (Exception ex) {
                    showError("Error al actualizar", "No se han guardado los cambios: " + ex.getMessage());
                }
            });

            // Lógica de Toggle: El botón cambia según el estado actual del usuario
            boolean isCurrentlyActive = Boolean.TRUE.equals(user.isActive());
            // Lógica de Toggle: El botón cambia visualmente según el estado actual
            boolean activeStatus = Boolean.TRUE.equals(user.isActive());
            btnDisable.setText(activeStatus ? "Desactivar Usuario" : "Activar Usuario");
            btnDisable.setStyle(activeStatus ?
                "-fx-background-color: transparent; -fx-text-fill: #f59e0b; -fx-border-color: #f59e0b; -fx-border-radius: 8;" : 
                "-fx-background-color: transparent; -fx-text-fill: #10b981; -fx-border-color: #10b981; -fx-border-radius: 8;");

            btnDisable.setOnAction(e -> {
                String accion = Boolean.TRUE.equals(user.isActive()) ? "desactivar" : "activar";
                Alert confirmToggle = new Alert(Alert.AlertType.CONFIRMATION, "¿Quieres " + accion + " a este usuario?");
                confirmToggle.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        try {
                            // DESHABILITAR/HABILITAR -> ADMINISTRADOR (Endpoints específicos)
                            if (Boolean.TRUE.equals(user.isActive())) {
                                userApiClient.disableUser(user.getId());
                            } else {
                                userApiClient.enableUser(user.getId());
                            }
                            
                            loadUsers();
                            dialog.close();
                            showInfo("Éxito", "El estado del usuario ha sido actualizado.");
                        } catch (Exception ex) {
                            showError("Error de Permisos (403)", "No puedes cambiar el estado de este usuario: " + ex.getMessage());
                        }
                    }
                });
            });

            btnDelete.setOnAction(e -> {
                Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "¿Borrar permanentemente a este usuario?");
                confirmDelete.showAndWait().ifPresent(response -> {
                    if (response == javafx.scene.control.ButtonType.OK) {
                        try {
                            userApiClient.deleteUser(user.getId());
                            loadUsers();
                            dialog.close();
                            showInfo("Eliminado", "Usuario borrado correctamente.");
                        } catch (Exception ex) {
                            showError("Error", "No se pudo eliminar al usuario: " + ex.getMessage());
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
     * Actualiza la lista de actividades desde internet.
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
     * Actualiza la bandeja de mensajes.
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
     * Carga los temas del foro para la barra lateral.
     */
    private void loadTopics() {
        new Thread(() -> {
            try {
                List<ForumDto> list = forumApiClient.getAllForums();
                Platform.runLater(() -> {
                    categoryListView.getItems().clear();
                    categoryListView.getItems().add(new ForumDto(0L, "Todas las comunidades"));
                    if (list != null) categoryListView.getItems().addAll(list);
                    categoryListView.getSelectionModel().selectFirst();
                });
            } catch (Exception e) {
                System.err.println("Error al cargar categorías: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Actualiza los mensajes del foro.
     */
    private void loadForumPosts() {
        new Thread(() -> {
            try {
                List<PostDto> list = postApiClient.getAllPosts(); 
                Platform.runLater(() -> {
                    // Corregido: La lista lateral contiene ForumDto
                    ForumDto currentSelection = categoryListView.getSelectionModel().getSelectedItem();
                    
                    foroPosts.clear();
                    if (list != null) {
                        if (currentSelection == null || currentSelection.getId() == 0L) {
                            // Opción "Todas las comunidades"
                            foroPosts.addAll(list);
                        } else {
                            Long sid = currentSelection.getId();
                            list.stream()
                                .filter(p -> {
                                    Long tid = p.getTopicId();
                                    // Si es General, incluimos nulos o -1
                                    if (sid == -1L) return tid == null || tid == -1L;
                                    return sid.equals(tid);
                                })
                                .forEach(foroPosts::add);
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Error de Foro", "No se pudieron cargar los mensajes: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Abre un panel para ver, cambiar o borrar un mensaje del foro (solo para el jefe).
     */
    private void showForumDetails(Long forumId) {
        try {
            PostDto post = postApiClient.getPostById(forumId); 
            UserDto currentUser = authService.getCurrentUser();
            boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
            boolean isOwner = currentUser != null && currentUser.getUsername().equals(post.getAuthor());

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
            titleField.setEditable(isAdmin || isOwner);

            Label authorHint = new Label("AUTOR");
            authorHint.getStyleClass().add("user-label");
            TextField authorField = new TextField(post.getAuthor());
            authorField.getStyleClass().add("search-field");
            authorField.setEditable(false); // El autor no se puede cambiar

            form.getChildren().addAll(titleHint, titleField, authorHint, authorField);

            layout.getChildren().addAll(header, new Separator(), form);

            if (isAdmin || isOwner) {
                Button btnUpdate = new Button("Guardar Cambios");
                btnUpdate.getStyleClass().add("primary-action-button");
                btnUpdate.setMaxWidth(Double.MAX_VALUE);

                Button btnDelete = new Button("Eliminar Tema");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
                btnDelete.setMaxWidth(Double.MAX_VALUE);

                btnUpdate.setOnAction(e -> {
                    try {
                        Long currentForum = post.getTopicId();
                        topicApiClient.editTopic(post.getId(), titleField.getText(), currentForum, currentForum);
                        loadForumPosts();
                        dialog.close();
                        backToForumList();
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
                                topicApiClient.deleteTopic(post.getId());
                                loadForumPosts();
                                dialog.close();
                                backToForumList();
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
     * Cambia el color del botón del menú para saber siempre en qué sección estamos.
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
     * Mi Perfil: Permite cambiar tu correo o borrar tu cuenta tras pedirte la clave.
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
        
        Label dateLabel = new Label("Miembro desde: " + DateUtils.format(user.getRegisterDate()));
        dateLabel.getStyleClass().add("content-hint");
        userInfo.getChildren().addAll(nameLabel, roleLabel, dateLabel);

        // Formulario de edición
        VBox form = new VBox(15);
        
        VBox userGroup = new VBox(5);
        Label userTitle = new Label("NOMBRE DE USUARIO");
        userTitle.getStyleClass().add("user-label");
        TextField usernameField = new TextField(user.getUsername());
        usernameField.setEditable(true);
        usernameField.setDisable(false);
        usernameField.setFocusTraversable(true);
        // Eliminamos -fx-opacity para asegurar que el campo no parezca deshabilitado visualmente
        usernameField.setStyle("-fx-padding: 8 12; -fx-background-color: #ffffff; -fx-border-color: #3b82f6; " +
                             "-fx-border-radius: 8; -fx-text-fill: #1e293b; -fx-border-width: 1px;");
        userGroup.getChildren().addAll(userTitle, usernameField);

        VBox emailGroup = new VBox(5);
        Label emailTitle = new Label("CORREO ELECTRÓNICO");
        emailTitle.getStyleClass().add("user-label");
        TextField emailField = new TextField(user.getEmail());
        emailField.getStyleClass().add("search-field");
        emailField.setStyle("-fx-padding: 8 12 8 12;"); 
        emailGroup.getChildren().addAll(emailTitle, emailField);

        VBox passGroup = new VBox(5);
        Label passTitle = new Label("CONFIRMAR CAMBIOS");
        passTitle.getStyleClass().add("user-label");
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPromptText("Contraseña actual");
        confirmPassField.getStyleClass().add("search-field");
        confirmPassField.setStyle("-fx-padding: 8 12 8 12;");
        passGroup.getChildren().addAll(passTitle, confirmPassField);
        
        form.getChildren().addAll(userGroup, emailGroup, passGroup);

        Button btnUpdate = new Button("Actualizar Información");
        btnUpdate.getStyleClass().add("primary-action-button");
        btnUpdate.setMaxWidth(Double.MAX_VALUE);
        btnUpdate.setPrefHeight(40);
        
        Button btnBaja = new Button("Cerrar mi cuenta");
        btnBaja.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 8; -fx-cursor: hand; -fx-font-weight: 600;");
        btnBaja.setMaxWidth(Double.MAX_VALUE);

        layout.getChildren().addAll(header, new Separator(), userInfo, form, btnUpdate, new Separator(), btnBaja);

        // Acción para actualizar datos
        btnUpdate.setOnAction(e -> {
            String pass = confirmPassField.getText();
            if (pass == null || pass.isBlank()) {
                showError("Seguridad", "Debes introducir tu contraseña para realizar cambios.");
                return;
            }

            try {
                // 1. Re-autenticamos para validar la identidad
                LoginResponseDto auth = authService.authenticate(authService.getCurrentUser().getUsername(), pass);
                if (!auth.isSuccess()) {
                    showError("Error", "Contraseña incorrecta para confirmar cambios.");
                    return;
                }

                
                // 3. Preparamos la petición de actualización "limpia".
                // NO enviamos role, active ni registerDate. Como usuario normal (USER), 
                // el servidor suele denegar (403) peticiones que incluyan campos de administración,
                // incluso si los valores no cambian.
                UserDto sessionUser = authService.getCurrentUser();
                String oldUsername = sessionUser.getUsername();
                
                UserDto updateRequest = new UserDto();
                updateRequest.setId(sessionUser.getId());
                updateRequest.setUsername(usernameField.getText().trim());
                updateRequest.setEmail(emailField.getText());
                updateRequest.setRole(sessionUser.getRole());
                updateRequest.setActive(sessionUser.isActive());
                updateRequest.setRegisterDate(sessionUser.getRegisterDate());
                updateRequest.setPassword(confirmPassField.getText());

                // IMPORTANTE: Usamos updateUser para que la URL incluya el ID (/api/user/{id})
                // Esto permite al servidor validar la propiedad del recurso y evitar el 403.
                userApiClient.updateProfile(updateRequest);
                
                // Si el nombre de usuario ha cambiado, cerramos sesión por seguridad
                if (!oldUsername.equals(updateRequest.getUsername())) {
                    dialog.close();
                    showInfo("Perfil actualizado", "Has cambiado tu nombre de usuario. Por seguridad, debes iniciar sesión de nuevo.");
                    handleLogout(e);
                    return;
                }

                // 4. Sincronizamos los cambios en la sesión local y actualizamos el Dashboard
                sessionUser.setUsername(updateRequest.getUsername());
                sessionUser.setEmail(updateRequest.getEmail());
                
                lblUsername.setText(sessionUser.getUsername());
                lblUserEmail.setText(sessionUser.getEmail());
                dialog.close();
                showInfo("Perfil actualizado", "Los cambios se han guardado.");
            } catch (Exception ex) {
                showError("Error de Actualización", "No se pudieron guardar los cambios: " + ex.getMessage());
            }
        });

        // Darse de baja uno mismo
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
     * Abre una ventana para crear una persona nueva.
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
                loadUsers(); 
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
     * Abre una ventana para escribir un tema nuevo en el foro.
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
        if (authService.getCurrentUser() != null) {
            authorField.setText(authService.getCurrentUser().getUsername());
        }

        TextArea contentAreaField = new TextArea();
        contentAreaField.setPromptText("Escribe aquí el contenido de tu tema...");
        contentAreaField.setPrefHeight(100);

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
        
        VBox contentGroup = new VBox(5);
        Label contentLabelGroup = new Label("CONTENIDO");
        contentLabelGroup.getStyleClass().add("user-label");
        contentGroup.getChildren().addAll(contentLabelGroup, contentAreaField);

        form.getChildren().addAll(titleGroup, authorGroup, contentGroup);

        layout.getChildren().addAll(header, new Separator(), form, okButton, cancelButton);

        okButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String content = contentAreaField.getText().trim();

                if (title.isEmpty() || author.isEmpty() || content.isEmpty()) {
                    showError("Campos requeridos", "Todos los campos son obligatorios.");
                    return;
                }

                ForumDto selectedForum = categoryListView.getSelectionModel().getSelectedItem();
                Long forumId = (selectedForum != null && selectedForum.getId() > 0) ? selectedForum.getId() : null;
                topicApiClient.addTopic(title, forumId);

                loadForumPosts(); 
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
     * Abre una ventana para crear una actividad nueva.
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
                loadEvents(); 
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
     * Abre una ventana para mandar un mensaje a alguien.
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

                messageApiClient.createMessage(newMessage);
                loadMessages(); 
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
     * Aviso rápido si ocurre algún error.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Aviso rápido para dar noticias positivas.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Guarda la llave de seguridad en todos los sitios donde se necesite.
     */
    public void setToken(String token) {
        this.token = token;

        // Repartir la llave a todas las herramientas de conexión
        userApiClient.setToken(token);
        eventApiClient.setToken(token);
        messageApiClient.setToken(token);
        postApiClient.setToken(token);
        topicApiClient.setToken(token);
        forumApiClient.setToken(token);
    }
}

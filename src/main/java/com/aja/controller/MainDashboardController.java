package com.aja.controller;

import com.aja.api.UserApiClient;
import com.aja.api.EventApiClient;
import com.aja.api.MessageApiClient;
import com.aja.api.ForumApiClient;
import com.aja.model.UserDto;
import com.aja.model.EventDto;
import com.aja.model.MessageDto;
import com.aja.model.ForumDto;
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

    @FXML
    private TableView<EventDto> eventosTable;
    @FXML
    private TableColumn<EventDto, Long> colEventId;
    @FXML
    private TableColumn<EventDto, String> colEventTitle;
    @FXML
    private TableColumn<EventDto, String> colEventDescription;
    @FXML
    private TableColumn<EventDto, String> colEventDate;
    @FXML
    private TableColumn<EventDto, String> colEventLocation;

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
    private TableView<ForumDto> foroTable;
    @FXML
    private TableColumn<ForumDto, Long> colForumId;
    @FXML
    private TableColumn<ForumDto, String> colForumTitle;
    @FXML
    private TableColumn<ForumDto, String> colForumAuthor;
    @FXML
    private TableColumn<ForumDto, String> colForumDate;

    private final UserApiClient userApiClient = new UserApiClient();
    private final ObservableList<UserDto> usuarios = FXCollections.observableArrayList();

    private final EventApiClient eventApiClient = new EventApiClient();
    private final ObservableList<EventDto> eventos = FXCollections.observableArrayList();

    private final MessageApiClient messageApiClient = new MessageApiClient();
    private final ObservableList<MessageDto> mensajes = FXCollections.observableArrayList();

    private final ForumApiClient forumApiClient = new ForumApiClient();
    private final ObservableList<ForumDto> foroPosts = FXCollections.observableArrayList();

    private List<Button> menuButtons;
    private List<VBox> contentPanes;

    /**
     * Método de inicialización llamado automáticamente por JavaFX.
     * Configura las listas de botones de navegación y paneles de contenido,
     * selecciona la sección de usuarios por defecto y configura todas las tablas
     * con sus respectivas columnas y carga inicial de datos.
     */
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

        if (eventosTable != null) {
            colEventId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colEventTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colEventDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colEventDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colEventLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

            eventosTable.setItems(eventos);
            loadEvents();
        }

        if (mensajesTable != null) {
            colMessageId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colMessageSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
            colMessageReceiver.setCellValueFactory(new PropertyValueFactory<>("receiver"));
            colMessageContent.setCellValueFactory(new PropertyValueFactory<>("content"));
            colMessageDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colMessageRead.setCellValueFactory(new PropertyValueFactory<>("read"));

            mensajesTable.setItems(mensajes);
            loadMessages();
        }

        if (foroTable != null) {
            colForumId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colForumTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colForumAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
            colForumDate.setCellValueFactory(new PropertyValueFactory<>("date"));

            foroTable.setItems(foroPosts);
            loadForumPosts();
        }
    }

    /**
     * Muestra la sección de usuarios en el panel principal.
     * Cambia la selección del menú de navegación a la pestaña de usuarios.
     */
    @FXML
    private void showUsuarios() {
        selectMenuItem(0);
    }

    /**
     * Muestra la sección del foro en el panel principal.
     * Cambia la selección del menú de navegación a la pestaña del foro.
     */
    @FXML
    private void showForo() {
        selectMenuItem(1);
    }

    /**
     * Muestra la sección de eventos en el panel principal.
     * Cambia la selección del menú de navegación a la pestaña de eventos.
     */
    @FXML
    private void showEventos() {
        selectMenuItem(2);
    }

    /**
     * Muestra la sección de mensajes en el panel principal.
     * Cambia la selección del menú de navegación a la pestaña de mensajes.
     */
    @FXML
    private void showMensajes() {
        selectMenuItem(3);
    }

    /**
     * Maneja el cierre de sesión del usuario.
     * Navega de vuelta a la pantalla de login, cerrando la sesión actual.
     *
     * @throws IOException Si ocurre un error al cargar la vista de login
     */
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

    /**
     * Carga la lista de usuarios desde la API y actualiza la tabla correspondiente.
     * En caso de error en la comunicación con el servidor, imprime el error en consola.
     */
    private void loadUsers() {
        try {
            List<UserDto> list = userApiClient.getAllUsers();
            usuarios.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la lista de eventos desde la API y actualiza la tabla correspondiente.
     * En caso de error en la comunicación con el servidor, imprime el error en consola.
     */
    private void loadEvents() {
        try {
            List<EventDto> list = eventApiClient.getAllEvents();
            eventos.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la lista de mensajes desde la API y actualiza la tabla correspondiente.
     * En caso de error en la comunicación con el servidor, imprime el error en consola.
     */
    private void loadMessages() {
        try {
            List<MessageDto> list = messageApiClient.getAllMessages();
            mensajes.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga la lista de publicaciones del foro desde la API y actualiza la tabla correspondiente.
     * En caso de error en la comunicación con el servidor, imprime el error en consola.
     */
    private void loadForumPosts() {
        try {
            List<ForumDto> list = forumApiClient.getAllForumPosts();
            foroPosts.setAll(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Selecciona y muestra el panel de contenido correspondiente al índice especificado.
     * Actualiza el estilo de los botones de navegación para resaltar el seleccionado
     * y controla la visibilidad de los paneles de contenido.
     *
     * @param index El índice del elemento del menú a seleccionar (0-3)
     */
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

package com.aja;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación AJA Desktop.
 * Punto de entrada que inicializa la interfaz gráfica de usuario
 * y configura la ventana principal de la aplicación.
 */
public class App extends Application {

    /**
     * Método principal de JavaFX que configura y muestra la interfaz de usuario.
     * Carga la pantalla de login como primera vista de la aplicación.
     *
     * @param stage El escenario principal de la aplicación donde se mostrarán las vistas
     * @throws IOException Si ocurre un error al cargar el archivo FXML de login
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        // Aumentamos el alto a 440 para que quepan los mensajes de error debajo de los campos
        Scene scene = new Scene(root, 420, 440);
        scene.getStylesheets().add(getClass().getResource("/styles/login.css").toExternalForm());

        stage.setTitle("AJA - Iniciar sesión");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Método main que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados actualmente)
     */
    public static void main(String[] args) {
        launch(args);
    }
}

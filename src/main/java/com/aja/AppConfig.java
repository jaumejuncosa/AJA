package com.aja;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración centralizada de la aplicación AJA Desktop.
 * Maneja la carga de propiedades desde archivos de configuración
 * para centralizar la configuración de la aplicación.
 */
public class AppConfig {

    private static final String CONFIG_FILE = "/config.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                // Valores por defecto si no se encuentra el archivo
                properties.setProperty("api.base.url", "http://localhost:8080");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Valores por defecto en caso de error
            properties.setProperty("api.base.url", "http://localhost:8080");
        }
    }

    /**
     * Obtiene la URL base de la API desde la configuración.
     *
     * @return La URL base de la API
     */
    public static String getApiBaseUrl() {
        return properties.getProperty("api.baseUrl", "http://localhost:8080");
    }
}
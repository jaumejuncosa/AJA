package com.aja.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración de la aplicación.
 *
 * Carga valores de configuración desde el archivo {@code config.properties} en recursos.
 */
public final class AppConfig {

    private static final String CONFIG_FILE = "/config.properties";
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String API_BASE_URL_KEY = "api.baseUrl";

    private static final Properties properties = loadProperties();

    private AppConfig() {
        // No instanciable
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
            // Si no se puede leer la configuración, se usa el valor por defecto.
        }
        return props;
    }

    /**
     * Obtiene la URL base de la API.
     *
     * @return URL base de la API definida en {@code config.properties} o un valor por defecto.
     */
    public static String getApiBaseUrl() {
        return properties.getProperty(API_BASE_URL_KEY, DEFAULT_BASE_URL);
    }
}

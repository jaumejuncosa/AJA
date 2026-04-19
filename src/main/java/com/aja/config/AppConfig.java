package com.aja.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class AppConfig {
    // Nombre del archivo de ajustes
    private static final String CONFIG_FILE = "/config.properties";
    
    // Si no hay ajustes, usamos esta dirección por defecto
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String API_BASE_URL_KEY = "api.baseUrl";

    // Ajustes guardados en la memoria
    private static final Properties properties = loadProperties();

    private AppConfig() {
        // No se puede crear un objeto de esta clase
    }

    /**
     * Leemos el archivo de ajustes guardado en el programa.
     */
    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = AppConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("Archivo de configuración no encontrado: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            System.err.println("Error cargando configuración: " + e.getMessage());
        }
        return props;
    }

    /**
     * Nos da la dirección de la web donde están los datos.
     */
    public static String getApiBaseUrl() {
        String url = properties.getProperty(API_BASE_URL_KEY, DEFAULT_BASE_URL);
        return url != null ? url.trim() : DEFAULT_BASE_URL;
    }
}

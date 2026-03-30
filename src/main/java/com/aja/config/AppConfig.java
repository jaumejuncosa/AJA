package com.aja.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public final class AppConfig {
    // Nombre del archivo donde guardamos las rutas
    private static final String CONFIG_FILE = "/config.properties";
    
    // Si el archivo no existe, usamos esta URL por defecto
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";
    private static final String API_BASE_URL_KEY = "api.baseUrl";

    // Propiedades cargadas en memoria    
    private static final Properties properties = loadProperties();

    private AppConfig() {
        // No instanciable
    }

    /**
     * Leemos el archivo properties de los recursos del proyecto.
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
            // Si no se puede leer la configuración, se usa el valor por defecto.
        }
        return props;
    }

    /**
     * Devuelve la URL de la API (ej: http://localhost:8080).
     */
    public static String getApiBaseUrl() {
        return properties.getProperty(API_BASE_URL_KEY, DEFAULT_BASE_URL);
    }
}

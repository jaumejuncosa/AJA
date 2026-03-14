package com.aja.service;

/**
 * Servicio de autenticación para la aplicación AJA Desktop.
 * Maneja la validación de credenciales de usuario para el acceso al sistema.
 * Actualmente implementa una autenticación básica con credenciales hardcodeadas
 * que puede ser reemplazada por integración con base de datos o servicios externos.
 */
public class AuthService {

    /**
     * Valida las credenciales de un usuario.
     * Compara el nombre de usuario y contraseña proporcionados con los valores
     * esperados en el sistema.
     *
     * @param username El nombre de usuario a validar
     * @param password La contraseña a validar
     * @return true si las credenciales son válidas, false en caso contrario
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        // Usuario de prueba: admin / 1234
        return username.equals("admin") && password.equals("1234");
    }
}
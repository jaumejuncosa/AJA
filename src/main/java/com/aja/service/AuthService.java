package com.aja.service;

public class AuthService {

    // Ejemplo simple: luego lo podrás cambiar a BD o API
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }

        // Usuario de prueba: admin / 1234
        return username.equals("admin") && password.equals("1234");
    }
}
package com.aja.service;

import com.aja.model.LoginResponseDto;
import com.aja.model.UserDto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService instance;

    @BeforeEach
    public void setUp() {
        instance = AuthService.getInstance();
        instance.logout(); // Limpiamos sesión antes de cada test
    }

    /**
     * Singleton: siempre devuelve la misma instancia.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        AuthService instance1 = AuthService.getInstance();
        AuthService instance2 = AuthService.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "Debe ser la misma instancia (Singleton)");
    }

    /**
     * Login con credenciales correctas devuelve éxito y guarda el usuario.
     */
    @Test
    public void testAuthenticate_credencialesCorrectas() {
        System.out.println("authenticate - credenciales correctas");
        LoginResponseDto response = instance.authenticate("admin", "1234");
        assertTrue(response.isSuccess(), "El login debería ser exitoso");
        assertNotNull(instance.getCurrentUser(), "Debería haber un usuario en sesión");
        assertEquals("admin", instance.getCurrentUser().getUsername());
    }

    /**
     * Login con credenciales incorrectas devuelve fallo.
     */
    @Test
    public void testAuthenticate_credencialesIncorrectas() {
        System.out.println("authenticate - credenciales incorrectas");
        LoginResponseDto response = instance.authenticate("usuario_inexistente", "password_malo");
        assertFalse(response.isSuccess(), "El login debería fallar");
        assertNull(instance.getCurrentUser(), "No debería haber usuario en sesión");
    }

    /**
     * Login con null devuelve fallo controlado.
     */
    @Test
    public void testAuthenticate_credencialesNulas() {
        System.out.println("authenticate - credenciales nulas");
        LoginResponseDto response = instance.authenticate(null, null);
        assertFalse(response.isSuccess(), "El login con null debería fallar");
    }

    /**
     * getCurrentUser devuelve null si no hay sesión.
     */
    @Test
    public void testGetCurrentUser_sinSesion() {
        System.out.println("getCurrentUser - sin sesión");
        assertNull(instance.getCurrentUser(), "Sin login no debería haber usuario");
    }

    /**
     * getCurrentUser devuelve el usuario tras login exitoso.
     */
    @Test
    public void testGetCurrentUser_conSesion() {
        System.out.println("getCurrentUser - con sesión");
        instance.authenticate("admin", "1234");
        UserDto user = instance.getCurrentUser();
        assertNotNull(user);
        assertNotNull(user.getUsername());
        assertNotNull(user.getEmail());
    }

    /**
     * logout limpia la sesión correctamente.
     */
    @Test
    public void testLogout() {
        System.out.println("logout");
        instance.authenticate("admin", "1234");
        assertNotNull(instance.getCurrentUser(), "Debería haber sesión antes del logout");
        instance.logout();
        assertNull(instance.getCurrentUser(), "Después del logout no debería haber sesión");
    }

    /**
     * getToken devuelve null si no hay sesión.
     */
    @Test
    public void testGetToken_sinSesion() {
        System.out.println("getToken - sin sesión");
        assertNull(instance.getToken(), "Sin login el token debería ser null");
    }
}
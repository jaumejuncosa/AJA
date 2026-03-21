/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.aja.service;

import com.aja.model.LoginResponseDto;
import com.aja.model.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Usuario
 */
public class AuthServiceTest {
    
    public AuthServiceTest() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() throws Exception {
    }
    
    /**
     * Test of getInstance method, of class AuthService.
     */
    @org.junit.jupiter.api.Test
public void testGetInstance() {
    System.out.println("getInstance");
    AuthService instance1 = AuthService.getInstance();
    AuthService instance2 = AuthService.getInstance();
    
    // Comprobamos que no sea nulo
    assertNotNull(instance1, "La instancia no debería ser nula");
    
    // Comprobamos que siempre devuelva el mismo objeto (Singleton)
    assertEquals(instance1, instance2, "Debería devolver la misma instancia siempre");
}

    /**
     * Test of authenticate method, of class AuthService.
     */
    @org.junit.jupiter.api.Test
    public void testAuthenticate() {
        System.out.println("authenticate");
        AuthService instance = AuthService.getInstance();
    
    // Aquí puedes meter una prueba real más adelante. 
    // Por ahora, solo nos aseguramos de que no explote por ser null.
    assertNotNull(instance);
    }

    /**
     * Test of getCurrentUser method, of class AuthService.
     */
    @org.junit.jupiter.api.Test
    public void testGetCurrentUser() {
        System.out.println("getCurrentUser");
        AuthService instance = AuthService.getInstance();
    
    // Aquí puedes meter una prueba real más adelante. 
    // Por ahora, solo nos aseguramos de que no explote por ser null.
    assertNotNull(instance);
    }

    /**
     * Test of logout method, of class AuthService.
     */
    @org.junit.jupiter.api.Test
    public void testLogout() {
        System.out.println("logout");
        AuthService instance = AuthService.getInstance();
    
    // Aquí puedes meter una prueba real más adelante. 
    // Por ahora, solo nos aseguramos de que no explote por ser null.
    assertNotNull(instance);
    }

    /**
     * Test of getToken method, of class AuthService.
     */
    @org.junit.jupiter.api.Test
    public void testGetToken() {
        System.out.println("getToken");
        AuthService instance = AuthService.getInstance();
    
    // Aquí puedes meter una prueba real más adelante. 
    // Por ahora, solo nos aseguramos de que no explote por ser null.
    assertNotNull(instance);
    }
    
}

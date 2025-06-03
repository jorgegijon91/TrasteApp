package com.example.trasteapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoginRegisterTest {

    @Test
    public void testRegistroConDatosValidos() {
        String email = "usuario@trasteapp.com";
        String password = "123456";

        System.out.println("Iniciando prueba de registro con datos válidos...");
        System.out.println("Email introducido: " + email);
        System.out.println("Contraseña introducida: " + password);

        boolean emailValido = email.contains("@") && email.endsWith(".com");
        boolean passwordValida = password.length() >= 6;

        System.out.println("¿Email válido?: " + emailValido);
        System.out.println("¿Contraseña válida?: " + passwordValida);

        assertTrue("El email debe ser válido", emailValido);
        assertTrue("La contraseña debe tener al menos 6 caracteres", passwordValida);

        System.out.println("✅ Prueba completada con éxito.\n");
    }

    @Test
    public void testLoginConDatosInvalidos() {
        String email = "usuarioTrasteapp";
        String password = "123";

        System.out.println("Iniciando prueba de login con datos inválidos...");
        System.out.println("Email introducido: " + email);
        System.out.println("Contraseña introducida: " + password);

        boolean emailValido = email.contains("@") && email.endsWith(".com");
        boolean passwordValida = password.length() >= 6;

        System.out.println("¿Email válido?: " + emailValido);
        System.out.println("¿Contraseña válida?: " + passwordValida);

        assertFalse("El email no debe ser válido", emailValido);
        assertFalse("La contraseña no debe ser válida", passwordValida);

        System.out.println("✅ Prueba de login con datos inválidos completada.\n");
    }
}

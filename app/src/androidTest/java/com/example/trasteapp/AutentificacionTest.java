package com.example.trasteapp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AutentificacionTest {

    private FirebaseAuth auth;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void loginUsuarioValido() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] testPassed = {false};

        auth.signInWithEmailAndPassword("pepito@gmail.com", "jorge1")
                .addOnSuccessListener(result -> {
                    testPassed[0] = true;
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Login exitoso con credenciales válidas", testPassed[0]);
    }

    @Test
    public void loginUsuarioNoExiste() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] mensaje = {null};
        final boolean[] errorCorrecto = {false};

        auth.signInWithEmailAndPassword("noexiste_92847@email.com", "loquesea")
                .addOnFailureListener(e -> {
                    mensaje[0] = e.getMessage();
                    if (mensaje[0] != null && mensaje[0].contains("INVALID_LOGIN_CREDENTIALS")) {
                        errorCorrecto[0] = true;
                    }
                    latch.countDown();
                })
                .addOnSuccessListener(result -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Se esperaba error de credenciales inválidas", errorCorrecto[0]);
    }




    @Test
    public void loginPasswordIncorrecta() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] correctError = {false};

        auth.signInWithEmailAndPassword("pepito@gmail.com ", "incorrecta")
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        correctError[0] = true;
                    }
                    latch.countDown();
                });

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe fallar por contraseña incorrecta", correctError[0]);
    }


}


package com.example.trasteapp;

import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test para comprobar que se pueden buscar trasteros en Firebase.
 * Se hacen 3 pruebas: traer todos, buscar por ciudad conocida y por ciudad que no tiene.
 *
 * @author Jorge Fresno
 */
@RunWith(AndroidJUnit4.class)
public class BuscarTrasterosTest {

    // Acceso a la base de datos Firebase
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Test 1 - Carga general de trasteros. Comprueba que la colección no esté vacía.
     */
    @Test
    public void buscarTodosLosTrasteros() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] cargados = {false};

        db.collection("trasteros")
                .get()
                .addOnSuccessListener(query -> {
                    cargados[0] = query != null && !query.isEmpty();
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("TEST", "Error al cargar trasteros", e);
                    latch.countDown();
                });

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe cargar al menos un trastero", cargados[0]);
    }

    /**
     * Test 2 - Busca trasteros en una ciudad donde sí hay (ej: Madrid).
     */
    @Test
    public void buscarTrasterosPorCiudadExistente() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] encontrados = {false};

        db.collection("trasteros")
                .whereEqualTo("ciudad", "Madrid")
                .get()
                .addOnSuccessListener(query -> {
                    encontrados[0] = query != null && !query.isEmpty();
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe encontrar trasteros en Madrid", encontrados[0]);
    }

    /**
     * Test 3 - Busca trasteros en una ciudad donde no debería haber (ej: Zaragoza).
     */
    @Test
    public void buscarTrasterosPorCiudadInexistente() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] vacia = {false};

        db.collection("trasteros")
                .whereEqualTo("ciudad", "Zaragoza")
                .get()
                .addOnSuccessListener(query -> {
                    vacia[0] = query != null && query.isEmpty();
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe retornar una lista vacía", vacia[0]);
    }
}

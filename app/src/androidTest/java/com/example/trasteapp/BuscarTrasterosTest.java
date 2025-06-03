package com.example.trasteapp;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BuscarTrasterosTest {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Test
    public void buscarTodosLosTrasteros() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] datosCargados = {false};

        db.collection("trasteros")
                .get()
                .addOnSuccessListener(query -> {
                    datosCargados[0] = query != null && !query.isEmpty();
                    latch.countDown();
                })
                .addOnFailureListener(e -> {
                    Log.e("TEST", "Error al cargar trasteros", e);
                    latch.countDown();
                });

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe cargar al menos un trastero", datosCargados[0]);
    }

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

    @Test
    public void buscarTrasterosPorCiudadInexistente() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] listaVacia = {false};

        db.collection("trasteros")
                .whereEqualTo("ciudad", "Zaragoza")
                .get()
                .addOnSuccessListener(query -> {
                    listaVacia[0] = query != null && query.isEmpty();
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Debe retornar una lista vacía", listaVacia[0]);
    }
}


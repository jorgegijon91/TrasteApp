package com.example.trasteapp;

import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ContratoTest {

    private static final String TAG = "ContratoTest";
    private FirebaseFirestore db;
    private DocumentReference contratoRef;
    private static final String USER_ID = "VXc5DiWjL1gpQ1OmepToeeEzbf83";
    private static final String CONTRATO_ID = "trastero_oviedo_naranco";

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        contratoRef = db.collection("usuarios")
                .document(USER_ID)
                .collection("contratos")
                .document(CONTRATO_ID);
    }

    @Test
    public void contratoDebeExistir() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] existe = {false};

        contratoRef.get().addOnSuccessListener(documentSnapshot -> {
            existe[0] = documentSnapshot.exists();
            latch.countDown();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al obtener el contrato", e);
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("El contrato debe existir en la base de datos", existe[0]);
    }

    @Test
    public void contratoDebeTenerCamposValidos() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] camposValidos = {false};

        contratoRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String ciudad  = document.getString("ciudad");
                String descripcion = document.getString("descripcion");

                camposValidos[0] = ciudad != null && !ciudad.isEmpty()
                        && descripcion != null && !descripcion.isEmpty();
            }
            latch.countDown();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al validar campos", e);
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("Los campos clave del contrato deben estar definidos correctamente", camposValidos[0]);
    }

    @Test
    public void contratoDebeEstarFirmado() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final Boolean[] firmado = {null};

        contratoRef.get().addOnSuccessListener(document -> {
            if (document.exists()) {
                firmado[0] = document.getBoolean("firmado");
            }
            latch.countDown();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al verificar firma", e);
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
        assertNotNull("El campo 'firmado' debe existir", firmado[0]);
        assertTrue("El contrato debe estar firmado", firmado[0]);
    }
}

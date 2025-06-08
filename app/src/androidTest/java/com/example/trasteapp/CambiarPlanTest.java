package com.example.trasteapp;

import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test sencillo para mostrar el plan actual (campo 'tipo') de un usuario en Firebase.
 * Solo imprime el resultado en consola.
 *
 * @author Jorge Fresno
 */
@RunWith(AndroidJUnit4.class)
public class CambiarPlanTest {

    private FirebaseFirestore db;
    private DocumentReference userRef;
    private static final String USER_ID = "VXc5DiWjL1gpQ1OmepToeeEzbf83";

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("usuarios").document(USER_ID);
    }

    @Test
    public void testMostrarPlanDelUsuario() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] plan = {null};

        userRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                plan[0] = doc.getString("tipo");
                Log.d("CambiarPlanTest", "Plan actual del usuario: " + plan[0]);
            } else {
                Log.w("CambiarPlanTest", "No se encontró el documento del usuario");
            }
            latch.countDown();
        }).addOnFailureListener(e -> {
            Log.e("CambiarPlanTest", "Error al obtener el plan", e);
            latch.countDown();
        });

        latch.await(5, TimeUnit.SECONDS);
        assertNotNull("El campo 'tipo' (plan del usuario) no puede ser nulo", plan[0]);
    }
}

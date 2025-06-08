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
 * Clase de pruebas para comprobar que las facturas del usuario están bien guardadas en Firebase.
 * Se mira si existen, si están pendientes o ya pagadas.
 *
 * @author Jorge Fresno
 */
@RunWith(AndroidJUnit4.class)
public class FacturaTest {

    private FirebaseFirestore db;
    private DocumentReference facturaRef;

    // ID de prueba del usuario y la factura con datos de Firebase
    private static final String USER_ID = "VXc5DiWjL1gpQ1OmepToeeEzbf83";
    private static final String FACTURA_ID = "factura_trastero_oviedo_naranco";

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        facturaRef = db.collection("usuarios")
                .document(USER_ID)
                .collection("facturas")
                .document(FACTURA_ID);
    }

    /**
     * Test 1 - Comprobar que la factura existe en la base de datos.
     */
    @Test
    public void testFacturaExiste() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] existe = {false};

        facturaRef.get().addOnSuccessListener(doc -> {
            existe[0] = doc.exists(); // Si existe el documento, guardamos true
            latch.countDown();
        }).addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("La factura debe existir en Firestore", existe[0]);
    }

    /**
     * Test 2 - Verifica que la factura esté marcada como pendiente.
     */
    @Test
    public void testFacturaPendiente() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] pendiente = {false};

        facturaRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String estado = doc.getString("estado");
                pendiente[0] = "pendiente".equalsIgnoreCase(estado); // Comparamos estado
            }
            latch.countDown();
        }).addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("La factura debe estar en estado 'pendiente'", pendiente[0]);
    }

    /**
     * Test 3 - Comprueba si la factura ya está pagada.
     */
    @Test
    public void testFacturaPagada() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] pagada = {false};

        facturaRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String estado = doc.getString("estado");
                pagada[0] = "pagada".equalsIgnoreCase(estado);
            }
            latch.countDown();
        }).addOnFailureListener(e -> latch.countDown());

        latch.await(5, TimeUnit.SECONDS);
        assertTrue("La factura debe estar en estado 'pagada'", pagada[0]);
    }
}

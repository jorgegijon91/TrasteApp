package com.example.trasteapp.trasteros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.trasteapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Actividad que muestra los detalles de un trastero disponible.
 * Permite al usuario visualizar la información, imágenes y reservarlo temporalmente.
 * Si se reserva, se registra un contrato preliminar en Firestore con 24h para firmarlo.
 *
 * @author Jorge Fresno
 */
public class TrasteroDetalleActivity extends AppCompatActivity {

    // Componentes UI
    private TextView ciudadTextView, descripcionTextView, precioTextView;
    private ViewPager2 viewPager;
    private Button alquilarButton, cancelarButton;

    // Datos del trastero recibidos por intent
    private String ciudad, descripcion, precio, trasteroId;
    private List<String> imagenes;

    /**
     * Método que se ejecuta al crear la actividad.
     * Muestra la información del trastero, carga imágenes y permite reservar o cancelar.
     *
     * @param savedInstanceState Estado anterior guardado de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trastero_detalle);

        // Referencias UI
        ciudadTextView = findViewById(R.id.detalle_ciudad);
        descripcionTextView = findViewById(R.id.detalle_descripcion);
        precioTextView = findViewById(R.id.detalle_precio);
        viewPager = findViewById(R.id.imagenes_pager);
        alquilarButton = findViewById(R.id.boton_alquilar);
        cancelarButton = findViewById(R.id.boton_cancelar);

        // Obtener datos del trastero desde la actividad anterior
        trasteroId = getIntent().getStringExtra("idDocumento");
        ciudad = getIntent().getStringExtra("ciudad");
        descripcion = getIntent().getStringExtra("descripcion");
        precio = getIntent().getStringExtra("precio");
        imagenes = getIntent().getStringArrayListExtra("imagenes");

        // Mostrar datos
        ciudadTextView.setText(ciudad);
        descripcionTextView.setText(descripcion);
        precioTextView.setText(precio);

        // Mostrar galería de imágenes si existen
        if (imagenes != null && !imagenes.isEmpty()) {
            ImagenAdapter adapter = new ImagenAdapter(this, imagenes);
            viewPager.setAdapter(adapter);
        }

        // Botón para cancelar y salir
        cancelarButton.setOnClickListener(v -> finish());

        // Botón para reservar el trastero
        alquilarButton.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Debes iniciar sesión para reservar", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Timestamp fechaReserva = Timestamp.now();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Marcar trastero como reservado
            db.collection("trasteros").document(trasteroId)
                    .update("reservado", true, "fecha_reserva", fechaReserva)
                    .addOnSuccessListener(unused -> {
                        // Crear contrato preliminar en Firestore
                        Map<String, Object> contrato = new HashMap<>();
                        contrato.put("ciudad", ciudad);
                        contrato.put("descripcion", descripcion);
                        contrato.put("precio", precio);
                        contrato.put("imagenes", imagenes);
                        contrato.put("fecha_reserva", fechaReserva);
                        contrato.put("firmado", false);

                        db.collection("usuarios").document(uid)
                                .collection("contratos").document(trasteroId)
                                .set(contrato)
                                .addOnSuccessListener(unused2 -> {
                                    Toast.makeText(this, "Reserva realizada. Tienes 24h para firmar el contrato.", Toast.LENGTH_LONG).show();
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error al guardar el contrato.", Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al reservar el trastero.", Toast.LENGTH_SHORT).show());
        });
    }
}

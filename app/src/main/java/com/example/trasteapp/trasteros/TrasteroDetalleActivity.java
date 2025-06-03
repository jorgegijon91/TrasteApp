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

public class TrasteroDetalleActivity extends AppCompatActivity {

    // UI
    private TextView ciudadTextView, descripcionTextView, precioTextView;
    private ViewPager2 viewPager;
    private Button alquilarButton, cancelarButton;

    // Datos recibidos
    private String ciudad, descripcion, precio, trasteroId;
    private List<String> imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trastero_detalle);

        // Referencias a la interfaz
        ciudadTextView = findViewById(R.id.detalle_ciudad);
        descripcionTextView = findViewById(R.id.detalle_descripcion);
        precioTextView = findViewById(R.id.detalle_precio);
        viewPager = findViewById(R.id.imagenes_pager);
        alquilarButton = findViewById(R.id.boton_alquilar);
        cancelarButton = findViewById(R.id.boton_cancelar);

        // Obtener datos pasados desde la pantalla anterior
        trasteroId = getIntent().getStringExtra("idDocumento");
        ciudad = getIntent().getStringExtra("ciudad");
        descripcion = getIntent().getStringExtra("descripcion");
        precio = getIntent().getStringExtra("precio");
        imagenes = getIntent().getStringArrayListExtra("imagenes");

        // Mostrar los datos en pantalla
        ciudadTextView.setText(ciudad);
        descripcionTextView.setText(descripcion);
        precioTextView.setText(precio);

        // Mostrar imágenes en un ViewPager
        if (imagenes != null && !imagenes.isEmpty()) {
            ImagenAdapter adapter = new ImagenAdapter(this, imagenes);
            viewPager.setAdapter(adapter);
        }

        // Botón para cancelar y volver atrás
        cancelarButton.setOnClickListener(v -> finish());

        // Botón para reservar el trastero (sin firmar aún)
        alquilarButton.setOnClickListener(v -> {
            // Verifica que el usuario esté logueado
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Debes iniciar sesión para reservar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Datos del usuario y hora de reserva
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Timestamp fechaReserva = Timestamp.now();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 1. Marcar trastero como reservado en la colección general
            db.collection("trasteros").document(trasteroId)
                    .update("reservado", true, "fecha_reserva", fechaReserva)
                    .addOnSuccessListener(unused -> {
                        // 2. Guardar un contrato preliminar para este usuario
                        Map<String, Object> contrato = new HashMap<>();
                        contrato.put("ciudad", ciudad);
                        contrato.put("descripcion", descripcion);
                        contrato.put("precio", precio);
                        contrato.put("imagenes", imagenes);
                        contrato.put("fecha_reserva", fechaReserva);
                        contrato.put("firmado", false); // Aún no firmado

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

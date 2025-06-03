package com.example.trasteapp.trasteros;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ContratosActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Referencia a la base de datos Firestore
    private LinearLayout contratosLayout; // Contenedor donde se añadirán los contratos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contratos);

        contratosLayout = findViewById(R.id.contratos_layout); // Enlaza con el layout
        db = FirebaseFirestore.getInstance(); // Inicializa Firestore
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verifica si el usuario está autenticado
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish(); // Cierra la actividad si no hay sesión activa
            return;
        }

        cargarContratos(); // Carga los contratos desde Firestore
    }

    private void cargarContratos() {
        contratosLayout.removeAllViews(); // Limpia los contratos anteriores

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); // ID del usuario actual

        // Consulta todos los contratos del usuario
        db.collection("usuarios").document(uid).collection("contratos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<String> idsMostrados = new HashSet<>(); // Para evitar duplicados

                    for (DocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        if (idsMostrados.contains(id)) continue; // Si ya se mostró, omitir
                        idsMostrados.add(id);

                        Boolean firmado = doc.getBoolean("firmado");
                        if (firmado == null) firmado = false;

                        Timestamp fechaReserva = doc.getTimestamp("fecha_reserva");
                        // Si no está firmado y pasó más de 24h desde la reserva, omitir
                        if (!firmado && fechaReserva != null) {
                            long horas = (Timestamp.now().getSeconds() - fechaReserva.getSeconds()) / 3600;
                            if (horas >= 24) continue;
                        }

                        // Obtener datos del contrato
                        String ciudad = doc.getString("ciudad");
                        String descripcion = doc.getString("descripcion");
                        String precio = doc.getString("precio");

                        Timestamp fechaIni = doc.getTimestamp("fecha_inicio");
                        Timestamp fechaFin = doc.getTimestamp("fecha_fin");

                        String fechaInicio = fechaIni != null
                                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaIni.toDate())
                                : "";
                        String fechaFinTxt = fechaFin != null
                                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fechaFin.toDate())
                                : "";

                        // Crear un TextView con la información del contrato
                        TextView contratoView = new TextView(this);
                        contratoView.setText(
                                "📍 " + ciudad + "\n" +
                                        descripcion + "\n" +
                                        "💶 Precio: " + precio + "\n" +
                                        "📅 Desde: " + fechaInicio + " hasta " + fechaFinTxt
                        );
                        contratoView.setPadding(0, 20, 0, 10);

                        // Botón para firmar el contrato
                        Button firmarBtn = new Button(this);
                        firmarBtn.setText(firmado ? "CONTRATO FIRMADO" : "FIRMAR CONTRATO");
                        firmarBtn.setEnabled(!firmado); // Deshabilita si ya está firmado

                        String idTrastero = doc.getId(); // ID del trastero asociado

                        // Acción del botón si el contrato aún no está firmado
                        if (!firmado) {
                            firmarBtn.setOnClickListener(v -> {
                                Intent intent = new Intent(this, FirmarContratoActivity.class);
                                intent.putExtra("idTrastero", idTrastero);
                                intent.putExtra("ciudad", ciudad);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("precio", precio);
                                intent.putStringArrayListExtra("imagenes", (ArrayList<String>) doc.get("imagenes"));
                                startActivity(intent); // Abre la actividad de firma
                            });
                        }

                        // Agrega los elementos al layout
                        contratosLayout.addView(contratoView);
                        contratosLayout.addView(firmarBtn);
                    }
                });
    }
}

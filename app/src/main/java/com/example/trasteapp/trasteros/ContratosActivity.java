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

/**
 * Actividad que muestra la lista de contratos del usuario actual.
 * Permite visualizar contratos ya firmados o pendientes, y en este último caso,
 * acceder a la actividad de firma de contrato.
 *
 * @author Jorge Fresno
 */
public class ContratosActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private LinearLayout contratosLayout;

    /**
     * Método llamado al crear la actividad. Inicializa vistas y Firestore.
     *
     * @param savedInstanceState Estado previo de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contratos);

        contratosLayout = findViewById(R.id.contratos_layout);
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Método que se ejecuta cada vez que la actividad vuelve a estar en primer plano.
     * Verifica si el usuario está autenticado y carga los contratos desde Firestore.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }

        cargarContratos();
    }

    /**
     * Carga los contratos del usuario actual desde Firestore y los muestra en la interfaz.
     * También gestiona el botón de firma para contratos aún no firmados.
     */
    private void cargarContratos() {
        contratosLayout.removeAllViews();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios").document(uid).collection("contratos")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Set<String> idsMostrados = new HashSet<>();

                    for (DocumentSnapshot doc : querySnapshot) {
                        String id = doc.getId();
                        if (idsMostrados.contains(id)) continue;
                        idsMostrados.add(id);

                        Boolean firmado = doc.getBoolean("firmado");
                        if (firmado == null) firmado = false;

                        Timestamp fechaReserva = doc.getTimestamp("fecha_reserva");
                        if (!firmado && fechaReserva != null) {
                            long horas = (Timestamp.now().getSeconds() - fechaReserva.getSeconds()) / 3600;
                            if (horas >= 24) continue;
                        }

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

                        // Crear vista de contrato
                        TextView contratoView = new TextView(this);
                        contratoView.setText(
                                " Ciudad: " + ciudad + "\n" +
                                        descripcion + "\n" +
                                        " Precio: " + precio + "\n" +
                                        " Desde: " + fechaInicio + " hasta " + fechaFinTxt
                        );
                        contratoView.setPadding(0, 20, 0, 10);

                        // Botón para firmar contrato si está pendiente
                        Button firmarBtn = new Button(this);
                        firmarBtn.setText(firmado ? "CONTRATO FIRMADO" : "FIRMAR CONTRATO");
                        firmarBtn.setEnabled(!firmado);

                        String idTrastero = doc.getId();

                        if (!firmado) {
                            firmarBtn.setOnClickListener(v -> {
                                Intent intent = new Intent(this, FirmarContratoActivity.class);
                                intent.putExtra("idTrastero", idTrastero);
                                intent.putExtra("ciudad", ciudad);
                                intent.putExtra("descripcion", descripcion);
                                intent.putExtra("precio", precio);
                                intent.putStringArrayListExtra("imagenes", (ArrayList<String>) doc.get("imagenes"));
                                startActivity(intent);
                            });
                        }

                        contratosLayout.addView(contratoView);
                        contratosLayout.addView(firmarBtn);
                    }
                });
    }
}

package com.example.trasteapp.facturas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.trasteapp.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class FacturaActivity extends AppCompatActivity {

    private TextView tvCiudad, tvDescripcion, tvPrecio;
    private ImageView ivTrastero;
    private Button btnPagar, btnDomiciliar;

    private FirebaseFirestore db;
    private double precioGlobal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        // Asociación con elementos de la interfaz
        tvCiudad = findViewById(R.id.tvCiudad);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvPrecio = findViewById(R.id.tvPrecio);
        ivTrastero = findViewById(R.id.ivTrastero);
        btnPagar = findViewById(R.id.btnPagar);
        btnDomiciliar = findViewById(R.id.btnDomiciliar);

        db = FirebaseFirestore.getInstance();

        // Cargar contrato ya firmado
        cargarContratoFirmado();

        // Acciones de los botones
        btnPagar.setOnClickListener(v -> registrarFactura(true));
        btnDomiciliar.setOnClickListener(v -> registrarFactura(false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Previene acceso si el usuario ha cerrado sesión
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }

    private void cargarContratoFirmado() {
        // Validar sesión de usuario
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener solo contratos firmados
        db.collection("usuarios").document(uid).collection("contratos")
                .whereEqualTo("firmado", true)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        DocumentSnapshot doc = snapshots.getDocuments().get(0);
                        Map<String, Object> data = doc.getData();
                        if (data == null) return;

                        // Obtener y mostrar los datos del contrato
                        String ciudad = (String) data.get("ciudad");
                        String descripcion = (String) data.get("descripcion");
                        String precioStr = String.valueOf(data.get("precio")).replaceAll("[^\\d.]", "");
                        try {
                            precioGlobal = Double.parseDouble(precioStr);
                        } catch (Exception e) {
                            precioGlobal = 0;
                        }

                        List<String> imagenes = (List<String>) data.get("imagenes");

                        tvCiudad.setText("Ciudad: " + ciudad);
                        tvDescripcion.setText(descripcion);
                        tvPrecio.setText("Precio: " + precioStr + "€");

                        if (imagenes != null && !imagenes.isEmpty()) {
                            Glide.with(this).load(imagenes.get(0)).into(ivTrastero);
                        }

                        verificarRestricciones(uid);
                    } else {
                        Toast.makeText(this, "No se encontró ningún contrato firmado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
    }

    private void verificarRestricciones(String uid) {
        // Revisar si ya está domiciliado
        db.collection("usuarios").document(uid).collection("contratos")
                .whereEqualTo("firmado", true)
                .limit(1)
                .get()
                .addOnSuccessListener(contratos -> {
                    if (!contratos.isEmpty()) {
                        Boolean domiciliado = contratos.getDocuments().get(0).getBoolean("domiciliado");
                        if (domiciliado != null && domiciliado) {
                            btnPagar.setEnabled(false);
                            btnDomiciliar.setEnabled(false);
                            return;
                        }
                    }

                    // Verificar si ya se pagó la factura del mes actual
                    String mesActual = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());
                    db.collection("usuarios").document(uid)
                            .collection("facturas")
                            .document("factura_" + mesActual)
                            .get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    String estado = doc.getString("estado");
                                    Timestamp fecha = doc.getTimestamp("fecha");

                                    if ("pagada".equalsIgnoreCase(estado) && fecha != null) {
                                        long dias = (Timestamp.now().getSeconds() - fecha.getSeconds()) / (60 * 60 * 24);
                                        if (dias < 30) {
                                            btnPagar.setEnabled(false);
                                        }
                                    }
                                }
                            });
                });
    }

    private void registrarFactura(boolean pagoUnico) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (pagoUnico) {
            // Crear factura con estado pagada
            Map<String, Object> factura = new HashMap<>();
            factura.put("fecha", Timestamp.now());
            factura.put("importe", precioGlobal);
            factura.put("estado", "pagada");

            String mesFactura = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());

            db.collection("usuarios").document(uid)
                    .collection("facturas")
                    .document("factura_" + mesFactura)
                    .set(factura)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Factura pagada.", Toast.LENGTH_SHORT).show();
                        btnPagar.setEnabled(false);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al pagar.", Toast.LENGTH_SHORT).show());

        } else {
            // Marcar contrato como domiciliado
            db.collection("usuarios").document(uid)
                    .collection("contratos")
                    .whereEqualTo("firmado", true)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(snapshots -> {
                        if (!snapshots.isEmpty()) {
                            String id = snapshots.getDocuments().get(0).getId();
                            db.collection("usuarios").document(uid)
                                    .collection("contratos").document(id)
                                    .update("domiciliado", true)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Pago domiciliado.", Toast.LENGTH_SHORT).show();
                                        btnPagar.setEnabled(false);
                                        btnDomiciliar.setEnabled(false);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error al domiciliar.", Toast.LENGTH_SHORT).show());
                        }
                    });
        }
    }
}

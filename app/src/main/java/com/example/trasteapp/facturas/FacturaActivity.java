package com.example.trasteapp.facturas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * Actividad que permite a usuarios premium visualizar el contrato vigente
 * y gestionar el pago manual o domiciliado de su factura mensual.
 * Incluye validaciones sobre contratos firmados y pagos ya realizados.
 *
 * @author Jorge Fresno
 */
public class FacturaActivity extends AppCompatActivity {

    private LinearLayout layoutContratos;
    private TextView tvSinContratos;
    private FirebaseFirestore db;

    /**
     * Inicializa la actividad, Firebase y los elementos visuales.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        layoutContratos = findViewById(R.id.layout_contratos);
        tvSinContratos = findViewById(R.id.tvSinContratos);
        db = FirebaseFirestore.getInstance();

        cargarContratos();
    }

    /**
     * Finaliza la actividad si no hay usuario logueado.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) finish();
    }

    /**
     * Recupera los contratos firmados del usuario y crea tarjetas individuales por cada uno.
     */
    private void cargarContratos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("usuarios").document(uid).collection("contratos")
                .whereEqualTo("firmado", true)
                .get()
                .addOnSuccessListener(snapshots -> {
                    layoutContratos.removeAllViews();

                    if (snapshots.isEmpty()) {
                        tvSinContratos.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvSinContratos.setVisibility(View.GONE);

                    for (DocumentSnapshot doc : snapshots) {
                        Map<String, Object> data = doc.getData();
                        if (data == null) continue;

                        String ciudad = (String) data.get("ciudad");
                        String descripcion = (String) data.get("descripcion");
                        String precioStr = String.valueOf(data.get("precio"));
                        List<String> imagenes = (List<String>) data.get("imagenes");
                        String contratoId = doc.getId();

                        // Generar ID de factura por contrato y mes
                        String mes = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());
                        String facturaId = "factura_" + contratoId + "_" + mes;

                        // Verificar estado antes de crear la vista
                        db.collection("usuarios").document(uid)
                                .collection("facturas")
                                .document(facturaId)
                                .get()
                                .addOnSuccessListener(facturaDoc -> {

                                    db.collection("usuarios").document(uid)
                                            .collection("contratos")
                                            .document(contratoId)
                                            .get()
                                            .addOnSuccessListener(contratoDoc -> {

                                                boolean pagado = facturaDoc.exists() &&
                                                        "pagada".equalsIgnoreCase(facturaDoc.getString("estado"));

                                                boolean domiciliado = contratoDoc.getBoolean("domiciliado") != null &&
                                                        contratoDoc.getBoolean("domiciliado");

                                                // Inflar vista de tarjeta
                                                View card = getLayoutInflater().inflate(R.layout.card_contrato, null);

                                                ImageView iv = card.findViewById(R.id.ivTrastero);
                                                TextView tvInfo = card.findViewById(R.id.tvInfo);
                                                TextView tvPrecio = card.findViewById(R.id.tvPrecio);
                                                Button btnPagar = card.findViewById(R.id.btnPagar);
                                                Button btnDomiciliar = card.findViewById(R.id.btnDomiciliar);
                                                Button btnEstado = card.findViewById(R.id.btnEstado);

                                                if (imagenes != null && !imagenes.isEmpty()) {
                                                    Glide.with(this).load(imagenes.get(0)).into(iv);
                                                }

                                                tvInfo.setText("\ud83d\udccd Ciudad: " + ciudad + "\n" + descripcion);
                                                tvPrecio.setText("\ud83d\udcb6 Precio: " + precioStr);

                                                if (domiciliado) {
                                                    btnPagar.setVisibility(View.GONE);
                                                    btnDomiciliar.setVisibility(View.GONE);
                                                    btnEstado.setText("PAGO DOMICILIADO");
                                                    btnEstado.setVisibility(View.VISIBLE);
                                                } else if (pagado) {
                                                    btnPagar.setVisibility(View.GONE);
                                                    btnEstado.setText("FACTURA PAGADA");
                                                    btnEstado.setVisibility(View.VISIBLE);
                                                } else {
                                                    btnEstado.setVisibility(View.GONE);
                                                    btnPagar.setVisibility(View.VISIBLE);
                                                    btnDomiciliar.setVisibility(View.VISIBLE);
                                                }

                                                btnPagar.setOnClickListener(v -> {
                                                    pagarFactura(uid, contratoId, precioStr);
                                                    btnPagar.setEnabled(false);
                                                    btnPagar.setVisibility(View.GONE);
                                                    btnEstado.setText("FACTURA PAGADA");
                                                    btnEstado.setVisibility(View.VISIBLE);
                                                });

                                                btnDomiciliar.setOnClickListener(v -> {
                                                    domiciliarContrato(uid, contratoId);
                                                    btnPagar.setEnabled(false);
                                                    btnDomiciliar.setEnabled(false);
                                                    btnPagar.setVisibility(View.GONE);
                                                    btnDomiciliar.setVisibility(View.GONE);
                                                    btnEstado.setText("PAGO DOMICILIADO");
                                                    btnEstado.setVisibility(View.VISIBLE);
                                                });
                                                layoutContratos.addView(card);
                                            });
                                });
                    }
                });
    }

    /**
     * Registra una factura pagada simulada para un contrato en el mes actual.
     */
    private void pagarFactura(String uid, String contratoId, String importeTexto) {
        String mes = new SimpleDateFormat("yyyyMM", Locale.getDefault()).format(new Date());
        String facturaId = "factura_" + contratoId + "_" + mes;

        Map<String, Object> factura = new HashMap<>();
        factura.put("fecha", Timestamp.now());
        factura.put("importe", importeTexto);
        factura.put("estado", "pagada");

        db.collection("usuarios").document(uid)
                .collection("facturas")
                .document(facturaId)
                .set(factura)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Factura pagada.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al pagar factura.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Marca un contrato individual como domiciliado.
     */
    private void domiciliarContrato(String uid, String contratoId) {
        db.collection("usuarios").document(uid)
                .collection("contratos")
                .document(contratoId)
                .update("domiciliado", true)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Pago domiciliado.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al domiciliar.", Toast.LENGTH_SHORT).show());
    }
}
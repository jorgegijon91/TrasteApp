package com.example.trasteapp.trasteros;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirmarContratoActivity extends AppCompatActivity {

    // Elementos de interfaz
    private SignaturePad signaturePad;
    private Button btnEnviar, btnLimpiar;

    // Datos recibidos del trastero
    private String idTrastero;
    private String ciudad, descripcion, precio;
    private List<String> imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmar_contrato);

        // Asociar vistas
        signaturePad = findViewById(R.id.signature_pad);
        btnEnviar = findViewById(R.id.botonEnviarFirma);
        btnLimpiar = findViewById(R.id.btn_limpiar_firma);

        // Obtener datos del Intent
        idTrastero = getIntent().getStringExtra("idTrastero");
        ciudad = getIntent().getStringExtra("ciudad");
        descripcion = getIntent().getStringExtra("descripcion");
        precio = getIntent().getStringExtra("precio");
        imagenes = getIntent().getStringArrayListExtra("imagenes");

        // Verificar usuario logueado y trastero válido
        if (FirebaseAuth.getInstance().getCurrentUser() == null || idTrastero == null) {
            Toast.makeText(this, "Error: usuario o contrato no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Botón para limpiar la firma
        btnLimpiar.setOnClickListener(v -> signaturePad.clear());

        // Botón para enviar la firma y registrar el contrato
        btnEnviar.setOnClickListener(v -> {
            if (signaturePad.isEmpty()) {
                Toast.makeText(this, "Debes firmar antes de continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirmación previa con un diálogo
            new AlertDialog.Builder(this)
                    .setTitle("Condiciones legales")
                    .setMessage("Al firmar este contrato, aceptas los términos de uso del trastero. ¿Deseas continuar?")
                    .setPositiveButton("Firmar", (dialog, which) -> subirFirmaYRegistrar())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void subirFirmaYRegistrar() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener la firma como imagen en bytes
        Bitmap firmaBitmap = signaturePad.getSignatureBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        firmaBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] firmaBytes = baos.toByteArray();

        String path = "firmas/" + uid + "/" + idTrastero + ".png";

        // Subir firma a Firebase Storage
        FirebaseStorage.getInstance().getReference(path)
                .putBytes(firmaBytes)
                .addOnSuccessListener(taskSnapshot ->
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            // Fechas de inicio y fin del contrato (1 año de duración)
                            Timestamp fechaInicio = Timestamp.now();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(fechaInicio.toDate());
                            cal.add(Calendar.YEAR, 1);
                            Timestamp fechaFin = new Timestamp(cal.getTime());

                            // Crear el objeto del contrato firmado
                            Map<String, Object> contrato = new HashMap<>();
                            contrato.put("ciudad", ciudad);
                            contrato.put("descripcion", descripcion);
                            contrato.put("precio", precio);
                            contrato.put("fecha_inicio", fechaInicio);
                            contrato.put("fecha_fin", fechaFin);
                            contrato.put("imagenes", imagenes);
                            contrato.put("firmado", true);
                            contrato.put("fecha_firma", fechaInicio);
                            contrato.put("firma_url", uri.toString());

                            // Crear factura pendiente (no pagada aún)
                            Map<String, Object> factura = new HashMap<>();
                            factura.put("fecha", fechaInicio);
                            factura.put("trastero", ciudad);
                            factura.put("importe", precio);
                            factura.put("estado", "pendiente");

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Guardar contrato firmado
                            db.collection("usuarios").document(uid)
                                    .collection("contratos").document(idTrastero)
                                    .set(contrato);

                            // Crear factura asociada
                            db.collection("usuarios").document(uid)
                                    .collection("facturas")
                                    .document("factura_" + idTrastero)
                                    .set(factura);

                            // Marcar el trastero como alquilado
                            db.collection("trasteros").document(idTrastero)
                                    .update("alquilado", true, "reservado", false);

                            Toast.makeText(this, "Contrato firmado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                );
    }
}

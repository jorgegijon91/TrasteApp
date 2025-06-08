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

/**
 * Actividad que permite al usuario firmar un contrato de alquiler para un trastero.
 * La firma se guarda en Firebase Storage y se genera un contrato y una factura pendiente
 * en Firestore. Además, el trastero se marca como alquilado.
 *
 * @author Jorge Fresno
 */
public class FirmarContratoActivity extends AppCompatActivity {

    private SignaturePad signaturePad;
    private Button btnEnviar, btnLimpiar;

    private String idTrastero;
    private String ciudad, descripcion, precio;
    private List<String> imagenes;

    /**
     * Inicializa la interfaz de firma y obtiene los datos del contrato desde el intent.
     * También configura los botones para limpiar la firma y enviarla.
     *
     * @param savedInstanceState Estado previo de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmar_contrato);

        signaturePad = findViewById(R.id.signature_pad);
        btnEnviar = findViewById(R.id.botonEnviarFirma);
        btnLimpiar = findViewById(R.id.btn_limpiar_firma);

        idTrastero = getIntent().getStringExtra("idTrastero");
        ciudad = getIntent().getStringExtra("ciudad");
        descripcion = getIntent().getStringExtra("descripcion");
        precio = getIntent().getStringExtra("precio");
        imagenes = getIntent().getStringArrayListExtra("imagenes");

        // Validación de datos e inicio de sesión
        if (FirebaseAuth.getInstance().getCurrentUser() == null || idTrastero == null) {
            Toast.makeText(this, "Error: usuario o contrato no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnLimpiar.setOnClickListener(v -> signaturePad.clear());

        btnEnviar.setOnClickListener(v -> {
            if (signaturePad.isEmpty()) {
                Toast.makeText(this, "Debes firmar antes de continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirmación legal antes de proceder
            new AlertDialog.Builder(this)
                    .setTitle("Condiciones legales")
                    .setMessage("Al firmar este contrato, aceptas los términos de uso del trastero. ¿Deseas continuar?")
                    .setPositiveButton("Firmar", (dialog, which) -> subirFirmaYRegistrar())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    /**
     * Sube la firma del usuario a Firebase Storage, guarda el contrato firmado
     * en Firestore, genera una factura pendiente y marca el trastero como alquilado.
     */
    private void subirFirmaYRegistrar() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Convertir firma a bytes
        Bitmap firmaBitmap = signaturePad.getSignatureBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        firmaBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] firmaBytes = baos.toByteArray();

        String path = "firmas/" + uid + "/" + idTrastero + ".png";

        FirebaseStorage.getInstance().getReference(path)
                .putBytes(firmaBytes)
                .addOnSuccessListener(taskSnapshot ->
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            Timestamp fechaInicio = Timestamp.now();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(fechaInicio.toDate());
                            cal.add(Calendar.YEAR, 1);
                            Timestamp fechaFin = new Timestamp(cal.getTime());

                            // Crear contrato
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

                            // Crear factura pendiente
                            Map<String, Object> factura = new HashMap<>();
                            factura.put("fecha", fechaInicio);
                            factura.put("trastero", ciudad);
                            factura.put("importe", precio);
                            factura.put("estado", "pendiente");

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Guardar contrato
                            db.collection("usuarios").document(uid)
                                    .collection("contratos").document(idTrastero)
                                    .set(contrato);

                            // Guardar factura
                            db.collection("usuarios").document(uid)
                                    .collection("facturas").document("factura_" + idTrastero)
                                    .set(factura);

                            // Actualizar estado del trastero
                            db.collection("trasteros").document(idTrastero)
                                    .update("alquilado", true, "reservado", false);

                            Toast.makeText(this, "Contrato firmado correctamente", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                );
    }
}

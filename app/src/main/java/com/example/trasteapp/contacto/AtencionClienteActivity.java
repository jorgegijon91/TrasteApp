package com.example.trasteapp.contacto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;

/**
 * Actividad que permite al usuario contactar con el soporte de TrasteApp
 * enviando un correo electrónico desde una app instalada en su dispositivo.
 * Valida que los campos de asunto y mensaje estén completos antes de generar el correo.
 *
 * Dirección de destino: trasteapp@gmail.com
 *
 * @author Jorge Fresno
 */
public class AtencionClienteActivity extends AppCompatActivity {

    private EditText editAsunto, editMensaje;
    private Button botonEnviar;

    /**
     * Método que se ejecuta al crear la actividad.
     * Inicializa los elementos de interfaz y configura el botón para enviar correo.
     *
     * @param savedInstanceState Estado anterior de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atencion_cliente);

        editAsunto = findViewById(R.id.edit_asunto);
        editMensaje = findViewById(R.id.edit_mensaje);
        botonEnviar = findViewById(R.id.boton_enviar);

        // Listener del botón "Enviar"
        botonEnviar.setOnClickListener(v -> {
            String asunto = editAsunto.getText().toString().trim();
            String mensaje = editMensaje.getText().toString().trim();

            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Configura un intent de envío de correo
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"trasteapp@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, asunto);
            intent.putExtra(Intent.EXTRA_TEXT, mensaje);

            try {
                // Lanza selector de aplicaciones de correo
                startActivity(Intent.createChooser(intent, "Enviar correo..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No hay ninguna app de correo instalada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

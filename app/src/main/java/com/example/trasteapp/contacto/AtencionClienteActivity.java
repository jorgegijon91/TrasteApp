package com.example.trasteapp.contacto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;

public class AtencionClienteActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz
    private EditText editAsunto, editMensaje;
    private Button botonEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atencion_cliente); // Carga la interfaz de la actividad

        // Asocia los elementos del layout a variables de Java
        editAsunto = findViewById(R.id.edit_asunto);
        editMensaje = findViewById(R.id.edit_mensaje);
        botonEnviar = findViewById(R.id.boton_enviar);

        // Acciones al hacer clic en el botón "Enviar"
        botonEnviar.setOnClickListener(v -> {
            // Obtiene los textos introducidos por el usuario
            String asunto = editAsunto.getText().toString().trim();
            String mensaje = editMensaje.getText().toString().trim();

            // Validación de campos vacíos
            if (asunto.isEmpty() || mensaje.isEmpty()) {
                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea un intent para enviar un correo electrónico
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // Especifica que solo apps de correo deben manejarlo
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"trasteapp@gmail.com"}); // Destinatario
            intent.putExtra(Intent.EXTRA_SUBJECT, asunto); // Asunto del correo
            intent.putExtra(Intent.EXTRA_TEXT, mensaje); // Cuerpo del mensaje

            try {
                // Abre un selector de apps de correo para enviar el mensaje
                startActivity(Intent.createChooser(intent, "Enviar correo..."));
            } catch (android.content.ActivityNotFoundException ex) {
                // Si no hay apps de correo, se muestra un mensaje de error
                Toast.makeText(this, "No hay ninguna app de correo instalada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

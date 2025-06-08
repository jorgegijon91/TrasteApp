package com.example.trasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Actividad que permite al usuario actualizar su nombre y contraseña.
 * El email no se modifica. Si los cambios se aplican correctamente,
 * se redirige a la pantalla de inicio (HomeActivity).
 */
public class ActualizarPerfilActivity extends AppCompatActivity {

    private EditText editNombre, editPassword;
    private Button btnGuardar, btnCancelar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_perfil);

        // Asociar componentes del layout
        editNombre = findViewById(R.id.editNombre);
        editPassword = findViewById(R.id.editPassword);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Inicialización Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Botón Cancelar -> vuelve a HomeActivity
        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Botón Guardar -> actualiza nombre y contraseña
        btnGuardar.setOnClickListener(v -> actualizarPerfil());
    }

    /**
     * Actualiza nombre en Firestore y contraseña en FirebaseAuth.
     * Si ambas operaciones tienen éxito, muestra confirmación y vuelve a Home.
     * Si algo falla, se muestra mensaje de error y se permanece en la pantalla.
     */
    private void actualizarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();

        if (nuevoNombre.isEmpty() || nuevaPassword.isEmpty()) {
            Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null) {
            // Actualizar nombre en Firestore
            db.collection("usuarios").document(user.getUid())
                    .update("nombre", nuevoNombre)
                    .addOnSuccessListener(aVoid -> {
                        // Si nombre actualizado, intentamos actualizar contraseña
                        user.updatePassword(nuevaPassword)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(this, "Nombre y contraseña actualizados", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, HomeActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "No se han podido aplicar los cambios", Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "No se han podido aplicar los cambios", Toast.LENGTH_LONG).show();
                    });
        }
    }
}

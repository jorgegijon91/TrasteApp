package com.example.trasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Actividad encargada del registro de nuevos usuarios en la aplicación TrasteApp.
 * Permite introducir correo electrónico, contraseña y nombre de usuario.
 *
 * Se conecta con Firebase Authentication para crear cuentas
 * y con Firestore para almacenar datos adicionales del usuario.
 *
 * @author Jorge Fresno
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    private Button buttonRegister, buttonGoToLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Método que se ejecuta al iniciar la actividad.
     * Configura la interfaz y los listeners para los botones de registro e ir a login.
     *
     * @param savedInstanceState Estado guardado de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGoToLogin = findViewById(R.id.buttonGoToLogin);

        // Botón para registrar al usuario
        buttonRegister.setOnClickListener(v -> registerUser());

        // Botón para ir a la pantalla de inicio de sesión
        buttonGoToLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication.
     * Si el registro es exitoso, guarda los datos del usuario en Firestore
     * y redirige a la actividad principal de la aplicación.
     */
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Correo y contraseña requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("username", username);
                    userData.put("tipo", "gratuito");

                    db.collection("usuarios").document(uid)
                            .set(userData)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomeActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

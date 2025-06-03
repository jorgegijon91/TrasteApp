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

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToRegister;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Enlazar componentes de la interfaz
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister);

        // Botón de login
        buttonLogin.setOnClickListener(v -> loginUser());

        // Ir a pantalla de registro
        buttonGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validación de campos vacíos
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Introduce el correo y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login con Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        // Obtener UID del usuario autenticado
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Verificamos si ya existe documento en Firestore
                        FirebaseFirestore.getInstance().collection("usuarios")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.exists()) {
                                        // 🆕 Si no existe, lo creamos como usuario gratuito
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("email", email);
                                        userData.put("tipo", "gratuito");

                                        FirebaseFirestore.getInstance().collection("usuarios")
                                                .document(uid)
                                                .set(userData);
                                    }
                                });

                        // Pasamos a HomeActivity
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        // Error al iniciar sesión
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

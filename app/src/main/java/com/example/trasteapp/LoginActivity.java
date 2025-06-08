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
 * Actividad de inicio de sesión para usuarios registrados en TrasteApp.
 * Permite iniciar sesión mediante correo y contraseña,
 * y redirige a Home si la autenticación es exitosa.
 *
 * @author Jorge Fresno
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonGoToRegister;
    private FirebaseAuth auth;

    /**
     * Método principal que se ejecuta al crear la actividad.
     * Inicializa componentes visuales y configura los listeners para login y navegación a registro.
     *
     * @param savedInstanceState Estado anterior de la actividad si existía.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // Enlaza los elementos visuales
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonGoToRegister = findViewById(R.id.buttonGoToRegister);

        // Listener para botón de login
        buttonLogin.setOnClickListener(v -> loginUser());

        // Navegación a pantalla de registro
        buttonGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    /**
     * Realiza el proceso de autenticación con Firebase usando correo y contraseña.
     * Si el login tiene éxito, verifica si el usuario está en Firestore y lo registra si es necesario.
     */
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validación de campos obligatorios
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Introduce el correo y la contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Verifica si el documento del usuario existe en Firestore
                        FirebaseFirestore.getInstance().collection("usuarios")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.exists()) {
                                        // Crea usuario gratuito por defecto si no existe
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("email", email);
                                        userData.put("tipo", "gratuito");

                                        FirebaseFirestore.getInstance().collection("usuarios")
                                                .document(uid)
                                                .set(userData);
                                    }
                                });

                        // Redirige al usuario al Home
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        // Muestra error si falló la autenticación
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

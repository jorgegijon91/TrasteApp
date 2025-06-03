package com.example.trasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.home.HomeActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    // Launcher para el resultado del login con Google
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Inicializa Firebase
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        // Si el usuario ya está autenticado, redirige a la pantalla principal
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // Configuración de Google Sign-In con el ID de cliente
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // definido en strings.xml
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Resultado del intent de Google Sign-In usando Activity Result API
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) {
                                firebaseAuthWithGoogle(account);
                            }
                        } catch (ApiException e) {
                            Log.w("SignIn", "Fallo login Google", e);
                            Toast.makeText(this, "Error en el login de Google", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Inicio de sesión cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Listener botón de login con Google
        findViewById(R.id.googleSignInButton).setOnClickListener(v -> signInWithGoogle());

        // Botones login y registro clásicos
        Button loginBtn = findViewById(R.id.loginButton);
        Button registerBtn = findViewById(R.id.registerButton);

        loginBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        registerBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        // Política de privacidad
        TextView politica = findViewById(R.id.tvPoliticaPrivacidad);
        politica.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PoliticaPrivacidadActivity.class)));
    }

    // Inicia el intent de Google Sign-In
    private void signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    // Lógica para autenticar con Firebase usando Google y registrar usuario en Firestore si no existe
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "signInWithCredential:success");

                        // Verificamos si ya existe documento del usuario en Firestore
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        String email = firebaseAuth.getCurrentUser().getEmail();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.exists()) {
                                        // Si no existe, lo registramos como usuario gratuito
                                        Map<String, Object> datos = new HashMap<>();
                                        datos.put("email", email);
                                        datos.put("tipo", "gratuito");

                                        db.collection("usuarios").document(uid).set(datos);
                                    }
                                });

                        // Redirigimos a Home
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Log.w("SignIn", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "No se pudo autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

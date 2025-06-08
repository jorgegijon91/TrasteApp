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

/**
 * Actividad principal de la aplicación TrasteApp.
 * Permite iniciar sesión mediante Google, acceder al login/registro clásico,
 * y consultar la política de privacidad. También gestiona el inicio automático
 * si el usuario ya está autenticado.
 *
 * @author Jorge Fresno
 */
public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    // Launcher para el resultado del login con Google
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    /**
     * Método principal que se ejecuta al iniciar la actividad.
     * Configura botones, listeners y verifica si el usuario ya está logueado.
     *
     * @param savedInstanceState Estado anterior de la actividad si existía.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Inicializa Firebase
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        // Si el usuario ya está logueado, lo redirige directamente a HomeActivity
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        // Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Manejador del resultado del login de Google
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

        // Configura botones de login, registro y política de privacidad
        findViewById(R.id.googleSignInButton).setOnClickListener(v -> signInWithGoogle());

        Button loginBtn = findViewById(R.id.loginButton);
        Button registerBtn = findViewById(R.id.registerButton);

        loginBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        registerBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        TextView politica = findViewById(R.id.tvPoliticaPrivacidad);
        politica.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PoliticaPrivacidadActivity.class)));
    }

    /**
     * Inicia el flujo de autenticación con Google.
     */
    private void signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    /**
     * Autentica al usuario en Firebase usando su cuenta de Google.
     * Si es la primera vez que accede, crea su documento en Firestore.
     *
     * @param acct Cuenta de Google obtenida del flujo de autenticación.
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "signInWithCredential:success");

                        String uid = firebaseAuth.getCurrentUser().getUid();
                        String email = firebaseAuth.getCurrentUser().getEmail();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.exists()) {
                                        // Registra el nuevo usuario en Firestore
                                        Map<String, Object> datos = new HashMap<>();
                                        datos.put("email", email);
                                        datos.put("tipo", "gratuito");

                                        db.collection("usuarios").document(uid).set(datos);
                                    }
                                });

                        // Redirige al usuario a la pantalla principal
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Log.w("SignIn", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "No se pudo autenticar con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

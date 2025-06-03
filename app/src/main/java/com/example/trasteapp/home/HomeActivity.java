package com.example.trasteapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.MainActivity;
import com.example.trasteapp.R;
import com.example.trasteapp.contacto.AtencionClienteActivity;
import com.example.trasteapp.facturas.FacturaActivity;
import com.example.trasteapp.plan.CambiarPlanActivity;
import com.example.trasteapp.portes.AyudaPortesActivity;
import com.example.trasteapp.trasteros.BuscarTrasterosActivity;
import com.example.trasteapp.trasteros.ContratosActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private Button buttonBuscar, buttonFacturas, buttonContratos, buttonPortes, buttonAtencion, buttonCerrarSesion, buttonCambiarPlan;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private String tipoUsuario = "gratuito"; // Valor por defecto
    private TextView textTipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializa Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Configura Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);

        // Referencias a botones
        buttonBuscar = findViewById(R.id.buttonBuscar);
        buttonFacturas = findViewById(R.id.buttonFacturas);
        buttonContratos = findViewById(R.id.buttonContratos);
        buttonPortes = findViewById(R.id.buttonPortes);
        buttonAtencion = findViewById(R.id.buttonAtencion);
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion);
        buttonCambiarPlan = findViewById(R.id.buttonCambiarPlan);

        // Cargar el tipo de usuario desde Firestore
        cargarTipoUsuario();
        textTipoUsuario = findViewById(R.id.textTipoUsuario);
        // Botón para cerrar sesión
        buttonCerrarSesion.setOnClickListener(view -> cerrarSesion());

        // Botón para buscar trasteros
        buttonBuscar.setOnClickListener(v -> {
            startActivity(new Intent(this, BuscarTrasterosActivity.class));
        });

        // Botón para ver facturas
        buttonFacturas.setOnClickListener(v -> {
            if (!tipoUsuario.equals("premium")) {
                Toast.makeText(this, "Solo disponible para usuarios premium", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, FacturaActivity.class));
        });

        // Botón para ver contratos (disponible para todos)
        buttonContratos.setOnClickListener(v -> startActivity(new Intent(this, ContratosActivity.class)));

        // Botón para ayuda con portes (disponible para todos)
        // Botón para ver facturas
        buttonPortes.setOnClickListener(v -> {
            if (!tipoUsuario.equals("premium")) {
                Toast.makeText(this, "Solo disponible para usuarios premium", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, AyudaPortesActivity.class));
        });

        // Botón para atención al cliente
        buttonAtencion.setOnClickListener(v -> startActivity(new Intent(this, AtencionClienteActivity.class)));

        // Botón para cambiar el plan
        buttonCambiarPlan.setOnClickListener(v -> startActivity(new Intent(this, CambiarPlanActivity.class)));
    }

    // Se ejecuta cada vez que volvemos al Home, por ejemplo tras cambiar plan
    @Override
    protected void onResume() {
        super.onResume();
        cargarTipoUsuario(); // Recarga el tipo de usuario actualizado
    }

    // Método para cargar tipo de usuario desde Firestore
    private void cargarTipoUsuario() {
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tipoUsuario = doc.getString("tipo");
                        if (tipoUsuario == null) tipoUsuario = "gratuito";

                        // Muestra el tipo de usuario
                        textTipoUsuario.setText("Plan actual: " + tipoUsuario);

                        // (Opcional) Feedback visual
                        Toast.makeText(this, "Modo " + tipoUsuario + " activo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar tipo de usuario", Toast.LENGTH_SHORT).show());
    }

    // Cerrar sesión en Firebase y Google
    private void cerrarSesion() {
        firebaseAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}

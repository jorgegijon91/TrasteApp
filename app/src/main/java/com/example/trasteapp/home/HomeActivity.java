package com.example.trasteapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.ActualizarPerfilActivity;
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

/**
 * Actividad principal tras iniciar sesión. Muestra opciones de navegación
 * a diferentes funcionalidades de la app según el tipo de plan del usuario
 * (gratuito o premium).
 *
 * @author Jorge Fresno
 */
public class HomeActivity extends AppCompatActivity {

    private Button buttonBuscar, buttonFacturas, buttonContratos, buttonPortes,
            buttonAtencion, buttonCerrarSesion, buttonCambiarPlan, buttonActualizarPerfil;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private String tipoUsuario = "gratuito"; // Por defecto
    private TextView textTipoUsuario;

    /**
     * Inicializa la actividad y sus botones, carga el tipo de usuario y
     * configura los listeners para cada funcionalidad.
     *
     * @param savedInstanceState Estado anterior de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);

        // Inicializa botones
        buttonBuscar = findViewById(R.id.buttonBuscar);
        buttonFacturas = findViewById(R.id.buttonFacturas);
        buttonContratos = findViewById(R.id.buttonContratos);
        buttonPortes = findViewById(R.id.buttonPortes);
        buttonAtencion = findViewById(R.id.buttonAtencion);
        buttonCerrarSesion = findViewById(R.id.buttonCerrarSesion);
        buttonCambiarPlan = findViewById(R.id.buttonCambiarPlan);
        buttonActualizarPerfil = findViewById(R.id.buttonActualizarPerfil);
        textTipoUsuario = findViewById(R.id.textTipoUsuario);

        // Carga tipo de usuario
        cargarTipoUsuario();

        // Acciones de botones
        buttonCerrarSesion.setOnClickListener(view -> cerrarSesion());

        buttonBuscar.setOnClickListener(v -> startActivity(new Intent(this, BuscarTrasterosActivity.class)));

        buttonFacturas.setOnClickListener(v -> {
            if (!tipoUsuario.equals("premium")) {
                Toast.makeText(this, "Solo disponible para usuarios premium", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, FacturaActivity.class));
            }
        });

        buttonContratos.setOnClickListener(v -> startActivity(new Intent(this, ContratosActivity.class)));

        buttonPortes.setOnClickListener(v -> {
            if (!tipoUsuario.equals("premium")) {
                Toast.makeText(this, "Solo disponible para usuarios premium", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, AyudaPortesActivity.class));
            }
        });

        buttonAtencion.setOnClickListener(v -> startActivity(new Intent(this, AtencionClienteActivity.class)));

        buttonCambiarPlan.setOnClickListener(v -> startActivity(new Intent(this, CambiarPlanActivity.class)));

        buttonActualizarPerfil.setOnClickListener(v ->
                startActivity(new Intent(this, ActualizarPerfilActivity.class))
        );
    }

    /**
     * Se ejecuta cuando la actividad vuelve al primer plano.
     * Vuelve a cargar el tipo de usuario por si ha cambiado.
     */
    @Override
    protected void onResume() {
        super.onResume();
        cargarTipoUsuario();
    }

    /**
     * Consulta el tipo de plan del usuario actual en Firestore.
     * Actualiza la UI con esta información.
     */
    private void cargarTipoUsuario() {
        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        tipoUsuario = doc.getString("tipo");
                        if (tipoUsuario == null) tipoUsuario = "gratuito";
                        textTipoUsuario.setText("Plan actual: " + tipoUsuario);
                        Toast.makeText(this, "Modo " + tipoUsuario + " activo", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar tipo de usuario", Toast.LENGTH_SHORT).show());
    }

    /**
     * Cierra la sesión del usuario tanto en Firebase como en Google,
     * y lo redirige a la pantalla principal de autenticación.
     */
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

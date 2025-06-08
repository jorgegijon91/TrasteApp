package com.example.trasteapp.plan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Esta actividad permite al usuario cambiar entre dos tipos de plan:
 * - Gratuito (por defecto)
 * - Premium (con más funciones)
 *
 * Cuando se selecciona un plan y se pulsa el botón "Guardar",
 * el nuevo tipo se guarda en la base de datos Firestore.
 *
 * Esta información se usará luego para permitir o restringir funciones dentro de la app.
 *
 * @author Jorge Fresno
 */
public class CambiarPlanActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz
    private RadioGroup radioGroup;
    private RadioButton radioGratuito, radioPremium;
    private Button btnGuardar;

    // Referencia a Firestore
    private FirebaseFirestore db;

    /**
     * Método que se ejecuta automáticamente al abrir la actividad.
     * Aquí se configura la vista y los comportamientos de los elementos.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cambiar_plan2);

        // Asociamos las variables con los elementos visuales mediante sus IDs
        radioGroup = findViewById(R.id.radioGroupPlanes);
        radioGratuito = findViewById(R.id.radioGratuito);
        radioPremium = findViewById(R.id.radioPremium);
        btnGuardar = findViewById(R.id.botonGuardarPlan);

        // Inicializamos la conexión con Firestore
        db = FirebaseFirestore.getInstance();

        // Configuramos qué ocurre cuando se pulsa el botón de guardar
        btnGuardar.setOnClickListener(v -> {
            // Obtenemos el ID del botón seleccionado dentro del grupo
            int selectedId = radioGroup.getCheckedRadioButtonId();

            // Si no hay ninguna opción seleccionada, mostramos aviso y salimos
            if (selectedId == -1) {
                Toast.makeText(this, "Selecciona un plan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determinamos el tipo de plan según el botón elegido
            String tipo = (selectedId == R.id.radioPremium) ? "premium" : "gratuito";

            // Obtenemos el ID del usuario actualmente autenticado en Firebase
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Actualizamos el campo "tipo" del documento del usuario en la colección "usuarios"
            db.collection("usuarios").document(uid)
                    .update("tipo", tipo)
                    .addOnSuccessListener(unused -> {
                        // Si se actualiza correctamente, mostramos mensaje y cerramos la actividad
                        Toast.makeText(this, "Plan actualizado a " + tipo, Toast.LENGTH_SHORT).show();
                        finish(); // Cerramos esta pantalla y volvemos atrás
                    })
                    .addOnFailureListener(e ->
                            // Si ocurre un error, mostramos un mensaje al usuario
                            Toast.makeText(this, "Error al actualizar plan", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}

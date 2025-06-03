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

public class CambiarPlanActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioGratuito, radioPremium;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_plan);

        // Referencias UI
        radioGroup = findViewById(R.id.radioGroupPlanes);
        radioGratuito = findViewById(R.id.radioGratuito);
        radioPremium = findViewById(R.id.radioPremium);
        btnGuardar = findViewById(R.id.botonGuardarPlan);

        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Maneja clic en el botón
        btnGuardar.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Selecciona un plan", Toast.LENGTH_SHORT).show();
                return;
            }

            String tipo = (selectedId == R.id.radioPremium) ? "premium" : "gratuito";

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("usuarios").document(uid)
                    .update("tipo", tipo)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Plan actualizado a " + tipo, Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al actualizar plan", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}

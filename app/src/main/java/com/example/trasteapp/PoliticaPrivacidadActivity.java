package com.example.trasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PoliticaPrivacidadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidad);

        TextView texto = findViewById(R.id.tvTextoPolitica);
        Button btnCerrar = findViewById(R.id.btnCerrarPolitica);

        texto.setText(getTextoPolitica());

        // Botón cerrar para volver a la pantalla principal
        btnCerrar.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    // Función para obtener el texto de la política de privacidad
    private String getTextoPolitica() {
        return "📄 Política de Privacidad de TrasteApp\n\n" +
                "Última actualización: 23 de mayo de 2025\n\n" +
                "🔹 ¿Quiénes somos?\n" +
                "TrasteApp es una aplicación desarrollada por TrasteApp S.L., con CIF B12345678, con domicilio en Avenida de Porugal, Gijón, España. Contacto: trasteapp@gmail.com\n\n" +
                "🔹 ¿Qué datos recopilamos?\n" +
                "- Nombre y correo electrónico (autenticación)\n" +
                "- Reservas de trasteros (ubicación, precio, fechas)\n" +
                "- Firma electrónica del contrato\n" +
                "- Historial de pagos\n\n" +
                "🔹 ¿Para qué usamos los datos?\n" +
                "- Gestión de contratos y reservas\n" +
                "- Emisión de facturas y pagos\n" +
                "- Soporte técnico\n\n" +
                "🔹 ¿Dónde se almacenan?\n" +
                "En servidores seguros de Firebase (Google Cloud), cumpliendo RGPD\n\n" +
                "🔹 ¿Con quién compartimos?\n" +
                "Con nadie. Tus datos solo los gestiona TrasteApp S.L.\n\n" +
                "🔹 ¿Cuánto tiempo se conservan?\n" +
                "Mientras tengas una cuenta activa. Puede pedir su eliminación escribiendo a trasteapp@gmail.com\n\n" +
                "🔹 Tus derechos:\n" +
                "- Acceder, corregir o eliminar tus datos\n" +
                "- Presentar una reclamación ante la AEPD\n\n" +
                "📩 Contacto legal: trasteapp@gmail.com";
    }
}

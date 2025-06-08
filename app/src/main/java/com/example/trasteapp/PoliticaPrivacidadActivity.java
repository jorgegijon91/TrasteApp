package com.example.trasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que muestra la política de privacidad de la aplicación TrasteApp.
 * Permite al usuario leer la información legal sobre el uso y protección de sus datos
 * y regresar a la pantalla principal.
 *
 * @author Jorge Fresno
 */
public class PoliticaPrivacidadActivity extends AppCompatActivity {

    /**
     * Método que se ejecuta al crear la actividad.
     * Establece el layout, asigna el texto de la política de privacidad al {@link TextView},
     * y configura el botón para cerrar la vista y volver a la pantalla principal.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad.
     */
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

    /**
     * Retorna el contenido completo de la política de privacidad.
     * Este texto incluye información sobre el responsable, datos recogidos,
     * finalidades, almacenamiento y derechos del usuario.
     *
     * @return Texto legal de la política de privacidad.
     */
    private String getTextoPolitica() {
        return " Política de Privacidad de TrasteApp\n\n" +
                "Última actualización: 23 de mayo de 2025\n\n" +
                " ¿Quiénes somos?\n" +
                "TrasteApp es una aplicación desarrollada por TrasteApp S.L., con CIF B12345678, con domicilio en Avenida de Portugal, Gijón, España. Contacto: trasteapp@gmail.com\n\n" +
                " ¿Qué datos recopilamos?\n" +
                "- Nombre y correo electrónico (autenticación)\n" +
                "- Reservas de trasteros (ubicación, precio, fechas)\n" +
                "- Firma electrónica del contrato\n" +
                "- Historial de pagos\n\n" +
                " ¿Para qué usamos los datos?\n" +
                "- Gestión de contratos y reservas\n" +
                "- Emisión de facturas y pagos\n" +
                "- Soporte técnico\n\n" +
                " ¿Dónde se almacenan?\n" +
                "En servidores seguros de Firebase (Google Cloud), cumpliendo RGPD\n\n" +
                " ¿Con quién compartimos?\n" +
                "Con nadie. Tus datos solo los gestiona TrasteApp S.L.\n\n" +
                " ¿Cuánto tiempo se conservan?\n" +
                "Mientras tengas una cuenta activa. Puede pedir su eliminación escribiendo a trasteapp@gmail.com\n\n" +
                " Tus derechos:\n" +
                "- Acceder, corregir o eliminar tus datos\n" +
                "- Presentar una reclamación ante la AEPD\n\n" +
                " Contacto legal: trasteapp@gmail.com";
    }
}

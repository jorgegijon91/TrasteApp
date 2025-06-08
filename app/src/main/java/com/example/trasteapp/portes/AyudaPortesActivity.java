package com.example.trasteapp.portes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.trasteapp.R;

import java.util.*;

/**
 * Actividad que permite a los usuarios buscar empresas de transporte según su ciudad.
 * Muestra una lista de empresas con enlaces a sus sitios web. Si el campo está vacío,
 * muestra todas las empresas disponibles.
 *
 * @author Jorge Fresno
 */
public class AyudaPortesActivity extends AppCompatActivity {

    private EditText ubicacionInput;
    private LinearLayout resultadoLayout;
    private Button buscarBtn;

    /**
     * Mapa que almacena listas de empresas de transporte por ciudad.
     * La clave es el nombre de la ciudad en minúsculas.
     */
    private final Map<String, List<Empresa>> empresasPorCiudad = new HashMap<>();

    /**
     * Clase auxiliar que representa una empresa de transporte.
     */
    static class Empresa {
        String nombre;
        String url;

        Empresa(String nombre, String url) {
            this.nombre = nombre;
            this.url = url;
        }
    }

    /**
     * Método principal que se ejecuta al crear la actividad.
     * Carga la interfaz, inicializa datos y configura el botón de búsqueda.
     *
     * @param savedInstanceState Estado anterior de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda_portes);

        ubicacionInput = findViewById(R.id.input_ubicacion);
        buscarBtn = findViewById(R.id.boton_buscar);
        resultadoLayout = findViewById(R.id.layout_resultados);

        cargarEmpresas();

        buscarBtn.setOnClickListener(v -> {
            String ciudad = ubicacionInput.getText().toString().trim().toLowerCase();
            if (ciudad.isEmpty()) ciudad = "todas";
            mostrarEmpresas(ciudad);
        });
    }

    /**
     * Carga en memoria una lista predefinida de empresas de transporte
     * categorizadas por ciudad. También crea una categoría "todas" que
     * agrupa todas las empresas disponibles.
     */
    private void cargarEmpresas() {
        empresasPorCiudad.put("gijón", Arrays.asList(
                new Empresa("Mudanzas Mario", "https://transportesymudanzasmario.com/"),
                new Empresa("Mudanzas Jose-Vicente", "https://www.jose-vicente.es/")
        ));
        empresasPorCiudad.put("oviedo", Arrays.asList(
                new Empresa("Mudanzas Trasnporteastur", "https://transporteastur.es/"),
                new Empresa("Mudanzas Abraham", "https://mudanzasabraham.com/")
        ));
        empresasPorCiudad.put("madrid", Arrays.asList(
                new Empresa("Mudanzas Madrid", "https://www.madrimudanzas.com/"),
                new Empresa("Mudanzas segoviana", "https://www.amsegoviana.com/")
        ));
        empresasPorCiudad.put("barcelona", Arrays.asList(
                new Empresa("Mudanzas González", "https://www.mudanzasgonzalez.com/"),
                new Empresa("Mudanzas Control", "https://mudanzascontrol.com")
        ));
        empresasPorCiudad.put("valencia", Arrays.asList(
                new Empresa("Mudanzas Jg", "https://mudanzasjg.es/"),
                new Empresa("Mudanzas Levante", "https://mudanzaslevante.com")
        ));
        empresasPorCiudad.put("andalucia", Arrays.asList(
                new Empresa("Mudanzas Andalucía", "https://mudanzasandalucia.es/"),
                new Empresa("Mudanzas Gil Stauffer", "https://www.gil-stauffer.com/mudanzas/andalucia/")
        ));

        // Crea lista global de todas las empresas
        List<Empresa> todas = new ArrayList<>();
        for (List<Empresa> lista : empresasPorCiudad.values()) {
            todas.addAll(lista);
        }
        empresasPorCiudad.put("todas", todas);
    }

    /**
     * Muestra las empresas de transporte correspondientes a la ciudad especificada.
     * Si no hay coincidencias, se muestra un mensaje indicativo.
     *
     * @param ciudad Nombre de la ciudad introducida por el usuario.
     */
    private void mostrarEmpresas(String ciudad) {
        resultadoLayout.removeAllViews();

        List<Empresa> lista = empresasPorCiudad.get(ciudad);

        if (lista == null || lista.isEmpty()) {
            TextView noResult = new TextView(this);
            noResult.setText("No hay empresas disponibles en esta ubicación.");
            resultadoLayout.addView(noResult);
            return;
        }

        for (Empresa empresa : lista) {
            TextView nombre = new TextView(this);
            nombre.setText("• " + empresa.nombre);
            nombre.setTextSize(18f);

            TextView link = new TextView(this);
            link.setText("Visitar web");
            link.setTextColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            link.setPadding(0, 0, 0, 24);
            link.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(empresa.url));
                startActivity(intent);
            });

            resultadoLayout.addView(nombre);
            resultadoLayout.addView(link);
        }
    }
}

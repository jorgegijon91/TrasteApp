package com.example.trasteapp.portes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;

import java.util.*;

public class AyudaPortesActivity extends AppCompatActivity {

    // Campos de entrada y botones de la interfaz
    private EditText ubicacionInput;         // Campo donde el usuario escribe su ciudad
    private LinearLayout resultadoLayout;    // Contenedor donde se mostrarán los resultados
    private Button buscarBtn;                // Botón que inicia la búsqueda

    // Estructura para almacenar empresas de transporte según la ciudad
    private final Map<String, List<Empresa>> empresasPorCiudad = new HashMap<>();

    // Clase interna para representar una empresa
    static class Empresa {
        String nombre;
        String url;

        Empresa(String nombre, String url) {
            this.nombre = nombre;
            this.url = url;
        }
    }

    // Método principal al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda_portes); // Cargar la interfaz XML

        // Enlazar elementos visuales
        ubicacionInput = findViewById(R.id.input_ubicacion);
        buscarBtn = findViewById(R.id.boton_buscar);
        resultadoLayout = findViewById(R.id.layout_resultados);

        cargarEmpresas(); // Llenar el mapa con empresas por ciudad

        // Acción del botón buscar
        buscarBtn.setOnClickListener(v -> {
            String ciudad = ubicacionInput.getText().toString().trim().toLowerCase();
            if (ciudad.isEmpty()) ciudad = "todas"; // Si no escribe ciudad, se muestran todas
            mostrarEmpresas(ciudad);
        });
    }

    // Método que carga todas las empresas en el mapa
    private void cargarEmpresas() {
        // Agrega empresas por ciudad (clave = ciudad, valor = lista de empresas)
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

        // Agrupa todas las empresas para búsquedas generales
        List<Empresa> todas = new ArrayList<>();
        for (List<Empresa> lista : empresasPorCiudad.values()) {
            todas.addAll(lista);
        }
        empresasPorCiudad.put("todas", todas);
    }

    // Muestra empresas en pantalla según la ciudad ingresada
    private void mostrarEmpresas(String ciudad) {
        resultadoLayout.removeAllViews(); // Limpia resultados anteriores

        List<Empresa> lista = empresasPorCiudad.get(ciudad); // Obtiene empresas de la ciudad

        if (lista == null || lista.isEmpty()) {
            // Si no hay resultados, se muestra un mensaje
            TextView noResult = new TextView(this);
            noResult.setText("No hay empresas disponibles en esta ubicación.");
            resultadoLayout.addView(noResult);
            return;
        }

        // Por cada empresa, se crea un TextView con nombre y enlace
        for (Empresa empresa : lista) {
            TextView nombre = new TextView(this);
            nombre.setText("• " + empresa.nombre);
            nombre.setTextSize(18f); // Tamaño del texto del nombre

            TextView link = new TextView(this);
            link.setText("Visitar web");
            link.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            link.setPadding(0, 0, 0, 24);
            link.setOnClickListener(v -> {
                // Al pulsar, abre el navegador con la URL de la empresa
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(empresa.url));
                startActivity(intent);
            });

            // Añade ambos elementos al layout
            resultadoLayout.addView(nombre);
            resultadoLayout.addView(link);
        }
    }
}

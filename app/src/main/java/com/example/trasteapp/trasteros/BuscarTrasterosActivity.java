package com.example.trasteapp.trasteros;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trasteapp.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

/**
 * Actividad que muestra un mapa general de España con marcadores
 * para distintas comunidades autónomas. Al hacer clic en un marcador,
 * se abre la vista detallada de trasteros disponibles en esa comunidad.
 *
 * @author Jorge Fresno
 */
public class BuscarTrasterosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * Método llamado al crear la actividad. Inicializa la interfaz
     * y configura el fragmento de mapa.
     *
     * @param savedInstanceState Estado guardado de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_trasteros);

        // Obtiene el fragmento de mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Método llamado cuando el mapa está listo.
     * Centra la vista sobre España y añade marcadores de comunidades autónomas.
     *
     * @param googleMap Mapa de Google ya inicializado.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Centra el mapa en España
        LatLng espana = new LatLng(40.4168, -3.7038);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(espana, 5.5f));

        // Añade marcadores de distintas comunidades
        addComunidad("Asturias", 43.3619, -5.8494, "Trasteros desde 45€/mes");
        addComunidad("Comunidad de Madrid", 40.4168, -3.7038, "Trasteros desde 68€/mes");
        addComunidad("Cataluña", 41.3851, 2.1734, "Trasteros desde 80€/mes");
        addComunidad("Comunidad Valenciana", 39.4699, -0.3763, "Trasteros desde 60€/mes");
        addComunidad("Andalucía", 37.3886, -5.9823, "Trasteros desde 75€/mes");

        // Evento al hacer clic en la ventana de un marcador
        mMap.setOnInfoWindowClickListener(marker -> {
            String comunidad = marker.getTitle();
            LatLng posicion = marker.getPosition();

            // Abre la actividad que muestra los trasteros disponibles en esa comunidad
            Intent intent = new Intent(BuscarTrasterosActivity.this, MapaTrasterosActivity.class);
            intent.putExtra("comunidad", comunidad);
            intent.putExtra("latitud", posicion.latitude);
            intent.putExtra("longitud", posicion.longitude);
            startActivity(intent);
        });
    }

    /**
     * Añade un marcador al mapa para representar una comunidad autónoma.
     *
     * @param nombre  Nombre de la comunidad.
     * @param lat     Latitud geográfica.
     * @param lon     Longitud geográfica.
     * @param snippet Texto adicional que se muestra debajo del título del marcador.
     */
    private void addComunidad(String nombre, double lat, double lon, String snippet) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(nombre)
                .snippet(snippet));
    }
}

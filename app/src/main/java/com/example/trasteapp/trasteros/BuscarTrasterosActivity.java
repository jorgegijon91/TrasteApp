package com.example.trasteapp.trasteros;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trasteapp.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class BuscarTrasterosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_trasteros);

        // Obtener el mapa de Google
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Centrar en España
        LatLng espana = new LatLng(40.4168, -3.7038);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(espana, 5.5f));

        // Marcadores de comunidades
        addComunidad("Asturias", 43.3619, -5.8494, "Trasteros desde 45€/mes");
        addComunidad("Comunidad de Madrid", 40.4168, -3.7038, "Trasteros desde 68€/mes");
        addComunidad("Cataluña", 41.3851, 2.1734, "Trasteros desde 100€/mes");
        addComunidad("Comunidad Valenciana", 39.4699, -0.3763, "Trasteros desde 90€/mes");
        addComunidad("Andalucía", 37.3886, -5.9823, "Trasteros desde 80€/mes");

        // Evento para cuando se haga clic en un marcador
        mMap.setOnInfoWindowClickListener(marker -> {
            String comunidad = marker.getTitle();
            LatLng posicion = marker.getPosition();

            Intent intent = new Intent(BuscarTrasterosActivity.this, MapaTrasterosActivity.class);
            intent.putExtra("comunidad", comunidad);
            intent.putExtra("latitud", posicion.latitude);
            intent.putExtra("longitud", posicion.longitude);
            startActivity(intent);
        });
    }

    private void addComunidad(String nombre, double lat, double lon, String snippet) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(nombre)
                .snippet(snippet));
    }
}

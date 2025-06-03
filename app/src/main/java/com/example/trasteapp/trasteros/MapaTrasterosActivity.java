package com.example.trasteapp.trasteros;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.trasteapp.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta actividad muestra un mapa con los trasteros disponibles en una comunidad autónoma concreta.
 */
public class MapaTrasterosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * Clase interna para guardar los datos de un trastero.
     */
    public static class TrasteroInfo {
        String idDocumento;
        String ciudad;
        String descripcion;
        String precio;
        List<String> imagenes;

        public TrasteroInfo(String idDocumento, String ciudad, String descripcion, String precio, List<String> imagenes) {
            this.idDocumento = idDocumento;
            this.ciudad = ciudad;
            this.descripcion = descripcion;
            this.precio = precio;
            this.imagenes = imagenes;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_trasteros);

        // Obtiene el fragmento del mapa del layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Cuando el mapa esté listo, se llama a onMapReady()
        }
    }

    /**
     * Se ejecuta cuando el mapa ya está cargado y listo para usarse.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Obtiene los datos que se pasaron desde la actividad anterior
        String comunidadSeleccionada = getIntent().getStringExtra("comunidad");
        double latitud = getIntent().getDoubleExtra("latitud", 40.4168);
        double longitud = getIntent().getDoubleExtra("longitud", -3.7038);

        // Centra el mapa en la comunidad seleccionada
        LatLng centro = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 10f));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta trasteros de esa comunidad que aún no están alquilados
        db.collection("trasteros")
                .whereEqualTo("alquilado", false)
                .whereEqualTo("comunidad", comunidadSeleccionada)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Verifica si está reservado y si la reserva ha expirado
                        Boolean reservado = doc.getBoolean("reservado") != null && doc.getBoolean("reservado");
                        Timestamp fechaReserva = doc.getTimestamp("fecha_reserva");

                        boolean expirado = false;
                        if (reservado && fechaReserva != null) {
                            long diferenciaHoras = (Timestamp.now().getSeconds() - fechaReserva.getSeconds()) / 3600;
                            expirado = diferenciaHoras >= 24;
                        }

                        if (reservado && !expirado) continue;

                        // Extrae los datos del trastero
                        String ciudad = doc.getString("ciudad");
                        String descripcion = doc.getString("descripcion");
                        String precio = doc.getString("precio");
                        List<String> imagenes = (List<String>) doc.get("imagenes");
                        double lat = doc.getDouble("latitud");
                        double lon = doc.getDouble("longitud");

                        // Crea un marcador en el mapa
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(ciudad)
                                .snippet(precio));

                        // Asocia los datos al marcador
                        if (marker != null) {
                            marker.setTag(new TrasteroInfo(doc.getId(), ciudad, descripcion, precio, imagenes));
                        }
                    }
                });

        // Cuando el usuario hace clic en un marcador, muestra los detalles en una hoja inferior
        mMap.setOnInfoWindowClickListener(marker -> {
            TrasteroInfo info = (TrasteroInfo) marker.getTag();
            if (info != null) {
                mostrarBottomSheet(info);
            }
        });
    }

    /**
     * Muestra los detalles del trastero en un BottomSheetDialog.
     */
    private void mostrarBottomSheet(TrasteroInfo info) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_trastero, null);

        // Elementos de la vista de detalle
        TextView ciudad = view.findViewById(R.id.bs_ciudad);
        TextView descripcion = view.findViewById(R.id.bs_descripcion);
        TextView precio = view.findViewById(R.id.bs_precio);
        ImageView imagen = view.findViewById(R.id.bs_imagen);

        ciudad.setText(info.ciudad);
        descripcion.setText(info.descripcion);
        precio.setText(info.precio);

        // Cargar imagen usando Glide
        if (info.imagenes != null && !info.imagenes.isEmpty()) {
            Glide.with(this).load(info.imagenes.get(0)).into(imagen);
        }

        // Si se pulsa la imagen, va a la pantalla de detalle
        imagen.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrasteroDetalleActivity.class);
            intent.putExtra("ciudad", info.ciudad);
            intent.putExtra("descripcion", info.descripcion);
            intent.putExtra("precio", info.precio);
            intent.putStringArrayListExtra("imagenes", new ArrayList<>(info.imagenes));
            intent.putExtra("idDocumento", info.idDocumento);
            startActivity(intent);
            dialog.dismiss(); // Cierra el BottomSheet
        });

        dialog.setContentView(view);
        dialog.show();
    }
}

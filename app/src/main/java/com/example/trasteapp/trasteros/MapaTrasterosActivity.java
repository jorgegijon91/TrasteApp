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
 * Actividad que muestra un mapa con los trasteros disponibles
 * en una comunidad autónoma concreta, utilizando Google Maps.
 * Cada trastero se representa mediante un marcador.
 * Al hacer clic en un marcador, se muestra información detallada
 * y se permite acceder a su pantalla de reserva.
 *
 * @author Jorge Fresno
 */
public class MapaTrasterosActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    /**
     * Clase auxiliar que encapsula los datos de un trastero
     * para ser asociados a los marcadores del mapa.
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

    /**
     * Inicializa la actividad y carga el fragmento de Google Maps.
     *
     * @param savedInstanceState Estado previo de la actividad, si lo hay.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_trasteros);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Espera a que el mapa esté listo
        }
    }

    /**
     * Se llama cuando el mapa ha sido cargado correctamente.
     * Centra la vista en la comunidad seleccionada y muestra los trasteros disponibles.
     *
     * @param googleMap Instancia del mapa listo para ser usado.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Recupera datos de la comunidad desde el intent
        String comunidadSeleccionada = getIntent().getStringExtra("comunidad");
        double latitud = getIntent().getDoubleExtra("latitud", 40.4168);
        double longitud = getIntent().getDoubleExtra("longitud", -3.7038);

        // Centra el mapa en la comunidad
        LatLng centro = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centro, 10f));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consulta trasteros disponibles en la comunidad seleccionada
        db.collection("trasteros")
                .whereEqualTo("alquilado", false)
                .whereEqualTo("comunidad", comunidadSeleccionada)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Comprobación de reserva vencida (24h)
                        Boolean reservado = doc.getBoolean("reservado") != null && doc.getBoolean("reservado");
                        Timestamp fechaReserva = doc.getTimestamp("fecha_reserva");
                        boolean expirado = false;

                        if (reservado && fechaReserva != null) {
                            long diferenciaHoras = (Timestamp.now().getSeconds() - fechaReserva.getSeconds()) / 3600;
                            expirado = diferenciaHoras >= 24;
                        }

                        if (reservado && !expirado) continue;

                        // Datos del trastero
                        String ciudad = doc.getString("ciudad");
                        String descripcion = doc.getString("descripcion");
                        String precio = doc.getString("precio");
                        List<String> imagenes = (List<String>) doc.get("imagenes");
                        double lat = doc.getDouble("latitud");
                        double lon = doc.getDouble("longitud");

                        // Crea el marcador en el mapa
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lon))
                                .title(ciudad)
                                .snippet(precio));

                        // Asocia la información al marcador
                        if (marker != null) {
                            marker.setTag(new TrasteroInfo(doc.getId(), ciudad, descripcion, precio, imagenes));
                        }
                    }
                });

        // Configura el clic en el marcador para mostrar información detallada
        mMap.setOnInfoWindowClickListener(marker -> {
            TrasteroInfo info = (TrasteroInfo) marker.getTag();
            if (info != null) {
                mostrarBottomSheet(info);
            }
        });
    }

    /**
     * Muestra una hoja inferior (BottomSheetDialog) con los detalles del trastero seleccionado.
     * Permite acceder a la actividad de detalle si se pulsa sobre la imagen.
     *
     * @param info Información del trastero asociada al marcador.
     */
    private void mostrarBottomSheet(TrasteroInfo info) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_trastero, null);

        TextView ciudad = view.findViewById(R.id.bs_ciudad);
        TextView descripcion = view.findViewById(R.id.bs_descripcion);
        TextView precio = view.findViewById(R.id.bs_precio);
        ImageView imagen = view.findViewById(R.id.bs_imagen);

        ciudad.setText(info.ciudad);
        descripcion.setText(info.descripcion);
        precio.setText(info.precio);

        // Carga la imagen del trastero
        if (info.imagenes != null && !info.imagenes.isEmpty()) {
            Glide.with(this).load(info.imagenes.get(0)).into(imagen);
        }

        // Abre pantalla de detalle al pulsar la imagen
        imagen.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrasteroDetalleActivity.class);
            intent.putExtra("ciudad", info.ciudad);
            intent.putExtra("descripcion", info.descripcion);
            intent.putExtra("precio", info.precio);
            intent.putStringArrayListExtra("imagenes", new ArrayList<>(info.imagenes));
            intent.putExtra("idDocumento", info.idDocumento);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}

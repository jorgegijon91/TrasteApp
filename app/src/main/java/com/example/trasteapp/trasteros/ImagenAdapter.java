package com.example.trasteapp.trasteros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Librería para cargar imágenes desde Internet
import com.example.trasteapp.R;

import java.util.List;

/**
 * Adaptador para mostrar una lista de imágenes en un RecyclerView.
 */
public class ImagenAdapter extends RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder> {

    private final Context context; // Contexto de la aplicación, necesario para Glide y LayoutInflater
    private final List<String> imagenes; // Lista de URLs o rutas de las imágenes a mostrar

    // Constructor que recibe el contexto y la lista de imágenes
    public ImagenAdapter(Context context, List<String> imagenes) {
        this.context = context;
        this.imagenes = imagenes;
    }

    /**
     * Este método se llama cuando se necesita crear una nueva vista (item) en el RecyclerView.
     * Aquí se infla el layout de un item individual.
     */
    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño del item individual (item_imagen.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagen, parent, false);
        return new ImagenViewHolder(view); // Se crea un ViewHolder con esa vista
    }

    /**
     * Este método se llama para mostrar los datos en una vista determinada.
     * Aquí se carga la imagen en el ImageView usando Glide.
     */
    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        // Usa Glide para cargar la imagen de la lista en el ImageView del ViewHolder
        Glide.with(context).load(imagenes.get(position)).into(holder.imagenView);
    }

    /**
     * Devuelve cuántas imágenes hay en total.
     * Esto le indica al RecyclerView cuántas veces llamar a onCreateViewHolder y onBindViewHolder.
     */
    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    /**
     * ViewHolder personalizado que contiene la vista para cada imagen.
     * Se usa para acceder fácilmente al ImageView de cada item.
     */
    public static class ImagenViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenView;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            // Encuentra el ImageView dentro del layout del item
            imagenView = itemView.findViewById(R.id.imagen_item);
        }
    }
}

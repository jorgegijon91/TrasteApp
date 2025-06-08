package com.example.trasteapp.trasteros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.trasteapp.R;

import java.util.List;

/**
 * Adaptador para mostrar una galería de imágenes dentro de un RecyclerView.
 * Utiliza Glide para cargar las imágenes desde URLs o rutas locales.
 * Cada imagen se muestra en un {@link ImageView} definido en item_imagen.xml.
 *
 * @author Jorge Fresno
 */
public class ImagenAdapter extends RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder> {

    private final Context context;
    private final List<String> imagenes;

    /**
     * Constructor del adaptador.
     *
     * @param context  Contexto necesario para inflar vistas y cargar imágenes.
     * @param imagenes Lista de rutas o URLs de imágenes a mostrar.
     */
    public ImagenAdapter(Context context, List<String> imagenes) {
        this.context = context;
        this.imagenes = imagenes;
    }

    /**
     * Crea una nueva vista (item) del RecyclerView.
     * Se llama cuando no hay vistas recicladas disponibles y hay que inflar una nueva.
     *
     * @param parent   El ViewGroup padre al que se añadirá la nueva vista.
     * @param viewType Tipo de vista (no usado en este caso).
     * @return Una nueva instancia de {@link ImagenViewHolder}.
     */
    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagen, parent, false);
        return new ImagenViewHolder(view);
    }

    /**
     * Asocia los datos (URL de imagen) con el {@link ImagenViewHolder}.
     *
     * @param holder   ViewHolder que debe ser actualizado.
     * @param position Posición de la imagen en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        Glide.with(context).load(imagenes.get(position)).into(holder.imagenView);
    }

    /**
     * Indica cuántos elementos hay en el RecyclerView.
     *
     * @return Número total de imágenes a mostrar.
     */
    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    /**
     * Clase ViewHolder que representa cada item de imagen en el RecyclerView.
     */
    public static class ImagenViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenView;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del item individual.
         */
        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenView = itemView.findViewById(R.id.imagen_item);
        }
    }
}

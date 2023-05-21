package com.example.proyecto2.Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2.R;

public class HotelesAdaptador extends RecyclerView.Adapter<HotelesAdaptador.ViewHolder> {

    private String[] nombresHoteles;
    private int[] imagenesHoteles;
    private String[] preciosHoteles;
    private String[] direccionHoteles;
    private float[] puntuacionesHoteles;
    private String tipo;
    private boolean mostrarTipo;


    public HotelesAdaptador(String[] nombresHoteles, int[] imagenesHoteles, String[] preciosHoteles, String[] direccionHoteles, float[] puntuacionesHoteles, String tipo, boolean mostrarTipo) {
        this.nombresHoteles = nombresHoteles;
        this.imagenesHoteles = imagenesHoteles;
        this.preciosHoteles = preciosHoteles;
        this.direccionHoteles = direccionHoteles;
        this.puntuacionesHoteles = puntuacionesHoteles;
        this.tipo = tipo;
        this.mostrarTipo = mostrarTipo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.hotel_resumen,null);
        ViewHolder vh = new ViewHolder(elLayoutDeCadaItem, tipo);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titulo.setText(nombresHoteles[position]);
        holder.foto.setImageResource(imagenesHoteles[position]);
        holder.direccion.setText(direccionHoteles[position]);
        holder.estrellas.setRating(puntuacionesHoteles[position]);
        if (mostrarTipo) {
            //Segun las preferencias
            holder.precio.setText(preciosHoteles[position]);
        }
    }

    @Override
    public int getItemCount() {
        return nombresHoteles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView foto;
        public TextView titulo;
        public TextView precio;
        public TextView direccion;
        public RatingBar estrellas;

        public ViewHolder(@NonNull View itemView, String tipo) {
            super(itemView);
            //Carga la info y si pulsas en la imagen te lleva al fragment de mas detalle
            foto = itemView.findViewById(R.id.foto);
            precio = itemView.findViewById(R.id.precio);
            titulo = itemView.findViewById(R.id.texto);
            direccion = itemView.findViewById(R.id.direccion);
            estrellas= (RatingBar) itemView.findViewById(R.id.ratingBar);
            foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle bundle = new Bundle();
                    String cod = getAdapterPosition()+"";
                    bundle.putString("codigo", cod);
                    bundle.putString("tipo", tipo);

                    Navigation.findNavController(view).navigate(R.id.action_fragmentPrincipal_to_fragmentProducto, bundle);
                }
            });
        }
    }
}

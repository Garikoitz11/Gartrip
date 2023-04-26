package com.example.proyecto2.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Fragments.FragmentProducto;
import com.example.proyecto2.R;

import java.util.ArrayList;
import java.util.List;

public class ProductosAdaptador extends RecyclerView.Adapter<ProductosAdaptador.ViewHolder> {

    private String[] nombresProductos;
    private int[] imagenesProductos;
    private String[] preciosProductos;
    private String tipo;
    private boolean mostrarTipo;


    public ProductosAdaptador(String[] nombresProductos, int[] imagenesProductos, String[] preciosProductos, String tipo, boolean mostrarTipo) {
        this.nombresProductos = nombresProductos;
        this.imagenesProductos = imagenesProductos;
        this.preciosProductos = preciosProductos;
        this.tipo = tipo;
        this.mostrarTipo = mostrarTipo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.producto_resumen,null);
        ViewHolder vh = new ViewHolder(elLayoutDeCadaItem, tipo);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titulo.setText(nombresProductos[position]);
        holder.foto.setImageResource(imagenesProductos[position]);
        if (mostrarTipo) {
            //Segun las preferencias
            holder.precio.setText(preciosProductos[position]);
        }
    }

    @Override
    public int getItemCount() {
        return nombresProductos.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView foto;
        public TextView titulo;
        public TextView precio;

        public ViewHolder(@NonNull View itemView, String tipo) {
            super(itemView);
            //Carga la info y si pulsas en la imagen te lleva al fragment de mas detalle
            foto = itemView.findViewById(R.id.foto);
            precio = itemView.findViewById(R.id.precio);
            titulo = itemView.findViewById(R.id.texto);
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

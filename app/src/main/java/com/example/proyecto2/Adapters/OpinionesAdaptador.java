package com.example.proyecto2.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.proyecto2.R;

public class OpinionesAdaptador extends BaseAdapter {

    private Context contexto;
    private LayoutInflater inflater;
    private String[] usuarios;
    private String[] comentarios;
    private float[] puntuaciones;

    public OpinionesAdaptador(Context pcontext, String[] pusuarios, String[] pcomentarios, float[] ppuntos)
    {
        this.contexto = pcontext;
        this.usuarios = pusuarios;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.comentarios = pcomentarios;
        this.puntuaciones = ppuntos;
    }

    @Override
    public int getCount() {
        return usuarios.length;
    }

    @Override
    public Object getItem(int i) {
        return usuarios[i];
    }

    @Override
    public long getItemId(int i)  {
        return i;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.item_opiniones,null);
        TextView nombre = (TextView) view.findViewById(R.id.usuario);
        TextView coment = (TextView) view.findViewById(R.id.comentario);
        RatingBar puntos = view.findViewById(R.id.estrellas);

        nombre.setText(usuarios[i]);
        coment.setText(String.valueOf(comentarios[i]));
        puntos.setRating(puntuaciones[i]);

        return view;
    }
}

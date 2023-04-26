package com.example.proyecto2.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2.ActividadMapa;
import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Dialogs.Compra;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FragmentProducto extends Fragment {

    String nombreProducto;
    String precioProducto;
    String descripcionProducto;
    String fotoProducto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        Bundle extras = getArguments();
        String email = extras.getString("email");
        String codigoProducto = extras.getString("codigo");
        String tipoProducto = extras.getString("tipo");

        InputStream fich;
        BufferedReader buff;

        //Lee el fichero segun el filtro para la muestra de los detalles del producto
        if (tipoProducto.equals("otros")) {
            fich = getResources().openRawResource(R.raw.otros);
            buff = new BufferedReader(new InputStreamReader(fich));
        }
        else if (tipoProducto.equals("movil")) {
            fich = getResources().openRawResource(R.raw.moviles);
            buff = new BufferedReader(new InputStreamReader(fich));
        }
        else if (tipoProducto.equals("ordenador")) {
            fich = getResources().openRawResource(R.raw.ordenadores);
            buff = new BufferedReader(new InputStreamReader(fich));
        }
        else if (tipoProducto.equals("consola")) {
            fich = getResources().openRawResource(R.raw.consolas);
            buff = new BufferedReader(new InputStreamReader(fich));
        }
        else {
            fich = getResources().openRawResource(R.raw.productos);
            buff = new BufferedReader(new InputStreamReader(fich));
        }

        try {
            //Lee el fichero
            String linea = buff.readLine();
            while(linea != null){
                if(linea.equals(codigoProducto))
                {
                    linea = buff.readLine();
                    nombreProducto = linea;
                    linea = buff.readLine();
                    precioProducto = linea;
                    linea = buff.readLine();
                    descripcionProducto = linea;
                    linea = buff.readLine();
                    fotoProducto = linea;
                }
                linea = buff.readLine();
            }
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView textoNombre = (TextView) view.findViewById(R.id.tituloProducto);
        textoNombre.setText(nombreProducto);
        TextView textoPrecio = (TextView) view.findViewById(R.id.precioProducto);
        textoPrecio.setText(precioProducto);
        TextView textoDescripcion = (TextView) view.findViewById(R.id.descripcionProducto);
        textoDescripcion.setText(descripcionProducto);
        Button pagar = (Button) view.findViewById(R.id.button2);

        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si decide comprar
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean esMayorEdad = prefs.getBoolean("mayorEdad", false);

                //Solo deja si en las preferencias ponemos mayor de edad
                if(esMayorEdad){
                    //Abre el mapa para que el usuario seleccione una ubicacion donde recoger el pedido
                    Intent intent = new Intent(getContext(), ActividadMapa.class);
                    ActividadPrincipal actividad = (ActividadPrincipal) getActivity();
                    intent.putExtra("email", actividad.obtenerUsuario());
                    intent.putExtra("nombreProducto", nombreProducto);
                    intent.putExtra("precioProducto", precioProducto);
                    startActivity(intent);
                }
                else {
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getContext(), getResources().getString(R.string.mayorEdadCompra), tiempo);
                    aviso.show();
                }
            }
        });

        //Añadimos la foto
        ImageView foto = (ImageView) view.findViewById(R.id.imagenProducto);
        int drawableResourceId = this.getResources().getIdentifier(fotoProducto, "drawable", getContext().getPackageName());
        foto.setImageResource(drawableResourceId);

        return view;
    }

}
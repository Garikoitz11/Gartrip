package com.example.proyecto2.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentProducto extends Fragment {

    List<String[]> rows = new ArrayList<>();

    static String nombreProducto;
    String precioProducto;
    String descripcionProducto;
    static String fotoProducto;
    Float estrellas;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        Bundle extras = getArguments();
        String id_hotel = extras.getString("id_hotel");

        InputStream fich;
        BufferedReader buff;


        //cogemos la info del fichero para mostrar los datos
        recogerInfo();
        boolean enc = false;
        int i =0;

        while (enc==false && i< rows.size() ){
            Log.i("linea",rows.get(i)[0]);
            if(id_hotel.equals(rows.get(i)[0])){
                descripcionProducto=rows.get(i)[1];
                nombreProducto=rows.get(i)[2];
                precioProducto = rows.get(i)[3];
                estrellas=Float.valueOf(rows.get(i)[4]);
                fotoProducto = rows.get(i)[5];
                enc =true;
            }
            i+=1;

        }
        Log.i("HOTeL", nombreProducto);

        TextView idHotel = view.findViewById(R.id.idHotel);
        idHotel.setText(id_hotel);
        TextView textoNombre = view.findViewById(R.id.tituloProducto);
        textoNombre.setText(nombreProducto);
        TextView textoPrecio = view.findViewById(R.id.precioProducto);
        textoPrecio.setText(precioProducto);
        TextView textoDescripcion = view.findViewById(R.id.descripcionProducto);
        textoDescripcion.setText(descripcionProducto);
        Button pagar = view.findViewById(R.id.button2);

        pagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("ENTRA QUI", "siii");
                //Si decide comprar
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                Boolean esMayorEdad = prefs.getBoolean("mayorEdad", false);

                //Solo deja si en las preferencias ponemos mayor de edad
                if(esMayorEdad){
                    //Abre el mapa para que el usuario seleccione una ubicacion donde recoger el pedido
                    Bundle bundle = new Bundle();
                    bundle.putString("nombreProducto", nombreProducto);
                    bundle.putString("precioProducto", precioProducto);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentProducto_to_fragmentMapa, bundle);
                }
                else {
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getContext(), getResources().getString(R.string.mayorEdadCompra), tiempo);
                    aviso.show();
                }
            }
        });

        //Añadimos la foto del producto
        ImageView foto = view.findViewById(R.id.imagenProducto);
        int drawableResourceId = this.getResources().getIdentifier(fotoProducto, "drawable", getContext().getPackageName());
        foto.setImageResource(drawableResourceId);

        return view;
    }

    private void recogerInfo(){
        //Se obtiene el archivo
        InputStream fich = getResources().openRawResource(R.raw.hotelinfo);
        BufferedReader buff = new BufferedReader(new InputStreamReader(fich));
        String splitby = ";";
        String linea;

        //Por cada linea de texto
        try {
            //Recogida
            buff.readLine();
            //Recogida de cada columna
            while ((linea=buff.readLine())!=null){
                String[] row = linea.split(splitby);
                rows.add(row);
            }
            //Cierre
            fich.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
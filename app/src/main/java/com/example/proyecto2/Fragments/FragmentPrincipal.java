package com.example.proyecto2.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2.Adapters.HotelesAdaptador;
import com.example.proyecto2.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FragmentPrincipal extends Fragment {

    HotelesAdaptador hotelesAdaptador;
    RecyclerView recyclerView;
    String categoria;
    String email;

    List<String[]> rows = new ArrayList<>();

    EditText editTextLupa;

    List<String> idHoteles = new ArrayList<>();
    List<String> hotelesNombres = new ArrayList<>();
    List<String> hotelesPrecios = new ArrayList<>();
    List<String> hotelesDireccion = new ArrayList<>();
    ArrayList<Integer> hotelesImagenes = new ArrayList<>();
    ArrayList<Float> hotelesEstrella = new ArrayList<>();

    ArrayList<String> filteredListIdHotel = new ArrayList<>();

    ArrayList<String> filteredListNombre = new ArrayList<>();
    ArrayList<String> filteredListPrecio = new ArrayList<>();
    ArrayList<Integer> filteredListImagen = new ArrayList<>();
    List<String> filteredListDireccion = new ArrayList<>();
    ArrayList<Float> filteredListEstrella = new ArrayList<>();

    SeekBar barraPrecio;
    TextView precioSeekBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        barraPrecio = view.findViewById(R.id.barraPrecio);
        precioSeekBar=view.findViewById(R.id.id_textView_PrecioCambiante);



        Log.i("num elementos", String.valueOf(hotelesNombres.size()));
        if(hotelesNombres.size()==0){
            recogerInfo();
            for (int i = 0; i < rows.size(); i++) {
                Log.d("Prueba", String.format("row %s: %s, %s, %s, %s; img: %s", i,rows.get(i)[0], rows.get(i)[1], rows.get(i)[2],rows.get(i)[3],rows.get(i)[4],rows.get(i)[5]));
                idHoteles.add(rows.get(i)[0]);
                hotelesDireccion.add(rows.get(i)[1]);
                hotelesNombres.add(rows.get(i)[2]);
                hotelesPrecios.add(rows.get(i)[3]);
                hotelesEstrella.add(Float.valueOf(rows.get(i)[4]));
                hotelesImagenes.add(getResources().getIdentifier(rows.get(i)[5], "drawable", Objects.requireNonNull(getContext()).getPackageName()));
                Log.i("NombreHotel",hotelesNombres.get(i));
                Log.i("imagenhotel",Integer.toString(hotelesImagenes.get(i)));
            }
            Log.i("lista", idHoteles.toString());
            String[] filteredListNombreArray = hotelesNombres.toArray(new String[hotelesNombres.size()]);
            String[] filteredListPrecioArray = hotelesPrecios.toArray(new String[hotelesPrecios.size()]);
            String[] filteredListDireccionArray = hotelesDireccion.toArray(new String[hotelesDireccion.size()]);
            String[] filteredListIdHotelesArray = idHoteles.toArray(new String[idHoteles.size()]);
            mostrarData(convertIntegers(hotelesImagenes), filteredListNombreArray, filteredListPrecioArray, filteredListDireccionArray, convertFloat(hotelesEstrella),filteredListIdHotelesArray);

        }

        //Comenzamos el filtrado segun el texto introducido(hotel o ciudad/país)
        editTextLupa = view.findViewById(R.id.id_editTextBuscador);
        editTextLupa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //Cada vez que cambiamos el texto comprobamos el precio máximo establecido
                String precio = precioSeekBar.getText().toString();
                String[] r = precio.split("€"); //quitamos el simbolo del euro para tener el int
                int p = Integer.parseInt(r[0]);
                filter(charSequence.toString(),p);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //Configuramos el seekBar
        barraPrecio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                precioSeekBar.setVisibility(View.VISIBLE);
                precioSeekBar.setText(String.valueOf(i) + "€");//muestra el progreso
                filtrarPrecio(i);//llamamos a filtrar precio
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



        return view;
    }

    private void filtrarPrecio(int precioMax) {

        //establecemos el precio maximo y llamamos al filtrado de nombre
        filter(editTextLupa.getText().toString(),precioMax);
    }

    private void filter(String texto, int precioMax) {

        //id para encontrar la pos del nombre y luego buscar en los demas [] y obtener todos los datos
        Log.i("buscador actual: ", texto);

        int idNombre = 0;
        int idDireccion = 0;

        //Vaciamos las listas con los resultados anteriores
        filteredListIdHotel.clear();
        filteredListNombre.clear();
        filteredListImagen.clear();
        filteredListPrecio.clear();
        filteredListDireccion.clear();
        filteredListEstrella.clear();
        Log.i("Llega","si");


        //Filtramos por nombre de hotel(comparamos cada precio del hotel)
        for (String nombre : hotelesNombres) {
            if (!filteredListNombre.contains(nombre)) {
                if (nombre.toLowerCase().contains(texto.toLowerCase() )) {
                    String precio = hotelesPrecios.get(idNombre);
                    String[] result = precio.split("€");
                    int precioHotel = Integer.parseInt(result[0]);
                    if(precioHotel<=precioMax){
                        filteredListNombre.add(nombre);
                        filteredListIdHotel.add(idHoteles.get(idNombre));
                        filteredListPrecio.add(hotelesPrecios.get(idNombre));
                        filteredListImagen.add(hotelesImagenes.get(idNombre));
                        filteredListDireccion.add(hotelesDireccion.get(idNombre));
                        filteredListEstrella.add(hotelesEstrella.get(idNombre));
                    }
                }
            }

            idNombre += 1;
        }

        //Filtramos por direccion (comparamos cada precio del hotel)
        for (String direccion : hotelesDireccion) {
            if(!filteredListNombre.contains(hotelesNombres.get(idDireccion))){
                if (direccion.toLowerCase().contains(texto.toLowerCase())) {
                    String precio = hotelesPrecios.get(idDireccion);
                    String[] result = precio.split("€");
                    int precioHotel = Integer.parseInt(result[0]);
                    if(precioHotel<=precioMax) {
                        filteredListIdHotel.add(idHoteles.get(idDireccion));
                        filteredListNombre.add(hotelesNombres.get(idDireccion));
                        filteredListPrecio.add(hotelesPrecios.get(idDireccion));
                        filteredListImagen.add(hotelesImagenes.get(idDireccion));
                        filteredListDireccion.add(hotelesDireccion.get(idDireccion));
                        filteredListEstrella.add(hotelesEstrella.get(idDireccion));
                    }
                }
            }
            idDireccion += 1;
        }


        if(filteredListNombre.size()==0){
            Log.i("entraaaa", "siii");
            Toast.makeText(getContext(),"No se ha encontrado ningun resultado", Toast.LENGTH_SHORT);//No se por que no lo hace no pilla el contexto¿?
        }

        //Pasamos de arrayList a array para adaptarse al formato del mostrarData()
        String[] filtroBuscadorListIdHotel = filteredListIdHotel.toArray(new String[filteredListIdHotel.size()]);
        String[] filtroBuscadorListNombreArray = filteredListNombre.toArray(new String[filteredListNombre.size()]);
        String[] filtroBuscadorListPrecioArray = filteredListPrecio.toArray(new String[filteredListPrecio.size()]);
        int[] filtroBuscadorListImagenArray = convertIntegers(filteredListImagen);
        String[] filtroBuscadorListDireccionArray = filteredListDireccion.toArray(new String[filteredListDireccion.size()]);
        Log.i("nº productos", String.valueOf(filtroBuscadorListNombreArray.length));

        mostrarData(filtroBuscadorListImagenArray, filtroBuscadorListNombreArray, filtroBuscadorListPrecioArray, filtroBuscadorListDireccionArray, convertFloat(filteredListEstrella),filtroBuscadorListIdHotel);

    }



    public void mostrarData(int[] hotelesImagenes, String[] hotelesNombres, String[] hotelesPrecios, String[] hotelesDireccion, float[] estrellasHoteles, String[] idHoteles) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //En funcion de las preferencias del usuario
        Boolean mostrarPrecio = prefs.getBoolean("mostrarPrecio", true);

        //Crea el recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hotelesAdaptador = new HotelesAdaptador(hotelesNombres, hotelesImagenes, hotelesPrecios, hotelesDireccion, estrellasHoteles, categoria, mostrarPrecio, idHoteles);
        recyclerView.setAdapter(hotelesAdaptador);

       // GridLayoutManager rejilla= new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
      //  recyclerView.setLayoutManager(rejilla);
    }

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    public static float[] convertFloat(ArrayList<Float> integers)
    {
        float[] ret = new float[integers.size()];
        Iterator<Float> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    //Metodo de recoleccion del archivo de texto
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

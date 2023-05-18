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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoria = getArguments().getString("tipo");
        }
        else {
            categoria = "todos";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_principal, container, false);
        recyclerView = view.findViewById(R.id.recycler);

        recogerInfo();
        List<String> hotelesNombres = new ArrayList<>();
        List<String> hotelesPrecios = new ArrayList<>();
        List<String> hotelesDireccion = new ArrayList<>();
        ArrayList<Integer> hotelesImagenes = new ArrayList<>();
        ArrayList<Float> hotelesEstrella = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Log.d("Prueba", String.format("row %s: %s, %s, %s, %s; img: %s", i, rows.get(i)[0], rows.get(i)[1],rows.get(i)[2],rows.get(i)[4],rows.get(i)[3]));
            hotelesNombres.add(rows.get(i)[0]);
            hotelesPrecios.add(rows.get(i)[1]);
            hotelesDireccion.add(rows.get(i)[2]);
            hotelesImagenes.add(getResources().getIdentifier(rows.get(i)[3], "drawable", Objects.requireNonNull(getContext()).getPackageName()));
            hotelesEstrella.add(Float.valueOf(rows.get(i)[4]));
        }
        
        //Los arrays de los productos
        /*int[] hotelesImagenes= {R.drawable.iphone14, R.drawable.ps5, R.drawable.auriculares, R.drawable.fifa23,
                R.drawable.gow, R.drawable.macbookpro13, R.drawable.nothingphone, R.drawable.ordenador, R.drawable.ordenador2,
                R.drawable.pocox4, R.drawable.ratonlogitech, R.drawable.victus16, R.drawable.xiaomiredmia1, R.drawable.cod};
        String[] hotelesNombres= {"Iphone 14","Play Station 5", "Auriculares inalámbricos", "FIFA 23", "God of War Ragnarok",
                "Macbook Pro 13", "Nothing Phone 1", "PC Racing Gaming AMD", "PC Racing Intel", "Poco X4", "Ratón Logitech",
                "Victus 16", "Xiaomi Redmi A1", "COD Cold War"};
        String[] hotelesPrecios= {"1459€/noche","549€/noche", "126,26€/noche", "54,99€/noche", "66,99€/noche",
                "1410€/noche", "499€/noche", "950,99€/noche", "610,99€/noche", "394,72€/noche", "31,99€/noche",
                "1199€/noche", "99,99€/noche", "23,99€/noche"};*/
        String[] productosCategoria= {"movil","consola", "otros", "consola", "consola",
                "ordenador", "movil", "ordenador", "ordenador", "movil", "otros",
                "ordenador", "movil", "consola"};
        /*String[] hotelesDireccion={"movil","consola", "otros", "consola", "consola",
                "ordenador", "movil", "ordenador", "ordenador", "movil", "otros",
                "ordenador", "movil", "consola"};
        float[] hotelesEstrella={ 1, 2, 3, 4, 5, 1, 2, 3, 4, 5, 1, 2, 3, 4};*/

        if (categoria.equals("todos")) {
            String[] filteredListNombreArray = hotelesNombres.toArray(new String[hotelesNombres.size()]);
            String[] filteredListPrecioArray = hotelesPrecios.toArray(new String[hotelesPrecios.size()]);
            String[] filteredListDireccionArray = hotelesDireccion.toArray(new String[hotelesDireccion.size()]);
            mostrarData(convertIntegers(hotelesImagenes), filteredListNombreArray, filteredListPrecioArray, filteredListDireccionArray, convertFloat(hotelesEstrella));
        }
        else {
            ArrayList<Integer> index = new ArrayList<Integer>();
            int i = 0;
            for (String producto: productosCategoria) {
                if(producto.equals(categoria)) {
                    index.add(i);
                }
                i++;
            }
            String [] nuevoArrayNombres = new String[index.size()];
            int [] nuevoArrayImagenes = new int[index.size()];
            String [] nuevoArrayPrecios = new String[index.size()];
            String [] nuevoArrayDirecciones = new String[index.size()];
            float [] nuevoArrayPuntuaciones = new float[index.size()];

            int j = 0;
            for (int h = 0; h < rows.size(); h++) {
                if(index.contains(h)) {
                    nuevoArrayNombres[j] = hotelesNombres.get(h);
                    nuevoArrayImagenes[j] = hotelesImagenes.get(h);
                    nuevoArrayPrecios[j] = hotelesPrecios.get(h);
                    nuevoArrayDirecciones[j] = hotelesDireccion.get(h);
                    nuevoArrayPuntuaciones[j] = hotelesEstrella.get(h);
                    j++;
                }
            }
            //Los datos filtrados por el filtro del getArguments
            mostrarData(nuevoArrayImagenes, nuevoArrayNombres, nuevoArrayPrecios, nuevoArrayDirecciones, nuevoArrayPuntuaciones);

        }

        return view;
    }

    public void mostrarData(int[] hotelesImagenes, String[] hotelesNombres, String[] hotelesPrecios, String[] hotelesDireccion, float[] estrellasHoteles) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        //En funcion de las preferencias del usuario
        Boolean mostrarPrecio = prefs.getBoolean("mostrarPrecio", true);

        //Crea el recycler
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hotelesAdaptador = new HotelesAdaptador(hotelesNombres, hotelesImagenes, hotelesPrecios, hotelesDireccion, estrellasHoteles, categoria, mostrarPrecio);
        recyclerView.setAdapter(hotelesAdaptador);

        GridLayoutManager rejilla= new GridLayoutManager(getContext(),1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(rejilla);
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
        InputStream fich = getResources().openRawResource(R.raw.hoteles);
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
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto2.Adapters.ProductosAdaptador;
import com.example.proyecto2.R;

import java.util.ArrayList;

public class FragmentPrincipal extends Fragment {

    ProductosAdaptador productosAdaptador;
    RecyclerView recyclerView;
    String categoria;
    String email;

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

        //Los arrays de los productos
        int[] productosImagenes= {R.drawable.iphone14, R.drawable.ps5, R.drawable.auriculares, R.drawable.fifa23,
                R.drawable.gow, R.drawable.macbookpro13, R.drawable.nothingphone, R.drawable.ordenador, R.drawable.ordenador2,
                R.drawable.pocox4, R.drawable.ratonlogitech, R.drawable.victus16, R.drawable.xiaomiredmia1, R.drawable.cod};
        String[] productosNombres= {"Iphone 14","Play Station 5", "Auriculares inalámbricos", "FIFA 23", "God of War Ragnarok",
                "Macbook Pro 13", "Nothing Phone 1", "PC Racing Gaming AMD", "PC Racing Intel", "Poco X4", "Ratón Logitech",
                "Victus 16", "Xiaomi Redmi A1", "COD Cold War"};
        String[] productosPrecios= {"1459€","549€", "126,26€", "54,99€", "66,99€",
                "1410€", "499€", "950,99€", "610,99€", "394,72€", "31,99€",
                "1199€", "99,99€", "23,99€"};
        String[] productosCategoria= {"movil","consola", "otros", "consola", "consola",
                "ordenador", "movil", "ordenador", "ordenador", "movil", "otros",
                "ordenador", "movil", "consola"};

        if (categoria.equals("todos")) {
            mostrarData(productosImagenes, productosNombres, productosPrecios);
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

            int j = 0;
            for (int h = 0; h < productosNombres.length; h++) {
                if(index.contains(h)) {
                    nuevoArrayNombres[j] = productosNombres[h];
                    nuevoArrayImagenes[j] = productosImagenes[h];
                    nuevoArrayPrecios[j] = productosPrecios[h];
                    j++;
                }
            }
            mostrarData(nuevoArrayImagenes, nuevoArrayNombres, nuevoArrayPrecios);

        }

        return view;
    }

    public void mostrarData(int[] productosImagenes, String[] productosNombres, String[] productosPrecios) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean mostrarPrecio = prefs.getBoolean("mostrarPrecio", false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productosAdaptador = new ProductosAdaptador(productosNombres, productosImagenes, productosPrecios, categoria, mostrarPrecio);
        recyclerView.setAdapter(productosAdaptador);

        GridLayoutManager rejilla= new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(rejilla);
    }

}
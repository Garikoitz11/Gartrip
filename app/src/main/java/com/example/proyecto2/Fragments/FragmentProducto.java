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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.proyecto2.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FragmentProducto extends Fragment {
    List<String[]> rows = new ArrayList<>();
    static String nombreHotel;
    String precioHotel;
    String provincia;
    static String imgHotel;
    Float estrellas;

    String piscina ="piscina";
    String wf = "Wifi";
    String adaptadoSillaDeRuedas="adaptado";
    String desayuno ="desayuno incluido";
    String jardin = "jardin";
    String terraza = "terraza";
    String aten24 = "atencion24h";
    String gimnasio = "gym";
    String parking="parking";
    String aire = "aire acondicionado";

    List<String> caracteristicasHoteles = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        Bundle extras = getArguments();
        String id_hotel = extras.getString("id_hotel");

        //cogemos la info del fichero para mostrar los datos
        recogerInfo();
        boolean enc = false;
        int i =0;

        while (enc == false && i < rows.size() ){
            Log.i("linea",rows.get(i)[0]);
            if(id_hotel.equals(rows.get(i)[0])){
                provincia = rows.get(i)[1];
                nombreHotel = rows.get(i)[2];
                precioHotel = rows.get(i)[3];
                estrellas = Float.valueOf(rows.get(i)[4]);
                imgHotel = rows.get(i)[5];
                if(estrellas==1){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                }
                if(estrellas==2){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                }
                if(estrellas==3){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                    caracteristicasHoteles.add(piscina);
                }
                if(estrellas==4){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                    caracteristicasHoteles.add(jardin);
                    caracteristicasHoteles.add(piscina);
                    caracteristicasHoteles.add(parking);
                }
                if(estrellas==5){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                    caracteristicasHoteles.add(jardin);
                    caracteristicasHoteles.add(piscina);
                    caracteristicasHoteles.add(terraza);
                    caracteristicasHoteles.add(parking);
                    caracteristicasHoteles.add(aten24);
                    caracteristicasHoteles.add(adaptadoSillaDeRuedas);

                }
                enc =true;
            }
            i+=1;
        }
        Log.i("HOTeL", nombreHotel);

        /*
        TextView textoNombre = view.findViewById(R.id.nombreHotel);
        textoNombre.setText(nombreHotel);
        TextView textoPrecio = view.findViewById(R.id.precioHotel);
        textoPrecio.setText(precioHotel);
        TextView textoDescripcion = view.findViewById(R.id.provincia);
        textoDescripcion.setText(provincia);
        */

        TextView txtVhotel = view .findViewById(R.id.txtHotel);
        txtVhotel.setText(nombreHotel);

        TextView txtVprovincia = view.findViewById(R.id.txtProvincia);
        txtVprovincia.setText(provincia);

        TextView txtVprecioNoche = view.findViewById(R.id.txtPrecio);
        String euros = getString(R.string.eurosNoche);
        txtVprecioNoche.setText(precioHotel + euros);

        RatingBar rtngEstrellas = view.findViewById(R.id.ratingBar);
        rtngEstrellas.setRating(estrellas);

        Button completarReserva = view.findViewById(R.id.btnCompletarReserva);

        completarReserva.setOnClickListener(new View.OnClickListener() {
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
                    bundle.putString("nombreProducto", nombreHotel);
                    bundle.putString("precioProducto", precioHotel);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentProducto_to_fragmentMapa, bundle);
                }
                else {
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getContext(), getResources().getString(R.string.mayorEdadCompra), tiempo);
                    aviso.show();
                }
            }
        });

        // IMAGENES AÑADIDAS AL SLIDER
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        /*
        slideModels.add(new SlideModel(R.drawable.acisgalatea1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.acisgalatea2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.acisgalatea3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.acisgalatea4, ScaleTypes.FIT));
         */

        for (int index = 1; index <= 5; index++) {
            String imageName = imgHotel + index;
            Log.i("IMAGEN",imageName);
            int resourceId = getResources().getIdentifier(imageName,"drawable",getActivity().getPackageName());
            slideModels.add(new SlideModel(resourceId, ScaleTypes.FIT));
        }

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        /*//Añadimos la foto del producto
        ImageView foto = view.findViewById(R.id.imagenProducto);
        int drawableResourceId = this.getResources().getIdentifier(fotoProducto, "drawable", getContext().getPackageName());

        foto.setImageResource(drawableResourceId);
        */

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
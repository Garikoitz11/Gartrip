package com.example.proyecto2.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Adapters.OpinionesAdaptador;
import com.example.proyecto2.Dialogs.Idioma;
import com.example.proyecto2.Dialogs.Opinion;
import com.example.proyecto2.R;
import com.example.proyecto2.Services.OpinionBDService;
import com.example.proyecto2.Services.RecogerOpinionesBDService;
import com.example.proyecto2.opinionListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FragmentProducto extends Fragment{
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

    TextView textoWifi;
    TextView textoDesayuno;
    TextView textoAire;
    TextView textoPiscina;
    TextView textoParking;
    TextView textoSilla;

    ImageView icoWifi;
    ImageView icoDesayuno;
    ImageView icoAire;
    ImageView icoPiscina;
    ImageView icoParking;
    ImageView icoSilla;

    String email;
    String id_hotel;
    ListView listView;

    String[] usuarios;
    String[] comentarios;
    float[] puntuaciones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_producto, container, false);

        Bundle extras = getArguments();
        id_hotel = extras.getString("id_hotel");
        email = extras.getString("email");

        //cogemos las vistas de iconos y sus textos
        //iconos

        icoWifi= view.findViewById(R.id.icoWifi);
        icoDesayuno= view.findViewById(R.id.desayuno);
        icoAire = view.findViewById(R.id.icoAire);
        icoPiscina = view.findViewById(R.id.icoPiscina);
        icoParking = view.findViewById(R.id.icoParking);
        icoSilla= view.findViewById(R.id.icoSilla);

        //textos
        textoWifi = view.findViewById(R.id.text_wifi);
        textoDesayuno = view.findViewById(R.id.text_desayuno);
        textoAire = view.findViewById(R.id.text_aire);
        textoPiscina = view.findViewById(R.id.text_piscina);
        textoParking =view.findViewById(R.id.text_parking);
        textoSilla = view.findViewById(R.id.text_Silla);

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
                    icoWifi.setVisibility(View.VISIBLE);
                    textoWifi.setVisibility(View.VISIBLE);
                    icoDesayuno.setVisibility(View.VISIBLE);
                    textoDesayuno.setVisibility(View.VISIBLE);
                }
                if(estrellas==2){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);

                    icoWifi.setVisibility(View.VISIBLE);
                    textoWifi.setVisibility(View.VISIBLE);
                    icoDesayuno.setVisibility(View.VISIBLE);
                    textoDesayuno.setVisibility(View.VISIBLE);
                    icoAire.setVisibility(View.VISIBLE);
                    textoAire.setVisibility(View.VISIBLE);
                }
                if(estrellas==3){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                    caracteristicasHoteles.add(piscina);

                    icoWifi.setVisibility(View.VISIBLE);
                    textoWifi.setVisibility(View.VISIBLE);
                    icoDesayuno.setVisibility(View.VISIBLE);
                    textoDesayuno.setVisibility(View.VISIBLE);
                    icoAire.setVisibility(View.VISIBLE);
                    textoAire.setVisibility(View.VISIBLE);
                    icoPiscina.setVisibility(View.VISIBLE);
                    textoPiscina.setVisibility(View.VISIBLE);
                }
                if(estrellas==4){
                    caracteristicasHoteles.add(wf);
                    caracteristicasHoteles.add(desayuno);
                    caracteristicasHoteles.add(aire);
                    caracteristicasHoteles.add(jardin);
                    caracteristicasHoteles.add(piscina);
                    caracteristicasHoteles.add(parking);

                    icoWifi.setVisibility(View.VISIBLE);
                    textoWifi.setVisibility(View.VISIBLE);
                    icoDesayuno.setVisibility(View.VISIBLE);
                    textoDesayuno.setVisibility(View.VISIBLE);
                    icoAire.setVisibility(View.VISIBLE);
                    textoAire.setVisibility(View.VISIBLE);
                    icoPiscina.setVisibility(View.VISIBLE);
                    textoPiscina.setVisibility(View.VISIBLE);
                    icoParking.setVisibility(View.VISIBLE);
                    textoParking.setVisibility(View.VISIBLE);
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


                    icoWifi.setVisibility(View.VISIBLE);
                    textoWifi.setVisibility(View.VISIBLE);
                    icoDesayuno.setVisibility(View.VISIBLE);
                    textoDesayuno.setVisibility(View.VISIBLE);
                    icoAire.setVisibility(View.VISIBLE);
                    textoAire.setVisibility(View.VISIBLE);
                    icoPiscina.setVisibility(View.VISIBLE);
                    textoPiscina.setVisibility(View.VISIBLE);
                    icoParking.setVisibility(View.VISIBLE);
                    textoParking.setVisibility(View.VISIBLE);
                    icoSilla.setVisibility(View.VISIBLE);
                    textoSilla.setVisibility(View.VISIBLE);
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

        // Spinner para seleccionar las personas y calcular entorno a las personas
        Spinner spinner = view.findViewById(R.id.spinner);
        String[] opciones = {"1","2","3","4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                int numeroPersonas = Integer.parseInt(selectedItem);
                String[] strPrecioSinEuro = precioHotel.split("€");
                int numPrecioSinEuro = Integer.parseInt(strPrecioSinEuro[0]);
                int resultado = numeroPersonas * numPrecioSinEuro;
                String strResultado = Integer.toString(resultado);
                txtVprecioNoche.setText(strResultado + euros);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Button enviarOpinion = view.findViewById(R.id.button5);

        enviarOpinion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data datos = new Data.Builder()
                        .putString("email",email)
                        .putString("id",id_hotel)
                        .putString("accion","comprobar")
                        .build();

                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(OpinionBDService.class)
                        .setInputData(datos)
                        .build();

                WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(getActivity(), new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if(workInfo != null && workInfo.getState().isFinished()){
                                    Log.i("BOTON", "Fin acceso");
                                    Log.i("BOTON", "¿Correcto? " + workInfo.getOutputData().getBoolean("valor", false) + ", " + workInfo.getOutputData().getString("texto"));
                                    Boolean resultado = workInfo.getOutputData().getBoolean("valor", false);
                                    String codigo = workInfo.getOutputData().getString("texto");

                                    if(resultado){
                                        ActividadPrincipal actividad = (ActividadPrincipal) getActivity();
                                        actividad.mostrarDialogoOpinion(id_hotel);
                                    } else {
                                        int tiempo= Toast.LENGTH_SHORT;
                                        Toast aviso = Toast.makeText(getContext(), "Ya has mandado opinion", tiempo);
                                        aviso.show();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(getContext()).enqueue(otwr);
            }
        });

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

                    //Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentProducto_to_fragmentMapa, bundle);
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

        listView = view.findViewById(R.id.listaOpiniones);

        rellenar();

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

    public void rellenar(){
        usuarios=null;
        comentarios=null;
        puntuaciones=null;

        //Creamos un objeto de tipo data y le metemos el email
        Data datos = new Data.Builder()
                .putString("hotel_id", id_hotel)
                .build();

        //Creamos una solicitud de trabajo para la ejecucion de la llamada asincrona a la bd
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(RecogerOpinionesBDService.class)
                .setInputData(datos)
                .build();
        //Le añadimos un observable para que actue una vez reciba de vuelta algo
        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            //Obtenemos la respuesta del servidor en funcion de si es correcto o no
                            boolean operacionCorrecta = workInfo.getOutputData().getBoolean("operacionCorrecta", false);
                            if (operacionCorrecta) {
                                usuarios = workInfo.getOutputData().getStringArray("nombres");
                                Log.i("recogida", Arrays.toString(usuarios));
                                comentarios = workInfo.getOutputData().getStringArray("comentarios");
                                Log.i("recogida", Arrays.toString(comentarios));
                                puntuaciones = workInfo.getOutputData().getFloatArray("puntuaciones");
                                Log.i("recogida", Arrays.toString(puntuaciones));

                                OpinionesAdaptador nuevoAdapter= new OpinionesAdaptador(getContext(),usuarios,comentarios,puntuaciones);
                                listView.setAdapter(nuevoAdapter);
                            }
                            else {
                                //Sino lanza mensaje de aviso de error
                                int tiempo= Toast.LENGTH_SHORT;
                                Toast aviso = Toast.makeText(getContext(), "¡Error!", tiempo);
                                aviso.show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(getContext()).enqueue(otwr);
    }
}
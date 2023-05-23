package com.example.proyecto2.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2.R;
import com.example.proyecto2.Services.ObtenerPerfilBDService;

import java.io.File;


public class FragmentPerfilPricipal extends Fragment {

    ImageButton boton1, boton2, boton3;
    private String email;
    private String nombre;
    private String apellidos;
    private Bitmap imagen;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Obtiene el email que es el identificador del usuario
            email = getArguments().getString("email");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_pricipal, container, false);

        //Creamos un objeto de tipo data y le metemos el email
        Data datos = new Data.Builder()
                .putString("email", email)
                .build();

        //Creamos una solicitud de trabajo para la ejecucion de la llamada asincrona a la bd
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerPerfilBDService.class)
                .setInputData(datos)
                .build();

        //Le añadimos un observable para que actue una vez reciba de vuelta algo
        WorkManager.getInstance(getContext()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            boolean operacionCorrecta = workInfo.getOutputData().getBoolean("operacionCorrecta", false);
                            if (operacionCorrecta) {
                                nombre = workInfo.getOutputData().getString("nombre");

                                TextView textoNombre = view.findViewById(R.id.nombrePerfil);
                                textoNombre.setText(nombre);

                            }else {
                                int tiempo= Toast.LENGTH_SHORT;
                                Toast aviso = Toast.makeText(getContext(), "¡Error!", tiempo);
                                aviso.show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(getContext()).enqueue(otwr);


        boton1 = view.findViewById(R.id.boton1);
        boton2 = view.findViewById(R.id.boton2);
        boton3 = view.findViewById(R.id.boton3);

        boton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundlePerfil = new Bundle();
                bundlePerfil.putString("email", email);
                bundlePerfil.putString("nombre", nombre);
                bundlePerfil.putString("apellidos", apellidos);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.fragmentPerfil, bundlePerfil);
            }
        });

        boton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.fragmentSobreNosotros);
            }
        });
        boton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.fragmentAtencionCliente);
            }
        });
        return view;
    }
}
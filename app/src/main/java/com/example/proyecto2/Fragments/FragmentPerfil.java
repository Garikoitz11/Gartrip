package com.example.proyecto2.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;
import com.example.proyecto2.Services.LoginBDService;
import com.example.proyecto2.Services.ObtenerPerfilBDService;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class FragmentPerfil extends Fragment {

    private String email;
    private String nombre;
    private String apellidos;
    private Bitmap imagen;

    @Override
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
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

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
                            //Obtenemos la respuesta del servidor en funcion de si es correcto o no
                            boolean operacionCorrecta = workInfo.getOutputData().getBoolean("operacionCorrecta", false);
                            if (operacionCorrecta) {
                                //Si es correcto cargamos los datos del perfil del usuario
                                nombre = workInfo.getOutputData().getString("nombre");
                                apellidos = workInfo.getOutputData().getString("apellidos");
                                String imagenUbicacion = workInfo.getOutputData().getString("imagen");

                                if (imagenUbicacion != null) {
                                    //Obtenemos la imagen y borramos el fichero intermedio
                                    imagen = BitmapFactory.decodeFile(imagenUbicacion);

                                    ImageView imageView = view.findViewById(R.id.imagenPerfil);
                                    imageView.setImageBitmap(imagen);

                                    File file = new File(imagenUbicacion);
                                    file.delete();
                                }

                                TextView textoEmail = view.findViewById(R.id.emailPerfil);
                                textoEmail.setText(email);
                                TextView textoNombre = view.findViewById(R.id.nombrePerfil);
                                textoNombre.setText(nombre);
                                TextView textoApellidos = view.findViewById(R.id.apellidosPerfil);
                                textoApellidos.setText(apellidos);
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

        Button botonEditarPerfil = view.findViewById(R.id.botonEditarPerfil);

        botonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Convertimos el bitmap a un array de bytes para poder enviarlo por el bundle
                byte[] imagenBytes = null;
                if (imagen != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    imagenBytes = stream.toByteArray();
                }

                //Pasamos los datos a editar perfil
                Bundle bundle = new Bundle();
                bundle.putString("email", email);
                bundle.putString("nombre", nombre);
                bundle.putString("apellidos", apellidos);
                bundle.putByteArray("imagen", imagenBytes);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentPerfil_to_fragmentEditarPerfil, bundle);
                //Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.fragmentEditarPerfil, bundle);
            }
        });

        return view;
    }

}
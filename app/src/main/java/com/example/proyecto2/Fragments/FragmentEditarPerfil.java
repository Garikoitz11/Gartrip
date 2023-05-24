package com.example.proyecto2.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto2.R;
import com.example.proyecto2.Services.ActualizarPerfilBDService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FragmentEditarPerfil extends Fragment {

    private String email;
    private String nombre;
    private String apellidos;
    ImageView imagen;
    TextView textoEmail;
    TextView textoContraseña;
    TextView textoNombre;
    TextView textoApellidos;
    Bitmap foto;
    File imagenFich;
    byte[] imagenBytes;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        foto = null;
        if (getArguments() != null) {
            email = getArguments().getString("email");
            nombre = getArguments().getString("nombre");
            apellidos = getArguments().getString("apellidos");
            imagenBytes = getArguments().getByteArray("imagen");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        //Obtenemos de nuevo la imagen en modo bitmap para mostrarla
        Bitmap imagenBitmap;
        if (imagenBytes != null) {
            imagenBitmap  = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
            imagen =  view.findViewById(R.id.imagenEditarPerfil);
            imagen.setImageBitmap(imagenBitmap);
        }

        textoEmail = view.findViewById(R.id.textoEmail);
        textoEmail.setText(email);
        textoContraseña = view.findViewById(R.id.editTextContraseña);
        textoNombre = view.findViewById(R.id.editTextNombre);
        textoNombre.setText(nombre);
        textoApellidos = view.findViewById(R.id.editTextApellidos);
        textoApellidos.setText(apellidos);

        pickMedia  = registerForActivityResult(new
                ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                imagen = view.findViewById(R.id.imagenEditarPerfil);
                imagen.setImageURI(uri);
                try {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                    foto = BitmapFactory.decodeStream(inputStream);
                } catch (FileNotFoundException e) {
                }

                File eldirectorio = getActivity().getFilesDir();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String nombrefichero = "IMG_" + timeStamp + "_";
                imagenFich = new File(eldirectorio, nombrefichero + ".jpg");
                OutputStream os;
                try {
                    os = new FileOutputStream(imagenFich);
                    foto.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int tiempo= Toast.LENGTH_SHORT;
                String imagenSeleccionada = getString(R.string.imagenSeleccionada);
                Toast aviso = Toast.makeText(getContext(), imagenSeleccionada, tiempo);
                aviso.show();
            }
        });

        takePictureLauncher = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData()!= null) {
                Bundle bundle = result.getData().getExtras();
                foto = (Bitmap) bundle.get("data");
                imagen.setImageBitmap(foto);

                File eldirectorio = getActivity().getFilesDir();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String nombrefichero = "IMG_" + timeStamp + "_";
                imagenFich = new File(eldirectorio, nombrefichero + ".jpg");
                OutputStream os;
                try {
                    os = new FileOutputStream(imagenFich);
                    foto.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int tiempo= Toast.LENGTH_SHORT;
                String imagenSacada = getString(R.string.imagenSacada);
                Toast aviso = Toast.makeText(getContext(), imagenSacada, tiempo);
                aviso.show();                    }
        });

        Button botonEditarPerfil = view.findViewById(R.id.botonSeleccionarFoto);
        botonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abrimos la galeria para seleccionar nuestra foto
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        Button botonHacerFoto = view.findViewById(R.id.botonHacerFoto);
        botonHacerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new
                            String[]{Manifest.permission.CAMERA}, 12);
                }
                else {
                    Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureLauncher.launch(elIntentFoto);
                }
            }
        });

        Button botonGuardarPerfil = view.findViewById(R.id.botonGuardarPerfil);
        botonGuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ubicacionImagen = null;
                if (imagenFich != null) {
                    ubicacionImagen = imagenFich.getAbsolutePath();
                }

                //Creamos un objeto de tipo data y le metemos el email
                Data datos = new Data.Builder()
                        .putString("email", textoEmail.getText().toString())
                        .putString("contrasena", textoContraseña.getText().toString())
                        .putString("nombre", textoNombre.getText().toString())
                        .putString("apellidos", textoApellidos.getText().toString())
                        .putString("ubicacionImagen", ubicacionImagen)
                        .build();

                //Creamos una solicitud de trabajo para la ejecucion de la llamada asincrona a la bd
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ActualizarPerfilBDService.class)
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
                                        //Si es correcto llevamos al usuario de nuevo al perfil
                                        Bundle bundle = new Bundle();
                                        bundle.putString("email", email);

                                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentEditarPerfil_to_fragmentPerfil, bundle);
                                    }
                                    else {
                                        //Sino lanza mensaje de aviso de error
                                        int tiempo= Toast.LENGTH_SHORT;
                                        String error = getString(R.string.error);
                                        Toast aviso = Toast.makeText(getContext(), error, tiempo);
                                        aviso.show();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(getContext()).enqueue(otwr);
            }
        });

        return view;
    }

}
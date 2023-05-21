package com.example.proyecto2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto2.Services.LoginBDService;
import com.example.proyecto2.Services.RegistroBDService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public class Registro extends AppCompatActivity {

    String nombreIntroducido;
    String apellidoIntroducido;
    String emailIntroducido;
    String contraseñaIntroducida;
    String idiomaApp;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        //Obtiene datos guardados onSave
        if (savedInstanceState!= null) {
            nombreIntroducido = savedInstanceState.getString("nombreIntroducido");
            apellidoIntroducido = savedInstanceState.getString("apellidoIntroducido");
            emailIntroducido = savedInstanceState.getString("emailIntroducido");
            contraseñaIntroducida = savedInstanceState.getString("contraseñaIntroducida");
            idiomaApp = savedInstanceState.getString("idioma");
        }

        //Obtiene el idioma si este se cambia
        else if (extras != null) {
            idiomaApp = extras.getString("idioma");
        }

        //Cambiamos el idioma al establecido o español por defecto. Se tiene que hacer al crearse
        Locale nuevaloc = new Locale(idiomaApp);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_registro);

        //Obtenemos el token actual del dispositivo
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                    }
                });

        Button volver = (Button) findViewById(R.id.volverRegistro);
        Button registrarse = (Button) findViewById(R.id.OKRegistro);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nos cierra el teclado
                View focus = getCurrentFocus();
                if (focus != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                }

                EditText contraseña = findViewById(R.id.contraseñaRegistro);
                EditText repetirContraseña = findViewById(R.id.repetirContraseñaRegistro);
                contraseñaIntroducida = contraseña.getText().toString();
                String contraseñaRepetidaIntroducida = repetirContraseña.getText().toString();

                //Comprobamos que coincidan las contraseñas
                if(contraseñaIntroducida.equals(contraseñaRepetidaIntroducida)) {
                    EditText nombre = findViewById(R.id.nombreRegistro);
                    nombreIntroducido = nombre.getText().toString();
                    EditText apellidos = findViewById(R.id.apellidoRegistro);
                    apellidoIntroducido = apellidos.getText().toString();
                    EditText email = findViewById(R.id.emailRegistro);
                    emailIntroducido = email.getText().toString();

                    Data datos = new Data.Builder()
                            .putString("nombre", nombreIntroducido)
                            .putString("apellidos", apellidoIntroducido)
                            .putString("email", emailIntroducido)
                            .putString("contrasena", contraseñaIntroducida)
                            .putString("token", token)
                            .build();

                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(RegistroBDService.class)
                            .setInputData(datos)
                            .build();
                    WorkManager.getInstance(Registro.this).getWorkInfoByIdLiveData(otwr.getId())
                            .observe(Registro.this, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    if(workInfo != null && workInfo.getState().isFinished()){
                                        boolean resultado = workInfo.getOutputData().getBoolean("resultados", false);
                                        if (resultado) {
                                            int tiempo= Toast.LENGTH_SHORT;
                                            Toast aviso = Toast.makeText(getApplicationContext(), getResources().getString(R.string.registroCorrecto), tiempo);
                                            aviso.show();

                                            //Volver con los datos al login
                                            Intent intent=new Intent();
                                            intent.putExtra("email", emailIntroducido);
                                            intent.putExtra("contraseña", contraseñaIntroducida);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                        else {
                                            //Aviso de error
                                            int tiempo= Toast.LENGTH_SHORT;
                                            Toast aviso = Toast.makeText(getApplicationContext(), "¡Error!", tiempo);
                                            aviso.show();
                                        }
                                    }
                                }
                            });
                    WorkManager.getInstance(Registro.this).enqueue(otwr);
                }// Validar campos vacíos
                else if (nombreIntroducido.isEmpty() || apellidoIntroducido.isEmpty() || emailIntroducido.isEmpty() || contraseñaIntroducida.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar restricción de dominio
                else if (!emailIntroducido.endsWith("gmail.com")) {
                    Toast.makeText(getApplicationContext(), "El email debe ser de Gmail", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    //Aviso error contras
                    int tiempo= Toast.LENGTH_SHORT;
                    Toast aviso = Toast.makeText(getApplicationContext(), "¡CUIDADO! Las contraseñas no coinciden", tiempo);
                    aviso.show();
                }
            }
        });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Volver sin datos
                Intent intent=new Intent();
                intent.putExtra("nombre", "");
                intent.putExtra("apellidos", "");
                intent.putExtra("email", "");
                intent.putExtra("contraseña", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    //Guardamos los datos de interes antes de destruir la actividad por la rotacion de pantalla
    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //Guarda lo deseado al hacer rotacion, etc
        EditText nombre = findViewById(R.id.nombreRegistro);
        savedInstanceState.putString("nombeIntroducido",  nombre.getText().toString());
        EditText apellidos = findViewById(R.id.apellidoRegistro);
        savedInstanceState.putString("apellidoIntroducido",  apellidos.getText().toString());
        EditText email = findViewById(R.id.emailRegistro);
        savedInstanceState.putString("emailIntroducido",  email.getText().toString());
        EditText contraseña = findViewById(R.id.contraseñaRegistro);
        savedInstanceState.putString("contraseñaIntroducida",  contraseña.getText().toString());
        savedInstanceState.putString("idioma",  idiomaApp);
    }
}
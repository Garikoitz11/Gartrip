package com.example.proyecto2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
//import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.proyecto2.Broadcasts.ElReceiver;
import com.example.proyecto2.Dialogs.Idioma;
import com.example.proyecto2.Services.LoginBDService;

import java.util.Calendar;
import java.util.Locale;

public class Login extends AppCompatActivity implements InterfaceIdioma {

    String idiomaApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        //Miramos los datos guardados cuando hay rotacion de pantalla
        if (savedInstanceState!= null) {
            idiomaApp = savedInstanceState.getString("idioma");
        }
        //Miramos los datos guardados cuando se cambia el idioma y se hace finish (No pasa por onSaveInstance)
        else if (extras != null) {
            idiomaApp = extras.getString("idioma");
        }
        //Miramos el fichero de preferencias en caso de ser la primera vez que se inicia la aplicación
        else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            idiomaApp = prefs.getString("idiomaPrefs","es");
        }

        //Cambiamos el idioma al establecido o español por defecto. Se tiene que hacer al crearse
        Locale nuevaloc = new Locale(idiomaApp);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);
        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_login);

        //Creamos la alarma
//        Calendar calendario= Calendar.getInstance();
//        calendario.set(Calendar.HOUR_OF_DAY,12);
//        calendario.set(Calendar.MINUTE,0);
//        calendario.set(Calendar.SECOND,0);
//
//        Intent i= new Intent(getIntent());
//        Intent intentBC = new Intent(this, ElReceiver.class);
//        intentBC.setAction("enviarNotificacionRetorno");
//
//        PendingIntent i2= PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager gestor= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        AlarmManager.AlarmClockInfo ac = new AlarmManager
//                .AlarmClockInfo(calendario.getTimeInMillis(), i2);
//        gestor.setAlarmClock(ac, i2);

        //Solicita permisos de poner notificaciones
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        //Solicita permisos para leer de la galeria
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
        }

        //Solicita permisos para utilizar la camara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.CAMERA}, 13);
        }

        //En caso de tener desactivado el modo background se avisa al usuario
        ActivityManager am= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (am.isBackgroundRestricted()==true){
                int tiempo= Toast.LENGTH_LONG;
                Toast aviso = Toast.makeText(getApplicationContext(), "Activa el modo background para un correcto funcionamiento", tiempo);
                aviso.show();
            }
        }

        //Obtenemos elementos del layout
        Button iniciarSesion = (Button) findViewById(R.id.iniciarSesionLogin);
        Button registro = (Button) findViewById(R.id.RegistroLogin);
        ImageButton twitter = (ImageButton) findViewById(R.id.twitter);
        EditText email = (EditText) findViewById(R.id.emailLogin);
        EditText contraseña = (EditText) findViewById(R.id.contraseñaLogin);
        ImageButton apagar = (ImageButton) findViewById(R.id.apagar);
        ImageButton idioma = (ImageButton) findViewById(R.id.idioma);

        apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nos saca de la app
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        idioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nos abre el dialog idiomas
                DialogFragment newFragment = new Idioma();
                newFragment.show(getSupportFragmentManager(), "SeleccionIdioma");
            }
        });

        //Espera que el intent devuelva algo
        ActivityResultLauncher<Intent> startActivityIntent =
                registerForActivityResult(new
                                ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {

                                if (result.getResultCode() == RESULT_OK) {
                                    //Obtiene usuario o contraseña si registro OK y los pone en los edit text email y contraseña
                                    email.setText(result.getData().getStringExtra("email"));
                                    contraseña.setText(result.getData().getStringExtra("contraseña"));
                                }
                            }
                        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean existe = false;
                //Nos cierra el teclado
                View focus = getCurrentFocus();
                if (focus != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
                }

                //Creamos un objeto de tipo data y le metemos el email y pass
                Data datos = new Data.Builder()
                        .putString("email", email.getText().toString())
                        .putString("contrasena", contraseña.getText().toString())
                        .build();

                //Creamos una solicitud de trabajo para la ejecucion de la llamada asincrona a la bd
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(LoginBDService.class)
                        .setInputData(datos)
                        .build();
                //Le añadimos un observable para que actue una vez reciba de vuelta algo
                WorkManager.getInstance(Login.this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(Login.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if(workInfo != null && workInfo.getState().isFinished()){
                                    //Obtenemos la respuesta del servidor en funcion de si es correcto o no
                                    boolean inicioCorrecto = workInfo.getOutputData().getBoolean("inicioCorrecto", false);
                                    if (inicioCorrecto) {
                                        //Si es correcto llevamos a la app al usuario y quitamos de la pila el login
                                        //para que si le da a volver atras no le vaya aqui y salga de la app
                                        Intent i = new Intent (Login.this, ActividadPrincipal.class);
                                        i.putExtra("email", email.getText().toString());
                                        startActivity(i);
                                        finish();
                                    }
                                    else {
                                        //Sino lanza mensaje de aviso de error
                                        int tiempo= Toast.LENGTH_SHORT;
                                        Toast aviso = Toast.makeText(getApplicationContext(), getResources().getString(R.string.usuarioContraseña), tiempo);
                                        aviso.show();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(Login.this).enqueue(otwr);
            }
        });

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abre registro
                Intent i = new Intent (Login.this, Registro.class);
                i.putExtra("idioma", idiomaApp);
                startActivityIntent.launch(i);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try{
                    //Si el usuario tiene twitter instalado lo abre en la app
                    getBaseContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/gartxon"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    //Sino lo abre en chrome o donde sea
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/gartxon"));
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void cambiarIdioma(String idioma) {
        if (idioma != idiomaApp) {
            //Recibe que se ha seleccionado un idioma y cual ha sido del dialogo
            idiomaApp = idioma;
            getIntent().putExtra("idioma" , idiomaApp);
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //guarda el idioma si hay rotacion de pantalla, cambio a modo oscuro, etc
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("idioma", idiomaApp);
    }
}
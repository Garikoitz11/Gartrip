package com.example.proyecto2.Fragments;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Dialogs.Compra;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;

public class FragmentFactura extends Fragment {

    private String nombreProducto;
    private String precioProducto;
    private String lugar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Obtenemos los parametros pasados del mapa
            nombreProducto = getArguments().getString("nombreProducto");
            precioProducto = getArguments().getString("precioProducto");
            lugar = getArguments().getString("lugar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_factura, container, false);

        //Obtenemos guardado en la actividad
        ActividadPrincipal actividad = (ActividadPrincipal) getActivity();
        String usuario = actividad.obtenerUsuario();

        //LLenamos de los datos de la compra los textviews
        TextView email = view.findViewById(R.id.email);
        email.setText(usuario);
        TextView hotel = view.findViewById(R.id.producto);
        hotel.setText(nombreProducto);
        TextView precio = view.findViewById(R.id.precio);
        precio.setText(precioProducto);
        TextView ubicacion = view.findViewById(R.id.lugar);
        ubicacion.setText(lugar);

        Button botonFactura = view.findViewById(R.id.botonFactura);
        Button botonVolver = view.findViewById(R.id.botonVolver);
        ImageButton contacto = view.findViewById(R.id.btnDatosPersonales);

        //Genera la factura en modo notificacion
        botonFactura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Generamos dialogo de informacion de la factura
                DialogFragment newFragment = new Compra();
                newFragment.show(getChildFragmentManager(), "Compra");

                //Generamos la notificacion de la factura
                NotificationManager elManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getContext(), "IdCanal");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                            NotificationManager.IMPORTANCE_DEFAULT);

                    elBuilder.setSmallIcon(R.drawable.gartrip_logo);
                    elBuilder.setContentTitle(getResources().getString(R.string.factura));

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    String nombreFactura = prefs.getString("nombreFactura", getResources().getString(R.string.anonimo));

                    if (nombreFactura.equals("")) {
                        nombreFactura = getResources().getString(R.string.anonimo);
                    }

                    elBuilder.setContentText(nombreFactura + " " + getResources().getString(R.string.compra) + " " + nombreProducto + getResources().getString(R.string.precioCompra) + " " + precioProducto + ". Lugar de recogida:" + lugar);
                    elBuilder.setVibrate(new long[]{0, 1000, 500, 1000});
                    elBuilder.setAutoCancel(true);

                    //Generamos el pending intent que se ejecutara cuando se pulse en el boton que volvera a comprar
                    Intent comprarIntent = new Intent(getContext(), Login.class);
                    PendingIntent pComprar;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pComprar = PendingIntent.getActivity(getContext(),
                                0, comprarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pComprar = PendingIntent.getActivity(getContext(),
                                0, comprarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    //Generamos el pending intent que se ejecutara cuando se pulse en el boton que saldra de la aplicacion
                    Intent salirIntent = new Intent(Intent.ACTION_MAIN);
                    salirIntent.addCategory(Intent.CATEGORY_HOME);
                    salirIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pSalir;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pSalir = PendingIntent.getActivity(getContext(),
                                0, salirIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    } else {
                        pSalir = PendingIntent.getActivity(getContext(),
                                0, salirIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    //Los añadimos
                    elBuilder.addAction(android.R.drawable.ic_menu_add, "Logout", pComprar);
                    elBuilder.addAction(android.R.drawable.ic_menu_delete, getResources().getString(R.string.salir), pSalir);

                    elCanal.setDescription("Código de verificación");
                    elCanal.enableLights(true);
                    elCanal.setLightColor(Color.WHITE);
                    elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                    elCanal.enableVibration(true);
                    elManager.createNotificationChannel(elCanal);
                }
                elManager.notify(1, elBuilder.build());
            }
        });

        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LLeva al usuario al fragmento principal
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentFactura_to_fragmentPrincipal);
            }
        });

        contacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS)!=
                        PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_CONTACTS)!=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, 11);
                }
                else {
                    //Obtenemos el content resolver para poder interactuar con los contactos
                    ContentResolver contentResolver = getContext().getContentResolver();

                    // Insertar un nuevo registro de contacto crudo y obtener su ID
                    Uri rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
                    long rawContactId = ContentUris.parseId(rawContactUri);

                    // Insertar el nombre del contacto en la tabla de datos
                    ContentValues nameValues = new ContentValues();
                    nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Gartrip Hoteles");
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, nameValues);

                    // Insertar el número de teléfono del contacto en la tabla de datos
                    ContentValues phoneValues = new ContentValues();
                    phoneValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                    phoneValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                    phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "669629087");
                    phoneValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues);
                }
            }
        });

        return view;
    }
}

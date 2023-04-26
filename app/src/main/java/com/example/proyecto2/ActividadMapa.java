package com.example.proyecto2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.example.proyecto2.Dialogs.Compra;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ActividadMapa extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap elmapa;
    private FusedLocationProviderClient proveedordelocalizacion;
    private LocationRequest peticion;
    private LocationCallback actualizador;
    private String nombreProducto;
    private String precioProducto;
    private boolean recibirActualizaciones;
    private static ArrayList<LatLng> ubicacionTiendas;
    private double latitud;
    private double longitud;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtenemos los parametros que recibe del login o del idioma
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
            nombreProducto= extras.getString("nombreProducto");
            precioProducto = extras.getString("precioProducto");
        }

        setContentView(R.layout.activity_mapa);

        ubicacionTiendas = new ArrayList<>();
        LatLng[] coordenadas = { new LatLng(43.2966, -2.9888), new LatLng(43.3213, -1.9864), new LatLng(40.2655, -3.8405), new LatLng(25.7966, -80.1318)};
        ubicacionTiendas.addAll(Arrays.asList(coordenadas));

        recibirActualizaciones = true;

        //Llama al metodo onMapReady que nos permitirá trabajar con el mapa
        SupportMapFragment elfragmento = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        elfragmento.getMapAsync(this);

        //Crea un location request con las caracteristicas deseadas
        peticion = LocationRequest.create();
        peticion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        peticion.setInterval(10000);

        //Crea un location callback indicando que hacer cuando se actualiza la posicion
        actualizador = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult!=null){
                    Location location = locationResult.getLastLocation();
                    if (location.getAccuracy() <= 50 && recibirActualizaciones) {
                        //Si la precision es buena obtiene las coordenadas actuales
                        double latitudActual = location.getLatitude();
                        double longitudActual = location.getLongitude();

                        latitud = latitudActual;
                        longitud = longitudActual;

                        //Actualiza la posicion de la camara
                        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(latitudActual, longitudActual), 10);
                        elmapa.animateCamera(actualizar);
                    }
                }
            }
        };

        Button boton = findViewById(R.id.button);
        Button botonBuscarCercania = findViewById(R.id.button2);


        //Actualiza la ubicación
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recibirActualizaciones = true;
            }
        });

        //Busca la tienda mas cercana
        botonBuscarCercania.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarTiendaMasCercana(latitud, longitud);
            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Guardamos el mapa e indicamos el tipo deseado
        elmapa = googleMap;
        elmapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        elmapa.addMarker(new MarkerOptions()
                        .position(ubicacionTiendas.get(0))
                        .title("Tienda Bizkaia"))
                .setSnippet("Situada en la localidad de Barakaldo");

        elmapa.addMarker(new MarkerOptions()
                        .position(ubicacionTiendas.get(1))
                        .title("Tienda Gipuzkoa"))
                .setSnippet("Situada junto a la playa de la Concha en Donostia");

        elmapa.addMarker(new MarkerOptions()
                        .position(ubicacionTiendas.get(2))
                        .title("Tienda Madrid"))
                .setSnippet("Situada en la capital española");

        elmapa.addMarker(new MarkerOptions()
                        .position(ubicacionTiendas.get(3))
                        .title("Tienda Miami"))
                .setSnippet("Situada en el extranjero");

//        elmapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            public void onMapClick(LatLng point) {
//                //Deja al usuario desplazarse sin que se actualice constantemente la ubicacion
//                recibirActualizaciones = false;
//            }
//        });

        elmapa.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                recibirActualizaciones = false;
            }
        });

        elmapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                String marcador = marker.getTitle();
                if (marcador != null) {
                    //Generamos dialogo de informacion de la factura
                    DialogFragment newFragment = new Compra(email);
                    newFragment.show(getSupportFragmentManager(), "Compra");

                    //Generamos la notificacion de la factura
                    NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getApplicationContext(), "IdCanal");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                                NotificationManager.IMPORTANCE_DEFAULT);

                        elBuilder.setSmallIcon(R.drawable.ic_gartxon);
                        elBuilder.setContentTitle(getResources().getString(R.string.factura));

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ActividadMapa.this);
                        String nombreFactura = prefs.getString("nombreFactura", getResources().getString(R.string.anonimo));

                        if(nombreFactura.equals("")) {
                            nombreFactura = getResources().getString(R.string.anonimo);
                        }

                        elBuilder.setContentText(nombreFactura + " " + getResources().getString(R.string.compra) + " " + nombreProducto + getResources().getString(R.string.precioCompra) + " " + precioProducto + ". Lugar de recogida:" + marcador);
                        elBuilder.setVibrate(new long[]{0, 1000, 500, 1000});
                        elBuilder.setAutoCancel(true);

                        //Generamos el pending intent que se ejecutara cuando se pulse en el boton que volvera a comprar
                        Intent comprarIntent = new Intent(ActividadMapa.this, Login.class);
                        PendingIntent pComprar;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            pComprar = PendingIntent.getActivity(ActividadMapa.this,
                                    0, comprarIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        }else {
                            pComprar = PendingIntent.getActivity(ActividadMapa.this,
                                    0, comprarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }

                        //Generamos el pending intent que se ejecutara cuando se pulse en el boton que saldra de la aplicacion
                        Intent salirIntent = new Intent(Intent.ACTION_MAIN);
                        salirIntent.addCategory( Intent.CATEGORY_HOME );
                        salirIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pSalir;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            pSalir = PendingIntent.getActivity(ActividadMapa.this,
                                    0, salirIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                        }else {
                            pSalir = PendingIntent.getActivity(ActividadMapa.this,
                                    0, salirIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        }

                        //Los añadimos
                        elBuilder.addAction(android.R.drawable.ic_menu_add, "Login", pComprar);
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
                return false;
            }
        });

        //Solicita los permisos de ubicacion si no los tiene
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
        }

        //Inicializamos el FusedLocationProviderClient obteniendo la ultima posicion conocida
        proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(this);

        //Solicita que este constantemente actualizando la posicion del dispositivo
        proveedordelocalizacion.requestLocationUpdates(peticion,actualizador,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //Si los permisos no son aceptados se envia un mensaje y se para
                Toast.makeText(this, "Se requieren permisos de ubicación para continuar.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Si se destruye la actividad se deja de solicitar constantes actualizaciones de posicion
        proveedordelocalizacion.removeLocationUpdates(actualizador);
    }

    public void buscarTiendaMasCercana (double lat1, double long1) {
        LatLng masCercana = null;
        double cercania = 0;
        for (LatLng tienda: ubicacionTiendas) {
            double latTienda = tienda.latitude;
            double longTienda = tienda.longitude;
            double R = 6371000; // radio de la Tierra en metros
            double dLat = Math.toRadians(latTienda - lat1);
            double dLong = Math.toRadians(longTienda - long1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(latTienda)) *
                            Math.sin(dLong/2) * Math.sin(dLong/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = R * c;
            if (masCercana == null || d < cercania) {
                masCercana = tienda;
                cercania = d;
            }
        }
        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(masCercana.latitude, masCercana.longitude), 10);
        elmapa.animateCamera(actualizar);
        recibirActualizaciones = false;
    }
}
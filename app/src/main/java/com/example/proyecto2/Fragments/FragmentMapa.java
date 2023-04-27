package com.example.proyecto2.Fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.proyecto2.Dialogs.Compra;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;
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

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle extras = getArguments();
        nombreProducto = extras.getString("nombreProducto");
        precioProducto = extras.getString("precioProducto");

        //Inflamos el fragmento con el layaout del mapa
        View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        //Guardamos el mapa e indicamos el tipo deseado
        SupportMapFragment elfragmento = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        elfragmento.getMapAsync(this);

        //Guardamos en un array la ubicacion de las tiendas para su posterior uso
        ubicacionTiendas = new ArrayList<>();
        LatLng[] coordenadas = { new LatLng(43.2966, -2.9888), new LatLng(43.3213, -1.9864), new LatLng(40.2655, -3.8405), new LatLng(25.7966, -80.1318)};
        ubicacionTiendas.addAll(Arrays.asList(coordenadas));

        //Por defecto que el mapa se coloque en nuestra posicion
        recibirActualizaciones = true;

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

        Button boton = view.findViewById(R.id.button);
        Button botonBuscarCercania = view.findViewById(R.id.button2);

        //Actualiza la ubicación y mantiene actualizaciones constantes
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recibirActualizaciones = true;
            }
        });

        //Busca la tienda mas cercana utilizando las coordenadas
        botonBuscarCercania.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarTiendaMasCercana(latitud, longitud);
            }
        });

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Guardamos el mapa e indicamos el tipo deseado
        elmapa = googleMap;
        elmapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Añadimos los marcadores de las tiendas
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

        //Si el usuario desplaza un poco la camara deja de recibir actualizaciones de ubicacion para que este se pueda mover
        elmapa.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                recibirActualizaciones = false;
            }
        });

        //Si el usuario clicka un marcador
        elmapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                //Obtiene el titulo del marcador para poder identificarlo
                String marcador = marker.getTitle();
                if (marcador != null) {
                    //LLevamos al usuario a la pantalla donde podra ver su factura
                    Bundle bundle = new Bundle();
                    bundle.putString("nombreProducto", nombreProducto);
                    bundle.putString("precioProducto", precioProducto);
                    bundle.putString("lugar", marcador);

                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_fragmentMapa_to_fragmentFactura, bundle);
                }
                return false;
            }
        });

        //Solicita los permisos de ubicacion si no los tiene
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
        }

        //Inicializamos el FusedLocationProviderClient obteniendo la ultima posicion conocida
        proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(getContext());

        //Solicita que este constantemente actualizando la posicion del dispositivo
        proveedordelocalizacion.requestLocationUpdates(peticion,actualizador,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //Si los permisos no son aceptados se envia un mensaje y se para
                Toast.makeText(getContext(), "Se requieren permisos de ubicación para continuar.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Si se destruye la actividad se deja de solicitar constantes actualizaciones de posicion
        proveedordelocalizacion.removeLocationUpdates(actualizador);
    }

    public void buscarTiendaMasCercana (double lat1, double long1) {
        //Calcula la distancia entre las coordenadas de la posicion del usuario y las tiendas y devuelve la mas cercana
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
        //Coloca la camara en la tienda mas cercana al usuario
        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(masCercana.latitude, masCercana.longitude), 10);
        elmapa.animateCamera(actualizar);
        recibirActualizaciones = false;
    }
}

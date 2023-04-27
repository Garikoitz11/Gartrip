package com.example.proyecto2.Broadcasts;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.proyecto2.ActividadPrincipal;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;
import com.example.proyecto2.Widgets.GartxonWidget;

import java.util.ArrayList;

public class ElReceiver extends BroadcastReceiver {

    static final ArrayList<String> listaOfertas = new ArrayList<String>();

    static {
        //La lista de ofertas del Widget
        listaOfertas.add("¡Iphone 14 rebajado!");
        listaOfertas.add("Descuentos en ordenadores");
        listaOfertas.add("No te pierdas nuestros últimos productos");
        listaOfertas.add("¿Necesitas un móvil? Mira esta pedazo oferta que tenemos para ti");
        listaOfertas.add("¡Hemos abierto una tienda nueva!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("enviarRetorno".equals(action)) {
            //Para que volviendo atras no pueda seguir en la app y tenga que iniciar sesion de nuevo
            ActividadPrincipal activity = ActividadPrincipal.getInstance();
            if (activity != null) {
                activity.finish();
            }

            //Le envia al login por exceder el tiempo maximo logeado
            Intent notificationIntent = new Intent(context, Login.class);
            notificationIntent.putExtra("cerradoInactividad", true);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(notificationIntent);
        }
        else {
            //Obtiene el indice en el que se encuentra el widget mostrando cosas del array
            SharedPreferences prefs = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
            int index = prefs.getInt("index", 0);

            //Obtiene el layaout del widget y lo actualiza
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.gartxon_widget);
            remoteViews.setTextViewText(R.id.textView9, listaOfertas.get(index));
            ComponentName tipowidget = new ComponentName(context, GartxonWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            manager.updateAppWidget(tipowidget, remoteViews);

            //Actualiza el indice
            index = (++index)%listaOfertas.size();

            //Lo guarda para el siguiente aviso via broadcast
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("index", index);
            editor.apply();
        }
    }
}

package com.example.proyecto2.Widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.proyecto2.Broadcasts.ElReceiver;
import com.example.proyecto2.Login;
import com.example.proyecto2.R;
import com.example.proyecto2.Services.ObtenerPerfilBDService;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class GartxonWidget extends AppWidgetProvider {
    public static final String ACTION_WIDGET_CLICKED = "com.gartxonwidget.ACTION_WIDGET_CLICKED";
    private AlarmManager am;
    private PendingIntent pi;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.gartxon_widget);

        Intent intent = new Intent(context, GartxonWidget.class);
        intent.setAction(ACTION_WIDGET_CLICKED);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.logoWidget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        //Creamos una alarma que se encargue de actualizar el widget de manera periódica, esperando menos de 30mins
        am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ElReceiver.class);
        pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_IMMUTABLE);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 30000, pi);
    }

    @Override
    public void onDisabled(Context context) {
        //Cancelamos la alarma
        //am.cancel(pi);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() != null && intent.getAction().equals(ACTION_WIDGET_CLICKED)) {
            Intent loginIntent = new Intent(context, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(loginIntent);
        }
    }
}
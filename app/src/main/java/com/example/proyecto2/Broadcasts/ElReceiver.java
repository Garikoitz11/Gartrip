package com.example.proyecto2.Broadcasts;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.proyecto2.Login;
import com.example.proyecto2.R;

public class ElReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("enviarNotificacionRetorno".equals(action)) {
            Intent notificationIntent = new Intent(context, Login.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canalPorDefecto")
                    .setSmallIcon(R.drawable.ic_gartxon)
                    .setContentTitle("Novedades en Gartxon")
                    .setContentText("¡Accede a la aplicación y no te pierdas ninguna oferta!")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            //Muestra la notificación si el usuario ha concedido permisos para enviar notificaciones
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManager.notify(0, builder.build());
        }
    }
}

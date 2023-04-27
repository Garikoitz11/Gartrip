package com.example.proyecto2.Services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        try {
            //Guarda el token en un fichero para que este se pueda recuperar al hacer login
            File file = new File(getFilesDir(), "token.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
        }
    }

}

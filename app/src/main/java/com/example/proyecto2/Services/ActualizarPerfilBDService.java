package com.example.proyecto2.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActualizarPerfilBDService extends Worker {

    public ActualizarPerfilBDService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        //Declaramos la variable para devolver el resultado de la consulta
        Data resultados = null;

        //Obtenemos los datos enviados desde la actividad
        String email = getInputData().getString("email");
        String contrasena = getInputData().getString("contrasena");
        String nombre = getInputData().getString("nombre");
        String apellidos = getInputData().getString("apellidos");
        String foto = getInputData().getString("ubicacionImagen");

        String fotoen64 = null;

        if (foto != null) {
            // Crear un objeto File con la ubicación de la imagen
            File imagenFich = new File(foto);

            // Crear un objeto FileInputStream para leer la imagen
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagenFich);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap fotoBitmap = BitmapFactory.decodeStream(fis);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] fototransformada = stream.toByteArray();
            fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
            imagenFich.delete();
        }

        //Realizamos la conexion con el servidor (con el php a ejecutar)
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/actualizarPerfil.php";
        HttpURLConnection urlConnection = null;
        URL destino = null;
        try {
            destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);

            //Le añadimos los parametros a la llamada al servidor
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("email", email)
                    .appendQueryParameter("contrasena", contrasena)
                    .appendQueryParameter("nombre", nombre)
                    .appendQueryParameter("apellidos", apellidos)
                    .appendQueryParameter("foto", fotoen64);
            String parametros = builder.build().getEncodedQuery();

            //Especificamos el tipo de peticion
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Envia los parametros a la url indicada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Espera a la respues del servidor
            int statusCode = urlConnection.getResponseCode();

            //Si la respuesta es correcta lee el resultado y lo almacena en la variable de tipo data para devolverlo a la actividad
            //y poder comprobar si ha sido exitoso el login
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                boolean operacionCorrecta = (boolean) json.get("operacionCorrecta");

                resultados = new Data.Builder()
                        .putBoolean("operacionCorrecta", operacionCorrecta)
                        .build();
                inputStream.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ListenableWorker.Result.success(resultados);
    }
}

package com.example.proyecto2.Services;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistroBDService extends Worker{

    public RegistroBDService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Declaramos la variable para devolver el resultado de la consulta
        Data resultados = new Data.Builder()
                .build();

        //Obtenemos los datos enviados desde la actividad
        String email = getInputData().getString("email");
        String contrasena = getInputData().getString("contrasena");
        String token = getInputData().getString("token");

        //Realizamos la conexion con el servidor (con el php a ejecutar)
        String direccion = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/gsalaberria004/WEB/registro.php";
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
                    .appendQueryParameter("token", token);
            String parametros = builder.build().getEncodedQuery();

            //Especificamos el tipo de peticion
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Envia los parametros a la url indicada
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Espera la respuesta del servidor
            int statusCode = urlConnection.getResponseCode();

            //Si la respuesta es correcta lee el resultado y lo almacena en la variable de tipo data para devolverlo a la actividad
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(result);
                boolean resultado = (boolean) json.get("result");

                resultados = new Data.Builder()
                        .putBoolean("resultados", resultado)
                        .build();
                inputStream.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Result.success(resultados);
    }
}
